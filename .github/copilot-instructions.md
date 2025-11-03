# Copilot Instructions for Traqqr

This is a modular Jakarta EE 10 application on Java 21, packaged as an EAR and designed to run on Open Liberty.
It follows a hexagonal architecture:

* the domain defines the core model, business logic and contracts (APIs/SPIs);
* adapters implement those contracts for I/O (REST, JSF, JPA).

Avoid Spring-specific patterns; use Jakarta EE (CDI, JAX-RS, JSF, JPA).

## Architecture (what lives where)

- Modules (see `app/pom.xml`):
  - `app/domain`: Core model and business services.
    - `.../api`: use case interfaces or API's, that are exposed to other modules.
    - `.../spi`: service interfaces or ports, implemented by adapters.
  - Adapters: `app/adapters/api` (JAX-RS), `app/adapters/web` (JSF/PrimeFaces), `app/adapters/jpa` (JPA-based persistence), `app/adapters/mem` (in-memory persistence, for testing only).
  - `runtime`: EAR assembly + Liberty config (see `runtime/pom.xml`).
  - `tests`: integration tests.
  - `docs`: documentation site.

## How components interact

- UI/REST → domain API (use case) → domain SPI → adapter implementations → database or external services.
- Domain never depends on adapters; adapters depend on domain contracts.
- Records for value objects; sealed types for operation results where relevant.

## Repository conventions

- Domain services: Jakarta CDI (`@ApplicationScoped`), constructor injection.
- Contracts return domain-centric results. Example: `measurements/spi/LocationLookupResult` is a sealed interface with:
  - `Success(Measurement.Location location)`
  - `Failure(Throwable cause)`
- Example service mapping (`LookupLocationServiceImpl`):
  - When invoking the `lookupLocation` method:
    - if description present → `NOT_NECESSARY`
    - else call `LocationLookup` SPI;
        - if success → `SUCCESS`.
        - if failure → `FAILURE`.
  - When invoking the `refreshLocation` method:
    - call `LocationLookup` SPI;
      - if success → `SUCCESS`.
      - if failure → `FAILURE`.
- Keep adapters thin (map I/O ⇆ domain). MapStruct (provided) can be used for DTO ↔ domain mappings.

## Build, test, run (use `mvn`)

- Full build + tests:
  - `mvn verify`
- Fast TDD on domain:
  - `mvn -pl :domain -am test`
- Run a single test:
  - `mvn -pl :domain -Dtest=it.mulders.traqqr.domain.measurements.LookupLocationServiceImplTest test`
- Liberty dev mode (hot reload) for runtime:
  - `mvn -pl :runtime -am io.openliberty.tools:liberty-maven-plugin:dev`

## Quality gates & style
- Java 21, Spotless Palantir formatter configured in root `pom.xml`; keep formatting/imports consistent.
- JUnit 5 + AssertJ (+ jqwik in some domain tests). Prefer constructor-injected collaborators for easy mocking. Avoid using Mockito or other mocking frameworks. 
