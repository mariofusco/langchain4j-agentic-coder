package dev.langchain4j.agentic.coder.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.coder.state.ImplementationResult;
import dev.langchain4j.agentic.coder.state.Plan;
import dev.langchain4j.agentic.coder.state.UserRequest;
import dev.langchain4j.agentic.coder.state.WorkingDirectory;
import dev.langchain4j.agentic.coder.tool.FileEditTool;
import dev.langchain4j.agentic.coder.tool.FileReadTool;
import dev.langchain4j.agentic.coder.tool.FileWriteTool;
import dev.langchain4j.agentic.coder.tool.GlobTool;
import dev.langchain4j.agentic.coder.tool.GrepTool;
import dev.langchain4j.agentic.declarative.K;
import dev.langchain4j.agentic.declarative.ToolsSupplier;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ImplementerAgent {

    @SystemMessage(
            """
            You are a code implementation specialist. You write and edit code following the plan \
            provided. You have access to tools for reading, writing, and editing files, as well \
            as searching the codebase.

            Guidelines:
            - Follow the plan step by step
            - Read existing files before editing to ensure correct context
            - Use editFile for precise modifications to existing files
            - Use writeFile for creating new files
            - Follow existing code style and conventions
            - Work within the provided working directory: {{WorkingDirectory}}

            After completing all changes, provide a summary of:
            - Files created (with full paths)
            - Files modified (with description of changes)
            - Any deviations from the plan and why
            """)
    @UserMessage(
            """
            Implement the following plan:
            {{Plan}}

            Original request: {{UserRequest}}
            """)
    @Agent(
            description = "Implements code changes by writing and editing files according to a plan",
            typedOutputKey = ImplementationResult.class)
    String implement(
            @K(Plan.class) String plan,
            @K(UserRequest.class) String request,
            @K(WorkingDirectory.class) String workingDir);

    @ToolsSupplier
    static Object[] tools() {
        return new Object[] {new FileReadTool(), new FileWriteTool(), new FileEditTool(), new GlobTool(), new GrepTool()
        };
    }
}
