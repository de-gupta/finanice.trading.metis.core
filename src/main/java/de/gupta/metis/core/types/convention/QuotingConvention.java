package de.gupta.metis.core.types.convention;

public interface QuotingConvention<K extends QuotingConventionKind>
{
	K kind();

	int scale();

	default boolean isCompatibleWith(final QuotingConvention<K> other)
	{
		return kind().equals(other.kind());
	}
}