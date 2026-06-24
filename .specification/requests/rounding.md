# Rounding Support Request For `core.types`

Athena now supports ordered division with explicit `RoundingStrategy`, including stock strategies such as `floor`,
`ceiling`, and `truncate`.
`core.types` should expose that capability at the higher-order domain level so metis and other consumers do not have to
work with low-level arithmetic types directly.

## What we need

Add policy-aware overloads for the domain operations that currently divide and silently pick a quotient:

- `QuotedPrice.divide(int divisor, ...rounding...)`
- `QuotedSize.divide(int divisor, ...rounding...)`
- `MoneyType.asPricePer(size, convention, ...rounding...)`
- matching overloads in `MoneyArithmetic`

These convenience overloads should return the rounded quotient directly.
The existing exact APIs returning `DivisionResult<...>` should stay, because they are still the correct lossless form.

## API direction

Prefer a `core.types`-level abstraction such as `PriceRoundingStrategy`, `SizeRoundingStrategy`, or a generic
scalar-division policy wrapper over exposing raw athena `RoundingStrategy<TradingNumber>` to downstream modules.
Internally, those policies can delegate to athena.

That keeps:

- athena as the algebra engine
- `core.types` as the domain adapter
- metis free of `TradingNumber` and other low-level arithmetic concerns

## Compatibility

Keep today’s default behavior as `floor` for existing overloads and code paths.
That matches the current effective behavior and avoids a silent semantic change.

## Nice follow-up

If useful, `core.types` can also expose named ready-made policies backed by athena defaults, so consumers can say things
like `floor`, `ceiling`, or `truncate` without writing custom strategies themselves.