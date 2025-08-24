package uk.joshiejack.penguinlib.world.block.entity.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.joshiejack.penguinlib.world.block.entity.PenguinBlockEntity;

import javax.annotation.Nonnull;

@SuppressWarnings("ConstantConditions")
public abstract class InventoryBlockEntity extends PenguinBlockEntity implements Container {
    protected final Lazy<IItemHandler> itemHandler = Lazy.of(this::createHandler);
    protected final NonNullList<ItemStack> items;

    public InventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int size) {
        super(type, pos, state);
        items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @NotNull
    public static <T extends InventoryBlockEntity> ICapabilityProvider getItemProvider(T entity) {
        return new ICapabilityProvider() {
            @Override
            public <C> LazyOptional<C> getCapability(Capability<C> cap, Direction side) {
                if (cap == ForgeCapabilities.ITEM_HANDLER) {
                    return LazyOptional.of(() -> (C) entity.getItemHandler());
                }
                return LazyOptional.empty();
            }
        };
    }

    @Nonnull
    protected IItemHandler createHandler() {
        return new PenguinInvWrapper(this);
    }

    public IItemHandler getItemHandler() {
        return itemHandler.get();
    }

    public int getSlotLimit(int slot) {
        return getMaxStackSize();
    }

    @Nonnull
    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public void setItem(int slot, @Nonnull ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }

        setChanged();
    }

    @Nonnull
    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack =  ContainerHelper.removeItem(items, slot, amount);
        if (!stack.isEmpty()) {
            setChanged();
        }

        return stack;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Nonnull
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean stillValid(@Nonnull Player player) {
        if (level.getBlockEntity(worldPosition) != this)
            return false;
        else
            return player.distanceToSqr((double) worldPosition.getX() + 0.5D, (double) worldPosition.getY() + 0.5D, (double) worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    protected void loadInventory(@Nonnull CompoundTag nbt) {
        ContainerHelper.loadAllItems(nbt, items);
    }

    public CompoundTag saveInventory(@Nonnull CompoundTag nbt) {
        return ContainerHelper.saveAllItems(nbt, items);
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        loadInventory(nbt.getCompound("Inventory"));
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("Inventory", saveInventory(new CompoundTag()));
    }
}
