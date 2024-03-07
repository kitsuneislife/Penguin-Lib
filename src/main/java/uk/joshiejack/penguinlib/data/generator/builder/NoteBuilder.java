package uk.joshiejack.penguinlib.data.generator.builder;

import com.google.common.collect.Lists;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import uk.joshiejack.penguinlib.data.PenguinRegistries;
import uk.joshiejack.penguinlib.util.icon.*;
import uk.joshiejack.penguinlib.world.note.Note;
import uk.joshiejack.penguinlib.world.note.type.NoteType;

import java.util.stream.Collectors;


public class NoteBuilder /*extends SimplePenguinBuilder<Note>*/ {

    private ResourceLocation category;
    private boolean isHidden;
    private boolean locked;
    private String type;
    private Icon icon = new ItemIcon(ItemStack.EMPTY);
    private CategoryBuilder categoryBuilder;
    public NoteBuilder() {
        //super(PenguinRegistries.NOTE_SERIALIZER.get());
    }

    public static NoteBuilder note() {
        return new NoteBuilder();
    }

    public static NoteBuilder note(CategoryBuilder builder) {
        NoteBuilder note = note();
        note.categoryBuilder = builder;
        return note;
    }

    public CategoryBuilder end() {
        return categoryBuilder;
    }

    public NoteBuilder withCategory(ResourceLocation id) {
        category = id;
        return this;
    }

    public NoteBuilder setNoteType(String type) {
        this.type = type;
        return this;
    }

    public NoteBuilder setNoteType(NoteType type) {
        return setNoteType(type.toString());
    }

    public NoteBuilder setHidden() {
        isHidden = true;
        return this;
    }

    public NoteBuilder setLockedByDefault() {
        locked = true;
        return this;
    }

    public NoteBuilder withItemIcon(ItemLike item) {
        icon = new ItemIcon(new ItemStack(item));
        return this;
    }

    public NoteBuilder withTextureIcon(ResourceLocation texture, int x, int y) {
        icon = new TextureIcon(texture, x, y, 1);
        return this;
    }

    public NoteBuilder withPenguinIcon(int x, int y) {
        icon = new TextureIcon(Icon.DEFAULT_LOCATION, x, y, 1);
        return this;
    }

    public NoteBuilder withEntityIcon(EntityType<?> type, int scale) {
        icon = new EntityIcon(Holder.direct(type), 1, scale);
        return this;
    }

    public NoteBuilder withNoteIcon() {
        icon = new TextureIcon(Icon.DEFAULT_LOCATION, 0, 0, 1);
        return this;
    }

    public NoteBuilder withTagIcon(TagKey<Item> tag) {
        icon = new TagIcon(tag, 1);
        return this;
    }

    public NoteBuilder withListIcon(Item... items) {
        icon = new ListIcon(Lists.newArrayList(items).stream().map(item -> new ItemIcon(new ItemStack(item))).collect(Collectors.toList()), 1);
        return this;
    }

//    @Override
//    public void serializeRecipeData(@Nonnull JsonObject json) {
//        if (isHidden) json.addProperty("hidden", true);
//        if (locked) json.addProperty("locked", true);
//        if (!Strings.isNullOrEmpty(type)) json.addProperty("note type", type);
//        json.addProperty("category", category.toString());
//        json.add("icon", icon.toJson(new JsonObject()));
//    }

    public Note toNote() {
        return new Note(category, PenguinRegistries.NOTE_TYPES.getOrDefault(type, NoteType.TEXT));
    }
}
