package uk.joshiejack.penguinlib.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;

import java.util.UUID;

public class LeaveTeamCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("leave")
                .executes(ctx -> {
                    PenguinTeams teams = PenguinTeams.getTeamsFromContext(ctx);
                    PenguinTeam current = PenguinTeams.getTeamFromContext(ctx);
                    CommandSourceStack source = ctx.getSource();
                    UUID playerID = source.getPlayerOrException().getUUID();
                    //If this is a single player team you cannot leave
                    if (current.getID().equals(playerID)) {
                        source.sendFailure(Component.translatable("command.penguinlib.team.leave.cannot"));
                        return 0;
                    }

                    teams.changeTeam(ctx, playerID, (pt) -> {});
                    source.sendSuccess(() -> Component.translatable("command.penguinlib.team.leave.success"), false);
                    return 1;
                });
    }
}