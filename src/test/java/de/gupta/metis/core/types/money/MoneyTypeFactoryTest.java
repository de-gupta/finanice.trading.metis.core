package de.gupta.metis.core.types.money;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.price.quoted.QuotedPrice;
import de.gupta.metis.core.types.price.quoted.QuotedPriceFactory;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;
import de.gupta.metis.core.types.size.quoted.QuotedSize;
import de.gupta.metis.core.types.size.quoted.QuotedSizeFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MoneyTypeFactory")
final class MoneyTypeFactoryTest
{
	@Nested
	@DisplayName("when weighting a quoted price by a quoted size")
	final class WhenWeightingAQuotedPriceByAQuotedSize
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsWeightedMoneyCases")
		@DisplayName("returns weighted money in the price currency")
		void returnsWeightedMoneyInThePriceCurrency(final String as, final WeightedCase weightedCase)
		{
			var result = MoneyTypeFactory.weightedBy(weightedCase.price(), weightedCase.size());

			assertThat(result.value().compare(TradingNumberFactory.of(weightedCase.expectedMoneyRawValue())))
					.as("%s - money raw value", as)
					.isEqualTo(OrderRelation.EQUAL);
			assertThat(result.currency())
					.as("%s - money currency", as)
					.isSameAs(Currency.USD.INSTANCE);
		}

		private static Stream<Arguments> returnsWeightedMoneyCases()
		{
			return Stream.of(
					WeightedCase.of("canonical USD price times fractional size",
							QuotedPriceFactory.of(4_500_000L, PriceQuotingConvention.currency(Currency.USD.INSTANCE)),
							QuotedSizeFactory.of(250_000_000L, SizeQuotingConvention.units(8)),
							11_250_000L),
					WeightedCase.of("requoted USD price is normalized back to canonical scale before multiplying",
							QuotedPriceFactory.of(45_000_000L,
									new PriceQuotingConvention<>(new CurrencyPriceUnit<>(Currency.USD.INSTANCE), 3)),
							QuotedSizeFactory.of(250_000_000L, SizeQuotingConvention.units(8)),
							11_250_000L),
					WeightedCase.of("zero quoted size yields zero money",
							QuotedPriceFactory.of(4_500_000L, PriceQuotingConvention.currency(Currency.USD.INSTANCE)),
							QuotedSizeFactory.zero(SizeQuotingConvention.units(8)),
							0L),
					WeightedCase.of("negative price yields negative money",
							QuotedPriceFactory.of(-4_500_000L, PriceQuotingConvention.currency(Currency.USD.INSTANCE)),
							QuotedSizeFactory.of(250_000_000L, SizeQuotingConvention.units(8)),
							-11_250_000L)
			).map(weightedCase -> Arguments.of(weightedCase.as(), weightedCase));
		}

		private record WeightedCase(String as, QuotedPrice<CurrencyPriceUnit<Currency.USD>> price,
		                            QuotedSize<SizeQuotingUnit.Units> size, long expectedMoneyRawValue)
		{
			private static WeightedCase of(final String as,
			                               final QuotedPrice<CurrencyPriceUnit<Currency.USD>> price,
			                               final QuotedSize<SizeQuotingUnit.Units> size,
			                               final long expectedMoneyRawValue)
			{
				return new WeightedCase(as, price, size, expectedMoneyRawValue);
			}
		}
	}
}
