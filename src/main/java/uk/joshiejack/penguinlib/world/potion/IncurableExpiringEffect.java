package uk.joshiejack.penguinlib.world.potion;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;
import java.util.function.Consumer;

public class IncurableExpiringEffect extends AbstractExpiringEffect {
    public IncurableExpiringEffect(MobEffectCategory type, int color, Consumer<ServerPlayer> onExpire) {
        super(type, color, onExpire);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {}
}
