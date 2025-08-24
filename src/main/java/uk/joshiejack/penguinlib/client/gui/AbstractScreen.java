package uk.joshiejack.penguinlib.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;

public abstract class AbstractScreen extends Screen {
    protected final ResourceLocation texture;
    protected final int imageWidth;
    protected final int imageHeight;
    protected int leftPos;
    protected int topPos;

    protected AbstractScreen(Component pTitle, ResourceLocation texture) {
        this(pTitle, texture, 166, 201);
    }

    protected AbstractScreen(Component pTitle, ResourceLocation texture, int imageWidth, int imageHeight) {
        super(pTitle);
        this.texture = texture;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    @Override
    protected void init() {
        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;
        if (minecraft != null && minecraft.player != null)
            initScreen(minecraft, minecraft.player);
    }

    protected abstract void initScreen(@Nonnull Minecraft minecraft, @Nonnull Player player);

    // Removed override annotation for Forge 1.20.1 compatibility
    public void renderBackground(GuiGraphics graphics, int x, int y, float pPartialTick) {
        graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderForeground(graphics, x, y, pPartialTick);
    }

    protected abstract void renderForeground(GuiGraphics graphics, int x, int y, float partialTicks);

    protected static ResourceLocation guiTexture(String modid, String name) {
        return new ResourceLocation(modid, "textures/gui/%s.png".formatted(name));
    }

    protected static Component translated(String modid, String name) {
        return Component.translatable("%s.%s.%s".formatted("gui", modid, name));
    }
}
