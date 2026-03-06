package dev.langchain4j.agentic.coder.agent;

import dev.langchain4j.agentic.coder.state.EvaluationScore;
import dev.langchain4j.agentic.coder.state.ExecutionResult;
import dev.langchain4j.agentic.coder.state.ImplementationResult;
import dev.langchain4j.agentic.coder.state.WorkingDirectory;
import dev.langchain4j.agentic.declarative.ExitCondition;
import dev.langchain4j.agentic.declarative.K;
import dev.langchain4j.agentic.declarative.LoopAgent;

/**
 * A loop agent that iteratively executes, evaluates, and refactors code.
 *
 * <p>Each iteration:
 * <ol>
 *   <li>ExecutorAgent runs builds and tests</li>
 *   <li>EvaluatorAgent scores the execution results (0.0 to 1.0)</li>
 *   <li>If the score is &gt;= 0.8, the loop exits</li>
 *   <li>Otherwise, RefactorAgent fixes the issues and the loop continues</li>
 * </ol>
 */
public interface ExecutionLoop {

    @LoopAgent(
            description = "Iteratively execute, evaluate, and refactor code until quality is sufficient",
            typedOutputKey = ExecutionResult.class,
            maxIterations = 5,
            subAgents = {ExecutorAgent.class, EvaluatorAgent.class, RefactorAgent.class})
    String executeAndRefine(
            @K(ImplementationResult.class) String implementationResult,
            @K(WorkingDirectory.class) String workingDir);

    @ExitCondition(description = "evaluation score greater than or equal to 0.8")
    static boolean exit(@K(EvaluationScore.class) double score) {
        return score >= 0.8;
    }
}
