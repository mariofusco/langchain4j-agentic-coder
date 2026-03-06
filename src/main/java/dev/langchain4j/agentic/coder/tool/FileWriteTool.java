package dev.langchain4j.agentic.coder.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileWriteTool {

    @Tool("Write content to a file, creating the file and any parent directories if they do not exist. "
            + "Overwrites existing content if the file already exists.")
    public String writeFile(
            @P("Absolute path to the file to create or overwrite") String filePath,
            @P("The content to write to the file") String content) {
        try {
            Path path = Path.of(filePath);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, content);
            return "Successfully wrote " + content.length() + " characters to " + filePath;
        } catch (IOException e) {
            return "Error writing file: " + e.getMessage();
        }
    }
}
