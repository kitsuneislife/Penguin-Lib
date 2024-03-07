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
public record UnlockNotePacket(Note note) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("unlock_note");
    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public UnlockNotePacket(FriendlyByteBuf buf) {
        this(PenguinNetwork.readRegistry(PenguinRegistries.NOTES, buf));
    }

    @Override
    public void write(@NotNull FriendlyByteBuf buf) {
        PenguinNetwork.writeRegistry(note, buf);
    }

    @Override
    public void handle(Player player) {
        note.unlock(player);
    }
}