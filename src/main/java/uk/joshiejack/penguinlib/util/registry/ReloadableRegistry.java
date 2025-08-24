package uk.joshiejack.penguinlib.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
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

    // Imports removidos: NeoForged network/configuration. Usar Forge SimpleChannel para sync.
    // Blocos Sync/Configure removidos. Use Forge SimpleChannel para sincronização customizada.
}