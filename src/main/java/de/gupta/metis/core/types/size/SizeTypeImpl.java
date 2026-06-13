package de.gupta.metis.core.types.size;

import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;

record SizeTypeImpl(TradingNumber value) implements SizeType
{
	@Override
	public SizeType zero()
	{
		return of(TradingNumberFactory.zero());
	}

	static SizeType of(final TradingNumber value)
	{
		return new SizeTypeImpl(value);
	}
}