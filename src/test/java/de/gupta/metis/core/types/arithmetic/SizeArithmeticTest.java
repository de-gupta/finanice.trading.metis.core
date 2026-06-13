package de.gupta.metis.core.types.arithmetic;

import de.gupta.commons.utility.comparison.ComparisonResult;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.size.SizeTypeFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SizeArithmetic")
final class SizeArithmeticTest
{
	@Nested
	@DisplayName("when adding sizes with units convention")
	final class WhenAddingWithUnitsConvention
	{
		private final SizeArithmetic arithmetic = SizeArithmetic.of(SizeQuotingConvention.units(0));

		@ParameterizedTest(name = "{0}")
		@MethodSource("additionCases")
		@DisplayName("returns the sum of raw values")
		void returnsTheSumOfRawValues(final String as, final long left, final long right, final long expectedSum)
		{
			var result = arithmetic.add(SizeTypeFactory.of(left), SizeTypeFactory.of(right));

			assertThat(result.value().compare(SizeTypeFactory.of(expectedSum).value()))
					.as(as)
					.isEqualTo(ComparisonResult.EQUAL);
		}

		@Test
		@DisplayName("is commutative")
		void isCommutative()
		{
			var left = SizeTypeFactory.of(300);
			var right = SizeTypeFactory.of(200);

			var leftPlusRight = arithmetic.add(left, right);
			var rightPlusLeft = arithmetic.add(right, left);

			assertThat(leftPlusRight.value().compare(rightPlusLeft.value()))
					.as("add(300, 200) should equal add(200, 300)")
					.isEqualTo(ComparisonResult.EQUAL);
		}

		@Test
		@DisplayName("is associative")
		void isAssociative()
		{
			var a = SizeTypeFactory.of(100);
			var b = SizeTypeFactory.of(200);
			var c = SizeTypeFactory.of(300);

			var leftGrouped = arithmetic.add(arithmetic.add(a, b), c);
			var rightGrouped = arithmetic.add(a, arithmetic.add(b, c));

			assertThat(leftGrouped.value().compare(rightGrouped.value()))
					.as("(a+b)+c should equal a+(b+c)")
					.isEqualTo(ComparisonResult.EQUAL);
		}

		@Test
		@DisplayName("adding inverse yields zero")
		void addingInverseYieldsZero()
		{
			var size = SizeTypeFactory.of(750);

			var result = arithmetic.add(size, arithmetic.negate(size));

			assertThat(result.value().compare(TradingNumberFactory.zero()))
					.as("size + (-size) should be zero")
					.isEqualTo(ComparisonResult.EQUAL);
		}

		private static Stream<Arguments> additionCases()
		{
			return Stream.of(
					Arguments.of("buy + buy accumulates", 100L, 50L, 150L),
					Arguments.of("add zero to size", 75L, 0L, 75L),
					Arguments.of("zero + size", 0L, 75L, 75L),
					Arguments.of("zero + zero", 0L, 0L, 0L),
					Arguments.of("reduce long position", 500L, -200L, 300L),
					Arguments.of("close long position exactly", 500L, -500L, 0L),
					Arguments.of("flip to short", 100L, -300L, -200L),
					Arguments.of("add to short", -200L, -100L, -300L),
					Arguments.of("reduce short position", -300L, 150L, -150L),
					Arguments.of("close short position exactly", -400L, 400L, 0L),
					Arguments.of("single-lot trade", 1L, 1L, 2L),
					Arguments.of("large institutional sizes", 1_000_000L, 2_000_000L, 3_000_000L),
					Arguments.of("large opposing institutional sizes", 5_000_000L, -5_000_000L, 0L)
			);
		}
	}

	@Nested
	@DisplayName("when adding sizes with lots convention")
	final class WhenAddingWithLotsConvention
	{
		private final SizeArithmetic arithmetic = SizeArithmetic.of(SizeQuotingConvention.lots(0));

		@ParameterizedTest(name = "{0}")
		@MethodSource("lotsCases")
		@DisplayName("treats raw values as lot counts")
		void treatsRawValuesAsLotCounts(final String as, final long left, final long right, final long expectedSum)
		{
			var result = arithmetic.add(SizeTypeFactory.of(left), SizeTypeFactory.of(right));

			assertThat(result.value().compare(SizeTypeFactory.of(expectedSum).value()))
					.as(as)
					.isEqualTo(ComparisonResult.EQUAL);
		}

		private static Stream<Arguments> lotsCases()
		{
			return Stream.of(
					Arguments.of("5 lots + 3 lots", 5L, 3L, 8L),
					Arguments.of("10 lots - 10 lots", 10L, -10L, 0L),
					Arguments.of("fractional lots at scale 2", 150L, 250L, 400L)
			);
		}
	}

	@Nested
	@DisplayName("when adding sizes with fractional scale")
	final class WhenAddingWithFractionalScale
	{
		@Test
		@DisplayName("scale normalization — left more precise than right")
		void scaleNormalizationLeftMorePreciseThanRight()
		{
			// left at scale 2: value 150 = 1.50 BTC
			// right at scale 0: value 1 = 1 BTC — normalized to scale 2 = 100
			// sum at scale 2: 150 + 100 = 250
			var arithmetic = SizeArithmetic.of(
					SizeQuotingConvention.units(2),
					SizeQuotingConvention.units(0));

			var result = arithmetic.add(SizeTypeFactory.of(150), SizeTypeFactory.of(1));

			assertThat(result.value().compare(SizeTypeFactory.of(250).value()))
					.as("size(150, scale=2) + size(1, scale=0) should equal size(250)")
					.isEqualTo(ComparisonResult.EQUAL);
		}
	}

	@Nested
	@DisplayName("when adding sizes with incompatible conventions")
	final class WhenAddingWithIncompatibleConventions
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("incompatibleCases")
		@DisplayName("throws for mismatched unit kinds")
		void throwsForMismatchedUnitKinds(final String as, final SizeArithmetic arithmetic)
		{
			assertThatThrownBy(() -> arithmetic.add(SizeTypeFactory.of(10), SizeTypeFactory.of(5)))
					.as(as)
					.isInstanceOf(RuntimeException.class);
		}

		private static Stream<Arguments> incompatibleCases()
		{
			return Stream.of(
					Arguments.of("units vs lots",
							SizeArithmetic.of(SizeQuotingConvention.units(0), SizeQuotingConvention.lots(0))),
					Arguments.of("units vs contracts",
							SizeArithmetic.of(SizeQuotingConvention.units(0), SizeQuotingConvention.contracts(0))),
					Arguments.of("lots vs contracts",
							SizeArithmetic.of(SizeQuotingConvention.lots(0), SizeQuotingConvention.contracts(0)))
			);
		}
	}

	@Nested
	@DisplayName("when negating")
	final class WhenNegating
	{
		private final SizeArithmetic arithmetic = SizeArithmetic.of(SizeQuotingConvention.units(0));

		@ParameterizedTest(name = "{0}")
		@MethodSource("negationCases")
		@DisplayName("returns size with negated raw value")
		void returnsSizeWithNegatedRawValue(final String as, final long value, final long expectedNegated)
		{
			var result = arithmetic.negate(SizeTypeFactory.of(value));

			assertThat(result.value().compare(SizeTypeFactory.of(expectedNegated).value()))
					.as(as)
					.isEqualTo(ComparisonResult.EQUAL);
		}

		@Test
		@DisplayName("double negation returns original value")
		void doubleNegationReturnsOriginalValue()
		{
			var size = SizeTypeFactory.of(12345);

			var result = arithmetic.negate(arithmetic.negate(size));

			assertThat(result.value().compare(size.value()))
					.as("negate(negate(size)) should equal size")
					.isEqualTo(ComparisonResult.EQUAL);
		}

		private static Stream<Arguments> negationCases()
		{
			return Stream.of(
					Arguments.of("negate long position", 500L, -500L),
					Arguments.of("negate short position", -200L, 200L),
					Arguments.of("negate zero", 0L, 0L),
					Arguments.of("negate single unit", 1L, -1L),
					Arguments.of("negate large position", 10_000_000L, -10_000_000L)
			);
		}
	}

	@Nested
	@DisplayName("when getting zero")
	final class WhenGettingZero
	{
		@Test
		@DisplayName("returns size with raw value of zero")
		void returnsSizeWithRawValueOfZero()
		{
			var arithmetic = SizeArithmetic.of(SizeQuotingConvention.units(0));

			var result = arithmetic.zero();

			assertThat(result.value().compare(TradingNumberFactory.zero()))
					.as("zero() should return a size whose raw value is zero")
					.isEqualTo(ComparisonResult.EQUAL);
		}

		@Test
		@DisplayName("acts as additive identity on both sides")
		void actsAsAdditiveIdentityOnBothSides()
		{
			var arithmetic = SizeArithmetic.of(SizeQuotingConvention.units(0));
			var size = SizeTypeFactory.of(500);

			var addZeroRight = arithmetic.add(size, arithmetic.zero());
			var addZeroLeft = arithmetic.add(arithmetic.zero(), size);

			assertThat(addZeroRight.value().compare(size.value()))
					.as("size + zero should equal size")
					.isEqualTo(ComparisonResult.EQUAL);
			assertThat(addZeroLeft.value().compare(size.value()))
					.as("zero + size should equal size")
					.isEqualTo(ComparisonResult.EQUAL);
		}
	}
}