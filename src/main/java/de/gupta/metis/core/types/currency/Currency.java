package de.gupta.metis.core.types.currency;

@FunctionalInterface
public interface Currency
{
	int canonicalScale();

	enum USD implements Currency
	{
		INSTANCE;

		@Override
		public int canonicalScale()
		{
			return 2;
		}
	}

	enum EUR implements Currency
	{
		INSTANCE;

		@Override
		public int canonicalScale()
		{
			return 2;
		}
	}

	enum JPY implements Currency
	{
		INSTANCE;

		@Override
		public int canonicalScale()
		{
			return 0;
		}
	}

	enum GBP implements Currency
	{
		INSTANCE;

		@Override
		public int canonicalScale()
		{
			return 2;
		}
	}

	enum BTC implements Currency
	{
		INSTANCE;

		@Override
		public int canonicalScale()
		{
			return 8;
		}
	}

	enum ETH implements Currency
	{
		INSTANCE;

		@Override
		public int canonicalScale()
		{
			return 9;
		}
	}
}