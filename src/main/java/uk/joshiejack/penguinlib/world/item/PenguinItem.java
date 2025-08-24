package uk.joshiejack.penguinlib.world.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class PenguinItem extends Item {
    private final Supplier<ItemStack> result;
    private final UseAnim useAction;
    private final int useDuration;
    private final BiConsumer<ItemStack, LivingEntity> finisher;

    public PenguinItem(Item.Properties properties) {
        super(properties);
        Properties pp = properties instanceof Properties ? ((Properties)properties) : null;
        this.useAction = pp == null ? UseAnim.NONE : pp.useAction;
        this.useDuration = pp == null ? 0 : pp.useDuration;
        this.result = pp == null  || pp.result == null ? null : pp.result;
        this.finisher = pp == null ? null : pp.consumer;
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack) {
        return stack.getItem().isEdible() ? useDuration : 0;
    }

    public ItemStack getLeftovers() {
        return result.get();
    }

    @Nonnull
    @Override
    public UseAnim getUseAnimation(@Nonnull ItemStack stack) {
        return stack.getItem().isEdible() ? useAction : UseAnim.NONE;
    }

    @Nonnull
    @Override
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull LivingEntity entity) {
        if (finisher != null) finisher.accept(stack, entity); //Do special stuff
        if (result == null || (!(entity instanceof Player))) return super.finishUsingItem(stack, world, entity);
        super.finishUsingItem(stack, world, entity);
        if (stack.isEmpty())
            return result.get();
        else
            ItemHandlerHelper.giveItemToPlayer((Player) entity, result.get());
        return stack;
    }

    public static class Properties extends Item.Properties {
        public BiConsumer<ItemStack, LivingEntity> consumer;
        private Supplier<ItemStack> result;
        private UseAnim useAction = UseAnim.EAT;
        private int useDuration = 32;

        public Properties useAction(UseAnim useAction) {
            this.useAction = useAction;
            return this;
        }

        public Properties useDuration(int useDuration) {
            this.useDuration = useDuration;
            return this;
        }

        public Properties withContainer(Supplier<ItemStack> result) {
            this.result = result;
            return this;
        }

        public Properties finishUsing(BiConsumer<ItemStack, LivingEntity> consumer) {
            this.consumer = consumer;
            return this;
        }
    }
}
