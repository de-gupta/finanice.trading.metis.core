package de.gupta.metis.core.types.convention;

public record PriceQuotingConvention(PriceQuotingConventionKind kind, int scale)
		implements QuotingConvention<PriceQuotingConventionKind>
{
	public static PriceQuotingConvention currency(final int scale)
	{
		return new PriceQuotingConvention(PriceQuotingConventionKind.CURRENCY, scale);
	}

	public static PriceQuotingConvention ticks(final int scale)
	{
		return new PriceQuotingConvention(PriceQuotingConventionKind.TICKS, scale);
	}

	public static PriceQuotingConvention basisPoints()
	{
		return new PriceQuotingConvention(PriceQuotingConventionKind.BASIS_POINTS, 0);
	}

	public static PriceQuotingConvention thirtySeconds()
	{
		return new PriceQuotingConvention(PriceQuotingConventionKind.THIRTY_SECONDS, 0);
	}
}