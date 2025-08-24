package uk.joshiejack.penguinlib.world.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import uk.joshiejack.penguinlib.PenguinLib;

public class PenguinItems {
    public static final ResourceKey<BannerPattern> PENGUIN_PATTERN_KEY = ResourceKey.create(Registries.BANNER_PATTERN, new ResourceLocation(PenguinLib.MODID, "penguin"));
    public static final TagKey<BannerPattern> REQUIRES_PENGUIN_ITEM = TagKey.create(Registries.BANNER_PATTERN, new ResourceLocation(PenguinLib.MODID, "requires_penguin_item"));
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PenguinLib.MODID);
    public static final DeferredRegister<BannerPattern> BANNER_PATTERNS = DeferredRegister.create(Registries.BANNER_PATTERN, PenguinLib.MODID);
    public static final RegistryObject<Item> DEEP_BOWL = ITEMS.register("deep_bowl", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GLASS = ITEMS.register("glass", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> JAM_JAR = ITEMS.register("jam_jar", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MUG = ITEMS.register("mug", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PICKLING_JAR = ITEMS.register("pickling_jar", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PLATE = ITEMS.register("plate", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> UNFIRED_MUG = ITEMS.register("unfired_mug", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> UNFIRED_PLATE = ITEMS.register("unfired_plate", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PENGUIN_BANNER_PATTERN = ITEMS.register("penguin_banner_pattern", () -> new BannerPatternItem(REQUIRES_PENGUIN_ITEM, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<BannerPattern> PENGUIN_PATTERN = BANNER_PATTERNS.register("penguin", () -> new BannerPattern("pgn"));

    public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
    BANNER_PATTERNS.register(eventBus);
    }
}
