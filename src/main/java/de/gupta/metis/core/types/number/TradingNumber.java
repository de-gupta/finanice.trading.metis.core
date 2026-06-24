package de.gupta.metis.core.types.number;

import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.module.ScalarQuotientable;
import de.gupta.commons.utility.math.algebra.element.ordered.OrderedEuclideanDomain;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.metis.core.types.rounding.ScalarRoundingPolicy;

public sealed interface TradingNumber
		extends Module<TradingNumber, IntegralNumber>, ScalarQuotientable<TradingNumber, RationalNumber>,
		OrderedEuclideanDomain<TradingNumber> permits TradingNumberImpl
{
	DivisionResult<TradingNumber> divide(int divisor);

	default TradingNumber quotient(final TradingNumber divisor, final ScalarRoundingPolicy policy)
	{
		return switch (policy)
		{
			case ScalarRoundingPolicyImpl p -> divide(divisor, p.strategy()).quotient();
		};
	}
}