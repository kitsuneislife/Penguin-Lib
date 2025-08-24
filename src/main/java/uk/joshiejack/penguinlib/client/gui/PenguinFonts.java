package uk.joshiejack.penguinlib.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import uk.joshiejack.penguinlib.PenguinLib;

import java.util.Map;

public class PenguinFonts {
    public static final ResourceLocation FANCY_LOCATION = new ResourceLocation(PenguinLib.MODID, "fancy");
    public static final Lazy<Font> FANCY = Lazy.of(PenguinFonts::createFancyFont);
    public static final Lazy<Font> UNICODE = Lazy.of(PenguinFonts::createUnicodeFont);

    private static Font createFancyFont() {
        // Font manager access simplified for Forge 1.20.1
        return Minecraft.getInstance().font;  // Use default font as fallback
    }

    private static Font createUnicodeFont() {
        // Font manager access simplified for Forge 1.20.1
        return Minecraft.getInstance().font;  // Use default font as fallback
    }
}