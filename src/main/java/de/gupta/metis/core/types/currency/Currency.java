package de.gupta.metis.core.types.currency;

public interface Currency
{
	String code();

	String name();

	int canonicalScale();

	final class USD extends AbstractCurrency
	{
		public static final USD INSTANCE = new USD();

		@Override
		public String code()
		{
			return "USD";
		}

		@Override
		public String name()
		{
			return "US Dollar";
		}

		private USD()
		{
		}

		@Override
		public int canonicalScale()
		{
			return 2;
		}
	}

	final class EUR extends AbstractCurrency
	{
		public static final EUR INSTANCE = new EUR();

		@Override
		public String code()
		{
			return "EUR";
		}

		@Override
		public String name()
		{
			return "Euro";
		}

		private EUR()
		{
		}

		@Override
		public int canonicalScale()
		{
			return 2;
		}
	}

	final class JPY extends AbstractCurrency
	{
		public static final JPY INSTANCE = new JPY();

		@Override
		public String code()
		{
			return "JPY";
		}

		@Override
		public String name()
		{
			return "Japanese Yen";
		}

		private JPY()
		{
		}

		@Override
		public int canonicalScale()
		{
			return 0;
		}
	}

	final class GBP extends AbstractCurrency
	{
		public static final GBP INSTANCE = new GBP();

		@Override
		public String code()
		{
			return "GBP";
		}

		@Override
		public String name()
		{
			return "Pound Sterling";
		}

		private GBP()
		{
		}

		@Override
		public int canonicalScale()
		{
			return 2;
		}
	}

	final class BTC extends AbstractCurrency
	{
		public static final BTC INSTANCE = new BTC();

		@Override
		public String code()
		{
			return "BTC";
		}

		@Override
		public String name()
		{
			return "Bitcoin";
		}

		private BTC()
		{
		}

		@Override
		public int canonicalScale()
		{
			return 8;
		}
	}

	final class ETH extends AbstractCurrency
	{
		public static final ETH INSTANCE = new ETH();

		@Override
		public String code()
		{
			return "ETH";
		}

		@Override
		public String name()
		{
			return "Ether";
		}

		private ETH()
		{
		}

		@Override
		public int canonicalScale()
		{
			return 9;
		}
	}
}