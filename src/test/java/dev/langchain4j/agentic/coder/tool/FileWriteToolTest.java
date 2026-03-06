package dev.langchain4j.agentic.coder.tool;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileWriteToolTest {

    private final FileWriteTool tool = new FileWriteTool();

    @Test
    void should_write_new_file(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("new.txt");

        String result = tool.writeFile(file.toString(), "hello world");

        assertThat(result).startsWith("Successfully");
        assertThat(Files.readString(file)).isEqualTo("hello world");
    }

    @Test
    void should_create_parent_directories(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("a/b/c/new.txt");

        String result = tool.writeFile(file.toString(), "nested content");

        assertThat(result).startsWith("Successfully");
        assertThat(Files.readString(file)).isEqualTo("nested content");
    }

    @Test
    void should_overwrite_existing_file(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("existing.txt");
        Files.writeString(file, "old content");

        String result = tool.writeFile(file.toString(), "new content");

        assertThat(result).startsWith("Successfully");
        assertThat(Files.readString(file)).isEqualTo("new content");
    }
}
