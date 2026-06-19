package de.gupta.metis.core.types.arithmetic;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.module.ModuleStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.RingStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.IntegerEuclideanDomainStructure;
import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.commons.utility.math.ordering.structure.TotalOrderStructure;
import de.gupta.metis.core.types.price.PriceType;
import de.gupta.metis.core.types.price.PriceTypeFactory;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;

public final class PriceArithmetic implements ModuleStructure<PriceType, IntegersAsEuclideanDomain>,
		TotalOrderStructure<PriceType>
{
	private final QuotingConventionAwareArithmetic<PriceType> delegate;

	public static PriceArithmetic of(final PriceQuotingConvention<?> convention)
	{
		return of(convention, convention);
	}

	public static PriceArithmetic of(final PriceQuotingConvention<?> left, final PriceQuotingConvention<?> right)
	{
		return new PriceArithmetic(left, right);
	}

	@Override
	public PriceType negate(final PriceType element)
	{
		return delegate.negate(element);
	}

	@Override
	public PriceType zero()
	{
		return delegate.zero();
	}

	@Override
	public PriceType add(final PriceType left, final PriceType right)
	{
		return delegate.add(left, right);
	}

	@Override
	public OrderRelation compare(final PriceType left, final PriceType right)
	{
		return delegate.compare(left, right);
	}

	@Override
	public RingStructure<IntegersAsEuclideanDomain> scalars()
	{
		return IntegerEuclideanDomainStructure.INSTANCE;
	}

	@Override
	public PriceType scale(final IntegersAsEuclideanDomain scalar, final PriceType priceType)
	{
		return delegate.scale(scalar, priceType);
	}

	private PriceArithmetic(final PriceQuotingConvention<?> left, final PriceQuotingConvention<?> right)
	{
		this.delegate = QuotingConventionAwareArithmetic.of(left, right, PriceType::value, PriceTypeFactory::of);
	}
}