package de.gupta.metis.core.types.time;

import de.gupta.commons.utility.math.ordering.element.AffinelyOrdered;

import java.time.Instant;

public sealed interface TimeType extends AffinelyOrdered<TimeType, TimeDeltaType>
		permits TimeTypeImpl
{
	Instant asInstant();

	default TimeDeltaType distanceTo(final TimeType other)
	{
		return displacementTo(other);
	}

	default TimeType shiftBy(final TimeDeltaType delta)
	{
		return translate(delta);
	}

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