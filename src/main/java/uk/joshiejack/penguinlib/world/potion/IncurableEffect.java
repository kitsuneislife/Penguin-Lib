package uk.joshiejack.penguinlib.world.potion;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
// Removed EffectCure import as it doesn't exist in Forge 1.20.1

import java.util.Set;

public class IncurableEffect extends PenguinEffect {
    public IncurableEffect(MobEffectCategory type, int color) {
        super(type, color);
    }

    // Removed fillEffectCures method as EffectCure doesn't exist in Forge 1.20.1
}
