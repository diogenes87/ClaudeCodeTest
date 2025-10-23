package com.example.mcp.config;

import com.example.mcp.ai.OracleDatabaseAIService;
import com.example.mcp.config.llm.LLMConfig;
import com.example.mcp.config.llm.LLMProvider;
import com.example.mcp.config.llm.LLMProviderFactory;
import com.example.mcp.tools.OracleDbTools;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for LangChain4J AI Services
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class LangChain4jConfiguration {

    private final OracleDbTools oracleDbTools;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        try {
            LLMConfig config = LLMConfig.fromEnvironment();
            log.info("Initializing ChatLanguageModel with provider: {}, model: {}",
                    config.getProvider().getDisplayName(),
                    config.getModelName());
            return LLMProviderFactory.create(config);
        } catch (Exception e) {
            log.warn("No LLM provider configured. AI features will be disabled. " +
                    "Set OPENAI_API_KEY or GEMINI_API_KEY environment variable.");
            return null;
        }
    }

    @Bean
    public OracleDatabaseAIService oracleDatabaseAIService(ChatLanguageModel chatLanguageModel) {
        if (chatLanguageModel == null) {
            log.warn("ChatLanguageModel is null. Returning mock AI service.");
            // Return a mock implementation
            return new OracleDatabaseAIService() {
                @Override
                public String chat(String userMessage) {
                    return "{\"error\": \"AI service not configured. Please set OPENAI_API_KEY or GEMINI_API_KEY.\"}";
                }

                @Override
                public String query(String userMessage) {
                    return "{\"error\": \"AI service not configured. Please set OPENAI_API_KEY or GEMINI_API_KEY.\"}";
                }
            };
        }

        return AiServices.builder(OracleDatabaseAIService.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(oracleDbTools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .build();
    }
}
