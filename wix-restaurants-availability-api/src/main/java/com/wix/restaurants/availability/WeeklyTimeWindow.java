package com.wix.restaurants.availability;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeeklyTimeWindow implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
    
	public static final int HOUR = 60;
	public static final int DAY = HOUR * 24;
	public static final int SUNDAY = 0;
	public static final int MONDAY = SUNDAY + DAY;
	public static final int TUESDAY = MONDAY + DAY;
	public static final int WEDNESDAY = TUESDAY + DAY;
	public static final int THURSDAY = WEDNESDAY + DAY;
	public static final int FRIDAY = THURSDAY + DAY;
	public static final int SATURDAY = FRIDAY + DAY;
	public static final int MINUTES_IN_WEEK = SATURDAY + DAY; 
    
	/** Default constructor for JSON deserialization. */
    public WeeklyTimeWindow() {}
    
    public WeeklyTimeWindow(Integer minuteOfWeek, Integer durationMins) {
    	this.minuteOfWeek = minuteOfWeek;
    	this.durationMins = durationMins;
    }
    
    @Override
	public Object clone() {
    	return new WeeklyTimeWindow(minuteOfWeek, durationMins);
	}

	public static List<WeeklyTimeWindow> clone(List<WeeklyTimeWindow> windows) {
		if (windows == null) {
			return null;
		}

		final List<WeeklyTimeWindow> cloned = new ArrayList<>(windows.size());
		for (WeeklyTimeWindow window : windows) {
			cloned.add((window != null) ? (WeeklyTimeWindow) window.clone() : null);
		}
		return cloned;
	}


    @JsonInclude(Include.NON_NULL)
    public Integer minuteOfWeek;
    
    @JsonInclude(Include.NON_NULL)
    public Integer durationMins;
    
    public int endMinuteOfWeek() {
    	return minuteOfWeek + durationMins;
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((durationMins == null) ? 0 : durationMins.hashCode());
		result = prime * result
				+ ((minuteOfWeek == null) ? 0 : minuteOfWeek.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WeeklyTimeWindow other = (WeeklyTimeWindow) obj;
		if (durationMins == null) {
			if (other.durationMins != null)
				return false;
		} else if (!durationMins.equals(other.durationMins))
			return false;
		if (minuteOfWeek == null) {
			if (other.minuteOfWeek != null)
				return false;
		} else if (!minuteOfWeek.equals(other.minuteOfWeek))
			return false;
		return true;
	}
}
