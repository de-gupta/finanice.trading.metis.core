package de.gupta.metis.core.types.currency;

import de.gupta.metis.core.types.money.MoneyType;
import de.gupta.metis.core.types.money.MoneyTypeFactory;
import de.gupta.metis.core.types.number.TradingNumberFactory;

public class df
{
	static void main()
	{
		MoneyType<Currency.EUR> money = MoneyTypeFactory.of(TradingNumberFactory.of(10), Currency.EUR.INSTANCE);
		MoneyType<Currency.USD> usd = MoneyTypeFactory.of(TradingNumberFactory.of(10), Currency.USD.INSTANCE);

		final var add = money.add(money);
//		final var adds = money.add(usd); // doesnt' compile
		System.out.println(add.value());

//		MyCurrency my = new MyCurrency();
//
//		MoneyType<MyCurrency> mys = MoneyTypeFactory.of(TradingNumberFactory.of(10), my);
//		final var add1 = mys.add(money); // doesn't compile
//
//		Currency nonsense1 = new Currency()
//		{
//			@Override
//			public int canonicalScale()
//			{
//				return 4;
//			}
//		};
//
//		Currency nonsense2 = new Currency()
//		{
//			@Override
//			public int canonicalScale()
//			{
//				return 6;
//			}
//		};
//
//		MoneyType<Currency> ns1 = MoneyTypeFactory.of(TradingNumberFactory.of(10), nonsense1);
//		MoneyType<Currency> ns2 = MoneyTypeFactory.of(TradingNumberFactory.of(10), nonsense2);
//		System.out.println(ns1.add(ns2));
	}
}