# Feature Request: Time Domain Types for `metis.core.types`

## Summary

Please add first-class time domain types to `metis.core.types` so downstream modules can model bars, sessions, temporal
ranges, and calendar-aware aggregation without leaking raw JDK time primitives throughout the public API.

`Instant` is technically adequate as an underlying representation, including for high-frequency data, because it is
UTC-based, unambiguous, and supports nanosecond precision. The request is therefore **not** about replacing `Instant`
for precision reasons. It is about introducing a **domain abstraction** for time with controlled semantics, arithmetic,
interval operations, and interoperability.

Our preferred direction is:

- `TimeType` as the canonical timestamp domain type
- `TimeDeltaType` or `DurationType` as the difference/shift type
- `TimeIntervalType` as the ordered interval abstraction over `TimeType`

These should cover the common needs of market data, bar aggregation, session modeling, and indicator inputs without
forcing downstream modules to invent their own time wrappers.

---

> Note
>
> Athena now provides the generic interval / range abstractions we need at the mathematical layer. That is supporting
> context for this request, not its core. The main request remains that `metis.core.types` should supply the concrete
> time-domain types and their domain-facing API.

---

## Motivation

### Why raw `Instant` is not enough at the domain boundary

Using `Instant` directly everywhere creates several issues:

- it leaks infrastructure/JDK details into domain APIs
- it provides no domain vocabulary for shifts, windows, bucketing, or intervals
- it leaves interval closure semantics implicit
- it makes future extensions such as trading sessions, exchange alignment, or market calendars harder to express cleanly
- it encourages every downstream module to add its own ad hoc helpers

### Why we need this now

Several future modules need a principled time model:

- bar and candle construction from historical and realtime data
- session-aware aggregation
- timeframe alignment and bucketing
- distance and duration computations
- interval containment / overlap / span logic
- eventually strategy and input modules that share the same temporal model

We would like those modules to depend on `core.types` for time semantics rather than re-implement them inconsistently.

---

## Proposed Type Family

### `TimeType`

Canonical timestamp value object.

Preferred characteristics:

- immutable
- UTC-normalized
- backed by `Instant`
- totally ordered
- explicit interoperability with `Instant`
- no timezone semantics in the type itself

Preferred API shape:

```java
public interface TimeType extends TotallyOrdered<TimeType>
{
	Instant asInstant();

	TimeType shiftBy(TimeDeltaType delta);

	TimeDeltaType distanceTo(TimeType other);

	boolean isBefore(TimeType other);

	boolean isAfter(TimeType other);

	boolean isAtOrBefore(TimeType other);

	boolean isAtOrAfter(TimeType other);
}
```

Factory:

```java
public final class TimeTypeFactory
{
	public static TimeType of(Instant instant)
	{ ...}
}
```

Optional convenience factories if desired:

- `ofEpochMilli(long)`
- `ofEpochSecond(long)`
- `now(Clock)` if the team wants controlled clock injection

### `TimeDeltaType`

Difference / shift type between two timestamps.

This should represent elapsed duration, not a wall-clock timezone concept.

Preferred API shape:

```java
public interface TimeDeltaType
{
	Duration asDuration();

	TimeDeltaType add(TimeDeltaType other);

	TimeDeltaType subtract(TimeDeltaType other);

	TimeDeltaType negate();

	boolean isZero();

	boolean isPositive();

	boolean isNegative();
}
```

Factory:

```java
public final class TimeDeltaTypeFactory
{
	public static TimeDeltaType of(Duration duration)
	{ ...}
}
```

Optional conveniences:

- `zero()`
- `ofMillis(long)`
- `ofSeconds(long)`
- `ofNanos(long)`

### `TimeIntervalType`

Ordered interval over `TimeType`.

This is the most important type after `TimeType` itself. Bars, sessions, windows, and time-bounded input all want a
first-class interval abstraction rather than loose `openTime` / `closeTime` pairs.

Preferred API shape:

```java
public interface TimeIntervalType
{
	TimeType lowerBound();

	TimeType upperBound();

	IntervalClosure closure();

	TimeDeltaType length();

	boolean contains(TimeType time);

	boolean contains(TimeIntervalType other);

	boolean overlaps(TimeIntervalType other);

	boolean isDisjointFrom(TimeIntervalType other);

	boolean touches(TimeIntervalType other);

	TimeIntervalType span(TimeIntervalType other);

	TimeIntervalType shiftBy(TimeDeltaType delta);
}
```

Factory:

```java
public final class TimeIntervalTypeFactory
{
	public static TimeIntervalType closed(TimeType start, TimeType end)
	{ ...}

	public static TimeIntervalType closedOpen(TimeType start, TimeType end)
	{ ...}

	public static TimeIntervalType openClosed(TimeType start, TimeType end)
	{ ...}

	public static TimeIntervalType open(TimeType start, TimeType end)
	{ ...}
}
```

### `IntervalClosure`

Please model interval closure explicitly.

```java
public enum IntervalClosure
{
	OPEN,
	CLOSED,
	LEFT_OPEN_RIGHT_CLOSED,
	LEFT_CLOSED_RIGHT_OPEN
}
```

For market bars, `closed-open` is usually the most useful default, but the type should not hardcode that assumption.

---

## Algebra We Need

This is the practical algebra we need. We do **not** need maximal abstraction for its own sake.

### `TimeType`

Useful structure:

- totally ordered set
- affine space / torsor over `TimeDeltaType`

Operationally that means:

- two `TimeType` values can be compared
- subtracting two times yields a `TimeDeltaType`
- shifting a time by a delta yields another `TimeType`

In other words:

```java
TimeType +TimeDeltaType ->TimeType
TimeType -TimeDeltaType ->TimeType
TimeType -TimeType ->TimeDeltaType
```

This is the right model. A timestamp should **not** be treated as a generic additive group element in the same sense as
a number.

### `TimeDeltaType`

Useful structure:

- additive abelian group
- totally ordered, if the team is comfortable with signed durations

Operationally that means:

- zero element
- addition / subtraction
- negation
- ordering by magnitude/sign

This is where the algebra properly lives, not on `TimeType` itself.

### `TimeIntervalType`

Useful structure:

- interval over a totally ordered domain
- shift action by `TimeDeltaType`

Operationally that means:

- containment
- overlap
- disjointness
- touching
- span / hull
- length
- translation by a delta

This is the key abstraction for bars and sessions.

### Distance

If Athena already has a metric-space abstraction, the team may want to consider whether:

- `distanceTo(...)` on `TimeType` should be the domain-level API
- a deeper metric-space implementation should exist in Athena

For `core.types`, the domain operation we need is simply:

```java
TimeDeltaType distanceTo(TimeType other)
```

That is enough for downstream use. Any more abstract metric formalization can remain an implementation or cross-library
concern.

---

## Preferred Semantics

### Backing representation

Preferred backing representation is `Instant`.

Reason:

- already UTC
- precise enough
- widely interoperable
- avoids inventing custom epoch arithmetic

### Time zones

Do not encode timezone semantics into `TimeType`.

If downstream modules later need exchange-local calendar logic, that should be modeled separately through market
calendars or session rules, not by making the base time type ambiguous.

### Precision

Nanosecond support via `Instant` is sufficient.

If some external sources are only millisecond or microsecond precise, that is an input-quality issue rather than a
reason to weaken the domain type.

### Interval validation

`TimeIntervalType` should reject invalid orderings where the lower bound is after the upper bound.

Whether degenerate zero-length intervals are allowed should be deliberate and documented. My preference is:

- allow equal bounds
- let closure determine whether the interval is empty or point-like

---

## Why This Belongs in `core.types`

This should not be reimplemented downstream because:

- time is a cross-cutting domain concern
- bars, sessions, input, indicators, and strategies will all need compatible semantics
- interval and duration logic are too central to duplicate ad hoc
- downstream modules should consume a common domain vocabulary rather than invent wrappers

This is exactly the kind of type family that `core.types` should own.

---

## How We Expect to Use It Downstream

Examples of desired downstream modeling:

```java
public interface Bar<U extends PriceQuotingUnit, P extends QuotedPrice<U>> extends Candle<U, P>
{
	TimeIntervalType interval();
}
```

```java
public interface TradeBar<U extends PriceQuotingUnit, P extends QuotedPrice<U>, S extends SizeQuotingUnit>
		extends Bar<U, P>
{
	QuotedSize<S> volume();
}
```

Examples of bar construction concerns that should become straightforward once the types exist:

- build 1-minute bars by closed-open interval bucketing
- shift a bar window forward by one timeframe
- test whether a trade timestamp belongs to a bar interval
- span two adjacent windows into a larger one
- reason about session boundaries explicitly

---

## Non-Goals

This request is **not** asking for:

- timezone-rich business calendar types in the initial version
- exchange session calendars in the initial version
- generic symbolic algebra for its own sake
- replacing JDK `Instant` internally
- forcing every downstream API to expose all temporal abstractions immediately

The goal is a strong, minimal temporal domain foundation.

---

## Minimal Viable Deliverable

If the team wants to phase this, the smallest high-value delivery would be:

1. `TimeType`
2. `TimeDeltaType`
3. `TimeIntervalType`
4. factories for all three
5. ordering, shifting, distance, containment, overlap, span, and length

That would already unlock clean bar/session modeling for downstream modules.

---

## Recommendation

Please add:

- `TimeType`
- `TimeDeltaType`
- `TimeIntervalType`
- explicit interval closure support

with the following conceptual model:

- `TimeType` is an ordered timestamp domain object backed by `Instant`
- `TimeDeltaType` is the additive duration/shift domain object
- `TimeIntervalType` is the interval abstraction over the ordered time domain

and the following essential operations:

- comparison / ordering
- `time +/- delta`
- `time - time`
- interval containment / overlap / disjointness / touching / span
- interval length
- interval translation

This is the cleanest foundation for bars, aggregation, sessions, and shared time semantics across future Metis modules.