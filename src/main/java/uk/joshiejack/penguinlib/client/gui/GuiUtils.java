package uk.joshiejack.penguinlib.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import uk.joshiejack.penguinlib.util.helper.StringHelper;

public class GuiUtils {
    public static void renderIconDecorations(GuiGraphics graphics, Font pFont, int pX, int pY, int count) {
        if (count != 1) {
            graphics.pose().pushPose();
            String s = StringHelper.convertNumberToString(count);
            graphics.pose().translate(0.0F, 0.0F, 200.0F);
            graphics.drawString(pFont, s, pX + 19 - 2 - pFont.width(s), pY + 6 + 3, 16777215, true);
            graphics.pose().popPose();
        }
    }
}
