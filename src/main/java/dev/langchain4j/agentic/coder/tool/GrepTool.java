package dev.langchain4j.agentic.coder.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.regex.Pattern;

public class GrepTool {

    private static final int MAX_MATCHES = 50;

    @Tool("Search for a regex pattern in file contents within a directory. "
            + "Returns matching lines with file paths and line numbers. "
            + "Useful for finding where specific code patterns, classes, or methods are used.")
    public String grep(
            @P("The regex pattern to search for") String regexPattern,
            @P(value = "The directory to search in. Defaults to working directory.", required = false) String directory,
            @P(value = "File glob filter, e.g. '*.java'. Defaults to all files.", required = false)
                    String filePattern) {
        try {
            Path dir = (directory != null && !directory.isEmpty())
                    ? Path.of(directory)
                    : Path.of(System.getProperty("user.dir"));
            if (!Files.isDirectory(dir)) {
                return "Error: Not a directory: " + dir;
            }

            Pattern pattern = Pattern.compile(regexPattern);
            PathMatcher fileMatcher = (filePattern != null && !filePattern.isEmpty())
                    ? dir.getFileSystem().getPathMatcher("glob:" + filePattern)
                    : null;

            StringBuilder sb = new StringBuilder();
            int[] matchCount = {0};

            Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (matchCount[0] >= MAX_MATCHES) {
                        return FileVisitResult.TERMINATE;
                    }
                    if (!attrs.isRegularFile() || attrs.size() > 1_000_000) {
                        return FileVisitResult.CONTINUE;
                    }
                    if (fileMatcher != null && !fileMatcher.matches(file.getFileName())) {
                        return FileVisitResult.CONTINUE;
                    }
                    try {
                        List<String> lines = Files.readAllLines(file);
                        for (int i = 0; i < lines.size() && matchCount[0] < MAX_MATCHES; i++) {
                            if (pattern.matcher(lines.get(i)).find()) {
                                sb.append(file)
                                        .append(":")
                                        .append(i + 1)
                                        .append(": ")
                                        .append(lines.get(i).trim())
                                        .append("\n");
                                matchCount[0]++;
                            }
                        }
                    } catch (IOException e) {
                        // Skip binary or unreadable files
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });

            if (matchCount[0] == 0) {
                return "No matches found for pattern '" + regexPattern + "' in " + dir;
            }

            String header = "Found " + matchCount[0];
            if (matchCount[0] >= MAX_MATCHES) {
                header += "+ (truncated)";
            }
            header += " matches:\n";
            return header + sb;
        } catch (java.util.regex.PatternSyntaxException e) {
            return "Error: Invalid regex pattern: " + e.getMessage();
        } catch (IOException e) {
            return "Error searching files: " + e.getMessage();
        }
    }
}
