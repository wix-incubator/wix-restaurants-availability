package com.wix.restaurants.availability;

import java.util.Iterator;

/**
 * Merges sequences of similar statuses to a single "long" status.
 */
public class MergingStatusIterator implements Iterator<Status> {
	private final Iterator<Status> it;
	private Status nextStatus;
	
	public MergingStatusIterator(Iterator<Status> it) {
		this.it = it;
		nextStatus = ((it.hasNext()) ? (it.next()) : null); 
	}
	
	@Override
	public boolean hasNext() {
		return (nextStatus != null);
	}
	
	@Override
	public Status next() {
		final Status mergedStatus = nextStatus;
		while (true) {
			if (!it.hasNext()) {
				nextStatus = null;
				break;
			}
			nextStatus = it.next();
			if (!nextStatus.equalsIgnoreUntil(mergedStatus)) {
				break;
			}
			mergedStatus.until = nextStatus.until;
		}
		return mergedStatus;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove unsupported");
	}
}
