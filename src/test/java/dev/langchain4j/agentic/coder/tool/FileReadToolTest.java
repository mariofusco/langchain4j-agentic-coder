package dev.langchain4j.agentic.coder.tool;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileReadToolTest {

    private final FileReadTool tool = new FileReadTool();

    @Test
    void should_read_entire_file(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "line1\nline2\nline3\n");

        String result = tool.readFile(file.toString(), null, null);

        assertThat(result).contains("1\tline1").contains("2\tline2").contains("3\tline3");
    }

    @Test
    void should_read_line_range(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "line1\nline2\nline3\nline4\nline5\n");

        String result = tool.readFile(file.toString(), 2, 4);

        assertThat(result).contains("line2").contains("line3").contains("line4");
        assertThat(result).doesNotContain("line1").doesNotContain("line5");
    }

    @Test
    void should_return_error_for_nonexistent_file() {
        String result = tool.readFile("/nonexistent/file.txt", null, null);
        assertThat(result).startsWith("Error:");
    }

    @Test
    void should_return_error_for_directory(@TempDir Path tempDir) {
        String result = tool.readFile(tempDir.toString(), null, null);
        assertThat(result).startsWith("Error:");
    }

    @Test
    void should_return_error_for_start_beyond_file(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "line1\n");

        String result = tool.readFile(file.toString(), 100, null);
        assertThat(result).startsWith("Error:");
    }
}
