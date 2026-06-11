package de.gupta.metis.core.types.price;

import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;

public sealed interface PriceType permits PriceTypeImpl
{
	TradingNumber value();

	static PriceType of(final long value)
	{
		return PriceTypeImpl.of(TradingNumberFactory.of(value));
	}

	static PriceType of(final TradingNumber value)
	{
		return PriceTypeImpl.of(value);
	}
}
