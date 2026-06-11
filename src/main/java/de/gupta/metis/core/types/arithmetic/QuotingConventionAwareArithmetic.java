package de.gupta.metis.core.types.arithmetic;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.Zero;
import de.gupta.commons.utility.math.algebra.structure.binary.notation.additive.AdditiveAbelianGroupStructure;
import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.quoting.QuotingConvention;

import java.util.function.Function;

final class QuotingConventionAwareArithmetic<E extends Zero<E>> implements AdditiveAbelianGroupStructure<E>
{
	private final QuotingConvention leftQuotingConvention;
	private final QuotingConvention rightQuotingConvention;
	private final Function<E, TradingNumber> extractor;
	private final Function<TradingNumber, E> factory;

	public static <E extends Zero<E>> QuotingConventionAwareArithmetic<E> of(QuotingConvention leftQuotingConvention,
	                                                                         QuotingConvention rightQuotingConvention,
	                                                                         Function<E, TradingNumber> extractor,
	                                                                         Function<TradingNumber, E> factory)
	{
		return new QuotingConventionAwareArithmetic<>(leftQuotingConvention, rightQuotingConvention, extractor,
				factory);
	}

	@Override
	public E negate(final E element)
	{
		return factory.apply(extractor.apply(element).negate());
	}

	@Override
	public E add(final E left, final E right)
	{
		return Unfolding.beckon(left)
		                .discern(_ -> leftQuotingConvention.isCompatibleWith(rightQuotingConvention))
		                .metamorphose(l -> scale(l, rightQuotingConvention.scale()).add(extractor.apply(right)))
		                .metamorphose(factory)
		                .decree(() -> new IllegalArgumentException(
								"Cannot add sizes with incompatible quoting conventions: " + leftQuotingConvention + " and " + rightQuotingConvention));
	}

	@Override
	public E zero()
	{
		return factory.apply(TradingNumberFactory.zero());
	}

	private TradingNumber scale(final E size, int scale)
	{
		return extractor.apply(size).multiply(TradingNumberFactory.of(scale));
	}

	private QuotingConventionAwareArithmetic(final QuotingConvention leftQuotingConvention,
	                                         final QuotingConvention rightQuotingConvention,
	                                         final Function<E, TradingNumber> extractor,
	                                         final Function<TradingNumber, E> factory)
	{
		this.leftQuotingConvention = leftQuotingConvention;
		this.rightQuotingConvention = rightQuotingConvention;
		this.extractor = extractor;
		this.factory = factory;
	}
}