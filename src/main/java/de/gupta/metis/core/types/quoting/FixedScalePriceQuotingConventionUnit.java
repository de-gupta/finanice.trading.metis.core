package de.gupta.metis.core.types.quoting;

public enum FixedScalePriceQuotingConventionUnit implements PriceQuotingConventionUnit, FixedScaleQuotingConventionUnit
{
	BASIS_POINTS(4);

	private final int scale;

	FixedScalePriceQuotingConventionUnit(final int scale)
	{
		this.scale = scale;
	}

	@Override
	public int scale()
	{
		return scale;
	}
}