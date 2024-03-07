package uk.joshiejack.penguinlib.data.generator;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.world.item.PenguinItems;

public class PenguinItemModels extends ItemModelProvider {
    public PenguinItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, PenguinLib.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        PenguinItems.ITEMS.getEntries().stream()
                .map(DeferredHolder::get)
                .forEach(item -> {
                    String path = BuiltInRegistries.ITEM.getKey(item).getPath();
                    singleTexture(path, mcLoc("item/generated"), "layer0", modLoc("item/" + path.replace("_treat", "")));
                });
    }
}
