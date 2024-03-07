package uk.joshiejack.penguinlib.world.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public abstract class AbstractBookMenu extends AbstractContainerMenu {
    public AbstractBookMenu(MenuType<?> type, int windowID) {
        super(type, windowID);
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    @Override
    public @Nonnull ItemStack quickMoveStack(@Nonnull Player player, int id) {
        return ItemStack.EMPTY;
    }
}
