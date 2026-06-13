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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

final class QuotingConventionAwareArithmetic<E extends Zero<E>> implements AdditiveAbelianGroupStructure<E>,
		DescriptivelyComparableStructure<E>
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
		return factory.apply(
				conventionGuarded(scaleReconciled(extractor.apply(left), extractor.apply(right), TradingNumber::add))
		);
	}

	@Override
	public ComparisonResult compare(final E left, final E right)
	{
		return conventionGuarded(
				scaleReconciled(extractor.apply(left), extractor.apply(right), TradingNumber::compare));
	}

	@Override
	public E zero()
	{
		return factory.apply(TradingNumberFactory.zero());
	}

	private static <R> Map<Predicate<? super Integer>, Function<? super Integer, R>> scaleReconciled(
			final TradingNumber left, final TradingNumber right,
			final BiFunction<TradingNumber, TradingNumber, R> operation)
	{
		final var cases = new LinkedHashMap<Predicate<? super Integer>, Function<? super Integer, R>>();
		cases.put(d -> d > 0, d -> operation.apply(left, right.multiply(scaleFactor(d))));
		cases.put(d -> d < 0, d -> operation.apply(left.multiply(scaleFactor(-d)), right));
		cases.put(d -> d == 0, _ -> operation.apply(left, right));
		return cases;
	}

	private static TradingNumber scaleFactor(final int n)
	{
		return TradingNumberFactory.of(Math.powExact(10L, n));
	}

	private <R> R conventionGuarded(final Map<Predicate<? super Integer>, Function<? super Integer, R>> cases)
	{
		return Unfolding.beckon(leftQuotingConvention.scaleDifference(rightQuotingConvention))
		                .discern(_ -> leftQuotingConvention.isCompatibleWith(rightQuotingConvention))
		                .smite(cases, IncompatibleInputException.from(
								"Incompatible quoting conventions: " + leftQuotingConvention + " and " + rightQuotingConvention));
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