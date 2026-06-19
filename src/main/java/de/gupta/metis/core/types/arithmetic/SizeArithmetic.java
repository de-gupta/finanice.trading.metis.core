package de.gupta.metis.core.types.arithmetic;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegerEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.module.ModuleStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.RingStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.IntegerEuclideanDomainStructure;
import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.commons.utility.math.ordering.structure.TotalOrderStructure;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.size.SizeType;
import de.gupta.metis.core.types.size.SizeTypeFactory;

public final class SizeArithmetic
		implements ModuleStructure<SizeType, IntegerEuclideanDomain>,
		TotalOrderStructure<SizeType>
{
	private final QuotingConventionAwareArithmetic<SizeType> delegate;

	public static SizeArithmetic of(final SizeQuotingConvention<?> convention)
	{
		return of(convention, convention);
	}

	public static SizeArithmetic of(final SizeQuotingConvention<?> left, final SizeQuotingConvention<?> right)
	{
		return new SizeArithmetic(left, right);
	}

	@Override
	public SizeType negate(final SizeType element)
	{
		return delegate.negate(element);
	}

	@Override
	public SizeType zero()
	{
		return delegate.zero();
	}

	@Override
	public SizeType add(final SizeType left, final SizeType right)
	{
		return delegate.add(left, right);
	}

	@Override
	public OrderRelation compare(final SizeType left, final SizeType right)
	{
		return delegate.compare(left, right);
	}

	@Override
	public RingStructure<IntegerEuclideanDomain> scalars()
	{
		return IntegerEuclideanDomainStructure.INSTANCE;
	}

	@Override
	public SizeType scale(final IntegerEuclideanDomain scalar, final SizeType sizeType)
	{
		return delegate.scale(scalar, sizeType);
	}

	private SizeArithmetic(final SizeQuotingConvention<?> left, final SizeQuotingConvention<?> right)
	{
		this.delegate = QuotingConventionAwareArithmetic.of(left, right, SizeType::value, SizeTypeFactory::of);
	}
}