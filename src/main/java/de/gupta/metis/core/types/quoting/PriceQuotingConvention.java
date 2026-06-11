package de.gupta.metis.core.types.quoting;

public record PriceQuotingConvention(PriceQuotingUnit unit, int scale) implements QuotingConvention
{
	public static PriceQuotingConvention ticks(final int scale)
	{
		return new PriceQuotingConvention(PriceQuotingUnit.TICKS, scale);
	}

	public static PriceQuotingConvention currency(final int scale)
	{
		return new PriceQuotingConvention(PriceQuotingUnit.CURRENCY, scale);
	}

	public static PriceQuotingConvention thirtySeconds(final int scale)
	{
		return new PriceQuotingConvention(PriceQuotingUnit.THIRTY_SECONDS, scale);
	}

	public boolean isCompatibleWith(final PriceQuotingConvention other)
	{
		return unit == other.unit;
	}
}