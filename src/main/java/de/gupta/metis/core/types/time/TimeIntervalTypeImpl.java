package de.gupta.metis.core.types.time;

import de.gupta.commons.utility.math.ordering.bound.Bound;
import de.gupta.commons.utility.math.ordering.interval.BoundedInterval;
import de.gupta.commons.utility.math.ordering.interval.Intervals;

record TimeIntervalTypeImpl(BoundedInterval<TimeType> delegate) implements TimeIntervalType
{
	static TimeIntervalType of(final BoundedInterval<TimeType> interval)
	{
		return new TimeIntervalTypeImpl(interval);
	}

	@Override
	public String toString()
	{
		return (lower().isClosed() ? "[" : "(") + lowerValue() + ", " + upperValue() + (upper().isClosed() ? "]" : ")");
	}	@Override
	public Bound<TimeType> lower()
	{
		return delegate.lower();
	}

	@Override
	public Bound<TimeType> upper()
	{
		return delegate.upper();
	}

	@Override
	public TimeDeltaType length()
	{
		return lowerValue().distanceTo(upperValue());
	}

	@Override
	public boolean contains(final TimeType time)
	{
		return delegate.contains(time);
	}

	@Override
	public boolean contains(final TimeIntervalType other)
	{
		return switch (other)
		{
			case TimeIntervalTypeImpl t -> delegate.contains(t.delegate);
		};
	}

	@Override
	public boolean overlaps(final TimeIntervalType other)
	{
		return switch (other)
		{
			case TimeIntervalTypeImpl t -> delegate.overlaps(t.delegate);
		};
	}

	@Override
	public boolean isDisjointFrom(final TimeIntervalType other)
	{
		return !overlaps(other);
	}

	@Override
	public boolean abuts(final TimeIntervalType other)
	{
		return switch (other)
		{
			case TimeIntervalTypeImpl t -> delegate.abuts(t.delegate);
		};
	}

	@Override
	public TimeIntervalType span(final TimeIntervalType other)
	{
		return switch (other)
		{
			// span of two bounded intervals is always bounded
			case TimeIntervalTypeImpl t ->
					new TimeIntervalTypeImpl((BoundedInterval<TimeType>) delegate.span(t.delegate));
		};
	}

	@Override
	public TimeIntervalType shiftBy(final TimeDeltaType delta)
	{
		return new TimeIntervalTypeImpl(makeInterval(shiftBound(lower(), delta), shiftBound(upper(), delta)));
	}

	@Override
	public TimeIntervalType closure()
	{
		return new TimeIntervalTypeImpl(Intervals.closed(lowerValue(), upperValue()));
	}



	private static Bound<TimeType> shiftBound(final Bound<TimeType> bound, final TimeDeltaType delta)
	{
		return switch (bound)
		{
			case Bound.Closed<TimeType> b -> new Bound.Closed<>(b.value().shiftBy(delta));
			case Bound.Open<TimeType> b -> new Bound.Open<>(b.value().shiftBy(delta));
		};
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
}