package dev.langchain4j.agentic.coder.tool;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ListDirectoryToolTest {

    private final ListDirectoryTool tool = new ListDirectoryTool();

    @Test
    void should_list_files_and_directories(@TempDir Path tempDir) throws Exception {
        Files.writeString(tempDir.resolve("file.txt"), "content");
        Files.createDirectory(tempDir.resolve("subdir"));

        String result = tool.listDirectory(tempDir.toString());

        assertThat(result).contains("[FILE]").contains("file.txt");
        assertThat(result).contains("[DIR]").contains("subdir");
    }

    @Test
    void should_handle_empty_directory(@TempDir Path tempDir) {
        String result = tool.listDirectory(tempDir.toString());
        assertThat(result).contains("Contents of");
    }

    @Test
    void should_handle_nonexistent_directory() {
        String result = tool.listDirectory("/nonexistent/directory");
        assertThat(result).startsWith("Error:");
    }

    @Test
    void should_handle_file_path(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("file.txt");
        Files.writeString(file, "content");

        String result = tool.listDirectory(file.toString());
        assertThat(result).startsWith("Error:").contains("Not a directory");
    }
}
