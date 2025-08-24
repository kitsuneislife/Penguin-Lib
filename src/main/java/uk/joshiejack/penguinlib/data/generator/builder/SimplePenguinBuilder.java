package uk.joshiejack.penguinlib.data.generator.builder;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("NullableProblems")
public abstract class SimplePenguinBuilder<T extends Recipe<Container>> implements RecipeBuilder {
    private final RecipeSerializer<T> type;
    protected ResourceLocation id;

    public SimplePenguinBuilder(RecipeSerializer<T> serializer) {
        this.type = serializer;
    }

    @Override
    public RecipeBuilder unlockedBy(String unlocked, CriterionTriggerInstance criteria) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public Item getResult() {
        return null;
    }

//    @Override
//    public ResourceLocation getId() {
//        return this.id;
//    }
//
//    @Override
//    public RecipeSerializer<?> getType() {
//        return this.type;
//    }
//
//    @Override
//    @Nullable
//    public JsonObject serializeAdvancement() {
//        return null;
//    }
//
//    @Override
//    @Nullable
//    public ResourceLocation getAdvancementId() {
//        return null;
//    }

//    @Override
//    public void save(RecipeOutput output, ResourceLocation resource) {
//        this.id = resource;
//        output.accept(resource, this, (AdvancementHolder) null);
//    }
}