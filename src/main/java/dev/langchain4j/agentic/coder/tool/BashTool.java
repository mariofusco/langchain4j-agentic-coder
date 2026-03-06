package dev.langchain4j.agentic.coder.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class BashTool {

    private final long defaultTimeoutMillis;

    public BashTool() {
        this(120_000);
    }

    public BashTool(long defaultTimeoutMillis) {
        this.defaultTimeoutMillis = defaultTimeoutMillis;
    }

    @Tool("Execute a bash command and return its stdout and stderr. "
            + "Use for running builds, tests, git operations, and other shell commands.")
    public String bash(
            @P("The bash command to execute") String command,
            @P(value = "Working directory for the command. Defaults to current directory.", required = false)
                    String workingDirectory,
            @P(value = "Timeout in milliseconds. Defaults to 120000.", required = false) Long timeoutMillis) {
        try {
            long timeout = (timeoutMillis != null && timeoutMillis > 0) ? timeoutMillis : defaultTimeoutMillis;

            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);
            if (workingDirectory != null && !workingDirectory.isEmpty()) {
                File dir = new File(workingDirectory);
                if (!dir.isDirectory()) {
                    return "Error: Working directory does not exist: " + workingDirectory;
                }
                pb.directory(dir);
            }
            pb.redirectErrorStream(true);

            Process process = pb.start();

            boolean completed = process.waitFor(timeout, TimeUnit.MILLISECONDS);
            if (!completed) {
                process.destroyForcibly();
                process.waitFor(5, TimeUnit.SECONDS);
                String partial = readAvailable(process.getInputStream());
                return "Error: Command timed out after " + timeout + "ms.\nPartial output:\n" + partial;
            }

            String output = new String(process.getInputStream().readAllBytes());
            int exitCode = process.exitValue();
            StringBuilder sb = new StringBuilder();
            sb.append("Exit code: ").append(exitCode).append("\n");
            if (!output.isEmpty()) {
                sb.append("Output:\n").append(output);
            }
            return sb.toString();
        } catch (IOException e) {
            return "Error executing command: " + e.getMessage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error: Command execution interrupted.";
        }
    }

    private static String readAvailable(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            while (is.available() > 0) {
                int read = is.read(buffer, 0, Math.min(buffer.length, is.available()));
                if (read <= 0) break;
                baos.write(buffer, 0, read);
            }
            return baos.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
