package de.gupta.metis.core.types.arithmetic;

import de.gupta.aletheia.collection.Dyad;
import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.Zero;
import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.module.ModuleStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.RingStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.IntegerEuclideanDomainStructure;
import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.commons.utility.math.ordering.structure.TotalOrderStructure;
import de.gupta.metis.core.types.exception.IncompatibleInputException;
import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.quoting.QuotingConvention;

import java.util.function.BiFunction;
import java.util.function.Function;

final class QuotingConventionAwareArithmetic<E extends Zero<E>>
		implements ModuleStructure<E, IntegersAsEuclideanDomain>,
		TotalOrderStructure<E>
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
		if (!leftQuotingConvention.isCompatibleWith(rightQuotingConvention)) throw IncompatibleInputException.of(
				"Incompatible quoting conventions: " + leftQuotingConvention + " and " + rightQuotingConvention);

		return new QuotingConventionAwareArithmetic<>(leftQuotingConvention.scaleDifference(rightQuotingConvention),
				extractor, factory);
	}

	@Override
	public E negate(final E element)
	{
		return factory.apply(extractor.apply(element).negate());
	}

	@Override
	public E add(final E left, final E right)
	{
		return factory.apply(scaleAndApply(extractor.apply(left), extractor.apply(right), TradingNumber::add));
	}

	@Override
	public OrderRelation compare(final E left, final E right)
	{
		return scaleAndApply(extractor.apply(left), extractor.apply(right), TradingNumber::compare);
	}

	@Override
	public E zero()
	{
		return factory.apply(TradingNumberFactory.zero());
	}

	@Override
	public RingStructure<IntegersAsEuclideanDomain> scalars()
	{
		return IntegerEuclideanDomainStructure.INSTANCE;
	}

	@Override
	public E scale(final IntegersAsEuclideanDomain scalar, final E e)
	{
		return factory.apply(extractor.apply(e).scale(scalar));
	}

	private <R> R scaleAndApply(
			final TradingNumber left, final TradingNumber right,
			final BiFunction<TradingNumber, TradingNumber, R> operation)
	{
		if (scaleDifference > 0) return operation.apply(left, right.multiply(scaleFactor(scaleDifference)));
		if (scaleDifference < 0) return operation.apply(left.multiply(scaleFactor(-scaleDifference)), right);
		return operation.apply(left, right);
	}

	@SuppressWarnings("unused")
	// Alternative elegant but slow implementation of scaleAndApply
	private <R> R operateRespectingScale(
			final TradingNumber left, final TradingNumber right,
			final BiFunction<TradingNumber, TradingNumber, R> operation)
	{
		return Unfolding.beckon(scaleDifference)
		                .trifurcate(Integer::intValue,
								d -> Dyad.of(left.multiply(scaleFactor(-d)), right),
								_ -> Dyad.of(left, right),
								d -> Dyad.of(left, right.multiply(scaleFactor(d))))
		                .coronate(dyad -> operation.apply(dyad.sinister(), dyad.dexter()));
	}

	private static TradingNumber scaleFactor(final int n)
	{
		return TradingNumberFactory.of(Math.powExact(10L, n));
	}

	private QuotingConventionAwareArithmetic(final int scaleDifference,
	                                         final Function<E, TradingNumber> extractor,
	                                         final Function<TradingNumber, E> factory)
	{
		this.scaleDifference = scaleDifference;
		this.extractor = extractor;
		this.factory = factory;
	}
}