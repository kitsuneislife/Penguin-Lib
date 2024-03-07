package uk.joshiejack.penguinlib.world.item;

import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import uk.joshiejack.penguinlib.util.PenguinTags;

public class SickleItem extends DiggerItem {
    public SickleItem(Tier tier, Properties properties) {
        super(2, -2.6F, tier, PenguinTags.MINEABLE_SICKLE, properties);
    }
}
