package uk.joshiejack.penguinlib.scripting;

import joptsimple.internal.Strings;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.scripting.wrapper.WrapperRegistry;
import uk.joshiejack.penguinlib.util.IModPlugin;
import uk.joshiejack.penguinlib.util.registry.Plugin;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Basic scripting support for simple things in Penguin-Lib
 * Making use of rhino itself, for instances where kubejs is not needed

  Allows for some basic calling without having two dependencies for use in scripts in the other mods
 */
@Plugin(value = "rhino")
public class ScriptLoader extends SimplePreparableReloadListener<Map<ResourceLocation, Interpreter<?>>> implements IModPlugin {
    public static final ScriptLocation SCRIPTS = new ScriptLocation(PenguinLib.MODID + "/scripts", ScriptFactory::getScript);

    @Override
    public void setup() {
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(WrapperRegistry.class);
        NeoForge.EVENT_BUS.register(DefaultScripting.class);
        NeoForge.EVENT_BUS.register(ScriptFactory.Collect.class);
    }

    public record ScriptLocation(String dir, Function<ResourceLocation, Interpreter<?>> getJS) {}

    @SubscribeEvent
    public void onReload(AddReloadListenerEvent event) {
        event.addListener(new ScriptLoader());
    }

    @Override
    protected @NotNull Map<ResourceLocation, Interpreter<?>> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        ScriptFactory.resetRegistry();
        Map<ResourceLocation, Interpreter<?>> map = new HashMap<>();
        scanJavaScript(resourceManager, SCRIPTS, map);
        return map;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, Interpreter<?>> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        map.forEach(ScriptFactory::register);
    }

    private static final Pattern REQUIRE_PATTERN = Pattern.compile("require\\(['\"](?:[^'\"]|\\\\[\"'])*['\"]\\);?");

    public static String requireToJavascript(ResourceManager manager, String input) {
        Matcher matcher = REQUIRE_PATTERN.matcher(input);
        while (matcher.find()) {
            String match = matcher.group();
            // Extracting the value inside the quotes
            String valueInsideQuotes = match.replaceAll("\\brequire\\(['\"](.*)['\"]\\);?", "$1");
            String namespace = valueInsideQuotes.split(":")[0];
            String path = PenguinLib.MODID + "/" + valueInsideQuotes.split(":")[1];
            ResourceLocation location = new ResourceLocation(namespace, path + (path.contains(".js") ? Strings.EMPTY : ".js"));
            Optional<Resource> resource = manager.getResource(location);
            if (resource.isPresent()) {
                try (Reader reader = resource.get().openAsReader()) {
                    String javascript = IOUtils.toString(reader);
                    input = input.replace(match, javascript);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (REQUIRE_PATTERN.matcher(input).find())
            return requireToJavascript(manager, input);
        return input;
    }

    public static void scanJavaScript(ResourceManager pResourceManager, ScriptLocation location, Map<ResourceLocation, Interpreter<?>> pOutput) {
        FileToIdConverter filetoidconverter = new FileToIdConverter(location.dir(), ".js");
        for (Map.Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(pResourceManager).entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            ResourceLocation resourcelocation1 = filetoidconverter.fileToId(resourcelocation);
            try (Reader reader = entry.getValue().openAsReader()) {
                String javascript = IOUtils.toString(reader);
                javascript = requireToJavascript(pResourceManager, javascript); //Replace the require statements
                Interpreter<?> interpreter = pOutput.put(resourcelocation1, new Interpreter.Script(resourcelocation1, javascript, location));
                if (interpreter != null) {
                    throw new IllegalStateException("Duplicate data file ignored with ID " + resourcelocation1);
                }
            } catch (IllegalArgumentException | IOException ex) {
                PenguinLib.LOGGER.error("Couldn't parse data file {} from {}", resourcelocation1, resourcelocation);
            }
        }
    }
}
