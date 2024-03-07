package uk.joshiejack.penguinlib.scripting.wrapper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.network.packet.SyncPlayerTagPacket;
import uk.joshiejack.penguinlib.util.helper.PlayerHelper;

public class PlayerStatusJS extends AbstractJS<PlayerJS> {
    public PlayerStatusJS(PlayerJS player) {
        super(player);
    }

    public int get(String status) {
        Player player = penguinScriptingObject.penguinScriptingObject;
        return PlayerHelper.getPenguinStatuses(player).getInt(status);
    }

    public void set(String status, int value) {
        Player player = penguinScriptingObject.penguinScriptingObject;
        CompoundTag tag = PlayerHelper.getPenguinStatuses(player);
        if (value == 0) tag.remove(status);
        else tag.putInt(status, value);
        if (player instanceof ServerPlayer sp) {
            PenguinNetwork.sendToClient(sp, new SyncPlayerTagPacket("PenguinStatuses", player));
        }
    }

    public void adjust(String status, int value) {
        set(status, get(status) + value);
    }

    public void adjustWithRange(String status, int value, int min, int max) {
        set(status, Math.min(max, Math.max(min, get(status) + value)));
    }
}
