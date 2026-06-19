package de.gupta.metis.core.types.size.quoted;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
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
			assertQuotedSize(quotedSize.scale(IntegersAsEuclideanDomain.of(2)), 9_000L, conventionCase.convention(),
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
	@DisplayName("when scaling by an integer")
	final class WhenScalingByAnInteger
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsQuotedSizeWithTheRawValueMultipliedByTheScalarCases")
		@DisplayName("returns quoted size with the raw value multiplied by the scalar")
		void returnsQuotedSizeWithTheRawValueMultipliedByTheScalar(final String as, final long value,
		                                                           final long scalar, final long expected)
		{
			var result = quotedSizeInUnitsWithScale2(value).scale(IntegersAsEuclideanDomain.of(scalar));

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

			var recomposed = result.quotient().scale(IntegersAsEuclideanDomain.of(20)).add(result.remainder());

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
}