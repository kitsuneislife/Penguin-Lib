package uk.joshiejack.penguinlib.network.packet;

import com.google.common.collect.Maps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.client.PenguinTeamsClient;
import uk.joshiejack.penguinlib.util.registry.Packet;

import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

@Packet(PacketFlow.CLIENTBOUND)
public class SyncTeamMembersPacket implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("sync_team_members");
    public final Map<UUID, UUID> memberOf;

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public SyncTeamMembersPacket(Map<UUID, UUID> memberOf) {
        this.memberOf = memberOf;
    }

    public SyncTeamMembersPacket (FriendlyByteBuf from) {
        memberOf = Maps.newHashMap();
        int size = from.readByte();
        IntStream.range(0, size).forEach(i ->
                memberOf.put(from.readUUID(), from.readUUID()));
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeByte(memberOf.size());
        memberOf.forEach((key, value) -> {
            to.writeUUID(key);
            to.writeUUID(value);
        });
    }

    @Override
    public void handleClient() {
        PenguinTeamsClient.setMembers(memberOf);
    }
}
