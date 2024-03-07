package uk.joshiejack.penguinlib;

import net.neoforged.neoforge.common.ModConfigSpec;

public class PenguinConfig {
    public static ModConfigSpec.BooleanValue enableTeamCommands;
    public static ModConfigSpec.BooleanValue enableDatabaseDebugger;

    PenguinConfig(ModConfigSpec.Builder builder) {
        enableTeamCommands = builder.define("Enable Penguin Team Commands", true);
        enableDatabaseDebugger = builder.define("Enable Database Debug Output", false);
    }

    public static ModConfigSpec create() {
        return new ModConfigSpec.Builder().configure(PenguinConfig::new).getValue();
    }
}
