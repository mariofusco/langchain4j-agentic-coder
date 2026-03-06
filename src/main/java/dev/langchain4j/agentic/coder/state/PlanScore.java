package dev.langchain4j.agentic.coder.state;

import dev.langchain4j.agentic.declarative.TypedKey;

/**
 * The score (0.0 to 1.0) given by the ReviewerAgent to the plan produced by the PlannerAgent.
 */
public class PlanScore implements TypedKey<Double> {
    @Override
    public Double defaultValue() {
        return 0.0;
    }
}
