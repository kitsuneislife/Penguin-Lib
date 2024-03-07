package uk.joshiejack.penguinlib.world.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public abstract class PenguinBlockEntity extends BlockEntity {
    public PenguinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()  {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    @Nonnull
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }


    protected void markUpdated() {
        this.setChanged();
        assert this.level != null;
        this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
}