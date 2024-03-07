package uk.joshiejack.penguinlib.util.helper;

import com.google.common.collect.Streams;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class PlayerHelper {
    private static CompoundTag getOrCreateTag(CompoundTag base, String name) {
        if (!base.contains(name))
            base.put(name, new CompoundTag());
        return base.getCompound(name);
    }

    public static boolean hasTag(Player player, String compoundTag, String tag) {
        return getOrCreateTag(player.getPersistentData(), compoundTag).contains(tag);
    }

    public static void setTag(Player player, String compoundTag, String tag) {
        getOrCreateTag(player.getPersistentData(), compoundTag).putBoolean(tag, true);
    }

    public static void setSubTag(Player player, String main, String sub, String tag) {
        getOrCreateTag(getOrCreateTag(player.getPersistentData(), main), sub).putBoolean(tag, true);
    }

    public static boolean hasSubTag(Player player, String main, String sub, String tag) {
        return getOrCreateTag(getOrCreateTag(player.getPersistentData(), main), sub).contains(tag);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static Stream<ItemStack> getInventoryStream(Player player) {
        return Streams.concat(player.getInventory().items.stream(), player.getInventory().armor.stream(), player.getInventory().offhand.stream());
    }

    public static boolean hasInInventory(Player player, Item item, int amount) {
        return hasInInventory(player, (stack) -> stack.getItem() == item, amount);
    }

    public static boolean hasInInventory(Player player, TagKey<Item> tag, int amount) {
        return hasInInventory(player, (stack) -> stack.is(tag), amount);
    }

    public static boolean hasInInventory(Player player, Predicate<ItemStack> predicate, int amount) {
        return getInventoryStream(player).mapToInt(stack -> predicate.test(stack) ? stack.getCount() : 0).sum() >= amount;
    }

    public static boolean takeFromInventory(Player player, Item item, int amount) {
        return takeFromInventory(player, (stack) -> stack.getItem() == item, amount);
    }

    public static boolean takeFromInventory(Player player, TagKey<Item> tag, int amount) {
        return takeFromInventory(player, (stack) -> stack.is(tag), amount);
    }

    public static boolean takeFromInventory(Player player, Predicate<ItemStack> predicate, int amount) {
        MutableInt taken = new MutableInt(amount);
        return getInventoryStream(player).anyMatch(stack -> {
            if (predicate.test(stack)) {
                int take = Math.min(stack.getCount(), taken.intValue());
                stack.shrink(take);
                taken.subtract(take);
            }

            return taken.intValue() == 0;
        });
    }

    public static CompoundTag getPenguinStatuses(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains("PenguinStatuses"))
            data.put("PenguinStatuses", new CompoundTag());
        return data.getCompound("PenguinStatuses");
    }

    public static Player getClient() {
        return Client.getPlayer();
    }

    public static class Client {
        public static Player getPlayer() {
            return Minecraft.getInstance().player;
        }
    }
}