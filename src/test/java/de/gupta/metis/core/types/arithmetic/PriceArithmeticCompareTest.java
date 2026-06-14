package de.gupta.metis.core.types.arithmetic;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.metis.core.types.price.PriceTypeFactory;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PriceArithmetic#compare")
final class PriceArithmeticCompareTest
{
	@Nested
	@DisplayName("when comparing prices with the same convention")
	final class WhenComparingPricesWithTheSameConvention
	{
		private final PriceArithmetic arithmetic = PriceArithmetic.of(PriceQuotingConvention.ticks(2));

		@ParameterizedTest(name = "{0}")
		@MethodSource("compareResultCases")
		@DisplayName("returns the correct ordering")
		void returnsTheCorrectOrdering(final String as, final long left, final long right,
		                               final OrderRelation expected)
		{
			var result = arithmetic.compare(PriceTypeFactory.of(left), PriceTypeFactory.of(right));

			assertThat(result).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("is antisymmetric — compare(a, b) is the inverse of compare(b, a)")
		void isAntisymmetric()
		{
			var a = PriceTypeFactory.of(70);
			var b = PriceTypeFactory.of(30);

			var aVsB = arithmetic.compare(a, b);
			var bVsA = arithmetic.compare(b, a);

			assertThat(aVsB).as("a > b").isEqualTo(OrderRelation.GREATER_THAN);
			assertThat(bVsA).as("b < a").isEqualTo(OrderRelation.LESS_THAN);
		}

		@Test
		@DisplayName("thirty-seconds convention compares correctly")
		void thirtySecondsConventionComparesCorrectly()
		{
			var thirtySeconds = PriceArithmetic.of(PriceQuotingConvention.thirtySeconds(0));

			var result = thirtySeconds.compare(PriceTypeFactory.of(3248), PriceTypeFactory.of(3216));

			assertThat(result)
					.as("101-16 (raw 3248) > 100-16 (raw 3216) in thirty-seconds notation")
					.isEqualTo(OrderRelation.GREATER_THAN);
		}

		private static Stream<Arguments> compareResultCases()
		{
			return Stream.of(
					Arguments.of("greater than", 100L, 50L, OrderRelation.GREATER_THAN),
					Arguments.of("less than", 50L, 100L, OrderRelation.LESS_THAN),
					Arguments.of("equal", 75L, 75L, OrderRelation.EQUAL),
					Arguments.of("zero equals zero", 0L, 0L, OrderRelation.EQUAL),
					Arguments.of("positive vs negative", 1L, -1L, OrderRelation.GREATER_THAN),
					Arguments.of("negative vs positive", -1L, 1L, OrderRelation.LESS_THAN),
					Arguments.of("large values", 9_000_000_000L, 8_999_999_999L, OrderRelation.GREATER_THAN)
			);
		}
	}

	@Nested
	@DisplayName("when comparing prices with different scales but the same unit")
	final class WhenComparingWithDifferentScales
	{
		@Test
		@DisplayName("normalizes before comparing — equal values at different scales")
		void normalizesBeforeComparingEqualValuesAtDifferentScales()
		{
			var arithmetic = PriceArithmetic.of(PriceQuotingConvention.ticks(3), PriceQuotingConvention.ticks(2));

			var result = arithmetic.compare(PriceTypeFactory.of(450), PriceTypeFactory.of(45));

			assertThat(result)
					.as("price(450, scale=3) = price(45, scale=2): both represent 0.450 ticks")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("normalizes before comparing — left greater at different scales")
		void normalizesBeforeComparingLeftGreaterAtDifferentScales()
		{
			var arithmetic = PriceArithmetic.of(PriceQuotingConvention.ticks(3), PriceQuotingConvention.ticks(2));

			var result = arithmetic.compare(PriceTypeFactory.of(451), PriceTypeFactory.of(45));

			assertThat(result)
					.as("price(451, scale=3) > price(45, scale=2): 0.451 > 0.450 ticks")
					.isEqualTo(OrderRelation.GREATER_THAN);
		}

		@Test
		@DisplayName("normalizes before comparing — right more precise than left, equal values")
		void normalizesBeforeComparingWhenRightIsMorePreciseEqualValues()
		{
			var arithmetic = PriceArithmetic.of(PriceQuotingConvention.ticks(2), PriceQuotingConvention.ticks(3));

			var result = arithmetic.compare(PriceTypeFactory.of(45), PriceTypeFactory.of(450));

			assertThat(result)
					.as("price(45, scale=2) = price(450, scale=3): both represent 0.450 ticks")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("normalizes before comparing — right more precise than left, left less")
		void normalizesBeforeComparingWhenRightIsMorePreciseLeftLess()
		{
			var arithmetic = PriceArithmetic.of(PriceQuotingConvention.ticks(2), PriceQuotingConvention.ticks(3));

			var result = arithmetic.compare(PriceTypeFactory.of(45), PriceTypeFactory.of(451));

			assertThat(result)
					.as("price(45, scale=2) < price(451, scale=3): 0.450 < 0.451 ticks")
					.isEqualTo(OrderRelation.LESS_THAN);
		}
	}

	@Nested
	@DisplayName("when constructing with incompatible conventions")
	final class WhenConstructingWithIncompatibleConventions
	{
		@Test
		@DisplayName("throws for mismatched unit kinds")
		void throwsForMismatchedUnitKinds()
		{
			assertThatThrownBy(() -> PriceArithmetic.of(
					PriceQuotingConvention.ticks(2), PriceQuotingConvention.thirtySeconds(2)))
					.as("constructing arithmetic with ticks and thirty-seconds conventions should be rejected")
					.isInstanceOf(RuntimeException.class);
		}
	}
}