package uk.joshiejack.penguinlib.client.gui.widget;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.client.PenguinClientConfig;

@OnlyIn(Dist.CLIENT)
public class PenguinNumberButton extends PenguinButton {
    protected static final Char2ObjectMap<ResourceLocation> NUMBERS = new Char2ObjectOpenHashMap<>();
    static {
        for (int i = 0; i <= 9; i++) {
            NUMBERS.put(("" + i).toCharArray()[0], PenguinLib.prefix("widget/numbers/number_" + i));
        }
    }

    private final char[] numberText;
    private final int number;

    protected PenguinNumberButton(Builder builder) {
        super(builder);
        this.numberText = getMessage().getString().toCharArray();
        this.number = Integer.parseInt(getMessage().getString());
    }

    public static Button.Builder builder(@NotNull Component component, @NotNull OnPress press) {
        return PenguinClientConfig.fancyGUI.get() ? new Builder(component, press) :  Button.builder(component, press);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, Font font, int pMouseX, int pMouseY, float pPartialTick) {
        if (PenguinClientConfig.fancyGUI.get()) {
            int x = 0;
            int offset = number < 10 ? 10 : number < 100 ? 5 : 0;
            for (char c: numberText) {
                x += 9;
                graphics.blitSprite(NUMBERS.get(c), this.getX() + x + offset - 7, this.getY() + 5, 8, 8);
            }
        } else
            this.renderString(graphics, font, getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    public static class Builder extends Button.Builder {
        public Builder(Component component, OnPress press) {
            super(component, press);
        }

        @Override
        public @NotNull PenguinNumberButton build() {
            return new PenguinNumberButton(this);
        }
    }
}
