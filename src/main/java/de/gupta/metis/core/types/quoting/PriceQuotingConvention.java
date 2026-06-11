package de.gupta.metis.core.types.quoting;

public sealed interface PriceQuotingConvention extends QuotingConvention<PriceQuotingUnit>
		permits FixedScalePriceQuotingConvention, VariableScalePriceQuotingConvention
{
}