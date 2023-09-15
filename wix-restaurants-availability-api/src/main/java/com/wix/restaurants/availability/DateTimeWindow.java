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

    /** Start setter for JSON deserialization. */
    public void setStart(Date start) {
        this.start = start;
    }

    /** End setter for JSON deserialization. */
    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
	public DateTimeWindow clone() {
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
			cloned.add((window != null) ? window.clone() : null);
		}
		return cloned;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DateTimeWindow that = (DateTimeWindow) o;
		return Objects.equals(start, that.start) &&
				Objects.equals(end, that.end) &&
				Objects.equals(available, that.available) &&
				Objects.equals(reason, that.reason) &&
				Objects.equals(comment, that.comment);
	}

	@Override
	public int hashCode() {
		return Objects.hash(start, end, available, reason, comment);
	}

    @Override
    public String toString() {
        return "DateTimeWindow{" +
                "start=" + start +
                ", end=" + end +
                ", available=" + available +
                ", reason='" + reason + '\'' +
                ", comment=" + comment +
                '}';
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
}
