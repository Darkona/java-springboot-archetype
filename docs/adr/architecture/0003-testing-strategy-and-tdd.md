# ADR 0004 — Domain testing standards: 100% unit coverage, mutation testing, and TDD

Date: 2025-10-02

Status: Accepted

Decision
--------
Domain model classes must be covered by unit tests at 100% coverage of lines, methods and paths. Mutation testing must be applied to the domain layer with a minimum mutation score of 65% (or higher as the codebase matures). All changes to domain model classes must be developed using Test-Driven Development (TDD): write failing tests describing the expected behavior, implement the behavior until tests pass, and refactor with tests green.

Context
-------
Domain model classes encapsulate business rules and invariants. Bugs in domain logic are costly and often subtle; comprehensive, verifiable tests and mutation analysis increase confidence that domain rules are correctly implemented and resilient to change. TDD ensures that behavior is specified and validated by tests before implementation.

Rationale
---------
- 100% unit coverage on domain code forces authors to exercise all code paths and helps reveal untested edge-cases.
- Mutation testing (e.g., PIT) evaluates the strength of the test suite by introducing small changes and verifying tests fail — a pragmatic way to assess whether tests actually assert behavior.
- TDD creates an executable, living specification of domain rules and reduces the risk of regressions.
- Combining coverage metrics and mutation testing provides both breadth (coverage) and depth (assertiveness) of tests.

Rules
-----
1. Scope
   - These rules apply to the domain packages (e.g., `com.archetype.layer.domain.*` and any other packages that exclusively contain domain logic).
   - Integration, infrastructure, controllers, and adapters follow their own testing standards (integration tests, contract tests, etc.) but are not subject to the 100% domain coverage requirement.

2. Unit test coverage
   - Domain classes must have 100% line, branch, and method coverage. Tests must exercise all branches and paths, including edge cases and invalid inputs that raise domain exceptions.
   - Use JUnit 5 (recommended) and standard assertion libraries. Avoid excessive mocking inside domain tests; prefer real object instances.

3. Mutation testing
   - Run mutation testing tool (PIT/Pitest) for the domain module(s) as part of local verification and CI.
   - Minimum mutation score: 65% (higher encouraged). If a domain package falls below the threshold, the author must improve tests before merging the change.
   - Configure PIT to focus on domain packages and to ignore generated code or trivial getters/setters if they are intentionally excluded — but aim to keep excluded surface minimal.

4. TDD process
   - Authors must write tests first: failing tests that specify the expected behavior or invariant.
   - Implement the smallest change to make the test pass.
   - Refactor with tests remaining green and re-run mutation tests locally to confirm test quality.
   - Commit tests and production code together, with tests demonstrating the new behavior.

5. Tooling & automation
   - Use JaCoCo (or equivalent) to produce coverage reports for unit tests.
   - Configure Pitest (mutation testing) in Gradle with a profile targeting the domain packages.
   - In CI, enforce:
     - Unit test execution (fail build on test failure)
     - JaCoCo coverage check for domain packages (fail build if coverage < 100%)
     - Pitest execution or a policy that fails build if mutation score < threshold OR require merge-time approval with explicit mitigation plan when mutation test is slow (see exemptions)
   - Consider running a fast mutation configuration in CI (reduced timeout / selected mutators) and a more exhaustive run in nightly pipeline.

6. Exemptions and pragmatic considerations
   - 100% coverage is strict; when introducing a new large domain or during incremental adoption, a temporary, documented exemption with a clear improvement plan is allowed (PR must include the plan and timeline).
   - For generated code, trivial POJOs or records with no logic, it is acceptable to exclude from mutation testing; however, the domain behavior surfaces must remain tested.
   - If a legacy area contains brittle tests, the team must create a remediation plan rather than permanently exempting code.

7. Reporting and visibility
   - Provide coverage and mutation reports in CI artifacts and store them in an accessible location.
   - Make the mutation score and coverage thresholds visible in the repository's README or contributing guide.

Implementation Steps
--------------------
1. Add testing tools to build:
   - Add JaCoCo plugin for coverage reporting.
   - Add Pitest plugin and configure it to run over domain packages (e.g., `com.archetype.layer.domain.*`).
   - Configure Gradle tasks:
     - `:domain:test` runs unit tests and JaCoCo
     - `:domain:pitest` runs mutation analysis (fast mode for CI)
     - `check` or CI pipeline must depend on the coverage check task and optionally pitest or pitest verification artifact
2. Add Gradle verification thresholds:
   - Fail the build when JaCoCo reports domain coverage < 100%
   - Fail the build (or fail gating) if pitest mutation score < 65% for domain code
3. Educate contributors:
   - Add a CONTRIBUTING.md snippet describing TDD requirement for domain changes and how to run mutation tests locally.
4. Establish CI strategy:
   - Fast unit tests and coverage checks on every PR.
   - Fast pitest run on PR or require the author to run pitest locally and attach report.
   - Full pitest nightly build for deeper analysis.
5. Add sample tests and mutation configuration:
   - Provide an example domain test that demonstrates how to cover all paths and how a mutation test catches weak assertions.
6. Add monitoring:
   - Ensure reports are kept as CI artifacts; alert the team when mutation score degrades.

Consequences / Trade-offs
-------------------------
- Positives:
  - High confidence in domain code correctness.
  - Lower regression risk and clearer specification through tests.
- Negatives:
  - Increased development time and friction — writing tests to reach 100% coverage and improving mutation score is labor-intensive.
  - Mutation testing can be slow; careful CI configuration is required (fast/partial runs for PRs, full runs in nightly pipelines).
  - Strict thresholds can block progress; use documented exemption process when necessary.

Notes and best practices
------------------------
- Domain tests should prefer real instances and verify behavior via assertions on state and exceptions, not by excessive mocking.
- For complex behavior, break tests into small units that assert one rule per test.
- Keep mutation test configuration pragmatic: limit PIT timeouts and mutators in PR pipelines to avoid prohibitive build times.
- Consider incremental enforcement: start by requiring 100% coverage; introduce mutation checks once teams are comfortable and local runs are efficient.
