package uk.joshiejack.penguinlib.scripting.wrapper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import uk.joshiejack.penguinlib.util.helper.InventoryHelper;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;

@SuppressWarnings("unused")
public class PlayerJS extends LivingEntityJS<Player> {
    public PlayerJS(Player player) {
        super(player);
    }

    public TeamJS team() {
        return WrapperRegistry.wrap(PenguinTeams.getTeamForPlayer(penguinScriptingObject));
    }

    public void give(ItemStackJS stack) {
        ItemHandlerHelper.giveItemToPlayer(penguinScriptingObject, stack.penguinScriptingObject);
    }

    public boolean has(String ore, int amount) {
        return InventoryHelper.hasInInventory(penguinScriptingObject, (stack) -> stack.is(ItemTags.create(new ResourceLocation(ore))), amount);
    }

    public boolean isHolding(String ore, int amount) {
        return InventoryHelper.isHolding(penguinScriptingObject, (stack) -> stack.is(ItemTags.create(new ResourceLocation(ore))), amount);
    }

    public long spawnday() {
        CompoundTag tag = penguinScriptingObject.getPersistentData();
        return tag.contains("Spawnday") ? tag.getLong("Spawnday") : -1;
    }

    public void setBirthday(long birthday) {
        penguinScriptingObject.getPersistentData().putLong("Birthday", birthday);
    }

    public String getUUID() {
        return penguinScriptingObject.getUUID().toString();
    }

    public PlayerStatusJS status() {
        return WrapperRegistry.wrap(this);
    }

    public boolean isHungry() {
        return penguinScriptingObject.getFoodData().needsFood();
    }

    public void feed(int hunger, float saturation) {
        penguinScriptingObject.getFoodData().eat(hunger, saturation);
    }
}
