package de.gupta.metis.core.types.price;

import de.gupta.metis.core.types.Currency;
import de.gupta.metis.core.types.number.TradingNumber;

public sealed interface PriceType permits PriceTypeImpl
{
	TradingNumber value();

	Currency currency();
}