package dev.langchain4j.agentic.coder.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileReadTool {

    @Tool("Read the contents of a file at the given path. Returns the file contents with line numbers. "
            + "Optionally specify startLine and endLine to read a specific range.")
    public String readFile(
            @P("Absolute path to the file to read") String filePath,
            @P(value = "Starting line number (1-based, inclusive)", required = false) Integer startLine,
            @P(value = "Ending line number (1-based, inclusive)", required = false) Integer endLine) {
        try {
            Path path = Path.of(filePath);
            if (!Files.exists(path)) {
                return "Error: File not found: " + filePath;
            }
            if (Files.isDirectory(path)) {
                return "Error: Path is a directory, not a file: " + filePath;
            }

            List<String> allLines = Files.readAllLines(path);
            int start = (startLine != null && startLine > 0) ? startLine - 1 : 0;
            int end = (endLine != null && endLine > 0) ? Math.min(endLine, allLines.size()) : allLines.size();

            if (start >= allLines.size()) {
                return "Error: startLine " + (start + 1) + " exceeds file length of " + allLines.size() + " lines.";
            }

            StringBuilder sb = new StringBuilder();
            for (int i = start; i < end; i++) {
                sb.append(String.format("%6d\t%s%n", i + 1, allLines.get(i)));
            }
            return sb.toString();
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }
}
