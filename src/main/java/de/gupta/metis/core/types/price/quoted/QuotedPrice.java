package de.gupta.metis.core.types.price.quoted;

import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.module.ScalarQuotientable;
import de.gupta.commons.utility.math.algebra.element.ordered.OrderedAdditiveGroup;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.metis.core.types.price.PriceType;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import de.gupta.metis.core.types.rounding.ScalarRoundingPolicy;

public sealed interface QuotedPrice<U extends PriceQuotingUnit> extends Module<QuotedPrice<U>,
		IntegralNumber>, OrderedAdditiveGroup<QuotedPrice<U>>, ScalarQuotientable<QuotedPrice<U>, RationalNumber>
		permits QuotedPriceImpl
{
	default boolean isZero()
	{
		return isEqualTo(zero());
	}

	PriceType price();

	QuotedPrice<U> requote(final PriceQuotingConvention<U> targetConvention);

	DivisionResult<QuotedPrice<U>> divide(final int divisor);

	QuotedPrice<U> divide(final int divisor, final ScalarRoundingPolicy policy);

	PriceQuotingConvention<U> convention();
}