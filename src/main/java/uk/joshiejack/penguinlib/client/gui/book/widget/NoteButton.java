package uk.joshiejack.penguinlib.client.gui.book.widget;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.client.gui.PenguinFonts;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.client.gui.widget.AbstractButton;
import uk.joshiejack.penguinlib.world.note.Note;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NoteButton extends AbstractButton<Book> {
    private final Note note;
    private final NoteWidget selected;

    public NoteButton(Book book, @Nullable NoteWidget selected, Note note, int x, int y, Button.OnPress pressable, Tooltip tooltip) {
        super(book, x, y, 16, 16, note.getTitle(), pressable);
        setTooltip(tooltip);
        this.note = note;
        this.selected = selected;
    }

    @Override
    protected void renderButton(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, boolean hovered) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        boolean unlocked = note.isDefault() || note.isUnlocked(player);
        if (selected != null && note.equals(selected.getNote())) graphics.fill(getX(), getY(), getX() + width, getY() + height, 0x559C8C63);
        else if (!hovered || !unlocked) graphics.fill(getX(), getY(), getX() + width, getY() + height, 0x55B0A483);
        else graphics.fill(getX(), getY(), getX() + width, getY() + height, 0x55C4B9A2);
//
//        //TODO: poseoffsetinstead? mc.gui.setBlitOffset(100);
        (unlocked ? note.getIcon() : note.getIcon().shadowed()).render(Minecraft.getInstance(), graphics, getX(), getY());
        if (unlocked && !note.isRead(player)) {
            graphics.pose().pushPose();
            graphics.pose().translate(0D, 0D, 110D);
            graphics.drawString(PenguinFonts.UNICODE.get(), ChatFormatting.BOLD + "NEW", getX() + 1, getY() + 8, 0xFFFFFFFF);
            graphics.pose().popPose();
        }
    }

//    @Override
//    public void renderToolTip(@Nonnull GuiGraphics mtx, int mouseX, int mouseY) {
//        if (note.isDefault() || note.isUnlocked(Minecraft.getInstance().player))
//            super.renderToolTip(mtx, mouseX, mouseY);
//    }
}