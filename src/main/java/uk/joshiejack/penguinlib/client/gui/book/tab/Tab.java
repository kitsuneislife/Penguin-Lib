package uk.joshiejack.penguinlib.client.gui.book.tab;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.client.gui.book.page.AbstractPage;
import uk.joshiejack.penguinlib.client.gui.book.widget.TabButton;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.util.icon.ItemIcon;

import java.util.ArrayList;
import java.util.List;

public class Tab {
    public static final Component EMPTY_STRING = Component.empty();
    public static final Tab EMPTY = new Tab(EMPTY_STRING, ItemIcon.EMPTY);
    private final List<AbstractPage> pages = new ArrayList<>();
    private final Component name;
    private final Icon icon;
    private AbstractPage defaultPage = AbstractPage.Basic.EMPTY;
    protected AbstractPage page;

    public Tab(Component name, Icon icon) {
        this.name = name;
        this.icon = icon;
    }

    protected List<AbstractPage> getPages() {
        return pages;
    }

    public Tab withPage(AbstractPage page) {
        if (!pages.contains(page))
            pages.add(page);
        if (defaultPage == AbstractPage.Basic.EMPTY)
            defaultPage = page;
        return this;
    }

    protected Icon getIcon() {
        return icon;
    }

    public AbstractPage getPage() {
        if (page == null)
            page = defaultPage;
        return page;
    }

    public void setPage(AbstractPage page) {
        this.page = page;
    }

//    protected Tooltip createTooltip(Book book, Component tooltip) {
//        return (btn, mtx, mX, mY) -> {
//            book.renderTooltip(mtx,
//                    book.minecraft().font.split(tooltip, Math.max(book.width / 2 - 43, 170)), mX, mY);
//        };
//    }

    public AbstractButton create(Book book, int x, int y) {
        //Creates the tab for this page
        return new TabButton.Left(book, getIcon(), x, y, name, (btn) -> {
            if (page == null)
                page = defaultPage;
            book.setTab(this); //Refresh
        }, Tooltip.create(name), book.isSelected(this));
    }

    public void addTabs(Book screen, int x, int y) {
        List<AbstractPage> pages = getPages();
        if (pages.size() > 1) {
            int i = 0;
            for (AbstractPage page : pages)
                screen.addRenderableWidget(page.createTab(screen, this, x, y + (i++ * 36)));
        }
    }
}
