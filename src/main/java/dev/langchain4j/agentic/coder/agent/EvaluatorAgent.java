package dev.langchain4j.agentic.coder.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.coder.state.EvaluationScore;
import dev.langchain4j.agentic.coder.state.ExecutionResult;
import dev.langchain4j.agentic.coder.state.ImplementationResult;
import dev.langchain4j.agentic.coder.state.Plan;
import dev.langchain4j.agentic.coder.state.UserRequest;
import dev.langchain4j.agentic.declarative.K;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Evaluates the output of the ExecutorAgent, giving a quality score between 0.0 and 1.0.
 * If the score is below 0.8, the loop continues to the RefactorAgent which reads the
 * execution results to fix the issues. If the score is 0.8 or above, the loop terminates.
 */
public interface EvaluatorAgent {

    @SystemMessage(
            """
            You are a quality assurance specialist. Your role is to evaluate the results of code \
            execution (builds and tests) and give a quality score between 0.0 and 1.0.

            Evaluate the execution results on these criteria:
            - Build success: Did the code compile without errors?
            - Test results: Did all tests pass?
            - Correctness: Does the implementation match the original request and plan?
            - Code quality: Are there any warnings, deprecations, or code smells in the output?

            Return ONLY a numeric score between 0.0 and 1.0 and nothing else.
            A score of 1.0 means everything compiled and all tests passed perfectly.
            A score of 0.8 or above means the implementation is acceptable.
            Below 0.8 means there are issues that need to be fixed.
            """)
    @UserMessage(
            """
            Evaluate the following execution results:
            {{ExecutionResult}}

            Implementation summary: {{ImplementationResult}}
            Original plan: {{Plan}}
            Original request: {{UserRequest}}

            Return ONLY a numeric score between 0.0 and 1.0.
            """)
    @Agent(
            description =
                    "Evaluates execution results and gives a quality score between 0.0 and 1.0",
            typedOutputKey = EvaluationScore.class)
    double evaluate(
            @K(ExecutionResult.class) String executionResult,
            @K(ImplementationResult.class) String implementationResult,
            @K(Plan.class) String plan,
            @K(UserRequest.class) String request);
}
