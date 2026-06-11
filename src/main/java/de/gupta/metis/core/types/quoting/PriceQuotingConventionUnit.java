package de.gupta.metis.core.types.quoting;

public sealed interface PriceQuotingConventionUnit extends QuotingConventionUnit
		permits FixedScalePriceQuotingConventionUnit, VariableScalePriceQuotingConventionUnit
{
}