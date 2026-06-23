package de.gupta.metis.core.types.price.quoted;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.metis.core.types.arithmetic.PriceArithmetic;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.price.PriceType;
import de.gupta.metis.core.types.price.PriceTypeFactory;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import de.gupta.metis.core.types.quoting.utility.QuotingConventionRequoting;
import de.gupta.metis.core.types.rounding.ScalarRoundingPolicy;

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
		var resultConvention = convention.scale() >= other.convention().scale() ? convention : other.convention();

		return of(PriceArithmetic.of(convention, other.convention()).add(price, other.price()), resultConvention);
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
	public OrderRelation compare(final QuotedPrice<U> other)
	{
		return PriceArithmetic.of(convention(), other.convention()).compare(price(), other.price());
	}

	@Override
	public PriceType price()
	{
		return price;
	}

	@Override
	public QuotedPrice<U> requote(final PriceQuotingConvention<U> targetConvention)
	{
		return of(PriceTypeFactory.of(QuotingConventionRequoting.requote(price.value(), convention, targetConvention)),
				targetConvention);
	}

	@Override
	public DivisionResult<QuotedPrice<U>> divide(final int divisor)
	{
		return price.value().divide(divisor).map(value -> with(PriceTypeFactory.of(value)));
	}

	@Override
	public QuotedPrice<U> divide(final int divisor, final ScalarRoundingPolicy policy)
	{
		return with(PriceTypeFactory.of(price.value().quotient(TradingNumberFactory.of(divisor), policy)));
	}

	@Override
	public PriceQuotingConvention<U> convention()
	{
		return convention;
	}

	@Override
	public String toString()
	{
		return "{" + price + ", " + convention + '}';
	}

	QuotedPriceImpl(final PriceType price, final PriceQuotingConvention<U> convention)
	{
		this.price = price;
		this.convention = convention;
		delegate = PriceArithmetic.of(convention);
	}
}