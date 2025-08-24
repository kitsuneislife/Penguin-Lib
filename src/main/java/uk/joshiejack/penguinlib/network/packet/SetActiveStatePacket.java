package uk.joshiejack.penguinlib.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.penguinlib.world.block.entity.machine.MachineBlockEntity;

import javax.annotation.Nonnull;

@Packet(PacketFlow.CLIENTBOUND)
public record SetActiveStatePacket(BlockPos pos, boolean active) implements PenguinPacket {
    public static final ResourceLocation ID = new ResourceLocation("penguinlib", "set_active_state");
    public @Nonnull ResourceLocation id() {
        return ID;
    }

    public SetActiveStatePacket(FriendlyByteBuf from) {
        this(from.readBlockPos(), from.readBoolean());
    }

    public void write(FriendlyByteBuf to) {
        to.writeBlockPos(pos);
        to.writeBoolean(active);
    }

    
    public void handle(Player player) {
        BlockEntity tile = player.level().getBlockEntity(pos);
        if (tile instanceof MachineBlockEntity) {
            ((MachineBlockEntity)tile).setState(active);
        }
    }
}

