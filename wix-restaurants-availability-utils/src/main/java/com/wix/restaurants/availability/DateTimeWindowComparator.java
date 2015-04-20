package com.wix.restaurants.availability;

import java.util.Calendar;
import java.util.Comparator;
import java.util.TimeZone;

/**
 * DateTimeWindow chronological comparator.
 *
 * Two overlapping windows are assumed to be equal. If this is not the case, the general Comparator contract may be
 * violated and weird results may follow, e.g. the comparator will be inconsistent with equals.
 */
public class DateTimeWindowComparator implements Comparator<DateTimeWindow> {
    private final TimeZone tz;

    public DateTimeWindowComparator(TimeZone tz) {
        this.tz = tz;
    }

    @Override
    public int compare(DateTimeWindow o1, DateTimeWindow o2) {
        final Calendar o1start = o1.start(tz);
        final Calendar o1end = o1.end(tz);
        final Calendar o2start = o2.start(tz);
        final Calendar o2end = o2.end(tz);

        if (strictlyBefore(o1end, o2start)) {
            return -1;
        } else if (strictlyBefore(o2end, o1start)) {
            return 1;
        } else {
            return 0;
        }
    }

    private static boolean strictlyBefore(Calendar window1end, Calendar window2start) {
        if ((window1end == null) || (window2start == null)) {
            return false;
        }
        return !window1end.after(window2start);
    }
}
