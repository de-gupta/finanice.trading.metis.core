package de.gupta.metis.core.types.quoting;

import de.gupta.metis.core.types.currency.Currency;

public record CurrencyPriceUnit<C extends Currency>(C currency)
		implements PriceQuotingUnit, CurrencyQuotingUnit<C>, VariableScaleQuotingUnit
{
	@Override
	public String toString()
	{
		return currency.toString();
	}
}