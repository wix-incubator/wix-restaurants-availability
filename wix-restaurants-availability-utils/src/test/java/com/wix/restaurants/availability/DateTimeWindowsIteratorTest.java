package com.wix.restaurants.availability;

//import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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
	
	private static DateTimeWindow when(Calendar start, int field, int amount, Boolean available) {
		final Calendar end = (Calendar) start.clone();
		end.add(field, amount);
		return new DateTimeWindow(start, end, available);
	}
}
