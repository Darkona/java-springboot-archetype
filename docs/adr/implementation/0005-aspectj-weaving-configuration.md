C:\Users\francisco.letterer\IdeaProjects\Olimpo# ADR 0011 — AspectJ weaving to support `logged` internal/self/private method logging

Date: 2025-10-03

Status: Accepted

Decision
--------
To enable `io.github.darkona:logged` to record internal/self/private method calls, this archetype supports using AspectJ weaving. Agents or maintainers may
enable either:

- Compile-Time Weaving (CTW) — preferred for production when private/self weaving is required and when you want stable, predictable behavior without JVM agent
  startup flags.
- Load-Time Weaving (LTW) with the AspectJ Java agent (`aspectjweaver.jar`) — allowed for local development, debugging and diagnostics; permitted in CI if
  intentionally configured. Use LTW (the `-javaagent` approach) when you need a quick enablement without changing the build.

Using the AspectJ agent solely to make `logged` work in production must be justified in an ADR describing risks, performance testing, and deployment changes.

Context
-------
The `logged` library provides powerful method-level logging (entry, return, exception, masks, MDC enrichment, OpenTelemetry integration) using AOP. Spring AOP
proxies intercept only public methods; to capture private methods and self-invocations `logged` offers AspectJ-based weaving. The `logged` README documents both
Load-Time Weaving (LTW) and Compile-Time Weaving (CTW) options and recommends approaches depending on needs.

Rationale
---------

- CTW yields the cleanest runtime environment (no -javaagent JVM flags) and is less likely to surprise production monitoring or deployment tooling.
- LTW is convenient for development and diagnostics because it avoids changing the build; developers can enable the agent locally.
- Using AspectJ weaving makes `logged` more effective (private/self calls) and completes the library's feature set.

Policy / Rules
--------------

1. Do not enable the AspectJ agent in production by default. If a production deployment requires AspectJ weaving, create an ADR documenting:
    - Why Spring AOP is insufficient (what private/self calls must be logged).
    - Performance and behavior testing results comparing CTW/LTW/no-weaving.
    - Deployment/ops implications and rollback plan.
    - Approvals from architecture/operations owners.

2. For local development and diagnostics:
    - Developers may enable LTW with the AspectJ agent (`-javaagent:/path/to/aspectjweaver.jar`) and a package-targeted `META-INF/aop.xml` to restrict weaving
      to the application's packages and the `io.github.darkona.logged` packages.

3. For CI / pre-production:
    - Prefer CTW where feasible (weave during build). If using LTW in CI, ensure the CI environment documents and controls the agent flags and that test
      baselines include the agent.

4. When using `logged` plus weaving:
    - Add `org.springframework.boot:spring-boot-starter-aop` and the `aspectjweaver` agent dependency (for development/packaging) per project needs.
    - Keep weaving scope minimal: include only application packages (and logged's packages when necessary) to reduce overhead.
    - Consider CTW via a Gradle AspectJ plugin (e.g., FreeFair/AspectJ) where the build process can weave classes and no agent is required at runtime.

Implementation notes / examples
------------------------------

1. Load-Time Weaving (developer / diagnostic)
    - Add to developer run script or `bootRun` JVM args:
        - -javaagent:/path/to/aspectjweaver.jar
    - Add `META-INF/aop.xml` to control the weaving scope (example snippet):
      ```xml
      <aspectj>
        <weaver options="-verbose -showWeaveInfo">
          <include within="com.example..*" />
          <include within="io.github.darkona.logged..*" />
        </weaver>
      </aspectj>
      ```
    - Add `spring-boot-starter-aop` to dependencies to ensure AOP support for proxying when needed.

2. Compile-Time Weaving (recommended for production if weaving needed)
    - Use a Gradle plugin to weave at compile time (e.g., FreeFair AspectJ plugin) or configure the AspectJ compiler step.
    - No -javaagent flags are required at JVM startup.
    - Ensure build artifacts are verified and performance-tested.

3. Minimal dependency changes for the archetype
    - This archetype's `build.gradle` already includes `aspectjweaver` in the `agents` configuration for staging agents (see `agents` configuration and
      `stageAgents` task).
    - Teams should decide whether to convert this agent usage into CTW for production or keep LTW for development only.

Consequences
------------
Positive:

- `logged` will be able to log private and self-invocation flows, increasing observability and traceability of critical paths.
- Developers can debug and validate logging behavior locally with minimal build changes.

Negative:

- Using LTW with `-javaagent` adds JVM-level configuration that must be managed (deployment scripts, CI shells, container startup).
- CTW requires additional build complexity (weaving step) and careful verification; build artifacts will be different from unwoven builds.
- Potential for subtle differences in timing/behavior/performance when weaving is present.

References
----------

- `logged` README and documentation (reviewed): https://github.com/Darkona/logged
- `logged` weaving guidance: use LTW or CTW per README and choose the option that best fits deployment constraints.
