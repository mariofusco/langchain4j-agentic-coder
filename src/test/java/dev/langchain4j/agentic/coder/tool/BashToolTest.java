package dev.langchain4j.agentic.coder.tool;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BashToolTest {

    private final BashTool tool = new BashTool();

    @Test
    void should_execute_simple_command() {
        String result = tool.bash("echo hello", null, null);
        assertThat(result).contains("Exit code: 0").contains("hello");
    }

    @Test
    void should_capture_exit_code() {
        String result = tool.bash("exit 42", null, null);
        assertThat(result).contains("Exit code: 42");
    }

    @Test
    void should_use_working_directory(@TempDir Path tempDir) {
        String result = tool.bash("pwd", tempDir.toString(), null);
        assertThat(result).contains("Exit code: 0").contains(tempDir.toString());
    }

    @Test
    void should_handle_invalid_working_directory() {
        String result = tool.bash("echo hello", "/nonexistent/directory", null);
        assertThat(result).startsWith("Error:");
    }

    @Test
    void should_timeout_long_running_command() {
        String result = tool.bash("sleep 60", null, 1000L);
        assertThat(result).contains("timed out");
    }

    @Test
    void should_capture_stderr() {
        String result = tool.bash("echo error >&2", null, null);
        assertThat(result).contains("Exit code: 0").contains("error");
    }
}
