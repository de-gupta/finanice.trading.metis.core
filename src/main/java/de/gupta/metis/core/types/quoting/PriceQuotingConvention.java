package de.gupta.metis.core.types.quoting;

public sealed interface PriceQuotingConvention extends QuotingConvention<PriceQuotingConventionUnit>
		permits FixedScalePriceQuotingConvention, VariableScalePriceQuotingConvention
{
}