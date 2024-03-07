package uk.joshiejack.penguinlib.util.helper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class FluidHelper {
    public static int getTankCapacityFromStack(ItemStack stack) {
        IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        return handler != null ? handler.getTankCapacity(0) : 0;
    }

    public static int getFluidInTankFromStack(ItemStack stack) {
        IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        return handler != null ? handler.getFluidInTank(0).getAmount() : 0;
    }

    public static boolean fillContainer(ItemStack stack, int maxWater) {
        IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        return handler != null && handler.fill(new FluidStack(Fluids.WATER, maxWater), IFluidHandler.FluidAction.EXECUTE) > 0;
    }

    public static void drainContainer(ItemStack stack, int amount) {
        IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler != null)
            handler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
    }
}
