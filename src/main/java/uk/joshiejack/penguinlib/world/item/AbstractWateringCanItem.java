package uk.joshiejack.penguinlib.world.item;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.event.PenguinEventHooks;
import uk.joshiejack.penguinlib.event.UseWateringCanEvent;
import uk.joshiejack.penguinlib.util.helper.FluidHelper;

import javax.annotation.Nonnull;
import java.util.Set;

public abstract class AbstractWateringCanItem extends Item {
    public AbstractWateringCanItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.round(((float) FluidHelper.getFluidInTankFromStack(stack) / FluidHelper.getFluidInTankFromStack(stack)) * 13F);
    }

    @Override
    public int getBarColor(@Nonnull ItemStack stack) {
        return FluidHelper.getTankCapacityFromStack(stack) > 0 ? 0x006DD9 : 0x555555;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (attemptToFill(level, player, stack)) return InteractionResultHolder.success(stack);
        else return InteractionResultHolder.pass(stack);
    }

    @SuppressWarnings("ConstantConditions")
    private boolean attemptToFill(Level level, Player player, ItemStack stack) {
        BlockHitResult rayTraceResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (rayTraceResult != null && rayTraceResult.getType() == BlockHitResult.Type.BLOCK) {
            BlockState state = level.getBlockState(rayTraceResult.getBlockPos());
            if (state.getFluidState().is(FluidTags.WATER)) {
                return FluidHelper.fillContainer(stack, FluidHelper.getTankCapacityFromStack(stack));
            }
        }

        return false;
    }

    public Set<BlockPos> getPositions(Player player, Level level, BlockPos pos) {
        return Sets.newHashSet(pos, pos.below());
    }

    public boolean water(Player player, Level level, BlockPos pos, ItemStack stack, InteractionHand hand) {
        return PenguinEventHooks.useWateringCan(player, level, pos, stack);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        InteractionHand hand = ctx.getHand();
        Direction direction = ctx.getClickedFace();
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        ItemStack itemstack = player.getItemInHand(hand);
        if (FluidHelper.getFluidInTankFromStack(itemstack) > 0) {
            if (!player.mayUseItemAt(pos.offset(direction.getNormal()), direction, itemstack)) {
                return InteractionResult.FAIL;
            } else if (level.dimensionType().ultraWarm()) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

                for (int l = 0; l < 8; ++l) {
                    level.addParticle(ParticleTypes.LARGE_SMOKE, (double) x + Math.random(), (double) y + Math.random(), (double) z + Math.random(), 0.0D, 0.0D, 0.0D);
                }

                ItemStack stack = player.getItemInHand(hand);
                if (!player.isCreative() && !stack.isEmpty()) {
                    FluidHelper.drainContainer(stack, 1);
                }

                return InteractionResult.SUCCESS;
            } else {
                boolean used = false;
                for (BlockPos target : getPositions(player, level, pos)) {
                    if (FluidHelper.getTankCapacityFromStack(itemstack) <= 0) break;
                    if (water(player, level, target, itemstack, hand)) {
                        level.playSound(null, target, SoundEvents.GENERIC_SWIM, SoundSource.NEUTRAL,
                                player.getRandom().nextFloat() * 0.25F + 0.7F, player.getRandom().nextFloat() + 0.5F);
                        for (int i = 0; i < 60; i++) {
                            double x = pos.getX() + player.getRandom().nextFloat();
                            double z = pos.getZ() + player.getRandom().nextFloat();
                            level.addAlwaysVisibleParticle(ParticleTypes.SPLASH, x, pos.getY() + 1D, z, 0, 0, 0);
                        }

                        NeoForge.EVENT_BUS.post(new UseWateringCanEvent.Post(player, itemstack, level, pos));
                        used = true;
                    }
                }

                return used ? InteractionResult.SUCCESS : InteractionResult.PASS;
            }
        } else return InteractionResult.FAIL;
    }

    public ItemStack createFilledWateringCan(ItemStack stack) {
        FluidHelper.fillContainer(stack, FluidHelper.getTankCapacityFromStack(stack));
        return stack;
    }
}
