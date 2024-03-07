package uk.joshiejack.penguinlib.world.block.entity.machine;

import net.minecraft.world.item.ItemStack;

public interface DoubleMachine {
    ItemStack getStack();
    boolean isActive();
}
