package de.gupta.metis.core.types.number;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.metis.core.types.rounding.ScalarRoundingPolicy;

public record ScalarRoundingPolicyImpl(RoundingStrategy<TradingNumber> strategy) implements ScalarRoundingPolicy
{
}