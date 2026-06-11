package de.gupta.metis.core.types.quoting;

public sealed interface FixedScaleQuotingUnit extends QuotingUnit
		permits FixedScaleQuotingUnit.FixedScaleQuotingUnits
{
	int scale();

	enum FixedScaleQuotingUnits implements FixedScaleQuotingUnit
	{
		BASIS_POINTS(4);

		private final int scale;

		@Override
		public int scale()
		{
			return scale;
		}

		FixedScaleQuotingUnits(int scale)
		{
			this.scale = scale;
		}
	}
}