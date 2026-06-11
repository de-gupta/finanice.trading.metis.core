package de.gupta.metis.core.types.size;

import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;

public final class SizeTypeFactory
{
	public static SizeType sizeOf(final long value)
	{
		return sizeOf(TradingNumberFactory.of(value));
	}

	public static SizeType sizeOf(final TradingNumber value)
	{
		return SizeTypeImpl.of(value);
	}

	public static SizeType zero()
	{
		return SizeTypeImpl.zero();
	}

	private SizeTypeFactory()
	{
	}
}