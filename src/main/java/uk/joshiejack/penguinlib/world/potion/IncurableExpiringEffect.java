package uk.joshiejack.penguinlib.world.potion;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
// Removed EffectCure import as it doesn't exist in Forge 1.20.1

import java.util.Set;
import java.util.function.Consumer;

public class IncurableExpiringEffect extends AbstractExpiringEffect {
    public IncurableExpiringEffect(MobEffectCategory type, int color, Consumer<ServerPlayer> onExpire) {
        super(type, color, onExpire);
    }

    // Removed fillEffectCures method as EffectCure doesn't exist in Forge 1.20.1
}
