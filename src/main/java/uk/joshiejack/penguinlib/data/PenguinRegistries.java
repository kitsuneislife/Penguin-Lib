package uk.joshiejack.penguinlib.data;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
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

    public static void register(IEventBus eventBus) {
        // Registration moved to specific icon codec classes or removed entirely
        // No custom registry needed for icon types
    }
}