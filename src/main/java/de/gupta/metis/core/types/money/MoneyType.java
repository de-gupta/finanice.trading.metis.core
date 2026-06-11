package de.gupta.metis.core.types.money;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveAbelianGroup;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.number.TradingNumber;

public interface MoneyType<C extends Currency> extends AdditiveAbelianGroup<MoneyType<C>>
{
	TradingNumber value();

	C currency();
}