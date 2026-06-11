package de.gupta.metis.core.types.arithmetic;

import de.gupta.commons.utility.math.algebra.structure.binary.notation.additive.AdditiveAbelianGroupStructure;
import de.gupta.metis.core.types.quoting.QuotingConvention;
import de.gupta.metis.core.types.size.SizeType;
import de.gupta.metis.core.types.size.SizeTypeFactory;

public final class SizeArithmetic implements AdditiveAbelianGroupStructure<SizeType>
{
	private final QuotingConventionAwareArithmetic<SizeType> delegate;

	public static SizeArithmetic of(final QuotingConvention quotingConvention)
	{
		return of(quotingConvention, quotingConvention);
	}

	public static SizeArithmetic of(final QuotingConvention leftQuotingConvention,
	                                final QuotingConvention rightQuotingConvention)
	{
		return new SizeArithmetic(leftQuotingConvention, rightQuotingConvention);
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

	private SizeArithmetic(final QuotingConvention leftQuotingConvention,
	                       final QuotingConvention rightQuotingConvention)
	{
		this.delegate =
				QuotingConventionAwareArithmetic.of(leftQuotingConvention, rightQuotingConvention, SizeType::value,
						SizeTypeFactory::sizeOf);
	}
}