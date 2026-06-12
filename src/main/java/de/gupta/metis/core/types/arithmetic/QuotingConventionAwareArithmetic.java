package de.gupta.metis.core.types.arithmetic;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.Zero;
import de.gupta.commons.utility.math.algebra.structure.binary.notation.additive.AdditiveAbelianGroupStructure;
import de.gupta.metis.core.types.exception.IncompatibleInputException;
import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.quoting.QuotingConvention;

import java.util.function.Function;

final class QuotingConventionAwareArithmetic<E extends Zero<E>> implements AdditiveAbelianGroupStructure<E>
{
	private final QuotingConvention<?> leftQuotingConvention;
	private final QuotingConvention<?> rightQuotingConvention;
	private final Function<E, TradingNumber> extractor;
	private final Function<TradingNumber, E> factory;

	static <E extends Zero<E>> QuotingConventionAwareArithmetic<E> of(
			final QuotingConvention<?> leftQuotingConvention,
			final QuotingConvention<?> rightQuotingConvention,
			final Function<E, TradingNumber> extractor,
			final Function<TradingNumber, E> factory)
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
		                .decree(IncompatibleInputException.from(
				                "Incompatible quoting conventions: " + leftQuotingConvention + " and " + rightQuotingConvention));
	}

	@Override
	public E zero()
	{
		return factory.apply(TradingNumberFactory.zero());
	}

	private TradingNumber scale(final E element, final int scale)
	{
		return extractor.apply(element).multiply(TradingNumberFactory.of(scale));
	}

	private QuotingConventionAwareArithmetic(final QuotingConvention<?> leftQuotingConvention,
	                                         final QuotingConvention<?> rightQuotingConvention,
	                                         final Function<E, TradingNumber> extractor,
	                                         final Function<TradingNumber, E> factory)
	{
		this.leftQuotingConvention = leftQuotingConvention;
		this.rightQuotingConvention = rightQuotingConvention;
		this.extractor = extractor;
		this.factory = factory;
	}
}