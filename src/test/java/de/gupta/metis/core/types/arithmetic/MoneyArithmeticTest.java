package de.gupta.metis.core.types.arithmetic;

import de.gupta.commons.utility.comparison.ComparisonResult;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.money.MoneyType;
import de.gupta.metis.core.types.money.MoneyTypeFactory;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.price.PriceType;
import de.gupta.metis.core.types.price.PriceTypeFactory;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;
import de.gupta.metis.core.types.size.SizeType;
import de.gupta.metis.core.types.size.SizeTypeFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MoneyArithmetic")
final class MoneyArithmeticTest
{
	// ── helpers ──────────────────────────────────────────────────────────────

	private static void assertMoneyEquals(final String as, final MoneyType<?> actual, final MoneyType<?> expected)
	{
		assertThat(actual.value().compare(expected.value())).as("%s — monetary value", as)
		                                                    .isEqualTo(ComparisonResult.EQUAL);
		assertThat(actual.currency()).as("%s — currency", as).isSameAs(expected.currency());
	}

	// ── USD whole-unit size — core arithmetic ─────────────────────────────

	@Nested
	@DisplayName("when multiplying a USD price by a whole-unit size")
	final class WhenMultiplyingUsdPriceByWholeUnitSize
	{
		private final PriceQuotingConvention<CurrencyPriceUnit<Currency.USD>> priceConvention =
				PriceQuotingConvention.currency(Currency.USD.INSTANCE);
		private final SizeQuotingConvention<SizeQuotingUnit.Units> sizeConvention = SizeQuotingConvention.units(0);

		@ParameterizedTest(name = "{0}")
		@MethodSource("multiplyCases")
		@DisplayName("returns money equal to price × size")
		void returnsMoneyEqualToPriceTimesSize(final String as, final UsdWholeUnitCase tc)
		{
			var result = MoneyArithmetic.multiply(tc.price(), tc.size(), priceConvention, sizeConvention);

			assertMoneyEquals(as, result, tc.expected());
		}

		private static Stream<Arguments> multiplyCases()
		{
			return Stream.of(UsdWholeUnitCase.of("price(100) × size(5) = money(500)", 100L, 5L, 500L),
								 UsdWholeUnitCase.of("price(1) × size(1) = money(1)", 1L, 1L, 1L),
								 UsdWholeUnitCase.of("zero size yields zero money", 5000L, 0L, 0L),
								 UsdWholeUnitCase.of("zero price yields zero money", 0L, 100L, 0L),
								 UsdWholeUnitCase.of("negative price (short sell) yields negative money", -4500L, 10L, -45000L),
								 UsdWholeUnitCase.of("negative size (short position) yields negative money", 4500L, -10L, -45000L),
								 UsdWholeUnitCase.of("both negative yields positive money", -4500L, -10L, 45000L),
								 UsdWholeUnitCase.of("single cent × single unit", 1L, 1L, 1L),
								 UsdWholeUnitCase.of("large price × large size", 1_000_000L, 100L, 100_000_000L),
								 UsdWholeUnitCase.of("large lot count", 200L, 10_000L, 2_000_000L))
			             .map(tc -> Arguments.of(tc.as(), tc));
		}

		private record UsdWholeUnitCase(String as, PriceType price, SizeType size, MoneyType<Currency.USD> expected)
		{
			static UsdWholeUnitCase of(final String as, final long priceRaw, final long sizeRaw, final long expectedRaw)
			{
				return new UsdWholeUnitCase(as, PriceTypeFactory.of(priceRaw), SizeTypeFactory.of(sizeRaw),
						MoneyTypeFactory.of(TradingNumberFactory.of(expectedRaw), Currency.USD.INSTANCE));
			}
		}
	}

	// ── fractional size scale — BTC/USD ──────────────────────────────────

	@Nested
	@DisplayName("when multiplying with fractional size scale")
	final class WhenMultiplyingWithFractionalSizeScale
	{
		private final PriceQuotingConvention<CurrencyPriceUnit<Currency.USD>> priceConvention =
				PriceQuotingConvention.currency(Currency.USD.INSTANCE);

		@ParameterizedTest(name = "{0}")
		@MethodSource("btcSizeCases")
		@DisplayName("divides raw product by ten to the power of size scale")
		void dividesRawProductByTenToThePowerOfSizeScale(final String as, final long priceRaw, final long sizeRaw,
		                                                 final int sizeScale, final long expectedMoneyRaw)
		{
			var sizeConvention = SizeQuotingConvention.units(sizeScale);
			var expected = MoneyTypeFactory.of(TradingNumberFactory.of(expectedMoneyRaw), Currency.USD.INSTANCE);

			var result = MoneyArithmetic.multiply(PriceTypeFactory.of(priceRaw), SizeTypeFactory.of(sizeRaw),
					priceConvention, sizeConvention);

			assertMoneyEquals(as, result, expected);
		}

		private static Stream<Arguments> btcSizeCases()
		{
			return Stream.of(
					// price=$45,000 (4500000 cents) × 1 BTC (10^8 satoshis) = $45,000 (4500000 cents)
					Arguments.of("$45,000 × 1 BTC = $45,000", 4500000L, 100_000_000L, 8, 4500000L),
					// price=$45,000 (4500000 cents) × 2.5 BTC (250000000 satoshis) = $112,500 (11250000 cents)
					Arguments.of("$45,000 × 2.5 BTC = $112,500", 4500000L, 250_000_000L, 8, 11250000L),
					// price=$1 (100 cents) × 0.5 BTC (50000000 satoshis) = $0.50 (50 cents)
					Arguments.of("$1.00 × 0.5 BTC = $0.50", 100L, 50_000_000L, 8, 50L),
					// price=$100 (10000 cents) × 10 units at scale 2 (1000 hundredths) = $1,000 (100000 cents)
					Arguments.of("$100.00 × 10 units (scale 2) = $1,000", 10000L, 1000L, 2, 100000L),
					// zero BTC always yields zero money
					Arguments.of("any price × zero BTC = zero money", 4500000L, 0L, 8, 0L),
					// negative size (short BTC)
					Arguments.of("$45,000 × -1 BTC = -$45,000", 4500000L, -100_000_000L, 8, -4500000L));
		}
	}

	// ── JPY (scale 0) ─────────────────────────────────────────────────────

	@Nested
	@DisplayName("when multiplying in JPY (zero decimal places)")
	final class WhenMultiplyingInJpy
	{
		private final PriceQuotingConvention<CurrencyPriceUnit<Currency.JPY>> priceConvention =
				PriceQuotingConvention.currency(Currency.JPY.INSTANCE);
		private final SizeQuotingConvention<SizeQuotingUnit.Units> sizeConvention = SizeQuotingConvention.units(0);

		@ParameterizedTest(name = "{0}")
		@MethodSource("jpyCases")
		@DisplayName("returns yen notional with no decimal division")
		void returnsYenNotionalWithNoDecimalDivision(final String as, final long priceRaw, final long sizeRaw,
		                                             final long expectedYen)
		{
			var expected = MoneyTypeFactory.of(TradingNumberFactory.of(expectedYen), Currency.JPY.INSTANCE);

			var result = MoneyArithmetic.multiply(PriceTypeFactory.of(priceRaw), SizeTypeFactory.of(sizeRaw),
					priceConvention, sizeConvention);

			assertMoneyEquals(as, result, expected);
		}

		private static Stream<Arguments> jpyCases()
		{
			return Stream.of(Arguments.of("¥150 × 100 shares = ¥15,000", 150L, 100L, 15000L),
					Arguments.of("¥3000 × 0 shares = ¥0", 3000L, 0L, 0L), Arguments.of("¥1 × 1 share = ¥1", 1L, 1L, 1L),
					Arguments.of("short: -¥150 × 100 shares = -¥15,000", -150L, 100L, -15000L),
					Arguments.of("large institutional: ¥5000 × 10,000 shares = ¥50,000,000", 5000L, 10_000L,
							50_000_000L));
		}
	}

	// ── currency propagation ──────────────────────────────────────────────
	// Each currency needs its own method — the specific C type cannot be threaded through a shared record.

	@Nested
	@DisplayName("when checking result currency")
	final class WhenCheckingResultCurrency
	{
		private final SizeQuotingConvention<SizeQuotingUnit.Units> sizeConvention = SizeQuotingConvention.units(0);

		@Test
		@DisplayName("USD convention produces USD money")
		void usdConventionProducesUsdMoney()
		{
			var result = MoneyArithmetic.multiply(PriceTypeFactory.of(100), SizeTypeFactory.of(1),
					PriceQuotingConvention.currency(Currency.USD.INSTANCE), sizeConvention);

			assertThat(result.currency()).as("currency").isSameAs(Currency.USD.INSTANCE);
		}

		@Test
		@DisplayName("EUR convention produces EUR money")
		void eurConventionProducesEurMoney()
		{
			var result = MoneyArithmetic.multiply(PriceTypeFactory.of(100), SizeTypeFactory.of(1),
					PriceQuotingConvention.currency(Currency.EUR.INSTANCE), sizeConvention);

			assertThat(result.currency()).as("currency").isSameAs(Currency.EUR.INSTANCE);
		}

		@Test
		@DisplayName("JPY convention produces JPY money")
		void jpyConventionProducesJpyMoney()
		{
			var result = MoneyArithmetic.multiply(PriceTypeFactory.of(100), SizeTypeFactory.of(1),
					PriceQuotingConvention.currency(Currency.JPY.INSTANCE), sizeConvention);

			assertThat(result.currency()).as("currency").isSameAs(Currency.JPY.INSTANCE);
		}

		@Test
		@DisplayName("GBP convention produces GBP money")
		void gbpConventionProducesGbpMoney()
		{
			var result = MoneyArithmetic.multiply(PriceTypeFactory.of(100), SizeTypeFactory.of(1),
					PriceQuotingConvention.currency(Currency.GBP.INSTANCE), sizeConvention);

			assertThat(result.currency()).as("currency").isSameAs(Currency.GBP.INSTANCE);
		}
	}
}