package uk.joshiejack.penguinlib.scripting.wrapper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;

public class TeamStatusJS extends AbstractJS<TeamJS> {
    public TeamStatusJS(TeamJS player) {
        super(player);
    }

    public int get(String status) {
        CompoundTag data = penguinScriptingObject.penguinScriptingObject.getData();
        return data.contains("PenguinStatuses") ? data.getCompound("PenguinStatuses").getInt(status) : 0;
    }

    public void set(LevelJS<?> levelJS, String status, int value) {
        PenguinTeam team = penguinScriptingObject.penguinScriptingObject;
        CompoundTag data = team.getData();
        if (!data.contains("PenguinStatuses")) data.put("PenguinStatuses", new CompoundTag());
        if (value == 0) data.getCompound("PenguinStatuses").remove(status);
        else data.getCompound("PenguinStatuses").putInt(status, value);
        if (!team.isClient() && levelJS.penguinScriptingObject instanceof ServerLevel level) {
            //Mark the data as dirty?
            PenguinTeams.get(level).setDirty();
            team.syncToTeam(level); //Sync the data to the client
        }
    }

    public void adjust(LevelJS<?> levelJS, String status, int value) {
        set(levelJS, status, get(status) + value);
    }

    public void adjustWithRange(LevelJS<?> levelJS, String status, int value, int min, int max) {
        set(levelJS, status, Math.min(max, Math.max(min, get(status) + value)));
    }
}
