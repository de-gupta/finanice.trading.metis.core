package de.gupta.metis.core.types.price;

import de.gupta.metis.core.types.Currency;
import de.gupta.metis.core.types.number.TradingNumber;

record PriceTypeImpl(TradingNumber value, Currency currency) implements PriceType
{
	static PriceType of(final TradingNumber value, final Currency currency)
	{
		return new PriceTypeImpl(value, currency);
	}
}