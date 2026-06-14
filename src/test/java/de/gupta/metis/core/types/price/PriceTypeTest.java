package de.gupta.metis.core.types.price;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PriceType")
final class PriceTypeTest
{
	@Nested
	@DisplayName("when getting zero")
	final class WhenGettingZero
	{
		@Test
		@DisplayName("returns a price with raw value zero")
		void returnsAPriceWithRawValueZero()
		{
			var price = PriceTypeFactory.of(12345L);

			var zero = price.zero();

			assertThat(zero.value().compare(TradingNumberFactory.zero()))
					.as("zero() raw value")
					.isEqualTo(OrderRelation.EQUAL);
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
			var number = TradingNumberFactory.of(99L);

			var price = PriceTypeFactory.of(number);

			assertThat(price.value().compare(number))
					.as("wrapped value equals original number")
					.isEqualTo(OrderRelation.EQUAL);
		}
	}
}