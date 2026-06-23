package de.gupta.metis.core.types.time;

import de.gupta.commons.utility.math.ordering.OrderRelation;

import java.time.Duration;
import java.time.Instant;

record TimeTypeImpl(Instant value) implements TimeType
{
	static TimeType of(final Instant instant)
	{
		return new TimeTypeImpl(instant);
	}

	@Override
	public Instant asInstant()
	{
		return value;
	}

	@Override
	public TimeType shiftBy(final TimeDeltaType delta)
	{
		return switch (delta)
		{
			case TimeDeltaTypeImpl d -> new TimeTypeImpl(value.plus(d.asDuration()));
		};
	}

	@Override
	public TimeDeltaType distanceTo(final TimeType other)
	{
		return switch (other)
		{
			case TimeTypeImpl t -> TimeDeltaTypeImpl.of(Duration.between(value, t.value));
		};
	}

	@Override
	public OrderRelation compare(final TimeType other)
	{
		return switch (other)
		{
			case TimeTypeImpl t -> OrderRelation.from(value.compareTo(t.value));
		};
	}

	@Override
	public String toString()
	{
		return value.toString();
	}
}