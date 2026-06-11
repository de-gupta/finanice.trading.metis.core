package de.gupta.metis.core.types.quoting;

public record VariableScalePriceQuotingConvention(VariableScalePriceQuotingConventionUnit unit, int scale)
		implements PriceQuotingConvention
{
	public static VariableScalePriceQuotingConvention ticks(final int scale)
	{
		return new VariableScalePriceQuotingConvention(VariableScalePriceQuotingConventionUnit.TICKS, scale);
	}

	public static VariableScalePriceQuotingConvention currency(final int scale)
	{
		return new VariableScalePriceQuotingConvention(VariableScalePriceQuotingConventionUnit.CURRENCY, scale);
	}

	public static VariableScalePriceQuotingConvention thirtySeconds(final int scale)
	{
		return new VariableScalePriceQuotingConvention(VariableScalePriceQuotingConventionUnit.THIRTY_SECONDS, scale);
	}
}