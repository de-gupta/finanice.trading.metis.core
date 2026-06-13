package de.gupta.metis.core.types.quoting;

public sealed interface QuotingConvention<U extends QuotingUnit>
		permits PriceQuotingConvention, SizeQuotingConvention
{
	U unit();

	int scale();

	default boolean isCompatibleWith(final QuotingConvention<?> other)
	{
		return unit().equals(other.unit());
	}

	default int scaleDifference(final QuotingConvention<?> other)
	{
		return scale() - other.scale();
	}

	default boolean hasSameScale(final QuotingConvention<?> other)
	{
		return scaleDifference(other) == 0;
	}
}