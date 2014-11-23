package com.wix.restaurants.availability;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class WeeklyTimeWindowsIterator implements Iterator<Status> {
	private final Calendar cal;
	private final List<WeeklyTimeWindowPlus> timeWindows = new ArrayList<WeeklyTimeWindowPlus>();
	
	private final boolean isConstant;
	private final boolean isFirstAndLastSame;
	private boolean hasNext = true;
	
	private class WeeklyTimeWindowPlus extends WeeklyTimeWindow {
		public WeeklyTimeWindowPlus(int minuteOfWeek, int durationMins, String status) {
			this.minuteOfWeek = minuteOfWeek;
			this.durationMins = durationMins;
			this.status = status;
		}
		public String status() {
			return status;
		}
		private final String status;
		private static final long serialVersionUID = 1L;
	}
	
	public WeeklyTimeWindowsIterator(Calendar cal, List<WeeklyTimeWindow> weekly) {
		if (weekly == null) {
			weekly = Collections.emptyList();
		}
		this.cal = (Calendar) cal.clone();
		
		if (weekly.isEmpty()) {
			timeWindows.add(new WeeklyTimeWindowPlus(
					0, WeeklyTimeWindow.MINUTES_IN_WEEK, Status.STATUS_AVAILABLE));
			isFirstAndLastSame = true;
		} else {
			int minuteOfWeek = 0;
			for (WeeklyTimeWindow timeWindow : weekly) {
				if (timeWindow.minuteOfWeek.intValue() > minuteOfWeek) {
					timeWindows.add(new WeeklyTimeWindowPlus(minuteOfWeek,
							timeWindow.minuteOfWeek.intValue() - minuteOfWeek,
							Status.STATUS_UNAVAILABLE));
				}
				timeWindows.add(new WeeklyTimeWindowPlus(timeWindow.minuteOfWeek.intValue(),
						timeWindow.durationMins.intValue(),
						Status.STATUS_AVAILABLE));
				minuteOfWeek = timeWindow.endMinuteOfWeek();
			}
			if (minuteOfWeek < WeeklyTimeWindow.MINUTES_IN_WEEK) {
				timeWindows.add(new WeeklyTimeWindowPlus(
						minuteOfWeek, WeeklyTimeWindow.MINUTES_IN_WEEK - minuteOfWeek, Status.STATUS_UNAVAILABLE));
			}
			
			final WeeklyTimeWindowPlus firstWindow = timeWindows.get(0);
			final WeeklyTimeWindowPlus lastWindow = timeWindows.get(timeWindows.size() - 1);
			isFirstAndLastSame = (firstWindow.status() == lastWindow.status());
		}

		isConstant = (timeWindows.size() == 1);
	}
	
	@Override
	public boolean hasNext() {
		return hasNext;
	}
	
	@Override
	public Status next() {
		if (isConstant) {
			hasNext = false;
			return new Status(timeWindows.get(0).status(), null);
		}
		
		final int minuteOfWeek = minutesFromStartOfWeek(cal);
		final WeeklyTimeWindowPlus currentWindow = timeWindows.get(find(minuteOfWeek));
		int newMinuteOfWeek = currentWindow.endMinuteOfWeek();
		if (newMinuteOfWeek == WeeklyTimeWindow.MINUTES_IN_WEEK) {
			if (isFirstAndLastSame) {
				newMinuteOfWeek = timeWindows.get(0).endMinuteOfWeek();
			} else {
				newMinuteOfWeek = 0;
			}
		}
		
		advanceCalender(minuteOfWeek, newMinuteOfWeek);
		return new Status(currentWindow.status(), cal.getTime());
	}
	
	private void advanceCalender(int oldMinuteOfWeek, int newMinuteOfWeek) {
		int minutesToAdvance = newMinuteOfWeek - oldMinuteOfWeek;
		if (minutesToAdvance < 0) {
			minutesToAdvance += WeeklyTimeWindow.MINUTES_IN_WEEK;
		}
		
		final int daysToAdvance = minutesToAdvance / WeeklyTimeWindow.DAY;
		minutesToAdvance -= (daysToAdvance * WeeklyTimeWindow.DAY);
		final int hoursToAdvance = minutesToAdvance / WeeklyTimeWindow.HOUR;
		minutesToAdvance -= (hoursToAdvance * WeeklyTimeWindow.HOUR);
		
		cal.add(Calendar.DAY_OF_MONTH, daysToAdvance);
		cal.add(Calendar.HOUR_OF_DAY, hoursToAdvance);
		cal.add(Calendar.MINUTE, minutesToAdvance);
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove unsupported");
	}
	
	private int find(int minuteOfWeek) {
		final WeeklyTimeWindow timeWindow = new WeeklyTimeWindow(minuteOfWeek, 1);
		final int index = Collections.binarySearch(timeWindows, timeWindow, new Comparator<WeeklyTimeWindow>() {
			@Override
			public int compare(WeeklyTimeWindow o1, WeeklyTimeWindow o2) {
				if (o1.endMinuteOfWeek() <= o2.minuteOfWeek) {
					return -1;
				}
				if (o2.endMinuteOfWeek() <= o1.minuteOfWeek) {
					return 1;
				}
				return 0;
			}
		});
		
		if (index < 0){
			throw new IllegalStateException("Unexpected weekly windows");
		}
		return index;
	}
	
	private static int minutesFromStartOfWeek(Calendar cal) {
		return (cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY) * WeeklyTimeWindow.DAY +
			cal.get(Calendar.HOUR_OF_DAY) * WeeklyTimeWindow.HOUR +
			cal.get(Calendar.MINUTE);
	}
}
