package de.gupta.metis.core.types.size;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.Zero;
import de.gupta.metis.core.types.number.TradingNumber;

public sealed interface SizeType extends Zero<SizeType> permits SizeTypeImpl
{
	TradingNumber value();
}