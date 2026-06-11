package de.gupta.metis.core.types.size;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.math.algebra.structure.binary.notation.additive.AdditiveAbelianGroupStructure;
import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;

public class SizeArithmetic implements AdditiveAbelianGroupStructure<SizeType>
{
	private final SizeQuotingConvention leftQuotingConvention;
	private final SizeQuotingConvention rightQuotingConvention;

	@Override
	public SizeType negate(final SizeType element)
	{
		return SizeTypeFactory.sizeOf(element.value().negate());
	}

	@Override
	public SizeType zero()
	{
		return SizeTypeFactory.zero();
	}

	@Override
	public SizeType add(final SizeType left, final SizeType right)
	{
		return Unfolding.beckon(left)
		                .discern(_ -> leftQuotingConvention.isCompatibleWith(rightQuotingConvention))
		                .metamorphose(l -> scale(l, rightQuotingConvention.scale()).add(right.value()))
		                .metamorphose(SizeTypeFactory::sizeOf)
		                .decree(() -> new IllegalArgumentException(
								"Cannot add sizes with incompatible quoting conventions: " + leftQuotingConvention + " and " + rightQuotingConvention));
	}

	private TradingNumber scale(final SizeType size, int scale)
	{
		return size.value().multiply(TradingNumberFactory.of(scale));
	}

	SizeArithmetic(final SizeQuotingConvention leftQuotingConvention,
	               final SizeQuotingConvention rightQuotingConvention)
	{
		this.leftQuotingConvention = leftQuotingConvention;
		this.rightQuotingConvention = rightQuotingConvention;
	}
}