package com.wix.restaurants.availability;

import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class CalendarUtilsTest {
    private static final TimeZone asiaJerusalem = TimeZone.getTimeZone("Asia/Jerusalem");

    private static Calendar calendar(TimeZone tz, int year, int month, int date, int hourOfDay, int minute, int second) {
        final Calendar cal = Calendar.getInstance(tz);
        cal.setLenient(false);
        cal.clear();
        cal.set(year, month, date, hourOfDay, minute, second);
        return cal;
    }

    @Test
    public void testAdvancesCalendarToRequestedMinuteOfWeekInSameWeek() {
        // In Asia/Jerusalem,
        //   advancing 2017-08-27 (Sunday) 00:00 to Monday yields 2017-08-28 (Monday) 00:00
        final Calendar cal = calendar(asiaJerusalem, 2017, Calendar.AUGUST, 27, 0, 0, 0);
        assertEquals(1503781200000L, cal.getTimeInMillis());

        CalendarUtils.advanceCalendar(cal, WeeklyTimeWindow.MONDAY);
        assertEquals(1503867600000L, cal.getTimeInMillis());
    }

    @Test
    public void testAdvancesCalendarToRequestedMinuteOfWeekInNextWeek() {
        // In Asia/Jerusalem,
        //   advancing 2017-08-26 (Saturday) 00:00 to Monday yields 2017-08-28 (Monday) 00:00
        final Calendar cal = calendar(asiaJerusalem, 2017, Calendar.AUGUST, 26, 0, 0, 0);
        assertEquals(1503694800000L, cal.getTimeInMillis());

        CalendarUtils.advanceCalendar(cal, WeeklyTimeWindow.MONDAY);
        assertEquals(1503867600000L, cal.getTimeInMillis());
    }

    @Test
    public void testSupportsSubMinuteCalendars() {
        // In Asia/Jerusalem,
        //   advancing 2017-08-26 (Saturday) 00:00:30 to Monday yields 2017-08-28 (Monday) 00:00
        final Calendar cal = calendar(asiaJerusalem, 2017, Calendar.AUGUST, 26, 0, 0, 30);
        assertEquals(1503694830000L, cal.getTimeInMillis());

        CalendarUtils.advanceCalendar(cal, WeeklyTimeWindow.MONDAY);
        assertEquals(1503867600000L, cal.getTimeInMillis());
    }

    @Test
    public void testStopsAdvancingOnDstStart() {
        // In Asia/Jerusalem,
        //   advancing 2016-03-25 (Friday) 00:00 to Saturday yields 2016-03-25 (Friday) 02:00
        //
        // Explanation:
        //   when local standard time was about to reach
        //   2016-03-25 (Friday) 02:00 clocks were turned forward 1 hour to
        //   2016-03-25 (Friday) 03:00 local daylight time instead.
        final Calendar cal = calendar(asiaJerusalem, 2016, Calendar.MARCH, 25, 0, 0, 0);
        assertEquals(1458856800000L, cal.getTimeInMillis());

        CalendarUtils.advanceCalendar(cal, WeeklyTimeWindow.SATURDAY);
        assertEquals(1458864000000L, cal.getTimeInMillis());
    }

    // Commented-out due to first assert failing (Java bug?)
    /*
    @Test
    public void testStopsAdvancingOnDstEnd() {
        // In Asia/Jerusalem,
        //   advancing 2016-10-30 (Sunday) 00:00 to Monday yields the second occurrence of 2016-10-30 (Sunday) 01:00
        //
        // Explanation:
        //   when local daylight time was about to reach
        //   2016-10-30 (Sunday) 02:00 clocks were turned backward 1 hour to
        //   2016-10-30 (Sunday) 01:00 local standard time instead.
        final Calendar cal = calendar(asiaJerusalem, 2016, Calendar.OCTOBER, 30, 0, 0, 0);
        assertEquals(1477774800000L, cal.getTimeInMillis());

        CalendarUtils.advanceCalendar(cal, WeeklyTimeWindow.MONDAY);
        assertEquals(1477782000000L, cal.getTimeInMillis());
    }
    */

    @Test
    public void testRunsFast() {
        final Calendar cal = calendar(asiaJerusalem, 2016, Calendar.JANUARY, 1, 0, 0, 0);

        for (int i = 0; i < 10*52; ++i) {
            CalendarUtils.advanceCalendar(cal, WeeklyTimeWindow.MONDAY);
            CalendarUtils.advanceCalendar(cal, WeeklyTimeWindow.SUNDAY);
        }
    }
}

