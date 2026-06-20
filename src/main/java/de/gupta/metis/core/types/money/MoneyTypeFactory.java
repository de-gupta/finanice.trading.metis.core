package de.gupta.metis.core.types.money;

import de.gupta.metis.core.types.arithmetic.MoneyArithmetic;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.price.quoted.QuotedPrice;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.size.quoted.QuotedSize;

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

	public static <C extends Currency> MoneyType<C> weightedBy(final QuotedPrice<CurrencyPriceUnit<C>> price,
	                                                           final QuotedSize<?> size)
	{
		var canonicalPriceConvention = PriceQuotingConvention.currency(price.convention().unit().currency());
		var canonicalPrice = price.requote(canonicalPriceConvention);

		return MoneyArithmetic.multiply(canonicalPrice.price(), size.size(), canonicalPriceConvention,
				size.convention());
	}

	private MoneyTypeFactory()
	{
	}
}