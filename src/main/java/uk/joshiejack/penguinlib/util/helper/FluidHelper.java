package uk.joshiejack.penguinlib.util.helper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class FluidHelper {
    public static int getTankCapacityFromStack(ItemStack stack) {
        LazyOptional<IFluidHandlerItem> capability = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        return capability.map(handler -> handler.getTankCapacity(0)).orElse(0);
    }

    public static int getFluidInTankFromStack(ItemStack stack) {
        LazyOptional<IFluidHandlerItem> capability = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        return capability.map(handler -> handler.getFluidInTank(0).getAmount()).orElse(0);
    }

    public static boolean fillContainer(ItemStack stack, int maxWater) {
        LazyOptional<IFluidHandlerItem> capability = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        return capability.map(handler -> handler.fill(new FluidStack(Fluids.WATER, maxWater), IFluidHandler.FluidAction.EXECUTE) > 0).orElse(false);
    }

    public static void drainContainer(ItemStack stack, int amount) {
        LazyOptional<IFluidHandlerItem> capability = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        capability.ifPresent(handler -> handler.drain(amount, IFluidHandler.FluidAction.EXECUTE));
    }
}
