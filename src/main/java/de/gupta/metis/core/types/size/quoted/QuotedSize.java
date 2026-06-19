package de.gupta.metis.core.types.size.quoted;

import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;
import de.gupta.metis.core.types.size.SizeType;

public sealed interface QuotedSize<U extends SizeQuotingUnit> extends Module<QuotedSize<U>,
		IntegersAsEuclideanDomain> permits QuotedSizeImpl
{
	SizeType size();

	SizeQuotingConvention<U> convention();

	DivisionResult<QuotedSize<U>> divide(final int divisor);
}
