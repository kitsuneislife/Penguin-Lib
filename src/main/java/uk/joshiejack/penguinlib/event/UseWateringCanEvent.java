package uk.joshiejack.penguinlib.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import javax.annotation.Nonnull;


@Event.HasResult
public class UseWateringCanEvent extends PlayerEvent implements ICancellableEvent {
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
