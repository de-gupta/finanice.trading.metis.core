package de.gupta.metis.core.types.price;

import de.gupta.metis.core.types.Currency;
import de.gupta.metis.core.types.number.TradingNumberFactory;

public final class PriceFactory
{
	public static PriceType priceOf(final long value, final Currency currency)
	{
		return PriceTypeImpl.of(TradingNumberFactory.of(value), currency);
	}

	private PriceFactory()
	{
	}
}