package dev.langchain4j.agentic.coder.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileEditTool {

    @Tool("Edit a file by replacing an exact string occurrence with a new string. "
            + "The oldString must match exactly including whitespace and indentation. "
            + "Fails if oldString is not found or is found multiple times.")
    public String editFile(
            @P("Absolute path to the file to edit") String filePath,
            @P("The exact string to find and replace") String oldString,
            @P("The new string to replace it with") String newString) {
        try {
            Path path = Path.of(filePath);
            if (!Files.exists(path)) {
                return "Error: File not found: " + filePath;
            }

            String content = Files.readString(path);

            int firstIndex = content.indexOf(oldString);
            if (firstIndex == -1) {
                return "Error: oldString not found in " + filePath;
            }

            int secondIndex = content.indexOf(oldString, firstIndex + 1);
            if (secondIndex != -1) {
                return "Error: oldString found multiple times in " + filePath
                        + ". Provide a larger context to make the match unique.";
            }

            String updated =
                    content.substring(0, firstIndex) + newString + content.substring(firstIndex + oldString.length());
            Files.writeString(path, updated);
            return "Successfully edited " + filePath;
        } catch (IOException e) {
            return "Error editing file: " + e.getMessage();
        }
    }
}
