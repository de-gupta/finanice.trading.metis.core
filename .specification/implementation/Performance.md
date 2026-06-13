# Performance Notes

This document records known performance constraints and ideas for future optimisation. Nothing here is implemented; it
is a design reference for when speed becomes a hard requirement.

---

## Where this library fits in the stack

`metis.core` is a **trading types library** — it targets strategy logic, risk calculation, P&L, order management, and
convention-aware arithmetic. These layers typically operate at 100K–1M operations/second, where the current
implementation is entirely adequate.

It is **not** designed for the matching engine, hot market data fan-out, or sub-microsecond order processing. Those
layers use raw `long` arithmetic, avoid all heap allocation, and do not use wrapper types. That is not a failure of this
library; it is the correct layering for a trading system.

---

## Current hot-path cost profile

For `PriceArithmetic.add(PriceType left, PriceType right)` — the representative hot-path method:

```
extractor.apply(left)          — virtual dispatch (Function interface)
extractor.apply(right)         — virtual dispatch
scaleAndApply(l, r, ::add)     — BiFunction indirect call + if/else
  [if cross-scale]
    scaleFactor(n)             — new TradingNumberImpl allocation
    l.multiply(scaleFactor)    — new TradingNumberImpl allocation
  l.add(r)                     — new TradingNumberImpl allocation (result)
factory.apply(result)          — virtual dispatch + new PriceTypeImpl allocation
```

**Same-scale case (most common):** 3 virtual dispatches, 2 allocations (`TradingNumber` result + `PriceType` wrapper).  
**Cross-scale case:** 3 virtual dispatches, 4 allocations (plus the above, two extra for `scaleFactor` and the scaled
operand).

The allocations are **structural** — they follow from the immutable sealed-interface + record design. They cannot be
eliminated while returning `PriceType` in current Java.

---

## Three execution profiles

### `SAFE` — current implementation

- Overflow detection via `Math.addExact`, `Math.multiplyExact`, `Math.powExact`
- Compatibility validated at construction, scale difference cached
- Returns typed wrappers (`PriceType`, `SizeType`)
- Allocates on every arithmetic result

Suitable for all non-hot code paths.

### `FAST` — drop overflow detection

Only change: replace `Math.addExact` / `Math.multiplyExact` with plain `+` / `*`.

- No `ArithmeticException` on overflow — silent wraparound instead
- Still returns typed wrappers, still allocates
- Meaningful only when profiling proves `Math.addExact` is a bottleneck (unlikely to be the dominant cost)

### `RAW` — zero-allocation, return raw longs

The honest escape hatch for innermost loops. The return type changes: instead of `PriceType`, the method returns `long`.

```java
class PriceRawArithmetic
{
	private final long scaleMultiplier;  // 10^|scaleDifference|, precomputed
	private final boolean scaleRight;    // direction of scaling

	long add(long left, long right)
	{
		if (scaleDifference == 0) return left + right;
		return scaleRight ? left + right * scaleMultiplier
				: left * scaleMultiplier + right;
	}

	long compare(long left, long right)
	{ ...}
}
```

Zero allocations. Two integer operations. The data model is preserved — callers extract raw longs at the hot-loop entry
point and re-wrap at the exit point:

```java
// Entry: extract raw values
long l = price1.value().rawLong();
long r = price2.value().rawLong();

// Hot loop: operate on raw longs
long result = rawArithmetic.add(l, r);

// Exit: re-wrap only when leaving the hot zone
PriceType wrapped = PriceTypeFactory.of(TradingNumberFactory.of(result));
```

---

## What `RAW` requires from the data model

`TradingNumber` currently does not expose the backing `long`. To enable the raw path without reflection or `Unsafe`,
`TradingNumber` needs:

```java
public sealed interface TradingNumber ...{

long rawLong();  // exposes the backing long — explicit breach of encapsulation
    ...
			}
```

This is a deliberate design decision. Adding `rawLong()` acknowledges that for extreme-frequency callers the type
abstraction is a cost they cannot pay, while keeping the method named and explicit rather than obtained via reflection.

`scaleFactor` would also be cached as a `long` in `PriceRawArithmetic` at construction (not as a `TradingNumber`),
eliminating that allocation too.

---

## What Java cannot currently solve

If the return type is `PriceType`, a heap allocation happens. This is not a fixable implementation detail — it is a
property of the JVM object model.

**Project Valhalla (inline / value classes)** will change this. When `PriceType` can be declared as a value class, it
becomes stack-allocated or inlined by the JIT, costing nothing. Java 25 has early-access Valhalla features. When the
feature stabilises, the `SAFE` profile likely becomes zero-allocation for value types and the distinction between `SAFE`
and `RAW` collapses for those types.

Watch: [JEP 401 — Value Classes and Objects](https://openjdk.org/jeps/401) and successor JEPs.

---

## Design invariant to preserve

Any performance work must preserve the **data model**: `PriceType`, `SizeType`, `MoneyType`, `Currency`,
`QuotingConvention`. These remain the canonical types at all layer boundaries. Raw arithmetic is an **escape hatch**
inside a computation, not a replacement for the model. Values enter the hot zone as typed values and exit as typed
values; rawness is contained.