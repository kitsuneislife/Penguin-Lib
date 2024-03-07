package uk.joshiejack.penguinlib.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import uk.joshiejack.penguinlib.PenguinLib;

public class PenguinTags {
    public static final TagKey<Item> BREAD = forgeItemTag("bread");
    public static final TagKey<Item> RAW_FISHES = forgeItemTag("raw_fishes");
    public static final TagKey<Item> CROPS_APPLE = forgeItemTag("crops/apple");
    public static final TagKey<Item> CROPS_PUMPKIN = forgeItemTag("crops/pumpkin");
    public static final TagKey<Item> CROPS_MELON = forgeItemTag("crops/melon");
    public static final TagKey<Item> FUNGI = forgeItemTag("fungi");
    //######################################### TOOLS ###########################################
    public static final TagKey<Item> TOOLS = forgeItemTag("tools");
    public static final TagKey<Item> HAMMERS = forgeItemTag("tools/hammers");
    public static final TagKey<Item> SICKLES = forgeItemTag("tools/sickles");
    public static final TagKey<Item> FISHING_RODS = forgeItemTag("tools/fishing_rods");
    public static final TagKey<Item> WATERING_CANS = forgeItemTag("tools/watering_cans");

    //######################################### Hammer AOE ######################################
    public static final TagKey<Block> SMASHABLE = penguinBlockTag("smashable");
    public static final TagKey<Block> MINEABLE_SICKLE = forgeBlockTag("mineable/sickle");
    public static final TagKey<Block> MINEABLE_HAMMER = forgeBlockTag("mineable/hammer");

    public static final TagKey<Item> CLOCKS = penguinItemTag("clocks");
    
    public static TagKey<Block> forgeBlockTag(String name) {
        return BlockTags.create(new ResourceLocation("forge", name));
    }

    public static TagKey<Item> forgeItemTag(String name) {
        return ItemTags.create(new ResourceLocation("forge", name));
    }

    public static TagKey<Block> penguinBlockTag(String name) {
        return BlockTags.create(new ResourceLocation(PenguinLib.MODID, name));
    }

    public static TagKey<Item> penguinItemTag(String name) {
        return ItemTags.create(new ResourceLocation(PenguinLib.MODID, name));
    }
}
