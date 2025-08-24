package uk.joshiejack.penguinlib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.data.PenguinRegistries;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.penguinlib.world.note.Note;

@Packet(value = PacketFlow.CLIENTBOUND)
public record ReadNotePacket(Note note) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("read_note");

    public @NotNull ResourceLocation id() {
        return ID;
    }

    public ReadNotePacket(FriendlyByteBuf buf) {
        this(PenguinNetwork.readRegistry(PenguinRegistries.NOTES, buf));
    }

    public void write(@NotNull FriendlyByteBuf buf) {
        PenguinNetwork.writeRegistry(note, buf);
    }

    
    public void handle(Player player) {
        note.read(player);
    }
}

