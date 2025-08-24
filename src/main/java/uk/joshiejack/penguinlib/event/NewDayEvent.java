package uk.joshiejack.penguinlib.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.level.LevelEvent;

public class NewDayEvent extends LevelEvent {
    public NewDayEvent(ServerLevel world) {
        super(world);
    }

    @Override
    public ServerLevel getLevel() {
        return (ServerLevel) super.getLevel();
    }
}
