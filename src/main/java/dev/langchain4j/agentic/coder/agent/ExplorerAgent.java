package dev.langchain4j.agentic.coder.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.coder.state.ExplorationResult;
import dev.langchain4j.agentic.coder.state.UserRequest;
import dev.langchain4j.agentic.coder.state.WorkingDirectory;
import dev.langchain4j.agentic.coder.tool.FileReadTool;
import dev.langchain4j.agentic.coder.tool.GlobTool;
import dev.langchain4j.agentic.coder.tool.GrepTool;
import dev.langchain4j.agentic.coder.tool.ListDirectoryTool;
import dev.langchain4j.agentic.declarative.K;
import dev.langchain4j.agentic.declarative.ToolsSupplier;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ExplorerAgent {

    @SystemMessage(
            """
            You are a codebase exploration specialist. Your job is to understand the structure \
            and contents of a codebase by reading files, searching for patterns, and listing directories.

            When exploring, be thorough but efficient:
            - Start by listing the top-level directory to understand project structure
            - Use glob patterns to find relevant files (e.g., **/*.java, **/pom.xml)
            - Use grep to search for specific classes, methods, or patterns
            - Read key files to understand architecture and conventions

            Always work within the provided working directory: {{WorkingDirectory}}

            Produce a clear, structured summary of your findings including:
            - Project structure overview
            - Key files and their purposes
            - Relevant code patterns and conventions
            - Any existing implementations similar to what is being requested
            """)
    @UserMessage(
            """
            Explore the codebase to understand the context needed for this request:
            {{UserRequest}}

            Provide a structured exploration summary.
            """)
    @Agent(
            description = "Explores and analyzes a codebase using file reading, glob search, and grep tools",
            typedOutputKey = ExplorationResult.class)
    String explore(@K(UserRequest.class) String request, @K(WorkingDirectory.class) String workingDir);

    @ToolsSupplier
    static Object[] tools() {
        return new Object[] {new FileReadTool(), new GlobTool(), new GrepTool(), new ListDirectoryTool()};
    }
}
