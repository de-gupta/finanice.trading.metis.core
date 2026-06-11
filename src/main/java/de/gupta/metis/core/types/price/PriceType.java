package de.gupta.metis.core.types.price;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.Zero;
import de.gupta.metis.core.types.number.TradingNumber;

public sealed interface PriceType extends Zero<PriceType> permits PriceTypeImpl
{
	TradingNumber value();
}