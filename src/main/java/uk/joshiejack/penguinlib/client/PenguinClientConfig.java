package uk.joshiejack.penguinlib.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class PenguinClientConfig {
    public static ModConfigSpec.EnumValue<ClockType> clockType;
    public static ModConfigSpec.BooleanValue displayClockInHUDs;
    public static ModConfigSpec.BooleanValue requireClockItemForTime;
    public static ModConfigSpec.BooleanValue fancyGUI;


    PenguinClientConfig(ModConfigSpec.Builder builder) {
        builder.push("Clock");
        clockType = builder.defineEnum("Clock Type", ClockType.TWENTY_FOUR_HOUR);
        displayClockInHUDs = builder.define("Display time in Penguin HUDs", true);
        requireClockItemForTime = builder.define("Require clock in inventory to display the time", false);
        builder.push("Style");
        fancyGUI = builder.define("Fancy GUI", true);
    }

    public static ModConfigSpec create() {
        return new ModConfigSpec.Builder().configure(PenguinClientConfig::new).getValue();
    }

    public enum ClockType {
        TWENTY_FOUR_HOUR, TWELVE_HOUR
    }
}
