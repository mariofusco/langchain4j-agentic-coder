package dev.langchain4j.agentic.coder.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.coder.state.EvaluationScore;
import dev.langchain4j.agentic.coder.state.ExecutionResult;
import dev.langchain4j.agentic.coder.state.ExplorationResult;
import dev.langchain4j.agentic.coder.state.ImplementationResult;
import dev.langchain4j.agentic.coder.state.Plan;
import dev.langchain4j.agentic.coder.state.Summary;
import dev.langchain4j.agentic.coder.state.UserRequest;
import dev.langchain4j.agentic.declarative.K;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Produces a comprehensive summary of the entire coding workflow: what was explored,
 * the plan that was created, the actions taken during implementation, and the final
 * evaluation results.
 */
public interface SummarizerAgent {

    @SystemMessage(
            """
            You are a technical report writer. Your role is to produce a clear, concise summary \
            of a coding workflow that was just completed.

            Your summary should include:
            1. **Request**: What the user originally asked for
            2. **Exploration**: Key findings from the codebase exploration
            3. **Plan**: The approach that was chosen and why
            4. **Implementation**: What files were created or modified and the changes made
            5. **Execution & Evaluation**: Build/test results and final quality assessment

            Keep the summary focused and actionable. Highlight any important decisions that were \
            made, any issues that were encountered and resolved, and any remaining concerns.
            """)
    @UserMessage(
            """
            Produce a summary of the following coding workflow:

            Original request: {{UserRequest}}

            Exploration results: {{ExplorationResult}}

            Implementation plan: {{Plan}}

            Implementation actions: {{ImplementationResult}}

            Execution results: {{ExecutionResult}}

            Final evaluation score: {{EvaluationScore}}
            """)
    @Agent(
            description = "Produces a comprehensive summary of the entire coding workflow",
            typedOutputKey = Summary.class)
    String summarize(
            @K(UserRequest.class) String request,
            @K(ExplorationResult.class) String explorationResult,
            @K(Plan.class) String plan,
            @K(ImplementationResult.class) String implementationResult,
            @K(ExecutionResult.class) String executionResult,
            @K(EvaluationScore.class) double evaluationScore);
}
