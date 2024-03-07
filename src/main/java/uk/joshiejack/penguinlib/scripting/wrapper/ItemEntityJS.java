package uk.joshiejack.penguinlib.scripting.wrapper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class ItemEntityJS extends EntityJS<ItemEntity> {
    public ItemEntityJS(ItemEntity entity) {
        super(entity);
    }

    public ItemStackJS item() {
        return WrapperRegistry.wrap(penguinScriptingObject.getItem());
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public PlayerJS thrower() {
        ItemEntity object = penguinScriptingObject;
        Entity thrower = object.getOwner();
        if (thrower instanceof Player player) {
            return WrapperRegistry.wrap(player);
        } else return null;
    }
}
