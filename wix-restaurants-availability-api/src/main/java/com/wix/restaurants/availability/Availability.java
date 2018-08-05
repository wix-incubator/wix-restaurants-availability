package com.wix.restaurants.availability;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 * A time availability schedule.
 * Supports regular weekly periods and specific date exceptions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Availability implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
    
	public Availability(List<WeeklyTimeWindow> weekly, List<DateTimeWindow> exceptions) {
        this.weekly = weekly;
        this.exceptions = exceptions;
    }
    
    /** Default constructor for JSON deserialization. */
    public Availability() {}
    
    @Override
	public Availability clone() {
		return new Availability(WeeklyTimeWindow.clone(weekly), DateTimeWindow.clone(exceptions));
	}

	public static Map<String, Availability> clone(Map<String, Availability> availabilities) {
		if (availabilities == null) {
			return null;
		}

		final Map<String, Availability> cloned = new LinkedHashMap<>(availabilities.size());
		for (Entry<String, Availability> entry : availabilities.entrySet()) {
			cloned.put(entry.getKey(), ((entry.getValue() != null) ? entry.getValue().clone() : null));
		}
		return cloned;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Availability that = (Availability) o;
        return Objects.equals(weekly, that.weekly) &&
                Objects.equals(exceptions, that.exceptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weekly, exceptions);
    }

    @Override
    public String toString() {
        return "Availability{" +
                "weekly=" + weekly +
                ", exceptions=" + exceptions +
                '}';
    }

    /** Weekly availability times. */
    @JsonInclude(Include.NON_DEFAULT)
    public List<WeeklyTimeWindow> weekly = new LinkedList<>();
    
    /** Availability exceptions. */
    @JsonInclude(Include.NON_DEFAULT)
    public List<DateTimeWindow> exceptions = new LinkedList<>();
}
