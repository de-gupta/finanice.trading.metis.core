package de.gupta.metis.core.types.arithmetic;

import de.gupta.commons.utility.math.algebra.structure.binary.notation.additive.AdditiveAbelianGroupStructure;
import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.commons.utility.math.ordering.structure.TotalOrderStructure;
import de.gupta.metis.core.types.price.PriceType;
import de.gupta.metis.core.types.price.PriceTypeFactory;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;

public final class PriceArithmetic
		implements AdditiveAbelianGroupStructure<PriceType>, TotalOrderStructure<PriceType>
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

	private PriceArithmetic(final PriceQuotingConvention<?> left, final PriceQuotingConvention<?> right)
	{
		this.delegate = QuotingConventionAwareArithmetic.of(left, right, PriceType::value, PriceTypeFactory::of);
	}
}