# AGENTS.md

## Architecture

- Flow: `controller` → `usecase` → `domain` (optional) → `data`.
- Controllers and UseCases are **dto**-based; persistence lives in `benefit-data-module` (`im.dangmoo.benefit.data`): **`entity`** (documents / types) and **`infrastructure`** (keys/config at root; repositories / publishers / scripts flat under `infrastructure.<domain>`).
- Thin controllers. Split by concern when flows diverge (e.g. issue / use / book).
- App paths use product language (`/promotions`), not internal policy wording.
- Domain: `of(...)` copies only fields needed for the rule; logic on the domain instance. Do not pass full data documents into domain methods.
- Domains do not hold other domains as fields. When multiple rules apply, the UseCase composes them.
- Domains do not throw. Express outcomes as state enums; UseCases map those to `ApiException`. Prefer instance methods on `of(...)` over static rule calls.
- Domain logic methods need tests.
- Prefer composition for feature variants over abstract-class hierarchies.
- New work belongs in the existing application / domain / data modules — mirror the nearest feature.
- Kafka: publish via `KafkaProducerTopics`, consume via `KafkaConsumerTopics` (separate classes; topic strings may match when produce and consume share a topic).
- Consumer / batch stay flat under `handler` / `listener` / `consumption` / `job` / `parameter` / `tasklet` — no domain subpackages.

## Change discipline

- Match neighboring code in the same area.
- Keep diffs scoped to the request.
