package de.gupta.metis.core.types.quoting;

public record VariableScalePriceQuotingConvention(VariableScalePriceQuotingUnit unit, int scale)
		implements PriceQuotingConvention
{
	public static VariableScalePriceQuotingConvention ticks(final int scale)
	{
		return new VariableScalePriceQuotingConvention(VariableScalePriceQuotingUnit.TICKS, scale);
	}

	public static VariableScalePriceQuotingConvention currency(final int scale)
	{
		return new VariableScalePriceQuotingConvention(VariableScalePriceQuotingUnit.CURRENCY, scale);
	}

	public static VariableScalePriceQuotingConvention thirtySeconds(final int scale)
	{
		return new VariableScalePriceQuotingConvention(VariableScalePriceQuotingUnit.THIRTY_SECONDS, scale);
	}
}