package de.gupta.metis.core.types.time;

import de.gupta.commons.utility.math.ordering.bound.Bound;
import de.gupta.commons.utility.math.ordering.interval.BoundedInterval;
import de.gupta.commons.utility.math.ordering.interval.Intervals;

public final class TimeIntervals
{
	public static TimeDeltaType length(final BoundedInterval<TimeType> interval)
	{
		return interval.lowerValue().distanceTo(interval.upperValue());
	}

	public static BoundedInterval<TimeType> shiftBy(final BoundedInterval<TimeType> interval,
	                                                final TimeDeltaType delta)
	{
		return makeInterval(shiftBound(interval.lower(), delta), shiftBound(interval.upper(), delta));
	}

	private static BoundedInterval<TimeType> makeInterval(final Bound<TimeType> lower, final Bound<TimeType> upper)
	{
		return switch (lower)
		{
			case Bound.Closed<TimeType> ignored -> switch (upper)
			{
				case Bound.Closed<TimeType> ignored2 -> Intervals.closed(lower.value(), upper.value());
				case Bound.Open<TimeType> ignored2 -> Intervals.closedOpen(lower.value(), upper.value());
			};
			case Bound.Open<TimeType> ignored -> switch (upper)
			{
				case Bound.Closed<TimeType> ignored2 -> Intervals.openClosed(lower.value(), upper.value());
				case Bound.Open<TimeType> ignored2 -> Intervals.open(lower.value(), upper.value());
			};
		};
	}

	private static Bound<TimeType> shiftBound(final Bound<TimeType> bound, final TimeDeltaType delta)
	{
		return switch (bound)
		{
			case Bound.Closed<TimeType> b -> new Bound.Closed<>(b.value().shiftBy(delta));
			case Bound.Open<TimeType> b -> new Bound.Open<>(b.value().shiftBy(delta));
		};
	}

	private TimeIntervals()
	{
	}
}