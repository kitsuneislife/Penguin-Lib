package uk.joshiejack.penguinlib.util.icon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class TextureIcon extends Icon {
    public static final Codec<TextureIcon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(icon -> icon.texture),
            Codec.INT.fieldOf("x").forGetter(icon -> icon.xPos),
            Codec.INT.fieldOf("y").forGetter(icon -> icon.yPos),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(icon -> icon.count)
    ).apply(instance, TextureIcon::new));
    private static final List<Component> EMPTY_LIST = new ArrayList<>();
    final ResourceLocation texture;
    final int xPos;
    final int yPos;
    private int count;

    public TextureIcon(ResourceLocation texture, int x, int y, int count) {
        super(Type.TEXTURE, count);
        this.texture = texture;
        this.xPos = x;
        this.yPos = y;
    }

    public TextureIcon(FriendlyByteBuf buf) {
        this(buf.readBoolean() ? buf.readResourceLocation() : DEFAULT_LOCATION, buf.readShort(), buf.readShort(),buf.readInt());
    }

    @Override
    public Codec<? extends Icon> codec() {
        return CODEC;
    }

    @Override
    public void toNetwork(FriendlyByteBuf pb) {
        if (texture.equals(DEFAULT_LOCATION))
            pb.writeBoolean(false);
        else {
            pb.writeBoolean(true);
            pb.writeResourceLocation(texture);
        }

        pb.writeShort(xPos);
        pb.writeShort(yPos);
        pb.writeInt(originalCount);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(Minecraft mc, GuiGraphics graphics, int x, int y) {
//        mc.gui.setBlitOffset(0); //TODO check this
        graphics.blit(texture, x, y, xPos, shadowed ? yPos + 16 : yPos, 16, 16);
        GuiUtils.renderIconDecorations(graphics, mc.font, x, y, count);
        shadowed = false;
    }

    @Override
    public Icon setCount(int count) {
        this.count = count;
        return this;
    }

    @Override
    public List<Component> getTooltipLines(Player player) {
        return EMPTY_LIST;
    }
}
