package de.gupta.metis.core.types.time;

import de.gupta.commons.utility.math.ordering.interval.Intervals;

public final class TimeIntervalTypeFactory
{
	public static TimeIntervalType closed(final TimeType start, final TimeType end)
	{
		return TimeIntervalTypeImpl.of(Intervals.closed(start, end));
	}

	public static TimeIntervalType closedOpen(final TimeType start, final TimeType end)
	{
		return TimeIntervalTypeImpl.of(Intervals.closedOpen(start, end));
	}

	public static TimeIntervalType openClosed(final TimeType start, final TimeType end)
	{
		return TimeIntervalTypeImpl.of(Intervals.openClosed(start, end));
	}

	public static TimeIntervalType open(final TimeType start, final TimeType end)
	{
		return TimeIntervalTypeImpl.of(Intervals.open(start, end));
	}

	private TimeIntervalTypeFactory()
	{
	}
}
