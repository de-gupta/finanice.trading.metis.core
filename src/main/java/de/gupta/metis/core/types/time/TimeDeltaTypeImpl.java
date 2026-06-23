package de.gupta.metis.core.types.time;

import de.gupta.commons.utility.math.ordering.OrderRelation;

import java.time.Duration;

record TimeDeltaTypeImpl(Duration value) implements TimeDeltaType
{
	static TimeDeltaType of(final Duration duration)
	{
		return new TimeDeltaTypeImpl(duration);
	}

	@Override
	public Duration asDuration()
	{
		return value;
	}

	@Override
	public TimeDeltaType zero()
	{
		return new TimeDeltaTypeImpl(Duration.ZERO);
	}

	@Override
	public TimeDeltaType add(final TimeDeltaType other)
	{
		return switch (other)
		{
			case TimeDeltaTypeImpl d -> new TimeDeltaTypeImpl(value.plus(d.value));
		};
	}

	@Override
	public TimeDeltaType negate()
	{
		return new TimeDeltaTypeImpl(value.negated());
	}

	@Override
	public OrderRelation compare(final TimeDeltaType other)
	{
		return switch (other)
		{
			case TimeDeltaTypeImpl d -> OrderRelation.from(value.compareTo(d.value));
		};
	}

	@Override
	public String toString()
	{
		return value.toString();
	}
}
