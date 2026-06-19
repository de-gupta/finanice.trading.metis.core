package de.gupta.metis.core.types.money;

import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;

public final class MoneyTypeFactory
{
	@Deprecated
	public static <C extends Currency> MoneyType<C> of(final TradingNumber value, final C currency)
	{
		return MoneyTypeImpl.of(value, currency);
	}

	public static <C extends Currency> MoneyType<C> zero(final C currency)
	{
		return of(0, currency);
	}

	public static <C extends Currency> MoneyType<C> of(final int value, final C currency)
	{
		return of((long) value, currency);
	}

	public static <C extends Currency> MoneyType<C> of(final long value, final C currency)
	{
		return MoneyTypeImpl.of(TradingNumberFactory.of(value), currency);
	}



	private MoneyTypeFactory()
	{
	}
}