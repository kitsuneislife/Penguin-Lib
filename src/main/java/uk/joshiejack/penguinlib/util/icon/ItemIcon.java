package uk.joshiejack.penguinlib.util.icon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.client.renderer.ShadowRenderer;

import java.util.List;

public class ItemIcon extends Icon {
    public static final Codec<ItemIcon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(icon -> icon.stack)
    ).apply(instance, ItemIcon::new));

    public static final Icon EMPTY = new ItemIcon(ItemStack.EMPTY);
    public final ItemStack stack;
    private int count;

    public ItemIcon(ItemStack stack) {
        super(Type.ITEM, stack.getCount());
        this.stack = stack;
        this.count = stack.getCount();
    }
    public ItemIcon(FriendlyByteBuf buf) {
        this(buf.readItem());
    }

    @Override
    public Codec<? extends Icon> codec() {
        return CODEC;
    }

    @Override
    public void toNetwork(FriendlyByteBuf pb) {
        pb.writeItem(stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(Minecraft mc, GuiGraphics graphics, int x, int y) {
        if (shadowed) ShadowRenderer.enable();
        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(mc.font, stack, x, y, count == 1 ? null : String.valueOf(this.count));
        if (shadowed) {
            ShadowRenderer.disable();
            shadowed = false;
        }

        setCount(originalCount); //Reset the count
    }

    @Override
    public Icon setCount(int count) {
        this.count = count;
        return this;
    }

    @Override
    public List<Component> getTooltipLines(Player player) {
        return stack.getTooltipLines(player, TooltipFlag.NORMAL);
    }
}
