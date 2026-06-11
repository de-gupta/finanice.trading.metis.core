package de.gupta.metis.core.types;

public sealed interface Currency
		permits Currency.USD, Currency.EUR, Currency.JPY, Currency.GBP, Currency.BTC, Currency.ETH
{
	int canonicalScale();

	record USD() implements Currency
	{
		@Override
		public int canonicalScale() { return 2; }
	}

	record EUR() implements Currency
	{
		@Override
		public int canonicalScale() { return 2; }
	}

	record JPY() implements Currency
	{
		@Override
		public int canonicalScale() { return 0; }
	}

	record GBP() implements Currency
	{
		@Override
		public int canonicalScale() { return 2; }
	}

	record BTC() implements Currency
	{
		@Override
		public int canonicalScale() { return 8; }
	}

	record ETH() implements Currency
	{
		@Override
		public int canonicalScale() { return 9; }
	}
}