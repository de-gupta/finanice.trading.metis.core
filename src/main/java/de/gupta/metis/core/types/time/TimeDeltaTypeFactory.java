package de.gupta.metis.core.types.time;

import java.time.Duration;

public final class TimeDeltaTypeFactory
{
	public static TimeDeltaType zero()
	{
		return TimeDeltaTypeImpl.of(Duration.ZERO);
	}

	public static TimeDeltaType ofMillis(final long millis)
	{
		return of(Duration.ofMillis(millis));
	}

	public static TimeDeltaType of(final Duration duration)
	{
		return TimeDeltaTypeImpl.of(duration);
	}

	public static TimeDeltaType ofSeconds(final long seconds)
	{
		return of(Duration.ofSeconds(seconds));
	}

	public static TimeDeltaType ofNanos(final long nanos)
	{
		return of(Duration.ofNanos(nanos));
	}

	private TimeDeltaTypeFactory()
	{
	}
}