package de.gupta.metis.core.types.quoting;

public sealed interface PriceQuotingUnit extends QuotingUnit
		permits PriceQuotingUnit.VariableScale
{
	enum VariableScale implements PriceQuotingUnit, VariableScaleQuotingUnit
	{
		TICKS,
		CURRENCY,
		THIRTY_SECONDS
	}
}