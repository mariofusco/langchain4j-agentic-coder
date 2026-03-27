package dev.langchain4j.agentic.coder;

import static dev.langchain4j.agentic.coder.Models.coderModel;
import static dev.langchain4j.agentic.coder.Models.plannerModel;
import static dev.langchain4j.agentic.observability.HtmlReportGenerator.generateReport;
import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.agentic.coder.agent.CoderSystem;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class CoderAgenticSystemIT {

    @Test
    void should_explore_codebase(@TempDir Path tempDir) throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java"));
        Files.writeString(
                tempDir.resolve("src/main/java/Hello.java"),
                "public class Hello {\n    public static void main(String[] args) {\n"
                        + "        System.out.println(\"Hello World\");\n    }\n}\n");
        Files.writeString(tempDir.resolve("pom.xml"), "<project><modelVersion>4.0.0</modelVersion></project>");

        CoderSystem coder = CoderAgenticSystem.supervisorCoder(plannerModel(), coderModel());
        String result = coder.code(
                "List all Java files and describe the project structure",
                tempDir.toAbsolutePath().toString());

        assertThat(result).isNotBlank();

        System.out.println("Working dir: " + tempDir.toAbsolutePath());
        System.out.println("Result: " + result);
    }

    @Test
    void supervisor_coder_file_generation() {
        Path tempDir = Path.of("/tmp/coder");
        CoderSystem coder = CoderAgenticSystem.supervisorCoder(plannerModel(Models.MODEL_PROVIDER.OPENAI), coderModel());
        String result = coder.code(
                "Create a Java file named HelloWorld.java with a main method that prints 'Hello World'",
                tempDir.toAbsolutePath().toString());

        assertThat(result).isNotBlank();

        System.out.println("Working dir: " + tempDir.toAbsolutePath());
        System.out.println("Result: " + result);

        generateReport(coder.agentMonitor(), Path.of("src", "test", "resources", "coder.html"));
    }

    @Test
    void workflow_coder_file_generation() {
        Path tempDir = Path.of("/tmp/coder-workflow");
        CoderSystem coder = CoderAgenticSystem.workflowCoder(coderModel());
        String result = coder.code(
                "Create a Java file named HelloWorld.java with a main method that prints 'Hello World'",
                tempDir.toAbsolutePath().toString());

        assertThat(result).isNotBlank();

        System.out.println("Working dir: " + tempDir.toAbsolutePath());
        System.out.println("Result: " + result);

        generateReport(coder.agentMonitor(), Path.of("src", "test", "resources", "workflow-coder.html"));
    }

    @Test
    void supervisor_should_fix_buggy_calculator() throws Exception {
        should_fix_buggy_calculator(CoderAgenticSystem.supervisorCoder(coderModel()));
    }

    @Test
    void workflow_should_fix_buggy_calculator() throws Exception {
        should_fix_buggy_calculator(CoderAgenticSystem.workflowCoder(coderModel()));
    }

    private void should_fix_buggy_calculator(CoderSystem coder) throws Exception {
        // Copy the buggy project to a temp directory so the agent can modify files freely
        Path source = Path.of("src/test/resources/buggy-project");
        Path workDir = Path.of("/tmp/buggy-calculator");
        copyDirectory(source, workDir);

        String result = coder.code(
                "Analyze the Calculator.java source and run its tests with 'mvn test'. "
                        + "The tests are currently failing because Calculator.java has bugs. "
                        + "Fix all the bugs in Calculator.java so that all tests in CalculatorTest.java pass.",
                workDir.toAbsolutePath().toString());

        assertThat(result).isNotBlank();

        System.out.println("Working dir: " + workDir.toAbsolutePath());
        System.out.println("Result: " + result);

        generateReport(coder.agentMonitor(), Path.of("src", "test", "resources", "bugfix.html"));
    }

    private static void copyDirectory(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
