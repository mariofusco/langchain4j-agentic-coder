package dev.langchain4j.agentic.coder.state;

import dev.langchain4j.agentic.declarative.TypedKey;

/**
 * The working directory for file operations.
 */
public class WorkingDirectory implements TypedKey<String> {

    @Override
    public String defaultValue() {
        return System.getProperty("user.dir");
    }
}
