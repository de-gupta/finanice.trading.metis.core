package de.gupta.metis.core.types.number;

import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ordered.OrderedEuclideanDomain;
import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

public sealed interface TradingNumber extends Module<TradingNumber, IntegersAsEuclideanDomain>,
		OrderedEuclideanDomain<TradingNumber> permits TradingNumberImpl
{
	DivisionResult<TradingNumber> divide(int divisor);
}
