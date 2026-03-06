package dev.langchain4j.agentic.coder;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.coder.agent.CoderSystem;
import dev.langchain4j.agentic.coder.agent.ExecutorAgent;
import dev.langchain4j.agentic.coder.agent.ExplorerAgent;
import dev.langchain4j.agentic.coder.agent.ImplementerAgent;
import dev.langchain4j.agentic.coder.agent.PlannerAgent;
import dev.langchain4j.agentic.coder.agent.SupervisorCoderSystem;
import dev.langchain4j.agentic.coder.agent.WorkflowCoderSystem;
import dev.langchain4j.agentic.observability.AgentListener;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;

/**
 * Builder/factory for creating the complete multi-agent coding system.
 *
 * <p>Usage:
 * <pre>{@code
 * // Simple usage with a single model
 * CoderSystem coder = CoderAgenticSystem.create(myChatModel);
 *
 * // Advanced usage with separate models
 * CoderSystem coder = CoderAgenticSystem.builder()
 *         .supervisorModel(strongModel)
 *         .agentModel(fastModel)
 *         .maxAgentInvocations(30)
 *         .build();
 *
 * // Use it
 * String result = coder.code(
 *         "Add a REST endpoint for user registration",
 *         "/path/to/project");
 * }</pre>
 */
public class CoderAgenticSystem {

    private CoderAgenticSystem() {}

    /**
     * Creates a CoderSystem with the given chat model using default configuration.
     * The same model is used for both the supervisor and all sub-agents.
     *
     * @param chatModel the chat model to use
     * @return a configured CoderSystem instance
     */
    public static CoderSystem supervisorCoder(ChatModel chatModel) {
        return supervisorBuilder().chatModel(chatModel).build();
    }

    /**
     * Creates a CoderSystem with separate models for the supervisor and sub-agents.
     *
     * @param supervisorModel the model for the supervisor (should be a capable reasoning model)
     * @param agentModel the model for the sub-agents
     * @return a configured CoderSystem instance
     */
    public static CoderSystem supervisorCoder(ChatModel supervisorModel, ChatModel agentModel) {
        return supervisorBuilder().supervisorModel(supervisorModel).agentModel(agentModel).build();
    }

    public static SupervisorBuilder supervisorBuilder() {
        return new SupervisorBuilder();
    }

    public static class SupervisorBuilder {

        private ChatModel supervisorModel;
        private ChatModel agentModel;
        private int maxChatMemoryMessages = 50;
        private int maxAgentInvocations = 20;
        private AgentListener listener;

        public SupervisorBuilder chatModel(ChatModel chatModel) {
            this.supervisorModel = chatModel;
            this.agentModel = chatModel;
            return this;
        }

        public SupervisorBuilder supervisorModel(ChatModel supervisorModel) {
            this.supervisorModel = supervisorModel;
            return this;
        }

        public SupervisorBuilder agentModel(ChatModel agentModel) {
            this.agentModel = agentModel;
            return this;
        }

        public SupervisorBuilder maxChatMemoryMessages(int maxMessages) {
            this.maxChatMemoryMessages = maxMessages;
            return this;
        }

        public SupervisorBuilder maxAgentInvocations(int max) {
            this.maxAgentInvocations = max;
            return this;
        }

        public SupervisorBuilder listener(AgentListener listener) {
            this.listener = listener;
            return this;
        }

        public CoderSystem build() {
            if (supervisorModel == null) {
                throw new IllegalStateException("A chat model must be provided");
            }
            if (agentModel == null) {
                agentModel = supervisorModel;
            }

            ExplorerAgent explorer = AgenticServices.agentBuilder(ExplorerAgent.class)
                    .chatModel(agentModel)
                    .build();

            PlannerAgent planner = AgenticServices.agentBuilder(PlannerAgent.class)
                    .chatModel(agentModel)
                    .build();

            ImplementerAgent implementer = AgenticServices.agentBuilder(ImplementerAgent.class)
                    .chatModel(agentModel)
                    .build();

            ExecutorAgent executor = AgenticServices.agentBuilder(ExecutorAgent.class)
                    .chatModel(agentModel)
                    .build();

            var supervisorBuilder = AgenticServices.supervisorBuilder(SupervisorCoderSystem.class)
                    .chatModel(supervisorModel)
                    .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(maxChatMemoryMessages))
                    .maxAgentsInvocations(maxAgentInvocations)
                    .responseStrategy(SupervisorResponseStrategy.SUMMARY)
                    .subAgents(explorer, planner, implementer, executor);

            if (listener != null) {
                supervisorBuilder.listener(listener);
            }

            return supervisorBuilder.build();
        }
    }

    /**
     * Creates a workflow-based CoderSystem that uses a deterministic sequence pipeline
     * with plan review and execution evaluation loops.
     *
     * @param chatModel the chat model to use for all agents
     * @return a configured WorkflowCoderSystem instance
     */
    public static CoderSystem workflowCoder(ChatModel chatModel) {
        return AgenticServices.createAgenticSystem(WorkflowCoderSystem.class, chatModel);
    }
}
