package uk.joshiejack.penguinlib.util.registry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = PenguinLib.MODID)
public class ReloadableManager extends SimplePreparableReloadListener<Map<String, Map<ResourceLocation, JsonElement>>> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Override
    protected @NotNull Map<String, Map<ResourceLocation, JsonElement>> prepare(@NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        Map<String, Map<ResourceLocation, JsonElement>> superMap = new HashMap<>();
        ReloadableRegistry.REGISTRIES.forEach(r -> {
            Map<ResourceLocation, JsonElement> map = new HashMap<>();
            SimpleJsonResourceReloadListener.scanDirectory(pResourceManager, r.dir(), GSON, map);
            superMap.put(r.dir(), map);
        });

        return superMap;
    }

    @SubscribeEvent
    public static void onReload(AddReloadListenerEvent event) {
        event.addListener(new ReloadableManager());
    }

    @Override
    protected void apply(@NotNull Map<String, Map<ResourceLocation, JsonElement>> superMap, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        ReloadableRegistry.REGISTRIES.stream().sorted(Comparator.comparing(ReloadableRegistry::priority)).forEach(registry -> {
            Map<ResourceLocation, JsonElement> map = superMap.get(registry.dir());
            Map<ResourceLocation, ReloadableRegistry.PenguinRegistry<?>> entries = new HashMap<>();
            map.forEach((rl, element) -> {
                try {
                    DataResult<? extends ReloadableRegistry.PenguinRegistry<?>> result = registry.codec().parse(JsonOps.INSTANCE, element);
                    if (result != null && result.result().isPresent())
                        entries.put(rl, result.get().orThrow().init(resourceManager, rl));
                } catch (Exception exception) {
                    PenguinLib.LOGGER.error("Parsing error loading {} {}: {}", registry.dir(), rl, exception.getMessage());
                    exception.printStackTrace();
                }
            });

            registry.set(entries);
        });
    }
}
