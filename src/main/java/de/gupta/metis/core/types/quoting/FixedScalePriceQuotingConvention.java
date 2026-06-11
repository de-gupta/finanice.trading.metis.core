package de.gupta.metis.core.types.quoting;

public record FixedScalePriceQuotingConvention(FixedScalePriceQuotingUnit unit) implements PriceQuotingConvention
{
	public static final FixedScalePriceQuotingConvention BASIS_POINTS =
			new FixedScalePriceQuotingConvention(FixedScalePriceQuotingUnit.BASIS_POINTS);

	@Override
	public int scale()
	{
		return unit.scale();
	}
}