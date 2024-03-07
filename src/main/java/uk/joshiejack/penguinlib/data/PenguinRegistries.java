package uk.joshiejack.penguinlib.data;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.icon.*;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.penguinlib.world.note.Category;
import uk.joshiejack.penguinlib.world.note.Note;
import uk.joshiejack.penguinlib.world.note.type.NoteType;

import java.util.Map;

public class PenguinRegistries {
    private static final ResourceLocation NONE = new ResourceLocation(PenguinLib.MODID, "none");
    public static final Map<String, NoteType> NOTE_TYPES = new Object2ObjectOpenHashMap<>();
    public static final ReloadableRegistry<Category> CATEGORIES = new ReloadableRegistry<>(PenguinLib.MODID, "categories", Category.CODEC, new Category(), true).withPriority(EventPriority.HIGHEST);
    public static final ReloadableRegistry<Note> NOTES = new ReloadableRegistry<>(PenguinLib.MODID, "notes", Note.CODEC, new Note(NONE, NoteType.TEXT), true);

    public static class Icons {
        public static final DeferredRegister<Codec<? extends Icon>> ICON_TYPES = DeferredRegister.create(ResourceKey.createRegistryKey(new ResourceLocation(PenguinLib.MODID, "icons")), PenguinLib.MODID);
        public static final Registry<Codec<? extends Icon>> ICON = ICON_TYPES.makeRegistry(b -> b.sync(true));
        public static final Holder<Codec<? extends Icon>> SPRITE = ICON_TYPES.register("sprite", () -> SpriteIcon.CODEC);
        public static final Holder<Codec<? extends Icon>> LIST = ICON_TYPES.register("list", () -> ListIcon.CODEC);
        public static final Holder<Codec<? extends Icon>> TAG_KEY = ICON_TYPES.register("tag", () -> TagIcon.CODEC);
        public static final Holder<Codec<? extends Icon>> ENTITY = ICON_TYPES.register("entity", () -> EntityIcon.CODEC);
        public static final Holder<Codec<? extends Icon>> ITEM = ICON_TYPES.register("item", () -> ItemIcon.CODEC);
    }

    public static void register(IEventBus eventBus) {
        Icons.ICON_TYPES.register(eventBus);
    }
}