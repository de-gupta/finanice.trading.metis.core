package de.gupta.metis.core.types.number;

import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ring.EuclideanDomain;
import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegerEuclideanDomain;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;

public sealed interface TradingNumber
		extends Module<TradingNumber, IntegerEuclideanDomain>,
		EuclideanDomain<TradingNumber>, TotallyOrdered<TradingNumber>
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