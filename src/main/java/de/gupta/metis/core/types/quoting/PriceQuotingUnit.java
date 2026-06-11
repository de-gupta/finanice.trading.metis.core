package de.gupta.metis.core.types.quoting;

public sealed interface PriceQuotingUnit extends QuotingUnit
		permits FixedScalePriceQuotingUnit, VariableScalePriceQuotingUnit
{
}