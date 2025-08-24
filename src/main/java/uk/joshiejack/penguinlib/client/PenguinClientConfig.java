package uk.joshiejack.penguinlib.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class PenguinClientConfig {
    public static ForgeConfigSpec.EnumValue<ClockType> clockType;
    public static ForgeConfigSpec.BooleanValue displayClockInHUDs;
    public static ForgeConfigSpec.BooleanValue requireClockItemForTime;
    public static ForgeConfigSpec.BooleanValue fancyGUI;

    PenguinClientConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Clock");
        clockType = builder.defineEnum("Clock Type", ClockType.TWENTY_FOUR_HOUR);
        displayClockInHUDs = builder.define("Display time in Penguin HUDs", true);
        requireClockItemForTime = builder.define("Require clock in inventory to display the time", false);
        builder.push("Style");
        fancyGUI = builder.define("Fancy GUI", true);
    }

    public static ForgeConfigSpec create() {
        return new ForgeConfigSpec.Builder().configure(PenguinClientConfig::new).getValue();
    }

    public enum ClockType {
        TWENTY_FOUR_HOUR, TWELVE_HOUR
    }
}
