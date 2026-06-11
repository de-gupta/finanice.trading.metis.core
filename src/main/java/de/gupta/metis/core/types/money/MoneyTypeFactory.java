package de.gupta.metis.core.types.money;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveAbelianGroup;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.number.TradingNumber;

public final class MoneyTypeFactory
{
	public static <C extends Currency> MoneyType<C> of(final TradingNumber value, final C currency)
	{
		return MoneyTypeImpl.of(value, currency);
	}

	private MoneyTypeFactory()
	{
	}
}