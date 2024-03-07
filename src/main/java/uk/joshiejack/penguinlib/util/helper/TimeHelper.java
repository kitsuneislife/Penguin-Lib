package uk.joshiejack.penguinlib.util.helper;

import net.minecraft.world.level.Level;

import java.time.DayOfWeek;

public class TimeHelper {
    public static long TICKS_PER_DAY = 24000L;
    public static double SCALE = TICKS_PER_DAY / 24000D;
    public static long SIX_AM = (long) (SCALE * 6000D);
    public static final DayOfWeek[] DAYS = DayOfWeek.values();

    public static long scaleTime(long time) {
        return (long) (SCALE * (double) time);
    }

    public static int getElapsedDays(long time) {
        return (int) (time / TICKS_PER_DAY);
    }

    public static int getElapsedDays(Level level) {
        return (int) (level.getDayTime() / TICKS_PER_DAY);
    }

    public static long getTimeOfDay(long time) {
        return (time + SIX_AM) % TICKS_PER_DAY;
    }

    public static boolean isBetween(Level level, int open, int close) {
        long timeOfDay = getTimeOfDay(level.getDayTime()); //0-23999 by default
        return timeOfDay >= open && timeOfDay <= close;
    }

    public static DayOfWeek getWeekday(long time) {
        int days = TimeHelper.getElapsedDays(time);
        int modulus = days % 7;
        if (modulus < 0) modulus = 0;
        return DAYS[modulus];
    }

    public static String shortName(DayOfWeek day) {
        return day.name().substring(0, 3);
    }
}