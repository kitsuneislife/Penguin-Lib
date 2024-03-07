package uk.joshiejack.penguinlib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;

import java.util.Map;

@Packet(PacketFlow.CLIENTBOUND)
public class SyncRegistryPacket implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("sync_penguin_registries");
    private final ReloadableRegistry<?> registry;
    private final Map<ResourceLocation, ? extends ReloadableRegistry.PenguinRegistry<?>> entries;

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public SyncRegistryPacket(ReloadableRegistry<?> registry) {
        this.registry = registry;
        this.entries = registry.registry();
    }

    public SyncRegistryPacket(FriendlyByteBuf buf) {
        String dir = buf.readUtf(32767);
        this.registry = ReloadableRegistry.REGISTRIES.stream().filter(r -> r.dir().equals(dir)).findFirst().orElse(null);
        assert registry != null;
        this.entries = buf.readMap(FriendlyByteBuf::readResourceLocation, (r) -> registry.emptyEntry().fromNetwork(r));
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeUtf(registry.dir());
        to.writeMap(entries, FriendlyByteBuf::writeResourceLocation, (buf, entry) -> entry.toNetwork(buf));
    }

    @Override
    public void handle(Player player) {
        registry.set(entries);
    }
}