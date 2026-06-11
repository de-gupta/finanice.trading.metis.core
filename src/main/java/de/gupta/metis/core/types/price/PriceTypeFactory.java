package de.gupta.metis.core.types.price;

import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;

public final class PriceTypeFactory
{
	public static PriceType priceOf(final long value)
	{
		return priceOf(TradingNumberFactory.of(value));
	}

	public static PriceType priceOf(final TradingNumber value)
	{
		return PriceTypeImpl.of(value);
	}

	private PriceTypeFactory()
	{
	}
}