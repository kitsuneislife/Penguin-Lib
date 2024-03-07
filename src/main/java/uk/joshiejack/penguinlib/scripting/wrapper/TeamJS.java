package uk.joshiejack.penguinlib.scripting.wrapper;

import uk.joshiejack.penguinlib.world.team.PenguinTeam;

public class TeamJS extends AbstractJS<PenguinTeam> {
    public TeamJS(PenguinTeam team) {
        super(team);
    }

    public TeamStatusJS status() {
        return WrapperRegistry.wrap(this);
    }

    public int size() {
        return penguinScriptingObject.members().size();
    }
}
