package uk.joshiejack.penguinlib.network.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.client.PenguinTeamsClient;
import uk.joshiejack.penguinlib.util.registry.Packet;

@Packet(PacketFlow.CLIENTBOUND)
public class SyncTeamDataPacket extends SyncCompoundTagPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("sync_team_data");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public SyncTeamDataPacket(CompoundTag tag) {
        super(tag);
    }

    public SyncTeamDataPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(Player player) {
        PenguinTeamsClient.setInstance(tag);
    }
}