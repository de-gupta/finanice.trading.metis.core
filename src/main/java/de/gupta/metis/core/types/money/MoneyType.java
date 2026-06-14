package de.gupta.metis.core.types.money;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveAbelianGroup;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.number.TradingNumber;

public sealed interface MoneyType<C extends Currency>
		extends AdditiveAbelianGroup<MoneyType<C>>, TotallyOrdered<MoneyType<C>> permits MoneyTypeImpl
{
	TradingNumber value();

	C currency();
}