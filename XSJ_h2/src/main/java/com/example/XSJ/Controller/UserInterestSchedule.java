package com.example.XSJ.Controller;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.example.XSJ.Pojo.User.UserVideoInteractDTO;
import com.example.XSJ.ServiceImpl.UserServiceImpl.UserHistoryServiceimpl;
import com.example.XSJ.ServiceImpl.UserServiceImpl.UserServiceImpl;
import com.example.XSJ.service.User.UserWatchHistoryDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserInterestSchedule {

    @Autowired
    private UserHistoryServiceimpl userHistoryServiceimpl;

    @Autowired
    private UserServiceImpl userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 10秒一次
    @Scheduled(cron = "0 0 0/6 * * ?")
    public void calculateUserInterest() {
        List<String> userIds = userHistoryServiceimpl.queryRecentThreeDaysActiveUsers();
        if (CollectionUtils.isEmpty(userIds)) {
            log.info("暂无活跃用户");
            return;
        }

        for (String userId : userIds) {
            try {
                calculateSingleUser(userId);
            } catch (Exception e) {
                log.error("用户 {} 计算异常", userId, e);
            }
        }
    }
    private void calculateSingleUser(String userId) throws JsonProcessingException {
        List<UserWatchHistoryDTO> historyList = userHistoryServiceimpl.queryUserHistory(userId);
        if (CollectionUtils.isEmpty(historyList)) {
            log.info("用户 {} 无观看记录", userId);
            return;
        }
        Map<String, List<UserWatchHistoryDTO>> groupById = historyList.stream()
                .collect(Collectors.groupingBy(UserWatchHistoryDTO::getId));

        // 批量查询互动
        List<String> videoIds = groupById.values().stream()
                .map(v -> v.get(0).getVideoId())
                .distinct()
                .collect(Collectors.toList());

        List<UserVideoInteractDTO> interactList = userHistoryServiceimpl.queryUserInteract(userId, videoIds);
        Map<String, UserVideoInteractDTO> interactMap = interactList.stream()
                .collect(Collectors.toMap(UserVideoInteractDTO::getVideoId, i -> i));

        // 计算真实得分
        Map<String, Integer> typeScoreMap = new HashMap<>();
        for (List<UserWatchHistoryDTO> recordTypes : groupById.values()) {
            UserWatchHistoryDTO first = recordTypes.get(0);

            int baseScore = 50;
            int durationScore = getDurationScore(first);
            int interactScore = getInteractScore(interactMap.get(first.getVideoId()));

            int total = baseScore + durationScore + interactScore;
            total = Math.max(total, 0);
            total = Math.min(total, 100);

            for (UserWatchHistoryDTO dto : recordTypes) {
                String typeName = dto.getTypeName();
                typeScoreMap.put(typeName, typeScoreMap.getOrDefault(typeName, 0) + total);
            }
        }

        // ====================== 核心算法：其他固定10% ======================
        Map<String, Integer> result = new LinkedHashMap<>();

        // 1. 计算总分
        int totalScore = typeScoreMap.values().stream().mapToInt(Integer::intValue).sum();
        if (totalScore <= 0) {
            result.put("其他", 100);
            String json = objectMapper.writeValueAsString(result);
            log.info("用户 {} 最终兴趣：{}", userId, json);
            userService.updateUserInterest(userId, json);
            return;
        }

        // 2. 按 90% 分配
        for (Map.Entry<String, Integer> entry : typeScoreMap.entrySet()) {
            String name = entry.getKey();
            int score = entry.getValue();

            int percent = (int) ((score * 90.0) / totalScore);
            if (percent > 0) {
                result.put(name, percent);
            }
        }

        // 3. 固定 其他:10
        result.put("其他", 10);

        // 4. 误差修正，总和=100
        int sum = result.values().stream().mapToInt(Integer::intValue).sum();
        if (sum != 100) {
            String first = result.keySet().iterator().next();
            result.put(first, result.get(first) + (100 - sum));
        }

        // 输出
        String json = objectMapper.writeValueAsString(result);
        log.info("用户 {} 最终兴趣：{}", userId, json);
        userService.updateUserInterest(userId, json);
    }

    // 播放时长得分
    private int getDurationScore(UserWatchHistoryDTO h) {
        Integer d = h.getDuration();
        if (d == null) return -50;

        if (d >= 80) return 30;
        if (d >= 50) return 15;
        if (d >= 20) return 0;
        if (d >= 10) return -20;
        return -50;
    }

    // 互动得分
    private int getInteractScore(UserVideoInteractDTO i) {
        if (i == null) return 0;
        int s = 0;
        if (i.getIsLike() == 1) s += 10;
        if (i.getIsCollect() == 1) s += 15;
        if (i.getIsComment() == 1) s += 20;
        if (i.getIsShare() == 1) s += 10;
        return s;
    }

}