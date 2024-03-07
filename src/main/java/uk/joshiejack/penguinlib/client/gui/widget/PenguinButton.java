package uk.joshiejack.penguinlib.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.joshiejack.penguinlib.PenguinLib;


public class PenguinButton extends Button {
    protected static final WidgetSprites SPRITES = new WidgetSprites(
            PenguinLib.prefix("widget/button"),
            PenguinLib.prefix("widget/button_disabled"),
            PenguinLib.prefix("widget/button_highlighted")
    );

    private final WidgetSprites sprites;

    public PenguinButton(Button.Builder builder) {
        super(builder);
        this.sprites = SPRITES;
    }

    public PenguinButton(Button.Builder builder, @Nullable WidgetSprites sprites) {
        super(builder);
        this.sprites = sprites == null ? SPRITES : sprites;
    }

    public static PenguinButton.Builder penguinBuilder(@NotNull Component pMessage, Button.@NotNull OnPress pOnPress) {
        return new PenguinButton.Builder(pMessage, pOnPress);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        pGuiGraphics.blitSprite(sprites.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        renderForeground(pGuiGraphics, Minecraft.getInstance().font, pMouseX, pMouseY, pPartialTick);
    }

    protected void renderForeground(GuiGraphics pGuiGraphics, Font font, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderString(pGuiGraphics, font, getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    public static class Builder extends Button.Builder {
        private WidgetSprites sprites;

        public Builder(Component component, OnPress press) {
            super(component, press);
        }

        public Builder sprites(String modid, String name) {
            sprites = new WidgetSprites(new ResourceLocation(modid, "widget/" + name),
                    new ResourceLocation(modid, "widget/" + name + "_disabled"),
                    new ResourceLocation(modid, "widget/" + name + "_highlighted"));
            return this;
        }

        @Override
        public @NotNull PenguinButton build() {
            return new PenguinButton(this, sprites);
        }
    }
}
