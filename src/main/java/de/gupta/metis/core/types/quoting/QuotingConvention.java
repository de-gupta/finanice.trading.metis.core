package de.gupta.metis.core.types.quoting;

public sealed interface QuotingConvention permits PriceQuotingConvention, SizeQuotingConvention
{
	QuotingUnit unit();

	int scale();

	default boolean isCompatibleWith(final QuotingConvention other)
	{
		return this.unit() == other.unit();
	}
}