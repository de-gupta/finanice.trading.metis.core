package de.gupta.metis.core.types.price.quoted;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.metis.core.types.arithmetic.PriceArithmetic;
import de.gupta.metis.core.types.price.PriceType;
import de.gupta.metis.core.types.price.PriceTypeFactory;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;

final class QuotedPriceImpl<U extends PriceQuotingUnit> implements QuotedPrice<U>
{
	private final PriceType price;
	private final PriceQuotingConvention<U> convention;
	private final PriceArithmetic delegate;

	@Override
	public QuotedPrice<U> negate()
	{
		return with(delegate.negate(price));
	}

	QuotedPrice<U> with(final PriceType price)
	{
		return of(price, convention);
	}

	public static <U extends PriceQuotingUnit> QuotedPriceImpl<U> of(final PriceType price,
	                                                                 final PriceQuotingConvention<U> convention)
	{
		return new QuotedPriceImpl<>(price, convention);
	}

	@Override
	public QuotedPrice<U> add(final QuotedPrice<U> other)
	{
		return with(delegate.add(price, other.price()));
	}

	@Override
	public QuotedPrice<U> zero()
	{
		return with(delegate.zero());
	}

	@Override
	public QuotedPrice<U> scale(final IntegersAsEuclideanDomain scalar)
	{
		return with(delegate.scale(scalar, price));
	}

	@Override
	public PriceType price()
	{
		return price;
	}

	@Override
	public PriceQuotingConvention<U> convention()
	{
		return convention;
	}

	@Override
	public DivisionResult<QuotedPrice<U>> divide(final int divisor)
	{
		return price.value().divide(divisor).map(value -> with(PriceTypeFactory.of(value)));
	}

	QuotedPriceImpl(final PriceType price, final PriceQuotingConvention<U> convention)
	{
		this.price = price;
		this.convention = convention;
		delegate = PriceArithmetic.of(convention);
	}
}