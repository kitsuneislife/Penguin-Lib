package uk.joshiejack.penguinlib.world.block.entity.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import uk.joshiejack.penguinlib.data.TimeUnitRegistry;

import javax.annotation.Nonnull;

public abstract class SimpleMachineBlockEntity extends MachineBlockEntity {
    private final String time;

    public SimpleMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, String time) {
        super(type, pos,state, 1);
        this.time = time;
    }

    @Override
    public long getOperationalTime() {
        return TimeUnitRegistry.get(time);
    }

    @Nonnull
    @Override
    public ItemStack removeItem(int slot, int amount) {
        return isActive() ? ItemStack.EMPTY : ContainerHelper.removeItem(items, slot, amount);
    }

    @Override
    public void setItem(int slot, @Nonnull ItemStack stack){
        super.setItem(slot, stack);
        if (isActive() && stack.isEmpty()) {
            onMachineEmptied();
        } else if (canStart()) {
            startMachine();
        }
    }

    @Override
    protected boolean canStart() {
        return canPlaceItem(0, items.get(0));
    }

    protected void onMachineEmptied() {}
}