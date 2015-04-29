package com.wix.restaurants.availability;

import java.util.Calendar;
import java.util.Iterator;

public class AvailabilityIterator implements Iterator<Status> {
    private final MergingStatusIterator it;

    public AvailabilityIterator(Calendar cal, Availability availability) {
        it = new MergingStatusIterator(new TimeWindowsIterator(cal, availability));
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
}
