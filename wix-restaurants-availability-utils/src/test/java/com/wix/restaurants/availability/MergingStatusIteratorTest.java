package com.wix.restaurants.availability;

//import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

public class MergingStatusIteratorTest {
	private final Calendar cal = Calendar.getInstance();
	
	@Test
	public void testOne() {
		final List<Status> statuses = Arrays.asList(new Status[] {
				new Status(Status.STATUS_AVAILABLE, null)
		});
		
		final StatusIteratorTester tester = new StatusIteratorTester(
				new MergingStatusIterator(statuses.iterator()));
		
		tester.assertLastStatus(Status.STATUS_AVAILABLE);
		tester.assertDone();
	}
	
	@Test
	public void testTwoDifferent() {
		cal.set(2010, Calendar.DECEMBER, 15, 0, 0, 0);
		final List<Status> statuses = Arrays.asList(new Status[] {
				new Status(Status.STATUS_UNAVAILABLE, cal.getTime()),
				new Status(Status.STATUS_AVAILABLE, null),
		});
		
		final StatusIteratorTester tester = new StatusIteratorTester(
				new MergingStatusIterator(statuses.iterator()), cal);
		
		tester.assertNextStatus(Status.STATUS_UNAVAILABLE, Calendar.DAY_OF_YEAR, 0);
		tester.assertLastStatus(Status.STATUS_AVAILABLE);
		tester.assertDone();
	}
	
	@Test
	public void testTwoSame() {
		cal.set(2010, Calendar.DECEMBER, 15, 0, 0, 0);
		final List<Status> statuses = Arrays.asList(new Status[] {
				new Status(Status.STATUS_AVAILABLE, cal.getTime()),
				new Status(Status.STATUS_AVAILABLE, null),
		});
		
		final StatusIteratorTester tester = new StatusIteratorTester(
				new MergingStatusIterator(statuses.iterator()), cal);
		
		tester.assertLastStatus(Status.STATUS_AVAILABLE);
		tester.assertDone();
	}
}
