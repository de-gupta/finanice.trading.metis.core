package de.gupta.metis.core.types.number;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegerEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.ordering.OrderRelation;

final class TradingNumberImpl implements TradingNumber
{
	private static final TradingNumber ZERO = new TradingNumberImpl(0);
	private static final TradingNumber ONE = new TradingNumberImpl(1);

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
			case TradingNumberImpl w -> from(Math.addExact(value, w.value));
		};
	}

	@Override
	public TradingNumber negate()
	{
		return from(Math.negateExact(value));
	}

	@Override
	public TradingNumber multiply(final TradingNumber other)
	{
		return switch (other)
		{
			case TradingNumberImpl w -> from(Math.multiplyExact(value, w.value));
		};
	}

	@Override
	public DivisionResult<TradingNumber> divideWithRemainder(final TradingNumber other)
	{
		return switch (other)
		{
			case TradingNumberImpl w -> DivisionResult.of(from(value / w.value), from(value % w.value));
		};
	}

	@Override
	public long norm()
	{
		return Math.absExact(value);
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
	public TradingNumber scale(final IntegerEuclideanDomain scalar)
	{
		return from(Math.multiplyExact(value, scalar.value()));
	}

	private TradingNumberImpl(final long value)
	{
		this.value = value;
	}
}