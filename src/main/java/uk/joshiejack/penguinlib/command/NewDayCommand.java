package uk.joshiejack.penguinlib.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;

public class NewDayCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("newDay")
                .executes(ctx -> {
                    ServerLevel world = ctx.getSource().getLevel();
                    long j = world.getLevelData().getDayTime() + 24000L;
                    world.setDayTime(j - j % 24000L);  // Simplified for Forge 1.20.1
                    return 1;
                });
    }
}