package uk.joshiejack.penguinlib.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;

import javax.annotation.Nonnull;

@Packet(PacketFlow.CLIENTBOUND)
public record BlockRenderUpdatePacket(BlockPos pos) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("block_render_update");

    public @Nonnull ResourceLocation id() {
        return ID;
    }

    public BlockRenderUpdatePacket(FriendlyByteBuf from) {
        this(BlockPos.of(from.readLong()));
    }

    public void write(FriendlyByteBuf to) {
        to.writeLong(pos.asLong());
    }
}

