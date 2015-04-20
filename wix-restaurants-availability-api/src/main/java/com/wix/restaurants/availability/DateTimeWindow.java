package com.wix.restaurants.availability;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DateTimeWindow implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
    
	/** Generic reason, e.g. item out of stock. */
	public static final String REASON_TEMPORARY = "temporary";
	
	public DateTimeWindow(Date start, Date end, Boolean available, String reason, Map<String, String> comment) {
        this.start = start;
        this.end = end;
        this.available = available;
        this.reason = reason;
        this.comment = comment;
    }
	
	/**
	 * Constructor for momentary absolute (exceptional) time windows.
	 * You generally shouldn't use this one.
	 */
	public static DateTimeWindow create(Calendar start, Boolean available) {
		final Calendar end = (Calendar) start.clone();
		end.add(Calendar.MINUTE, 1);
		
		return new DateTimeWindow(start, end, available);
	}
	
	public DateTimeWindow(Date start, Date end, Boolean available) {
		this(start, end, available, null, new HashMap<String, String>());
    }
    
    public DateTimeWindow(Calendar start, Calendar end, Boolean available) {
        this(new Date(start), new Date(end), available);
    }

    /** Default constructor for JSON deserialization. */
    public DateTimeWindow() {}
    
    @Override
	protected Object clone() {
    	return new DateTimeWindow(
    			((start != null) ? (Date) start.clone() : null),
    			((end != null) ? (Date) end.clone() : null),
    			available,
    			reason,
    			((comment != null) ? new HashMap<>(comment) : null));
	}

	public static List<DateTimeWindow> clone(List<DateTimeWindow> windows) {
		if (windows == null) {
			return null;
		}

		final List<DateTimeWindow> cloned = new ArrayList<>(windows.size());
		for (DateTimeWindow window : windows) {
			cloned.add((window != null) ? (DateTimeWindow) window.clone() : null);
		}
		return cloned;
	}

    public Calendar start(TimeZone tz) {
        return ((start != null) ? start.calendar(tz) : null);
    }

    public Calendar end(TimeZone tz) {
        return ((end != null) ? end.calendar(tz) : null);
    }

	/** Start date, inclusive (null means "since forever"). */
    @JsonInclude(Include.NON_NULL)
    public Date start;

	/** End date, exclusive (null means "until forever"). */
    @JsonInclude(Include.NON_NULL)
    public Date end;

    @JsonInclude(Include.NON_NULL)
    public Boolean available;
    
    /** See possible reasons above. */
    @JsonInclude(Include.NON_NULL)
    public String reason;
    
    /** Additional reason information (localized free-text). */
    @JsonInclude(Include.NON_DEFAULT)
    public Map<String, String> comment = new LinkedHashMap<>();
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((available == null) ? 0 : available.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((reason == null) ? 0 : reason.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		DateTimeWindow other = (DateTimeWindow) obj;
		if (available == null) {
			if (other.available != null)
				return false;
		} else if (!available.equals(other.available))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (reason == null) {
			if (other.reason != null)
				return false;
		} else if (!reason.equals(other.reason))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}
}
