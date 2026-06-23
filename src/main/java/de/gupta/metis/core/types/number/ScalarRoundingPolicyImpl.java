package de.gupta.metis.core.types.number;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.metis.core.types.rounding.ScalarRoundingPolicy;

public record ScalarRoundingPolicyImpl(RoundingStrategy<TradingNumber> strategy) implements ScalarRoundingPolicy
{
	public static TradingNumber apply(final ScalarRoundingPolicy policy,
	                                  final TradingNumber dividend,
	                                  final TradingNumber divisor)
	{
		return switch (policy)
		{
			case ScalarRoundingPolicyImpl p -> p.strategy.divide(dividend, divisor).quotient();
		};
	}
}
