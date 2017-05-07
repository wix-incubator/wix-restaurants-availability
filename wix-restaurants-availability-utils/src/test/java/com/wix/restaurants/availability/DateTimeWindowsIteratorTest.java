package com.wix.restaurants.availability;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;

public class DateTimeWindowsIteratorTest {
	private static final Calendar cal = Calendar.getInstance();
	
	@Before
	public void setUp() {
		cal.setLenient(false);
		cal.clear();
	}

	@Test
	public void testNull() {
		final StatusIteratorTester tester = new StatusIteratorTester(
				new DateTimeWindowsIterator(cal, null));
		
		tester.assertLastStatus(Status.STATUS_UNKNOWN);
		tester.assertDone();
	}
	
	@Test
	public void testEmpty() {
		@SuppressWarnings("unchecked")
		final StatusIteratorTester tester = new StatusIteratorTester(
				new DateTimeWindowsIterator(cal, Collections.EMPTY_LIST));
		
		tester.assertLastStatus(Status.STATUS_UNKNOWN);
		tester.assertDone();
	}

	@Test
	public void testOneWindowSinceForever() {
		cal.set(2010, Calendar.DECEMBER, 12, 0, 0, 0);
		final List<DateTimeWindow> timeWindows = Arrays.asList(new DateTimeWindow[] {
				when(cal, Calendar.DAY_OF_MONTH, 1, Boolean.TRUE)
		});
		timeWindows.get(0).start = null;

		final StatusIteratorTester tester = new StatusIteratorTester(
				new DateTimeWindowsIterator(cal, timeWindows), cal);

		tester.assertNextStatus(Status.STATUS_AVAILABLE, Calendar.DAY_OF_MONTH, 1);
		tester.assertLastStatus(Status.STATUS_UNKNOWN);
		tester.assertDone();
	}

	@Test
	public void testOneWindowUntilForever() {
		cal.set(2010, Calendar.DECEMBER, 12, 0, 0, 0);
		final List<DateTimeWindow> timeWindows = Arrays.asList(new DateTimeWindow[] {
				when(cal, Calendar.DAY_OF_MONTH, 1, Boolean.TRUE)
		});
		timeWindows.get(0).end = null;

		final StatusIteratorTester tester = new StatusIteratorTester(
				new DateTimeWindowsIterator(cal, timeWindows), cal);

		tester.assertLastStatus(Status.STATUS_AVAILABLE);
		tester.assertDone();
	}

	@Test
	public void testOneWindowSinceForeverAndUntilForever() {
		cal.set(2010, Calendar.DECEMBER, 12, 0, 0, 0);
		final List<DateTimeWindow> timeWindows = Arrays.asList(new DateTimeWindow[] {
				new DateTimeWindow((Date)null, null, Boolean.TRUE)
		});

		final StatusIteratorTester tester = new StatusIteratorTester(
				new DateTimeWindowsIterator(cal, timeWindows), cal);

		tester.assertLastStatus(Status.STATUS_AVAILABLE);
		tester.assertDone();
	}

	@Test
	public void testImmediateOneDay() {
		cal.set(2010, Calendar.DECEMBER, 12, 0, 0, 0);
		final List<DateTimeWindow> timeWindows = Arrays.asList(new DateTimeWindow[] {
				when(cal, Calendar.DAY_OF_MONTH, 1, Boolean.TRUE)
		});
		
		final StatusIteratorTester tester = new StatusIteratorTester(
				new DateTimeWindowsIterator(cal, timeWindows), cal);
		
		tester.assertNextStatus(Status.STATUS_AVAILABLE, Calendar.DAY_OF_MONTH, 1);
		tester.assertLastStatus(Status.STATUS_UNKNOWN);
		tester.assertDone();
	}

	@Test
	public void testFutureOneDay() {
		cal.set(2010, Calendar.DECEMBER, 13, 0, 0, 0);
		final List<DateTimeWindow> timeWindows = Arrays.asList(new DateTimeWindow[] {
				when(cal, Calendar.DAY_OF_MONTH, 1, Boolean.FALSE)
		});
		
		cal.add(Calendar.DAY_OF_YEAR, -1);
		final StatusIteratorTester tester = new StatusIteratorTester(
				new DateTimeWindowsIterator(cal, timeWindows), cal);

		tester.assertNextStatus(Status.STATUS_UNKNOWN, Calendar.DAY_OF_MONTH, 1);
		tester.assertNextStatus(Status.STATUS_UNAVAILABLE, Calendar.DAY_OF_MONTH, 1);
		tester.assertLastStatus(Status.STATUS_UNKNOWN);
		tester.assertDone();
	}
	
	@Test
	public void testStartMidWindow() {
		cal.set(2010, Calendar.DECEMBER, 13, 0, 0, 0);
		final List<DateTimeWindow> timeWindows = Arrays.asList(new DateTimeWindow[] {
				when(cal, Calendar.DAY_OF_MONTH, 1, Boolean.TRUE)
		});

		cal.add(Calendar.HOUR_OF_DAY, 12);
		final StatusIteratorTester tester = new StatusIteratorTester(
				new DateTimeWindowsIterator(cal, timeWindows), cal);

		tester.assertNextStatus(Status.STATUS_AVAILABLE, Calendar.HOUR_OF_DAY, 12);
	}
	
	@Test
	public void testThreeDays() {
		cal.set(2010, Calendar.DECEMBER, 11, 0, 0, 0);
		final Calendar cal2 = (Calendar) cal.clone();
		cal2.set(2010, Calendar.DECEMBER, 13, 0, 0, 0);
		final Calendar cal3 = (Calendar) cal.clone();
		cal3.set(2010, Calendar.DECEMBER, 15, 0, 0, 0);
		
		final List<DateTimeWindow> timeWindows = Arrays.asList(new DateTimeWindow[] {
				when(cal, Calendar.DAY_OF_MONTH, 1, Boolean.TRUE),
				when(cal2, Calendar.DAY_OF_MONTH, 1, Boolean.FALSE),
				when(cal3, Calendar.DAY_OF_MONTH, 1, Boolean.TRUE)
		});
		
		cal.add(Calendar.DAY_OF_MONTH, 1);
		final StatusIteratorTester tester = new StatusIteratorTester(
				new DateTimeWindowsIterator(cal, timeWindows), cal);

		tester.assertNextStatus(Status.STATUS_UNKNOWN, Calendar.DAY_OF_MONTH, 1);
		tester.assertNextStatus(Status.STATUS_UNAVAILABLE, Calendar.DAY_OF_MONTH, 1);
		tester.assertNextStatus(Status.STATUS_UNKNOWN, Calendar.DAY_OF_MONTH, 1);
		tester.assertNextStatus(Status.STATUS_AVAILABLE, Calendar.DAY_OF_MONTH, 1);
		tester.assertLastStatus(Status.STATUS_UNKNOWN);
		tester.assertDone();
	}

	@Test
	public void testSupportsOverlappingWindowsByLastOneWinsRule() {
		final Calendar yesterday = (Calendar) cal.clone();
		yesterday.set(2010, Calendar.DECEMBER, 12, 0, 0, 0);

		final Calendar today = (Calendar) cal.clone();
		today.set(2010, Calendar.DECEMBER, 13, 0, 0, 0);

		final Calendar tomorrow = (Calendar) cal.clone();
		tomorrow.set(2010, Calendar.DECEMBER, 14, 0, 0, 0);

        final List<DateTimeWindow> timeWindows = Arrays.asList(new DateTimeWindow[] {
                when(today, Calendar.DAY_OF_MONTH, 2, Boolean.FALSE),
                when(tomorrow, Calendar.DAY_OF_MONTH, 1, Boolean.TRUE)
        });


		final StatusIteratorTester tester = new StatusIteratorTester(
                new DateTimeWindowsIterator(yesterday, timeWindows), yesterday);

        tester.assertNextStatus(Status.STATUS_UNKNOWN, Calendar.DAY_OF_MONTH, 1);
        tester.assertNextStatus(Status.STATUS_UNAVAILABLE, Calendar.DAY_OF_MONTH, 1);
        tester.assertNextStatus(Status.STATUS_UNKNOWN, Calendar.DAY_OF_MONTH, 0); // TODO: This shouldn't be returned (we chose not to deal with it now as it doesn't really matter)
		tester.assertNextStatus(Status.STATUS_AVAILABLE, Calendar.DAY_OF_MONTH, 1);
		tester.assertLastStatus(Status.STATUS_UNKNOWN);
		tester.assertDone();
	}

	@Test
	public void testPerformsWellWhenGivenManyPastExceptions() {
	    final int numWindows = 100000;

		final Calendar today = (Calendar) cal.clone();
		today.set(2010, Calendar.DECEMBER, 13, 0, 0, 0);

		final Calendar yesterday = (Calendar) today.clone();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);

		final List<DateTimeWindow> timeWindows = new ArrayList<>(numWindows);
		for (int i = -numWindows; i <= -1; ++i) {
			final Calendar start = (Calendar) today.clone();
			start.add(Calendar.DAY_OF_MONTH, i);

			timeWindows.add(when(start, Calendar.DAY_OF_MONTH, 1, false));
		}

        final long before = System.currentTimeMillis();
        {
            final StatusIteratorTester tester = new StatusIteratorTester(
                    new DateTimeWindowsIterator(yesterday, timeWindows), yesterday);

            tester.assertNextStatus(Status.STATUS_UNAVAILABLE, Calendar.DAY_OF_MONTH, 1);
            tester.assertLastStatus(Status.STATUS_UNKNOWN);
            tester.assertDone();
        }
		final long after = System.currentTimeMillis();

        assertTrue(after - before < 1000);
	}

	private static DateTimeWindow when(Calendar start, int field, int amount, Boolean available) {
		final Calendar end = (Calendar) start.clone();
		end.add(field, amount);
		return new DateTimeWindow(start, end, available);
	}
}
