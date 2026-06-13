package de.gupta.metis.core.types.arithmetic;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.comparison.ComparisonResult;
import de.gupta.commons.utility.comparison.DescriptivelyComparableStructure;
import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.Zero;
import de.gupta.commons.utility.math.algebra.structure.binary.notation.additive.AdditiveAbelianGroupStructure;
import de.gupta.metis.core.types.exception.IncompatibleInputException;
import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.quoting.QuotingConvention;

import java.util.function.BiFunction;
import java.util.function.Function;

final class QuotingConventionAwareArithmetic<E extends Zero<E>> implements AdditiveAbelianGroupStructure<E>,
		DescriptivelyComparableStructure<E>
{
	private final int scaleDifference;
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
		return factory.apply(
				operateRespectingScale(extractor.apply(left), extractor.apply(right), TradingNumber::add)
		);
	}

	@Override
	public ComparisonResult compare(final E left, final E right)
	{
		return operateRespectingScale(extractor.apply(left), extractor.apply(right),
				TradingNumber::compare);
	}

	@Override
	public E zero()
	{
		return factory.apply(TradingNumberFactory.zero());
	}

	private <R> R operateRespectingScale(
			final TradingNumber left, final TradingNumber right,
			final BiFunction<TradingNumber, TradingNumber, R> operation)
	{
		return Unfolding.beckon(scaleDifference)
		                .trifurcate(
								Integer::signum,
								() -> operation.apply(left.multiply(scaleFactor(-scaleDifference)), right),
								() -> operation.apply(left, right),
								() -> operation.apply(left, right.multiply(scaleFactor(scaleDifference)))
						);
	}

	private static TradingNumber scaleFactor(final int n)
	{
		return TradingNumberFactory.of(Math.powExact(10L, n));
	}

	private QuotingConventionAwareArithmetic(final QuotingConvention<?> leftQuotingConvention,
	                                         final QuotingConvention<?> rightQuotingConvention,
	                                         final Function<E, TradingNumber> extractor,
	                                         final Function<TradingNumber, E> factory)
	{
		if (!leftQuotingConvention.isCompatibleWith(rightQuotingConvention))
			throw IncompatibleInputException.of(
					"Incompatible quoting conventions: " + leftQuotingConvention + " and " + rightQuotingConvention);

		this.scaleDifference = leftQuotingConvention.scaleDifference(rightQuotingConvention);
		this.extractor = extractor;
		this.factory = factory;
	}
}