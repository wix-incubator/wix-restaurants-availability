package com.wix.restaurants.availability;

import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DateTimeWindowsUtilsTest {
    @Test
    public void testLeavesEmptyListAsIs() {
        final List<DateTimeWindow> windows = Collections.emptyList();

        final List<DateTimeWindow> normalized = DateTimeWindowsUtils.normalize(windows);

        assertEquals(windows, normalized);
    }

    @Test
    public void testLeavesSingletonListAsIs() {
        final List<DateTimeWindow> windows = Collections.singletonList(
                when(calendar(2010, Calendar.DECEMBER, 15, 0, 0), Calendar.DAY_OF_MONTH, 1, Boolean.TRUE)
        );

        final List<DateTimeWindow> normalized = DateTimeWindowsUtils.normalize(windows);

        assertEquals(windows, normalized);
    }

    @Test
    public void testOrdersWindowsFirstToLast() {
        final List<DateTimeWindow> windows = Arrays.asList(
                when(calendar(2010, Calendar.DECEMBER, 15, 0, 0), Calendar.DAY_OF_MONTH, 1, Boolean.TRUE),
                when(calendar(2010, Calendar.DECEMBER, 14, 0, 0), Calendar.DAY_OF_MONTH, 1, Boolean.TRUE)
        );

        final List<DateTimeWindow> normalized = DateTimeWindowsUtils.normalize(windows);

        final List<DateTimeWindow> expected = Arrays.asList(
                when(calendar(2010, Calendar.DECEMBER, 14, 0, 0), Calendar.DAY_OF_MONTH, 1, Boolean.TRUE),
                when(calendar(2010, Calendar.DECEMBER, 15, 0, 0), Calendar.DAY_OF_MONTH, 1, Boolean.TRUE)
        );

        assertEquals(expected, normalized);
    }

    @Test
    public void testResolvesOverlappingWindowsByLastOneWinsRule() {
        final List<DateTimeWindow> windows = Arrays.asList(
                when(calendar(2010, Calendar.DECEMBER, 14, 0, 0), Calendar.DAY_OF_MONTH, 2, Boolean.TRUE),
                when(calendar(2010, Calendar.DECEMBER, 15, 0, 0), Calendar.DAY_OF_MONTH, 1, Boolean.FALSE)
        );

        final List<DateTimeWindow> normalized = DateTimeWindowsUtils.normalize(windows);

        final List<DateTimeWindow> expected = Arrays.asList(
                when(calendar(2010, Calendar.DECEMBER, 14, 0, 0), Calendar.DAY_OF_MONTH, 1, Boolean.TRUE),
                when(calendar(2010, Calendar.DECEMBER, 15, 0, 0), Calendar.DAY_OF_MONTH, 1, Boolean.FALSE)
        );

        assertEquals(expected, normalized);
    }

    private static Calendar calendar(int year, int month, int date, int hourOfDay, int minute) {
        final Calendar cal = Calendar.getInstance();
        cal.setLenient(false);
        cal.clear();
        cal.set(year, month, date, hourOfDay, minute);
        return cal;
    }

    private static DateTimeWindow when(Calendar start, int field, int amount, Boolean available) {
        final Calendar end = (Calendar) start.clone();
        end.add(field, amount);
        return new DateTimeWindow(start, end, available);
    }
}
