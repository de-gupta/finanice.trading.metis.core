package de.gupta.metis.core.types.rounding;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategies;
import de.gupta.metis.core.types.number.ScalarRoundingPolicyImpl;

public final class ScalarRoundingPolicies
{
	private static final ScalarRoundingPolicy FLOOR = new ScalarRoundingPolicyImpl(RoundingStrategies.floor());
	private static final ScalarRoundingPolicy CEILING = new ScalarRoundingPolicyImpl(RoundingStrategies.ceiling());
	private static final ScalarRoundingPolicy TRUNCATE = new ScalarRoundingPolicyImpl(RoundingStrategies.truncate());

	public static ScalarRoundingPolicy floor()
	{
		return FLOOR;
	}

	public static ScalarRoundingPolicy ceiling()
	{
		return CEILING;
	}

	public static ScalarRoundingPolicy truncate()
	{
		return TRUNCATE;
	}

	private ScalarRoundingPolicies()
	{
	}
}