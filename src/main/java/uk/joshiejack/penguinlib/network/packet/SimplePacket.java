package uk.joshiejack.penguinlib.network.packet;

import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;

public interface SimplePacket extends PenguinPacket{
    @Override
    default void write(@Nonnull FriendlyByteBuf buf) {}
}
