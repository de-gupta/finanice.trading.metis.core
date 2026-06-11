package de.gupta.metis.core.types.size;

import de.gupta.metis.core.types.number.TradingNumber;

public sealed interface SizeType permits SizeTypeImpl
{
	TradingNumber value();
}