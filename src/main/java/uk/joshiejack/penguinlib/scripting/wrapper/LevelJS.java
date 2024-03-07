package uk.joshiejack.penguinlib.scripting.wrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.UUID;

public class LevelJS<W extends Level> extends AbstractJS<W> {
    public LevelJS(W world) {
        super(world);
    }

    //EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, int numberOfParticles, double xOffset, double yOffset, double zOffset, double particleSpeed, int... particleArguments

    public BiomeJS biome(PositionJS pos) {
        return WrapperRegistry.wrap(penguinScriptingObject.getBiome(pos.get()).value());
    }

    public void playSound(PositionJS pos, String sound, SoundSource category, double volume, double pitch) {
        penguinScriptingObject.playSound(null, pos.get(), Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(sound))), category, (float) volume, (float) pitch);
    }

    //TODO?
//    @SuppressWarnings("ConstantConditions")
//    public EntityJS<?> getEntity(String name, int x, int y, int z, int distance) {
//        Class <? extends Entity> clazz = EntityList.getClass(new ResourceLocation(name));
//        List<Entity> entity = penguinScriptingObject.getEntitiesWithinAABB(clazz, new AxisAlignedBB(x - 0.5F, y - 0.5F, z - 0.5F, x + 0.5F, y + 0.5F, z + 0.5F).expand(distance, distance, distance));
//        return !entity.isEmpty() ? WrapperRegistry.wrap(entity.get(0)) : null;
//    }

    public AbstractJS<?> createEntity(String name, double x, double y, double z) {
        Level object = penguinScriptingObject;
        net.minecraft.world.entity.Entity entity = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(name)).create(object);
        if (entity != null) {
            entity.setPos(x, y, z);
            object.addFreshEntity(entity);
            return WrapperRegistry.wrap(entity);
        } else return null;
    }

    public PlayerJS getPlayer(UUID uuid) {
        return WrapperRegistry.wrap(Objects.requireNonNull(penguinScriptingObject.getPlayerByUUID(uuid)));
    }

    public boolean isAir(PositionJS wrapper) {
        return penguinScriptingObject.getBlockState(wrapper.penguinScriptingObject).isAir();
    }

    public long time() {
        return penguinScriptingObject.getDayTime();
    }

    public void drop(PositionJS pos, ItemStackJS wrapper) {
        Block.popResource(penguinScriptingObject, pos.penguinScriptingObject, wrapper.penguinScriptingObject);
    }

    public String id() {
        return penguinScriptingObject.dimension().location().toString();
    }

    public boolean isClient() {
        return penguinScriptingObject.isClientSide;
    }

    public BlockStateJS getState(PositionJS position) {
        return WrapperRegistry.wrap(penguinScriptingObject.getBlockState(position.penguinScriptingObject));
    }

    public void setState(BlockStateJS block, PositionJS position) {
        Level world = penguinScriptingObject;
        BlockPos pos = position.penguinScriptingObject;
        BlockState state = block.penguinScriptingObject;
        world.setBlockAndUpdate(pos, state);
        //world.notifyNeighborsOfStateChange(pos, state.getBlock(), false);
    }
}
