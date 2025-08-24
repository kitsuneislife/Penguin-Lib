package uk.joshiejack.penguinlib.util.icon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.client.renderer.ShadowRenderer;

import java.util.List;
import java.util.Random;

public abstract class AbstractCyclicIcon<T extends Icon> extends Icon {
    private static final Random random = new Random(System.currentTimeMillis());
    final List<T> icons;
    protected T object;
    private long timer;
    private int id;

    public AbstractCyclicIcon(Type type, List<T> icons, int count) {
        super(type, count);
        this.icons = icons;
        this.id = this.icons.isEmpty() ? 0 : random.nextInt(this.icons.size());
        this.object = this.icons.isEmpty() ? null : this.icons.get(id);
        this.timer = System.currentTimeMillis();
    }


    @Override
    public Icon setCount(int count) {
        icons.forEach(icon -> icon.setCount(count));
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(Minecraft mc, GuiGraphics graphics, int x, int y) {
        if (object == null) return;
        if (System.currentTimeMillis() - timer > 1000) {
            id++;

            if (id >= icons.size())
                id = 0;
            object = icons.get(id);
            timer = System.currentTimeMillis();
        }

        renderCyclicIcon(mc, graphics, x, y);
    }

    @OnlyIn(Dist.CLIENT)
    protected abstract void renderCyclicIcon(Minecraft mc, GuiGraphics graphics, int x, int y);

    public abstract static class ItemStack extends AbstractCyclicIcon<ItemIcon> {
        public ItemStack(Type type, List<ItemIcon> list, int count) {
            super(type, list, count);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void renderCyclicIcon(Minecraft mc, GuiGraphics graphics, int x, int y) {
            if (shadowed) ShadowRenderer.enable();
            object.render(mc, graphics, x, y);
            if (shadowed) {
                ShadowRenderer.disable();
                shadowed = false;
            }
        }

        @Override
        public List<Component> getTooltipLines(Player player) {
            return object.getTooltipLines(player);
        }
    }
}
