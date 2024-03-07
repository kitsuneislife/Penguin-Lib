package uk.joshiejack.penguinlib.scripting.wrapper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import java.util.Objects;

public class LivingEntityJS<E extends LivingEntity> extends EntityJS<E> {
    public LivingEntityJS(E entity) {
        super(entity);
    }

    public void heal(int amount) {
        penguinScriptingObject.heal(amount);
    }

    public ItemStackJS getHeldItem(InteractionHand hand) {
        return WrapperRegistry.wrap(penguinScriptingObject.getItemInHand(hand));
    }

    public boolean isHolding(String name, InteractionHand hand) {
        return penguinScriptingObject.getItemInHand(hand).getItem() == BuiltInRegistries.ITEM.get(new ResourceLocation(name));
    }

    public boolean isWielding(String name) {
        LivingEntity object = penguinScriptingObject;
        TagKey<Item> tag = ItemTags.create(new ResourceLocation(name));
        return object.getMainHandItem().is(tag);
    }

    public boolean hasEffect(String name) {
        return penguinScriptingObject.hasEffect(Objects.requireNonNull(BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(name))));
    }

    public void addEffect(String name, int time, int modifier) {
        penguinScriptingObject.addEffect(new MobEffectInstance(Objects.requireNonNull(BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(name))), time, modifier));
    }

    public void removeEffect(String name) {
        penguinScriptingObject.removeEffect(Objects.requireNonNull(BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(name))));
    }

    public boolean isDamaged() {
        LivingEntity object = penguinScriptingObject;
        return object.getHealth() < object.getMaxHealth();
    }
}
