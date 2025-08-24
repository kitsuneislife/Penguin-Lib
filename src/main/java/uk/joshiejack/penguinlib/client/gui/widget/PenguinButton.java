package uk.joshiejack.penguinlib.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.joshiejack.penguinlib.PenguinLib;


public class PenguinButton extends Button {
    // WidgetSprites não está disponível no Forge 1.20.1, usar ResourceLocation diretamente

    public PenguinButton(Button.Builder builder) {
        super(builder);
    }

    public static PenguinButton.Builder penguinBuilder(@NotNull Component pMessage, Button.@NotNull OnPress pOnPress) {
        return new PenguinButton.Builder(pMessage, pOnPress);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        // Render simples sem WidgetSprites para Forge 1.20.1
        pGuiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 
                         this.active ? (this.isHoveredOrFocused() ? 0xFF8B8B8B : 0xFF000000) : 0xFF666666);
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        renderForeground(pGuiGraphics, Minecraft.getInstance().font, pMouseX, pMouseY, pPartialTick);
    }

    protected void renderForeground(GuiGraphics pGuiGraphics, Font font, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderString(pGuiGraphics, font, getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    public static class Builder extends Button.Builder {
        
        public Builder(Component component, OnPress press) {
            super(component, press);
        }

        public Builder sprites(String modid, String name) {
            // WidgetSprites não disponível no Forge 1.20.1
            return this;
        }

        @Override
        public @NotNull PenguinButton build() {
            return new PenguinButton(this);
        }
    }
}
