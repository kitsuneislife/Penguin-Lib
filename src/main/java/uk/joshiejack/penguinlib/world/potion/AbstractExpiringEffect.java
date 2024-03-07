package uk.joshiejack.penguinlib.world.potion;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class AbstractExpiringEffect extends PenguinEffect {
    protected boolean expiring = false;
    private final Consumer<ServerPlayer> onExpire;

    public AbstractExpiringEffect(MobEffectCategory type, int color, Consumer<ServerPlayer> onExpire) {
        super(type, color);
        this.onExpire = onExpire;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        expiring = duration == 1;
        return expiring || super.shouldApplyEffectTickThisTick(duration, amplifier);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (expiring && entity instanceof ServerPlayer player)
            onExpire.accept(player);
    }
}
