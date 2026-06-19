package de.gupta.metis.core.types.size.quoted;

import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;
import de.gupta.metis.core.types.size.SizeType;
import de.gupta.metis.core.types.size.SizeTypeFactory;

public final class QuotedSizeFactory
{
	public static <U extends SizeQuotingUnit> QuotedSize<U> zero(final SizeQuotingConvention<U> convention)
	{
		return of(0, convention);
	}

	public static <U extends SizeQuotingUnit> QuotedSize<U> of(final int size,
	                                                           final SizeQuotingConvention<U> convention)
	{
		return of((long) size, convention);
	}

	public static <U extends SizeQuotingUnit> QuotedSize<U> of(final long size,
	                                                           final SizeQuotingConvention<U> convention)
	{
		return of(SizeTypeFactory.of(size), convention);
	}

	public static <U extends SizeQuotingUnit> QuotedSize<U> of(final SizeType size,
	                                                           final SizeQuotingConvention<U> convention)
	{
		return QuotedSizeImpl.of(size, convention);
	}

	private QuotedSizeFactory()
	{
	}
}