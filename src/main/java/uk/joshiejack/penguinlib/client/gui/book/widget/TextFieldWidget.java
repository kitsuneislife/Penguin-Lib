//package uk.joshiejack.penguinlib.client.gui.book.widget;
//
//import net.minecraft.ChatFormatting;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.components.Button;
//import net.minecraft.network.chat.Component;
//import net.minecraft.util.Mth;
//import org.jetbrains.annotations.NotNull;
//import uk.joshiejack.penguinlib.client.gui.book.Book;
//import uk.joshiejack.penguinlib.client.gui.book.page.AbstractPage;
//import uk.joshiejack.penguinlib.client.gui.widget.PenguinButton;
//
//public abstract class TextFieldWidget< P extends AbstractPage> extends PenguinButton {
//    protected static final String BLACK_CURSOR = ChatFormatting.BLACK + "_";
//    protected static final String GRAY_CURSOR = ChatFormatting.GRAY + "_";
//    public static final int WIDTH = 102;
//    public static final int HEIGHT = 24;
//    private final int maxLength;
//    private final String id;
//    protected final Book book;
//    protected final P page; //Switch to abstract pave
//    protected final Book.BookTextField textFieldHelper;
//
//    public TextFieldWidget(Button.Builder builder, Book book, P page, String id, int maxLength) {
//        super(builder);
//        this.book = book;
//        this.page = page;
//        this.maxLength = maxLength;
//        this.id = id;
//        this.textFieldHelper = new Book.BookTextField(id(), book, this::getText, this::setText, (s) -> s.length() <= getMaxLength());
//    }
//
//    public int getMaxLength() {
//        return maxLength;
//    }
//
//    public String id() {
//        return id;
//    }
//
//    @Override
//    public @NotNull Component getMessage() {
//        String text = this.getText();
//        if (this.book.isEditing(this.textFieldHelper)) {
//            int cursor = this.book.getCursor();
//            boolean flag = this.book.frameTick / 6 % 2 == 0;
//            text = flag ? text.substring(0, cursor) + BLACK_CURSOR + ChatFormatting.RESET
//                    + text.substring(cursor) : text.substring(0, cursor) + GRAY_CURSOR + ChatFormatting.RESET + text.substring(cursor);
//        }
//
//        return Component.literal(text);
//    }
//
//    @Override
//    public void onPress() {
//        this.onPress.onPress(this);
//        this.setFocused(true);
//        book.setEditingTextField(textFieldHelper);
//    }
//
//    public abstract String getText();
//
//    public abstract void setText(String text);
//
//    @Override
//    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
//        this.renderString(pGuiGraphics, Minecraft.getInstance().font, this.getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24);
//    }
//}