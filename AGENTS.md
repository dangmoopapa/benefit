# AGENTS.md

## Architecture

- Flow: `controller` → `usecase` → `domain` (optional) → `infrastructure`.
- Controllers and UseCases are **model**-based; infrastructure is **data**-based (documents, repositories).
- Thin controllers. Split by concern when flows diverge (e.g. issue / use / book).
- App paths use product language (`/promotions`), not internal policy wording.
- Domain: `of(...)` copies only fields needed for the rule; logic on the domain instance. Do not pass full infrastructure documents into domain methods.
- Prefer composition for feature variants over abstract-class hierarchies.
- New work belongs in the existing application / domain / infrastructure modules — mirror the nearest feature.

## Change discipline

- Match neighboring code in the same area.
- Keep diffs scoped to the request.
