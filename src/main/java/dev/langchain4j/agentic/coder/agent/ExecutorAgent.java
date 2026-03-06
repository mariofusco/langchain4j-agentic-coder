package dev.langchain4j.agentic.coder.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.coder.state.ExecutionResult;
import dev.langchain4j.agentic.coder.state.ImplementationResult;
import dev.langchain4j.agentic.coder.state.WorkingDirectory;
import dev.langchain4j.agentic.coder.tool.BashTool;
import dev.langchain4j.agentic.coder.tool.FileReadTool;
import dev.langchain4j.agentic.declarative.K;
import dev.langchain4j.agentic.declarative.ToolsSupplier;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ExecutorAgent {

    @SystemMessage(
            """
            You are a build and test execution specialist. You run shell commands to compile, \
            build, and test code changes.

            Guidelines:
            - Run builds and tests in the correct working directory: {{WorkingDirectory}}
            - Start with compilation to catch syntax errors
            - Then run relevant tests
            - If tests fail, analyze the error output
            - Read failing test files or source files to understand issues

            Provide a structured execution report including:
            - Commands executed and their exit codes
            - Build success/failure with relevant output
            - Test results (passed/failed/skipped)
            - Any error messages or stack traces from failures
            """)
    @UserMessage(
            """
            Validate the following implementation by running builds and tests:
            {{ImplementationResult}}

            Run appropriate build and test commands.
            """)
    @Agent(
            description = "Executes shell commands to build and test code changes",
            typedOutputKey = ExecutionResult.class)
    String execute(
            @K(ImplementationResult.class) String implementationResult, @K(WorkingDirectory.class) String workingDir);

    @ToolsSupplier
    static Object[] tools() {
        return new Object[] {new BashTool(), new FileReadTool()};
    }
}
