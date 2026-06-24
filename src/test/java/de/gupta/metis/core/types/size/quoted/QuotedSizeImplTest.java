package de.gupta.metis.core.types.size.quoted;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;
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

@DisplayName("QuotedSizeImpl")
final class QuotedSizeImplTest
{
	private static final SizeQuotingConvention<SizeQuotingUnit.Units> UNITS_2 = SizeQuotingConvention.units(2);

	private static QuotedSize<SizeQuotingUnit.Units> quotedSizeInUnitsWithScale2(final long rawValue)
	{
		return quotedSize(rawValue, UNITS_2);
	}

	private static <U extends SizeQuotingUnit> QuotedSize<U> quotedSize(final long rawValue,
	                                                                    final SizeQuotingConvention<U> convention)
	{
		return QuotedSizeImpl.of(SizeTypeFactory.of(rawValue), convention);
	}

	private static void assertQuotedSize(final QuotedSize<?> actual, final long expectedRawValue,
	                                     final SizeQuotingConvention<?> expectedConvention, final String as)
	{
		assertThat(actual.size().value().compare(TradingNumberFactory.of(expectedRawValue)))
				.as("%s - raw value", as)
				.isEqualTo(OrderRelation.EQUAL);
		assertThat(actual.convention())
				.as("%s - convention", as)
				.isEqualTo(expectedConvention);
	}

	private static void assertRationalNumber(final RationalNumber actual, final long expectedNumerator,
	                                         final long expectedDenominator, final String as)
	{
		assertThat(actual.numerator().value()).as("%s - numerator", as).isEqualTo(expectedNumerator);
		assertThat(actual.denominator().value()).as("%s - denominator", as).isEqualTo(expectedDenominator);
	}

	@Nested
	@DisplayName("when accessing state")
	final class WhenAccessingState
	{
		@Test
		@DisplayName("returns the wrapped size and convention")
		void returnsTheWrappedSizeAndConvention()
		{
			var quotedSize = quotedSizeInUnitsWithScale2(4_500L);

			assertQuotedSize(quotedSize, 4_500L, UNITS_2, "quoted size");
		}
	}

	@Nested
	@DisplayName("when using different size quoting conventions")
	final class WhenUsingDifferentSizeQuotingConventions
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsTheWrappedSizeAndConventionCases")
		@DisplayName("returns the wrapped size and convention")
		void returnsTheWrappedSizeAndConvention(final String as, final ConventionCase conventionCase)
		{
			var quotedSize = quotedSize(4_500L, conventionCase.convention());

			assertQuotedSize(quotedSize, 4_500L, conventionCase.convention(), as);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("preservesTheConventionAcrossUnaryOperationsCases")
		@DisplayName("preserves the convention across unary operations")
		void preservesTheConventionAcrossUnaryOperations(final String as, final ConventionCase conventionCase)
		{
			var quotedSize = quotedSize(4_500L, conventionCase.convention());

			assertQuotedSize(quotedSize.negate(), -4_500L, conventionCase.convention(), "%s - negate".formatted(as));
			assertQuotedSize(quotedSize.zero(), 0L, conventionCase.convention(), "%s - zero".formatted(as));
			assertQuotedSize(quotedSize.scale(IntegralNumberFactory.of(2)), 9_000L, conventionCase.convention(),
					"%s - scale".formatted(as));

			var divisionResult = quotedSize.divide(2_000);

			assertQuotedSize(divisionResult.quotient(), 2L, conventionCase.convention(),
					"%s - quotient".formatted(as));
			assertQuotedSize(divisionResult.remainder(), 500L, conventionCase.convention(),
					"%s - remainder".formatted(as));
		}

		private static Stream<Arguments> returnsTheWrappedSizeAndConventionCases()
		{
			return conventionCases().map(conventionCase -> Arguments.of(conventionCase.as(), conventionCase));
		}

		private static Stream<ConventionCase> conventionCases()
		{
			return Stream.of(
					ConventionCase.of("units with fractional scale", SizeQuotingConvention.units(2)),
					ConventionCase.of("lots with fractional scale", SizeQuotingConvention.lots(3)),
					ConventionCase.of("contracts with whole-number scale", SizeQuotingConvention.contracts(0))
			);
		}

		private static Stream<Arguments> preservesTheConventionAcrossUnaryOperationsCases()
		{
			return conventionCases().map(conventionCase -> Arguments.of(conventionCase.as(), conventionCase));
		}

		private record ConventionCase(String as, SizeQuotingConvention<?> convention)
		{
			private static ConventionCase of(final String as, final SizeQuotingConvention<?> convention)
			{
				return new ConventionCase(as, convention);
			}
		}
	}

	@Nested
	@DisplayName("when negating")
	final class WhenNegating
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsQuotedSizeWithNegatedRawValueAndSameConventionCases")
		@DisplayName("returns quoted size with negated raw value and same convention")
		void returnsQuotedSizeWithNegatedRawValueAndSameConvention(final String as, final long value,
		                                                           final long expected)
		{
			var result = quotedSizeInUnitsWithScale2(value).negate();

			assertQuotedSize(result, expected, UNITS_2, as);
		}

		private static Stream<Arguments> returnsQuotedSizeWithNegatedRawValueAndSameConventionCases()
		{
			return Stream.of(
					Arguments.of("negate positive", 4_500L, -4_500L),
					Arguments.of("negate negative", -125L, 125L),
					Arguments.of("negate zero", 0L, 0L)
			);
		}
	}

	@Nested
	@DisplayName("when adding")
	final class WhenAdding
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsTheSumWithTheSameConventionCases")
		@DisplayName("returns the sum with the same convention")
		void returnsTheSumWithTheSameConvention(final String as, final long left, final long right,
		                                        final long expected)
		{
			var result = quotedSizeInUnitsWithScale2(left).add(quotedSizeInUnitsWithScale2(right));

			assertQuotedSize(result, expected, UNITS_2, as);
		}

		private static Stream<Arguments> returnsTheSumWithTheSameConventionCases()
		{
			return Stream.of(
					Arguments.of("4500 + 125 = 4625", 4_500L, 125L, 4_625L),
					Arguments.of("4500 + 0 = 4500", 4_500L, 0L, 4_500L),
					Arguments.of("negative and positive values combine", -300L, 125L, -175L)
			);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsTheSumWithDifferentScalesButTheSameUnitCases")
		@DisplayName("returns the sum normalized to the more precise convention when scales differ")
		void returnsTheSumNormalizedToTheMorePreciseConventionWhenScalesDiffer(final String as,
		                                                                       final AdditionCase additionCase)
		{
			var result = quotedSize(additionCase.leftRawValue(), additionCase.leftConvention())
					.add(quotedSize(additionCase.rightRawValue(), additionCase.rightConvention()));

			assertQuotedSize(result, additionCase.expectedRawValue(), additionCase.expectedConvention(), as);
		}

		private static Stream<Arguments> returnsTheSumWithDifferentScalesButTheSameUnitCases()
		{
			return Stream.of(
					AdditionCase.of("left more precise than right",
							SizeQuotingConvention.units(2), 150L,
							SizeQuotingConvention.units(0), 1L,
							250L, SizeQuotingConvention.units(2)),
					AdditionCase.of("right more precise than left",
							SizeQuotingConvention.units(0), 1L,
							SizeQuotingConvention.units(2), 150L,
							250L, SizeQuotingConvention.units(2)),
					AdditionCase.of("zero at a different scale leaves left unchanged",
							SizeQuotingConvention.units(3), 750L,
							SizeQuotingConvention.units(1), 0L,
							750L, SizeQuotingConvention.units(3))
			).map(additionCase -> Arguments.of(additionCase.as(), additionCase));
		}

		private record AdditionCase(String as, SizeQuotingConvention<SizeQuotingUnit.Units> leftConvention,
		                            long leftRawValue, SizeQuotingConvention<SizeQuotingUnit.Units> rightConvention,
		                            long rightRawValue, long expectedRawValue,
		                            SizeQuotingConvention<SizeQuotingUnit.Units> expectedConvention)
		{
			private static AdditionCase of(final String as,
			                               final SizeQuotingConvention<SizeQuotingUnit.Units> leftConvention,
			                               final long leftRawValue,
			                               final SizeQuotingConvention<SizeQuotingUnit.Units> rightConvention,
			                               final long rightRawValue, final long expectedRawValue,
			                               final SizeQuotingConvention<SizeQuotingUnit.Units> expectedConvention)
			{
				return new AdditionCase(as, leftConvention, leftRawValue, rightConvention, rightRawValue,
						expectedRawValue, expectedConvention);
			}
		}
	}

	@Nested
	@DisplayName("when getting zero")
	final class WhenGettingZero
	{
		@Test
		@DisplayName("returns quoted size with zero raw value and same convention")
		void returnsQuotedSizeWithZeroRawValueAndSameConvention()
		{
			var result = quotedSizeInUnitsWithScale2(4_500L).zero();

			assertQuotedSize(result, 0L, UNITS_2, "zero()");
		}
	}

	@Nested
	@DisplayName("when checking whether the quoted size is zero")
	final class WhenCheckingWhetherTheQuotedSizeIsZero
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsWhetherTheQuotedSizeIsZeroCases")
		@DisplayName("returns whether the quoted size is zero")
		void returnsWhetherTheQuotedSizeIsZero(final String as, final ZeroCase zeroCase)
		{
			var quotedSize = quotedSize(zeroCase.rawValue(), zeroCase.convention());

			assertThat(quotedSize.isZero())
					.as("%s - isZero()", as)
					.isEqualTo(zeroCase.expected());
		}

		private static Stream<Arguments> returnsWhetherTheQuotedSizeIsZeroCases()
		{
			return Stream.of(
					ZeroCase.of("zero raw value at whole-unit scale", 0L, SizeQuotingConvention.units(0), true),
					ZeroCase.of("zero raw value at fractional scale", 0L, SizeQuotingConvention.units(8), true),
					ZeroCase.of("positive raw value is not zero", 1L, SizeQuotingConvention.units(0), false),
					ZeroCase.of("negative raw value is not zero", -1L, SizeQuotingConvention.units(3), false)
			).map(zeroCase -> Arguments.of(zeroCase.as(), zeroCase));
		}

		private record ZeroCase(String as, long rawValue, SizeQuotingConvention<SizeQuotingUnit.Units> convention,
		                        boolean expected)
		{
			private static ZeroCase of(final String as, final long rawValue,
			                           final SizeQuotingConvention<SizeQuotingUnit.Units> convention,
			                           final boolean expected)
			{
				return new ZeroCase(as, rawValue, convention, expected);
			}
		}
	}

	@Nested
	@DisplayName("when checking whether quoted sizes are equal")
	final class WhenCheckingWhetherQuotedSizesAreEqual
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsWhetherTheQuotedSizesAreEqualCases")
		@DisplayName("returns whether the quoted sizes are equal")
		void returnsWhetherTheQuotedSizesAreEqual(final String as, final EqualityCase equalityCase)
		{
			var left = quotedSize(equalityCase.leftRawValue(), equalityCase.leftConvention());
			var right = quotedSize(equalityCase.rightRawValue(), equalityCase.rightConvention());

			assertThat(left.isEqualTo(right))
					.as("%s - left.isEqualTo(right)", as)
					.isEqualTo(equalityCase.expected());
			assertThat(right.isEqualTo(left))
					.as("%s - right.isEqualTo(left)", as)
					.isEqualTo(equalityCase.expected());
		}

		private static Stream<Arguments> returnsWhetherTheQuotedSizesAreEqualCases()
		{
			return Stream.of(
					EqualityCase.of("same convention and same raw value", 450L, SizeQuotingConvention.units(2),
							450L, SizeQuotingConvention.units(2), true),
					EqualityCase.of("different scales with the same represented value", 45L,
							SizeQuotingConvention.units(2), 450L, SizeQuotingConvention.units(3), true),
					EqualityCase.of("different scales with different represented values", 45L,
							SizeQuotingConvention.units(2), 451L, SizeQuotingConvention.units(3), false),
					EqualityCase.of("negative values compare equal across scales", -45L,
							SizeQuotingConvention.units(2), -450L, SizeQuotingConvention.units(3), true),
					EqualityCase.of("zero compares equal across scales", 0L,
							SizeQuotingConvention.units(0), 0L, SizeQuotingConvention.units(8), true)
			).map(equalityCase -> Arguments.of(equalityCase.as(), equalityCase));
		}

		private record EqualityCase(String as, long leftRawValue,
		                            SizeQuotingConvention<SizeQuotingUnit.Units> leftConvention, long rightRawValue,
		                            SizeQuotingConvention<SizeQuotingUnit.Units> rightConvention, boolean expected)
		{
			private static EqualityCase of(final String as, final long leftRawValue,
			                               final SizeQuotingConvention<SizeQuotingUnit.Units> leftConvention,
			                               final long rightRawValue,
			                               final SizeQuotingConvention<SizeQuotingUnit.Units> rightConvention,
			                               final boolean expected)
			{
				return new EqualityCase(as, leftRawValue, leftConvention, rightRawValue, rightConvention, expected);
			}
		}
	}

	@Nested
	@DisplayName("when comparing quoted sizes")
	final class WhenComparingQuotedSizes
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsTheOrderRelationCases")
		@DisplayName("returns the order relation")
		void returnsTheOrderRelation(final String as, final ComparisonCase comparisonCase)
		{
			var left = quotedSize(comparisonCase.leftRawValue(), comparisonCase.leftConvention());
			var right = quotedSize(comparisonCase.rightRawValue(), comparisonCase.rightConvention());

			assertThat(left.compare(right))
					.as("%s - left.compare(right)", as)
					.isEqualTo(comparisonCase.expected());
		}

		private static Stream<Arguments> returnsTheOrderRelationCases()
		{
			return Stream.of(
					ComparisonCase.of("same convention greater than", 451L, SizeQuotingConvention.units(2),
							450L, SizeQuotingConvention.units(2), OrderRelation.GREATER_THAN),
					ComparisonCase.of("same convention less than", 449L, SizeQuotingConvention.units(2),
							450L, SizeQuotingConvention.units(2), OrderRelation.LESS_THAN),
					ComparisonCase.of("same convention equal", 450L, SizeQuotingConvention.units(2),
							450L, SizeQuotingConvention.units(2), OrderRelation.EQUAL),
					ComparisonCase.of("different scales equal values", 45L, SizeQuotingConvention.units(2),
							450L, SizeQuotingConvention.units(3), OrderRelation.EQUAL),
					ComparisonCase.of("different scales left less", 45L, SizeQuotingConvention.units(2),
							451L, SizeQuotingConvention.units(3), OrderRelation.LESS_THAN),
					ComparisonCase.of("different scales left greater", 46L, SizeQuotingConvention.units(2),
							451L, SizeQuotingConvention.units(3), OrderRelation.GREATER_THAN),
					ComparisonCase.of("negative and positive", -1L, SizeQuotingConvention.units(0),
							1L, SizeQuotingConvention.units(0), OrderRelation.LESS_THAN)
			).map(comparisonCase -> Arguments.of(comparisonCase.as(), comparisonCase));
		}

		private record ComparisonCase(String as, long leftRawValue,
		                              SizeQuotingConvention<SizeQuotingUnit.Units> leftConvention, long rightRawValue,
		                              SizeQuotingConvention<SizeQuotingUnit.Units> rightConvention,
		                              OrderRelation expected)
		{
			private static ComparisonCase of(final String as, final long leftRawValue,
			                                 final SizeQuotingConvention<SizeQuotingUnit.Units> leftConvention,
			                                 final long rightRawValue,
			                                 final SizeQuotingConvention<SizeQuotingUnit.Units> rightConvention,
			                                 final OrderRelation expected)
			{
				return new ComparisonCase(as, leftRawValue, leftConvention, rightRawValue, rightConvention, expected);
			}
		}
	}

	@Nested
	@DisplayName("when requoting")
	final class WhenRequoting
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsQuotedSizeWithTheTargetConventionCases")
		@DisplayName("returns quoted size with the target convention")
		void returnsQuotedSizeWithTheTargetConvention(final String as, final RequoteCase requoteCase)
		{
			var result = quotedSize(requoteCase.sourceRawValue(), requoteCase.sourceConvention())
					.requote(requoteCase.targetConvention());

			assertQuotedSize(result, requoteCase.expectedRawValue(), requoteCase.targetConvention(), as);
		}

		private static Stream<Arguments> returnsQuotedSizeWithTheTargetConventionCases()
		{
			return Stream.of(
					RequoteCase.of("same scale keeps raw value unchanged",
							SizeQuotingConvention.units(2), 450L,
							SizeQuotingConvention.units(2), 450L),
					RequoteCase.of("upscaling multiplies by the scale factor",
							SizeQuotingConvention.units(2), 45L,
							SizeQuotingConvention.units(3), 450L),
					RequoteCase.of("downscaling truncates the quotient",
							SizeQuotingConvention.units(3), 451L,
							SizeQuotingConvention.units(2), 45L)
			).map(requoteCase -> Arguments.of(requoteCase.as(), requoteCase));
		}

		private record RequoteCase(String as, SizeQuotingConvention<SizeQuotingUnit.Units> sourceConvention,
		                           long sourceRawValue, SizeQuotingConvention<SizeQuotingUnit.Units> targetConvention,
		                           long expectedRawValue)
		{
			private static RequoteCase of(final String as,
			                              final SizeQuotingConvention<SizeQuotingUnit.Units> sourceConvention,
			                              final long sourceRawValue,
			                              final SizeQuotingConvention<SizeQuotingUnit.Units> targetConvention,
			                              final long expectedRawValue)
			{
				return new RequoteCase(as, sourceConvention, sourceRawValue, targetConvention, expectedRawValue);
			}
		}
	}

	@Nested
	@DisplayName("when scaling by an integer")
	final class WhenScalingByAnInteger
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsQuotedSizeWithTheRawValueMultipliedByTheScalarCases")
		@DisplayName("returns quoted size with the raw value multiplied by the scalar")
		void returnsQuotedSizeWithTheRawValueMultipliedByTheScalar(final String as, final long value,
		                                                           final long scalar, final long expected)
		{
			var result = quotedSizeInUnitsWithScale2(value).scale(IntegralNumberFactory.of(scalar));

			assertQuotedSize(result, expected, UNITS_2, as);
		}

		private static Stream<Arguments> returnsQuotedSizeWithTheRawValueMultipliedByTheScalarCases()
		{
			return Stream.of(
					Arguments.of("positive value times positive scalar", 150L, 3L, 450L),
					Arguments.of("positive value times zero scalar", 150L, 0L, 0L),
					Arguments.of("positive value times negative scalar", 150L, -2L, -300L)
			);
		}
	}

	@Nested
	@DisplayName("when dividing by an integer")
	final class WhenDividingByAnInteger
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsQuotientAndRemainderWithTheSameConventionCases")
		@DisplayName("returns quotient and remainder with the same convention")
		void returnsQuotientAndRemainderWithTheSameConvention(final String as, final long dividend,
		                                                      final int divisor, final long expectedQuotient,
		                                                      final long expectedRemainder)
		{
			var result = quotedSizeInUnitsWithScale2(dividend).divide(divisor);

			assertQuotedSize(result.quotient(), expectedQuotient, UNITS_2, "%s - quotient".formatted(as));
			assertQuotedSize(result.remainder(), expectedRemainder, UNITS_2, "%s - remainder".formatted(as));
		}

		@Test
		@DisplayName("satisfies dividend equals divisor times quotient plus remainder")
		void satisfiesDividendEqualsDivisorTimesQuotientPlusRemainder()
		{
			var dividend = quotedSizeInUnitsWithScale2(5_612L);
			DivisionResult<QuotedSize<SizeQuotingUnit.Units>> result = dividend.divide(20);

			var recomposed = result.quotient().scale(IntegralNumberFactory.of(20)).add(result.remainder());

			assertQuotedSize(recomposed, 5_612L, UNITS_2, "20 * quotient + remainder");
		}

		@Test
		@DisplayName("throws arithmetic exception for zero divisor")
		void throwsArithmeticExceptionForZeroDivisor()
		{
			assertThatThrownBy(() -> quotedSizeInUnitsWithScale2(4_500L).divide(0))
					.as("divide by zero")
					.isInstanceOf(ArithmeticException.class);
		}

		private static Stream<Arguments> returnsQuotientAndRemainderWithTheSameConventionCases()
		{
			return Stream.of(
					Arguments.of("exact division", 4_500L, 2, 2_250L, 0L),
					Arguments.of("inexact division", 5_612L, 20, 280L, 12L),
					Arguments.of("negative dividend uses floor division", -5_612L, 20, -281L, 8L)
			);
		}
	}

	@Nested
	@DisplayName("when computing a ratio")
	final class WhenComputingARatio
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsExactConventionAwareRatioCases")
		@DisplayName("returns the exact convention-aware ratio")
		void returnsTheExactConventionAwareRatio(final String as, final RatioCase ratioCase)
		{
			var numerator = quotedSize(ratioCase.numeratorRawValue(), ratioCase.numeratorConvention());
			var denominator = quotedSize(ratioCase.denominatorRawValue(), ratioCase.denominatorConvention());

			assertRationalNumber(numerator.ratio(denominator), ratioCase.expectedNumerator(),
					ratioCase.expectedDenominator(), as);
		}

		private static Stream<Arguments> returnsExactConventionAwareRatioCases()
		{
			return Stream.of(
					RatioCase.of("same scale 120 / 100 = 6/5",
							SizeQuotingConvention.units(0), 120L,
							SizeQuotingConvention.units(0), 100L,
							6L, 5L),
					RatioCase.of("more precise numerator scale preserves equality",
							SizeQuotingConvention.units(3), 450L,
							SizeQuotingConvention.units(2), 45L,
							1L, 1L),
					RatioCase.of("less precise numerator scale preserves equality",
							SizeQuotingConvention.units(2), 45L,
							SizeQuotingConvention.units(3), 450L,
							1L, 1L),
					RatioCase.of("mixed scales preserve exact fractional ratio",
							SizeQuotingConvention.units(1), 120L,
							SizeQuotingConvention.units(2), 10000L,
							3L, 25L)
			).map(ratioCase -> Arguments.of(ratioCase.as(), ratioCase));
		}

		private record RatioCase(String as, SizeQuotingConvention<SizeQuotingUnit.Units> numeratorConvention,
		                         long numeratorRawValue,
		                         SizeQuotingConvention<SizeQuotingUnit.Units> denominatorConvention,
		                         long denominatorRawValue, long expectedNumerator, long expectedDenominator)
		{
			private static RatioCase of(final String as,
			                            final SizeQuotingConvention<SizeQuotingUnit.Units> numeratorConvention,
			                            final long numeratorRawValue,
			                            final SizeQuotingConvention<SizeQuotingUnit.Units> denominatorConvention,
			                            final long denominatorRawValue, final long expectedNumerator,
			                            final long expectedDenominator)
			{
				return new RatioCase(as, numeratorConvention, numeratorRawValue, denominatorConvention,
						denominatorRawValue, expectedNumerator, expectedDenominator);
			}
		}
	}
}
