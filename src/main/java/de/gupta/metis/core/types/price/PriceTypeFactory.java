package de.gupta.metis.core.types.price;

import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;

public final class PriceTypeFactory
{
	public static PriceType of(final long value)
	{
		return of(TradingNumberFactory.of(value));
	}

	public static PriceType of(final TradingNumber value)
	{
		return PriceTypeImpl.of(value);
	}

	private PriceTypeFactory()
	{
	}
}