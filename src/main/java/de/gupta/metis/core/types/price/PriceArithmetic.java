package de.gupta.metis.core.types.price;

import de.gupta.commons.utility.math.algebra.structure.binary.notation.additive.AdditiveAbelianGroupStructure;
import de.gupta.metis.core.types.arithmetic.QuotingConventionAwareArithmetic;
import de.gupta.metis.core.types.quoting.QuotingConvention;

public final class PriceArithmetic implements AdditiveAbelianGroupStructure<PriceType>
{
	private final QuotingConventionAwareArithmetic<PriceType> arithmetic;

	public static PriceArithmetic of(final QuotingConvention leftQuotingConvention,
	                                 final QuotingConvention rightQuotingConvention)
	{
		return new PriceArithmetic(leftQuotingConvention, rightQuotingConvention);
	}

	@Override
	public PriceType negate(final PriceType element)
	{
		return arithmetic.negate(element);
	}

	@Override
	public PriceType zero()
	{
		return arithmetic.zero();
	}

	@Override
	public PriceType add(final PriceType left, final PriceType right)
	{
		return arithmetic.add(left, right);
	}

	private PriceArithmetic(final QuotingConvention leftQuotingConvention,
	                        final QuotingConvention rightQuotingConvention)
	{
		this.arithmetic =
				QuotingConventionAwareArithmetic.of(leftQuotingConvention, rightQuotingConvention, PriceType::value,
						PriceTypeFactory::priceOf);
	}
}