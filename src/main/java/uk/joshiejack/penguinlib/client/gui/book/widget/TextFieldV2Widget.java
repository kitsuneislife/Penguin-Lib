package uk.joshiejack.penguinlib.client.gui.book.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class TextFieldV2Widget extends EditBox {
    public TextFieldV2Widget(Font pFont, int pX, int pY, int pWidth, int pHeight, @Nullable EditBox pEditBox, Component pMessage) {
        super(pFont, pX, pY, pWidth, pHeight, pEditBox, pMessage);
    }
}
