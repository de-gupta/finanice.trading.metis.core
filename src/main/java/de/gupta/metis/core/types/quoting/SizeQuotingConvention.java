package de.gupta.metis.core.types.quoting;

public record SizeQuotingConvention<U extends SizeQuotingUnit>(U unit, int scale)
		implements QuotingConvention<U>
{
	public static SizeQuotingConvention<SizeQuotingUnit.Units> units(final int scale)
	{
		return new SizeQuotingConvention<>(new SizeQuotingUnit.Units(), scale);
	}

	public static SizeQuotingConvention<SizeQuotingUnit.Lots> lots(final int scale)
	{
		return new SizeQuotingConvention<>(new SizeQuotingUnit.Lots(), scale);
	}

	public static SizeQuotingConvention<SizeQuotingUnit.Contracts> contracts(final int scale)
	{
		return new SizeQuotingConvention<>(new SizeQuotingUnit.Contracts(), scale);
	}
}