package uk.joshiejack.penguinlib.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class PenguinBlock extends Block {
    protected boolean hasInventory = false;

    public PenguinBlock(Block.Properties properties) {
        super(properties);
    }

    protected void setHasInventory() {
        hasInventory = true;
    }

    protected int getInsertAmount(IItemHandler handler, ItemStack held) {
        return held.getCount();
    }

    protected InteractionResult insert(Player player, InteractionHand hand, IItemHandler handler) {
        ItemStack held = player.getItemInHand(hand).copy();
        held.setCount(getInsertAmount(handler, held));
        ItemStack ret = ItemHandlerHelper.insertItem(handler, held, false);
        if (ret.getCount() != held.getCount() || ret.isEmpty()) {
            if (!player.isCreative())
                player.getItemInHand(hand).shrink(held.getCount() - ret.getCount());
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    protected int getExtractAmount(IItemHandler handler, int slot) {
        return 1;
    }

    protected InteractionResult extract(Player player, IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            int extract = getExtractAmount(handler, i);
            if (extract > 0) {
                ItemStack extracted = handler.extractItem(i, extract, false);
                if (!extracted.isEmpty()) {
                    ItemHandlerHelper.giveItemToPlayer(player, extracted);
                    return InteractionResult.SUCCESS; //Successfully extracted an item
                }
            }
        }

        return InteractionResult.PASS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@Nonnull BlockState state, @NotNull Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult blockRayTraceResult) {
        if (!hasInventory) return super.use(state, world, pos, player, hand, blockRayTraceResult);
        BlockEntity tileentity = world.getBlockEntity(pos);
        if (tileentity == null || player.isShiftKeyDown())
            return InteractionResult.FAIL;
        else {
            LazyOptional<IItemHandler> capability = tileentity.getCapability(ForgeCapabilities.ITEM_HANDLER, blockRayTraceResult.getDirection());
            return capability.map(handler -> {
                return !player.getItemInHand(hand).isEmpty() && insert(player, hand, handler) == InteractionResult.SUCCESS ? InteractionResult.SUCCESS : extract(player, handler);
            }).orElse(InteractionResult.PASS);
        }
    }

    protected void onRemoved(IItemHandler handler, Level world, BlockPos pos) {
        NonNullList<ItemStack> list = NonNullList.withSize(handler.getSlots(), ItemStack.EMPTY);
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack inSlot = handler.getStackInSlot(i);
            if (!inSlot.isEmpty())
                list.set(i, inSlot);
        }

        Containers.dropContents(world, pos, list);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(@NotNull BlockState oldState, @Nonnull Level world, @Nonnull BlockPos pos, @NotNull BlockState newState, boolean bool) {
        if (hasInventory) {
            if (!oldState.is(newState.getBlock())) {
                BlockEntity tileentity = world.getBlockEntity(pos);
                if (tileentity != null) {
                    LazyOptional<IItemHandler> capability = tileentity.getCapability(ForgeCapabilities.ITEM_HANDLER, null);
                    capability.ifPresent(handler -> {
                        onRemoved(handler, world, pos);
                        world.updateNeighbourForOutputSignal(pos, this);
                    });
                }

                super.onRemove(oldState, world, pos, newState, bool);
            }
        } else super.onRemove(oldState, world, pos, newState, bool);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeB, BlockEntityTicker<? super E> ticker) {
        return typeB == typeA ? (BlockEntityTicker<A>) ticker : null;
    }
}