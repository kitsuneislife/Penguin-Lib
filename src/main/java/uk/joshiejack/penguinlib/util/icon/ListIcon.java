package uk.joshiejack.penguinlib.util.icon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.client.renderer.ShadowRenderer;

import java.util.ArrayList;
import java.util.List;

public class ListIcon extends AbstractCyclicIcon<Icon> {
    public static final Icon EMPTY = new ListIcon(new ArrayList<>(), 1);
    public static final Codec<ListIcon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(Icon.CODEC).fieldOf("icons").forGetter(i -> i.icons),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(i -> i.originalCount)
            ).apply(instance, ListIcon::new));

    public ListIcon(List<Icon> icons) {
        super(Type.LIST, icons, 1);
    }

    public ListIcon(List<Icon> icons, int count) {
        super(Type.LIST, icons, count);
    }

    public ListIcon(FriendlyByteBuf buf) {
        super(Type.LIST, new ArrayList<>(), buf.readInt());
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            icons.add(Icon.Type.values()[buf.readByte()].apply(buf)); //Hmm
        }
    }

    @Override
    public Codec<? extends Icon> codec() {
        return CODEC;
    }

    @Override
    public void toNetwork(FriendlyByteBuf pb) {
        pb.writeInt(originalCount);
        pb.writeShort(icons.size());
        icons.forEach(icon -> icon.toNetwork(pb));
    }

    @Override
    protected void renderCyclicIcon(Minecraft mc, GuiGraphics graphics, int x, int y) {
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