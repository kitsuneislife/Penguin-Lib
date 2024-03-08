package uk.joshiejack.penguinlib.world.block.entity.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.network.packet.SetActiveStatePacket;
import uk.joshiejack.penguinlib.world.block.entity.inventory.InventoryBlockEntity;

import javax.annotation.Nonnull;

public abstract class MachineBlockEntity extends InventoryBlockEntity {
    private Boolean shouldRender;
    private boolean active;
    private long started;
    private long passed;

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int size) {
        super(type, pos, state, size);
    }

    //1000 = 60 minutes
    //500 = 30 minutes
    //250 = 15 minutes
    //50 = 3 minutes

    protected void startMachine() {
        active = true;
        assert level != null;
        started = level.getGameTime();
        if (!level.isClientSide) {
            PenguinNetwork.sendToNearby(this, new SetActiveStatePacket(worldPosition, true));
        }
    }


    public boolean shouldRender(ItemStack item) {
        if (shouldRender == null) {
            BlockState state = getBlockState();
            boolean isDouble = state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF);
            shouldRender = !isDouble || state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
        }

        return shouldRender && !item.isEmpty() && !isActive();
    }

    public abstract void finishMachine();

    public abstract long getOperationalTime();

    public void setState(boolean active) {
        this.active = active;
        this.markUpdated();
    }

    public boolean isActive() {
        return active;
    }

    protected boolean canStart() { return false; }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MachineBlockEntity entity) {
        if (level.getGameTime() % 50 == 1) {
            if (!entity.isActive() && entity.canStart()) entity.startMachine();
            if (entity.active && entity.started != 0L) {
                entity.passed += (level.getGameTime() - entity.started);
                entity.started = level.getGameTime(); //Reset the time
                if (entity.passed >= entity.getOperationalTime()) {
                    entity.active = false;
                    entity.passed = 0L;
                    entity.started = 0L;
                    entity.finishMachine();
                    PenguinNetwork.sendToNearby(entity, new SetActiveStatePacket(pos, false));
                }
            }
        }
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        active = nbt.getBoolean("Active");
        started = nbt.getLong("Started");
        passed = nbt.getLong("Passed");
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("Active", active);
        nbt.putLong("Started", started);
        nbt.putLong("Passed", passed);
    }
}
