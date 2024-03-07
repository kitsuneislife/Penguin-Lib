package uk.joshiejack.penguinlib.data.generator;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.client.ClientResources;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PenguinSpriteSourceProvider extends SpriteSourceProvider {
    public PenguinSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
        super(output, lookupProvider, PenguinLib.MODID, fileHelper);
    }

    @Override
    protected void gather() {
        atlas(SpriteSourceProvider.BLOCKS_ATLAS).addSource(new SingleFile(ClientResources.SPEECH_BUBBLE, Optional.empty()));
    }
}
