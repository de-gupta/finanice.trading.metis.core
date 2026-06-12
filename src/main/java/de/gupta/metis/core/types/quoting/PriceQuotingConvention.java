package de.gupta.metis.core.types.quoting;

import de.gupta.metis.core.types.currency.Currency;

public record PriceQuotingConvention<U extends PriceQuotingUnit>(U unit, int scale)
		implements QuotingConvention<U>
{
	public static PriceQuotingConvention<PriceQuotingUnit.Ticks> ticks(final int scale)
	{
		return new PriceQuotingConvention<>(new PriceQuotingUnit.Ticks(), scale);
	}

	public static PriceQuotingConvention<PriceQuotingUnit.ThirtySeconds> thirtySeconds(final int scale)
	{
		return new PriceQuotingConvention<>(new PriceQuotingUnit.ThirtySeconds(), scale);
	}

	public static <C extends Currency> PriceQuotingConvention<CurrencyPriceUnit<C>> currency(final C currency)
	{
		return new PriceQuotingConvention<>(new CurrencyPriceUnit<>(currency), currency.canonicalScale());
	}
}