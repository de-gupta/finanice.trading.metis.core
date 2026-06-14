package de.gupta.metis.core.types.arithmetic;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.price.PriceTypeFactory;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PriceArithmetic")
final class PriceArithmeticTest
{
	@Nested
	@DisplayName("when adding prices with the same convention")
	final class WhenAddingPricesWithTheSameConvention
	{
		private final PriceQuotingConvention<PriceQuotingUnit.Ticks> convention = PriceQuotingConvention.ticks(2);
		private final PriceArithmetic arithmetic = PriceArithmetic.of(convention);

		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsTheSumOfRawValuesCases")
		@DisplayName("returns the sum of raw values")
		void returnsTheSumOfRawValues(final String as, final long left, final long right, final long expectedSum)
		{
			var result = arithmetic.add(PriceTypeFactory.of(left), PriceTypeFactory.of(right));

			assertThat(result.value().compare(PriceTypeFactory.of(expectedSum).value()))
					.as(as)
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("thirty-seconds convention adds correctly")
		void thirtySecondsConventionAddsCorrectly()
		{
			var thirtySecondsArithmetic = PriceArithmetic.of(PriceQuotingConvention.thirtySeconds(0));

			var result = thirtySecondsArithmetic.add(PriceTypeFactory.of(3248), PriceTypeFactory.of(8));

			assertThat(result.value().compare(PriceTypeFactory.of(3256).value()))
					.as("101-16 (raw 3248) + 0-08 (raw 8) = 101-24 (raw 3256) in thirty-seconds notation")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("is commutative")
		void isCommutative()
		{
			var left = PriceTypeFactory.of(45);
			var right = PriceTypeFactory.of(30);

			var leftPlusRight = arithmetic.add(left, right);
			var rightPlusLeft = arithmetic.add(right, left);

			assertThat(leftPlusRight.value().compare(rightPlusLeft.value()))
					.as("add(45, 30) should equal add(30, 45)")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("is associative")
		void isAssociative()
		{
			var a = PriceTypeFactory.of(10);
			var b = PriceTypeFactory.of(20);
			var c = PriceTypeFactory.of(30);

			var leftGrouped = arithmetic.add(arithmetic.add(a, b), c);
			var rightGrouped = arithmetic.add(a, arithmetic.add(b, c));

			assertThat(leftGrouped.value().compare(rightGrouped.value()))
					.as("(a+b)+c should equal a+(b+c)")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("adding inverse yields zero")
		void addingInverseYieldsZero()
		{
			var price = PriceTypeFactory.of(12345);

			var result = arithmetic.add(price, arithmetic.negate(price));

			assertThat(result.value().compare(TradingNumberFactory.zero()))
					.as("price + (-price) should be zero")
					.isEqualTo(OrderRelation.EQUAL);
		}

		private static Stream<Arguments> returnsTheSumOfRawValuesCases()
		{
			return Stream.of(
					Arguments.of("positive + positive", 45L, 30L, 75L),
					Arguments.of("positive + zero", 100L, 0L, 100L),
					Arguments.of("zero + positive", 0L, 100L, 100L),
					Arguments.of("zero + zero", 0L, 0L, 0L),
					Arguments.of("positive + negative (net positive)", 100L, -40L, 60L),
					Arguments.of("positive + negative (net negative)", 30L, -70L, -40L),
					Arguments.of("negative + negative", -20L, -30L, -50L),
					Arguments.of("sum to zero", 500L, -500L, 0L),
					Arguments.of("single unit", 1L, 1L, 2L),
					Arguments.of("large positive values", 4_000_000L, 5_000_000L, 9_000_000L),
					Arguments.of("large negative values", -4_000_000L, -5_000_000L, -9_000_000L),
					Arguments.of("large opposing values", 9_000_000_000L, -9_000_000_000L, 0L)
			);
		}
	}

	@Nested
	@DisplayName("when adding prices with different scales but the same unit")
	final class WhenAddingPricesWithDifferentScalesButTheSameUnit
	{
		@Test
		@DisplayName("normalizes right up to left scale when left is more precise")
		void normalizesRightUpToLeftScaleWhenLeftIsMorePrecise()
		{
			var leftConvention = PriceQuotingConvention.ticks(3);
			var rightConvention = PriceQuotingConvention.ticks(2);
			var arithmetic = PriceArithmetic.of(leftConvention, rightConvention);

			var result = arithmetic.add(PriceTypeFactory.of(450), PriceTypeFactory.of(45));

			assertThat(result.value().compare(PriceTypeFactory.of(900).value()))
					.as("price(450, scale=3) + price(45, scale=2): right normalized ×10 → 450 + 450 = 900")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("large scale difference — scale 4 and scale 1")
		void largeScaleDifferenceFourAndOne()
		{
			var leftConvention = PriceQuotingConvention.ticks(4);
			var rightConvention = PriceQuotingConvention.ticks(1);
			var arithmetic = PriceArithmetic.of(leftConvention, rightConvention);

			var result = arithmetic.add(PriceTypeFactory.of(10000), PriceTypeFactory.of(1));

			assertThat(result.value().compare(PriceTypeFactory.of(11000).value()))
					.as("price(10000, scale=4) + price(1, scale=1): right normalized ×1000 → 10000 + 1000 = 11000")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("zero right operand at different scale yields left unchanged")
		void zeroRightOperandAtDifferentScaleYieldsLeftUnchanged()
		{
			var arithmetic = PriceArithmetic.of(PriceQuotingConvention.ticks(3), PriceQuotingConvention.ticks(1));

			var result = arithmetic.add(PriceTypeFactory.of(750), PriceTypeFactory.of(0));

			assertThat(result.value().compare(PriceTypeFactory.of(750).value()))
					.as("price(750) + zero(any scale) = price(750)")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("normalizes left up to right scale when right is more precise")
		void normalizesLeftUpToRightScaleWhenRightIsMorePrecise()
		{
			var leftConvention = PriceQuotingConvention.ticks(2);
			var rightConvention = PriceQuotingConvention.ticks(3);
			var arithmetic = PriceArithmetic.of(leftConvention, rightConvention);

			var result = arithmetic.add(PriceTypeFactory.of(45), PriceTypeFactory.of(450));

			assertThat(result.value().compare(PriceTypeFactory.of(900).value()))
					.as("price(45, scale=2) + price(450, scale=3): left normalized ×10 → 450 + 450 = 900 at scale 3")
					.isEqualTo(OrderRelation.EQUAL);
		}
	}

	@Nested
	@DisplayName("when constructing with incompatible conventions")
	final class WhenConstructingWithIncompatibleConventions
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("throwsForMismatchedUnitKindsCases")
		@DisplayName("throws for mismatched unit kinds")
		void throwsForMismatchedUnitKinds(final String as, final IncompatibleCase tc)
		{
			assertThatThrownBy(() -> PriceArithmetic.of(tc.left(), tc.right()))
					.as(as)
					.isInstanceOf(RuntimeException.class);
		}

		private static Stream<Arguments> throwsForMismatchedUnitKindsCases()
		{
			return Stream.of(
					new IncompatibleCase("ticks vs thirty-seconds",
							PriceQuotingConvention.ticks(2), PriceQuotingConvention.thirtySeconds(2)),
					new IncompatibleCase("thirty-seconds vs ticks",
							PriceQuotingConvention.thirtySeconds(2), PriceQuotingConvention.ticks(2)),
					new IncompatibleCase("ticks vs currency",
							PriceQuotingConvention.ticks(2), PriceQuotingConvention.currency(Currency.USD.INSTANCE)),
					new IncompatibleCase("currency vs ticks",
							PriceQuotingConvention.currency(Currency.USD.INSTANCE), PriceQuotingConvention.ticks(2))
			).map(tc -> Arguments.of(tc.as(), tc));
		}

		private record IncompatibleCase(String as, PriceQuotingConvention<?> left, PriceQuotingConvention<?> right)
		{
		}
	}

	@Nested
	@DisplayName("when negating")
	final class WhenNegating
	{
		private final PriceArithmetic arithmetic = PriceArithmetic.of(PriceQuotingConvention.ticks(2));

		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsPriceWithNegatedRawValueCases")
		@DisplayName("returns price with negated raw value")
		void returnsPriceWithNegatedRawValue(final String as, final long value, final long expectedNegated)
		{
			var result = arithmetic.negate(PriceTypeFactory.of(value));

			assertThat(result.value().compare(PriceTypeFactory.of(expectedNegated).value()))
					.as(as)
					.isEqualTo(OrderRelation.EQUAL);
		}

		private static Stream<Arguments> returnsPriceWithNegatedRawValueCases()
		{
			return Stream.of(
					Arguments.of("negate positive", 45L, -45L),
					Arguments.of("negate negative", -30L, 30L),
					Arguments.of("negate zero", 0L, 0L),
					Arguments.of("negate single unit", 1L, -1L),
					Arguments.of("negate large value", 9_000_000_000L, -9_000_000_000L),
					Arguments.of("negate large negative", -9_000_000_000L, 9_000_000_000L)
			);
		}

		@Test
		@DisplayName("double negation returns original value")
		void doubleNegationReturnsOriginalValue()
		{
			var price = PriceTypeFactory.of(54321);

			var result = arithmetic.negate(arithmetic.negate(price));

			assertThat(result.value().compare(price.value()))
					.as("negate(negate(price)) should equal price")
					.isEqualTo(OrderRelation.EQUAL);
		}
	}

	@Nested
	@DisplayName("when getting zero")
	final class WhenGettingZero
	{
		private final PriceArithmetic arithmetic = PriceArithmetic.of(PriceQuotingConvention.ticks(2));

		@Test
		@DisplayName("returns price with raw value of zero")
		void returnsPriceWithRawValueOfZero()
		{
			var result = arithmetic.zero();

			assertThat(result.value().compare(TradingNumberFactory.zero()))
					.as("zero() raw value should be zero")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("acts as additive identity on the right")
		void actsAsAdditiveIdentityOnTheRight()
		{
			var price = PriceTypeFactory.of(75);

			var result = arithmetic.add(price, arithmetic.zero());

			assertThat(result.value().compare(price.value()))
					.as("price + zero should equal price")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("acts as additive identity on the left")
		void actsAsAdditiveIdentityOnTheLeft()
		{
			var price = PriceTypeFactory.of(75);

			var result = arithmetic.add(arithmetic.zero(), price);

			assertThat(result.value().compare(price.value()))
					.as("zero + price should equal price")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("raw value is zero regardless of convention scale")
		void rawValueIsZeroRegardlessOfConventionScale()
		{
			var arith0 = PriceArithmetic.of(PriceQuotingConvention.ticks(0));
			var arith5 = PriceArithmetic.of(PriceQuotingConvention.ticks(5));

			assertThat(arith0.zero().value().compare(arith5.zero().value()))
					.as("zero at scale 0 and zero at scale 5 both have raw value zero")
					.isEqualTo(OrderRelation.EQUAL);
		}
	}

	@Nested
	@DisplayName("when arithmetic overflows")
	final class WhenArithmeticOverflows
	{
		private final PriceArithmetic arithmetic = PriceArithmetic.of(PriceQuotingConvention.ticks(0));

		@Test
		@DisplayName("add throws when the sum overflows long")
		void addThrowsWhenTheSumOverflowsLong()
		{
			var nearMax = PriceTypeFactory.of(Long.MAX_VALUE / 2 + 1);

			assertThatThrownBy(() -> arithmetic.add(nearMax, nearMax))
					.as("adding two values whose sum exceeds Long.MAX_VALUE should throw")
					.isInstanceOf(ArithmeticException.class);
		}

		@Test
		@DisplayName("negate throws for Long.MIN_VALUE")
		void negateThrowsForLongMinValue()
		{
			var minValue = PriceTypeFactory.of(Long.MIN_VALUE);

			assertThatThrownBy(() -> arithmetic.negate(minValue))
					.as("negating Long.MIN_VALUE overflows because Math.abs(Long.MIN_VALUE) > Long.MAX_VALUE")
					.isInstanceOf(ArithmeticException.class);
		}
	}
}