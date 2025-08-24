package uk.joshiejack.penguinlib.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nonnull;


@Event.HasResult
@Cancelable
public class UseWateringCanEvent extends PlayerEvent {
    private final ItemStack current;
    private final Level level;
    private final BlockPos pos;

    public UseWateringCanEvent(Player player, @Nonnull ItemStack current, Level level, BlockPos pos) {
        super(player);
        this.current = current;
        this.level = level;
        this.pos = pos;
    }

    @Nonnull
    public ItemStack getCurrent()
    {
        return current;
    }

    public Level getLevel()
    {
        return level;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public static class Post extends UseWateringCanEvent {
        public Post(Player player, @Nonnull ItemStack current, Level level, BlockPos pos) {
            super(player, current, level, pos);
        }
    }
}
