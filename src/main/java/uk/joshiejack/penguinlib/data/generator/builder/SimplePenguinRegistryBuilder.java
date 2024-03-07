package uk.joshiejack.penguinlib.data.generator.builder;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("NullableProblems")
public abstract class SimplePenguinRegistryBuilder <T extends Recipe<Container>, R> extends AbstractNoAdvancementsBuilder {
    protected final Ingredient ingredient;
    protected final RecipeSerializer<T> type;
    protected final R result;
    private ResourceLocation id;

    public SimplePenguinRegistryBuilder(RecipeSerializer<T> serializer, Ingredient ingredient, R result) {
        this.type = serializer;
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

        @Override
        public void save(@NotNull RecipeOutput output, @NotNull ResourceLocation resource) {
            output.accept(resource, factory.create(ingredient, result), null);
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

        @Override
        public void save(@NotNull RecipeOutput output, @NotNull ResourceLocation resource) {
            output.accept(resource, factory.create(ingredient, result), null);
        }

        public interface IRecipeFactory<T extends Recipe<?>> {
            T create(Ingredient input, EntityType<?> output);
        }
    }
}