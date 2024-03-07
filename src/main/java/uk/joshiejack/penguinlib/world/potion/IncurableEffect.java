package uk.joshiejack.penguinlib.world.potion;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;

public class IncurableEffect extends PenguinEffect {
    public IncurableEffect(MobEffectCategory type, int color) {
        super(type, color);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {}
}
