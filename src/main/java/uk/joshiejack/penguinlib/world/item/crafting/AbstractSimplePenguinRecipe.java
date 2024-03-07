package uk.joshiejack.penguinlib.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nonnull;

@SuppressWarnings("NullableProblems")
public class AbstractSimplePenguinRecipe<RT extends Recipe<?>, RS extends RecipeSerializer<?>, O> implements Recipe<Container> {
    public final Ingredient ingredient;
    public final O output;
    protected final DeferredHolder<RecipeType<?>, RecipeType<RT>> type;
    protected final DeferredHolder<RecipeSerializer<?>, RS> serializer;

    public AbstractSimplePenguinRecipe(DeferredHolder<RecipeType<?>, RecipeType<RT>> recipeType, DeferredHolder<RecipeSerializer<?>, RS> recipeSerializer, Ingredient ingredient, O output) {
        this.type = recipeType;
        this.serializer = recipeSerializer;
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean matches(Container inventory, @Nonnull Level world) {
        return ingredient.test(inventory.getItem(0));
    }

    @Override
    public ItemStack assemble(@Nonnull Container inventory, RegistryAccess registry) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registry) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializer.get();
    }

    @Override
    public RecipeType<?> getType() {
        return type.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }
}
