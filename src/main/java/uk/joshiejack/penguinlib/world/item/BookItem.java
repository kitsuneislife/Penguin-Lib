package uk.joshiejack.penguinlib.world.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class BookItem extends Item {
    private final Supplier<MenuProvider> provider;

    public BookItem(Properties properties, Supplier<MenuProvider> provider) {
        super(properties);
        this.provider = provider;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(provider.get(), Component.translatable(getDescriptionId())));
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}