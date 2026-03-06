package dev.langchain4j.agentic.coder.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.coder.state.UserRequest;
import dev.langchain4j.agentic.coder.state.WorkingDirectory;
import dev.langchain4j.agentic.declarative.K;
import dev.langchain4j.agentic.declarative.SupervisorRequest;
import dev.langchain4j.agentic.observability.MonitoredAgent;

/**
 * Top-level supervisor agent that orchestrates the multi-agent coding system.
 * The supervisor decides which sub-agent(s) to invoke based on the user's request.
 *
 * <p>Flow examples:
 * <ul>
 *   <li>Simple question: Explorer only</li>
 *   <li>Code change request: Explorer -&gt; Planner -&gt; Implementer -&gt; Executor</li>
 *   <li>Build/test request: Executor only</li>
 *   <li>Planning request: Explorer -&gt; Planner</li>
 * </ul>
 */
public interface SupervisorCoderSystem extends CoderSystem {

    @SupervisorRequest
    static String request(@K(UserRequest.class) String userRequest, @K(WorkingDirectory.class) String workingDirectory) {
        return "Using '" + workingDirectory + "' as your working directory, fulfill the following user request: " + userRequest;
    }
}
