package com.example.mcp.config.llm;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * Factory for creating ChatLanguageModel instances based on provider
 * This abstraction allows transparent switching between different LLM providers
 */
@Slf4j
public class LLMProviderFactory {

    /**
     * Create ChatLanguageModel from environment variables (auto-detect provider)
     */
    public static ChatLanguageModel createFromEnvironment() {
        LLMConfig config = LLMConfig.fromEnvironment();
        return create(config);
    }

    /**
     * Create ChatLanguageModel for specific provider
     */
    public static ChatLanguageModel createForProvider(LLMProvider provider) {
        LLMConfig config = LLMConfig.forProvider(provider);
        return create(config);
    }

    /**
     * Create ChatLanguageModel with custom configuration
     */
    public static ChatLanguageModel create(LLMConfig config) {
        log.info("Creating LLM client for provider: {} with model: {}",
            config.getProvider().getDisplayName(),
            config.getModelName());

        return switch (config.getProvider()) {
            case OPENAI -> createOpenAI(config);
            case GEMINI -> createGemini(config);
            case ANTHROPIC -> throw new UnsupportedOperationException(
                "Anthropic Claude support coming soon. Please use OPENAI or GEMINI for now."
            );
        };
    }

    /**
     * Create OpenAI ChatLanguageModel
     */
    private static ChatLanguageModel createOpenAI(LLMConfig config) {
        log.debug("Configuring OpenAI with model: {}", config.getModelName());

        return OpenAiChatModel.builder()
            .apiKey(config.getApiKey())
            .modelName(config.getModelName())
            .temperature(config.getTemperature())
            .timeout(Duration.ofSeconds(config.getTimeoutSeconds()))
            .maxTokens(config.getMaxTokens())
            .logRequests(false)
            .logResponses(false)
            .build();
    }

    /**
     * Create Google Gemini ChatLanguageModel
     */
    private static ChatLanguageModel createGemini(LLMConfig config) {
        log.debug("Configuring Google Gemini with model: {}", config.getModelName());

        return GoogleAiGeminiChatModel.builder()
            .apiKey(config.getApiKey())
            .modelName(config.getModelName())
            .temperature(config.getTemperature())
            .maxOutputTokens(config.getMaxTokens())
            .logRequestsAndResponses(false)
            .build();
    }

    /**
     * Get information about configured provider
     */
    public static String getProviderInfo() {
        LLMProvider provider = LLMProvider.autoDetect();
        if (provider == null) {
            return "No LLM provider configured";
        }
        return String.format("%s (model: %s)",
            provider.getDisplayName(),
            provider.getDefaultModel());
    }

    /**
     * Check if any LLM provider is configured
     */
    public static boolean isAnyProviderConfigured() {
        return LLMProvider.autoDetect() != null;
    }

    /**
     * Get configured provider
     */
    public static LLMProvider getConfiguredProvider() {
        return LLMProvider.autoDetect();
    }
}
