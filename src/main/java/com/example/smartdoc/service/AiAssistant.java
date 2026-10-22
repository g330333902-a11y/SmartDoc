package com.example.smartdoc.service;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
public interface AiAssistant {
    @SystemMessage("""
        你是一位專業的企業財務與法務審計師。
        請嚴格根據系統檢索到的文件上下文（Context）來回答使用者提問。
        1. 如果上下文中沒有提到相關資訊，請誠實回答「根據目前上傳的知識庫資料無法回答此問題」，切勿捏造事實。
        2. 請用結構化、條列式的方式回答，確保邏輯清晰。
        """)
    String chat(@UserMessage String userMessage);
}