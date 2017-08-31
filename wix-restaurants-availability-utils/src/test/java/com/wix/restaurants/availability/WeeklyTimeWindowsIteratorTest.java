package com.wix.restaurants.availability;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WeeklyTimeWindowsIteratorTest {
    private static final TimeZone asiaJerusalem = TimeZone.getTimeZone("Asia/Jerusalem");
    private static final TimeZone americaSaoPaulo = TimeZone.getTimeZone("America/Sao_Paulo");

    private static Calendar lenientCalendar(TimeZone tz, int year, int month, int date, int hourOfDay, int minute, int second) {
        final Calendar cal = Calendar.getInstance(tz);
        cal.setLenient(true);
        cal.clear();
        cal.set(year, month, date, hourOfDay, minute, second);
        return cal;
    }

    private static Calendar calendar(TimeZone tz, int year, int month, int date, int hourOfDay, int minute, int second) {
        final Calendar cal = Calendar.getInstance(tz);
        cal.setLenient(false);
        cal.clear();
        cal.set(year, month, date, hourOfDay, minute, second);
        return cal;
    }

	@Test
	public void testNull() {
        final Calendar cal = calendar(asiaJerusalem, 0, 0, 0, 0, 0, 0);
		final StatusIteratorTester tester = new StatusIteratorTester(
				new WeeklyTimeWindowsIterator(cal, null));
		
		tester.assertLastStatus(Status.STATUS_AVAILABLE);
		tester.assertDone();
	}
	
	@Test
	public void testEmpty() {
        final Calendar cal = calendar(asiaJerusalem, 0, 0, 0, 0, 0, 0);
		@SuppressWarnings("unchecked")
		final StatusIteratorTester tester = new StatusIteratorTester(
				new WeeklyTimeWindowsIterator(cal, Collections.EMPTY_LIST));
		
		tester.assertLastStatus(Status.STATUS_AVAILABLE);
		tester.assertDone();
	}

	@Test
	public void testSundayOnly() {
		final Calendar cal = calendar(asiaJerusalem, 2010, Calendar.DECEMBER, 12, 0, 0, 0);
		final List<WeeklyTimeWindow> weekly = Arrays.asList(new WeeklyTimeWindow[] {
				new WeeklyTimeWindow(WeeklyTimeWindow.SUNDAY, WeeklyTimeWindow.DAY)
		});

		final StatusIteratorTester tester = new StatusIteratorTester(
				new WeeklyTimeWindowsIterator(cal, weekly), cal);

		for (int i = 0; i < 100; ++i) {
			tester.assertNextStatus(Status.STATUS_AVAILABLE, Calendar.DAY_OF_MONTH, 1);
			tester.assertNextStatus(Status.STATUS_UNAVAILABLE, Calendar.DAY_OF_MONTH, 6);
		}
	}

	@Test
	public void testSundayOnlyWithSeconds() {
        final Calendar cal = calendar(asiaJerusalem, 2010, Calendar.DECEMBER, 12, 0, 0, 30);
		final List<WeeklyTimeWindow> weekly = Arrays.asList(new WeeklyTimeWindow[] {
				new WeeklyTimeWindow(WeeklyTimeWindow.SUNDAY, WeeklyTimeWindow.DAY)
		});

        final Calendar calNoSeconds = (Calendar) cal.clone();
        calNoSeconds.set(Calendar.SECOND, 0);

		final StatusIteratorTester tester = new StatusIteratorTester(
				new WeeklyTimeWindowsIterator(cal, weekly), calNoSeconds);

		tester.assertNextStatus(Status.STATUS_AVAILABLE, Calendar.DAY_OF_MONTH, 1);
	}
	
	@Test
	public void testMondayOnly() {
        final Calendar cal = calendar(asiaJerusalem, 2010, Calendar.DECEMBER, 13, 0, 0, 0);
		final List<WeeklyTimeWindow> weekly = Arrays.asList(new WeeklyTimeWindow[] {
				new WeeklyTimeWindow(WeeklyTimeWindow.MONDAY, WeeklyTimeWindow.DAY)
		});
		
		final StatusIteratorTester tester = new StatusIteratorTester(
				new WeeklyTimeWindowsIterator(cal, weekly), cal);
		
		for (int i = 0; i < 100; ++i) {
			tester.assertNextStatus(Status.STATUS_AVAILABLE, Calendar.DAY_OF_MONTH, 1);
			tester.assertNextStatus(Status.STATUS_UNAVAILABLE, Calendar.DAY_OF_MONTH, 6);
		}
	}
	
	@Test
	public void testStartMidWindow() {
        final Calendar cal = calendar(asiaJerusalem, 2010, Calendar.DECEMBER, 13, 12, 0, 0);
		final List<WeeklyTimeWindow> weekly = Arrays.asList(new WeeklyTimeWindow[] {
				new WeeklyTimeWindow(WeeklyTimeWindow.MONDAY, WeeklyTimeWindow.DAY)
		});
		
		final StatusIteratorTester tester = new StatusIteratorTester(
				new WeeklyTimeWindowsIterator(cal, weekly), cal);
		
		tester.assertNextStatus(Status.STATUS_AVAILABLE, Calendar.HOUR_OF_DAY, 12);
	}
	
	@Test
	public void testMondayTuesdayFriday() {
        final Calendar cal = calendar(asiaJerusalem, 2010, Calendar.DECEMBER, 13, 0, 0, 0);
		final List<WeeklyTimeWindow> weekly = Arrays.asList(new WeeklyTimeWindow[] {
				new WeeklyTimeWindow(WeeklyTimeWindow.MONDAY, 2 * WeeklyTimeWindow.DAY),
				new WeeklyTimeWindow(WeeklyTimeWindow.FRIDAY, WeeklyTimeWindow.DAY)
		});
		
		final StatusIteratorTester tester = new StatusIteratorTester(
				new WeeklyTimeWindowsIterator(cal, weekly), cal);
		
		for (int i = 0; i < 100; ++i) {
			tester.assertNextStatus(Status.STATUS_AVAILABLE, Calendar.DAY_OF_MONTH, 2);
			tester.assertNextStatus(Status.STATUS_UNAVAILABLE, Calendar.DAY_OF_MONTH, 2);
			tester.assertNextStatus(Status.STATUS_AVAILABLE, Calendar.DAY_OF_MONTH, 1);
			tester.assertNextStatus(Status.STATUS_UNAVAILABLE, Calendar.DAY_OF_MONTH, 2);
		}
	}

    @Test
    public void testDstStart() {
	    // Clock Changes in São Paulo, São Paulo, Brazil in 2017
        //   When local standard time is about to reach
        //   Sunday, October 15, 2017, 00:00:00 clocks are turned forward 1 hour to
        //   Sunday, October 15, 2017, 01:00:00 local daylight time instead.
        final Calendar today = calendar(americaSaoPaulo,2017, Calendar.OCTOBER, 14, 0, 0, 0);
        final Calendar tomorrow = lenientCalendar(americaSaoPaulo,2017, Calendar.OCTOBER, 15, 0, 0, 0);
        final Calendar alsoTomorrow = calendar(americaSaoPaulo,2017, Calendar.OCTOBER, 15, 1, 0, 0);
        final Calendar dayAfterTomorrow = calendar(americaSaoPaulo,2017, Calendar.OCTOBER, 16, 0, 0, 0);

        // Sanity: verify DST start
        assertEquals(tomorrow.getTime(), alsoTomorrow.getTime());

        final WeeklyTimeWindowsIterator it = new WeeklyTimeWindowsIterator(today, Arrays.asList(new WeeklyTimeWindow[] {
                new WeeklyTimeWindow(WeeklyTimeWindow.MONDAY, 6 * WeeklyTimeWindow.DAY)
        }));

        assertTrue(it.hasNext());
        final Status status1 = it.next();
        assertEquals(Status.STATUS_AVAILABLE, status1.status);
        assertEquals(tomorrow.getTimeInMillis(), status1.until.longValue());

        assertTrue(it.hasNext());
        final Status status2 = it.next();
        assertEquals(Status.STATUS_UNAVAILABLE, status2.status);
        assertEquals(dayAfterTomorrow.getTimeInMillis(), status2.until.longValue());
    }

	@Test
	public void testConsecutiveWindows() {
        final Calendar cal = calendar(asiaJerusalem, 2010, Calendar.DECEMBER, 15, 0, 0, 0);
		final List<WeeklyTimeWindow> weekly = Arrays.asList(new WeeklyTimeWindow[] {
				new WeeklyTimeWindow(WeeklyTimeWindow.SUNDAY, 1 * WeeklyTimeWindow.DAY),
				new WeeklyTimeWindow(WeeklyTimeWindow.MONDAY, 6 * WeeklyTimeWindow.DAY)
		});

		final StatusIteratorTester tester = new StatusIteratorTester(
				new WeeklyTimeWindowsIterator(cal, weekly), cal);

        tester.assertLastStatus(Status.STATUS_AVAILABLE);
        tester.assertDone();
	}
}

