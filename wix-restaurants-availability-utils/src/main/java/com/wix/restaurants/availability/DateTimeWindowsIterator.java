package com.wix.restaurants.availability;

import java.util.*;

public class DateTimeWindowsIterator implements Iterator<Status> {
	private final TimeZone tz;
	private final Index index;
	private final boolean lastWindowUntilForever;
	
	public DateTimeWindowsIterator(Calendar cal, List<DateTimeWindow> timeWindows) {
		if (timeWindows == null) {
			timeWindows = Collections.emptyList();
		}
		
		tz = cal.getTimeZone();
		this.timeWindows = DateTimeWindowsUtils.normalize(timeWindows);
		
		if (!this.timeWindows.isEmpty()) {
			final DateTimeWindow timeWindow = DateTimeWindow.create(cal, null);
			final int searchIndex = Collections.binarySearch(this.timeWindows, timeWindow, new DateTimeWindowComparator(tz));

			if (searchIndex >= 0) {
				index = new Index(searchIndex, false);
			} else {
				final int insertionIndex = -searchIndex - 1;
				index = new Index(insertionIndex, true);
			}
			lastWindowUntilForever = (this.timeWindows.get(this.timeWindows.size() - 1).end == null);
		} else {
			index = new Index(0, true);
			lastWindowUntilForever = false;
		}
	}

	private static java.util.Date toJavaDate(Calendar cal) {
		return ((cal != null) ? cal.getTime() : null);
	}

	@Override
	public boolean hasNext() {
		if (index.index() < timeWindows.size()) {
			return true;
		}

		return (index.isDummyBefore() && !lastWindowUntilForever);
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
						(nextTimeWindow.available ? Status.STATUS_AVAILABLE : Status.STATUS_UNAVAILABLE),
						toJavaDate(nextTimeWindow.end(tz)), nextTimeWindow.reason, nextTimeWindow.comment);
			} else {
				return new Status(Status.STATUS_UNKNOWN, toJavaDate(nextTimeWindow.start(tz)));
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
