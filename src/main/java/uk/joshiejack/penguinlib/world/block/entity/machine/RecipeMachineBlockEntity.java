package uk.joshiejack.penguinlib.world.block.entity.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.network.packet.SetInventorySlotPacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public abstract class RecipeMachineBlockEntity<I extends Recipe<Container>> extends SimpleMachineBlockEntity {
    protected final RecipeType<I> recipeType;

    public RecipeMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, RecipeType<I> recipeType) {
        this(type, pos, state, recipeType, Objects.requireNonNull(BuiltInRegistries.RECIPE_TYPE.getKey(recipeType)).toString());
    }

    public RecipeMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, RecipeType<I> recipeType, String time) {
        super(type, pos, state, time);
        this.recipeType = recipeType;
    }

    @Override
    public int getMaxStackSize() {
        return items.get(0).isEmpty() ? 1 : items.get(0).getMaxStackSize();
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    protected I getRecipeResult(ItemStack stack) {
        for (RecipeHolder<I> recipe : level.getRecipeManager().getAllRecipesFor(recipeType)) {
            if (recipe.value().getIngredients().stream().allMatch(ing -> ing.test(stack)))
                return recipe.value();
        }

        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    protected ItemStack getResult(ItemStack stack) {
        Recipe<?> recipe = getRecipeResult(stack);
        return recipe == null ? ItemStack.EMPTY : recipe.getResultItem(level.registryAccess());
    }

    @Override
    public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) {
        return items.get(slot).isEmpty() && !getResult(stack).isEmpty();
    }

    @Override
    protected boolean canStart() {
        return !getResult(items.get(0)).isEmpty();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void finishMachine() {
        ItemStack result = Objects.requireNonNull(getRecipeResult(items.get(0))).assemble(this, level.registryAccess());
        items.set(0, result); //Hell yeah!
        PenguinNetwork.sendToNearby(this, new SetInventorySlotPacket(worldPosition, 0, result));
        setChanged();
    }

}