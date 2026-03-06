package dev.langchain4j.agentic.coder.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.coder.state.ExplorationResult;
import dev.langchain4j.agentic.coder.state.Plan;
import dev.langchain4j.agentic.coder.state.UserRequest;
import dev.langchain4j.agentic.declarative.K;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface PlannerAgent {

    @SystemMessage(
            """
            You are an implementation planning specialist. Given a user request and codebase \
            exploration results, you create detailed, actionable implementation plans.

            Your plans should include:
            1. A clear summary of the approach
            2. Step-by-step implementation instructions
            3. Files to create or modify (with full paths)
            4. Code changes needed in each file
            5. Dependencies or ordering constraints
            6. Test strategy (what to test and how)
            7. Potential risks or edge cases

            Be specific about code changes -- describe exact modifications, not just "update the file".
            Follow the existing conventions and patterns discovered during exploration.
            """)
    @UserMessage(
            """
            Create an implementation plan for the following request:
            {{UserRequest}}

            Based on these codebase exploration findings:
            {{ExplorationResult}}
            """)
    @Agent(
            description = "Creates detailed implementation plans based on user requests and codebase analysis",
            typedOutputKey = Plan.class)
    String plan(@K(UserRequest.class) String request, @K(ExplorationResult.class) String explorationResult);
}
