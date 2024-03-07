package uk.joshiejack.penguinlib.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nonnull;

public abstract class AbstractContainerScreen <T extends AbstractContainerMenu> extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<T> {
    protected final ResourceLocation texture;

    public AbstractContainerScreen(T container, Inventory inv, Component name, int width, int height) {
        this(container, inv, name, null, width, height);
    }

    public AbstractContainerScreen(T container, Inventory inv, Component name, ResourceLocation texture, int width, int height) {
        super(container, inv, name);
        this.texture = texture;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    @Override
    protected void init() {
        super.init();
        if (minecraft != null && minecraft.player != null)
            initScreen(minecraft, minecraft.player);
    }

    protected abstract void initScreen(@Nonnull Minecraft minecraft, @Nonnull Player player);

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.setColor(1F, 1F, 1F, 1F);
    }

    protected static ResourceLocation guiTexture(String modid, String name) {
        return new ResourceLocation(modid, "textures/gui/%s.png".formatted(name));
    }

    protected static Component translated(String modid, String name) {
        return Component.translatable("%s.%s.%s".formatted("gui", modid, name));
    }
}
