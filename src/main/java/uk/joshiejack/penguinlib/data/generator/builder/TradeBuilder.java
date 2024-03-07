package uk.joshiejack.penguinlib.data.generator.builder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import uk.joshiejack.penguinlib.data.database.CSVUtils;
import uk.joshiejack.penguinlib.data.generator.AbstractDatabaseProvider;

public class TradeBuilder {
    private final VillagerProfession profession;
    private ItemLike input = Items.EMERALD;
    private final int tier;
    private int inputAmount = 1;
    private ItemLike output = Items.EMERALD;
    private int outputAmount = 1;
    private int maxTrades = 32;
    private int xp = 16;
    private float priceMultiplier = 2F;

    public TradeBuilder(VillagerProfession profession, int tier, Item output) {
        this.profession = profession;
        this.tier = tier;
        this.output = output;
    }

    public TradeBuilder setInput(Item item) {
        this.input = item;
        return this;
    }

    public TradeBuilder setInputAmount(int i) {
        this.inputAmount = i;
        return this;
    }

    public TradeBuilder setOutputAmount(int i) {
        this.outputAmount = i;
        return this;
    }

    public TradeBuilder setMaxTrades(int i) {
        this.maxTrades = i;
        return this;
    }

    public TradeBuilder setXP(int i) {
        this.xp = i;
        return this;
    }

    public TradeBuilder setPriceMultiplier(float mp) {
        this.priceMultiplier = mp;
        return this;
    }

    public void build(AbstractDatabaseProvider gen) {
        gen.addEntry("villager_trades", "Profession,Tier,Input Item,Input Amount,Output Item,Output Amount,Max Trades,Experience,Price Multiplier",
                CSVUtils.join(BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString(), tier, BuiltInRegistries.ITEM.getKey(input.asItem()).toString(), inputAmount,
                        BuiltInRegistries.ITEM.getKey(output.asItem()).toString(), outputAmount,
                        maxTrades, xp, priceMultiplier));
    }
}
