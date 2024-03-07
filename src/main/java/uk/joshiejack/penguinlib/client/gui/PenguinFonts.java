package uk.joshiejack.penguinlib.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.Lazy;
import uk.joshiejack.penguinlib.PenguinLib;

import java.util.Map;

public class PenguinFonts {
    public static final ResourceLocation FANCY_LOCATION = new ResourceLocation(PenguinLib.MODID, "fancy");
    public static final Lazy<Font> FANCY = Lazy.of(PenguinFonts::createFancyFont);
    public static final Lazy<Font> UNICODE = Lazy.of(PenguinFonts::createUnicodeFont);

    private static Font createFancyFont() {
        FontSet missingFontSet = Minecraft.getInstance().fontManager.missingFontSet;
        Map<ResourceLocation, FontSet> fontSets = Minecraft.getInstance().fontManager.fontSets;
        return new Font((m) -> fontSets.getOrDefault(FANCY_LOCATION, missingFontSet), false);
    }

    private static Font createUnicodeFont() {
        FontSet missingFontSet = Minecraft.getInstance().fontManager.missingFontSet;
        Map<ResourceLocation, FontSet> fontSets = Minecraft.getInstance().fontManager.fontSets;
        return new Font((m) -> fontSets.getOrDefault(Minecraft.UNIFORM_FONT, missingFontSet), false);
    }
}