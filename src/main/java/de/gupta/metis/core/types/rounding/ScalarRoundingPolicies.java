package de.gupta.metis.core.types.rounding;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategies;
import de.gupta.metis.core.types.number.ScalarRoundingPolicyImpl;
import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.number.TradingNumberFactory;

public final class ScalarRoundingPolicies
{
	public static ScalarRoundingPolicy floor()
	{
		return new ScalarRoundingPolicyImpl(TradingNumber::divideWithRemainder);
	}

	public static ScalarRoundingPolicy ceiling()
	{
		return new ScalarRoundingPolicyImpl(RoundingStrategies.ceiling(
				TradingNumber::divideWithRemainder,
				TradingNumber::isZero,
				TradingNumberFactory.of(1),
				TradingNumber::add,
				TradingNumber::subtract));
	}

	public static ScalarRoundingPolicy truncate()
	{
		return new ScalarRoundingPolicyImpl(RoundingStrategies.truncate(
				TradingNumber::divideWithRemainder,
				TradingNumber::isZero,
				TradingNumber::isNegative,
				TradingNumberFactory.of(1),
				TradingNumber::add,
				TradingNumber::subtract));
	}

	private ScalarRoundingPolicies()
	{
	}
}
