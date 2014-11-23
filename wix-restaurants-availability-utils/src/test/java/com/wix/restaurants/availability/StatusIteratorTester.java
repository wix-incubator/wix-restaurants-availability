package com.wix.restaurants.availability;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Iterator;

public class StatusIteratorTester {
	public StatusIteratorTester(Iterator<Status> it, Calendar cal) {
		this.it = it;
		this.cal = ((cal != null) ? ((Calendar) cal.clone()) : (null));
	}
	
	public StatusIteratorTester(Iterator<Status> it) {
		this(it, null);
	}
	
	public void assertNextStatus(String status, int field, int amount) {
		assertTrue(it.hasNext());
		final Status actualStatus = it.next();
		assertEquals(status, actualStatus.status);
		assertNotNull(actualStatus.until);
		cal.add(field, amount);
		assertEquals(cal.getTime(), actualStatus.until());
	}
	
	public void assertLastStatus(String status) {
		assertTrue(it.hasNext());
		final Status actualStatus = it.next();
		assertEquals(status, actualStatus.status);
		assertNull(actualStatus.until);
	}
	
	public void assertDone() {
		assertFalse(it.hasNext());
	}
	
	private final Iterator<Status> it;
	private final Calendar cal;
}
