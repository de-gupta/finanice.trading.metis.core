package de.gupta.metis.core.types.quoting;

public enum FixedScalePriceQuotingUnit implements PriceQuotingUnit, FixedScaleQuotingUnit
{
	BASIS_POINTS(4);

	private final int scale;

	FixedScalePriceQuotingUnit(final int scale)
	{
		this.scale = scale;
	}

	@Override
	public int scale()
	{
		return scale;
	}
}