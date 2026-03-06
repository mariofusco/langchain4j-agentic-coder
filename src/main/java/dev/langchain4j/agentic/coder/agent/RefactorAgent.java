package dev.langchain4j.agentic.coder.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.coder.state.ExecutionResult;
import dev.langchain4j.agentic.coder.state.ImplementationResult;
import dev.langchain4j.agentic.coder.state.Plan;
import dev.langchain4j.agentic.coder.state.RefactorResult;
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

/**
 * Refactors and fixes code based on the execution results from the ExecutorAgent.
 * Has the same tools as the ImplementerAgent to read, write, and edit files.
 * Its goal is to fix the issues identified during build and test execution.
 */
public interface RefactorAgent {

    @SystemMessage(
            """
            You are a code refactoring and bug-fixing specialist. Your role is to analyze execution \
            failures and quality issues, then fix the code to resolve them.

            You have access to tools for reading, writing, and editing files, as well as \
            searching the codebase.

            Guidelines:
            - Carefully analyze the execution results to understand what went wrong
            - Read the relevant source files before making changes
            - Use editFile for precise modifications to existing files
            - Use writeFile only if new files need to be created
            - Fix compilation errors first, then test failures
            - Follow existing code style and conventions
            - Work within the provided working directory: {{WorkingDirectory}}

            After completing all fixes, provide a summary of:
            - Files modified (with description of changes)
            - Issues fixed
            - Any remaining concerns
            """)
    @UserMessage(
            """
            The following execution had issues that need to be fixed:

            Execution results: {{ExecutionResult}}

            Previous implementation summary: {{ImplementationResult}}
            Original plan: {{Plan}}

            Analyze the errors and fix the issues in the code.
            """)
    @Agent(
            description = "Refactors and fixes code based on execution failures",
            typedOutputKey = RefactorResult.class)
    String refactor(
            @K(ExecutionResult.class) String executionResult,
            @K(ImplementationResult.class) String implementationResult,
            @K(Plan.class) String plan,
            @K(WorkingDirectory.class) String workingDir);

    @ToolsSupplier
    static Object[] tools() {
        return new Object[] {new FileReadTool(), new FileWriteTool(), new FileEditTool(), new GlobTool(), new GrepTool()
        };
    }
}
