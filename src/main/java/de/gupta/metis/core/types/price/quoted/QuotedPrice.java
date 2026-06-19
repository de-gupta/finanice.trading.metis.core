package de.gupta.metis.core.types.price.quoted;

import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegerEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.metis.core.types.price.PriceType;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;

public interface QuotedPrice<U extends PriceQuotingUnit> extends Module<QuotedPrice<U>,
		IntegerEuclideanDomain>
{
	PriceType price();

	PriceQuotingConvention<U> convention();

	DivisionResult<QuotedPrice<U>> divide(final int divisor);
}