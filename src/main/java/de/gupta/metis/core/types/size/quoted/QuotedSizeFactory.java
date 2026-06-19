package de.gupta.metis.core.types.size.quoted;

import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;
import de.gupta.metis.core.types.size.SizeType;

public final class QuotedSizeFactory
{
	public static <U extends SizeQuotingUnit> QuotedSize<U> of(final SizeType size,
	                                                           final SizeQuotingConvention<U> convention)
	{
		return QuotedSizeImpl.of(size, convention);
	}

	private QuotedSizeFactory()
	{
	}
}
