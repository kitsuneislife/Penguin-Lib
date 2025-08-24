package uk.joshiejack.penguinlib.data.generator.builder;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
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
    public void save(Consumer<FinishedRecipe> recipeConsumer, @NotNull ResourceLocation resourceLocation) {
        recipeConsumer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject jsonObject) {}
            
            @Override
            public ResourceLocation getId() { return resourceLocation; }
            
            @Override
            public RecipeSerializer<?> getType() { return null; }
            
            @Override
            public JsonObject serializeAdvancement() { return null; }
            
            @Override
            public ResourceLocation getAdvancementId() { return null; }
        });
    }
}