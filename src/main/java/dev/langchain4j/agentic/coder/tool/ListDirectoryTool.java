package dev.langchain4j.agentic.coder.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ListDirectoryTool {

    @Tool("List the contents of a directory, showing files and subdirectories with their types and sizes.")
    public String listDirectory(@P("Absolute path to the directory to list") String directoryPath) {
        try {
            Path dir = Path.of(directoryPath);
            if (!Files.exists(dir)) {
                return "Error: Directory not found: " + directoryPath;
            }
            if (!Files.isDirectory(dir)) {
                return "Error: Not a directory: " + directoryPath;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Contents of ").append(directoryPath).append(":\n");

            try (Stream<Path> entries = Files.list(dir).sorted()) {
                entries.forEach(entry -> {
                    try {
                        String type = Files.isDirectory(entry) ? "[DIR] " : "[FILE]";
                        String size = Files.isDirectory(entry) ? "" : " (" + Files.size(entry) + " bytes)";
                        sb.append(String.format("  %s %s%s%n", type, entry.getFileName(), size));
                    } catch (IOException e) {
                        sb.append(String.format("  [?]   %s (error reading)%n", entry.getFileName()));
                    }
                });
            }
            return sb.toString();
        } catch (IOException e) {
            return "Error listing directory: " + e.getMessage();
        }
    }
}
