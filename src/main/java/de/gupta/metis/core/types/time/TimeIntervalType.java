package de.gupta.metis.core.types.time;

import de.gupta.commons.utility.math.ordering.bound.Bound;

public sealed interface TimeIntervalType permits TimeIntervalTypeImpl
{
	default TimeType lowerValue()
	{
		return lower().value();
	}

	Bound<TimeType> lower();

	default TimeType upperValue()
	{
		return upper().value();
	}

	Bound<TimeType> upper();

	TimeDeltaType length();

	boolean contains(TimeType time);

	boolean contains(TimeIntervalType other);

	boolean overlaps(TimeIntervalType other);

	boolean isDisjointFrom(TimeIntervalType other);

	default boolean touches(final TimeIntervalType other)
	{
		return abuts(other);
	}

	boolean abuts(TimeIntervalType other);

	TimeIntervalType span(TimeIntervalType other);

	TimeIntervalType shiftBy(TimeDeltaType delta);

	TimeIntervalType closure();
}