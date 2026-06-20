package de.gupta.metis.core.types.price.quoted;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.ordering.OrderRelation;
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

@DisplayName("QuotedPriceImpl")
final class QuotedPriceImplTest
{
	private static final PriceQuotingConvention<PriceQuotingUnit.Ticks> TICKS_2 = PriceQuotingConvention.ticks(2);

	private static QuotedPrice<PriceQuotingUnit.Ticks> ticks2(final long rawValue)
	{
		return quotedPrice(rawValue, TICKS_2);
	}

	private static <U extends PriceQuotingUnit> QuotedPrice<U> quotedPrice(final long rawValue,
	                                                                       final PriceQuotingConvention<U> convention)
	{
		return QuotedPriceImpl.of(PriceTypeFactory.of(rawValue), convention);
	}

	private static void assertQuotedPrice(final QuotedPrice<?> actual, final long expectedRawValue,
	                                      final PriceQuotingConvention<?> expectedConvention, final String as)
	{
		assertThat(actual.price().value().compare(TradingNumberFactory.of(expectedRawValue)))
				.as("%s - raw value", as)
				.isEqualTo(OrderRelation.EQUAL);
		assertThat(actual.convention())
				.as("%s - convention", as)
				.isEqualTo(expectedConvention);
	}

	@Nested
	@DisplayName("when accessing state")
	final class WhenAccessingState
	{
		@Test
		@DisplayName("returns the wrapped price and convention")
		void returnsTheWrappedPriceAndConvention()
		{
			var quotedPrice = ticks2(4_500L);

			assertQuotedPrice(quotedPrice, 4_500L, TICKS_2, "quoted price");
		}
	}

	@Nested
	@DisplayName("when negating")
	final class WhenNegating
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsQuotedPriceWithNegatedRawValueCases")
		@DisplayName("returns quoted price with negated raw value and same convention")
		void returnsQuotedPriceWithNegatedRawValueAndSameConvention(final String as, final long value,
		                                                            final long expected)
		{
			var result = ticks2(value).negate();

			assertQuotedPrice(result, expected, TICKS_2, as);
		}

		private static Stream<Arguments> returnsQuotedPriceWithNegatedRawValueCases()
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
		@MethodSource("returnsTheSumWithSameConventionCases")
		@DisplayName("returns the sum with the same convention")
		void returnsTheSumWithSameConvention(final String as, final long left, final long right,
		                                     final long expected)
		{
			var result = ticks2(left).add(ticks2(right));

			assertQuotedPrice(result, expected, TICKS_2, as);
		}

		private static Stream<Arguments> returnsTheSumWithSameConventionCases()
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
			var result = quotedPrice(additionCase.leftRawValue(), additionCase.leftConvention())
					.add(quotedPrice(additionCase.rightRawValue(), additionCase.rightConvention()));

			assertQuotedPrice(result, additionCase.expectedRawValue(), additionCase.expectedConvention(), as);
		}

		private static Stream<Arguments> returnsTheSumWithDifferentScalesButTheSameUnitCases()
		{
			return Stream.of(
					AdditionCase.of("left more precise than right",
							PriceQuotingConvention.ticks(3), 450L,
							PriceQuotingConvention.ticks(2), 45L,
							900L, PriceQuotingConvention.ticks(3)),
					AdditionCase.of("right more precise than left",
							PriceQuotingConvention.ticks(2), 45L,
							PriceQuotingConvention.ticks(3), 450L,
							900L, PriceQuotingConvention.ticks(3)),
					AdditionCase.of("zero at a different scale leaves left unchanged",
							PriceQuotingConvention.ticks(3), 750L,
							PriceQuotingConvention.ticks(1), 0L,
							750L, PriceQuotingConvention.ticks(3))
			).map(additionCase -> Arguments.of(additionCase.as(), additionCase));
		}

		private record AdditionCase(String as, PriceQuotingConvention<PriceQuotingUnit.Ticks> leftConvention,
		                            long leftRawValue, PriceQuotingConvention<PriceQuotingUnit.Ticks> rightConvention,
		                            long rightRawValue, long expectedRawValue,
		                            PriceQuotingConvention<PriceQuotingUnit.Ticks> expectedConvention)
		{
			private static AdditionCase of(final String as,
			                               final PriceQuotingConvention<PriceQuotingUnit.Ticks> leftConvention,
			                               final long leftRawValue,
			                               final PriceQuotingConvention<PriceQuotingUnit.Ticks> rightConvention,
			                               final long rightRawValue, final long expectedRawValue,
			                               final PriceQuotingConvention<PriceQuotingUnit.Ticks> expectedConvention)
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
		@DisplayName("returns quoted price with zero raw value and same convention")
		void returnsQuotedPriceWithZeroRawValueAndSameConvention()
		{
			var result = ticks2(4_500L).zero();

			assertQuotedPrice(result, 0L, TICKS_2, "zero()");
		}
	}

	@Nested
	@DisplayName("when checking whether the quoted price is zero")
	final class WhenCheckingWhetherTheQuotedPriceIsZero
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsWhetherTheQuotedPriceIsZeroCases")
		@DisplayName("returns whether the quoted price is zero")
		void returnsWhetherTheQuotedPriceIsZero(final String as, final long rawValue, final boolean expected)
		{
			var quotedPrice = ticks2(rawValue);

			assertThat(quotedPrice.isZero())
					.as("%s - isZero()", as)
					.isEqualTo(expected);
		}

		private static Stream<Arguments> returnsWhetherTheQuotedPriceIsZeroCases()
		{
			return Stream.of(
					Arguments.of("zero raw value", 0L, true),
					Arguments.of("positive raw value", 1L, false),
					Arguments.of("negative raw value", -1L, false)
			);
		}
	}

	@Nested
	@DisplayName("when requoting")
	final class WhenRequoting
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsQuotedPriceWithTheTargetConventionCases")
		@DisplayName("returns quoted price with the target convention")
		void returnsQuotedPriceWithTheTargetConvention(final String as, final RequoteCase requoteCase)
		{
			var result = quotedPrice(requoteCase.sourceRawValue(), requoteCase.sourceConvention())
					.requote(requoteCase.targetConvention());

			assertQuotedPrice(result, requoteCase.expectedRawValue(), requoteCase.targetConvention(), as);
		}

		private static Stream<Arguments> returnsQuotedPriceWithTheTargetConventionCases()
		{
			return Stream.of(
					RequoteCase.of("same scale keeps raw value unchanged",
							PriceQuotingConvention.ticks(2), 450L,
							PriceQuotingConvention.ticks(2), 450L),
					RequoteCase.of("upscaling multiplies by the scale factor",
							PriceQuotingConvention.ticks(2), 45L,
							PriceQuotingConvention.ticks(3), 450L),
					RequoteCase.of("downscaling truncates the quotient",
							PriceQuotingConvention.ticks(3), 451L,
							PriceQuotingConvention.ticks(2), 45L)
			).map(requoteCase -> Arguments.of(requoteCase.as(), requoteCase));
		}

		private record RequoteCase(String as, PriceQuotingConvention<PriceQuotingUnit.Ticks> sourceConvention,
		                           long sourceRawValue, PriceQuotingConvention<PriceQuotingUnit.Ticks> targetConvention,
		                           long expectedRawValue)
		{
			private static RequoteCase of(final String as,
			                              final PriceQuotingConvention<PriceQuotingUnit.Ticks> sourceConvention,
			                              final long sourceRawValue,
			                              final PriceQuotingConvention<PriceQuotingUnit.Ticks> targetConvention,
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
		@MethodSource("returnsScaledQuotedPriceCases")
		@DisplayName("returns quoted price with the raw value multiplied by the scalar")
		void returnsQuotedPriceWithTheRawValueMultipliedByTheScalar(final String as, final long value,
		                                                            final long scalar, final long expected)
		{
			var result = ticks2(value).scale(IntegersAsEuclideanDomain.of(scalar));

			assertQuotedPrice(result, expected, TICKS_2, as);
		}

		private static Stream<Arguments> returnsScaledQuotedPriceCases()
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
		@MethodSource("returnsQuotientAndRemainderCases")
		@DisplayName("returns quotient and remainder with the same convention")
		void returnsQuotientAndRemainderWithTheSameConvention(final String as, final long dividend,
		                                                      final int divisor, final long expectedQuotient,
		                                                      final long expectedRemainder)
		{
			var result = ticks2(dividend).divide(divisor);

			assertQuotedPrice(result.quotient(), expectedQuotient, TICKS_2, "%s - quotient".formatted(as));
			assertQuotedPrice(result.remainder(), expectedRemainder, TICKS_2, "%s - remainder".formatted(as));
		}

		@Test
		@DisplayName("satisfies dividend equals divisor times quotient plus remainder")
		void satisfiesDividendEqualsDivisorTimesQuotientPlusRemainder()
		{
			var dividend = ticks2(5_612L);
			DivisionResult<QuotedPrice<PriceQuotingUnit.Ticks>> result = dividend.divide(20);

			var recomposed = result.quotient().scale(IntegersAsEuclideanDomain.of(20)).add(result.remainder());

			assertQuotedPrice(recomposed, 5_612L, TICKS_2, "20 * quotient + remainder");
		}

		@Test
		@DisplayName("throws arithmetic exception for zero divisor")
		void throwsArithmeticExceptionForZeroDivisor()
		{
			assertThatThrownBy(() -> ticks2(4_500L).divide(0))
					.as("divide by zero")
					.isInstanceOf(ArithmeticException.class);
		}

		private static Stream<Arguments> returnsQuotientAndRemainderCases()
		{
			return Stream.of(
					Arguments.of("exact division", 4_500L, 2, 2_250L, 0L),
					Arguments.of("inexact division", 5_612L, 20, 280L, 12L),
					Arguments.of("negative dividend uses floor division", -5_612L, 20, -281L, 8L)
			);
		}
	}
}
