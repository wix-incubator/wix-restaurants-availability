package com.wix.restaurants.availability;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
	public WeeklyTimeWindow clone() {
    	return new WeeklyTimeWindow(minuteOfWeek, durationMins);
	}

	public static List<WeeklyTimeWindow> clone(List<WeeklyTimeWindow> windows) {
		if (windows == null) {
			return null;
		}

		final List<WeeklyTimeWindow> cloned = new ArrayList<>(windows.size());
		for (WeeklyTimeWindow window : windows) {
			cloned.add((window != null) ? window.clone() : null);
		}
		return cloned;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WeeklyTimeWindow that = (WeeklyTimeWindow) o;
		return Objects.equals(minuteOfWeek, that.minuteOfWeek) &&
				Objects.equals(durationMins, that.durationMins);
	}

	@Override
	public int hashCode() {
		return Objects.hash(minuteOfWeek, durationMins);
	}

    @Override
    public String toString() {
        return "WeeklyTimeWindow{" +
                "minuteOfWeek=" + minuteOfWeek +
                ", durationMins=" + durationMins +
                '}';
    }

    @JsonInclude(Include.NON_NULL)
    public Integer minuteOfWeek;
    
    @JsonInclude(Include.NON_NULL)
    public Integer durationMins;
    
    public int endMinuteOfWeek() {
    	return minuteOfWeek + durationMins;
    }
}
