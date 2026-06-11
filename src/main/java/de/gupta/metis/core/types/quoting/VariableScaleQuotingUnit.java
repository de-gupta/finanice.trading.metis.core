package de.gupta.metis.core.types.quoting;

public sealed interface VariableScaleQuotingUnit extends QuotingUnit
		permits PriceQuotingUnit.VariableScale, SizeQuotingUnit.VariableScale
{
}