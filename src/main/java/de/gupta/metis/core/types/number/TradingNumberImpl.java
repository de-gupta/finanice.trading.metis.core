package de.gupta.metis.core.types.number;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.algebra.structure.ring.EuclideanDomainStructure;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.IntegersEuclideanDomainStructure;
import de.gupta.commons.utility.math.ordering.OrderRelation;

final class TradingNumberImpl implements TradingNumber
{
	private static final TradingNumber ZERO = new TradingNumberImpl(0);
	private static final TradingNumber ONE = new TradingNumberImpl(1);
	private static final EuclideanDomainStructure<Long> canonical = IntegersEuclideanDomainStructure.INSTANCE;

	private final long value;

	static TradingNumber from(final long value)
	{
		if (value == 0) return ZERO;
		if (value == 1) return ONE;
		return new TradingNumberImpl(value);
	}

	@Override
	public boolean isZero()
	{
		return value == 0;
	}

	@Override
	public TradingNumber zero()
	{
		return ZERO;
	}

	@Override
	public TradingNumber one()
	{
		return ONE;
	}

	@Override
	public TradingNumber add(final TradingNumber other)
	{
		return switch (other)
		{
			case TradingNumberImpl w -> from(canonical.add(value, w.value));
		};
	}

	@Override
	public TradingNumber negate()
	{
		return from(canonical.negate(value));
	}

	@Override
	public TradingNumber multiply(final TradingNumber other)
	{
		return switch (other)
		{
			case TradingNumberImpl w -> from(canonical.multiply(value, w.value));
		};
	}

	@Override
	public long norm()
	{
		return canonical.norm(value);
	}

	@Override
	public DivisionResult<TradingNumber> divideFloor(final TradingNumber other)
	{
		return switch (other)
		{
			case TradingNumberImpl w -> canonical.divideWithRemainder(value, w.value).map(TradingNumberFactory::of);
		};
	}

	@Override
	public OrderRelation compare(final TradingNumber other)
	{
		return switch (other)
		{
			case TradingNumberImpl w -> OrderRelation.from(Long.compare(value, w.value));
		};
	}

	@Override
	public String toString()
	{
		return Long.toString(value);
	}

	@Override
	public TradingNumber scale(final IntegersAsEuclideanDomain scalar)
	{
		return from(Math.multiplyExact(value, scalar.value()));
	}

	@Override
	public DivisionResult<TradingNumber> divide(final int divisor)
	{
		return divideWithRemainder(TradingNumberFactory.of(divisor));
	}

	private TradingNumberImpl(final long value)
	{
		this.value = value;
	}
}