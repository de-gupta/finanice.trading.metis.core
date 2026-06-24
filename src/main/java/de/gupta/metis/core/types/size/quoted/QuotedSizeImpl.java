package de.gupta.metis.core.types.size.quoted;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.metis.core.types.arithmetic.SizeArithmetic;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;
import de.gupta.metis.core.types.quoting.utility.QuotingConventionRequoting;
import de.gupta.metis.core.types.rounding.ScalarRoundingPolicy;
import de.gupta.metis.core.types.size.SizeType;
import de.gupta.metis.core.types.size.SizeTypeFactory;

final class QuotedSizeImpl<U extends SizeQuotingUnit> implements QuotedSize<U>
{
	private final SizeType size;
	private final SizeQuotingConvention<U> convention;
	private final SizeArithmetic delegate;

	@Override
	public QuotedSize<U> negate()
	{
		return with(delegate.negate(size));
	}

	QuotedSize<U> with(final SizeType size)
	{
		return of(size, convention);
	}

	public static <U extends SizeQuotingUnit> QuotedSizeImpl<U> of(final SizeType size,
	                                                               final SizeQuotingConvention<U> convention)
	{
		return new QuotedSizeImpl<>(size, convention);
	}

	@Override
	public QuotedSize<U> add(final QuotedSize<U> other)
	{
		var resultConvention = convention.scale() >= other.convention().scale() ? convention : other.convention();

		return of(SizeArithmetic.of(convention, other.convention()).add(size, other.size()), resultConvention);
	}

	@Override
	public QuotedSize<U> zero()
	{
		return with(delegate.zero());
	}

	@Override
	public QuotedSize<U> scale(final IntegralNumber scalar)
	{
		return with(delegate.scale(scalar, size));
	}

	@Override
	public SizeType size()
	{
		return size;
	}

	@Override
	public SizeQuotingConvention<U> convention()
	{
		return convention;
	}

	@Override
	public QuotedSize<U> requote(final SizeQuotingConvention<U> targetConvention)
	{
		return of(SizeTypeFactory.of(QuotingConventionRequoting.requote(size.value(), convention, targetConvention)),
				targetConvention);
	}

	@Override
	public DivisionResult<QuotedSize<U>> divide(final int divisor)
	{
		return size.value().divide(divisor).map(value -> with(SizeTypeFactory.of(value)));
	}

	@Override
	public QuotedSize<U> divide(final int divisor, final ScalarRoundingPolicy policy)
	{
		return with(SizeTypeFactory.of(size.value().quotient(TradingNumberFactory.of(divisor), policy)));
	}

	QuotedSizeImpl(final SizeType size, final SizeQuotingConvention<U> convention)
	{
		this.size = size;
		this.convention = convention;
		delegate = SizeArithmetic.of(convention);
	}
}