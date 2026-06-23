package de.gupta.metis.core.types.time;

import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;

import java.time.Instant;

public sealed interface TimeType extends TotallyOrdered<TimeType>
		permits TimeTypeImpl
{
	Instant asInstant();

	TimeType shiftBy(TimeDeltaType delta);

	TimeDeltaType distanceTo(TimeType other);

	default boolean isBefore(final TimeType other)
	{
		return compare(other).isLessThan();
	}

	default boolean isAfter(final TimeType other)
	{
		return compare(other).isGreaterThan();
	}

	default boolean isAtOrBefore(final TimeType other)
	{
		return compare(other).isLessThanOrEqualTo();
	}

	default boolean isAtOrAfter(final TimeType other)
	{
		return compare(other).isGreaterThanOrEqualTo();
	}
}