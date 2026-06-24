package de.gupta.metis.core.types.number;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
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
			case TradingNumberImpl w ->
					from(IntegralNumberFactory.of(value).add(IntegralNumberFactory.of(w.value)).value());
		};
	}

	@Override
	public TradingNumber negate()
	{
		return from(IntegralNumberFactory.of(value).negate().value());
	}

	@Override
	public TradingNumber multiply(final TradingNumber other)
	{
		return switch (other)
		{
			case TradingNumberImpl w ->
					from(IntegralNumberFactory.of(value).multiply(IntegralNumberFactory.of(w.value)).value());
		};
	}

	@Override
	public long norm()
	{
		return IntegralNumberFactory.of(value).norm();
	}

	@Override
	public DivisionResult<TradingNumber> divideFloor(final TradingNumber other)
	{
		return switch (other)
		{
//			case TradingNumberImpl w -> canonical.divideWithRemainder(value, w.value).map(TradingNumberFactory::of);
			case TradingNumberImpl w -> IntegralNumberFactory.of(value).divideFloor(IntegralNumberFactory.of(w.value))
			                                                 .map(IntegralNumber::value)
			                                                 .map(TradingNumberFactory::of);
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
	public TradingNumber scale(final IntegralNumber scalar)
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