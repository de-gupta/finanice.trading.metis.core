package de.gupta.metis.core.types.currency;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Currency")
final class CurrencyTest
{
	@Nested
	@DisplayName("USD")
	final class Usd
	{
		private final Currency.USD currency = Currency.USD.INSTANCE;

		@Test
		@DisplayName("has code USD")
		void hasCodeUsd()
		{
			assertThat(currency.code()).as("code").isEqualTo("USD");
		}

		@Test
		@DisplayName("has name US Dollar")
		void hasNameUsDollar()
		{
			assertThat(currency.name()).as("name").isEqualTo("US Dollar");
		}

		@Test
		@DisplayName("has canonical scale 2")
		void hasCanonicalScaleTwo()
		{
			assertThat(currency.canonicalScale()).as("canonical scale").isEqualTo(2);
		}

		@Test
		@DisplayName("toString returns the code")
		void toStringReturnsTheCode()
		{
			assertThat(currency.toString()).as("toString").isEqualTo("USD");
		}
	}

	@Nested
	@DisplayName("EUR")
	final class Eur
	{
		private final Currency.EUR currency = Currency.EUR.INSTANCE;

		@Test
		@DisplayName("has code EUR")
		void hasCodeEur()
		{
			assertThat(currency.code()).as("code").isEqualTo("EUR");
		}

		@Test
		@DisplayName("has name Euro")
		void hasNameEuro()
		{
			assertThat(currency.name()).as("name").isEqualTo("Euro");
		}

		@Test
		@DisplayName("has canonical scale 2")
		void hasCanonicalScaleTwo()
		{
			assertThat(currency.canonicalScale()).as("canonical scale").isEqualTo(2);
		}

		@Test
		@DisplayName("toString returns the code")
		void toStringReturnsTheCode()
		{
			assertThat(currency.toString()).as("toString").isEqualTo("EUR");
		}
	}

	@Nested
	@DisplayName("JPY")
	final class Jpy
	{
		private final Currency.JPY currency = Currency.JPY.INSTANCE;

		@Test
		@DisplayName("has code JPY")
		void hasCodeJpy()
		{
			assertThat(currency.code()).as("code").isEqualTo("JPY");
		}

		@Test
		@DisplayName("has name Japanese Yen")
		void hasNameJapaneseYen()
		{
			assertThat(currency.name()).as("name").isEqualTo("Japanese Yen");
		}

		@Test
		@DisplayName("has canonical scale 0")
		void hasCanonicalScaleZero()
		{
			assertThat(currency.canonicalScale()).as("canonical scale").isEqualTo(0);
		}

		@Test
		@DisplayName("toString returns the code")
		void toStringReturnsTheCode()
		{
			assertThat(currency.toString()).as("toString").isEqualTo("JPY");
		}
	}

	@Nested
	@DisplayName("GBP")
	final class Gbp
	{
		private final Currency.GBP currency = Currency.GBP.INSTANCE;

		@Test
		@DisplayName("has code GBP")
		void hasCodeGbp()
		{
			assertThat(currency.code()).as("code").isEqualTo("GBP");
		}

		@Test
		@DisplayName("has name Pound Sterling")
		void hasNamePoundSterling()
		{
			assertThat(currency.name()).as("name").isEqualTo("Pound Sterling");
		}

		@Test
		@DisplayName("has canonical scale 2")
		void hasCanonicalScaleTwo()
		{
			assertThat(currency.canonicalScale()).as("canonical scale").isEqualTo(2);
		}

		@Test
		@DisplayName("toString returns the code")
		void toStringReturnsTheCode()
		{
			assertThat(currency.toString()).as("toString").isEqualTo("GBP");
		}
	}

	@Nested
	@DisplayName("BTC")
	final class Btc
	{
		private final Currency.BTC currency = Currency.BTC.INSTANCE;

		@Test
		@DisplayName("has code BTC")
		void hasCodeBtc()
		{
			assertThat(currency.code()).as("code").isEqualTo("BTC");
		}

		@Test
		@DisplayName("has name Bitcoin")
		void hasNameBitcoin()
		{
			assertThat(currency.name()).as("name").isEqualTo("Bitcoin");
		}

		@Test
		@DisplayName("has canonical scale 8")
		void hasCanonicalScaleEight()
		{
			assertThat(currency.canonicalScale()).as("canonical scale").isEqualTo(8);
		}

		@Test
		@DisplayName("toString returns the code")
		void toStringReturnsTheCode()
		{
			assertThat(currency.toString()).as("toString").isEqualTo("BTC");
		}
	}

	@Nested
	@DisplayName("ETH")
	final class Eth
	{
		private final Currency.ETH currency = Currency.ETH.INSTANCE;

		@Test
		@DisplayName("has code ETH")
		void hasCodeEth()
		{
			assertThat(currency.code()).as("code").isEqualTo("ETH");
		}

		@Test
		@DisplayName("has name Ether")
		void hasNameEther()
		{
			assertThat(currency.name()).as("name").isEqualTo("Ether");
		}

		@Test
		@DisplayName("has canonical scale 9")
		void hasCanonicalScaleNine()
		{
			assertThat(currency.canonicalScale()).as("canonical scale").isEqualTo(9);
		}

		@Test
		@DisplayName("toString returns the code")
		void toStringReturnsTheCode()
		{
			assertThat(currency.toString()).as("toString").isEqualTo("ETH");
		}
	}
}
