package de.gupta.metis.core.types.quoting;

public interface FixedScaleQuotingUnit
{
	int scale();

	enum FixedScaleQuotingUnits implements FixedScaleQuotingUnit
	{
		BASIS_POINT(10^-4);

		private final int scale;

		@Override
		public int scale()
		{
			return scale;
		}

		FixedScaleQuotingUnits(final int scale)
		{
			this.scale = scale;
		}
	}
}