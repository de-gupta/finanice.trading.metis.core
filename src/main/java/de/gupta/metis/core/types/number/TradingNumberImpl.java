package de.gupta.metis.core.types.number;

import de.gupta.commons.utility.comparison.ComparisonResult;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;

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
	public TradingNumber norm()
	{
		return from(Math.abs(value));
	}

	@Override
	public ComparisonResult compare(final TradingNumber other)
	{
		return switch (other)
		{
			case TradingNumberImpl w -> switch (Long.compare(value, w.value))
			{
				case 0 -> ComparisonResult.EQUAL;
				case -1 -> ComparisonResult.LESS_THAN;
				default -> ComparisonResult.GREATER_THAN;
			};
		};
	}

	private TradingNumberImpl(final long value)
	{
		this.value = value;
	}
}