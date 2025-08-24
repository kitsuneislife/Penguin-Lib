package uk.joshiejack.penguinlib.data.generator;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import uk.joshiejack.penguinlib.PenguinLib;

public class PenguinLanguage extends LanguageProvider {
    public PenguinLanguage(PackOutput output) {
        super(output, PenguinLib.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("button.penguinlib.next", "Next");
        add("button.penguinlib.previous", "Previous");
        add("command.penguinlib.team.create.success", "You created a new team with the name: %s");
        add("command.penguinlib.team.create.name_in_use", "You cannot use %s for your new team as it is already in use.");
        add("command.penguinlib.team.create.must_leave", "YOU ARE ALREADY IN A TEAM!\nYou must leave to create a new one!\n%s");
        add("command.penguinlib.team.create.must_leave.tooltip", "Click to leave team");
        add("command.penguinlib.team.leave.cannot", "You cannot leave your own personal team!");
        add("command.penguinlib.team.leave.name_in_use", "You cannot use %s for your new team as it is already in use.");
        add("command.penguinlib.team.leave.success", "You have left your team!");
        add("command.penguinlib.team.join.must_leave", "YOU ARE ALREADY IN A TEAM!\nYou must leave it in order to join a new one!\n%s");
        add("command.penguinlib.team.join.must_leave.tooltip", "Click to leave team");
        add("command.penguinlib.team.join.not_exist", "No team with the name %s can be found!");
        add("command.penguinlib.team.join.success.owner", "The team %s had no owner. You have now taken control!");
        add("command.penguinlib.team.join.success.member", "You are now a member of the team %s, Welcome!");
        add("command.penguinlib.team.join.success.not_invited", "You have not been invited to the %s team. Ask %s for an invite.");
        add("command.penguinlib.team.invite.success", "You have invited %s to join your team.");
        add("command.penguinlib.team.invite.failure", "No player by the name %s could be found.");
        add("command.penguinlib.team.invite.message", "You have been invited to join the %s team. To join -> %s");
        add("command.penguinlib.team.invite.tooltip", "Click to join the team");
        add("command.penguinlib.team.invite.none", "You have no team invites pending.");
        add("command.penguinlib.team.reject.not_exist", "No team with the name %s can be found!");
        add("command.penguinlib.team.reject.not_invited", "You have not been invited to join %s!");
        add("command.penguinlib.team.reject.success", "You have rejected the invite to join %s.");
    }
}
