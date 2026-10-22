package com.example.smartdoc.config;
import com.example.smartdoc.service.AiAssistant;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;
@Configuration
public class LangChain4jConfig {
    @Value("${ollama.base-url}") private String ollamaUrl;
    @Value("${chroma.url}") private String chromaUrl;
    @Bean
    public OllamaChatModel chatModel() {
        return OllamaChatModel.builder().baseUrl(ollamaUrl).modelName("deepseek-r1").temperature(0.1).timeout(Duration.ofSeconds(120)).build();
    }
    @Bean
    public OllamaEmbeddingModel embeddingModel() {
        return OllamaEmbeddingModel.builder().baseUrl(ollamaUrl).modelName("nomic-embed-text").timeout(Duration.ofSeconds(60)).build();
    }
    @Bean
    public ChromaEmbeddingStore embeddingStore() {
        return ChromaEmbeddingStore.builder().baseUrl(chromaUrl).collectionName("free_smart_doc_collection").build();
    }
    @Bean
    public AiAssistant aiAssistant(OllamaChatModel chatModel, OllamaEmbeddingModel embeddingModel, ChromaEmbeddingStore embeddingStore) {
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder().embeddingStore(embeddingStore).embeddingModel(embeddingModel).maxResults(3).build();
        return AiServices.builder(AiAssistant.class).chatLanguageModel(chatModel).contentRetriever(contentRetriever).build();
    }
}