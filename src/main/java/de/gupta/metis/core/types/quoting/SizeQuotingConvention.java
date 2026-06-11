package de.gupta.metis.core.types.quoting;

public record SizeQuotingConvention(SizeQuotingUnit unit, int scale) implements QuotingConvention
{
	public static SizeQuotingConvention units(final int scale)
	{
		return new SizeQuotingConvention(SizeQuotingUnit.UNITS, scale);
	}

	public static SizeQuotingConvention lots(final int scale)
	{
		return new SizeQuotingConvention(SizeQuotingUnit.LOTS, scale);
	}

	public static SizeQuotingConvention contracts(final int scale)
	{
		return new SizeQuotingConvention(SizeQuotingUnit.CONTRACTS, scale);
	}

	public boolean isCompatibleWith(final SizeQuotingConvention other)
	{
		return unit == other.unit;
	}
}