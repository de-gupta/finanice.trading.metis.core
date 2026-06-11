package de.gupta.metis.core.types.size;

import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;

public sealed interface SizeType permits SizeTypeImpl
{
	TradingNumber value();

	static SizeType of(final long value)
	{
		return SizeTypeImpl.of(TradingNumberFactory.of(value));
	}

	static SizeType of(final TradingNumber value)
	{
		return SizeTypeImpl.of(value);
	}
}
