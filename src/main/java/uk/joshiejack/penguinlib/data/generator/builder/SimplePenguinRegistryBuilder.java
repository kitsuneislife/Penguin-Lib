package uk.joshiejack.penguinlib.data.generator.builder;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Consumer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("NullableProblems")
public abstract class SimplePenguinRegistryBuilder <T extends Recipe<Container>, R> extends AbstractNoAdvancementsBuilder {
    protected final Ingredient ingredient;
    protected final RecipeSerializer<T> serializer;
    protected final R result;
    private ResourceLocation id;

    public SimplePenguinRegistryBuilder(RecipeSerializer<T> serializer, Ingredient ingredient, R result) {
        this.serializer = serializer;
        this.ingredient = ingredient;
        this.result = result;
    }

    public static class ItemOutput<T extends Recipe<Container>> extends SimplePenguinRegistryBuilder<T, ItemStack> {
        protected final IRecipeFactory<T> factory;

        public ItemOutput(RecipeSerializer<T> serializer, IRecipeFactory<T> factory, Ingredient ingredient, ItemStack result) {
            super(serializer, ingredient, result);
            this.factory = factory;
        }

        @Override
        public net.minecraft.world.item.Item getResult() {
            return result.getItem();
        }

        public void save(@NotNull CachedOutput output, @NotNull ResourceLocation resource) {
            // For Forge 1.20.1, we need to adapt to the new data generation system
            // This needs to be handled through the proper recipe provider
        }

        public void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation id) {
            // Implementation required by RecipeBuilder interface
            FinishedRecipe recipe = new FinishedRecipe() {
                @Override
                public void serializeRecipeData(JsonObject json) {}
                
                @Override
                public ResourceLocation getId() { return id; }
                
                @Override
                public RecipeSerializer<?> getType() { return serializer; }
                
                @Override
                public JsonObject serializeAdvancement() { return null; }
                
                @Override
                public ResourceLocation getAdvancementId() { return null; }
            };
            finishedRecipeConsumer.accept(recipe);
        }

        public interface IRecipeFactory<T extends Recipe<?>> {
            T create(Ingredient input, ItemStack output);
        }
    }

    public static class EntityOutput<T extends Recipe<Container>> extends SimplePenguinRegistryBuilder<T, EntityType<?>> {
        protected final IRecipeFactory<T> factory;

        public EntityOutput(RecipeSerializer<T> serializer, IRecipeFactory<T> factory, Ingredient ingredient, EntityType<?> result) {
            super(serializer, ingredient, result);
            this.factory = factory;
        }

        @Override
        public net.minecraft.world.item.Item getResult() {
            return Items.AIR;
        }

        public void save(@NotNull CachedOutput output, @NotNull ResourceLocation resource) {
            // For Forge 1.20.1, we need to adapt to the new data generation system
            // This needs to be handled through the proper recipe provider
        }

        public void save(Consumer<FinishedRecipe> finishedRecipeConsumer, ResourceLocation id) {
            // Implementation required by RecipeBuilder interface
            FinishedRecipe recipe = new FinishedRecipe() {
                @Override
                public void serializeRecipeData(JsonObject json) {}
                
                @Override
                public ResourceLocation getId() { return id; }
                
                @Override
                public RecipeSerializer<?> getType() { return serializer; }
                
                @Override
                public JsonObject serializeAdvancement() { return null; }
                
                @Override
                public ResourceLocation getAdvancementId() { return null; }
            };
            finishedRecipeConsumer.accept(recipe);
        }

        public interface IRecipeFactory<T extends Recipe<?>> {
            T create(Ingredient input, EntityType<?> output);
        }
    }
}