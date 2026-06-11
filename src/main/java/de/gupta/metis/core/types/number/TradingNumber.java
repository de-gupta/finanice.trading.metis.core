package de.gupta.metis.core.types.number;

import de.gupta.commons.utility.comparison.DescriptivelyComparable;
import de.gupta.commons.utility.math.algebra.element.ring.EuclideanDomain;

public sealed interface TradingNumber
		extends EuclideanDomain<TradingNumber>, DescriptivelyComparable<TradingNumber>
		permits TradingNumberImpl
{
	boolean isZero();
}