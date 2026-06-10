package com.example.XSJ.service.Ai;

import com.example.XSJ.Mapper.Ai.AiConfigMapper;
import com.example.XSJ.Pojo.Ai.*;
import com.example.XSJ.utils.ThreadLocalUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
public class AiChatService {

    @Resource
    private AiConfigMapper aiConfigMapper;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private ObjectMapper objectMapper;

    private final WebClient webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
            .build();

    private static final int MAX_HISTORY_COUNT = 20;

    private String getSessionId(String requestSessionId) {
        if (requestSessionId != null && !requestSessionId.trim().isEmpty()) {
            return requestSessionId.trim();
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    private AiConfig getValidAiConfig(String modelCode) {
        AiConfig aiConfig = null;

        if (modelCode != null && !modelCode.trim().isEmpty()) {
            aiConfig = aiConfigMapper.selectByModelCode(modelCode.trim());
            log.info("从MySQL查询模型配置：modelCode={}, 配置={}", modelCode, aiConfig);
        }

        if (aiConfig == null) {
            aiConfig = aiConfigMapper.selectDefaultEnableConfig();
            log.info("使用MySQL默认模型配置：{}", aiConfig);
        }

        if (aiConfig != null && aiConfig.getStatus() != 1) {
            throw new RuntimeException("模型已禁用：" + aiConfig.getModelCode());
        }

        if (aiConfig != null) {
            String baseUrl = aiConfig.getBaseUrl();
            if (baseUrl != null) {
                baseUrl = baseUrl.trim();
                if (baseUrl.endsWith(",")) {
                    baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
                }
                aiConfig.setBaseUrl(baseUrl);
            }

            String apiKey = aiConfig.getApiKey();
            if (apiKey != null) {
                apiKey = apiKey.trim();
                aiConfig.setApiKey(apiKey);
            }
        }

        return aiConfig;
    }

    private boolean isVolcanoArkPlatform(String baseUrl) {
        return baseUrl != null && (baseUrl.contains("volces.com") || baseUrl.contains("ark.cn-beijing.volces.com"));
    }

    private void saveOrUpdateConversation(String sessionId, String userId,
                                          String modelCode, String firstMessage) {
        int exists = aiConfigMapper.existsById(sessionId);

        if (exists == 0) {
            UserAndAi conversation = new UserAndAi();
            conversation.setUserAndAi(sessionId);
            conversation.setUserId(userId);
            conversation.setAiId(modelCode);
            String title = generateTitle(firstMessage);
            conversation.setConvTitle(title);
            conversation.setCreatTime(LocalTime.now());
            conversation.setUpdateTime(LocalTime.now());
            conversation.setIsDeleted(1);

            aiConfigMapper.insert(conversation);
            log.info("新会话已存入MySQL：sessionId={}, title={}", sessionId, title);
        } else {
            aiConfigMapper.updateTime(sessionId);
            log.info("会话时间已更新：sessionId={}", sessionId);
        }
    }

    private String generateTitle(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "新对话";
        }
        String title = message.replaceAll("[\\r\\n]", " ").trim();
        if (title.length() > 50) {
            title = title.substring(0, 50) + "...";
        }
        return title;
    }

    private void saveUserChatRecord(AiChatRequest request, String sessionId, AiConfig aiConfig, String userId) {
        AiChatRecord userRecord = new AiChatRecord();
        userRecord.setUserId(userId);
        userRecord.setSessionId(sessionId);
        userRecord.setModelCode(aiConfig.getModelCode());
        userRecord.setRole("user");
        userRecord.setContent(request.getContent());
        userRecord.setChatTime(LocalDateTime.now());
        userRecord.setIsDeleted(false);

        mongoTemplate.insert(userRecord);
        log.info("用户提问已存入MongoDB：sessionId={}, userId={}", sessionId, userId);
    }

    private void saveAiChatRecord(String sessionId, AiConfig aiConfig, String aiReply, String userId) {
        AiChatRecord aiRecord = new AiChatRecord();
        aiRecord.setUserId(userId);
        aiRecord.setSessionId(sessionId);
        aiRecord.setModelCode(aiConfig.getModelCode());
        aiRecord.setRole("ai");
        aiRecord.setContent(aiReply);
        aiRecord.setChatTime(LocalDateTime.now());
        aiRecord.setIsDeleted(false);

        mongoTemplate.insert(aiRecord);
        log.info("AI回复已存入MongoDB：sessionId={}, userId={}, content长度={}", sessionId, userId, aiReply.length());
    }

    public List<AiChatRecord> getChatHistory(String sessionId) {
        Map<String, Object> date = ThreadLocalUtil.get();
        String enCode = date.get("EnCode").toString();
        String userId = aiConfigMapper.getUserIdByEncode(enCode);

        Query query = new Query();
        query.addCriteria(
                Criteria.where("userId").is(userId)
                        .and("sessionId").is(sessionId)
                        .and("isDeleted").is(false)
        );
        query.with(Sort.by(Sort.Direction.ASC, "chatTime"));

        List<AiChatRecord> history = mongoTemplate.find(query, AiChatRecord.class);
        log.info("查询会话历史：userId={}, sessionId={}, 记录数={}", userId, sessionId, history.size());
        return history;
    }

    public List<UserAndAi> getConversationList(String userId) {
        return aiConfigMapper.selectByUserId(userId);
    }

    public int deleteConversation(String sessionId) {
        return aiConfigMapper.deleteById(sessionId);
    }

    private List<Map<String, String>> buildHistoryMessages(String userId, String sessionId) {
        log.info("【调试】开始构建历史消息: userId={}, sessionId={}", userId, sessionId);

        Query query = new Query();
        query.addCriteria(
                Criteria.where("userId").is(userId)
                        .and("sessionId").is(sessionId)
                        .and("isDeleted").is(false)
        );
        query.with(Sort.by(Sort.Direction.ASC, "chatTime"));
        query.limit(MAX_HISTORY_COUNT);

        List<AiChatRecord> records = mongoTemplate.find(query, AiChatRecord.class);
        log.info("【调试】MongoDB查询结果: 记录数={}", records.size());

        List<Map<String, String>> messages = new ArrayList<>();
        for (AiChatRecord record : records) {
            String role = "user".equals(record.getRole()) ? "user" : "assistant";
            messages.add(Map.of("role", role, "content", record.getContent()));
            log.info("【调试】历史消息: role={}, content前20字={}", role,
                    record.getContent().substring(0, Math.min(20, record.getContent().length())));
        }

        log.info("构建历史消息上下文：sessionId={}, 历史消息数={}", sessionId, messages.size());
        return messages;
    }

    public Flux<AiChatStreamResponse> streamChatWithAiFlux(AiChatRequest request) {
        Map<String, Object> map = ThreadLocalUtil.get();
        String userEncode = map.get("EnCode").toString();
        String sessionId = getSessionId(request.getSessionId());

        return Mono.fromCallable(() -> {

                    AiConfig aiConfig = getValidAiConfig(request.getModelCode());
                    if (aiConfig == null) {
                        throw new RuntimeException("无可用的AI模型配置");
                    }

                    String userId = aiConfigMapper.getUserIdByEncode(userEncode);

                    List<Map<String, String>> historyMessages = buildHistoryMessages(userId, sessionId);

                    return new ChatContext(aiConfig, userId, sessionId, new StringBuilder(), historyMessages);
                })
                .flatMapMany(context -> {
                    AiConfig aiConfig = context.getAiConfig();
                    String userId = context.getUserId();
                    StringBuilder fullAiReply = context.getFullAiReply();
                    List<Map<String, String>> historyMessages = context.getHistoryMessages();

                    saveUserChatRecord(request, sessionId, aiConfig, userId);

                    saveOrUpdateConversation(sessionId, userId, aiConfig.getModelCode(), request.getContent());

                    List<Map<String, String>> messages = new ArrayList<>(historyMessages);
                    messages.add(Map.of("role", "user", "content", request.getContent()));

                    log.info("发送给AI的消息总数: 历史消息={}, 当前消息=1, 总计={}",
                            historyMessages.size(), messages.size());

                    Map<String, Object> requestBody = buildRequestBody(aiConfig, messages);

                    log.info("开始调用AI API: {}, 平台: {}", aiConfig.getBaseUrl(),
                            isVolcanoArkPlatform(aiConfig.getBaseUrl()) ? "火山方舟" : "其他");

                    return callAiApi(aiConfig, requestBody, sessionId, fullAiReply, userId);
                });
    }

    private Map<String, Object> buildRequestBody(AiConfig aiConfig, List<Map<String, String>> messages) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiConfig.getModel());
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2000);
        requestBody.put("stream", true);

        return requestBody;
    }

    private Flux<AiChatStreamResponse> callAiApi(AiConfig aiConfig, Map<String, Object> requestBody,
                                                 String sessionId, StringBuilder fullAiReply, String userId) {
        log.info("请求体: {}", requestBody);
        log.info("Authorization: Bearer {}", aiConfig.getApiKey().substring(0, Math.min(10, aiConfig.getApiKey().length())) + "...");

        WebClient.RequestHeadersSpec<?> request = webClient.post()
                .uri(aiConfig.getBaseUrl())
                .header("Authorization", "Bearer " + aiConfig.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(requestBody);

        return request.retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("API调用失败，状态码: {}, 响应: {}", response.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("API调用失败: " + errorBody));
                                })
                )
                .bodyToFlux(DataBuffer.class)
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .flatMap(content -> parseSseContent(content, sessionId, aiConfig, fullAiReply))
                .doOnComplete(() -> {
                    String aiReply = fullAiReply.toString();
                    log.info("AI回复完成，总长度: {}", aiReply.length());
                    if (aiReply.length() > 0) {
                        saveAiChatRecord(sessionId, aiConfig, aiReply, userId);
                    }
                })
                .concatWith(Mono.defer(() -> {
                    AiChatStreamResponse finishResp = new AiChatStreamResponse();
                    finishResp.setIsFinish(true);
                    finishResp.setSessionId(sessionId);
                    finishResp.setModelName(aiConfig.getModelName());
                    finishResp.setChunkContent(fullAiReply.toString());
                    return Mono.just(finishResp);
                }))
                .onErrorResume(e -> {
                    log.error("AI流式对话失败", e);
                    AiChatStreamResponse errorResp = new AiChatStreamResponse();
                    errorResp.setIsFinish(true);
                    errorResp.setSessionId(sessionId);
                    return Mono.just(errorResp);
                });
    }

    private Flux<AiChatStreamResponse> parseSseContent(String content, String sessionId,
                                                       AiConfig aiConfig, StringBuilder fullAiReply) {
        if (content == null || content.trim().isEmpty()) {
            return Flux.empty();
        }

        List<AiChatStreamResponse> responses = new ArrayList<>();
        String[] lines = content.split("\n");

        for (String line : lines) {
            line = line.trim();

            if (line.isEmpty() || !line.startsWith("data: ")) {
                continue;
            }

            String jsonStr = line.substring(6).trim();

            if ("[DONE]".equals(jsonStr)) {
                log.info("收到 [DONE] 标记");
                continue;
            }

            try {
                JsonNode chunkNode = objectMapper.readTree(jsonStr);
                JsonNode choices = chunkNode.get("choices");

                if (choices != null && choices.isArray() && choices.size() > 0) {
                    JsonNode delta = choices.get(0).get("delta");

                    if (delta != null && delta.hasNonNull("content")) {
                        JsonNode contentNode = delta.get("content");
                        String chunkContent = contentNode.asText();

                        if (chunkContent != null && !chunkContent.isEmpty() && !"null".equals(chunkContent)) {
                            fullAiReply.append(chunkContent);

                            AiChatStreamResponse streamResp = new AiChatStreamResponse();
                            streamResp.setChunkContent(chunkContent);
                            streamResp.setIsFinish(false);
                            streamResp.setSessionId(sessionId);
                            streamResp.setModelName(aiConfig.getModelName());
                            responses.add(streamResp);
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("解析JSON失败: {}", jsonStr);
            }
        }

        return Flux.fromIterable(responses);
    }

    private static class ChatContext {
        private final AiConfig aiConfig;
        private final String userId;
        private final String sessionId;
        private final StringBuilder fullAiReply;
        private final List<Map<String, String>> historyMessages;

        public ChatContext(AiConfig aiConfig, String userId, String sessionId,
                           StringBuilder fullAiReply, List<Map<String, String>> historyMessages) {
            this.aiConfig = aiConfig;
            this.userId = userId;
            this.sessionId = sessionId;
            this.fullAiReply = fullAiReply;
            this.historyMessages = historyMessages;
        }

        public AiConfig getAiConfig() { return aiConfig; }
        public String getUserId() { return userId; }
        public String getSessionId() { return sessionId; }
        public StringBuilder getFullAiReply() { return fullAiReply; }
        public List<Map<String, String>> getHistoryMessages() { return historyMessages; }
    }
}
