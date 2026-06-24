package de.gupta.metis.core.types.size.quoted;

import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.module.ScalarQuotientable;
import de.gupta.commons.utility.math.algebra.element.ordered.OrderedAdditiveGroup;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.metis.core.types.arithmetic.SizeArithmetic;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;
import de.gupta.metis.core.types.rounding.ScalarRoundingPolicy;
import de.gupta.metis.core.types.size.SizeType;

public sealed interface QuotedSize<U extends SizeQuotingUnit> extends Module<QuotedSize<U>,
		IntegralNumber>, OrderedAdditiveGroup<QuotedSize<U>>, ScalarQuotientable<QuotedSize<U>, RationalNumber>
		permits QuotedSizeImpl
{
	SizeType size();

	SizeQuotingConvention<U> convention();

	default boolean isZero()
	{
		return size().value().isZero();
	}

	default boolean isEqualTo(final QuotedSize<U> other)
	{
		return compare(other) == OrderRelation.EQUAL;
	}

	@Override
	default OrderRelation compare(final QuotedSize<U> other)
	{
		return SizeArithmetic.of(convention(), other.convention()).compare(size(), other.size());
	}

	QuotedSize<U> requote(final SizeQuotingConvention<U> targetConvention);

	DivisionResult<QuotedSize<U>> divide(final int divisor);

	QuotedSize<U> divide(final int divisor, final ScalarRoundingPolicy policy);
}