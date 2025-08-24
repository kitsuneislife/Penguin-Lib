package uk.joshiejack.penguinlib.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

@SuppressWarnings("NullableProblems")
public class AbstractSimplePenguinRecipe<RT extends Recipe<?>, RS extends RecipeSerializer<?>, O> implements Recipe<Container> {
    public final Ingredient ingredient;
    public final O output;
    protected final RecipeType<RT> type;
    protected final RS serializer;

    public AbstractSimplePenguinRecipe(RecipeType<RT> recipeType, RS recipeSerializer, Ingredient ingredient, O output) {
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
    public ResourceLocation getId() {
        return new ResourceLocation("penguinlib", "simple_recipe");  // Default ID for Forge 1.20.1
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registry) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    @Override
    public RecipeType<?> getType() {
        return type;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }
}
