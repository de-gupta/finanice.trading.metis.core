package de.gupta.metis.core.types.money;

import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.size.quoted.QuotedSizeFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MoneyType")
final class MoneyTypeTest
{
	private static MoneyType<Currency.USD> usd(final long value)
	{
		return MoneyTypeFactory.of(TradingNumberFactory.of(value), Currency.USD.INSTANCE);
	}

	private static void assertRationalNumber(final RationalNumber actual, final long expectedNumerator,
	                                         final long expectedDenominator, final String as)
	{
		assertThat(actual.numerator().value()).as("%s - numerator", as).isEqualTo(expectedNumerator);
		assertThat(actual.denominator().value()).as("%s - denominator", as).isEqualTo(expectedDenominator);
	}

	@Nested
	@DisplayName("when negating")
	final class WhenNegating
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsMoneyWithNegatedRawValueCases")
		@DisplayName("returns money with negated raw value and same currency")
		void returnsMoneyWithNegatedRawValueAndSameCurrency(final String as, final long value, final long expected)
		{
			var result = usd(value).negate();

			assertThat(result.value().compare(TradingNumberFactory.of(expected)))
					.as("%s — value", as)
					.isEqualTo(OrderRelation.EQUAL);
			assertThat(result.currency())
					.as("%s — currency", as)
					.isSameAs(Currency.USD.INSTANCE);
		}

		private static Stream<Arguments> returnsMoneyWithNegatedRawValueCases()
		{
			return Stream.of(
					Arguments.of("negate positive", 100L, -100L),
					Arguments.of("negate negative", -50L, 50L),
					Arguments.of("negate zero", 0L, 0L),
					Arguments.of("negate large value", 1_000_000L, -1_000_000L)
			);
		}
	}

	@Nested
	@DisplayName("when getting zero")
	final class WhenGettingZero
	{
		@Test
		@DisplayName("returns money with zero value and same currency")
		void returnsMoneyWithZeroValueAndSameCurrency()
		{
			var money = usd(12345L);

			var result = money.zero();

			assertThat(result.value().compare(TradingNumberFactory.zero()))
					.as("zero() raw value")
					.isEqualTo(OrderRelation.EQUAL);
			assertThat(result.currency())
					.as("zero() currency")
					.isSameAs(Currency.USD.INSTANCE);
		}
	}

	@Nested
	@DisplayName("when checking whether the money is zero")
	final class WhenCheckingWhetherTheMoneyIsZero
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsWhetherTheMoneyIsZeroCases")
		@DisplayName("returns whether the money is zero")
		void returnsWhetherTheMoneyIsZero(final String as, final long value, final boolean expected)
		{
			var money = usd(value);

			assertThat(money.isZero())
					.as("%s - isZero()", as)
					.isEqualTo(expected);
		}

		private static Stream<Arguments> returnsWhetherTheMoneyIsZeroCases()
		{
			return Stream.of(
					Arguments.of("zero raw value", 0L, true),
					Arguments.of("positive raw value", 1L, false),
					Arguments.of("negative raw value", -1L, false)
			);
		}
	}

	@Nested
	@DisplayName("when adding")
	final class WhenAdding
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsTheSumWithSameCurrencyCases")
		@DisplayName("returns the sum with the same currency")
		void returnsTheSumWithSameCurrency(final String as, final long left, final long right, final long expectedSum)
		{
			var result = usd(left).add(usd(right));

			assertThat(result.value().compare(TradingNumberFactory.of(expectedSum)))
					.as("%s — value", as)
					.isEqualTo(OrderRelation.EQUAL);
			assertThat(result.currency())
					.as("%s — currency preserved", as)
					.isSameAs(Currency.USD.INSTANCE);
		}

		private static Stream<Arguments> returnsTheSumWithSameCurrencyCases()
		{
			return Stream.of(
					Arguments.of("100 + 50 = 150", 100L, 50L, 150L),
					Arguments.of("100 + 0 = 100", 100L, 0L, 100L),
					Arguments.of("0 + 0 = 0", 0L, 0L, 0L),
					Arguments.of("-100 + 50 = -50", -100L, 50L, -50L),
					Arguments.of("100 + -100 = 0", 100L, -100L, 0L)
			);
		}
	}

	@Nested
	@DisplayName("when expressing money as price per quoted size")
	final class WhenExpressingMoneyAsPricePerQuotedSize
	{
		@Test
		@DisplayName("returns quoted price with the requested output convention")
		void returnsQuotedPriceWithTheRequestedOutputConvention()
		{
			var money = usd(11_250_000L);
			var size = QuotedSizeFactory.of(250_000_000L, SizeQuotingConvention.units(8));
			var outputPriceConvention = PriceQuotingConvention.currency(Currency.USD.INSTANCE);

			var result = money.asPricePer(size, outputPriceConvention);

			assertThat(result.price().value().compare(TradingNumberFactory.of(4_500_000L)))
					.as("raw USD price")
					.isEqualTo(OrderRelation.EQUAL);
			assertThat(result.convention())
					.as("output convention")
					.isEqualTo(outputPriceConvention);
		}

		@Test
		@DisplayName("requotes the result into a non-canonical currency price convention")
		void requotesTheResultIntoANonCanonicalCurrencyPriceConvention()
		{
			var money = usd(11_250_000L);
			var size = QuotedSizeFactory.of(250_000_000L, SizeQuotingConvention.units(8));
			var outputPriceConvention = new PriceQuotingConvention<>(new CurrencyPriceUnit<>(Currency.USD.INSTANCE), 3);

			var result = money.asPricePer(size, outputPriceConvention);

			assertThat(result.price().value().compare(TradingNumberFactory.of(45_000_000L)))
					.as("raw USD price at scale 3")
					.isEqualTo(OrderRelation.EQUAL);
			assertThat(result.convention())
					.as("output convention")
					.isEqualTo(outputPriceConvention);
		}

		@Test
		@DisplayName("throws illegal argument exception for zero quoted size")
		void throwsIllegalArgumentExceptionForZeroQuotedSize()
		{
			var money = usd(10_000L);
			var size = QuotedSizeFactory.zero(SizeQuotingConvention.units(0));

			assertThatThrownBy(() -> money.asPricePer(size, PriceQuotingConvention.currency(Currency.USD.INSTANCE)))
					.as("dividing by zero quoted size")
					.isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Nested
	@DisplayName("when comparing")
	final class WhenComparing
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsCorrectOrderRelationCases")
		@DisplayName("returns correct comparison result")
		void returnsCorrectOrderRelation(final String as, final long left, final long right,
		                                 final OrderRelation expected)
		{
			var result = usd(left).compare(usd(right));

			assertThat(result).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> returnsCorrectOrderRelationCases()
		{
			return Stream.of(
					Arguments.of("100 > 50", 100L, 50L, OrderRelation.GREATER_THAN),
					Arguments.of("50 < 100", 50L, 100L, OrderRelation.LESS_THAN),
					Arguments.of("100 = 100", 100L, 100L, OrderRelation.EQUAL),
					Arguments.of("0 = 0", 0L, 0L, OrderRelation.EQUAL),
					Arguments.of("positive > negative", 1L, -1L, OrderRelation.GREATER_THAN),
					Arguments.of("negative < positive", -1L, 1L, OrderRelation.LESS_THAN)
			);
		}
	}

	@Nested
	@DisplayName("when computing a ratio")
	final class WhenComputingARatio
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsExactRatioCases")
		@DisplayName("returns the exact ratio of the raw money values")
		void returnsTheExactRatioOfTheRawMoneyValues(final String as, final long numerator, final long denominator,
		                                             final long expectedNumerator, final long expectedDenominator)
		{
			assertRationalNumber(usd(numerator).ratio(usd(denominator)), expectedNumerator, expectedDenominator, as);
		}

		private static Stream<Arguments> returnsExactRatioCases()
		{
			return Stream.of(
					Arguments.of("120 / 100 = 6/5", 120L, 100L, 6L, 5L),
					Arguments.of("45 / 120 = 3/8", 45L, 120L, 3L, 8L),
					Arguments.of("-45 / 120 = -3/8", -45L, 120L, -3L, 8L)
			);
		}
	}

	@Nested
	@DisplayName("when converting to string")
	final class WhenConvertingToString
	{
		@Test
		@DisplayName("formats as value space currency-code")
		void formatsAsValueSpaceCurrencyCode()
		{
			assertThat(usd(12345L).toString()).as("toString").isEqualTo("12345 USD");
		}

		@Test
		@DisplayName("formats zero as zero space currency-code")
		void formatsZeroAsZeroSpaceCurrencyCode()
		{
			assertThat(usd(0L).toString()).as("toString for zero").isEqualTo("0 USD");
		}

		@Test
		@DisplayName("formats negative value with minus sign")
		void formatsNegativeValueWithMinusSign()
		{
			assertThat(usd(-500L).toString()).as("toString for negative").isEqualTo("-500 USD");
		}
	}

	@Nested
	@DisplayName("when accessing currency")
	final class WhenAccessingCurrency
	{
		@Test
		@DisplayName("returns the currency instance used to create it")
		void returnsTheCurrencyInstanceUsedToCreateIt()
		{
			var money = MoneyTypeFactory.of(TradingNumberFactory.of(100L), Currency.EUR.INSTANCE);

			assertThat(money.currency()).as("currency").isSameAs(Currency.EUR.INSTANCE);
		}
	}
}