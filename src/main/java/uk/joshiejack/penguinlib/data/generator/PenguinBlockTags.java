package uk.joshiejack.penguinlib.data.generator;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.PenguinTags;

import java.util.concurrent.CompletableFuture;

public final class PenguinBlockTags extends BlockTagsProvider {
    public PenguinBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, PenguinLib.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(PenguinTags.MINEABLE_SICKLE)
                .addTag(BlockTags.SWORD_EFFICIENT)
                .add(Blocks.CACTUS)
                .add(Blocks.SEAGRASS)
                .add(Blocks.TALL_SEAGRASS)
                .add(Blocks.KELP_PLANT)
                .addTag(BlockTags.CORAL_PLANTS);
    }
}
