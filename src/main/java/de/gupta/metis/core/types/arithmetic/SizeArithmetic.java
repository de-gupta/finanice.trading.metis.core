package de.gupta.metis.core.types.arithmetic;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.structure.module.ModuleStructure;
import de.gupta.commons.utility.math.algebra.structure.module.ScalarQuotientableStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.RingStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.IntegerEuclideanDomainStructure;
import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.commons.utility.math.ordering.structure.TotalOrderStructure;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.size.SizeType;
import de.gupta.metis.core.types.size.SizeTypeFactory;

public final class SizeArithmetic
		implements ModuleStructure<SizeType, IntegralNumber>, ScalarQuotientableStructure<SizeType, RationalNumber>,
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
	public RingStructure<IntegralNumber> scalars()
	{
		return IntegerEuclideanDomainStructure.INSTANCE;
	}

	@Override
	public SizeType scale(final IntegralNumber scalar, final SizeType sizeType)
	{
		return delegate.scale(scalar, sizeType);
	}

	@Override
	public RationalNumber ratio(final SizeType numerator, final SizeType denominator)
	{
		return delegate.ratio(numerator, denominator);
	}

	private SizeArithmetic(final SizeQuotingConvention<?> left, final SizeQuotingConvention<?> right)
	{
		this.delegate = QuotingConventionAwareArithmetic.of(left, right, SizeType::value, SizeTypeFactory::of);
	}
}