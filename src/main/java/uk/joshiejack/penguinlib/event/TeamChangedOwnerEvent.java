package uk.joshiejack.penguinlib.event;

import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;
import java.util.UUID;

public class TeamChangedOwnerEvent extends Event {
    private final UUID teamUUID;
    private final UUID newOwner;

    public TeamChangedOwnerEvent(UUID teamUUID, UUID newOwner) {
        this.teamUUID = teamUUID;
        this.newOwner = newOwner;
    }

    @Nullable //There may no longer be an owner
    public UUID getNewOwner() {
        return newOwner;
    }

    public UUID getTeamUUID() {
        return teamUUID;
    }
}
