package uk.joshiejack.penguinlib.scripting.wrapper;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class EntityJS<E extends Entity> extends AbstractJS<E> {
    public EntityJS(E entity) {
        super(entity);
    }

    @SuppressWarnings("ConstantConditions")
    public boolean is(String name) {
        Entity object = penguinScriptingObject;
        if (name.equals("player")) return object instanceof Player;
        ResourceLocation entry = BuiltInRegistries.ENTITY_TYPE.getKey(object.getType());
        return entry != null && entry.equals(new ResourceLocation(name));
    }

    public String registry() {
        Entity object = penguinScriptingObject;
        ResourceLocation entry = BuiltInRegistries.ENTITY_TYPE.getKey(object.getType());
        return !entry.equals(BuiltInRegistries.ENTITY_TYPE.getDefaultKey()) ? entry.toString() : "none";
    }

    public DataJS data() {
        return WrapperRegistry.wrap(penguinScriptingObject.getEntityData());
    }

    public boolean isInsideOf(String material) {
        return penguinScriptingObject.isInFluidType(BuiltInRegistries.FLUID.get(new ResourceLocation(material)).getFluidType());
    }

    public int portalTimer() {
        return penguinScriptingObject.getPortalCooldown();
    }

    public int existed() {
        return penguinScriptingObject.tickCount;
    }

//    public void disableItemDrops() { //TODO: Maybe?
//        penguinScriptingObject.setDropItemsWhenDead(false);
//    }

    public void kill() {
        penguinScriptingObject.kill();
    }

    public String getUUID() {
        return penguinScriptingObject.getUUID().toString();
    }

    public String name() {
        return penguinScriptingObject.getName().getString();
    }

    public PositionJS pos() {
        return WrapperRegistry.wrap(penguinScriptingObject.blockPosition());
    }

    public LevelJS<?> level() {
        return WrapperRegistry.wrap(penguinScriptingObject.level());
    }

    public Direction facing() {
        return penguinScriptingObject.getDirection().getOpposite();
    }

    public double x() {
        return penguinScriptingObject.position().x;
    }

    public double y() {
        return penguinScriptingObject.position().y;
    }

    public double z() {
        return penguinScriptingObject.position().z;
    }
}
