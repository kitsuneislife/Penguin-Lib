package uk.joshiejack.penguinlib.scripting.wrapper;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import uk.joshiejack.penguinlib.util.helper.RegistryHelper;

public class BiomeJS extends AbstractJS<Biome> {
    public BiomeJS(Biome biome) {
        super(biome);
    }

    public boolean snows(PositionJS position) {
        return get().coldEnoughToSnow(position.get());
    }

    public boolean is (String name) {
        Registry<Biome> registry = RegistryHelper.registryAccess().registryOrThrow(Registries.BIOME);
        ResourceLocation key = registry.getKey(penguinScriptingObject);
        return key != null && key.toString().equals(name);
    }

    public boolean isTag(String type) {
        return Holder.direct(penguinScriptingObject).is(TagKey.create(Registries.BIOME, new ResourceLocation(type)));
    }
}