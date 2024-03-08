package uk.joshiejack.penguinlib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;

import javax.annotation.Nullable;
import java.util.UUID;

public class PenguinNetwork {
    public static void sendToClient(@Nullable ServerPlayer player, PenguinPacket... packets) {
        if (player != null) {
            PacketDistributor.PLAYER.with(player).send(packets);
        }
    }

    public static void sendToTeam(ServerLevel world, UUID uuid, PenguinPacket... packets) {
        PenguinTeam team = PenguinTeams.getTeamFromID(world, uuid);
        if (team != null) {
            team.members().stream()
                    .map(world::getPlayerByUUID)
                    .filter(player -> player instanceof ServerPlayer)
                    .map(player -> (ServerPlayer) player)
                    .forEach(player -> sendToClient(player, packets));
        }
    }

    public static void sendToServer(PenguinPacket packet) {
        PacketDistributor.SERVER.noArg().send(packet);
    }

    public static void sendToEveryone(PenguinPacket packet) {
        PacketDistributor.ALL.noArg().send(packet);
    }

    public static void sendToDimension(ServerLevel world, PenguinPacket... packets) {
        PacketDistributor.DIMENSION.with(world.dimension()).send(packets);
    }

    public static void sendToNearby(ServerLevel world, double x, double y, double z, double distance, PenguinPacket... packets) {
        PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(x, y, z, distance, world.dimension())).send(packets);
    }

    public static void sendToNearby(BlockEntity entity, PenguinPacket... packets) {
        sendToNearby((ServerLevel) entity.getLevel(), entity.getBlockPos().getX(), entity.getBlockPos().getY(), entity.getBlockPos().getZ(), 64D, packets);
    }

    public static void sendToNearby(Entity entity, PenguinPacket... packets) {
        sendToNearby((ServerLevel) entity.level(), entity.getX(), entity.getY(), entity.getZ(), 64D, packets);
    }

    public static <P extends PenguinPacket> void handlePacket(P packet, PlayPayloadContext context) {
        context.player().ifPresent(player -> {
            if (player instanceof ServerPlayer spl)
                context.workHandler().submitAsync(() -> packet.handleServer(spl));
            else context.workHandler().submitAsync(() -> packet.handleClient());
        });
    }

    public static <T extends ReloadableRegistry.PenguinRegistry<T>> T readRegistry(ReloadableRegistry<T> registry, FriendlyByteBuf buf) {
        return registry.get(buf.readResourceLocation());
    }

    public static <T extends ReloadableRegistry.PenguinRegistry<T>> void writeRegistry(T obj, FriendlyByteBuf buf) {
        buf.writeResourceLocation(obj.id());
    }
}
