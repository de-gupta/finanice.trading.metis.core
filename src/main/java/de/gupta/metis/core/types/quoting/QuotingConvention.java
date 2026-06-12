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
}