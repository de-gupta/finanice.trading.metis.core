package de.gupta.metis.core.types.money;

import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;

record MoneyTypeImpl<C extends Currency>(TradingNumber value, C currency) implements MoneyType<C>
{
	static <C extends Currency> MoneyType<C> of(final TradingNumber value, final C currency)
	{
		return new MoneyTypeImpl<>(value, currency);
	}

	@Override
	public MoneyType<C> negate()
	{
		return of(value.negate(), currency);
	}

	@Override
	public MoneyType<C> zero()
	{
		return of(TradingNumberFactory.zero(), currency);
	}

	@Override
	public MoneyType<C> add(final MoneyType<C> other)
	{
		return of(value.add(other.value()), currency);
	}

	@Override
	public C currency()
	{
		return currency;
	}
}