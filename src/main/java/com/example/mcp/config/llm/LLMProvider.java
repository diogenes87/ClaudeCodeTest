package com.example.mcp.config.llm;

/**
 * Enumeration of supported LLM providers
 */
public enum LLMProvider {
    OPENAI("OpenAI", "gpt-4", "OPENAI_API_KEY"),
    GEMINI("Google Gemini", "gemini-1.5-flash", "GEMINI_API_KEY"),
    ANTHROPIC("Anthropic Claude", "claude-3-sonnet-20240229", "ANTHROPIC_API_KEY");

    private final String displayName;
    private final String defaultModel;
    private final String apiKeyEnvVar;

    LLMProvider(String displayName, String defaultModel, String apiKeyEnvVar) {
        this.displayName = displayName;
        this.defaultModel = defaultModel;
        this.apiKeyEnvVar = apiKeyEnvVar;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public String getApiKeyEnvVar() {
        return apiKeyEnvVar;
    }

    public String getApiKey() {
        return System.getenv(apiKeyEnvVar);
    }

    public boolean isConfigured() {
        String apiKey = getApiKey();
        return apiKey != null && !apiKey.isEmpty();
    }

    /**
     * Auto-detect which LLM provider to use based on environment variables
     */
    public static LLMProvider autoDetect() {
        // Check in order of preference
        if (OPENAI.isConfigured()) {
            return OPENAI;
        }
        if (GEMINI.isConfigured()) {
            return GEMINI;
        }
        if (ANTHROPIC.isConfigured()) {
            return ANTHROPIC;
        }
        return null; // No provider configured
    }

    /**
     * Get provider by name (case-insensitive)
     */
    public static LLMProvider fromName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        for (LLMProvider provider : values()) {
            if (provider.name().equalsIgnoreCase(name)) {
                return provider;
            }
        }
        return null;
    }
}
