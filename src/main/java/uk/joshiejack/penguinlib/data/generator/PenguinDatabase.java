package uk.joshiejack.penguinlib.data.generator;

import net.minecraft.data.PackOutput;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.data.TimeUnitRegistry;

import java.util.Arrays;

public class PenguinDatabase extends AbstractDatabaseProvider {
    public PenguinDatabase(PackOutput output) {
        super(output, PenguinLib.MODID);
    }

    @Override
    protected void addDatabaseEntries() {
        Arrays.stream(TimeUnitRegistry.Defaults.values())
                .forEach(unit -> addTimeUnit(unit.getName(), unit.getValue()));
    }
}