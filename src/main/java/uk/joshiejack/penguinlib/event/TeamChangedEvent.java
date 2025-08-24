package uk.joshiejack.penguinlib.event;

import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.LevelEvent;

import java.util.UUID;

public class TeamChangedEvent extends LevelEvent {
    private final UUID player;
    private final UUID oldTeam;
    private final UUID newTeam;

    public TeamChangedEvent(Level level, UUID player, UUID oldTeam, UUID newTeam) {
        super(level);
        this.player = player;
        this.oldTeam = oldTeam;
        this.newTeam = newTeam;
    }

    public UUID getPlayer() {
        return player;
    }

    public UUID getOldTeam() {
        return oldTeam;
    }

    public UUID getNewTeam() {
        return newTeam;
    }
}
