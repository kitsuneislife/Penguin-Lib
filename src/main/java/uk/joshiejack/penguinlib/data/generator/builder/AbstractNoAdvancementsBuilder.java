package uk.joshiejack.penguinlib.data.generator.builder;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.RecipeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractNoAdvancementsBuilder implements RecipeBuilder {
    @Override
    public @NotNull RecipeBuilder unlockedBy(@NotNull String name, @NotNull CriterionTriggerInstance criteria) {
        return this;
    }

    @Override
    public @NotNull RecipeBuilder group(@Nullable String name) {
        return this;
    }
}
