package de.gupta.metis.core.types.quoting;

import de.gupta.metis.core.types.currency.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("QuotingConvention")
final class QuotingConventionTest
{
	@Nested
	@DisplayName("FixedScaleQuotingUnit")
	final class FixedScale
	{
		@Test
		@DisplayName("BASIS_POINT has scale 4")
		void basisPointHasScaleFour()
		{
			assertThat(FixedScaleQuotingUnit.FixedScaleQuotingUnits.BASIS_POINT.scale())
					.as("basis point scale")
					.isEqualTo(4);
		}
	}

	@Nested
	@DisplayName("PriceQuotingConvention")
	final class PriceConvention
	{
		@Test
		@DisplayName("currency convention scale equals the currency canonical scale")
		void currencyConventionScaleEqualsCurrencyCanonicalScale()
		{
			var convention = PriceQuotingConvention.currency(Currency.BTC.INSTANCE);

			assertThat(convention.scale()).as("BTC convention scale").isEqualTo(8);
		}

		@Test
		@DisplayName("currency convention unit holds the currency")
		void currencyConventionUnitHoldsTheCurrency()
		{
			var convention = PriceQuotingConvention.currency(Currency.ETH.INSTANCE);

			assertThat(convention.unit().currency()).as("ETH currency in unit").isSameAs(Currency.ETH.INSTANCE);
		}

		@Test
		@DisplayName("ticks convention has the given scale")
		void ticksConventionHasTheGivenScale()
		{
			var convention = PriceQuotingConvention.ticks(3);

			assertThat(convention.scale()).as("ticks scale").isEqualTo(3);
			assertThat(convention.unit()).as("unit type").isInstanceOf(PriceQuotingUnit.Ticks.class);
		}

		@Test
		@DisplayName("thirtySeconds convention has the given scale")
		void thirtySecondsConventionHasTheGivenScale()
		{
			var convention = PriceQuotingConvention.thirtySeconds(1);

			assertThat(convention.scale()).as("thirtySeconds scale").isEqualTo(1);
			assertThat(convention.unit()).as("unit type").isInstanceOf(PriceQuotingUnit.ThirtySeconds.class);
		}
	}

	@Nested
	@DisplayName("SizeQuotingConvention")
	final class SizeConvention
	{
		@Test
		@DisplayName("contracts convention has the given scale")
		void contractsConventionHasTheGivenScale()
		{
			var convention = SizeQuotingConvention.contracts(2);

			assertThat(convention.scale()).as("contracts scale").isEqualTo(2);
			assertThat(convention.unit()).as("unit type").isInstanceOf(SizeQuotingUnit.Contracts.class);
		}

		@Test
		@DisplayName("lots convention has the given scale")
		void lotsConventionHasTheGivenScale()
		{
			var convention = SizeQuotingConvention.lots(3);

			assertThat(convention.scale()).as("lots scale").isEqualTo(3);
			assertThat(convention.unit()).as("unit type").isInstanceOf(SizeQuotingUnit.Lots.class);
		}

		@Test
		@DisplayName("units convention has the given scale")
		void unitsConventionHasTheGivenScale()
		{
			var convention = SizeQuotingConvention.units(0);

			assertThat(convention.scale()).as("units scale").isEqualTo(0);
			assertThat(convention.unit()).as("unit type").isInstanceOf(SizeQuotingUnit.Units.class);
		}
	}

	@Nested
	@DisplayName("compatibility")
	final class Compatibility
	{
		@Test
		@DisplayName("same unit type at different scales is compatible")
		void sameUnitTypeAtDifferentScalesIsCompatible()
		{
			var left = PriceQuotingConvention.ticks(2);
			var right = PriceQuotingConvention.ticks(3);

			assertThat(left.isCompatibleWith(right)).as("ticks(2) compatible with ticks(3)").isTrue();
		}

		@Test
		@DisplayName("different unit types are not compatible")
		void differentUnitTypesAreNotCompatible()
		{
			var left = PriceQuotingConvention.ticks(2);
			var right = PriceQuotingConvention.thirtySeconds(2);

			assertThat(left.isCompatibleWith(right)).as("ticks incompatible with thirtySeconds").isFalse();
		}

		@Test
		@DisplayName("scale difference is left scale minus right scale")
		void scaleDifferenceIsLeftMinusRight()
		{
			var left = PriceQuotingConvention.ticks(3);
			var right = PriceQuotingConvention.ticks(1);

			assertThat(left.scaleDifference(right)).as("scale difference 3 - 1 = 2").isEqualTo(2);
		}
	}
}
