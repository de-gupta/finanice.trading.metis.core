package de.gupta.metis.core.types.time;

import de.gupta.commons.utility.math.algebra.element.ordered.OrderedAdditiveGroup;

import java.time.Duration;

public sealed interface TimeDeltaType extends OrderedAdditiveGroup<TimeDeltaType>
		permits TimeDeltaTypeImpl
{
	Duration asDuration();
}
