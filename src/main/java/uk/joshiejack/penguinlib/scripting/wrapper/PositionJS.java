package uk.joshiejack.penguinlib.scripting.wrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class PositionJS extends AbstractJS<BlockPos> {
    public PositionJS(BlockPos pos) {
        super(pos);
    }

    public PositionJS offset(int x, int y, int z) {
        return WrapperRegistry.wrap(penguinScriptingObject.offset(x, y, z));
    }

    public PositionJS offset(Direction facing, int amount) {
        return WrapperRegistry.wrap(penguinScriptingObject.relative(facing, amount));
    }

    public PositionJS north() {
        return WrapperRegistry.wrap(penguinScriptingObject.north());
    }

    public PositionJS east() {
        return WrapperRegistry.wrap(penguinScriptingObject.east());
    }

    public PositionJS south() {
        return WrapperRegistry.wrap(penguinScriptingObject.south());
    }

    public PositionJS west() {
        return WrapperRegistry.wrap(penguinScriptingObject.west());
    }

    public PositionJS up() {
        return WrapperRegistry.wrap(penguinScriptingObject.above());
    }

    public PositionJS down() {
        return WrapperRegistry.wrap(penguinScriptingObject.below());
    }

    public int x() {
        return penguinScriptingObject.getX();
    }

    public int y() {
        return penguinScriptingObject.getY();
    }

    public int z() {
        return penguinScriptingObject.getZ();
    }
}
