package uk.joshiejack.penguinlib.data.generator;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BannerPatternTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.world.item.PenguinItems;

import java.util.concurrent.CompletableFuture;

public final class PenguinBannerTags extends BannerPatternTagsProvider {
    public PenguinBannerTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, PenguinLib.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(PenguinItems.REQUIRES_PENGUIN_ITEM).add(PenguinItems.PENGUIN_PATTERN_KEY);
    }
}
