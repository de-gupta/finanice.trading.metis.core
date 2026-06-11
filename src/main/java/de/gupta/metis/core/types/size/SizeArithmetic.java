package de.gupta.metis.core.types.size;

import de.gupta.commons.utility.math.algebra.structure.binary.notation.additive.AdditiveAbelianGroupStructure;
import de.gupta.metis.core.types.arithmetic.QuotingConventionAwareArithmetic;
import de.gupta.metis.core.types.quoting.QuotingConvention;

public final class SizeArithmetic implements AdditiveAbelianGroupStructure<SizeType>
{
	private final QuotingConventionAwareArithmetic<SizeType> arithmetic;

	public static SizeArithmetic of(final QuotingConvention leftQuotingConvention,
	                                final QuotingConvention rightQuotingConvention)
	{
		return new SizeArithmetic(leftQuotingConvention, rightQuotingConvention);
	}

	@Override
	public SizeType negate(final SizeType element)
	{
		return arithmetic.negate(element);
	}

	@Override
	public SizeType zero()
	{
		return arithmetic.zero();
	}

	@Override
	public SizeType add(final SizeType left, final SizeType right)
	{
		return arithmetic.add(left, right);
	}

	private SizeArithmetic(final QuotingConvention leftQuotingConvention,
	                       final QuotingConvention rightQuotingConvention)
	{
		this.arithmetic =
				QuotingConventionAwareArithmetic.of(leftQuotingConvention, rightQuotingConvention, SizeType::value,
						SizeTypeFactory::sizeOf);
	}
}