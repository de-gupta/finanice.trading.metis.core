package de.gupta.metis.core.types.arithmetic;

import de.gupta.commons.utility.math.ordering.OrderRelation;
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

@DisplayName("SizeArithmetic#compare")
final class SizeArithmeticCompareTest
{
	@Nested
	@DisplayName("when comparing sizes with the same convention")
	final class WhenComparingWithTheSameConvention
	{
		private final SizeArithmetic arithmetic = SizeArithmetic.of(SizeQuotingConvention.units(0));

		@ParameterizedTest(name = "{0}")
		@MethodSource("compareResultCases")
		@DisplayName("returns the correct ordering")
		void returnsTheCorrectOrdering(final String as, final long left, final long right,
		                               final OrderRelation expected)
		{
			var result = arithmetic.compare(SizeTypeFactory.of(left), SizeTypeFactory.of(right));

			assertThat(result).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("lots convention also compares correctly")
		void lotsConventionAlsoComparesCorrectly()
		{
			var lots = SizeArithmetic.of(SizeQuotingConvention.lots(0));

			var result = lots.compare(SizeTypeFactory.of(5), SizeTypeFactory.of(3));

			assertThat(result)
					.as("5 lots > 3 lots")
					.isEqualTo(OrderRelation.GREATER_THAN);
		}

		private static Stream<Arguments> compareResultCases()
		{
			return Stream.of(
					Arguments.of("long position larger than short", 500L, -200L, OrderRelation.GREATER_THAN),
					Arguments.of("flat vs long", 0L, 100L, OrderRelation.LESS_THAN),
					Arguments.of("equal positions", 300L, 300L, OrderRelation.EQUAL),
					Arguments.of("zero equals zero", 0L, 0L, OrderRelation.EQUAL),
					Arguments.of("two short positions", -100L, -200L, OrderRelation.GREATER_THAN),
					Arguments.of("large vs small", 10_000_000L, 1L, OrderRelation.GREATER_THAN)
			);
		}
	}

	@Nested
	@DisplayName("when comparing sizes with different scales but the same unit")
	final class WhenComparingWithDifferentScales
	{
		@Test
		@DisplayName("normalizes before comparing — equal values at different scales")
		void normalizesBeforeComparingEqualValuesAtDifferentScales()
		{
			var arithmetic = SizeArithmetic.of(SizeQuotingConvention.units(2), SizeQuotingConvention.units(0));

			var result = arithmetic.compare(SizeTypeFactory.of(100), SizeTypeFactory.of(1));

			assertThat(result)
					.as("size(100, scale=2) = size(1, scale=0): both represent 1 unit")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("normalizes before comparing — left less at different scales")
		void normalizesBeforeComparingLeftLessAtDifferentScales()
		{
			var arithmetic = SizeArithmetic.of(SizeQuotingConvention.units(2), SizeQuotingConvention.units(0));

			var result = arithmetic.compare(SizeTypeFactory.of(99), SizeTypeFactory.of(1));

			assertThat(result)
					.as("size(99, scale=2) < size(1, scale=0): 0.99 < 1.00 units")
					.isEqualTo(OrderRelation.LESS_THAN);
		}

		@Test
		@DisplayName("normalizes before comparing — right more precise than left, equal values")
		void normalizesBeforeComparingWhenRightIsMorePreciseEqualValues()
		{
			var arithmetic = SizeArithmetic.of(SizeQuotingConvention.units(0), SizeQuotingConvention.units(2));

			var result = arithmetic.compare(SizeTypeFactory.of(1), SizeTypeFactory.of(100));

			assertThat(result)
					.as("size(1, scale=0) = size(100, scale=2): both represent 1 unit")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("normalizes before comparing — right more precise than left, left greater")
		void normalizesBeforeComparingWhenRightIsMorePreciseLeftGreater()
		{
			var arithmetic = SizeArithmetic.of(SizeQuotingConvention.units(0), SizeQuotingConvention.units(2));

			var result = arithmetic.compare(SizeTypeFactory.of(2), SizeTypeFactory.of(199));

			assertThat(result)
					.as("size(2, scale=0) > size(199, scale=2): 2.00 > 1.99 units")
					.isEqualTo(OrderRelation.GREATER_THAN);
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
			assertThatThrownBy(() -> SizeArithmetic.of(SizeQuotingConvention.units(0), SizeQuotingConvention.lots(0)))
					.as("constructing arithmetic with units and lots conventions should be rejected")
					.isInstanceOf(RuntimeException.class);
		}
	}
}