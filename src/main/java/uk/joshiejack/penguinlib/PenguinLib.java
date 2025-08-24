package uk.joshiejack.penguinlib;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import net.minecraft.DetectedVersion;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import uk.joshiejack.penguinlib.client.PenguinClientConfig;
import uk.joshiejack.penguinlib.data.PenguinRegistries;
import uk.joshiejack.penguinlib.data.generator.*;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.IModPlugin;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.penguinlib.util.registry.Plugin;
import uk.joshiejack.penguinlib.world.item.PenguinItems;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Mod.EventBusSubscriber(modid = PenguinLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(PenguinLib.MODID)
public class PenguinLib {
    public static final String MODID = "penguinlib";
    public static final String DATABASE_FOLDER = MODID + "/database";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final Type PACKET = Type.getType(Packet.class);
    private static final Type PLUGIN = Type.getType(Plugin.class);
    public static final String CATEGORIES_FOLDER = MODID + "/categories";
    public static final String NOTES_FOLDER = MODID + "/notes";
    private static List<IModPlugin> plugins = new ArrayList<>();

    public PenguinLib() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        plugins = getPlugins();
        PenguinLib.plugins.forEach(IModPlugin::construct);
        PenguinItems.register(eventBus);
        PenguinRegistries.register(eventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, PenguinClientConfig.create());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PenguinConfig.create());
    }

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        //Grab any other things that need to be automagically registered ^
        registerPenguinLoaderData(); //Process them and load them
        plugins.forEach(IModPlugin::setup);
        plugins = null; //Kill the plugins
        PACKETS = null; //Clear packets after registration
    }

    @SubscribeEvent
    public static void onDataGathering(GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        final PackOutput output = event.getGenerator().getPackOutput();
        //PackMetadataGenerator
        PenguinBlockTags blocktags = new PenguinBlockTags(output, event.getLookupProvider(), event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), blocktags);
        generator.addProvider(event.includeServer(), new PenguinItemTags(output, event.getLookupProvider(), blocktags.contentsGetter(), event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new PenguinBannerTags(output, event.getLookupProvider(), event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new PenguinDatabase(output));
        generator.addProvider(event.includeServer(), new TestNotes(output));

        //Client
        generator.addProvider(event.includeClient(), new PenguinSpriteSourceProvider(output, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new PenguinLanguage(output));
        generator.addProvider(event.includeClient(), new PenguinItemModels(output, event.getExistingFileHelper()));
        generator.addProvider(true, new PackMetadataGenerator(output).add(PackMetadataSection.TYPE, new PackMetadataSection(
                Component.literal("Resources for Penguin-Lib"),
                DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA))));
    }

    private static List<Pair<Class<PenguinPacket>, PacketFlow>> PACKETS = Lists.newArrayList();

    @SubscribeEvent
    public static void registerPackets(FMLCommonSetupEvent event) {
        // Network registration simplified for Forge 1.20.1 - use NetworkRegistry
        // Removed PACKETS = null to prevent NullPointerException
    }

    private static List<IModPlugin> getPlugins() {
        List<IModPlugin> list = new ArrayList<>();
        ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> PLUGIN.equals(a.annotationType()))
                .filter(a -> ModList.get().isLoaded((String) a.annotationData().get("value")))
                .forEach(a -> {
                    try {
                        Class<?> clazz = Class.forName(a.clazz().getClassName());
                        list.add((IModPlugin) clazz.getDeclaredConstructor().newInstance());
                    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                             NoSuchMethodException ignored) { } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
        return list;
    }

    @SuppressWarnings("unchecked")
    private static void registerPenguinLoaderData() {
        ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream) //Either of the penguin annotation or the packet annotation is ok
                .filter(a -> PACKET.equals(a.annotationType()))//, i trust that i will use the packet one only on packets ;)
                .forEach((a -> {
                    try {
                        Class<?> clazz = Class.forName(a.clazz().getClassName());
                        PACKETS.add(Pair.of((Class<PenguinPacket>) clazz, PacketFlow.valueOf(((ModAnnotation.EnumHolder) a.annotationData().get("value")).getValue())));
                    } catch (ClassNotFoundException ignored) {}
                }));
    }

    public static ResourceLocation prefix(String path) {
        return new ResourceLocation(MODID, path.toLowerCase());
    }

}
