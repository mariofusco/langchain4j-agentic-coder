package dev.langchain4j.agentic.coder.tool;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileEditToolTest {

    private final FileEditTool tool = new FileEditTool();

    @Test
    void should_replace_unique_string(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.java");
        Files.writeString(file, "public class Foo {\n    int x = 1;\n}\n");

        String result = tool.editFile(file.toString(), "int x = 1;", "int x = 42;");

        assertThat(result).startsWith("Successfully");
        assertThat(Files.readString(file)).contains("int x = 42;");
    }

    @Test
    void should_fail_if_string_not_found(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.java");
        Files.writeString(file, "public class Foo {}\n");

        String result = tool.editFile(file.toString(), "nonexistent string", "replacement");

        assertThat(result).startsWith("Error:").contains("not found");
    }

    @Test
    void should_fail_if_string_found_multiple_times(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.java");
        Files.writeString(file, "int x = 1;\nint y = 1;\n");

        String result = tool.editFile(file.toString(), "= 1;", "= 2;");

        assertThat(result).startsWith("Error:").contains("multiple times");
    }

    @Test
    void should_fail_for_nonexistent_file() {
        String result = tool.editFile("/nonexistent/file.txt", "old", "new");
        assertThat(result).startsWith("Error:");
    }
}
