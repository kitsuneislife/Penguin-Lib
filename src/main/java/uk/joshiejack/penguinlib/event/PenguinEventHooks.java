package uk.joshiejack.penguinlib.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.helper.FluidHelper;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;

@Mod.EventBusSubscriber(modid = PenguinLib.MODID)
public class PenguinEventHooks {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level.getDayTime() % TimeHelper.TICKS_PER_DAY == 1 && event.level instanceof ServerLevel serverWorld)
            MinecraftForge.EVENT_BUS.post(new NewDayEvent(serverWorld)); //Post the new day event, to update
    }

    /**
     * Same as the original method, but without shrinking the stack
     *
     * @param player the player using the bonemeal
     * @param level  the world
     * @param pos    the position of the block
     * @param state  the state of the block
     */
    public static int onApplyBonemeal(Player player, Level level, BlockPos pos, BlockState state, ItemStack stack) {
        BonemealEvent event = new BonemealEvent(player, level, pos, state, stack);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return -1;
        return event.getResult() == Event.Result.ALLOW ? 1 : 0;
    }


    /**
     * Same as the original method, but without shrinking the stack
     *
     * @param stack  the bonemeal stack
     * @param world  the world
     * @param pos    the position of the block
     * @param player the player using the bonemeal
     */
    public static boolean applyBonemeal(ItemStack stack, Level world, BlockPos pos, Player player) {
        BlockState blockstate = world.getBlockState(pos);
        // EventHooks não existe no Forge 1.20.1, removendo esta linha e hook check
        // int hook = EventHooks.onApplyBonemeal(player, world, pos, blockstate, stack);
        // if (hook != 0) return hook > 0;
        if (blockstate.getBlock() instanceof BonemealableBlock bonemealableBlock) {
            if (bonemealableBlock.isValidBonemealTarget(world, pos, blockstate, false)) {
                if (world instanceof ServerLevel serverLevel) {
                    if (bonemealableBlock.isBonemealSuccess(serverLevel, world.random, pos, blockstate)) {
                        bonemealableBlock.performBonemeal(serverLevel, world.random, pos, blockstate);
                    }
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Hook for using the watering can
     *
     * @param stack  the watering can
     * @param player the player using the watering can
     * @param level  the world
     * @param pos    the position of the block
     */
    public static Event.Result onUseWateringCan(ItemStack stack, Player player, Level level, BlockPos pos) {
        UseWateringCanEvent event = new UseWateringCanEvent(player, stack, level, pos);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return Event.Result.DENY;
        if (event.getResult() == Event.Result.ALLOW) {
            if (!player.isCreative()) {
                FluidHelper.drainContainer(stack, 1);
            }

            return Event.Result.ALLOW;
        }

        return Event.Result.DEFAULT;
    }

    public static boolean useWateringCan(Player player, Level level, BlockPos pos, ItemStack stack) {
        net.minecraftforge.eventbus.api.Event.Result hook = PenguinEventHooks.onUseWateringCan(stack, player, level, pos);
        if (hook != Event.Result.DEFAULT) return hook == net.minecraftforge.eventbus.api.Event.Result.ALLOW;

        BlockState state = level.getBlockState(pos);
        if (state.getProperties().contains(BlockStateProperties.MOISTURE) &&
                state.getValue(BlockStateProperties.MOISTURE) != 7) {
            level.setBlock(pos, state.setValue(BlockStateProperties.MOISTURE, 7), 2);
            if (!player.isCreative() && !stack.isEmpty()) {
                FluidHelper.drainContainer(stack, 1);
            }

            return true;
        } else return false;
    }
}
