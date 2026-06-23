package de.gupta.metis.core.types.money;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveAbelianGroup;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.metis.core.types.arithmetic.MoneyArithmetic;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.price.quoted.QuotedPrice;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.rounding.ScalarRoundingPolicy;
import de.gupta.metis.core.types.size.quoted.QuotedSize;

public sealed interface MoneyType<C extends Currency>
		extends AdditiveAbelianGroup<MoneyType<C>>, TotallyOrdered<MoneyType<C>> permits MoneyTypeImpl
{
	C currency();

	default boolean isZero()
	{
		return value().isZero();
	}

	TradingNumber value();

	default QuotedPrice<CurrencyPriceUnit<C>> asPricePer(final QuotedSize<?> size,
	                                                     final PriceQuotingConvention<CurrencyPriceUnit<C>> outputPriceConvention)
	{
		return MoneyArithmetic.divide(this, size, outputPriceConvention);
	}

	default QuotedPrice<CurrencyPriceUnit<C>> asPricePer(final QuotedSize<?> size,
	                                                     final PriceQuotingConvention<CurrencyPriceUnit<C>> outputPriceConvention,
	                                                     final ScalarRoundingPolicy policy)
	{
		return MoneyArithmetic.divide(this, size, outputPriceConvention, policy);
	}
}