package com.example.smartdoc.controller;
import com.example.smartdoc.service.AiAssistant;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
@RestController
@RequestMapping("/api/ai")
public class DocAiController {
    private final AiAssistant aiAssistant;
    private final OllamaEmbeddingModel embeddingModel;
    private final ChromaEmbeddingStore embeddingStore;
    public DocAiController(AiAssistant aiAssistant, OllamaEmbeddingModel embeddingModel, ChromaEmbeddingStore embeddingStore) {
        this.aiAssistant = aiAssistant;
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }
    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            Document document = Document.from(content);
            DocumentSplitter splitter = DocumentSplitters.recursive(400, 40);
            var segments = splitter.split(document);
            for (TextSegment segment : segments) {
                var embedding = embeddingModel.embed(segment).content();
                embeddingStore.add(embedding, segment);
            }
            return ResponseEntity.ok("文件上傳成功！已成功切分為 " + segments.size() + " 個知識區塊並寫入向量資料庫。");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("檔案解析失敗: " + e.getMessage());
        }
    }
    @GetMapping("/ask")
    public ResponseEntity<String> askAi(@RequestParam("question") String question) {
        return ResponseEntity.ok(aiAssistant.chat(question));
    }
}