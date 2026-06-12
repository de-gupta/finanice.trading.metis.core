package de.gupta.metis.core.types.quoting;

public sealed interface PriceQuotingUnit extends QuotingUnit
		permits PriceQuotingUnit.Ticks, PriceQuotingUnit.ThirtySeconds, CurrencyPriceUnit
{
	record Ticks() implements PriceQuotingUnit, VariableScaleQuotingUnit
	{
	}

	record ThirtySeconds() implements PriceQuotingUnit, VariableScaleQuotingUnit
	{
	}
}