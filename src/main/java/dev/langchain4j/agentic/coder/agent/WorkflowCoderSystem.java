package dev.langchain4j.agentic.coder.agent;

import dev.langchain4j.agentic.coder.state.Summary;
import dev.langchain4j.agentic.coder.state.UserRequest;
import dev.langchain4j.agentic.coder.state.WorkingDirectory;
import dev.langchain4j.agentic.declarative.K;
import dev.langchain4j.agentic.declarative.SequenceAgent;

/**
 * A workflow-based coder system that uses prompt chaining (sequence pattern) to process
 * coding requests through a deterministic pipeline of agents.
 *
 * <p>The workflow is:
 * <ol>
 *   <li><b>ExplorerAgent</b> - Explores the codebase to understand structure and conventions</li>
 *   <li><b>PlanReviewLoop</b> - PlannerAgent creates a plan, ReviewerAgent scores it; loops
 *       until the plan score reaches 0.8 or above</li>
 *   <li><b>ImplementerAgent</b> - Implements the plan by writing and editing code</li>
 *   <li><b>ExecutionLoop</b> - ExecutorAgent runs builds/tests, EvaluatorAgent scores the results;
 *       if the score is below 0.8, RefactorAgent fixes the issues and the loop continues</li>
 *   <li><b>SummarizerAgent</b> - Produces a comprehensive summary of the entire workflow</li>
 * </ol>
 *
 * <p>This workflow approach provides a deterministic, predictable pipeline with built-in quality
 * gates via the review and evaluation loops.
 */
public interface WorkflowCoderSystem extends CoderSystem {

    @SequenceAgent(
            description = "A workflow-based coding pipeline: explore, plan (with review loop), "
                    + "implement, execute (with evaluation and refactoring loop), then summarize",
            typedOutputKey = Summary.class,
            subAgents = {
                ExplorerAgent.class,
                PlanReviewLoop.class,
                ImplementerAgent.class,
                ExecutionLoop.class,
                SummarizerAgent.class
            })
    @Override
    String code(@K(UserRequest.class) String request, @K(WorkingDirectory.class) String workingDirectory);


}
