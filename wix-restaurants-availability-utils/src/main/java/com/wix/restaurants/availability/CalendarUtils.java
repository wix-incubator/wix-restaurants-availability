package com.wix.restaurants.availability;

import java.util.Calendar;

public class CalendarUtils {
    private CalendarUtils() {}

    private static final int millisInMinute = 1000 * 60;


    public static void advanceCalendar(Calendar cal, int toMinuteOfWeek) {
        final int millisOfMinute = getMillisOfMinute(cal);
        if (millisOfMinute > 0) {
            cal.add(Calendar.MILLISECOND, millisInMinute - millisOfMinute);
        }

        int currentMinuteOfWeek = getMinuteOfWeek(cal);

        int minutesToAdvance = toMinuteOfWeek - currentMinuteOfWeek;
        if (minutesToAdvance < 0) {
            minutesToAdvance += WeeklyTimeWindow.MINUTES_IN_WEEK;
        }

        // Optimization: start optimistic (assume no DST)
        cal.add(Calendar.MINUTE, minutesToAdvance);
        if (getMinuteOfWeek(cal) == toMinuteOfWeek) {
            return;
        }
        cal.add(Calendar.MINUTE, -minutesToAdvance);

        // Optimism failed, find that DST change moment
        for (int i = 0; i < minutesToAdvance; ++i) {
            final int previousMinuteOfWeek = currentMinuteOfWeek;
            cal.add(Calendar.MINUTE, 1);
            currentMinuteOfWeek = getMinuteOfWeek(cal);
            if (!isConsecutive(previousMinuteOfWeek, currentMinuteOfWeek)) {
                break;
            }
        }
    }

    private static int getMillisOfMinute(Calendar cal) {
        return cal.get(Calendar.SECOND) * 1000 +
                cal.get(Calendar.MILLISECOND);
    }

    private static int getMinuteOfWeek(Calendar cal) {
        return (cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY) * WeeklyTimeWindow.DAY +
                cal.get(Calendar.HOUR_OF_DAY) * WeeklyTimeWindow.HOUR +
                cal.get(Calendar.MINUTE);
    }

    private static boolean isConsecutive(int minuteOfWeek1, int minuteOfWeek2) {
        return ((minuteOfWeek1 + 1 - minuteOfWeek2) % WeeklyTimeWindow.MINUTES_IN_WEEK == 0);
    }
}
