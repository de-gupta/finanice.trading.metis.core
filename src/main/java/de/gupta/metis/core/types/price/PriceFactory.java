package de.gupta.metis.core.types.price;

import de.gupta.metis.core.types.number.TradingNumberFactory;

public final class PriceFactory
{
	public static PriceType priceOf(final long value)
	{
		return PriceTypeImpl.of(TradingNumberFactory.of(value));
	}

	private PriceFactory()
	{
	}
}