package uk.joshiejack.penguinlib.client.gui.book.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.client.gui.widget.AbstractButton;
import uk.joshiejack.penguinlib.util.icon.Icon;

import javax.annotation.Nonnull;

public abstract class TabButton extends AbstractButton<Book> {
    protected final boolean isSelected;
    protected final Icon icon;

    public TabButton(Book book, Icon icon, int x, int y, Component name, Button.OnPress action, Tooltip tooltip, boolean isSelected) {
        super(book, x, y, 26, 32, name, action);
        setTooltip(tooltip);
        this.isSelected = isSelected;
        this.icon = icon;
    }

    @Override
    public void onPress() {
        super.onPress();
        //screen.setEditingTextField(null);
    }

    public static class Left extends TabButton {
        public Left(Book book, Icon icon, int x, int y, Component name, Button.OnPress action, Tooltip tooltip, boolean isSelected) {
            super(book, icon, x, y, name, action, tooltip, isSelected);
        }

        @Override
        protected void renderButton(@Nonnull GuiGraphics matrix, int mouseX, int mouseY, float partialTicks, boolean hovered) {
            //screen.bindLeftTexture(); //TODO: Switch these to "sprites"
            int yPos = 32 * (isSelected ? 1 : hovered ? 2: 0);
            matrix.blit(screen.backgroundL, getX(), getY(), 26, yPos, width, height);
            icon.render(Minecraft.getInstance(), matrix, getX() + 10, getY() + 8);
        }
    }

    public static class Right extends TabButton {
        public Right(Book book, Icon icon, int x, int y, Component name, Button.OnPress action, Tooltip tooltip, boolean isSelected) {
            super(book, icon, x, y, name, action, tooltip, isSelected);
        }

        @Override
        protected void renderButton(@Nonnull GuiGraphics matrix, int mouseX, int mouseY, float partialTicks, boolean hovered) {
            //screen.bindLeftTexture(); //TODO: Switch these to "sprites"
            int yPos = 32 * (isSelected ? 1 : hovered ? 2: 0);
            matrix.blit(screen.backgroundL, getX(), getY(), 0, yPos, width, height);
            icon.render(Minecraft.getInstance(), matrix, getX(), getY() + 8);
        }
    }
}
