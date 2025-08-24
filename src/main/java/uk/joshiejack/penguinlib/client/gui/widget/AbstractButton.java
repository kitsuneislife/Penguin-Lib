package uk.joshiejack.penguinlib.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.joshiejack.penguinlib.client.gui.MultiTooltip;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractButton<S extends Screen> extends Button {
    protected final S screen;
    protected final Minecraft mc;
    private MultiTooltip tooltip;

    public AbstractButton(S screen, int x, int y, int w, int h, Component name, Button.OnPress action) {
        super(x, y, w, h, name, action, DEFAULT_NARRATION);
        this.screen = screen;
        this.mc = screen.getMinecraft();
    }

    public AbstractButton<S> setMultiTooltip(MultiTooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics matrix, int mouseX, int mouseY, float partialTicks) {
        renderButton(matrix, mouseX, mouseY, partialTicks, isHovered());
        if (this.tooltip != null) {
            this.tooltip.refreshTooltipForNextRenderPass(mc, this.isHovered(), this.isFocused(), this.getRectangle());
        }
//        if (this.isHovered()) //TODO: TEST?
//            screen.addFuture(() -> renderToolTip(matrix, mouseX, mouseY));
    }

    protected abstract void renderButton(@Nonnull GuiGraphics matrix, int mouseX, int mouseY, float partialTicks, boolean hovered);
}