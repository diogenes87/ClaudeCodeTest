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

    // List of Gemini models to try (in order of preference for free tier)
    private static final String[] GEMINI_MODELS_TO_TRY = {
        "gemini-1.5-flash-8b",      // Free tier optimized
        "gemini-1.5-flash",          // Standard free tier
        "gemini-pro",                // Legacy but stable
        "models/gemini-1.5-flash",   // With models/ prefix
        "models/gemini-pro"          // Legacy with prefix
    };

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
     * Create Google Gemini ChatLanguageModel with fallback model support
     */
    private static ChatLanguageModel createGemini(LLMConfig config) {
        String requestedModel = config.getModelName();
        log.debug("Configuring Google Gemini with model: {}", requestedModel);

        // Try the requested model first
        ChatLanguageModel model = tryCreateGeminiModel(config.getApiKey(), requestedModel, config);
        if (model != null) {
            return model;
        }

        // If requested model fails, try fallback models
        log.warn("Requested Gemini model '{}' not available, trying fallback models...", requestedModel);

        for (String fallbackModel : GEMINI_MODELS_TO_TRY) {
            if (fallbackModel.equals(requestedModel)) {
                continue; // Skip already tried model
            }

            log.info("Trying Gemini model: {}", fallbackModel);
            model = tryCreateGeminiModel(config.getApiKey(), fallbackModel, config);
            if (model != null) {
                log.info("Successfully connected using Gemini model: {}", fallbackModel);
                return model;
            }
        }

        throw new IllegalStateException(
            "Could not connect to any Gemini model. Please check:\n" +
            "1. Your API key is valid (get from https://aistudio.google.com/app/apikey)\n" +
            "2. Gemini API is enabled for your project\n" +
            "3. Your region has access to Gemini models\n\n" +
            "Tried models: " + String.join(", ", GEMINI_MODELS_TO_TRY)
        );
    }

    /**
     * Try to create Gemini model, return null if fails
     */
    private static ChatLanguageModel tryCreateGeminiModel(String apiKey, String modelName, LLMConfig config) {
        try {
            ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(config.getTemperature())
                .maxOutputTokens(config.getMaxTokens())
                .logRequestsAndResponses(false)
                .build();

            // Try a test call to verify it works
            // Note: This is optional and might increase startup time
            // model.generate("test");

            return model;
        } catch (Exception e) {
            log.debug("Failed to create Gemini model '{}': {}", modelName, e.getMessage());
            return null;
        }
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
