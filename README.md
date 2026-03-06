# LangChain4j Agentic Coder

A multi-agent coding assistant built on the [langchain4j-agentic](../langchain4j-agentic) framework.
It orchestrates specialized AI agents to explore codebases, plan implementations, write code, run builds/tests,
and iteratively refine the results — mimicking the workflow of an AI-powered coding tool.

## Architecture

The module provides two execution models:

### Supervisor Mode

An LLM-driven supervisor dynamically decides which agents to invoke and in what order,
adapting the workflow to the user's request.

```
CoderSystem (Supervisor)
├── ExplorerAgent      — codebase exploration
├── PlannerAgent       — implementation planning
├── ImplementerAgent   — code writing/editing
└── ExecutorAgent      — build & test execution
```

### Workflow Mode

A deterministic sequence pipeline with built-in quality gates via review and evaluation loops.

```
WorkflowCoderSystem (Sequence)
│
├── 1. ExplorerAgent
│       Explores the codebase structure, finds relevant files,
│       identifies patterns and conventions.
│
├── 2. PlanReviewLoop (max 5 iterations)
│       ├── PlannerAgent   — creates/refines the implementation plan
│       └── ReviewerAgent  — scores the plan (0.0–1.0)
│           Exit when score >= 0.8
│
├── 3. ImplementerAgent
│       Writes and edits code according to the approved plan.
│
├── 4. ExecutionLoop (max 5 iterations)
│       ├── ExecutorAgent   — runs builds and tests
│       ├── EvaluatorAgent  — scores execution results (0.0–1.0)
│       │   Exit when score >= 0.8
│       └── RefactorAgent   — fixes issues if score < 0.8
│
└── 5. SummarizerAgent
        Produces a comprehensive summary of the entire workflow.
```

## Quick Start

```java
// Supervisor mode — LLM decides the agent flow
CoderSystem coder = CoderAgenticSystem.supervisorCoder(chatModel);
String result = coder.code("Add input validation to the REST API", "/path/to/project");

// Workflow mode — deterministic pipeline with quality gates
CoderSystem coder = CoderAgenticSystem.workflowCoder(chatModel);
String result = coder.code("Add input validation to the REST API", "/path/to/project");
```

### Advanced Configuration (Supervisor Mode)

```java
CoderSystem coder = CoderAgenticSystem.supervisorBuilder()
        .supervisorModel(strongModel)   // capable reasoning model for orchestration
        .agentModel(fastModel)          // faster model for sub-agents
        .maxChatMemoryMessages(100)
        .maxAgentInvocations(30)
        .listener(myAgentListener)      // observability
        .build();
```

## Agents

| Agent | Role | Tools |
|-------|------|-------|
| **ExplorerAgent** | Explores codebase structure, finds files, identifies patterns | FileRead, Glob, Grep, ListDirectory |
| **PlannerAgent** | Creates detailed, actionable implementation plans | _(none — pure LLM reasoning)_ |
| **ReviewerAgent** | Scores plans on quality criteria (0.0–1.0) | _(none — pure LLM reasoning)_ |
| **ImplementerAgent** | Writes and edits code following the plan | FileRead, FileWrite, FileEdit, Glob, Grep |
| **ExecutorAgent** | Runs shell commands to build and test | Bash, FileRead |
| **EvaluatorAgent** | Scores execution results (0.0–1.0) | _(none — pure LLM reasoning)_ |
| **RefactorAgent** | Fixes code based on build/test failures | FileRead, FileWrite, FileEdit, Glob, Grep |
| **SummarizerAgent** | Produces a final summary of the workflow | _(none — pure LLM reasoning)_ |

## Tools

| Tool | Description |
|------|-------------|
| **FileReadTool** | Reads file contents with optional line range, formatted with line numbers |
| **FileWriteTool** | Creates/overwrites files, auto-creating parent directories |
| **FileEditTool** | Precise find-and-replace editing (exact string match required) |
| **GlobTool** | Finds files by glob pattern (e.g. `**/*.java`) |
| **GrepTool** | Searches file contents by regex with optional file type filter |
| **BashTool** | Executes shell commands with configurable timeout (default 2 min) |
| **ListDirectoryTool** | Lists directory contents with types and sizes |

## State Management

Agents communicate through an `AgenticScope` using strongly-typed state keys:

| State Key | Type | Description |
|-----------|------|-------------|
| `UserRequest` | `String` | The original user coding request |
| `WorkingDirectory` | `String` | Working directory for file operations |
| `ExplorationResult` | `String` | Codebase exploration findings |
| `Plan` | `String` | The implementation plan |
| `PlanScore` | `Double` | Plan quality score (0.0–1.0) |
| `ImplementationResult` | `String` | Summary of code changes made |
| `ExecutionResult` | `String` | Build/test execution results |
| `EvaluationScore` | `Double` | Execution quality score (0.0–1.0) |
| `RefactorResult` | `String` | Summary of refactoring fixes |
| `Summary` | `String` | Final workflow summary |

## Observability

Both modes implement `MonitoredAgent`, providing access to an `AgentMonitor` for tracking agent invocations.
HTML reports can be generated for visual inspection:

```java
CoderSystem coder = CoderAgenticSystem.workflowCoder(chatModel);
String result = coder.code("Create a hello world app", "/tmp/project");

// Generate an HTML report of all agent invocations
HtmlReportGenerator.generateReport(coder.agentMonitor(), Path.of("report.html"));
```

## Example: Fixing a Buggy Calculator

The test suite includes a complete end-to-end example that demonstrates the workflow mode
fixing real bugs in a Java project. The example is in
[`CoderAgenticSystemIT.workflow_should_fix_buggy_calculator`](src/test/java/dev/langchain4j/agentic/coder/CoderAgenticSystemIT.java).

### The Buggy Project

Under [`src/test/resources/buggy-project/`](src/test/resources/buggy-project/) there is a small
Maven project containing a `Calculator.java` class with **four deliberate bugs** and a
`CalculatorTest.java` with 11 tests that expose them:

```java
// Bug 1 — Off-by-one in sum(): i <= numbers.size() causes IndexOutOfBoundsException
for (int i = 0; i <= numbers.size(); i++) {
    total += numbers.get(i);
}

// Bug 2 — Integer division in average(): truncates the result (2.5 becomes 2.0)
return sum(numbers) / numbers.size();

// Bug 3 — Wrong initial value in max(): fails for lists of all negative numbers
int max = 0;

// Bug 4 — Off-by-one in factorial(): i < n misses the last factor (factorial(5) = 24 instead of 120)
for (int i = 1; i < n; i++) {
    result *= i;
}
```

### The Test

The integration test copies the buggy project to a temp directory and invokes the workflow coder:

```java
CoderSystem coder = CoderAgenticSystem.workflowCoder(coderModel());
String result = coder.code(
        "Analyze the Calculator.java source and run its tests with 'mvn test'. "
                + "The tests are currently failing because Calculator.java has bugs. "
                + "Fix all the bugs in Calculator.java so that all tests in CalculatorTest.java pass.",
        workDir.toAbsolutePath().toString());
```

### What Happens Step by Step

The `WorkflowCoderSystem` executes its deterministic pipeline:

**1. ExplorerAgent** reads the project structure, discovers `pom.xml`, `Calculator.java`, and
`CalculatorTest.java`. It identifies the Maven project layout and the relationship between
source and test files.

**2. PlanReviewLoop** (PlannerAgent + ReviewerAgent, up to 5 iterations):
- The **PlannerAgent** reads both files, identifies the four bugs by comparing the implementation
  against the test expectations, and produces a fix plan (e.g., "change `<=` to `<` in `sum()`",
  "cast to `double` in `average()`", etc.).
- The **ReviewerAgent** scores the plan. If the plan misses a bug or is too vague, it scores
  below 0.8 and the PlannerAgent refines it. The loop exits once the plan scores >= 0.8.

**3. ImplementerAgent** applies the fixes using `FileEditTool` — precise string replacements
in `Calculator.java` for each of the four bugs.

**4. ExecutionLoop** (ExecutorAgent + EvaluatorAgent + RefactorAgent, up to 5 iterations):
- The **ExecutorAgent** runs `mvn test` via `BashTool` and captures the output.
- The **EvaluatorAgent** examines the test results and gives a score.
  If all 11 tests pass, it scores >= 0.8 and the loop exits.
- If some tests still fail (e.g., the ImplementerAgent missed a bug or introduced a regression),
  the **RefactorAgent** reads the failure output, identifies the remaining issue, and applies
  another fix. The loop then re-runs `mvn test` to verify.

**5. SummarizerAgent** produces a final report describing what was explored, what was planned,
what was changed, and the final test results.

### Expected Fixes

The agents should produce changes equivalent to:

```diff
-  for (int i = 0; i <= numbers.size(); i++) {
+  for (int i = 0; i < numbers.size(); i++) {

-  return sum(numbers) / numbers.size();
+  return (double) sum(numbers) / numbers.size();

-  int max = 0;
+  int max = numbers.get(0);

-  for (int i = 1; i < n; i++) {
+  for (int i = 1; i <= n; i++) {
```

After these fixes, all 11 tests pass: `Tests run: 11, Failures: 0, Errors: 0`.

### Observing the Workflow: HTML Report

After the test completes, it generates and HTML report like the following, showing a visual timeline of every agent invocation.

![](/src/test/resources/workflow-bugfix.png)

Here is what a real run looks like (total time: **4 min 37 s**):

```
code (Sequence, 4m 37s)
├── explore (AI, 1m 0s)
│     Discovers the project structure, reads Calculator.java and CalculatorTest.java,
│     identifies Maven layout and the four bugs.
│
├── reviewPlan (Loop, 21.3s) — 1 iteration
│   ├── plan  iter 0 (AI, 20.8s) — produces a detailed fix plan
│   └── review iter 0 (AI, 532ms) — scores 0.9 → exits immediately (>= 0.8)
│
├── implement (AI, 51.3s)
│     Applies fixes using FileEditTool. In this run, the ImplementerAgent
│     missed the sum() off-by-one bug and the average empty-list edge case.
│
├── executeAndRefine (Loop, 2m 15s) — 3 iterations
│   │
│   │ ── Iteration 0 ──
│   ├── execute  iter 0 (AI, 53.4s) — runs `mvn test`, exit code 1
│   │     Tests run: 11, Failures: 2, Errors: 3
│   │       - max_with_negative_numbers: expected <-1> but was <0>
│   │       - factorial_of_5: expected <120> but was <24>
│   │       - sum_of_empty_list: ArrayIndexOutOfBoundsException
│   │       - sum_of_positive_numbers: ArrayIndexOutOfBoundsException
│   │       - average_of_positive_numbers: ArrayIndexOutOfBoundsException
│   ├── evaluate iter 0 (AI, 567ms) — scores 0.0
│   ├── refactor iter 0 (AI, 33.7s)
│   │     Fixes: i <= to i < in sum(), max init 0 → Integer.MIN_VALUE,
│   │     factorial i < n → i <= n, average empty-list handling.
│   │
│   │ ── Iteration 1 ──
│   ├── execute  iter 1 (AI, 16.1s) — runs `mvn test`, exit code 1
│   │     Tests run: 11, Failures: 1
│   │       - average_of_empty_list: expected exception not thrown
│   ├── evaluate iter 1 (AI, 477ms) — scores 0.0
│   ├── refactor iter 1 (AI, 21.5s)
│   │     Fixes: average() now throws IllegalArgumentException for empty list.
│   │
│   │ ── Iteration 2 ──
│   ├── execute  iter 2 (AI, 9.3s) — runs `mvn test`, exit code 0
│   │     Tests run: 11, Failures: 0, Errors: 0 ✓
│   └── evaluate iter 2 (AI, 543ms) — scores 0.8 → loop exits
│
└── summarize (AI, 8.7s)
      Produces the final workflow summary with all findings and results.
```

The report's **System Topology** section shows the agent hierarchy as an interactive tree,
while the **Execution History** section provides a Gantt-chart-style timeline with expandable
input/output details for each agent invocation. This makes it easy to trace how state flows
between agents and diagnose where issues were caught and resolved by the refactoring loop.
