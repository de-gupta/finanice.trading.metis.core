package de.gupta.metis.core.types;

import de.gupta.metis.core.types.price.PriceFactory;
import de.gupta.metis.core.types.price.PriceType;
import de.gupta.metis.core.types.size.SizeFactory;
import de.gupta.metis.core.types.size.SizeType;

public final class TypeFactory
{
	public static PriceType priceOf(final long value, final Currency currency)
	{
		return PriceFactory.priceOf(value, currency);
	}

	public static SizeType sizeOf(final long value)
	{
		return SizeFactory.sizeOf(value);
	}

	private TypeFactory() {}
}