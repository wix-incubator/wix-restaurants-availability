package com.wix.restaurants.availability;

import java.util.Calendar;
import java.util.Iterator;

public class TimeWindowsIterator implements Iterator<Status> {
	private final MergingStatusIterator it;
	
	public TimeWindowsIterator(Calendar cal, Availability availability) {
		it = new MergingStatusIterator(new TimeWindowsIteratorImpl(cal, availability)); 
	}
	
	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public Status next() {
		return it.next();
	}

	@Override
	public void remove() {
		it.remove();
	}
	
	private class TimeWindowsIteratorImpl implements Iterator<Status> {
		private final WeeklyTimeWindowsIterator regularIt;
		private final DateTimeWindowsIterator exceptionsIt;
		
		private Status regularStatus;
		private Status exceptionStatus;
		private boolean hasNext = true;
		
		public TimeWindowsIteratorImpl(Calendar cal, Availability availability) {
			this.regularIt = new WeeklyTimeWindowsIterator(cal, availability.weekly);
			this.exceptionsIt = new DateTimeWindowsIterator(cal, availability.exceptions);
			
			// TimeWindow iterators always return at least one element
			regularStatus = regularIt.next();
			exceptionStatus = exceptionsIt.next();
		}
		
		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public Status next() {
			// Future has no exceptions?
			if (exceptionStatus.until == null) {
				// Continue with regular statuses
				final Status lastRegularStatus = regularStatus;
				if (regularIt.hasNext()) {
					regularStatus = regularIt.next();
				} else {
					hasNext = false;
				}
				return lastRegularStatus;
			}
			
			// So we do have real exceptions to deal with
			
			// Real exceptions take precedent
			if (!Status.STATUS_UNKNOWN.equals(exceptionStatus.status)) {
				final Status lastExceptionStatus = exceptionStatus;				
				exceptionStatus = exceptionsIt.next(); // we know there are still real exceptions later
				
				while ((regularStatus.until != null) &&
						(!regularStatus.until().after(lastExceptionStatus.until()))) {
					regularStatus = regularIt.next();
				}
				
				return lastExceptionStatus;
			}
			
			// No real exception this time
			if ((regularStatus.until == null) ||
					(regularStatus.until().after(exceptionStatus.until()))) {
				final Status lastExceptionStatus = exceptionStatus;				
				exceptionStatus = exceptionsIt.next(); // we know there are still real exceptions later
				return new Status(regularStatus.status, lastExceptionStatus.until());
			} else if (regularStatus.until().before(exceptionStatus.until())) {
				final Status lastRegularStatus = regularStatus;
				regularStatus = regularIt.next(); // we know there are still regular statuses later
				return lastRegularStatus;
			} else {
				exceptionStatus = exceptionsIt.next(); // we know there are still real exceptions later
				final Status lastRegularStatus = regularStatus;
				regularStatus = regularIt.next(); // we know there are still regular statuses later
				return lastRegularStatus;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Remove unsupported");
		}
	}
}
