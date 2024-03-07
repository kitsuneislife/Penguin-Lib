package uk.joshiejack.penguinlib.world.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import javax.annotation.Nonnull;
import java.util.Locale;

import static uk.joshiejack.penguinlib.world.block.ConnectedFloorBlock.TextureStyle.*;

public class ConnectedFloorBlock extends Block {
    public static final EnumProperty<TextureStyle> NORTH_EAST = EnumProperty.create("ne", TextureStyle.class);
    public static final EnumProperty<TextureStyle> NORTH_WEST = EnumProperty.create("nw", TextureStyle.class);
    public static final EnumProperty<TextureStyle> SOUTH_EAST = EnumProperty.create("se", TextureStyle.class);
    public static final EnumProperty<TextureStyle> SOUTH_WEST = EnumProperty.create("sw", TextureStyle.class);
    public final FloorOverlay[] overlays;

    @SuppressWarnings("ConstantConditions")
    public ConnectedFloorBlock(Block.Properties properties, FloorOverlay... overlays) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(NORTH_EAST, OUTER)
                .setValue(NORTH_WEST, OUTER)
                .setValue(SOUTH_EAST, OUTER)
                .setValue(SOUTH_WEST, OUTER));
        this.overlays = overlays;
    }

    @Nonnull
    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST);
    }

    private TextureStyle getStateFromBoolean(boolean one, boolean two, boolean three) {
        if (one && !two && !three) return VERTICAL;
        if (!one && two && !three) return  HORIZONTAL;
        if (one && two && !three) return INNER;
        if (!one && two) return HORIZONTAL;
        if (one && !two) return VERTICAL;
        if (one) return BLANK;
        return OUTER;
    }
//
//    private boolean isSameBlock(IBlockAccess world, BlockPos pos) {
//        return world.getBlockState(pos).getBlock() == this;
//    }
//
//    @Nonnull
//    @Override
//    public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
//        return getActualState(state, world, pos);
//    }
//
//    @SuppressWarnings("deprecation, unchecked")
//    @Override
//    @Nonnull
//    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
//        boolean north = isSameBlock(world, pos.north());
//        boolean west = isSameBlock(world, pos.west());
//        boolean south = isSameBlock(world, pos.south());
//        boolean east = isSameBlock(world, pos.east());
//        boolean northEast = north && east && isSameBlock(world, pos.north().east());
//        boolean northWest = north && west && isSameBlock(world, pos.north().west());
//        boolean southEast = south && east && isSameBlock(world, pos.south().east());
//        boolean southWest = south && west && isSameBlock(world, pos.south().west());
//        TextureStyle ne = getStateFromBoolean(north, east, northEast);
//        TextureStyle nw = getStateFromBoolean(north, west, northWest);
//        TextureStyle se = getStateFromBoolean(south, east, southEast);
//        TextureStyle sw = getStateFromBoolean(south, west, southWest);
//        return state.withProperty(NORTH_EAST, ne).withProperty(NORTH_WEST, nw).withProperty(SOUTH_EAST, se).withProperty(SOUTH_WEST, sw);
//    }
//
//    @SuppressWarnings("ConstantConditions")
//    @SideOnly(Side.CLIENT)
//    @Override
//    public void registerModels(Item item) {
//        ModelLoader.setCustomStateMapper(this, new StateMapperFloor());
//        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(getRegistryName(), "ne=outer,nw=outer,se=outer,sw=outer"));
//    }

    public record FloorOverlay(ResourceLocation texture, int weight) {}

    public enum TextureStyle implements StringRepresentable {
        BLANK, INNER, VERTICAL, HORIZONTAL, OUTER;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }
}
