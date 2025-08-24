package uk.joshiejack.penguinlib.world.item.crafting;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class SimplePenguinRecipe<R extends SimplePenguinRecipe<?>> extends AbstractSimplePenguinRecipe<R, SimplePenguinRecipe.Serializer<R>, ItemStack> {
    public SimplePenguinRecipe(RecipeType<R> recipeType, Serializer<R> recipeSerializer, Ingredient ingredient, ItemStack output) {
        super(recipeType, recipeSerializer, ingredient, output);
    }

    public static class Serializer<T extends SimplePenguinRecipe<?>> implements RecipeSerializer<T> {
        private final Serializer.IRecipeFactory<T> factory;

        public Serializer(Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        public Codec<T> codec() {
            // Simplified codec for Forge 1.20.1
            return null; // Will be implemented when codec system is needed
        }

        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
            ItemStack result = ShapedRecipe.itemStackFromJson(json.getAsJsonObject("result"));
            return factory.create(ingredient, result);
        }

        @Override
        public T fromNetwork(ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            return factory.create(ingredient, result);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.output);
        }

        public interface IRecipeFactory<T extends SimplePenguinRecipe<?>> {
            T create(Ingredient ingredient, ItemStack output);
        }
    }
}
