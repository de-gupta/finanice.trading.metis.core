package de.gupta.metis.core.types.quoting;

public interface QuotingConvention<U extends QuotingConventionUnit>
{
	U unit();

	int scale();

	default boolean isCompatibleWith(final QuotingConvention<U> other)
	{
		return unit().equals(other.unit());
	}
}