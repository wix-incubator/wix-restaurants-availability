package com.wix.restaurants.availability;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

public class DateTimeWindowsIterator implements Iterator<Status> {
	private final TimeZone tz;
	private final Index index;
	
	public DateTimeWindowsIterator(Calendar cal, List<DateTimeWindow> timeWindows) {
		if (timeWindows == null) {
			timeWindows = Collections.emptyList();
		}
		
		tz = cal.getTimeZone();
		this.timeWindows = timeWindows;
		
		if (!timeWindows.isEmpty()) {
			final DateTimeWindow timeWindow = DateTimeWindow.create(cal, null);
			final int searchIndex = Collections.binarySearch(timeWindows, timeWindow,
					new Comparator<DateTimeWindow>() {
				@Override
				public int compare(DateTimeWindow o1, DateTimeWindow o2) {
					if (!o1.end(tz).after(o2.start(tz))) {
						return -1;
					}
					if (!o2.end(tz).after(o1.start(tz))) {
						return 1;
					}
					return 0;
				}
			});
			
			if (searchIndex >= 0) {
				index = new Index(searchIndex, false);
			} else {
				final int insertionIndex = -searchIndex - 1;
				index = new Index(insertionIndex, true);
			}
		} else {
			index = new Index(0, true);
		}
	}

	@Override
	public boolean hasNext() {
		return ((index.isDummyBefore()) || (index.index() < timeWindows.size()));
	}

	@Override
	public Status next() {
		try {
			if (index.index() == timeWindows.size()) {
				return Status.UNKNOWN;
			}
			
			final DateTimeWindow nextTimeWindow = timeWindows.get(index.index());
			if (!index.isDummyBefore) {
				return new Status(
						(nextTimeWindow.available.booleanValue() ? Status.STATUS_AVAILABLE : Status.STATUS_UNAVAILABLE),
						nextTimeWindow.end(tz).getTime(), nextTimeWindow.reason, nextTimeWindow.comment);
			} else {
				return new Status(Status.STATUS_UNKNOWN, nextTimeWindow.start(tz).getTime());
			}
		} finally {
			index.advance();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove unsupported");
	}
	
	private final List<DateTimeWindow> timeWindows;
	
	private class Index {
		public Index(int index, boolean isDummyBefore) {
			this.index = index;
			this.isDummyBefore = isDummyBefore;
		}
		public int index() {
			return index;
		}
		public boolean isDummyBefore() {
			return isDummyBefore;
		}
		public void advance() {
			if (isDummyBefore) {
				isDummyBefore = false;
			} else {
				isDummyBefore = true;
				++index;
			}
		}
		private int index;
		private boolean isDummyBefore;
	}
}
