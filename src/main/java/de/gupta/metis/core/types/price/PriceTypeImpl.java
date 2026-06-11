package de.gupta.metis.core.types.price;

import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;

record PriceTypeImpl(TradingNumber value) implements PriceType
{
	static PriceType of(final TradingNumber value)
	{
		return new PriceTypeImpl(value);
	}

	@Override
	public PriceType zero()
	{
		return of(TradingNumberFactory.zero());
	}
}