package uk.joshiejack.penguinlib.command;


import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import uk.joshiejack.penguinlib.util.helper.StringHelper;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;

public class ListInvitesCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("invite_list")
                .executes(ctx -> {
                    boolean success = false;
                    for (PenguinTeam team: PenguinTeams.get(ctx.getSource().getLevel()).teams()) {
                        if (team.isInvited(ctx.getSource().getPlayerOrException().getUUID())) {
                            ctx.getSource().getPlayerOrException().createCommandSourceStack().sendSuccess(() -> Component.translatable("command.penguinlib.team.invite.message",
                                    team.getName(), StringHelper.withClickableCommand(ChatFormatting.GREEN, "/penguin team join %s", team.getName())), false);
                            success = true;
                        }
                    }

                    if (!success) {
                        ctx.getSource().sendFailure(Component.translatable("command.penguinlib.team.invite.none"));
                    }

                    return success ? 1 : 0;
                });
    }
}