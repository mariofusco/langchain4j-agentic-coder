package dev.langchain4j.agentic.coder.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class GlobTool {

    private static final int MAX_RESULTS = 200;

    @Tool("Find files matching a glob pattern within a directory. "
            + "Returns a list of matching file paths. "
            + "Useful for discovering file structures and finding files by name pattern.")
    public String glob(
            @P("The glob pattern, e.g. '**/*.java' or 'src/**/*.xml'") String pattern,
            @P(value = "The directory to search in. Defaults to working directory.", required = false)
                    String directory) {
        try {
            Path dir = (directory != null && !directory.isEmpty())
                    ? Path.of(directory)
                    : Path.of(System.getProperty("user.dir"));
            if (!Files.isDirectory(dir)) {
                return "Error: Not a directory: " + dir;
            }

            FileSystem fs = dir.getFileSystem();
            PathMatcher matcher = fs.getPathMatcher("glob:" + pattern);
            List<String> matches = new ArrayList<>();

            Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (matches.size() >= MAX_RESULTS) {
                        return FileVisitResult.TERMINATE;
                    }
                    Path relativePath = dir.relativize(file);
                    if (matcher.matches(relativePath)) {
                        matches.add(file.toString());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });

            if (matches.isEmpty()) {
                return "No files matching pattern '" + pattern + "' found in " + dir;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Found ").append(matches.size());
            if (matches.size() >= MAX_RESULTS) {
                sb.append("+ (truncated)");
            }
            sb.append(" matching files:\n");
            for (String match : matches) {
                sb.append(match).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            return "Error searching files: " + e.getMessage();
        }
    }
}
