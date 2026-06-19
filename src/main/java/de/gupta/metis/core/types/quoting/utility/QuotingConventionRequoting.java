package de.gupta.metis.core.types.quoting.utility;

import de.gupta.metis.core.types.exception.IncompatibleInputException;
import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;
import de.gupta.metis.core.types.quoting.QuotingConvention;
import de.gupta.metis.core.types.quoting.QuotingUnit;

public final class QuotingConventionRequoting
{
	public static <U extends QuotingUnit> TradingNumber requote(final TradingNumber value,
	                                                            final QuotingConvention<U> sourceConvention,
	                                                            final QuotingConvention<U> targetConvention)
	{
		if (!sourceConvention.isCompatibleWith(targetConvention))
		{
			throw IncompatibleInputException.of(
					"Incompatible quoting conventions: " + sourceConvention + " and " + targetConvention);
		}

		var scaleDifference = targetConvention.scaleDifference(sourceConvention);
		if (scaleDifference > 0)
		{
			return value.multiply(scaleFactor(scaleDifference));
		}
		if (scaleDifference < 0)
		{
			return value.quotient(scaleFactor(-scaleDifference));
		}
		return value;
	}

	private static TradingNumber scaleFactor(final int exponent)
	{
		return TradingNumberFactory.of(Math.powExact(10L, exponent));
	}

	private QuotingConventionRequoting()
	{
	}
}