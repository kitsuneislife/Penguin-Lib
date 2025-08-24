package uk.joshiejack.penguinlib.data.generator;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.client.ClientResources;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PenguinSpriteSourceProvider extends SpriteSourceProvider {
    public PenguinSpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper, PenguinLib.MODID);
    }

    @Override
    protected void addSources() {
        atlas(SpriteSourceProvider.BLOCKS_ATLAS).addSource(new SingleFile(ClientResources.SPEECH_BUBBLE, Optional.empty()));
    }
}
