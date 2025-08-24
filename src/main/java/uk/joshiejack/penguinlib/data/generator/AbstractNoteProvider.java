package uk.joshiejack.penguinlib.data.generator;

import com.google.common.collect.Maps;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.world.note.Category;
import uk.joshiejack.penguinlib.world.note.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractNoteProvider implements DataProvider {
    private final PackOutput.PathProvider categoryPathProvider;
    private final PackOutput.PathProvider notePathProvider;
    private final Map<ResourceLocation, Category> categories = Maps.newHashMap();
    private final Map<ResourceLocation, Note> notes = Maps.newHashMap();

    public AbstractNoteProvider(PackOutput output) {
        this.categoryPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, PenguinLib.CATEGORIES_FOLDER);
        this.notePathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, PenguinLib.NOTES_FOLDER);
    }

    @Override
    public @NotNull String getName() {
        return "notes";
    }

    @Override
    public @NotNull CompletableFuture<?> run(final @NotNull CachedOutput output) {
        final List<CompletableFuture<?>> list = new ArrayList<>();
        buildNotes(categories, notes);
        categories.forEach((key, category) -> list.add(DataProvider.saveStable(output, Category.CODEC.encodeStart(JsonOps.INSTANCE, category).getOrThrow(false, s -> {}), categoryPathProvider.json(key))));
        notes.forEach((key, note) ->
        {
            if (!categories.containsKey(note.getCategory()))
                throw new IllegalStateException("Note " + note.id() + " has an invalid category " + note.getCategory());
            list.add(DataProvider.saveStable(output, Note.CODEC.encodeStart(JsonOps.INSTANCE, note).getOrThrow(false, s -> {}), this.notePathProvider.json(key)));
        });


        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    protected abstract void buildNotes(Map<ResourceLocation, Category> categories, Map<ResourceLocation, Note> notes);
}
