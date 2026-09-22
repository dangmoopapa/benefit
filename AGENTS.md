# AGENTS.md

## Conventions

- Thin controllers. UseCases use purpose verbs (`create`, `claim`, `apply`, …).
- Split controllers by concern when flows diverge (issue / use / book, etc.).
- App paths use product language (`/promotions`), not internal policy wording.
- Flow: `controller` → `usecase` → `domain` (optional) → `infrastructure`. Controllers and UseCases are model-based; infrastructure is data-based.
- Domain: `of(...)` copies only fields required for the rule; logic lives on the domain instance. Do not pass full infrastructure documents into domain methods.
- Prefer composition for variants; no abstract-class feature hierarchies.
- Fluent entity updates (`change…` / `update…` return `this`), then `repository.save(…)`.
- Precise naming; avoid platform-word collisions (`Applier`, not `Application`).
- Do not extract a private helper called only once. Remove unused methods.
- Match neighboring style. Keep diffs scoped to the request — no drive-by refactors or unsolicited docs.
