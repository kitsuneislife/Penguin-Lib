package uk.joshiejack.penguinlib.network.packet;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.util.helper.PlayerHelper;

public interface PenguinPacket {
    default void handleClient() {
        handle(PlayerHelper.getClient());
    }

    default void handleServer(ServerPlayer spl) {
        handle(spl);
    }

    default void handle(Player player) {}
}

