package uk.joshiejack.penguinlib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.client.PenguinTeamsClient;
import uk.joshiejack.penguinlib.util.registry.Packet;

import java.util.UUID;

@Packet(value = PacketFlow.CLIENTBOUND)
public class ChangeTeamPacket implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("change_team");
    private final UUID player;
    private final UUID oldTeam;
    private final UUID newTeam;

    public ChangeTeamPacket(UUID player, UUID oldTeam, UUID newTeam) {
        this.player = player;
        this.oldTeam = oldTeam;
        this.newTeam = newTeam;
    }

    public ChangeTeamPacket(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readUUID(), buf.readUUID());
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeUUID(player);
        to.writeUUID(oldTeam);
        to.writeUUID(newTeam);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    @Override
    public void handle(Player clientPlayer) {
        PenguinTeamsClient.changeTeam(player, oldTeam, newTeam);
    }
}
