package uk.joshiejack.penguinlib.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import uk.joshiejack.penguinlib.util.helper.StringHelper;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;

import java.util.UUID;

public class CreateTeamCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("create")
                .then(Commands.argument("name", StringArgumentType.string())
                .executes(ctx -> {
                    PenguinTeams teams = PenguinTeams.getTeamsFromContext(ctx);
                    PenguinTeam current = PenguinTeams.getTeamFromContext(ctx);
                    CommandSourceStack source = ctx.getSource();
                    UUID playerID = source.getPlayerOrException().getUUID();
                    //If the player is in a team already, they cannot create one
                    if (!current.getID().equals(playerID)) {
                        source.sendFailure(Component.translatable("command.penguinlib.team.create.must_leave",
                                StringHelper.withClickableCommand("/penguin team leave", "command.penguinlib.team.leave.tooltip")));
                        return 0;
                    }

                    //If the team name is already in use then error
                    String name = StringArgumentType.getString(ctx, "name");
                    if (teams.nameExists(name)) {
                        source.sendFailure(Component.translatable("command.penguinlib.team.create.name_in_use", name));
                        return 0;
                    }

                    teams.changeTeam(ctx, UUID.randomUUID(), (pt) -> pt.setName(name));
                    source.sendSuccess(() -> Component.translatable("command.penguinlib.team.create.success", name), false);
                    return 1;
                }));
    }
}
