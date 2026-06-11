package de.gupta.metis.core.types.quoting;

public record SizeQuotingConvention(SizeQuotingConventionUnit unit, int scale)
		implements QuotingConvention<SizeQuotingConventionUnit>
{
	public static SizeQuotingConvention units(final int scale)
	{
		return new SizeQuotingConvention(SizeQuotingConventionUnit.UNITS, scale);
	}

	public static SizeQuotingConvention lots(final int scale)
	{
		return new SizeQuotingConvention(SizeQuotingConventionUnit.LOTS, scale);
	}

	public static SizeQuotingConvention contracts(final int scale)
	{
		return new SizeQuotingConvention(SizeQuotingConventionUnit.CONTRACTS, scale);
	}
}