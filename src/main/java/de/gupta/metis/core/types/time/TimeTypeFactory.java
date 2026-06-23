package de.gupta.metis.core.types.time;

import java.time.Instant;

public final class TimeTypeFactory
{
	public static TimeType ofEpochMilli(final long epochMilli)
	{
		return of(Instant.ofEpochMilli(epochMilli));
	}

	public static TimeType of(final Instant instant)
	{
		return TimeTypeImpl.of(instant);
	}

	public static TimeType ofEpochSecond(final long epochSecond)
	{
		return of(Instant.ofEpochSecond(epochSecond));
	}

	private TimeTypeFactory()
	{
	}
}