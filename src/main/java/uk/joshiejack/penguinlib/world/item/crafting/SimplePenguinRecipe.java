package uk.joshiejack.penguinlib.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class SimplePenguinRecipe<R extends uk.joshiejack.penguinlib.world.item.crafting.SimplePenguinRecipe<?>> extends AbstractSimplePenguinRecipe<R, uk.joshiejack.penguinlib.world.item.crafting.SimplePenguinRecipe.Serializer<R>, ItemStack> {
    public SimplePenguinRecipe(DeferredHolder<RecipeType<?>, RecipeType<R>> recipeType, DeferredHolder<RecipeSerializer<?>, Serializer<R>> recipeSerializer, Ingredient ingredient, ItemStack output) {
        super(recipeType, recipeSerializer, ingredient, output);
    }

    public static class Serializer<T extends uk.joshiejack.penguinlib.world.item.crafting.SimplePenguinRecipe<?>> implements RecipeSerializer<T> {
        public static <T extends uk.joshiejack.penguinlib.world.item.crafting.SimplePenguinRecipe<?>> Codec<T> createCodec(Serializer.IRecipeFactory<T> factory) {
            return RecordCodecBuilder.create((instance) -> instance.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter((p_311729_) -> p_311729_.ingredient),
                    ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter((p_311730_) -> p_311730_.output)
            ).apply(instance, factory::create));
        }

        private final Serializer.IRecipeFactory<T> factory;
        private final Codec<T> codec;

        public Serializer(Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
            this.codec = createCodec(factory);
        }

        @Override
        public @NotNull Codec<T> codec() {
            return codec;
        }

        @Nonnull
        @Override
        public T fromNetwork(@Nonnull FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack itemstack = buffer.readItem();
            return factory.create(ingredient, itemstack);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull T recipe) {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.output);
        }

        public interface IRecipeFactory<T extends uk.joshiejack.penguinlib.world.item.crafting.SimplePenguinRecipe<?>> {
            T create(Ingredient input, ItemStack output);
        }
    }
}
