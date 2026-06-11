package de.gupta.metis.core.types.number;

public final class TradingNumberFactory
{
	public static TradingNumber of(final long value)
	{
		return TradingNumberImpl.from(value);
	}

	public static TradingNumber zero()
	{
		return TradingNumberImpl.from(0);
	}

	private TradingNumberFactory() {}
}