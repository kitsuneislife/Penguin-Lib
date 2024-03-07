package uk.joshiejack.penguinlib.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import uk.joshiejack.penguinlib.util.helper.StringHelper;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;

public class InviteCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("invite")
                .then(Commands.argument("player_name", StringArgumentType.string())
                        .executes(ctx -> {
                            PenguinTeam current = PenguinTeams.getTeamFromContext(ctx);
                            if (current.getID().equals(ctx.getSource().getPlayerOrException().getUUID())) {
                                ctx.getSource().sendSuccess(() -> Component.translatable("command.penguinlib.team.invite.personal"), false);
                                return 0;
                            }

                            String playerName = StringArgumentType.getString(ctx, "player_name");
                            ServerPlayer player = ctx.getSource().getLevel().getServer().getPlayerList().getPlayerByName(playerName);
                            if (player == null) {
                                ctx.getSource().sendSuccess(() -> Component.translatable("command.penguinlib.team.invite.failure", playerName), false);
                                return 0;
                            } else {
                                PenguinTeams.get(ctx.getSource().getLevel()).getTeam(ctx.getSource().getPlayerOrException().getUUID()).invite(ctx.getSource().getLevel(), player.getUUID());
                                ctx.getSource().sendSuccess(() -> Component.translatable("command.penguinlib.team.invite.success", playerName), false);
                                player.createCommandSourceStack().sendSuccess(() -> Component.translatable("command.penguinlib.team.invite.message",
                                        current.getName(), StringHelper.withClickableCommand(ChatFormatting.GREEN, "/penguin team join " + current.getName(), "command.penguinlib.team.invite.tooltip")), false);
                                return 1;
                            }
                        }));
    }
}