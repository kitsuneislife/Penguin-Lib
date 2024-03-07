package uk.joshiejack.penguinlib.client.gui.book.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.client.gui.book.page.AbstractPage;
import uk.joshiejack.penguinlib.client.gui.book.page.PageNotes;
import uk.joshiejack.penguinlib.data.PenguinRegistries;
import uk.joshiejack.penguinlib.util.icon.Icon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotesTab extends Tab {
    private final List<ResourceLocation> valid = new ArrayList<>();

    public NotesTab(Component name, Icon icon) {
        super(name, icon);
    }

    public NotesTab withCategory(ResourceLocation resource) {
        valid.add(resource);
        return this;
    }

    //Use the list to get the default page instead
    @Override
    public AbstractPage getPage() {
        if (this.page == null || page == AbstractPage.Basic.EMPTY)
            this.page = getPages().get(0);
        return this.page;
    }

    @Override
    protected List<AbstractPage> getPages() {
        assert Minecraft.getInstance().level != null;
        return PenguinRegistries.CATEGORIES.stream()
                .filter(c -> valid.isEmpty() || valid.contains(PenguinRegistries.CATEGORIES.getID(c)))
                .map(PageNotes::new)
                .collect(Collectors.toList());
    }
}
