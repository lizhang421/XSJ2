package com.example.XSJ.Controller;

import com.example.XSJ.Pojo.System.SysScoreConfig;
import com.example.XSJ.Pojo.Video.Video;
import com.example.XSJ.Pojo.Video.VideoPoolConfig;
import com.example.XSJ.ServiceImpl.Manager.Manager.ManagerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class VideoScoreCalculateTask {

    @Autowired
    private ManagerServiceImpl managerServiceImpl;

    @Scheduled(cron = "0 0 0/3 * * ?")
    public void calculateVideoSortWeight() {
        SysScoreConfig sysScoreConfigs = managerServiceImpl.getSysScoreConfig();
        int pageSize = 500;
        int currentPage = 1;
        int totalCount = 0;
        while (true) {
            int offset = (currentPage - 1) * pageSize;
            List<Video> videoList = managerServiceImpl.getVideoList(pageSize, offset);

            if (videoList == null || videoList.isEmpty()) {
                break;
            }
            log.info("正在处理第 {} 页，本批次 {} 条视频", currentPage, videoList.size());
            for (Video video : videoList) {
                Integer viewCount = video.getViewCount() == null ? 0 : video.getViewCount();
                Integer likeCount = video.getLikeCount() == null ? 0 : video.getLikeCount();
                Integer commentCount = video.getCommentCount() == null ? 0 : video.getCommentCount();
                Integer collectCount = video.getCollectCount() == null ? 0 : video.getCollectCount();
                Integer shareCount = video.getShareCount() == null ? 0 : video.getShareCount();

                if (viewCount <= 0) {
                    video.setSortWeight("0");
                    managerServiceImpl.updateVideoSortWeight(video);
                    totalCount++;
                    continue;
                }
                double likeScore = (likeCount.doubleValue() / viewCount) * sysScoreConfigs.getLikeRateWeight().doubleValue();
                double commentScore = (commentCount.doubleValue() / viewCount) * sysScoreConfigs.getCommentRateWeight().doubleValue();
                double collectScore = (collectCount.doubleValue() / viewCount) * sysScoreConfigs.getCollectRateWeight().doubleValue();
                double shareScore = (shareCount.doubleValue() / viewCount) * sysScoreConfigs.getShareRateWeight().doubleValue();

                double interactionTotal = likeScore + commentScore + collectScore + shareScore;


                LocalDateTime now = LocalDateTime.now();
                LocalDateTime createTime = video.getCreateTime();

                if (createTime == null) {
                    createTime = now;
                }

                // 已发布小时数
                long hoursPassed = Duration.between(createTime, now).toHours();

                //时间窗口
                int scoreWindowHours = sysScoreConfigs.getScoreWindowHours() == null ? 168 : sysScoreConfigs.getScoreWindowHours();

                // 时间系数 = (总窗口 - 已过时间) / 总窗口
                double timeCoefficient = 0.0;
                if (hoursPassed < scoreWindowHours) {
                    timeCoefficient = (double) (scoreWindowHours - hoursPassed) / scoreWindowHours;
                }

                // 时间权重百分比（配置表里的）
                double timeWeight = sysScoreConfigs.getTimeScoreWeight() == null ? 0.0 : sysScoreConfigs.getTimeScoreWeight().doubleValue();

                // 时间分 = 系数 * 权重
                double timeScore = timeCoefficient * timeWeight;


                double total = interactionTotal + timeScore;

                // 限制 0~100
                int score = (int) Math.round(total);
                score = Math.max(0, Math.min(100, score));

                video.setSortWeight(String.valueOf(score));
                managerServiceImpl.updateVideoSortWeight(video);
                totalCount++;

                // ==============================================
                // 查当前视频所在的旧池
                // ==============================================
                VideoPoolConfig oldPoolConfig = managerServiceImpl.getVideoPoolConfig(video.getVideoId());

                // 无旧池 初始化数据 跳过
                if (oldPoolConfig == null) {
                    log.info("视频 {} 无旧池信息 → 初始化数据，跳过", video.getVideoId());
                    continue;
                }

                // 从旧池拿分类
                String useCategory = oldPoolConfig.getPoolCaetGory();

                // 分数判断目标池类型
                String targetPoolType;
                String poolTypeName;
                if (score >= 71) {
                    targetPoolType = "爆款池";
                    poolTypeName = "爆款池";
                } else if (score >= 41) {
                    targetPoolType = "长尾池";
                    poolTypeName = "长尾池";
                } else if (score >= 21) {
                    targetPoolType = "冷池";
                    poolTypeName = "冷池";
                } else {
                    targetPoolType = "冷启动池";
                    poolTypeName = "冷启动池";
                }

                // 获取目标池ID
                String targetPoolId = managerServiceImpl.getRealPoolIdByCategoryAndType(useCategory, targetPoolType);
                if (targetPoolId == null) {
                    log.warn("视频 {} 分类 {} 未找到对应的池：{}", video.getVideoId(), useCategory, targetPoolType);
                    continue;
                }

                // ==============================================
                // ==============================================
                String oldPoolId = managerServiceImpl.getCurrentVideoPoolId(video.getVideoId());

                // 出旧池
                managerServiceImpl.leaveAllPools(
                        video.getVideoId(),
                        "新池准入，清空所有旧池"
                );

                // 入新池
                if (!managerServiceImpl.isInPool(video.getVideoId(), targetPoolId)) {
                    managerServiceImpl.joinPool(video.getVideoId(), targetPoolId, "分数自动升降池 | " + poolTypeName + " | 分数：" + score);
                    log.info("视频 {} 进入新池：{}", video.getVideoId(), targetPoolId);
                }

                // 更新视频当前池
                video.setCurrentPoolType(targetPoolId);
                managerServiceImpl.updateVideoCurrentPool(video);
            }
            currentPage++;
        }
        log.info("==============================");
        log.info("任务完成！总处理视频数：{}", totalCount);
        log.info("==============================");
    }
}