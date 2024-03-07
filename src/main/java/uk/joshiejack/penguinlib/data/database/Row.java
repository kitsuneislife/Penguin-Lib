package uk.joshiejack.penguinlib.data.database;

import com.google.common.collect.Maps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.Patterns;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("null")
public class Row {
    public static final Row EMPTY = new Row(Table.EMPTY, new String[0], new String[0]);
    private final Map<String, Object> data = Maps.newHashMap();
    private final Table table;

    Row(Table table, String[] labelset, String[] dataset) {
        try {
            for (int i = 0; i < labelset.length; i++) {
                set(labelset[i], dataset[i]);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            PenguinLib.LOGGER.error("Failed to set the values for the label set: " + Arrays.toString(labelset) +
                    " with the data " + Arrays.toString(dataset));
            throw (ex);
        }

        this.table = table;
    }

    public String get() {
        return (String) data.values().stream().findFirst().get();
    }


    @SuppressWarnings("unchecked")
    public <T> T get(String label) {
        if (!data.containsKey(label.toLowerCase(Locale.ENGLISH))) {
            PenguinLib.LOGGER.error("Failed to find the label: " + label + " in the row: " + this + " of the table: " + table.name());
            return null;
        }

        return (T) data.get(label.toLowerCase(Locale.ENGLISH));
    }

    public int getTime(String name) {
        String entry = get(name).toString();
        if (!entry.contains(":")) return -1;
        String[] split = get(name).toString().split(":");
        int hours = Integer.parseInt(split[0]);
        int minutes = Integer.parseInt(split[1]);
        int seconds = split.length > 2 ? Integer.parseInt(split[2]) : 0;
        int totalSeconds = (hours * 3600) + (minutes * 60) + seconds;
        return (int) (24000 / (86400D / totalSeconds));
    }

    public ResourceLocation getScript() {
        return new ResourceLocation(get("script").toString().replace("/", "_")); //Convert to namesake
    }

    public <E extends Enum<E>> E getAsEnum(Class<E> clazz) {
        return getAsEnum(clazz, clazz.getSimpleName().toLowerCase(Locale.ROOT));
    }

    private <E extends Enum<E>> E getAsEnum(Class<E> clazz, String field) {
        return Enum.valueOf(clazz, get(field).toString().toUpperCase(Locale.ROOT));
    }

    public String id() {
        return get("id");
    }

    @Deprecated
    public ItemStack icon() {
        //ItemStack icon = StackHelper.getStackFromString(get("icon"));
        //return icon.isEmpty() ? new ItemStack(Items.POTATO) : icon;
        return ItemStack.EMPTY; //TODO: re-enable item stacks where applicable
    }

    public EntityType<?> entity() {
        return entity("entity");
    }

    public EntityType<?> entity(String name) {
        return BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(get(name).toString()));
    }

    public ResourceLocation getRL(String name) {
        return new ResourceLocation(get(name));
    }

    public Block block() {
        return block("block");
    }

    public Block block(String name) {
        return BuiltInRegistries.BLOCK.get(new ResourceLocation(get(name).toString()));
    }

    public Item item() {
        return item("item");
    }

    public Item item(String name) {
        return BuiltInRegistries.ITEM.get(new ResourceLocation(get(name).toString()));
    }

    public float getAsFloat(String label) {
        return Float.parseFloat(get(label).toString());
    }

    public long getAsLong(String label) {
        return Long.parseLong(get(label).toString().trim());
    }

    public double getAsDouble(String label) {
        return Double.parseDouble(get(label).toString().trim());
    }

    public int getAsInt(String label) {
        return Integer.parseInt(get(label).toString());
    }

    public int getColor(String label) {
        return Integer.parseInt(get(label).toString().replace("0x", "").replace("#", ""), 16);
    }

    public boolean isEmpty(String label) {
        if (get(label) == null) return true;
        String name = get(label).toString();
        return name.isEmpty() || name.equals("none") || name.equals("default");
    }

    public MobEffect effect() {
        return effect("effect");
    }

    public MobEffect effect(String name) {
        return BuiltInRegistries.MOB_EFFECT.get(getRL(name));
    }

    public TagKey<Item> itemTag() {
        return itemTag("tag");
    }

    public TagKey<Item> itemTag(String name) {
        return ItemTags.create(getRL(name));
    }

    public TagKey<Block> blockTag() {
        return blockTag("tag");
    }

    public TagKey<Block> blockTag(String name) {
        return BlockTags.create(getRL(name));
    }

    @Override
    public String toString() {
        return Arrays.toString(data.values().toArray());
    }

    //Search the objects for a match
    public boolean contains(String match) {
        return data.values().stream().anyMatch(match::equals);
    }

    //Call to set the values, pulled in from the csvs
    public void set(String label, String value) {
        if (Patterns.BOOLEAN_PATTERN.matcher(value).matches()) data.put(label, Boolean.valueOf(value));
        else if (Patterns.DOUBLE_PATTERN.matcher(value).matches()) data.put(label, Double.valueOf(value));
        else if (Patterns.INTEGER_PATTERN.matcher(value).matches()) data.put(label, Integer.valueOf(value));
        else data.put(label, value);
    }

    public String name() {
        return get("name");
    }

    public boolean isTrue(String name) {
        Object ret = get(name);
        return ret instanceof Boolean ? (boolean) ret : Boolean.parseBoolean(ret.toString());
    }
}
