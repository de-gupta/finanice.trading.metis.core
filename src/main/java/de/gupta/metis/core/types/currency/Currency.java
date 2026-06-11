package de.gupta.metis.core.types.currency;

@FunctionalInterface
public interface Currency
{
	int canonicalScale();

	Currency USD = () -> 2;
	Currency EUR = () -> 2;
	Currency JPY = () -> 0;
	Currency GBP = () -> 2;
	Currency BTC = () -> 8;
	Currency ETH = () -> 9;
}