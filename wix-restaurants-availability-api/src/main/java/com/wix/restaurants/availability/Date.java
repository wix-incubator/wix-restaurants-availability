package com.wix.restaurants.availability;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Date implements Serializable, Cloneable, Comparable<Date> {
	private static final long serialVersionUID = 1L;
    
	public Date(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public Date(Calendar cal) {
        this(cal.get(Calendar.YEAR), 1 + cal.get(Calendar.MONTH) - Calendar.JANUARY,
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE));
    }

    /** Default constructor for JSON deserialization. */
    public Date() {}

	@Override
	public Object clone() {
		return new Date(year, month, day, hour, minute);
	}
    
    public Calendar calendar(TimeZone tz) {
        final Calendar cal = Calendar.getInstance(tz);
        cal.clear();
        cal.set(year, Calendar.JANUARY + month - 1, day, hour, minute);
        return cal;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Date date = (Date) o;
		return Objects.equals(year, date.year) &&
				Objects.equals(month, date.month) &&
				Objects.equals(day, date.day) &&
				Objects.equals(hour, date.hour) &&
				Objects.equals(minute, date.minute);
	}

	@Override
	public int hashCode() {
		return Objects.hash(year, month, day, hour, minute);
	}

    @Override
	public String toString() {
    	return String.format("%04d-%02d-%02d %02d:%02d",
    			year, month, day, hour, minute);
	}
    
	private static int compare(Integer int1, Integer int2) {
		if (int1 != null) {
			return ((int2 != null) ? int1.compareTo(int2) : 1);
		} else {
			return ((int2 != null) ? -1 : 0);
		}
	}
	
	public static int compare(Date date1, Date date2) {
		if (date1 == null) {
			return ((date2 == null) ? 0 : 1);
		} else if (date2 == null) {
			return -1;
		}
		
		int result = compare(date1.year, date2.year);
		if (result == 0) {
			result = compare(date1.month, date2.month);
			if (result == 0) {
				result = compare(date1.day, date2.day);
				if (result == 0) {
					result = compare(date1.hour, date2.hour);
					if (result == 0) {
						result = compare(date1.minute, date2.minute);
					}
				}
			}
		}
		return result;
	}

    private static final Pattern serializedPattern = Pattern.compile("^(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d) (\\d\\d):(\\d\\d)$");

	public static Date parse(String str) {
        final Matcher matcher = serializedPattern.matcher(str);
        if (matcher.matches()) {
            return new Date(Integer.valueOf(matcher.group(1)),
                    Integer.valueOf(matcher.group(2)),
                    Integer.valueOf(matcher.group(3)),
                    Integer.valueOf(matcher.group(4)),
                    Integer.valueOf(matcher.group(5)));
        } else {
            throw new IllegalArgumentException("Invalid date format: " + str);
        }
	}
	
	public int compareTo(Date other) {
		return compare(this, other);
	}

    @JsonInclude(Include.NON_NULL)
    public Integer year;

    /** 1-based. */
    @JsonInclude(Include.NON_NULL)
    public Integer month;

    @JsonInclude(Include.NON_NULL)
    public Integer day;

    @JsonInclude(Include.NON_NULL)
    public Integer hour;

    @JsonInclude(Include.NON_NULL)
    public Integer minute;
}
