package de.gupta.metis.core.types.quoting;

public sealed interface PriceQuotingConvention extends QuotingConvention
		permits PriceQuotingConvention.Variable
{
	@Override
	PriceQuotingUnit unit();

	default boolean isCompatibleWith(final PriceQuotingConvention other)
	{
		return unit().equals(other.unit());
	}

	record Variable(PriceQuotingUnit.VariableScale unit, int scale) implements PriceQuotingConvention
	{
		public static Variable ticks(final int scale)
		{
			return new Variable(PriceQuotingUnit.VariableScale.TICKS, scale);
		}

		public static Variable currency(final int scale)
		{
			return new Variable(PriceQuotingUnit.VariableScale.CURRENCY, scale);
		}

		public static Variable thirtySeconds(final int scale)
		{
			return new Variable(PriceQuotingUnit.VariableScale.THIRTY_SECONDS, scale);
		}
	}
}