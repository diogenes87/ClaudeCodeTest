package com.example.mcp.config.llm;

import lombok.Builder;
import lombok.Data;

/**
 * Configuration for LLM connection
 */
@Data
@Builder
public class LLMConfig {

    private LLMProvider provider;
    private String apiKey;
    private String modelName;

    @Builder.Default
    private Double temperature = 0.7;

    @Builder.Default
    private Integer timeoutSeconds = 60;

    @Builder.Default
    private Integer maxTokens = 2000;

    /**
     * Create configuration from environment variables
     */
    public static LLMConfig fromEnvironment() {
        LLMProvider provider = LLMProvider.autoDetect();

        if (provider == null) {
            throw new IllegalStateException(
                "No LLM provider configured. Please set one of: " +
                "OPENAI_API_KEY, GEMINI_API_KEY, or ANTHROPIC_API_KEY"
            );
        }

        return LLMConfig.builder()
            .provider(provider)
            .apiKey(provider.getApiKey())
            .modelName(provider.getDefaultModel())
            .build();
    }

    /**
     * Create configuration with specific provider
     */
    public static LLMConfig forProvider(LLMProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }

        if (!provider.isConfigured()) {
            throw new IllegalStateException(
                "Provider " + provider.name() + " is not configured. " +
                "Please set " + provider.getApiKeyEnvVar()
            );
        }

        return LLMConfig.builder()
            .provider(provider)
            .apiKey(provider.getApiKey())
            .modelName(provider.getDefaultModel())
            .build();
    }

    /**
     * Create configuration with custom model
     */
    public static LLMConfig forProviderWithModel(LLMProvider provider, String modelName) {
        LLMConfig config = forProvider(provider);
        config.setModelName(modelName);
        return config;
    }
}
