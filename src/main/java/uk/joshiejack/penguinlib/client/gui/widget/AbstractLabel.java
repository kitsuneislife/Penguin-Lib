package uk.joshiejack.penguinlib.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.client.gui.MultiTooltip;

import javax.annotation.Nonnull;

public abstract class AbstractLabel<S extends Screen> extends net.minecraft.client.gui.components.AbstractWidget {
    protected final S screen;
    protected final Minecraft mc;
    private MultiTooltip tooltip;

    public AbstractLabel(S screen, int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.screen = screen;
        this.mc = screen.getMinecraft();
    }

    public AbstractLabel<S> setMultiTooltip(MultiTooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) { }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderLabel(graphics, mouseX, mouseY, partialTicks);
        if (this.tooltip != null) {
            this.tooltip.refreshTooltipForNextRenderPass(mc, this.isHovered(), this.isFocused(), this.getRectangle());
        }
    }

    protected abstract void renderLabel(@Nonnull GuiGraphics matrix, int mouseX, int mouseY, float partialTicks);
}