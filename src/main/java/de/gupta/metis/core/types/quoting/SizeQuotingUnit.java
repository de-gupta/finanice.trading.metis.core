package de.gupta.metis.core.types.quoting;

public sealed interface SizeQuotingUnit extends QuotingUnit
		permits SizeQuotingUnit.VariableScale
{
	enum VariableScale implements SizeQuotingUnit, VariableScaleQuotingUnit
	{
		UNITS,
		LOTS,
		CONTRACTS
	}
}