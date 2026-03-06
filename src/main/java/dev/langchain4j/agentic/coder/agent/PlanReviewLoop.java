package dev.langchain4j.agentic.coder.agent;

import dev.langchain4j.agentic.coder.state.PlanScore;
import dev.langchain4j.agentic.declarative.ExitCondition;
import dev.langchain4j.agentic.declarative.K;
import dev.langchain4j.agentic.declarative.LoopAgent;

/**
 * A loop agent that iteratively refines the implementation plan.
 * The PlannerAgent creates/updates the plan, then the ReviewerAgent scores it.
 * The loop continues until the plan score reaches 0.8 or above, or maxIterations is reached.
 */
public interface PlanReviewLoop {

    @LoopAgent(
            description = "Iteratively refine the implementation plan until it reaches sufficient quality",
            typedOutputKey = dev.langchain4j.agentic.coder.state.Plan.class,
            maxIterations = 5,
            subAgents = {PlannerAgent.class, ReviewerAgent.class})
    String reviewPlan(
            @K(dev.langchain4j.agentic.coder.state.UserRequest.class) String request,
            @K(dev.langchain4j.agentic.coder.state.ExplorationResult.class) String explorationResult);

    @ExitCondition(description = "plan score greater than or equal to 0.8")
    static boolean exit(@K(PlanScore.class) double score) {
        return score >= 0.8;
    }
}
