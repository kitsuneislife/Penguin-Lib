package uk.joshiejack.penguinlib.data.generator;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import javax.annotation.Nullable;

public class DatapackUtils {
    public static Feature createKey(ResourceLocation resourceLocation) {
        return new Feature(ResourceKey.create(Registries.PLACED_FEATURE, resourceLocation), ResourceKey.create(Registries.CONFIGURED_FEATURE, resourceLocation));
    }

    public static Feature createKeyFromExisting(ResourceLocation resourceLocation, ResourceKey<ConfiguredFeature<?, ?>> key) {
        return new Feature(ResourceKey.create(Registries.PLACED_FEATURE, resourceLocation), key);
    }

    public record Feature(ResourceKey<PlacedFeature> feature, ResourceKey<ConfiguredFeature<?, ?>> configured) {}

    public static <FC extends FeatureConfiguration, F extends net.minecraft.world.level.levelgen.feature.Feature<FC>> void registerFeatures(@Nullable BootstapContext<PlacedFeature> ftrContext, @Nullable BootstapContext<ConfiguredFeature<?, ?>> cnfContext,
                                                                                                                                            Feature key, F feature, FC configuration, PlacementModifier... modifiers) {
        if (cnfContext != null)
            net.minecraft.data.worldgen.features.FeatureUtils.register(cnfContext, key.configured(), feature, configuration);
        if (ftrContext != null) {
            HolderGetter<ConfiguredFeature<?, ?>> ftr = ftrContext.lookup(Registries.CONFIGURED_FEATURE);
            PlacementUtils.register(ftrContext, key.feature(), ftr.getOrThrow(key.configured()), modifiers);
        }
    }
}