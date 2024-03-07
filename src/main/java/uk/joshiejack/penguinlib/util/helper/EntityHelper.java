package uk.joshiejack.penguinlib.util.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class EntityHelper {
    public static BlockPos lookingAt(Entity entity, double distance) {
        HitResult raytrace = entity.pick(distance, 0F, false);
        return raytrace.getType() == HitResult.Type.BLOCK ? ((BlockHitResult) raytrace).getBlockPos() : null;
    }

    public static Rotation getRotationFromEntity(Entity entity) {
        Direction facing = entity.getDirection().getOpposite();
        return switch (facing) {
            case NORTH -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.COUNTERCLOCKWISE_90;
            case EAST -> Rotation.CLOCKWISE_180;
            default -> Rotation.NONE;
        };
    }
}
