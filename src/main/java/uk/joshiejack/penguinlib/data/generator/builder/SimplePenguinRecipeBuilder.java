package uk.joshiejack.penguinlib.data.generator.builder;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.world.item.crafting.SimplePenguinRecipe;

public class SimplePenguinRecipeBuilder<R extends SimplePenguinRecipe<?>> extends SimplePenguinRegistryBuilder<R, ItemStack> {
    private final SimplePenguinRecipe.Serializer.IRecipeFactory<R> factory;

    public SimplePenguinRecipeBuilder(Ingredient ingredient, ItemStack output, RecipeSerializer<R> serializer, SimplePenguinRecipe.Serializer.IRecipeFactory<R> factory) {
        super(serializer, ingredient, output);
        this.factory = factory;
    }

    @Override
    public @NotNull Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceLocation resourceLocation) {
        recipeOutput.accept(resourceLocation, factory.create(ingredient, result), null);
    }
}