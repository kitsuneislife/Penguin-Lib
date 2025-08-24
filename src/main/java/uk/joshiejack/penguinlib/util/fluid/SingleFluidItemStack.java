package uk.joshiejack.penguinlib.util.fluid;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class SingleFluidItemStack extends FluidHandlerItemStack {
    private final Fluid fluid;

    public SingleFluidItemStack(ItemStack container, Fluid fluid, int capacity) {
        super(container, capacity);
        this.fluid = fluid;
    }

    public SingleFluidItemStack(Fluid fluid, int capacity) {
        super(ItemStack.EMPTY, capacity);
        this.fluid = fluid;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        return fluid.getFluid().equals(this.fluid);
    }
}