package de.gupta.metis.core.types.arithmetic;

import de.gupta.commons.utility.math.algebra.structure.binary.notation.additive.AdditiveAbelianGroupStructure;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.size.SizeType;
import de.gupta.metis.core.types.size.SizeTypeFactory;

public final class SizeArithmetic implements AdditiveAbelianGroupStructure<SizeType>
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

	private SizeArithmetic(final SizeQuotingConvention<?> left, final SizeQuotingConvention<?> right)
	{
		this.delegate = QuotingConventionAwareArithmetic.of(left, right, SizeType::value, SizeTypeFactory::of);
	}
}