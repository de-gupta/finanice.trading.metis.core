package de.gupta.metis.core.types.quoting.utility;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.exception.IncompatibleInputException;
import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.quoting.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("QuotingConventionRequoting#requote")
final class QuotingConventionRequotingTest
{
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static TradingNumber requoteUnsafely(final long sourceRawValue, final QuotingConvention<?> sourceConvention,
	                                             final QuotingConvention<?> targetConvention)
	{
		return QuotingConventionRequoting.requote(TradingNumberFactory.of(sourceRawValue),
				(QuotingConvention) sourceConvention, (QuotingConvention) targetConvention);
	}

	private static void assertTradingNumber(final TradingNumber actual, final long expectedRawValue, final String as)
	{
		assertThat(actual.compare(TradingNumberFactory.of(expectedRawValue)))
				.as("%s - raw value", as)
				.isEqualTo(OrderRelation.EQUAL);
	}

	@Nested
	@DisplayName("when source and target conventions are compatible")
	final class WhenSourceAndTargetConventionsAreCompatible
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsTheRequotedValueCases")
		@DisplayName("returns the requoted value using the target scale")
		<U extends QuotingUnit> void returnsTheRequotedValueUsingTheTargetScale(final String as,
		                                                                        final RequoteCase<U> requoteCase)
		{
			var result = QuotingConventionRequoting.requote(
					TradingNumberFactory.of(requoteCase.sourceRawValue()),
					requoteCase.sourceConvention(),
					requoteCase.targetConvention());

			assertTradingNumber(result, requoteCase.expectedRawValue(), as);
		}

		private static Stream<Arguments> returnsTheRequotedValueCases()
		{
			return Stream.of(
					RequoteCase.of("same ticks scale keeps raw value unchanged",
							450L, PriceQuotingConvention.ticks(2), PriceQuotingConvention.ticks(2), 450L),
					RequoteCase.of("ticks upscale multiplies by ten",
							45L, PriceQuotingConvention.ticks(2), PriceQuotingConvention.ticks(3), 450L),
					RequoteCase.of("ticks downscale truncates the quotient",
							451L, PriceQuotingConvention.ticks(3), PriceQuotingConvention.ticks(2), 45L),
					RequoteCase.of("negative ticks downscale uses current quotient semantics",
							-451L, PriceQuotingConvention.ticks(3), PriceQuotingConvention.ticks(2), -46L),
					RequoteCase.of("large scale difference multiplies by one thousand",
							1L, PriceQuotingConvention.ticks(1), PriceQuotingConvention.ticks(4), 1_000L),
					RequoteCase.of("zero stays zero when upscaling size units",
							0L, SizeQuotingConvention.units(0), SizeQuotingConvention.units(8), 0L),
					RequoteCase.of("size downscale truncates fractional remainder",
							12_345L, SizeQuotingConvention.units(4), SizeQuotingConvention.units(2), 123L),
					RequoteCase.of("currency conventions with the same currency can be requoted",
							4_500_000L,
							new PriceQuotingConvention<>(new CurrencyPriceUnit<>(Currency.USD.INSTANCE), 2),
							new PriceQuotingConvention<>(new CurrencyPriceUnit<>(Currency.USD.INSTANCE), 3),
							45_000_000L)
			).map(requoteCase -> Arguments.of(requoteCase.as(), requoteCase));
		}

		private record RequoteCase<U extends QuotingUnit>(String as, long sourceRawValue,
		                                                  QuotingConvention<U> sourceConvention,
		                                                  QuotingConvention<U> targetConvention,
		                                                  long expectedRawValue)
		{
			private static <U extends QuotingUnit> RequoteCase<U> of(final String as, final long sourceRawValue,
			                                                         final QuotingConvention<U> sourceConvention,
			                                                         final QuotingConvention<U> targetConvention,
			                                                         final long expectedRawValue)
			{
				return new RequoteCase<>(as, sourceRawValue, sourceConvention, targetConvention, expectedRawValue);
			}
		}
	}

	@Nested
	@DisplayName("when source and target conventions are incompatible")
	final class WhenSourceAndTargetConventionsAreIncompatible
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("throwsIncompatibleInputExceptionCases")
		@DisplayName("throws incompatible input exception")
		void throwsIncompatibleInputException(final String as, final IncompatibleCase incompatibleCase)
		{
			assertThatThrownBy(
					() -> requoteUnsafely(incompatibleCase.sourceRawValue(), incompatibleCase.sourceConvention(),
							incompatibleCase.targetConvention()))
					.as("%s - thrown exception", as)
					.isInstanceOf(IncompatibleInputException.class)
					.hasMessageContaining("Incompatible quoting conventions");
		}

		@Test
		@DisplayName("rejects different currencies even when both are currency price units")
		void rejectsDifferentCurrenciesEvenWhenBothAreCurrencyPriceUnits()
		{
			var sourceConvention = new PriceQuotingConvention<>(new CurrencyPriceUnit<>(Currency.USD.INSTANCE), 2);
			var targetConvention = new PriceQuotingConvention<>(new CurrencyPriceUnit<>(Currency.EUR.INSTANCE), 2);

			assertThatThrownBy(() -> requoteUnsafely(4_500L, sourceConvention, targetConvention))
					.as("USD currency price unit should not be compatible with EUR currency price unit")
					.isInstanceOf(IncompatibleInputException.class)
					.hasMessageContaining("Incompatible quoting conventions");
		}

		private static Stream<Arguments> throwsIncompatibleInputExceptionCases()
		{
			return Stream.of(
					IncompatibleCase.of("ticks and thirty-seconds are incompatible", 450L,
							PriceQuotingConvention.ticks(2), PriceQuotingConvention.thirtySeconds(2)),
					IncompatibleCase.of("size units and size lots are incompatible", 450L,
							SizeQuotingConvention.units(2), SizeQuotingConvention.lots(2)),
					IncompatibleCase.of("price ticks and currency price units are incompatible", 450L,
							PriceQuotingConvention.ticks(2), PriceQuotingConvention.currency(Currency.USD.INSTANCE))
			).map(incompatibleCase -> Arguments.of(incompatibleCase.as(), incompatibleCase));
		}

		private record IncompatibleCase(String as, long sourceRawValue, QuotingConvention<?> sourceConvention,
		                                QuotingConvention<?> targetConvention)
		{
			private static IncompatibleCase of(final String as, final long sourceRawValue,
			                                   final QuotingConvention<?> sourceConvention,
			                                   final QuotingConvention<?> targetConvention)
			{
				return new IncompatibleCase(as, sourceRawValue, sourceConvention, targetConvention);
			}
		}
	}
}