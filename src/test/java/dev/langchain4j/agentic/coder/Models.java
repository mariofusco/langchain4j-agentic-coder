package dev.langchain4j.agentic.coder;

import java.time.Duration;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

public class Models {

    public enum MODEL_PROVIDER {
        OPENAI,
        OLLAMA
    }

    public static final MODEL_PROVIDER modelProvider = MODEL_PROVIDER.OPENAI;

    private static final String OLLAMA_DEFAULT_URL = "http://127.0.0.1:11434";
    private static final String OLLAMA_ENV_URL = System.getenv("OLLAMA_BASE_URL");

    private static final String OLLAMA_BASE_URL = OLLAMA_ENV_URL != null ? OLLAMA_ENV_URL : OLLAMA_DEFAULT_URL;

    private static final ChatModel OPENAI_BASE_MODEL = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .organizationId(System.getenv("OPENAI_ORGANIZATION_ID"))
            .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                        .temperature(0.0)
                        .logRequests(true)
                        .logResponses(true)
                        .build();

    private static final ChatModel OPENAI_PLANNER_MODEL = OPENAI_BASE_MODEL;
    private static final ChatModel OPENAI_CODER_MODEL = OPENAI_BASE_MODEL;

    private static final StreamingChatModel OPENAI_STREAMING_BASE_MODEL = OpenAiStreamingChatModel.builder()
            .baseUrl(System.getenv("OPENAI_BASE_URL"))
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .organizationId(System.getenv("OPENAI_ORGANIZATION_ID"))
            .modelName(OpenAiChatModelName.GPT_4_O_MINI)
            .temperature(0.0)
            .logRequests(true)
            .build();

    private static final ChatModel OLLAMA_BASE_MODEL = OllamaChatModel.builder()
            .baseUrl(OLLAMA_BASE_URL)
            .modelName("qwen2.5:7b")
            .timeout(Duration.ofMinutes(10))
            .temperature(0.0)
            .logRequests(true)
            .logResponses(true)
            .build();

    private static final ChatModel OLLAMA_CODER_MODEL = OllamaChatModel.builder()
            .baseUrl(OLLAMA_BASE_URL)
//            .modelName("qwen2.5-coder:7b")
//            .modelName("qwen3:8b")
            .modelName("qwen3.5:4b")
            .timeout(Duration.ofMinutes(15))
            .temperature(0.0)
            .logRequests(true)
            .logResponses(true)
            .build();

    private static final ChatModel OLLAMA_PLANNER_MODEL = OllamaChatModel.builder()
            .baseUrl(OLLAMA_BASE_URL)
            .modelName("qwen3:8b")
            .timeout(Duration.ofMinutes(10))
            .temperature(0.0)
            .logRequests(true)
            .logResponses(true)
            .think(false)
            .build();

    private static final StreamingChatModel OLLAMA_STREAMING_BASE_MODEL = OllamaStreamingChatModel.builder()
            .baseUrl(OLLAMA_BASE_URL)
            .modelName("qwen2.5:7b")
            .timeout(Duration.ofMinutes(10))
            .temperature(0.0)
            .logRequests(true)
            .think(false)
            .build();

    private static final ChatModel OLLAMA_VISION_MODEL = OllamaChatModel.builder()
            .baseUrl(OLLAMA_BASE_URL)
            .modelName("qwen3-vl:8b")
            .timeout(Duration.ofMinutes(10))
            .temperature(0.0)
            .logRequests(true)
            .logResponses(false)
            .think(false)
            .build();

    public static ChatModel baseModel() {
        return baseModel(modelProvider);
    }

    public static ChatModel baseModel(MODEL_PROVIDER modelProvider) {
        return switch (modelProvider) {
            case OPENAI -> OPENAI_BASE_MODEL;
            case OLLAMA -> OLLAMA_BASE_MODEL;
        };
    }

    public static ChatModel plannerModel() {
        return plannerModel(modelProvider);
    }

    public static ChatModel plannerModel(MODEL_PROVIDER modelProvider) {
        return switch (modelProvider) {
            case OPENAI -> OPENAI_PLANNER_MODEL;
            case OLLAMA -> OLLAMA_PLANNER_MODEL;
        };
    }

    public static ChatModel coderModel() {
        return coderModel(modelProvider);
    }

    public static ChatModel coderModel(MODEL_PROVIDER modelProvider) {
        return switch (modelProvider) {
            case OPENAI -> OPENAI_CODER_MODEL;
            case OLLAMA -> OLLAMA_CODER_MODEL;
        };
    }

    public static StreamingChatModel streamingBaseModel() {
        return streamingBaseModel(modelProvider);
    }

    public static StreamingChatModel streamingBaseModel(MODEL_PROVIDER modelProvider) {
        return switch (modelProvider) {
            case OPENAI -> OPENAI_STREAMING_BASE_MODEL;
            case OLLAMA -> OLLAMA_STREAMING_BASE_MODEL;
        };
    }

    public static ChatModel visionModel() {
        return visionModel(modelProvider);
    }

    public static ChatModel visionModel(MODEL_PROVIDER modelProvider) {
        return switch (modelProvider) {
            case OPENAI -> OPENAI_BASE_MODEL;
            case OLLAMA -> OLLAMA_VISION_MODEL;
        };
    }

    public static ChatModel throwingModel() {
        return new ChatModel() {
            @Override
            public ChatResponse chat(final ChatRequest chatRequest) {
                throw new RuntimeException("ERROR");
            }
        };
    }
}
