package de.gupta.metis.core.types.quoting;

import de.gupta.metis.core.types.currency.Currency;

public interface CurrencyQuotingUnit<C extends Currency>
{
	C currency();
}
