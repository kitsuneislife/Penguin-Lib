package uk.joshiejack.penguinlib.util.helper;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;

public class StringHelper {
    public static String[] getListFromDatabase(String data) {
        return data.replace("\"", "").replace(", ", ",").split(",");
    }

    public static Component localize(String text) {
        return Component.translatable(text);
    }

    public static Component format(String text, Object... objects) {
        return Component.translatable(text, objects);
    }

    public static boolean isEarlierThan(String name, char c) {
        return Character.toLowerCase(name.charAt(0)) < c;
    }

    public static String convertNumberToString(long number) {
        return convertNumberToString(number, false);
    }

    public static String convertNumberToString(long number, boolean shortform) {
        if (number < 0) number = -number;
        if (number < 1000) return "" + number;
        long remainder = number % 1000;
        int decimal = remainder == 0 ? 0 : shortform || remainder % 100 == 0 ? 1 : remainder % 10 == 0 ? 2 : 3;
        int exp = (int) (Math.log(number) / Math.log(1000));
        return String.format("%." + decimal + "f%c", number / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

    private static boolean unicode;

    public static void enableUnicode() {
        Minecraft mc = Minecraft.getInstance();
        unicode = mc.options.forceUnicodeFont().get();
        mc.options.forceUnicodeFont().set(true);
    }

    public static void disableUnicode() {
        Minecraft.getInstance().options.forceUnicodeFont().set(unicode);
    }

    public static String[] decompose(String namespace, char character) {
        String[] astring = new String[]{"minecraft", namespace};
        int i = namespace.indexOf(character);
        if (i >= 0) {
            astring[1] = namespace.substring(i + 1);
            if (i >= 1) {
                astring[0] = namespace.substring(0, i);
            }
        }

        return astring;
    }

    public static String join(char character, Object... vars) {
        return StringUtils.join(Lists.newArrayList(vars).stream().map(Object::toString).collect(Collectors.toList()), character);
    }

    public static Component withClickableCommand(String command, String tooltip, Object... formatting) {
        return withClickableCommand(ChatFormatting.AQUA, command, tooltip, formatting);
    }

    public static Component withClickableCommand(ChatFormatting color, String command, String tooltip, Object... formatting) {
        return Component.translatable(command, formatting).withStyle((p_241055_1_) -> p_241055_1_.withColor(color)
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command + join(' ', formatting)))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(tooltip))));
    }
}
