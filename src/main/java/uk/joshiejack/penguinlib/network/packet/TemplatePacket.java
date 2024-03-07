package uk.joshiejack.penguinlib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;

@Packet(value = PacketFlow.CLIENTBOUND)
public class TemplatePacket implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("template_packet");
    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public TemplatePacket() {

    }

    public TemplatePacket(FriendlyByteBuf buf) {

    }

    @Override
    public void write(FriendlyByteBuf to) {

    }


    @Override
    public void handle(Player player) {

    }
}