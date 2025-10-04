# ADR 0005 — Use github.com/darkona/logged for local development and enforce sensitive-data obfuscation

Date: 2025-10-02

Status: Accepted (for local/dev usage)

Decision
--------
Prefer using the `logged` library (github.com/darkona/logged) for local development and debugging. Configure `logged` globally for developer environments to enable convenient, consistent, and togglable logging semantics. In all environments (local, dev, staging, prod) enforce policies and tooling to obfuscate or redact sensitive data before it is emitted to logs.

Context
-------
Logging is essential for diagnosis and local debugging. Local development benefits from a developer-friendly logging library that enables quick inspection, structured output, and configuration switches. However, logs are a common vector for leaking secrets or sensitive personal data. We need a pragmatic default for local debugging while ensuring we never expose sensitive information in shared or production logs.

Rationale
---------
- `logged` is lightweight and developer-focused; it simplifies toggling verbose/debug logging locally.
- Centralized logging configuration improves developer ergonomics and consistency across modules.
- Obfuscation/redaction prevents accidental leak of sensitive fields (PII, secrets, credentials) and satisfies security and compliance needs.
- Using `logged` for local/dev does not preclude structured, production-grade logging (e.g., Logback/SLF4J + JSON) in CI/production — treat `logged` as the developer convenience layer.

Guidelines
----------
1. Dependency and scope
   - Add `logged` as a development-time dependency. Prefer marking non-essential dev-only utilities as `testImplementation` or conditionally enabled by profile/environment so they are not active by default in production artifacts.
   - Example (Gradle): add dependency and document how to enable it in `application-local.yaml` or via a `spring.profiles.active` flag.

2. Global configuration
   - Create a single configuration class or module (e.g., `com.skeletor.logging.LoggingConfig`) that wires `logged` for local profiles and configures:
     - default log level for local development,
     - human-friendly formatting,
     - a global redaction pipeline (see below).
   - Use Spring profiles to enable `logged` only for local/dev: `@Profile("local","dev")` or similar.

3. Sensitive-data obfuscation / redaction
   - Always run logs through a redaction layer before output. Redaction must cover at least:
     - Authorization headers, tokens, API keys
     - Passwords and secret values
     - Personal Identifiable Information (PII) fields (e.g., national id, social security, email as required)
     - Payment or card numbers
   - Implement a central Redactor service with a configurable list of field names/regex patterns to mask.
     - Default mask result: replace middle characters with `*` (for example `sk-***********xyz`) or replace entire value with `[REDACTED]` depending on sensitivity and config.
     - Use deterministic masking for logs used in debugging where partial visibility helps, but make full redaction the default for production.
   - Ensure structured log outputs (JSON) run field-level redaction before serialization.

4. Logging API and usage
   - For application code, prefer using the configured logging adapter (a small wrapper) instead of directly calling `System.out` or lower-level logging from multiple places.
   - Provide helper methods to safely log objects:
     - `safeLog.debug("request payload: {}", Redactor.redact(requestDto))`
     - This ensures DTOs/POJOs pass through redaction logic.
   - Encourage developers to avoid logging entire objects that may include secrets unless using the safeLog + redaction path.

5. Automation & checks
   - Add a unit test or integration test that validates the redactor masks configured sensitive keys.
   - Add an automated scanner (static or dynamic) in CI that looks for suspicious logging patterns:
     - Logging of fields named `password`, `secret`, `token`, `ssn`, etc.
     - Raw logging of Authorization headers or full request bodies to standard (non-redacted) sinks.

6. Documentation & examples
   - Document how to enable/disable developer logging in README and in `docs/logging.md`.
   - Provide code examples:
     - How to wire `logged` in a `@Configuration` class for `local` profile.
     - How to use the Redactor API and the safe logging helper.

7. Runtime behaviour: local vs production
   - Local/dev: `logged` may format logs to developer-friendly output and include additional debug fields (subject to redaction).
   - Staging/prod: prefer structured logging (JSON) with field-level redaction enforced and `logged` disabled or configured to forward through SLF4J/Logback with redaction active.

Implementation notes
--------------------
- Reference: github.com/darkona/logged — consult README for configuration options and recommended usage.
- Example artifact addition (Gradle): evaluate whether `logged` should be `implementation` or `developmentOnly` based on build tooling. If the project uses the `developmentOnly` configuration or a separate dev profile artifact, prefer that. Otherwise include as `implementation` but conditionally enable via Spring profile.
- Implement a `Redactor` utility:
  - Accept an object or map and a list of sensitive keys/regex patterns.
  - Recursively walk structures (maps, POJOs, collections) and mask values for configured keys.
  - For logging strings that contain tokens (e.g., Authorization header), run regex replacement to mask token bodies.
- Add a `SafeLogger` wrapper that:
  - Accepts any object, runs `Redactor.redact(obj)`, and delegates to the underlying logging implementation (either `logged` or SLF4J depending on active profile).
  - Provides convenience methods for JSON pretty-print or compact output based on profile.

Security considerations
-----------------------
- Treat the redaction list as security-sensitive configuration; do not put secret regexes in logs.
- Default to conservative redaction: mask broadly rather than narrowly.
- Maintain and periodically review the redaction rules to adapt to new sensitive field names.
- Consider integration with secret scanning tools that detect secrets checked into logs or code.

Example snippets
----------------
1) High-level pseudo-usage:
```java
@Service
@Profile("local")
public class LoggingConfig {
    @Bean
    public SafeLogger safeLogger() {
        return new SafeLogger(new Redactor(defaultSensitivePatterns), new LoggedAdapter());
    }
}
```

2) Redaction behaviour:
- Input: { "password": "mySecret", "email": "dev@example.com", "token": "sk-live-abcdef" }
- Redacted: { "password": "[REDACTED]", "email": "dev@example.com", "token": "sk-*****ef" } (example policy)

Decision consequences
---------------------
Positive:
- Developers get a consistent, easy-to-use logging experience locally.
- Centralized redaction reduces risk of accidental exposure.
- Clear guidance minimizes ad-hoc logging and insecure practices.

Negative:
- Additional library introduces another dependency that must be evaluated for license/maintenance.
- Developers must follow the safe logging pattern; training and enforcement required.
- Redaction may hide some context needed for debugging; provide a safe path to get more detail locally (but never in shared/prod logs).

Next steps (suggested)
----------------------
- Add ADR file (this document) — done.
- Optionally: add `logged` dependency to build.gradle guarded by profile or dev-only configuration and create `LoggingConfig` + `SafeLogger` + `Redactor` implementation.
- Add tests that assert redaction works for common sensitive keys.
- Add docs/README snippet describing how to enable developer logging and how to use `SafeLogger`.
