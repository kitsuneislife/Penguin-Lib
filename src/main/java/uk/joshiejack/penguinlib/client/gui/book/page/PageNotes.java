package uk.joshiejack.penguinlib.client.gui.book.page;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.client.gui.book.widget.ArrowButton;
import uk.joshiejack.penguinlib.client.gui.book.widget.NoteButton;
import uk.joshiejack.penguinlib.client.gui.book.widget.NoteWidget;
import uk.joshiejack.penguinlib.data.PenguinRegistries;
import uk.joshiejack.penguinlib.util.icon.Icon;
import uk.joshiejack.penguinlib.world.note.Category;
import uk.joshiejack.penguinlib.world.note.Note;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PageNotes extends AbstractMultiPage.Left<Note> {
    private final Category category;
    private final Predicate<Note> filter = (n) -> true;
    public NoteWidget note;

    public PageNotes(Category category) {
        super(category.getTitle(), 56);
        this.category = category;
    }

    @Override
    protected Icon getIcon() {
        return category.getIcon();
    }

    @Override
    public void initRight(Book book, int left, int top) {
        note = new NoteWidget(left + 18, top, 130, 20, note);
        book.addRenderableOnly(note);
        if (note.getNote() != null) {
            if (note.getChatter().getPage() < note.getChatter().getMaxPage())
                book.addRenderableWidget(new ArrowButton.Right(book, left + 130, top + 154, (button) -> {
                    note.getChatter().mouseClicked(0);
                    note.getChatter().update(book.minecraft().font);
                    book.markChanged();
                }));

            if (note.getChatter().getPage() > 0)
                book.addRenderableWidget(new ArrowButton.Left(book, left + 20, top + 154, (button) -> {
                    note.getChatter().mouseClicked(1);
                    note.getChatter().update(book.minecraft().font);
                    book.markChanged();
                }));
        }
    }

    @Override
    protected void initEntry(Book book, int left, int top, int id, Note note) {
        book.addRenderableWidget(new NoteButton(book, this.note, note, left + 18 + ((id % 7) * 18), top + 8 + ((id / 7) * 18),
                (button) -> {
                    if (note.isDefault() || note.isUnlocked(Minecraft.getInstance().player)) {
                        this.note.set(note, null); //Set the page to this and mark as read

                        book.markChanged(); //Reinit the book
                        note.read(Minecraft.getInstance().player);
                    }
                },
                note.isDefault() || note.isUnlocked(Minecraft.getInstance().player) ? Tooltip.create(note.getTitle()) : null));
    }

    @Override
    protected List<Note> getEntries() {
        Player player = Minecraft.getInstance().player;
        assert player != null;
        return PenguinRegistries.NOTES.stream()
                .filter(n -> n.getCategory().equals(PenguinRegistries.CATEGORIES.getID(category)))
                .filter(this.filter)
                .filter(n -> !n.isHidden() || n.isUnlocked(player) || n.isDefault())
                .collect(Collectors.toList());
    }
}
