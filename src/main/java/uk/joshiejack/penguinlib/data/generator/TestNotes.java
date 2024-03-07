package uk.joshiejack.penguinlib.data.generator;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.world.note.Category;
import uk.joshiejack.penguinlib.world.note.Note;

import java.util.Map;

public class TestNotes extends AbstractNoteProvider {
    public TestNotes(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildNotes(Map<ResourceLocation, Category> categories, Map<ResourceLocation, Note> notes) {
//        CategoryBuilder.category().withItemIcon(Items.BAMBOO_CHEST_RAFT)
//                .withNote("energy").withNoteIcon().end()
//                .withNote("fishing").withItemIcon(Items.FISHING_ROD).setNoteType("lifespan").end() //TODO: With the piscary fishing rod
//                .withNote("shovel").withItemIcon(Items.STONE_SHOVEL).end() //TODO: With the HF shovel
//                .withNote("hammer").withItemIcon(Items.HANGING_ROOTS).end()
//                .withNote("axe").withItemIcon(Items.BLACK_SHULKER_BOX).end()
//                .save(categories, notes, new ResourceLocation(PenguinLib.MODID, "activities"));
    }
}
