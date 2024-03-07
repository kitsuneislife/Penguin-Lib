package uk.joshiejack.penguinlib.util;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum PenguinGroup implements StringRepresentable {
    PLAYER, TEAM, GLOBAL;

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
