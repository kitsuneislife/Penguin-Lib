package uk.joshiejack.penguinlib.util.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;

public class BlockPosHelper {
    public static BlockPos getTransformedPosition(BlockPos target, BlockPos original, Rotation rotation) {
        BlockPos adjusted = transformBlockPos(target, rotation);
        return new BlockPos(original.getX() + adjusted.getX(), original.getY() + adjusted.getY(), original.getZ() + adjusted.getZ());
    }

    public static BlockPos transformBlockPos(BlockPos target, Rotation rotation) {
        int i = target.getX();
        int j = target.getY();
        int k = target.getZ();
        return switch (rotation) {
            case COUNTERCLOCKWISE_90 -> new BlockPos(k, j, -i);
            case CLOCKWISE_90 -> new BlockPos(-k, j, i);
            case CLOCKWISE_180 -> new BlockPos(-i, j, -k);
            default -> target;
        };
    }
}

