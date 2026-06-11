package de.gupta.metis.core.types.size;

import de.gupta.metis.core.types.number.TradingNumberFactory;

public final class SizeFactory
{
	public static SizeType sizeOf(final long value)
	{
		return SizeTypeImpl.of(TradingNumberFactory.of(value));
	}

	private SizeFactory()
	{
	}
}