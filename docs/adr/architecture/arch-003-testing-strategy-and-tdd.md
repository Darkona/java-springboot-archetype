## ADR arch-003 — Domain testing standards: 100% unit coverage, mutation testing, and TDD

**Date:** 2025-10-02

**Status:** Accepted

### Decision
Domain model classes must be covered by unit tests at 100% coverage of lines, methods, and paths. Mutation testing must be applied to the domain layer with a minimum mutation score of 65% (or higher as the codebase matures). All changes to domain model classes must be developed using Test-Driven Development (TDD): write failing tests describing the expected behavior, implement the behavior until tests pass, and then refactor with tests green.

### Context
The domain model classes encapsulate business rules and invariants. Bugs within domain logic are costly and often subtle; comprehensive, verifiable tests combined with mutation analysis increase confidence that domain rules are implemented correctly and remain resilient to changes. TDD ensures that behavior is specified and validated by tests prior to implementation.

### Rationale
- **100% unit coverage** on domain code prompts authors to traverse all code paths, revealing untested edge cases.
- **Mutation testing** (e.g., PIT) assesses the strength of the test suite by introducing small changes and verifying tests fail — a practical approach to ensure tests assert actual behavior.
- **TDD** creates an executable, living specification of domain rules and mitigates the risk of regressions.
- Pairing coverage metrics with mutation testing offers both breadth (coverage) and depth (assertiveness) of testing efforts.

### Rules
1. **Scope**
   - These rules apply to the domain packages (e.g., `com.archetype.layer.domain.*` and any other packages that exclusively contain domain logic).
   - Integration, infrastructure, controllers, and adapters have their own testing standards (integration tests, contract tests, etc.) but are not subject to the 100% domain coverage requirement.

2. **Unit Test Coverage**
   - Domain classes must achieve 100% line, branch, and method coverage. Tests should exercise all branches and paths, including edge cases and invalid inputs that trigger domain exceptions.
   - Utilize JUnit 5 (recommended) and standard assertion libraries. Avoid mocking within domain tests; prefer actual object instances.

3. **Mutation Testing**
   - Run a mutation testing tool (PIT/Pitest) for the domain module(s) as part of local verification and CI processes.
   - Minimum mutation score: 65% (higher scores encouraged). If a domain package's score falls below this threshold, authors must enhance tests prior to merging changes.
   - Configure PIT to focus on domain packages and to ignore generated code or trivial getters/setters if intentionally excluded — however, aim to keep exclusions minimal.

4. **TDD Process**
   - Authors must write tests first: starting with failing tests that specify expected behaviors or invariants.
   - Implement the smallest change required to make the test pass.
   - Refactor while ensuring tests remain green, and re-run mutation tests locally to verify test quality.
   - Commit tests and production code together, with tests clearly demonstrating new behavior.

5. **Tooling & Automation**
   - Employ JaCoCo (or equivalent) to generate coverage reports for unit tests.
   - Set up Pitest (mutation testing) in Gradle with a configuration targeting domain packages.
   - Enforce the following checks in CI:
     - Execute unit tests (fail build upon test failure).
     - Check JaCoCo coverage for domain packages (fail build if coverage < 100%).
     - Run Pitest, or a policy that fails the build if mutation score < threshold OR requires merge-time approval with an explicit mitigation plan when mutation testing is slow (consider exemptions).
   - Optionally, execute a rapid mutation configuration in CI (reduced timeout/select mutators) and a comprehensive run in nightly builds.

6. **Exemptions and Pragmatic Considerations**
   - While 100% coverage is demanding, temporary, documented exemptions with clear improvement plans can be permissible during new large domain introductions or incremental adoptions (PR must detail the plan and timeline).
   - Excluded from mutation testing should be generated code, trivial POJOs, or records without logic; however, the behavior surface must remain tested.
   - For legacy areas containing brittle tests, teams must formulate a remediation plan rather than allowing permanent exceptions.

7. **Reporting and Visibility**
   - Include coverage and mutation reports within CI artifacts and store them in an accessible location.
   - Make the mutation score and coverage thresholds visible within the repository's README or contributing guide.

### Implementation Steps
1. Integrate testing tools into the build:
    - Add JaCoCo plugin for coverage reporting.
    - Incorporate the Pitest plugin and configure it for domain packages (e.g., `com.archetype.layer.domain.*`).
    - Adjust Gradle tasks:
        - `:domain:test` executes unit tests alongside JaCoCo.
        - `:domain:pitest` handles mutation analysis (fast mode for CI).
        - `check` or CI pipeline must depend on the coverage check task; include Pitest or its verification artifact as an optional step.

2. Establish a CI strategy:
    - Run fast unit tests and coverage checks on every PR.
    - Execute a quick Pitest run on PR or require authors to run Pitest locally and attach the report.
    - Perform a full Pitest nightly build for extended analyses.

3. Provide sample tests and mutation configurations:
    - Add an example domain test illustrating how to cover all paths and demonstrating how a mutation test identifies weak assertions.
  
4. Implement monitoring:
    - Maintain reports as CI artifacts; alert the team upon degradation in mutation scores.

### Consequences / Trade-offs
- **Positive:** High confidence in the correctness of domain code; reduced regression risks, and clearer specifications through tests.
- **Negative:** Increased development time and friction — achieving 100% coverage and improving mutation scores can be labor-intensive.
- **Risk:** Mutation testing may introduce slowness; careful configuration within CI is essential. Strict thresholds can impede progress; use documented exemption processes when necessary.

### Notes and Best Practices
- Domain tests should prefer real instances and verify behavior through assertions on state and exceptions, avoiding excessive mocking.
- For complex behavior, segment tests into small units that assert a single rule per test.
- Maintain pragmatic mutation test configurations: limit PIT timeouts and mutators in PR pipelines to prevent prohibitively long build times.
- Consider incremental enforcement: begin by mandating 100% coverage, and introduce mutation checks once teams are comfortable and local runs are efficient.
