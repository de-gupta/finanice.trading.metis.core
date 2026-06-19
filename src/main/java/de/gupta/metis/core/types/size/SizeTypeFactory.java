package de.gupta.metis.core.types.size;

import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;

public final class SizeTypeFactory
{
	public static SizeType zero()
	{
		return of(0);
	}

	public static SizeType of(final int value)
	{
		return of((long) value);
	}

	public static SizeType of(final long value)
	{
		return of(TradingNumberFactory.of(value));
	}

	public static SizeType of(final TradingNumber value)
	{
		return SizeTypeImpl.of(value);
	}

	private SizeTypeFactory()
	{
	}
}