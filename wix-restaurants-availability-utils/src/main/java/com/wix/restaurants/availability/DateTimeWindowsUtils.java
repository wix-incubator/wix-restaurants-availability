package com.wix.restaurants.availability;

import java.util.*;

public class DateTimeWindowsUtils {
    private DateTimeWindowsUtils() {}

    public static List<DateTimeWindow> relevantOnly(List<DateTimeWindow> timeWindows, Calendar cal) {
        final List<DateTimeWindow> filtered = new LinkedList<>();

        for (DateTimeWindow timeWindow : timeWindows) {
            if ((timeWindow.end == null) || cal.before(timeWindow.end(cal.getTimeZone()))) {
                filtered.add(timeWindow);
            }
        }

        return filtered;
    }

    public static List<DateTimeWindow> normalize(List<DateTimeWindow> timeWindows) {
        boolean hasSinceForever = false;
        boolean hasUntilForever = false;
        final Set<Date> sortedUniquePoints = new TreeSet<>();

        for (DateTimeWindow timeWindow : timeWindows) {
            if (timeWindow.start != null) {
                sortedUniquePoints.add(timeWindow.start);
            } else {
                hasSinceForever = true;
            }
            if (timeWindow.end != null) {
                sortedUniquePoints.add(timeWindow.end);
            } else {
                hasUntilForever = true;
            }
        }
        final List<Date> sortedUniquePointsList = new ArrayList<>(sortedUniquePoints);

        // Convert list to sorted non-overlapping windows
        final List<DateTimeWindow> normalizedTimeWindows = new ArrayList<>(sortedUniquePointsList.size() - 1 + 2);
        if (hasSinceForever) {
            if (!sortedUniquePointsList.isEmpty()) {
                normalizedTimeWindows.add(new DateTimeWindow(null, sortedUniquePointsList.get(0), false));
            } else if (hasUntilForever) {
                normalizedTimeWindows.add(new DateTimeWindow((Date) null, null, false));
            }
        }

        for (int i = 0; i < sortedUniquePointsList.size() - 1; ++i) {
            normalizedTimeWindows.add(new DateTimeWindow(
                    sortedUniquePointsList.get(i), sortedUniquePointsList.get(i + 1), false));
        }

        if (hasUntilForever) {
            if (!sortedUniquePointsList.isEmpty()) {
                normalizedTimeWindows.add(new DateTimeWindow(
                        sortedUniquePointsList.get(sortedUniquePointsList.size() - 1), null, false));
            }
        }

        // Assign availability to windows, omit unknowns
        final List<DateTimeWindow> result = new ArrayList<>(normalizedTimeWindows.size());
        for (DateTimeWindow normalizedTimeWindow : normalizedTimeWindows) {
            final DateTimeWindow lastCover = findLastCovering(timeWindows, normalizedTimeWindow);
            if (lastCover != null) {
                result.add(new DateTimeWindow(
                        normalizedTimeWindow.start, normalizedTimeWindow.end,
                        lastCover.available, lastCover.reason, lastCover.comment
                ));
            }
        }
        return result;
    }

    private static DateTimeWindow findLastCovering(List<DateTimeWindow> potentialCovers, DateTimeWindow window) {
        final ListIterator<DateTimeWindow> it = potentialCovers.listIterator(potentialCovers.size());
        while (it.hasPrevious()) {
            final DateTimeWindow potentialCover = it.previous();
            if (covers(potentialCover, window)) {
                return potentialCover;
            }
        }
        return null;
    }

    private static boolean covers(DateTimeWindow cover, DateTimeWindow window) {
        return (((cover.start == null) || (window.start != null && cover.start.compareTo(window.start) <= 0)) &&
                ((cover.end == null) || (window.end != null && cover.end.compareTo(window.end) >= 0)));
    }
}
