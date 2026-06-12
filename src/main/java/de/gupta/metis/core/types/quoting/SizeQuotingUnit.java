package de.gupta.metis.core.types.quoting;

public sealed interface SizeQuotingUnit extends QuotingUnit
		permits SizeQuotingUnit.Units, SizeQuotingUnit.Lots, SizeQuotingUnit.Contracts
{
	record Units() implements SizeQuotingUnit, VariableScaleQuotingUnit
	{
	}

	record Lots() implements SizeQuotingUnit, VariableScaleQuotingUnit
	{
	}

	record Contracts() implements SizeQuotingUnit, VariableScaleQuotingUnit
	{
	}
}