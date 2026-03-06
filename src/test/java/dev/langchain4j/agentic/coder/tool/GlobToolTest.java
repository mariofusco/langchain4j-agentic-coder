package dev.langchain4j.agentic.coder.tool;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GlobToolTest {

    private final GlobTool tool = new GlobTool();

    @Test
    void should_find_files_matching_pattern(@TempDir Path tempDir) throws Exception {
        Files.createDirectories(tempDir.resolve("src/main"));
        Files.writeString(tempDir.resolve("src/main/Foo.java"), "class Foo {}");
        Files.writeString(tempDir.resolve("src/main/Bar.java"), "class Bar {}");
        Files.writeString(tempDir.resolve("readme.md"), "# readme");

        String result = tool.glob("**/*.java", tempDir.toString());

        assertThat(result).contains("Foo.java").contains("Bar.java");
        assertThat(result).doesNotContain("readme.md");
    }

    @Test
    void should_return_no_matches(@TempDir Path tempDir) throws Exception {
        Files.writeString(tempDir.resolve("readme.md"), "# readme");

        String result = tool.glob("**/*.java", tempDir.toString());

        assertThat(result).contains("No files matching");
    }

    @Test
    void should_handle_invalid_directory() {
        String result = tool.glob("**/*.java", "/nonexistent/directory");
        assertThat(result).startsWith("Error:");
    }
}
