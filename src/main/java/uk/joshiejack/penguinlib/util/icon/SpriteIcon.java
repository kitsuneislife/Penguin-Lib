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

public class SpriteIcon extends Icon {
    public static final Codec<SpriteIcon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("sprite").forGetter(icon -> icon.sprite),
            ResourceLocation.CODEC.optionalFieldOf("shadowed_sprite", DEFAULT_LOCATION).forGetter(icon -> icon.shadowedSprite),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(icon -> icon.count)
    ).apply(instance, SpriteIcon::new));
    private static final List<Component> EMPTY_LIST = new ArrayList<>();
    final ResourceLocation sprite;
    final ResourceLocation shadowedSprite;
    private int count;

    //Use same sprite for shadowedSprite if not provided
    public SpriteIcon(ResourceLocation sprite) {
        this(sprite, sprite, 1);
    }
    public SpriteIcon(ResourceLocation sprite, ResourceLocation shadowedSprite) {
        this(sprite, shadowedSprite, 1);
    }

    public SpriteIcon(ResourceLocation sprite, ResourceLocation shadowedSprite, int count) {
        super(Type.SPRITE, count);
        this.sprite = sprite;
        this.shadowedSprite = shadowedSprite;
    }

    public SpriteIcon(FriendlyByteBuf buf) {
        this(buf.readBoolean() ? buf.readResourceLocation() : DEFAULT_LOCATION, buf.readBoolean() ? buf.readResourceLocation() : DEFAULT_LOCATION, buf.readInt());
    }

    @Override
    public Codec<? extends Icon> codec() {
        return CODEC;
    }

    @Override
    public void toNetwork(FriendlyByteBuf pb) {
        if (sprite.equals(DEFAULT_LOCATION))
            pb.writeBoolean(false);
        else {
            pb.writeBoolean(true);
            pb.writeResourceLocation(sprite);
        }

        pb.writeInt(originalCount);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(Minecraft mc, GuiGraphics graphics, int x, int y) {
//        mc.gui.setBlitOffset(0); //TODO check this
        ResourceLocation sprite = shadowed ? shadowedSprite : this.sprite;
                    graphics.blit(sprite, x, y, 0, 0, 16, 16);  // Corrected signature for Forge 1.20.1
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
