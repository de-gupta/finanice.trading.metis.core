package de.gupta.metis.core.types.price;

import de.gupta.metis.core.types.number.TradingNumber;

record PriceTypeImpl(TradingNumber value) implements PriceType
{
	static PriceType of(final TradingNumber value)
	{
		return new PriceTypeImpl(value);
	}
}