package uk.joshiejack.penguinlib.util.helper;

import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class InventoryHelper {
    public static final Function<ItemStack, Boolean> ANY = (stack) -> true;

    public static boolean isHolding(Player player, Function<ItemStack, Boolean> search, int count) {
        return getCountHeld(player, search) >= count;
    }

    private static int getCountHeld(Player player, Function<ItemStack, Boolean> search) {
        if (search.apply(player.getMainHandItem())) {
            return player.getMainHandItem().getCount();
        } else return 0;
    }

    public static boolean hasInInventory(Player player, ItemStack stack, int count) {
        return hasInInventory(player, (stack2) -> ItemStack.isSameItem(stack, stack2), count);
    }

    public static boolean hasInInventory(Player player, Function<ItemStack, Boolean> search, int count) {
        return getCount(player, search) >= count;
    }

    public static boolean hasInInventory(IItemHandler handler, ItemStack stack, int count) {
        return getCount(handler, (stack2) -> ItemStack.isSameItem(stack, stack2)) >= count;
    }

    public static boolean hasInInventory(IItemHandler handler, Function<ItemStack, Boolean> search, int count) {
        return getCount(handler, search) >= count;
    }

    private static int getCount(IItemHandler handler, Function<ItemStack, Boolean> search) {
        int count = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty() && search.apply(stack)) count += stack.getCount();
        }

        return count;
    }

    public static int getCount(Player player, Function<ItemStack, Boolean> search) {
        int count = 0;
        for (ItemStack item: player.getInventory().items) {
            if (item.isEmpty()) continue;
            if (search.apply(item)) {
                count += item.getCount();
            }
        }

        ItemStack offhand = player.getInventory().offhand.get(0);
        if (!offhand.isEmpty() && search.apply(offhand)) {
            count += offhand.getCount();
        }

        return count;
    }

    public static int takeItemsInInventory(Player player, Function<ItemStack, Boolean> search, int count) {
        //Take from the mainhand first before anything else
        if (search.apply(player.getMainHandItem())) {
            count -= reduceHeld(player, InteractionHand.MAIN_HAND, count);
        }

        return takeItems(player, search, count);
    }

    private static int reduceHeld(Player player, InteractionHand hand, int amount) {
        ItemStack held = player.getItemInHand(hand);
        if (held.getCount() <= amount) {
            int ret = held.getCount();
            player.setItemInHand(hand, ItemStack.EMPTY);
            return ret;
        } else {
            held.shrink(amount);
            return amount;
        }
    }

    private static int takeItems(Player player, Function<ItemStack, Boolean> search, int amount) {
        int toTake = amount;
        ItemStack offhand = player.getInventory().offhand.get(0);
        if (!offhand.isEmpty() && search.apply(offhand)) {
            ItemStack taken = offhand.split(toTake);
            toTake -= taken.getCount();
            if (offhand.getCount() <= 0) player.getInventory().offhand.set(0, ItemStack.EMPTY);
            if (toTake <= 0) {
                return 0; //No further processing neccessary
            }
        }

        //Main Inventory
        for (int i = 0; i < player.getInventory().items.size() && toTake > 0; i++) {
            ItemStack stack = player.getInventory().items.get(i);
            if (!stack.isEmpty() && search.apply(stack)) {
                ItemStack taken = stack.split(toTake);
                toTake -= taken.getCount();
                if (stack.getCount() <= 0) player.getInventory().items.set(i, ItemStack.EMPTY); //Clear
                if (toTake <= 0) return 0; //No further processing neccessary
            }
        }

        return toTake;
    }

    public static NonNullList<ItemStack> getAllStacks(List<IItemHandlerModifiable> inventories) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        for (IItemHandlerModifiable handler: inventories) {
            for (int i = 0 ; i < handler.getSlots(); i++) {
                ItemStack item = handler.getStackInSlot(i);
                if (!item.isEmpty()) {
                    stacks.add(item);
                }
            }
        }

        return stacks;
    }

    public static List<FluidStack> getAllFluids(List<IItemHandlerModifiable> inventories) {
        List<FluidStack> list = Lists.newArrayList();
        for (IItemHandlerModifiable handler: inventories) {
            for (int i = 0 ; i < handler.getSlots(); i++) {
                ItemStack item = handler.getStackInSlot(i);
                if (!item.isEmpty()) {
                    Optional<FluidStack> stack = FluidUtil.getFluidContained(item);
                    stack.ifPresent(list::add);
                }
            }
        }

        return list;
    }
}
