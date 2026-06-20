package de.gupta.metis.core.types.arithmetic;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.money.MoneyTypeFactory;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.price.PriceTypeFactory;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;
import de.gupta.metis.core.types.size.SizeTypeFactory;
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

@DisplayName("MoneyArithmetic#divide")
final class MoneyArithmeticDivideTest
{
	@Nested
	@DisplayName("when dividing USD money by whole-unit size")
	final class WhenDividingUsdMoneyByWholeUnitSize
	{
		private final SizeQuotingConvention<SizeQuotingUnit.Units> sizeConvention = SizeQuotingConvention.units(0);

		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsPriceEqualToMoneyDividedBySizeCases")
		@DisplayName("returns price equal to money divided by size")
		void returnsPriceEqualToMoneyDividedBySize(final String as, final long moneyRaw, final long sizeRaw,
		                                           final long expectedPriceRaw)
		{
			var money = MoneyTypeFactory.of(TradingNumberFactory.of(moneyRaw), Currency.USD.INSTANCE);

			var result = MoneyArithmetic.divide(money, SizeTypeFactory.of(sizeRaw), sizeConvention);

			assertThat(result.value().compare(PriceTypeFactory.of(expectedPriceRaw).value()))
					.as(as)
					.isEqualTo(OrderRelation.EQUAL);
		}

		private static Stream<Arguments> returnsPriceEqualToMoneyDividedBySizeCases()
		{
			return Stream.of(
					Arguments.of("$5.00 / 5 units = $1.00 (100 cents)", 500L, 5L, 100L),
					Arguments.of("$1.00 / 1 unit = $1.00", 100L, 1L, 100L),
					Arguments.of("$100.00 / 4 units = $25.00", 10000L, 4L, 2500L),
					Arguments.of("$45,000.00 / 100 units = $450.00", 4500000L, 100L, 45000L),
					Arguments.of("-$50.00 / 5 units = -$10.00 (short notional)", -5000L, 5L, -1000L)
			);
		}
	}

	@Nested
	@DisplayName("when dividing with fractional size scale")
	final class WhenDividingWithFractionalSizeScale
	{
		private final SizeQuotingConvention<SizeQuotingUnit.Units> btcSizeConvention = SizeQuotingConvention.units(8);

		@ParameterizedTest(name = "{0}")
		@MethodSource("appliesSizeScaleBeforeDividingCases")
		@DisplayName("applies size scale before dividing")
		void appliesSizeScaleBeforeDividing(final String as, final long moneyRaw, final long sizeRaw,
		                                    final long expectedPriceRaw)
		{
			var money = MoneyTypeFactory.of(TradingNumberFactory.of(moneyRaw), Currency.USD.INSTANCE);

			var result = MoneyArithmetic.divide(money, SizeTypeFactory.of(sizeRaw), btcSizeConvention);

			assertThat(result.value().compare(PriceTypeFactory.of(expectedPriceRaw).value()))
					.as(as)
					.isEqualTo(OrderRelation.EQUAL);
		}

		private static Stream<Arguments> appliesSizeScaleBeforeDividingCases()
		{
			return Stream.of(
					Arguments.of("$112,500 / 2.5 BTC (scale 8) = $45,000", 11250000L, 250_000_000L, 4500000L),
					Arguments.of("$45,000 / 1 BTC (scale 8) = $45,000", 4500000L, 100_000_000L, 4500000L),
					Arguments.of("$0.50 / 0.5 BTC (scale 8) = $1.00", 50L, 50_000_000L, 100L),
					Arguments.of("$0 / 1 BTC (scale 8) = $0", 0L, 100_000_000L, 0L)
			);
		}
	}

	@Nested
	@DisplayName("when division does not divide evenly")
	final class WhenDivisionDoesNotDivideEvenly
	{
		@Test
		@DisplayName("truncates toward zero — remainder is discarded")
		void truncatesTowardZeroRemainderIsDiscarded()
		{
			var money = MoneyTypeFactory.of(TradingNumberFactory.of(500L), Currency.USD.INSTANCE);

			var result = MoneyArithmetic.divide(money, SizeTypeFactory.of(3L), SizeQuotingConvention.units(0));

			assertThat(result.value().compare(PriceTypeFactory.of(166L).value()))
					.as("$5.00 / 3 units = $1.66 (500 / 3 = 166 remainder 2, truncated)")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("truncates negative division away from zero")
		void truncatesNegativeDivisionTowardZero()
		{
			var money = MoneyTypeFactory.of(TradingNumberFactory.of(-500L), Currency.USD.INSTANCE);

			var result = MoneyArithmetic.divide(money, SizeTypeFactory.of(3L), SizeQuotingConvention.units(0));

			assertThat(result.value().compare(PriceTypeFactory.of(-167L).value()))
					.as("-$5.00 / 3 units = -$1.67 (rounded up so that remainder is positive, not -$1.66)")
					.isEqualTo(OrderRelation.EQUAL);
		}
	}

	@Nested
	@DisplayName("when divide is the inverse of multiply")
	final class WhenDivideIsTheInverseOfMultiply
	{
		@Test
		@DisplayName("multiply then divide recovers the original price when division is exact")
		void multiplyThenDivideRecoversOriginalPriceWhenDivisionIsExact()
		{
			var originalPrice = PriceTypeFactory.of(4_500_000L);
			var size = SizeTypeFactory.of(250_000_000L);
			var priceConvention = PriceQuotingConvention.currency(Currency.USD.INSTANCE);
			var sizeConvention = SizeQuotingConvention.units(8);

			var money = MoneyArithmetic.multiply(originalPrice, size, priceConvention, sizeConvention);
			var result = MoneyArithmetic.divide(money, size, sizeConvention);

			assertThat(result.value().compare(originalPrice.value()))
					.as("multiply then divide should recover the original price when division is exact")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("multiply then divide recovers original price for JPY")
		void multiplyThenDivideRecoversOriginalPriceForJpy()
		{
			var originalPrice = PriceTypeFactory.of(5000L);
			var size = SizeTypeFactory.of(10_000L);
			var priceConvention = PriceQuotingConvention.currency(Currency.JPY.INSTANCE);
			var sizeConvention = SizeQuotingConvention.units(0);

			var money = MoneyArithmetic.multiply(originalPrice, size, priceConvention, sizeConvention);
			var result = MoneyArithmetic.divide(money, size, sizeConvention);

			assertThat(result.value().compare(originalPrice.value()))
					.as("¥5,000 × 10,000 shares then ÷ 10,000 shares should recover ¥5,000")
					.isEqualTo(OrderRelation.EQUAL);
		}
	}

	@Nested
	@DisplayName("when size is zero")
	final class WhenSizeIsZero
	{
		@Test
		@DisplayName("throws Illegal Argument exception for division by zero")
		void throwsIllegalArgumentExceptionForDivisionByZero()
		{
			var money = MoneyTypeFactory.of(TradingNumberFactory.of(10000L), Currency.USD.INSTANCE);

			assertThatThrownBy(
					() -> MoneyArithmetic.divide(money, SizeTypeFactory.of(0L), SizeQuotingConvention.units(0)))
					.as("dividing by zero size should throw ArithmeticException")
					.isInstanceOf(IllegalArgumentException.class);
		}
	}

	// ── Money / Price = Size ──────────────────────────────────────────────────

	@Nested
	@DisplayName("when dividing money by quoted size")
	final class WhenDividingMoneyByQuotedSize
	{
		@Test
		@DisplayName("returns quoted price using the quoted size convention directly")
		void returnsQuotedPriceUsingTheQuotedSizeConventionDirectly()
		{
			var money = MoneyTypeFactory.of(11_250_000L, Currency.USD.INSTANCE);
			var size = QuotedSizeFactory.of(250_000_000L, SizeQuotingConvention.units(8));
			var outputPriceConvention = PriceQuotingConvention.currency(Currency.USD.INSTANCE);

			var result = MoneyArithmetic.divide(money, size, outputPriceConvention);

			assertThat(result.price().value().compare(PriceTypeFactory.of(4_500_000L).value()))
					.as("$112,500 / 2.5 BTC should produce raw USD price 4,500,000")
					.isEqualTo(OrderRelation.EQUAL);
			assertThat(result.convention())
					.as("result convention")
					.isEqualTo(outputPriceConvention);
		}

		@Test
		@DisplayName("requotes the canonical currency price into the requested output convention")
		void requotesTheCanonicalCurrencyPriceIntoTheRequestedOutputConvention()
		{
			var money = MoneyTypeFactory.of(11_250_000L, Currency.USD.INSTANCE);
			var size = QuotedSizeFactory.of(250_000_000L, SizeQuotingConvention.units(8));
			var outputPriceConvention = new PriceQuotingConvention<>(new CurrencyPriceUnit<>(Currency.USD.INSTANCE), 3);

			var result = MoneyArithmetic.divide(money, size, outputPriceConvention);

			assertThat(result.price().value().compare(PriceTypeFactory.of(45_000_000L).value()))
					.as("$45,000 at canonical scale 2 requoted to scale 3 becomes raw value 45,000,000")
					.isEqualTo(OrderRelation.EQUAL);
			assertThat(result.convention())
					.as("result convention")
					.isEqualTo(outputPriceConvention);
		}

		@Test
		@DisplayName("throws Illegal argument exception for zero quoted size")
		void throwsIllegalArgumentExceptionForZeroQuotedSize()
		{
			var money = MoneyTypeFactory.of(10_000L, Currency.USD.INSTANCE);
			var size = QuotedSizeFactory.zero(SizeQuotingConvention.units(0));

			assertThatThrownBy(() -> MoneyArithmetic.divide(money, size, PriceQuotingConvention.currency(
					Currency.USD.INSTANCE)))
					.as("dividing by zero quoted size should throw ArithmeticException")
					.isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Nested
	@DisplayName("when dividing USD money by a USD price (whole-unit size)")
	final class WhenDividingUsdMoneyByUsdPrice
	{
		private final SizeQuotingConvention<SizeQuotingUnit.Units> sizeConvention = SizeQuotingConvention.units(0);

		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsSizeEqualToMoneyDividedByPriceCases")
		@DisplayName("returns size equal to money divided by price")
		void returnsSizeEqualToMoneyDividedByPrice(final String as, final long moneyRaw, final long priceRaw,
		                                           final long expectedSizeRaw)
		{
			var money = MoneyTypeFactory.of(TradingNumberFactory.of(moneyRaw), Currency.USD.INSTANCE);

			var result = MoneyArithmetic.divide(money, PriceTypeFactory.of(priceRaw), sizeConvention);

			assertThat(result.value().compare(SizeTypeFactory.of(expectedSizeRaw).value()))
					.as(as)
					.isEqualTo(OrderRelation.EQUAL);
		}

		private static Stream<Arguments> returnsSizeEqualToMoneyDividedByPriceCases()
		{
			return Stream.of(
					Arguments.of("$5.00 / $1.00 = 5 units", 500L, 100L, 5L),
					Arguments.of("$1.00 / $1.00 = 1 unit", 100L, 100L, 1L),
					Arguments.of("$45,000 / $450 = 100 units", 4_500_000L, 45_000L, 100L),
					Arguments.of("-$50.00 / $10.00 = -5 units (short)", -5000L, 1000L, -5L)
			);
		}
	}

	@Nested
	@DisplayName("when dividing money by price with fractional size scale")
	final class WhenDividingMoneyByPriceWithFractionalSizeScale
	{
		private final SizeQuotingConvention<SizeQuotingUnit.Units> btcSizeConvention = SizeQuotingConvention.units(8);

		@ParameterizedTest(name = "{0}")
		@MethodSource("appliesSizeScaleBeforeDividingByPriceCases")
		@DisplayName("applies size scale before dividing by price")
		void appliesSizeScaleBeforeDividingByPrice(final String as, final long moneyRaw, final long priceRaw,
		                                           final long expectedSizeRaw)
		{
			var money = MoneyTypeFactory.of(TradingNumberFactory.of(moneyRaw), Currency.USD.INSTANCE);

			var result = MoneyArithmetic.divide(money, PriceTypeFactory.of(priceRaw), btcSizeConvention);

			assertThat(result.value().compare(SizeTypeFactory.of(expectedSizeRaw).value()))
					.as(as)
					.isEqualTo(OrderRelation.EQUAL);
		}

		private static Stream<Arguments> appliesSizeScaleBeforeDividingByPriceCases()
		{
			return Stream.of(
					Arguments.of("$112,500 / $45,000 (scale 8) = 2.5 BTC", 11_250_000L, 4_500_000L, 250_000_000L),
					Arguments.of("$45,000 / $45,000 (scale 8) = 1 BTC", 4_500_000L, 4_500_000L, 100_000_000L),
					Arguments.of("$0 / $45,000 (scale 8) = 0 BTC", 0L, 4_500_000L, 0L),
					Arguments.of("-$112,500 / $45,000 (scale 8) = -2.5 BTC (short)", -11_250_000L, 4_500_000L,
							-250_000_000L)
			);
		}
	}

	@Nested
	@DisplayName("when money divide by price is the inverse of multiply")
	final class WhenMoneyDivideByPriceIsTheInverseOfMultiply
	{
		@Test
		@DisplayName("multiply then divide by price recovers original size when division is exact")
		void multiplyThenDivideByPriceRecoversOriginalSizeWhenDivisionIsExact()
		{
			var price = PriceTypeFactory.of(4_500_000L);
			var originalSize = SizeTypeFactory.of(250_000_000L);
			var priceConvention = PriceQuotingConvention.currency(Currency.USD.INSTANCE);
			var sizeConvention = SizeQuotingConvention.units(8);

			var money = MoneyArithmetic.multiply(price, originalSize, priceConvention, sizeConvention);
			var result = MoneyArithmetic.divide(money, price, sizeConvention);

			assertThat(result.value().compare(originalSize.value()))
					.as("multiply then divide by price should recover original size when division is exact")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("multiply then divide by price recovers original size for JPY")
		void multiplyThenDivideByPriceRecoversOriginalSizeForJpy()
		{
			var price = PriceTypeFactory.of(5000L);
			var originalSize = SizeTypeFactory.of(10_000L);
			var priceConvention = PriceQuotingConvention.currency(Currency.JPY.INSTANCE);
			var sizeConvention = SizeQuotingConvention.units(0);

			var money = MoneyArithmetic.multiply(price, originalSize, priceConvention, sizeConvention);
			var result = MoneyArithmetic.divide(money, price, sizeConvention);

			assertThat(result.value().compare(originalSize.value()))
					.as("¥5,000 × 10,000 then ÷ ¥5,000 should recover 10,000 shares")
					.isEqualTo(OrderRelation.EQUAL);
		}
	}

	@Nested
	@DisplayName("when price is zero")
	final class WhenPriceIsZero
	{
		@Test
		@DisplayName("throws arithmetic exception for division by zero price")
		void throwsArithmeticExceptionForDivisionByZeroPrice()
		{
			var money = MoneyTypeFactory.of(TradingNumberFactory.of(10000L), Currency.USD.INSTANCE);

			assertThatThrownBy(
					() -> MoneyArithmetic.divide(money, PriceTypeFactory.of(0L), SizeQuotingConvention.units(0)))
					.as("dividing by zero price should throw ArithmeticException")
					.isInstanceOf(ArithmeticException.class);
		}
	}
}