package de.gupta.metis.core.types.arithmetic;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.exception.MissingInputException;
import de.gupta.metis.core.types.money.MoneyType;
import de.gupta.metis.core.types.money.MoneyTypeFactory;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.price.PriceType;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.size.SizeType;

public final class MoneyArithmetic
{
	public static <C extends Currency> MoneyType<C> multiply(final PriceType price, final SizeType size,
	                                                         final PriceQuotingConvention<CurrencyPriceUnit<C>> priceConvention,
	                                                         final SizeQuotingConvention<?> sizeConvention)
	{
		return Unfolding.beckon(price)
		                .metamorphose(PriceType::value)
		                .metamorphose(n -> n.multiply(size.value()))
		                .metamorphose(
				                m -> m.quotient(TradingNumberFactory.of(Math.powExact(10L, sizeConvention.scale()))))
		                .metamorphose(m -> MoneyTypeFactory.of(m, priceConvention.unit().currency()))
		                .decree(MissingInputException.from("Missing price or size"));
	}

	private MoneyArithmetic()
	{
	}
}