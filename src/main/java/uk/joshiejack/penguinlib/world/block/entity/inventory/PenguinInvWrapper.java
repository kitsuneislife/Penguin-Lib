package uk.joshiejack.penguinlib.world.block.entity.inventory;

import net.minecraftforge.items.wrapper.InvWrapper;

public class PenguinInvWrapper extends InvWrapper {
    private final InventoryBlockEntity tileEntity;

    public PenguinInvWrapper(InventoryBlockEntity inv) {
        super(inv);
        this.tileEntity = inv;
    }

    @Override
    public int getSlotLimit(int slot) {
        return tileEntity.getSlotLimit(slot);
    }
}
