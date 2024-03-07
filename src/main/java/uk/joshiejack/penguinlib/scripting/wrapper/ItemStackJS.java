package uk.joshiejack.penguinlib.scripting.wrapper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class ItemStackJS extends AbstractJS<ItemStack> {
    public static final ItemStackJS EMPTY = new ItemStackJS(ItemStack.EMPTY);

    public ItemStackJS(ItemStack stack) {
        super(stack);
    }

    public boolean isEmpty() {
        return penguinScriptingObject.isEmpty();
    }

    public boolean is(ItemStackJS stack) {
        return ItemStack.isSameItem(penguinScriptingObject, stack.penguinScriptingObject);
    }

    @SuppressWarnings("ConstantConditions")
    public boolean is(String item) {
        return penguinScriptingObject.is(ItemTags.create(new ResourceLocation(item.substring(1))));
    }

    public DataJS data() {
        ItemStack object = penguinScriptingObject;
        CompoundTag tag = object.getOrCreateTag();
        return WrapperRegistry.wrap(Objects.requireNonNull(tag));
    }

    public ItemStackJS setCount(int count) {
        penguinScriptingObject.setCount(count);
        return this;
    }

    public String name() {
        return penguinScriptingObject.getDisplayName().getString();
    }

    public int count() {
        return penguinScriptingObject.getCount();
    }

    public void shrink(int amount) {
        penguinScriptingObject.shrink(amount);
    }

    public void grow(int amount) { penguinScriptingObject.grow(amount); }

    //TODO?

//    public ItemStackJS setStack(String item) {
//        CompoundTag tag = penguinScriptingObject.getOrCreateTag();
//        ItemStack internal = StackHelper.getStackFromString(item);
//        internal.writeToNBT(tag);
//        return this;
//    }
}
