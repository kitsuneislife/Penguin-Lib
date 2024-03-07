package uk.joshiejack.penguinlib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;

@Packet(value = PacketFlow.CLIENTBOUND)
public class SyncPlayerTagPacket extends SyncCompoundTagPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("sync_player_tag");
    private final String tagName;

    public SyncPlayerTagPacket(String tagName, Player player) {
        super(player.getPersistentData().getCompound(tagName));
        this.tagName = tagName;
    }

    public SyncPlayerTagPacket(FriendlyByteBuf buf) {
        super(buf);
        tagName = buf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeUtf(tagName);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handle(Player player) {
        player.getPersistentData().put(tagName, tag);
    }
}
