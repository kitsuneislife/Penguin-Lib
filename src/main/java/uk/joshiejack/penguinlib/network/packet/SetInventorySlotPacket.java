package uk.joshiejack.penguinlib.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.penguinlib.world.block.entity.inventory.InventoryBlockEntity;

import javax.annotation.Nonnull;

@Packet(PacketFlow.CLIENTBOUND)
public record SetInventorySlotPacket(BlockPos pos, int slot, ItemStack stack) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("set_inventory_slot");
    public @Nonnull ResourceLocation id() {
        return ID;
    }

    public SetInventorySlotPacket(FriendlyByteBuf from) {
        this(BlockPos.of(from.readLong()), from.readInt(), from.readItem());
    }

    public void write(FriendlyByteBuf to) {
        to.writeLong(pos.asLong());
        to.writeInt(slot);
        to.writeItem(stack);
    }

    
    public void handle(Player player) {
        BlockEntity tile = player.level().getBlockEntity(pos);
        if (tile instanceof InventoryBlockEntity te) {
            te.setItem(slot, stack);
        }
    }
}

