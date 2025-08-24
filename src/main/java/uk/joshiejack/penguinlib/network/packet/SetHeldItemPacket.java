package uk.joshiejack.penguinlib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;

@Packet(PacketFlow.CLIENTBOUND)
public record SetHeldItemPacket(InteractionHand hand, ItemStack stack) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("set_held_item");
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public SetHeldItemPacket(InteractionHand hand, ItemStack stack) {
        this.hand = hand;
        this.stack = stack;
    }

    public SetHeldItemPacket(FriendlyByteBuf from) {
        this(from.readEnum(InteractionHand.class), from.readItem());
    }

    public void write(FriendlyByteBuf to) {
        to.writeEnum(hand);
        to.writeItem(stack);
    }

    
    public void handle(Player player) {
        player.setItemInHand(hand, stack);
    }
}

