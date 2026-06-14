package de.gupta.metis.core.types.number;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TradingNumber")
final class TradingNumberTest
{
	@Nested
	@DisplayName("when checking isZero")
	final class WhenCheckingIsZero
	{
		@Test
		@DisplayName("returns true for the zero singleton")
		void returnsTrueForTheZeroSingleton()
		{
			var zero = TradingNumberFactory.zero();

			assertThat(zero.isZero()).as("zero.isZero()").isTrue();
		}

		@Test
		@DisplayName("returns false for a positive value")
		void returnsFalseForAPositiveValue()
		{
			var number = TradingNumberFactory.of(42L);

			assertThat(number.isZero()).as("42.isZero()").isFalse();
		}

		@Test
		@DisplayName("returns false for a negative value")
		void returnsFalseForANegativeValue()
		{
			var number = TradingNumberFactory.of(-1L);

			assertThat(number.isZero()).as("(-1).isZero()").isFalse();
		}
	}

	@Nested
	@DisplayName("when getting zero")
	final class WhenGettingZero
	{
		@Test
		@DisplayName("returns a value that isZero")
		void returnsAValueThatIsZero()
		{
			var number = TradingNumberFactory.of(100L);

			assertThat(number.zero().isZero()).as("zero.isZero()").isTrue();
		}

		@Test
		@DisplayName("zero from different instances compare equal")
		void zeroFromDifferentInstancesCompareEqual()
		{
			var zeroA = TradingNumberFactory.zero();
			var zeroB = TradingNumberFactory.of(5L).zero();

			assertThat(zeroA.compare(zeroB)).as("zero instances compare equal").isEqualTo(OrderRelation.EQUAL);
		}
	}

	@Nested
	@DisplayName("when getting one")
	final class WhenGettingOne
	{
		@Test
		@DisplayName("returns a value equal to 1")
		void returnsAValueEqualToOne()
		{
			var number = TradingNumberFactory.of(100L);

			var one = number.one();

			assertThat(one.compare(TradingNumberFactory.of(1L)))
					.as("one.compare(1)")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("value-1 factory result and one() compare equal")
		void valueOneSingletonAndOneMethodCompareEqual()
		{
			var fromFactory = TradingNumberFactory.of(1L);
			var fromMethod = TradingNumberFactory.of(50L).one();

			assertThat(fromFactory.compare(fromMethod)).as("both equal one").isEqualTo(OrderRelation.EQUAL);
		}
	}

	@Nested
	@DisplayName("when computing norm")
	final class WhenComputingNorm
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("normCases")
		@DisplayName("returns the absolute value")
		void returnsAbsoluteValue(final String as, final long value, final long expectedNorm)
		{
			var result = TradingNumberFactory.of(value).norm();

			assertThat(result.compare(TradingNumberFactory.of(expectedNorm)))
					.as(as)
					.isEqualTo(OrderRelation.EQUAL);
		}

		private static Stream<Arguments> normCases()
		{
			return Stream.of(
					Arguments.of("norm(5) = 5", 5L, 5L),
					Arguments.of("norm(-5) = 5", -5L, 5L),
					Arguments.of("norm(0) = 0", 0L, 0L),
					Arguments.of("norm(-1) = 1", -1L, 1L),
					Arguments.of("norm(1_000_000) = 1_000_000", 1_000_000L, 1_000_000L)
			);
		}
	}

	@Nested
	@DisplayName("when converting to string")
	final class WhenConvertingToString
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("toStringCases")
		@DisplayName("returns decimal representation")
		void returnsDecimalRepresentation(final String as, final long value, final String expected)
		{
			assertThat(TradingNumberFactory.of(value).toString()).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> toStringCases()
		{
			return Stream.of(
					Arguments.of("positive number", 12345L, "12345"),
					Arguments.of("zero", 0L, "0"),
					Arguments.of("negative number", -99L, "-99"),
					Arguments.of("one", 1L, "1")
			);
		}
	}

	@Nested
	@DisplayName("when creating with int value")
	final class WhenCreatingWithIntValue
	{
		@Test
		@DisplayName("creates a value equal to the same long value")
		void createsAValueEqualToTheSameLongValue()
		{
			var fromInt = TradingNumberFactory.of(42);
			var fromLong = TradingNumberFactory.of(42L);

			assertThat(fromInt.compare(fromLong))
					.as("int and long 42 compare equal")
					.isEqualTo(OrderRelation.EQUAL);
		}

		@Test
		@DisplayName("zero int creates an isZero value")
		void zeroIntCreatesAnIsZeroValue()
		{
			assertThat(TradingNumberFactory.of(0).isZero()).as("0.isZero()").isTrue();
		}
	}
}