package uk.joshiejack.penguinlib.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import uk.joshiejack.penguinlib.PenguinConfig;
import uk.joshiejack.penguinlib.PenguinLib;

@Mod.EventBusSubscriber(modid = PenguinLib.MODID)
public class PenguinLibCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        if (PenguinConfig.enableTeamCommands.get()) {
            event.getDispatcher().register(
                    LiteralArgumentBuilder.<CommandSourceStack>literal("penguin")
                            .then(NewDayCommand.register())
                            .then(Commands.literal("team")
                                    .then(CreateTeamCommand.register())
                                    .then(InviteCommand.register())
                                    .then(ListInvitesCommand.register())
                                    .then(JoinTeamCommand.register())
                                    .then(LeaveTeamCommand.register())
                                    .then(RenameTeamCommand.register())
                                    .then(RejectTeamCommand.register()))
            );
        }
    }
}
