package de.gupta.metis.core.types.size;

import de.gupta.metis.core.types.number.TradingNumber;

record SizeTypeImpl(TradingNumber value) implements SizeType
{
	static SizeType of(final TradingNumber value)
	{
		return new SizeTypeImpl(value);
	}
}