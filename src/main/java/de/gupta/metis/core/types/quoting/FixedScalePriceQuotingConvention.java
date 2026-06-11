package de.gupta.metis.core.types.quoting;

public record FixedScalePriceQuotingConvention(FixedScalePriceQuotingConventionUnit unit) implements PriceQuotingConvention
{
	public static final FixedScalePriceQuotingConvention BASIS_POINTS =
			new FixedScalePriceQuotingConvention(FixedScalePriceQuotingConventionUnit.BASIS_POINTS);

	@Override
	public int scale()
	{
		return unit.scale();
	}
}