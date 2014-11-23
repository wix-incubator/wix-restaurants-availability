package com.wix.restaurants.availability;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
	public Object clone() {
    	final List<WeeklyTimeWindow> clonedWeekly;
    	if (weekly != null) {
    		clonedWeekly = new ArrayList<WeeklyTimeWindow>(weekly.size());
    		for (WeeklyTimeWindow w : weekly) {
    			clonedWeekly.add((WeeklyTimeWindow) w.clone());
    		}
    	} else {
    		clonedWeekly = null;
    	}
    	
    	final List<DateTimeWindow> clonedExceptions;
    	if (exceptions != null) {
    		clonedExceptions = new ArrayList<DateTimeWindow>(exceptions.size());
    		for (DateTimeWindow w : exceptions) {
    			clonedExceptions.add((DateTimeWindow) w.clone());
    		}
    	} else {
    		clonedExceptions = null;
    	}
    	
    	return new Availability(clonedWeekly, clonedExceptions);
	}
    
    /** Weekly availability times. */
    @JsonInclude(Include.NON_DEFAULT)
    public List<WeeklyTimeWindow> weekly = Collections.emptyList();
    
    /** Availability exceptions. */
    @JsonInclude(Include.NON_DEFAULT)
    public List<DateTimeWindow> exceptions = Collections.emptyList();
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((exceptions == null) ? 0 : exceptions.hashCode());
		result = prime * result + ((weekly == null) ? 0 : weekly.hashCode());
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
		Availability other = (Availability) obj;
		if (exceptions == null) {
			if (other.exceptions != null)
				return false;
		} else if (!exceptions.equals(other.exceptions))
			return false;
		if (weekly == null) {
			if (other.weekly != null)
				return false;
		} else if (!weekly.equals(other.weekly))
			return false;
		return true;
	}
}
