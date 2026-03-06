package dev.langchain4j.agentic.coder.tool;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GrepToolTest {

    private final GrepTool tool = new GrepTool();

    @Test
    void should_find_matching_lines(@TempDir Path tempDir) throws Exception {
        Files.writeString(tempDir.resolve("Foo.java"), "public class Foo {\n    int value = 42;\n}\n");
        Files.writeString(tempDir.resolve("Bar.java"), "public class Bar {\n    String name;\n}\n");

        String result = tool.grep("class\\s+\\w+", tempDir.toString(), null);

        assertThat(result).contains("class Foo").contains("class Bar");
    }

    @Test
    void should_filter_by_file_pattern(@TempDir Path tempDir) throws Exception {
        Files.writeString(tempDir.resolve("Foo.java"), "class Foo {}");
        Files.writeString(tempDir.resolve("readme.md"), "class NotAClass");

        String result = tool.grep("class", tempDir.toString(), "*.java");

        assertThat(result).contains("Foo.java").doesNotContain("readme.md");
    }

    @Test
    void should_return_no_matches(@TempDir Path tempDir) throws Exception {
        Files.writeString(tempDir.resolve("Foo.java"), "class Foo {}");

        String result = tool.grep("nonexistent_pattern_xyz", tempDir.toString(), null);

        assertThat(result).contains("No matches");
    }

    @Test
    void should_handle_invalid_regex() {
        String result = tool.grep("[invalid", "/tmp", null);
        assertThat(result).startsWith("Error:").contains("Invalid regex");
    }
}
