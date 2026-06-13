package de.gupta.metis.core.types.size;

import de.gupta.commons.utility.comparison.ComparisonResult;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SizeType")
final class SizeTypeTest
{
	@Nested
	@DisplayName("when getting zero")
	final class WhenGettingZero
	{
		@Test
		@DisplayName("returns a size with raw value zero")
		void returnsASizeWithRawValueZero()
		{
			var size = SizeTypeFactory.of(500L);

			var zero = size.zero();

			assertThat(zero.value().compare(TradingNumberFactory.zero()))
					.as("zero() raw value")
					.isEqualTo(ComparisonResult.EQUAL);
		}
	}

	@Nested
	@DisplayName("when creating from TradingNumber")
	final class WhenCreatingFromTradingNumber
	{
		@Test
		@DisplayName("wraps the trading number unchanged")
		void wrapsTheTradingNumberUnchanged()
		{
			var number = TradingNumberFactory.of(77L);

			var size = SizeTypeFactory.of(number);

			assertThat(size.value().compare(number))
					.as("wrapped value equals original number")
					.isEqualTo(ComparisonResult.EQUAL);
		}
	}
}
