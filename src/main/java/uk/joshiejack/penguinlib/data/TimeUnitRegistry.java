package uk.joshiejack.penguinlib.data;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.event.DatabaseLoadedEvent;

import java.util.Locale;

@Mod.EventBusSubscriber(modid = PenguinLib.MODID)
public class TimeUnitRegistry {
    private static final Object2IntMap<String> TIME_UNITS = new Object2IntOpenHashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) {
        TIME_UNITS.clear(); //Reset the time unit data
        event.table("time_unit").rows().forEach(row -> {
            TIME_UNITS.put(row.name(), row.getAsInt("duration"));
        });
    }

    public static int get(String name) {
        return TIME_UNITS.containsKey(name) ? TIME_UNITS.getInt(name) : Integer.MAX_VALUE;
    }

    public static int getOrDefault(String name, int def) {
        return TIME_UNITS.containsKey(name) ? TIME_UNITS.getInt(name) : def;
    }

    public enum Defaults {
        THREE_MINUTES(50), FIVE_MINUTES(100), QUARTER_HOUR(250),
        HALF_HOUR(500), HOUR(1000), HALF_DAY(12000), DAY(24000),
        WEEK(168000), YEAR(2880000);

        private final int time;

        Defaults(int time) {
            this.time = time;
        }

        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public long getValue() {
            return time;
        }
    }
}
