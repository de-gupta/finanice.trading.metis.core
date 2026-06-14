package de.gupta.metis.core.types.number;

import de.gupta.commons.utility.math.algebra.element.ring.EuclideanDomain;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;

public sealed interface TradingNumber
		extends EuclideanDomain<TradingNumber>, TotallyOrdered<TradingNumber>
		permits TradingNumberImpl
{
	boolean isZero();

	default boolean isPositive()
	{
		return this.compare(zero()).isGreaterThan();
	}

	default boolean isNegative()
	{
		return this.compare(zero()).isLessThan();
	}
}