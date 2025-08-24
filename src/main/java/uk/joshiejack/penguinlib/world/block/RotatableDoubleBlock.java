package uk.joshiejack.penguinlib.world.block;



import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public abstract class RotatableDoubleBlock extends RotatableBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public RotatableDoubleBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    protected boolean isTop(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER;
    }

    @Override
    public boolean skipRendering(@NotNull BlockState state, @NotNull BlockState stateAbove, @NotNull Direction direction) {
        return isTop(state);
    }

    @Override
    public @NotNull InteractionResult use(@Nonnull BlockState state, @NotNull Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult blockRayTraceResult) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER)
            return super.use(world.getBlockState(pos.below()), world, pos.below(), player, hand, blockRayTraceResult);
        else return super.use(state, world, pos, player, hand, blockRayTraceResult);
    }

    @Nonnull
    @Override
    public BlockState updateShape(@Nonnull BlockState oldState, Direction direction, @Nonnull BlockState newState, @Nonnull LevelAccessor world, @Nonnull BlockPos oldPos, @Nonnull BlockPos newPos) {
        DoubleBlockHalf doubleblockhalf = oldState.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (direction == Direction.UP))
            return newState.is(this) && newState.getValue(HALF) != doubleblockhalf ? oldState.setValue(FACING, newState.getValue(FACING)) : Blocks.AIR.defaultBlockState();
        else
            return doubleblockhalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !oldState.canSurvive(world, oldPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(oldState, direction, newState, world, oldPos, newPos);
    }

    @Override
    public void playerWillDestroy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
        if (!world.isClientSide && player.isCreative())
            RotatableDoubleBlock.preventCreativeDropFromBottomPart(world, pos, state, player);
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void playerDestroy(@Nonnull Level world, @Nonnull Player player, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable BlockEntity tile, @Nonnull ItemStack stack) {
        super.playerDestroy(world, player, pos, Blocks.AIR.defaultBlockState(), tile, stack);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext ctx) {
        BlockPos blockpos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        if (blockpos.getY() < level.getMaxBuildHeight() - 1 && ctx.getLevel().getBlockState(blockpos.above()).canBeReplaced(ctx))
            return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection()).setValue(HALF, DoubleBlockHalf.LOWER);
        else
            return null;
    }

    @Override
    public void setPlacedBy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity entity, @Nonnull ItemStack stack) {
        world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader world, @Nonnull BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = world.getBlockState(blockpos);
        return state.getValue(HALF) == DoubleBlockHalf.LOWER || blockstate.is(this);
    }

    @Override
    public long getSeed(@Nonnull BlockState state, @Nonnull BlockPos pos) {
        return Mth.getSeed(pos.getX(), pos.below(state.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING);
    }

    private static void preventCreativeDropFromBottomPart(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = world.getBlockState(blockpos);
            if (blockstate.getBlock() == state.getBlock() && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                world.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                world.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
        }
    }
}
