package uk.joshiejack.penguinlib.client.gui.book.page;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.client.gui.book.tab.Tab;
import uk.joshiejack.penguinlib.client.gui.book.widget.TabButton;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractPage {
    public static final Component EMPTY_STRING = Component.empty();
    private final Component name;

    public AbstractPage(Component name) {
        this.name = name;
    }

    public AbstractButton createTab(Book book, Tab tab, int x, int y) {
        //Creates the tab for this page
        return new TabButton.Right(book, getIcon(), x, y, name, (btn) -> {
            tab.setPage(this);
            book.markChanged();
        }, Tooltip.create(name), book.isSelected(this));
    }

    protected abstract Icon getIcon();

    public abstract void initLeft(Book book, int left, int top);

    public abstract void initRight(Book book, int left, int top);

    @OnlyIn(Dist.CLIENT)
    public static class Basic extends AbstractPage {
        public static final AbstractPage EMPTY = new Basic(EMPTY_STRING);

        public Basic(Component name) {
            super(name);
        }

        @Override
        protected Icon getIcon() {
            return ItemIcon.EMPTY;
        }

        @Override
        public void initLeft(Book book, int left, int top) {
        }

        @Override
        public void initRight(Book book, int left, int top) {
        }
    }
}
