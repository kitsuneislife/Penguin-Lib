package uk.joshiejack.penguinlib.world.note.type;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.client.gui.PenguinFonts;
import uk.joshiejack.penguinlib.client.gui.book.widget.NoteWidget;
import uk.joshiejack.penguinlib.data.PenguinRegistries;
import uk.joshiejack.penguinlib.world.note.Note;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NoteType {
    //Codec with just the name?
    public static final NoteType TEXT = new NoteType("text");

    //Grab a codec with a data field which gets the data from this typ
    private final String name;

    public NoteType(String text) {
        this.name = text;
        PenguinRegistries.NOTE_TYPES.put(text, this);
    }

    @Override
    public String toString() {
        return name;
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics,NoteWidget widget, int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        int j = widget.getFGColor();
        graphics.drawCenteredString(fontrenderer, widget.getMessage(), widget.getX() + widget.getWidth() / 2, widget.getY(), j | Mth.ceil(widget.getAlpha() * 255.0F) << 24);
        widget.getChatter().draw(graphics, PenguinFonts.UNICODE.get(), widget.getX() + 2, widget.getY() + 8, 4210752);
    }

    public int getTextWidth() {
        return 165;
    }

    public int getLineCount() {
        return 18;
    }

    @Nullable
    public ChatFormatting getTextFormatting() {
        return null;
    }

    @Nonnull
    public Component getText(Note note) {
        return note.getText();
    }


    public static class Serializer {
        public void toNetwork(FriendlyByteBuf buf, NoteType type) {

        }

        public NoteType fromNetwork(FriendlyByteBuf buf) {

            return null;
        }
    }
}