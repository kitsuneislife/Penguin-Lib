package uk.joshiejack.penguinlib.data.generator;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.PenguinTags;

import java.util.concurrent.CompletableFuture;

public class PenguinItemTags extends ItemTagsProvider {
    public PenguinItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> blockLookup, ExistingFileHelper existingFileHelper, String modid) {
        super(output, provider, blockLookup, modid, existingFileHelper);
    }

    public PenguinItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> blockLookup, ExistingFileHelper existingFileHelper) {
        super(output, provider, blockLookup, PenguinLib.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(PenguinTags.CLOCKS).add(Items.CLOCK);
        tag(PenguinTags.BREAD).add(Items.BREAD);
        tag(PenguinTags.RAW_FISHES).add(Items.COD, Items.SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH);
        tag(PenguinTags.CROPS_APPLE).add(Items.APPLE);
        tag(Tags.Items.CROPS).add(Items.APPLE);
        tag(PenguinTags.CROPS_PUMPKIN).add(Blocks.PUMPKIN.asItem());
        tag(PenguinTags.CROPS_MELON).add(Items.MELON_SLICE);
        tag(PenguinTags.FUNGI).add(Items.WARPED_FUNGUS, Items.CRIMSON_FUNGUS);
        tag(PenguinTags.HAMMERS);
        tag(PenguinTags.SICKLES);
        tag(PenguinTags.WATERING_CANS);
        tag(ItemTags.TOOLS).addTags(Tags.Items.SHEARS, PenguinTags.HAMMERS, PenguinTags.FISHING_RODS, PenguinTags.WATERING_CANS);
    }
}
