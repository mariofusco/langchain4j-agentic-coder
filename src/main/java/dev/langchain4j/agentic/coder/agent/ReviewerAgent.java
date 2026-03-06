package dev.langchain4j.agentic.coder.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.coder.state.ExplorationResult;
import dev.langchain4j.agentic.coder.state.Plan;
import dev.langchain4j.agentic.coder.state.PlanScore;
import dev.langchain4j.agentic.coder.state.UserRequest;
import dev.langchain4j.agentic.declarative.K;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Reviews and scores plans produced by the PlannerAgent.
 * Returns a score between 0.0 and 1.0 and provides feedback on how to improve.
 * Used in a loop with PlannerAgent until the score reaches 0.8 or above.
 */
public interface ReviewerAgent {

    @SystemMessage(
            """
            You are a senior software architect and code review specialist. Your role is to critically \
            review implementation plans and give them a quality score between 0.0 and 1.0.

            Evaluate the plan on these criteria:
            - Completeness: Does it cover all aspects of the user request?
            - Correctness: Are the proposed changes technically sound?
            - Consistency: Does it follow existing codebase conventions and patterns?
            - Specificity: Are the changes described precisely enough to implement?
            - Test coverage: Does it include a proper test strategy?
            - Risk awareness: Does it identify potential risks and edge cases?

            Return ONLY a numeric score between 0.0 and 1.0 and nothing else.
            A score of 0.8 or above means the plan is ready for implementation.
            Below 0.8 means the plan needs improvement.
            """)
    @UserMessage(
            """
            Review and score the following implementation plan:
            {{Plan}}

            Original user request: {{UserRequest}}

            Codebase exploration findings: {{ExplorationResult}}

            Return ONLY a numeric score between 0.0 and 1.0.
            """)
    @Agent(
            description = "Reviews implementation plans and gives a quality score between 0.0 and 1.0",
            typedOutputKey = PlanScore.class)
    double review(
            @K(Plan.class) String plan,
            @K(UserRequest.class) String request,
            @K(ExplorationResult.class) String explorationResult);
}
