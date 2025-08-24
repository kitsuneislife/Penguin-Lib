package uk.joshiejack.penguinlib;

import net.minecraftforge.common.ForgeConfigSpec;

public class PenguinConfig {
    public static ForgeConfigSpec.BooleanValue enableTeamCommands;
    public static ForgeConfigSpec.BooleanValue enableDatabaseDebugger;

    PenguinConfig(ForgeConfigSpec.Builder builder) {
        enableTeamCommands = builder.define("Enable Penguin Team Commands", true);
        enableDatabaseDebugger = builder.define("Enable Database Debug Output", false);
    }

    public static ForgeConfigSpec create() {
        return new ForgeConfigSpec.Builder().configure(PenguinConfig::new).getValue();
    }
}
