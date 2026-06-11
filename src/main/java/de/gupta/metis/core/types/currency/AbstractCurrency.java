package de.gupta.metis.core.types.currency;

public abstract class AbstractCurrency implements Currency
{
	@Override
	public final String toString()
	{
		return code();
	}
}