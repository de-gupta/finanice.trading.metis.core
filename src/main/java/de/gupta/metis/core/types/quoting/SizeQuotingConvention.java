package de.gupta.metis.core.types.quoting;

public sealed interface SizeQuotingConvention extends QuotingConvention
		permits SizeQuotingConvention.Variable
{
	@Override
	SizeQuotingUnit unit();

	default boolean isCompatibleWith(final SizeQuotingConvention other)
	{
		return unit().equals(other.unit());
	}

	record Variable(SizeQuotingUnit.VariableScale unit, int scale) implements SizeQuotingConvention
	{
		public static Variable units(final int scale)
		{
			return new Variable(SizeQuotingUnit.VariableScale.UNITS, scale);
		}

		public static Variable lots(final int scale)
		{
			return new Variable(SizeQuotingUnit.VariableScale.LOTS, scale);
		}

		public static Variable contracts(final int scale)
		{
			return new Variable(SizeQuotingUnit.VariableScale.CONTRACTS, scale);
		}
	}
}