package uk.joshiejack.penguinlib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;

import javax.annotation.Nullable;
import java.util.UUID;

public class PenguinNetwork {
    public static void sendToClient(@Nullable ServerPlayer player, PenguinPacket... packets) {
        if (player != null) {
            // For Forge 1.20.1, this needs to be handled through the proper network channel
            // PacketDistributor.PLAYER.with(() -> player).send(packets);
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
        // For Forge 1.20.1, we need to send packets through the network channel
        // This will need to be handled through the proper network registration
        // PacketDistributor.SERVER.noArg().send(packet);
    }

    public static void sendToEveryone(PenguinPacket packet) {
        // For Forge 1.20.1, we need to send packets through the network channel  
        // This will need to be handled through the proper network registration
        // PacketDistributor.ALL.noArg().send(packet);
    }

    public static void sendToDimension(ServerLevel world, PenguinPacket... packets) {
        // For Forge 1.20.1, this needs to be handled through the proper network channel
        // PacketDistributor.DIMENSION.with(() -> world.dimension()).send(packets);
    }

    public static void sendToNearby(ServerLevel world, double x, double y, double z, double distance, PenguinPacket... packets) {
        // For Forge 1.20.1, this needs to be handled through the proper network channel
        // PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, distance, world.dimension())).send(packets);
    }

    public static void sendToNearby(BlockEntity entity, PenguinPacket... packets) {
        sendToNearby((ServerLevel) entity.getLevel(), entity.getBlockPos().getX(), entity.getBlockPos().getY(), entity.getBlockPos().getZ(), 64D, packets);
    }

    public static void sendToNearby(Entity entity, PenguinPacket... packets) {
        sendToNearby((ServerLevel) entity.level(), entity.getX(), entity.getY(), entity.getZ(), 64D, packets);
    }

    public static <P extends PenguinPacket> void handlePacket(P packet, NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null) {
            context.enqueueWork(() -> packet.handleServer(player));
        } else {
            context.enqueueWork(() -> packet.handleClient());
        }
    }

    public static <T extends ReloadableRegistry.PenguinRegistry<T>> T readRegistry(ReloadableRegistry<T> registry, FriendlyByteBuf buf) {
        return registry.getOrEmpty(buf.readResourceLocation());
    }

    public static <T extends ReloadableRegistry.PenguinRegistry<T>> void writeRegistry(T obj, FriendlyByteBuf buf) {
        buf.writeResourceLocation(obj.id());
    }
}
