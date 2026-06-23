package de.gupta.metis.core.types.arithmetic;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.exception.ExceptionHelper;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.exception.MissingInputException;
import de.gupta.metis.core.types.money.MoneyType;
import de.gupta.metis.core.types.money.MoneyTypeFactory;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.price.PriceType;
import de.gupta.metis.core.types.price.PriceTypeFactory;
import de.gupta.metis.core.types.price.quoted.QuotedPrice;
import de.gupta.metis.core.types.price.quoted.QuotedPriceFactory;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.rounding.ScalarRoundingPolicy;
import de.gupta.metis.core.types.size.SizeType;
import de.gupta.metis.core.types.size.SizeTypeFactory;
import de.gupta.metis.core.types.size.quoted.QuotedSize;

public final class MoneyArithmetic
{
	public static <C extends Currency> MoneyType<C> multiply(
			final PriceType price,
			final SizeType size,
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

	public static <C extends Currency> QuotedPrice<CurrencyPriceUnit<C>> divide(
			final MoneyType<C> money,
			final QuotedSize<?> size,
			final PriceQuotingConvention<CurrencyPriceUnit<C>> outputPriceConvention)
	{
		return Unfolding.beckon(size)
		                .interdict(QuotedSize::isZero, ExceptionHelper.iaeFrom("Size may not be zero"))
		                .metamorphose(s -> divide(money, s.size(), s.convention()))
		                .metamorphose(p -> QuotedPriceFactory.of(p, PriceQuotingConvention.currency(money.currency())))
		                .coronate(p -> p.requote(outputPriceConvention));
	}

	public static <C extends Currency> QuotedPrice<CurrencyPriceUnit<C>> divide(
			final MoneyType<C> money,
			final QuotedSize<?> size,
			final PriceQuotingConvention<CurrencyPriceUnit<C>> outputPriceConvention,
			final ScalarRoundingPolicy policy)
	{
		return Unfolding.beckon(size)
		                .interdict(QuotedSize::isZero, ExceptionHelper.iaeFrom("Size may not be zero"))
		                .metamorphose(s -> divide(money, s.size(), s.convention(), policy))
		                .metamorphose(p -> QuotedPriceFactory.of(p, PriceQuotingConvention.currency(money.currency())))
		                .coronate(p -> p.requote(outputPriceConvention));
	}

	static <C extends Currency> PriceType divide(
			final MoneyType<C> money,
			final SizeType size,
			final SizeQuotingConvention<?> sizeConvention)
	{
		return Unfolding.beckon(money)
		                .interdict(_ -> size.value().isZero(), ExceptionHelper.iaeFrom("Size may not be zero"))
		                .metamorphose(MoneyType::value)
		                .metamorphose(
								m -> m.multiply(TradingNumberFactory.of(Math.powExact(10L, sizeConvention.scale()))))
		                .metamorphose(scaled -> scaled.quotient(size.value()))
		                .metamorphose(PriceTypeFactory::of)
		                .decree(MissingInputException.from("Missing money or size"));
	}

	static <C extends Currency> PriceType divide(
			final MoneyType<C> money,
			final SizeType size,
			final SizeQuotingConvention<?> sizeConvention,
			final ScalarRoundingPolicy policy)
	{
		return Unfolding.beckon(money)
		                .interdict(_ -> size.value().isZero(), ExceptionHelper.iaeFrom("Size may not be zero"))
		                .metamorphose(MoneyType::value)
		                .metamorphose(
								m -> m.multiply(TradingNumberFactory.of(Math.powExact(10L, sizeConvention.scale()))))
		                .metamorphose(scaled -> scaled.quotient(size.value(), policy))
		                .metamorphose(PriceTypeFactory::of)
		                .decree(MissingInputException.from("Missing money or size"));
	}

	public static <C extends Currency> SizeType divide(
			final MoneyType<C> money,
			final PriceType price,
			final SizeQuotingConvention<?> sizeConvention)
	{
		return Unfolding.beckon(money)
		                .metamorphose(MoneyType::value)
		                .metamorphose(
								m -> m.multiply(TradingNumberFactory.of(Math.powExact(10L, sizeConvention.scale()))))
		                .metamorphose(scaled -> scaled.quotient(price.value()))
		                .metamorphose(SizeTypeFactory::of)
		                .decree(MissingInputException.from("Missing money or price"));
	}

	private MoneyArithmetic()
	{
	}
}