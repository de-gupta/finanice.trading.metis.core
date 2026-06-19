package de.gupta.metis.core.types.price.quoted;

import de.gupta.metis.core.types.price.PriceType;
import de.gupta.metis.core.types.price.PriceTypeFactory;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;

public final class QuotedPriceFactory
{
	public static <U extends PriceQuotingUnit> QuotedPrice<U> zero(final PriceQuotingConvention<U> convention)
	{
		return of(0, convention);
	}

	public static <U extends PriceQuotingUnit> QuotedPrice<U> of(final int price,
	                                                             final PriceQuotingConvention<U> convention)
	{
		return of((long) price, convention);
	}

	public static <U extends PriceQuotingUnit> QuotedPrice<U> of(final long price,
	                                                             final PriceQuotingConvention<U> convention)
	{
		return of(PriceTypeFactory.of(price), convention);
	}

	public static <U extends PriceQuotingUnit> QuotedPrice<U> of(final PriceType price,
	                                                             final PriceQuotingConvention<U> convention)
	{
		return QuotedPriceImpl.of(price, convention);
	}
}