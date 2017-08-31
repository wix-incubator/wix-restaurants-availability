package com.wix.restaurants.availability;

//import static org.junit.Assert.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;

public class WeeklyTimeWindowsIteratorTest {

	@Before
	public void setUp() {
		cal.setLenient(false);
		cal.clear();
	}
	
	@Test
	public void testNull() {
		final StatusIteratorTester tester = new StatusIteratorTester(
				new WeeklyTimeWindowsIterator(cal, null));
		
		tester.assertLastStatus(Status.STATUS_AVAILABLE);
		tester.assertDone();
	}
	
	@Test
	public void testEmpty() {
		@SuppressWarnings("unchecked")
		final StatusIteratorTester tester = new StatusIteratorTester(
				new WeeklyTimeWindowsIterator(cal, Collections.EMPTY_LIST));
		
		tester.assertLastStatus(Status.STATUS_AVAILABLE);
		tester.assertDone();
	}

	@Test
	public void testSundayOnly() {
		cal.set(2010, Calendar.DECEMBER, 12, 0, 0, 0);
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
		cal.set(2010, Calendar.DECEMBER, 12, 0, 0, 30);
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
		cal.set(2010, Calendar.DECEMBER, 13, 0, 0, 0);
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
		cal.set(2010, Calendar.DECEMBER, 13, 12, 0, 0);
		final List<WeeklyTimeWindow> weekly = Arrays.asList(new WeeklyTimeWindow[] {
				new WeeklyTimeWindow(WeeklyTimeWindow.MONDAY, WeeklyTimeWindow.DAY)
		});
		
		final StatusIteratorTester tester = new StatusIteratorTester(
				new WeeklyTimeWindowsIterator(cal, weekly), cal);
		
		tester.assertNextStatus(Status.STATUS_AVAILABLE, Calendar.HOUR_OF_DAY, 12);
	}
	
	@Test
	public void testMondayTuesdayFriday() {
		cal.set(2010, Calendar.DECEMBER, 13, 0, 0, 0);
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
	public void testConsecutiveWindows() {
		cal.set(2010, Calendar.DECEMBER, 15, 0, 0, 0);
		final List<WeeklyTimeWindow> weekly = Arrays.asList(new WeeklyTimeWindow[] {
				new WeeklyTimeWindow(WeeklyTimeWindow.SUNDAY, 1 * WeeklyTimeWindow.DAY),
				new WeeklyTimeWindow(WeeklyTimeWindow.MONDAY, 6 * WeeklyTimeWindow.DAY)
		});

		final StatusIteratorTester tester = new StatusIteratorTester(
				new WeeklyTimeWindowsIterator(cal, weekly), cal);

        tester.assertLastStatus(Status.STATUS_AVAILABLE);
        tester.assertDone();
	}

	private static final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jerusalem"));
}

