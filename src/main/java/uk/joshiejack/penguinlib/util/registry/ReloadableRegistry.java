package uk.joshiejack.penguinlib.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.OnGameConfigurationEvent;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.SyncRegistryPacket;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ReloadableRegistry<O extends ReloadableRegistry.PenguinRegistry<O>> {
    public static final Set<ReloadableRegistry<?>> REGISTRIES = Sets.newHashSet();
    private final BiMap<ResourceLocation, O> registry = HashBiMap.create();
    private final O none;
    private final Codec<O> codec;
    private final String dir;
    private final boolean sync;
    private EventPriority priority;

    public ReloadableRegistry(String modid, String dir, Codec<O> codec, O none, boolean sync) {
        this.dir = modid + "/" + dir;
        this.codec = codec;
        this.none = none;
        this.sync = sync;
        this.priority = EventPriority.NORMAL;
        REGISTRIES.add(this);
    }

    public ReloadableRegistry<O> withPriority(EventPriority priority) {
        this.priority = priority;
        return this;
    }

    public EventPriority priority() {
        return priority;
    }

    public boolean sync() {
        return sync;
    }

    public O emptyEntry() {
        return none;
    }

    public BiMap<ResourceLocation, O> registry() {
        return registry;
    }

    public ResourceLocation getID(O object) {
        return registry.inverse().get(object);
    }

    public String dir() {
        return dir;
    }

    public Codec<O> codec() {
        return codec;
    }

    public void add(O object) {
        registry.put(object.id(), object);
    }

    public Stream<O> stream() {
        return registry.values().stream();
    }

    public O get(ResourceLocation id) {
        return registry.get(id);
    }

    public O getOrDefault(ResourceLocation id, O def) {
        return registry.getOrDefault(id, def);
    }

    public O getOrEmpty(ResourceLocation id) {
        return registry.getOrDefault(id, none);
    }

    @SuppressWarnings("unchecked")
    public void set(Map<ResourceLocation, ? extends PenguinRegistry<?>> notes) {
        registry.clear();
        notes.forEach((rl, o) -> registry.put(rl, (O) o));
    }


    public interface PenguinRegistry<O extends PenguinRegistry<O>> {
        @SuppressWarnings("unchecked")
        default O init (ResourceManager resourceManager, ResourceLocation id) {
            return (O) this;
        }

        ResourceLocation id();
        O fromNetwork(FriendlyByteBuf buf);

        void toNetwork(FriendlyByteBuf buf);
    }

    @Mod.EventBusSubscriber(modid = PenguinLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Sync {
        @SubscribeEvent
        public static void onDataPack(OnGameConfigurationEvent event) {
            event.register(new Configure(event.getListener()));
        }
    }

    public record Configure (ServerConfigurationPacketListener listener) implements ICustomConfigurationTask {
        public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(SyncRegistryPacket.ID);

        @Override
        public void run(@NotNull Consumer<CustomPacketPayload> sender) {
            ReloadableRegistry.REGISTRIES.stream()
                    .sorted(Comparator.comparing(ReloadableRegistry::priority))
                    .filter(ReloadableRegistry::sync)
                    .forEach(s -> sender.accept(new SyncRegistryPacket(s)));
            listener.finishCurrentTask(TYPE);
        }

        @Override
        public @NotNull Type type() {
            return TYPE;
        }
    }
}