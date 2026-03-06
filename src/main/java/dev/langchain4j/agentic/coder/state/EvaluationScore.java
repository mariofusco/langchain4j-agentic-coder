package dev.langchain4j.agentic.coder.state;

import dev.langchain4j.agentic.declarative.TypedKey;

/**
 * The score (0.0 to 1.0) given by the EvaluatorAgent to the execution result.
 */
public class EvaluationScore implements TypedKey<Double> {
    @Override
    public Double defaultValue() {
        return 0.0;
    }
}
