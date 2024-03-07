package uk.joshiejack.penguinlib.client.gui.book.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.client.gui.widget.AbstractButton;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public abstract class ArrowButton extends AbstractButton<Book> {
    public ArrowButton(Book book, int x, int y, Component name, Button.OnPress action) {
        //super(book, x, y, 15, 10, name, action, (btn, mtx, mX, mY) -> book.renderTooltip(mtx,
               // book.minecraft().font.split(name, Math.max(book.width / 2 - 43, 170)), mX, mY));
        super(book, x, y, 15, 10, name, action);
        setTooltip(Tooltip.create(name));
    }

    @Override
    public void onPress() {
        super.onPress();
        //screen.setEditingTextField(null);
    }

    public static class Left extends ArrowButton {
        public Left(Book book, int x, int y, Button.OnPress action) {
            super(book, x, y, Component.translatable("button." + PenguinLib.MODID + ".previous"), action);
        }

        @Override
        protected void renderButton(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, boolean hovered) {
            //book.bindLeftTexture(); //TODO: Switch these to "sprites"
            int yPos = 11 * (hovered ? 1: 0);
            graphics.blit(screen.backgroundL, getX(), getY(), 16, 235 + yPos, width, height);
        }
    }

    public static class Right extends ArrowButton {
        public Right(Book book, int x, int y, Button.OnPress action) {
            super(book, x, y, Component.translatable("button." + PenguinLib.MODID + ".next"), action);
        }

        @Override
        protected void renderButton(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, boolean hovered) {
            //book.bindLeftTexture(); //TODO: Switch these to "sprites"
            int yPos = 11 * (hovered ? 1: 0);
            graphics.blit(screen.backgroundL, getX(), getY(), 0, 235 + yPos, width, height);
        }
    }
}
