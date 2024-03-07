package uk.joshiejack.penguinlib.network.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public abstract class SyncCompoundTagPacket implements PenguinPacket {
    public CompoundTag tag;

    public SyncCompoundTagPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public SyncCompoundTagPacket(final FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }
}