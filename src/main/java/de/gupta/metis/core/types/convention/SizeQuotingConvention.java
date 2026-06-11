package de.gupta.metis.core.types.convention;

public record SizeQuotingConvention(SizeQuotingConventionKind kind, int scale)
		implements QuotingConvention<SizeQuotingConventionKind>
{
	public static SizeQuotingConvention units(final int scale)
	{
		return new SizeQuotingConvention(SizeQuotingConventionKind.UNITS, scale);
	}

	public static SizeQuotingConvention lots(final int scale)
	{
		return new SizeQuotingConvention(SizeQuotingConventionKind.LOTS, scale);
	}

	public static SizeQuotingConvention contracts(final int scale)
	{
		return new SizeQuotingConvention(SizeQuotingConventionKind.CONTRACTS, scale);
	}
}