# metis.core

A trading types library for the JVM. Provides type-safe, algebraically honest primitives for price, size, and money —
backed by `long` arithmetic for performance — with explicit quoting conventions governing all scale-dependent
operations.

**Module:** `de.gupta.metis.core`  
**Group:** `io.github.de-gupta`  
**Java:** 25  
**Dependencies:** athena (algebra), aletheia (functional)

---

## Design in one paragraph

`PriceType` and `SizeType` are bare typed wrappers around a raw `long`. They have no arithmetic methods because addition
requires knowing the scale, and scale is an instrument/venue concern — it lives in a `QuotingConvention`, not in the
value. `MoneyType<C>` is the exception: it always carries a `Currency`, which pins the scale, so it IS a full additive
group. Multiplication (`Price × Size`) is the main cross-type operation and only compiles when the price convention is
currency-backed.

---

## Package map

| Package                                | Contents                                               |
|----------------------------------------|--------------------------------------------------------|
| `de.gupta.metis.core.types.currency`   | `Currency`, `AbstractCurrency`                         |
| `de.gupta.metis.core.types.number`     | `TradingNumber`, `TradingNumberFactory`                |
| `de.gupta.metis.core.types.price`      | `PriceType`, `PriceTypeFactory`                        |
| `de.gupta.metis.core.types.size`       | `SizeType`, `SizeTypeFactory`                          |
| `de.gupta.metis.core.types.money`      | `MoneyType<C>`, `MoneyTypeFactory`                     |
| `de.gupta.metis.core.types.quoting`    | All quoting convention and unit types                  |
| `de.gupta.metis.core.types.arithmetic` | `PriceArithmetic`, `SizeArithmetic`, `MoneyArithmetic` |
| `de.gupta.metis.core.types.exception`  | `IncompatibleInputException`, `MissingInputException`  |

---

## Core types

### `TradingNumber`

A `long`-backed algebraic primitive. Implements `EuclideanDomain<TradingNumber>` (add, negate, multiply,
divideWithRemainder, quotient, remainder) and `DescriptivelyComparable<TradingNumber>`.

```java
TradingNumber a = TradingNumberFactory.of(4500);
TradingNumber b = TradingNumberFactory.of(300);
TradingNumber sum  = a.add(b);          // 4800
TradingNumber prod = a.multiply(b);     // 1350000
TradingNumber zero = TradingNumberFactory.zero();
```

All arithmetic uses `Math.addExact`, `Math.multiplyExact`, etc. — overflow throws `ArithmeticException`.

---

### `PriceType`

A typed wrapper around `TradingNumber`. Has no arithmetic methods — all operations go through `PriceArithmetic` with an
explicit convention. Implements `Zero<PriceType>` (the `zero()` method is scale-independent and always returns raw 0).

```java
PriceType price = PriceTypeFactory.of(4500);       // raw value 4500
PriceType zero  = PriceTypeFactory.of(0);          // or: price.zero()
TradingNumber raw = price.value();                 // extract the TradingNumber
```

`PriceType` carries no currency and no scale. Its raw value is only meaningful in the context of a
`PriceQuotingConvention`.

---

### `SizeType`

Identical structure to `PriceType`. Carries no currency. Raw value is only meaningful in the context of a
`SizeQuotingConvention`.

```java
SizeType size = SizeTypeFactory.of(250_000_000);   // e.g., 2.5 BTC at scale 8
SizeType zero = SizeTypeFactory.of(0);             // or: size.zero()
```

---

### `MoneyType<C extends Currency>`

Generic on currency. Implements `AdditiveAbelianGroup<MoneyType<C>>` and `DescriptivelyComparable<MoneyType<C>>`. The
currency's `canonicalScale()` defines the scale — `MoneyType(4500, Currency.USD.INSTANCE)` means 4500 cents = $45.00.

```java
MoneyType<Currency.USD> a = MoneyTypeFactory.of(TradingNumberFactory.of(4500), Currency.USD.INSTANCE);
MoneyType<Currency.USD> b = MoneyTypeFactory.of(TradingNumberFactory.of(300),  Currency.USD.INSTANCE);

MoneyType<Currency.USD> sum  = a.add(b);           // 4800 cents = $48.00
MoneyType<Currency.USD> neg  = a.negate();         // -4500 cents
MoneyType<Currency.USD> zero = a.zero();           // 0 USD

ComparisonResult cmp = a.compare(b);               // GREATER_THAN

Currency  currency = a.currency();                 // Currency.USD.INSTANCE
TradingNumber raw  = a.value();                    // TradingNumber(4500)
String text        = a.toString();                 // "4500 USD"
```

**Generic safety:** `MoneyType<Currency.USD>` and `MoneyType<Currency.EUR>` are distinct types. `a.add(eurMoney)` is a
compile error.  
**Custom currencies:** Implement `Currency` (or extend `AbstractCurrency` for free `toString()`) to get the same generic
safety for exotic currencies.

---

## Currency

```java
public interface Currency {
    String code();
    String name();
    int canonicalScale();
}
```

Known singleton currencies — access via `INSTANCE`:

| Constant                | Code | Name           | Scale        |
|-------------------------|------|----------------|--------------|
| `Currency.USD.INSTANCE` | USD  | US Dollar      | 2 (cents)    |
| `Currency.EUR.INSTANCE` | EUR  | Euro           | 2            |
| `Currency.JPY.INSTANCE` | JPY  | Japanese Yen   | 0            |
| `Currency.GBP.INSTANCE` | GBP  | Pound Sterling | 2            |
| `Currency.BTC.INSTANCE` | BTC  | Bitcoin        | 8 (satoshis) |
| `Currency.ETH.INSTANCE` | ETH  | Ether          | 9 (gwei)     |

`AbstractCurrency` provides `final toString()` returning `code()`. Custom currencies that extend it get this for free.

---

## Quoting conventions

A `QuotingConvention` has a `unit()` (the kind of scale) and a `scale()` (decimal places). Two conventions are
compatible for addition if and only if `unit().equals(other.unit())`.

### Price quoting conventions

| Factory method                                | Type                                                     | Unit     | Scale                       |
|-----------------------------------------------|----------------------------------------------------------|----------|-----------------------------|
| `PriceQuotingConvention.ticks(scale)`         | `PriceQuotingConvention<PriceQuotingUnit.Ticks>`         | TICKS    | explicit                    |
| `PriceQuotingConvention.thirtySeconds(scale)` | `PriceQuotingConvention<PriceQuotingUnit.ThirtySeconds>` | 32nds    | explicit                    |
| `PriceQuotingConvention.currency(currency)`   | `PriceQuotingConvention<CurrencyPriceUnit<C>>`           | CURRENCY | `currency.canonicalScale()` |

For `currency()`, scale is derived from the currency — no scale argument:

```java
var usdPriceConv = PriceQuotingConvention.currency(Currency.USD.INSTANCE);  // scale = 2
var btcPriceConv = PriceQuotingConvention.currency(Currency.BTC.INSTANCE);  // scale = 8
var ticksConv    = PriceQuotingConvention.ticks(2);
var thirtyConv   = PriceQuotingConvention.thirtySeconds(0);
```

### Size quoting conventions

| Factory method                           | Unit      |
|------------------------------------------|-----------|
| `SizeQuotingConvention.units(scale)`     | UNITS     |
| `SizeQuotingConvention.lots(scale)`      | LOTS      |
| `SizeQuotingConvention.contracts(scale)` | CONTRACTS |

```java
var sharesConv   = SizeQuotingConvention.units(0);    // whole shares
var btcSizeConv  = SizeQuotingConvention.units(8);    // satoshis
var lotsConv     = SizeQuotingConvention.lots(0);
```

### Compatibility rule

Conventions of **different unit kinds** are **incompatible** — adding a ticks price to a currency price, or units size
to lots size, throws `IncompatibleInputException`. Same kind, different scale → scales are normalised automatically (the
less precise operand is scaled up; no precision is lost).

---

## Arithmetic

### `PriceArithmetic`

```java
// Same convention for both operands
var arithmetic = PriceArithmetic.of(PriceQuotingConvention.ticks(2));

PriceType sum    = arithmetic.add(p1, p2);
PriceType neg    = arithmetic.negate(p1);
PriceType zero   = arithmetic.zero();

// Different conventions (left and right may have different scales)
var mixed = PriceArithmetic.of(PriceQuotingConvention.ticks(3), PriceQuotingConvention.ticks(2));
PriceType sum2 = mixed.add(p1, p2);  // right is scaled up before adding
```

Throws `IncompatibleInputException` if the two conventions have different unit kinds.  
Throws `ArithmeticException` on long overflow.

### `SizeArithmetic`

Same interface as `PriceArithmetic` but typed for `SizeType` and `SizeQuotingConvention`.

```java
var arithmetic = SizeArithmetic.of(SizeQuotingConvention.units(0));
SizeType sum  = arithmetic.add(s1, s2);
SizeType neg  = arithmetic.negate(s1);
```

### `MoneyArithmetic`

Three cross-type operations forming a closed triangle: multiply and two divisions. All three share the same scale
formula; `sizeConvention` is always required because `sizeScale` participates in every operation.

**Scale formula shared by all three:**

| Operation  | Signature                                    | Formula                                        |
|------------|----------------------------------------------|------------------------------------------------|
| `multiply` | `(Price, Size, priceConv, sizeConv) → Money` | `moneyRaw = priceRaw × sizeRaw / 10^sizeScale` |
| `divide`   | `(Money, Size, sizeConv) → Price`            | `priceRaw = moneyRaw × 10^sizeScale / sizeRaw` |
| `divide`   | `(Money, Price, sizeConv) → Size`            | `sizeRaw = moneyRaw × 10^sizeScale / priceRaw` |

`multiply` only accepts `PriceQuotingConvention<CurrencyPriceUnit<C>>` — ticks or 32nds conventions are a **compile
error**. The two `divide` operations accept any `SizeQuotingConvention<?>` and assume the price operand is at the
money's canonical scale.

Division uses integer truncation — remainders are discarded. Division by zero throws `ArithmeticException`.

```java
var priceConv = PriceQuotingConvention.currency(Currency.USD.INSTANCE); // scale 2
var sizeConv  = SizeQuotingConvention.units(8);                          // satoshis

// multiply: Price × Size → Money
MoneyType<Currency.USD> notional = MoneyArithmetic.multiply(
    PriceTypeFactory.of(4_500_000L),    // $45,000.00
    SizeTypeFactory.of(250_000_000L),   // 2.5 BTC
    priceConv, sizeConv
);
// notional.value() = 11_250_000 cents = $112,500.00

// divide: Money / Size → Price  (inverse of multiply)
PriceType price = MoneyArithmetic.divide(notional, SizeTypeFactory.of(250_000_000L), sizeConv);
// price.value() = 4_500_000 cents = $45,000.00

// divide: Money / Price → Size  (inverse of multiply)
SizeType size = MoneyArithmetic.divide(notional, PriceTypeFactory.of(4_500_000L), sizeConv);
// size.value() = 250_000_000 satoshis = 2.5 BTC

// JPY (canonicalScale = 0, no decimal division)
MoneyType<Currency.JPY> jpyNotional = MoneyArithmetic.multiply(
    PriceTypeFactory.of(5000L), SizeTypeFactory.of(100L),
    PriceQuotingConvention.currency(Currency.JPY.INSTANCE),
    SizeQuotingConvention.units(0)
);
// jpyNotional.value() = 500_000 yen
```

---

## What this library does NOT contain

- **Instrument / symbol types** — `PriceType` carries no instrument context. Scale and quoting convention are always
  supplied externally by the caller who knows the instrument.
- **Yield / rate types** — basis points and percentage-based quoting belong to a future `YieldType` domain.
  `FixedScaleQuotingUnit` is reserved for this purpose.
- **FX conversion** — cross-currency operations require an FX rate type not yet modelled.
- **Comparison of Price/Size** — comparing two `PriceType` values requires knowing their scale (i.e., the convention).
  Not yet implemented; use `TradingNumber.compare()` on extracted raw values if needed.

---

## Invariants for agents to remember

1. `PriceType.value()` and `SizeType.value()` return the **raw long** — meaningless without a convention.
2. `MoneyType<C>.value()` is always in **`currency.canonicalScale()` units** — fully self-describing.
3. `MoneyArithmetic.multiply` only accepts `PriceQuotingConvention<CurrencyPriceUnit<C>>` — ticks/32nds conventions are
   a compile error.
4. Adding values with different scales of the **same unit kind** is valid and lossless — the less precise is scaled up.
5. Adding values with **different unit kinds** always throws at runtime.
6. `Currency.USD.INSTANCE` etc. are singletons — safe to compare with `==` or `isSameAs()`.
7. Arithmetic overflow throws `ArithmeticException` (from `Math.addExact`, `Math.multiplyExact`, `Math.powExact`).
8. `MoneyArithmetic.divide(money, price, sizeConvention)` assumes `price` is at `money.currency().canonicalScale()`.
   There is no compile-time enforcement — the caller is responsible. Passing a tick-priced `PriceType` gives a silently
   wrong result.