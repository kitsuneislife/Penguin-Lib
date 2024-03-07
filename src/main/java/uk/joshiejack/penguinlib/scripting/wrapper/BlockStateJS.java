package uk.joshiejack.penguinlib.scripting.wrapper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.logging.log4j.util.Strings;

public class BlockStateJS extends AbstractJS<BlockState> {
    public BlockStateJS(BlockState state) {
        super(state);
    }

    public ItemStackJS item() {
        return new ItemStackJS(new ItemStack(get().getBlock().asItem()));
    }

    public boolean is(String state) {
        return false;

        //TODO: return penguinScriptingObject == StateAdapter.fromString(state);
    }

    //public boolean is(String propertyName, String value) {
        //TODO?
//        BlockState state = penguinScriptingObject;
//        Property<?> theProperty = state.getBlock().getStateDefinition().getProperty(propertyName);
//        if (theProperty != null) {
//            Optional<? extends Comparable<?>> optional = theProperty.parseValue(value);
//            if (optional.isPresent()) {
//                Comparable<?> targetProperty = optional.get();
//                Comparable<?> thisProperty = state.getValue(theProperty);
//                return targetProperty.equals(thisProperty);
//            } else return false;
//        } else return false;
    //}

    public String block() {
        return BuiltInRegistries.BLOCK.getKey(get().getBlock()).toString();
    }

    @SuppressWarnings("unchecked")
    public boolean isLeaves(LevelJS<?> worldW, PositionJS posW) {
        return get().is(BlockTags.LEAVES);
    }

    @SuppressWarnings("unchecked, rawtypes")
    public String property(String name) {
        BlockState object = penguinScriptingObject;
        Property property = object.getBlock().getStateDefinition().getProperty(name);
        return property != null ? property.getName(object.getValue(property)) : Strings.EMPTY;
    }
}
