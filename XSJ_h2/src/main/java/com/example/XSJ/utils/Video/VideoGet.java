package com.example.XSJ.utils.Video;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.example.XSJ.Pojo.PoolExtractRule;
import com.example.XSJ.Pojo.Video.Video;
import com.example.XSJ.Pojo.Video.VideoPoolConfig;
import com.example.XSJ.ServiceImpl.VideoServiceImpl.VideoServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 视频推荐算法工具类
 */
@Slf4j
@RestController
public class VideoGet {

    @Autowired
    private VideoServiceImpl videoService;

    @Autowired
    private ObjectMapper objectMapper;

    private VideoCache videoCache=new VideoCache();

    /**
     * 视频推荐入口方法
     * @param Code  传递过来的用户代码
     * @param mont  需要的视频数量
     * @return  推荐视频列表
     */
    public List<Video> VideoRecommendations(String Code, int mont) {
        List<Video> videos = new ArrayList<>();
        // 获取当前用户的基础信息 encode 权重面板
        String date = videoService.getuserdatebycode(Code);
        //如果这个值是空的说明这个账号刚注册的还没有用户画像
        if (date == null || date.isEmpty()) {
            // 进入兜底
            videos = randomByDb(mont, null);
            return videos;
        }
        // 反序列化用户面板
        try {
            Map<String, BigDecimal> UserDate = objectMapper.readValue(date,
                    objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, BigDecimal.class));
            List<Video> finalVideo = videos(UserDate, mont);
            videos.addAll(finalVideo);
            if (finalVideo.size() == 0 || finalVideo.size() < mont) {
                log.debug("意外的结果出现了，没有根据typeList查询出来对应的数据，在这里进入了备选随机筛选视频");
                int VideoSize = mont - finalVideo.size();
                List<Video> finalDate = randomByDb(VideoSize, finalVideo);
                videos.addAll(finalDate);
            } else {
                // 返回解析的视频没有问题，那么就给他返回video
                videos = finalVideo;
            }
        } catch (JsonProcessingException e) {
            log.warn("用户面板反序列化异常：{}", e.getMessage(), e);
        }
        return videos;
    }

    private String OTHER_CATEGORY = "其它";

    /**
     * 多类型多池视频推荐
     */
    public List<Video> videos(Map<String, BigDecimal> typeList, int mont) {
        List<Video> resultList = new ArrayList<>();
        // 例如 高考 30 就要把mont 按照不同的池子进行分析 爆款吃 长尾池 和冷启动吃池 冷池子 种筛选出数据
        // 系统存储，可以对推荐进行设定 池的比例以及池内比例
        // 根据视频权重进行排序  key 是池名称 value 是视频
        if (typeList == null || typeList.isEmpty()) {
            log.warn("传入的typelist为空，直接返回空结果");
            return resultList;
        }

        BigDecimal totalRatio = BigDecimal.ZERO;
        BigDecimal OtherValue = null;

        for (Map.Entry<String, BigDecimal> type : typeList.entrySet()) {
            if (type.getValue() == null) {
                log.warn("类型{}的占比为null，跳过校验", type.getKey());
                continue;
            }
            try {
                BigDecimal ratio = type.getValue();
                totalRatio = totalRatio.add(ratio);
            } catch (Exception e) {
                log.warn("类型{}的占比格式无效：{}，跳过校验", type.getKey(), type.getValue());
            }
        }
        if (totalRatio.compareTo(BigDecimal.valueOf(100)) > 0) {
            log.warn("所有类型占比总和超过100%（{}），可能导致最终结果超过mont", totalRatio);
        }

        for (Map.Entry<String, BigDecimal> type : typeList.entrySet()) {
            if (type == null || type.getKey() == null || type.getKey().trim().isEmpty() || type.getValue() == null) {
                log.warn("当前type是无效对象 typecode 为空或占比为null，跳过处理，马上进入兜底返回数据方法");
                continue;
            }

            BigDecimal typeRatio = BigDecimal.ZERO;
            int typeMont = 0;
            try {
                typeRatio = type.getValue();

                typeMont = Math.round(typeRatio.multiply(new BigDecimal(mont))
                        .divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).floatValue());
                // 边界处理：避免分配数量为负数或超过总mont的剩余数量
                typeMont = Math.max(0, Math.min(typeMont, mont - resultList.size()));
            } catch (Exception e) {
                log.warn("类型{}的占比格式无效：{}，跳过该类型处理", type.getKey(), type.getValue());
                continue;
            }
            if (typeMont <= 0) {
                log.debug("类型{}分配的视频数量为0，跳过该类型处理", type.getKey());
                continue;
            }

            // 从pool 缓存中获取当前类型的缓存
            String standardCategory = type.getKey();
            List<String> poolIdList = new ArrayList<>();
            // 便利池id 列表 查询第二层 核心池信息缓存 获取完整数据
            List<VideoPoolConfig> resultPoolList = new ArrayList<>();
            Map<String, List<Video>> poolVideoMap = new HashMap<>();

            List<Video> currentTypeResultList = new ArrayList<>();

            try {
                if (OTHER_CATEGORY.equals(standardCategory)) {
                    log.debug("检测到「其他」类型，暂不处理，记录分配数量");
                    OtherValue = type.getValue();
                    continue;
                }

                String poolIdsJson = videoCache.PoolExtractRule.getIfPresent(standardCategory);

                if (poolIdsJson != null && !poolIdsJson.trim().isEmpty()) {
                    // 缓存命中 json 反序列化 池id列表
                    poolIdList = objectMapper.readValue(poolIdsJson,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    log.debug("缓存命中，获取到{}类型对应的池ID列表，数量：{}", standardCategory, poolIdList.size());
                } else {
                    // 缓存没有命中 查询数据库，获取这个类型下所有的id
                    poolIdList = videoService.queryPoolIdsFromDb(standardCategory);
                    if (!poolIdList.isEmpty()) {
                        // 把池id列表序列化json 存入分类索引表（更新缓存）
                        String jsonStr = objectMapper.writeValueAsString(poolIdList);
                        videoCache.poolcache.put(standardCategory, jsonStr);
                        log.debug("缓存未命中，从数据库查询到{}类型对应的池ID列表，已存入缓存，数量：{}", standardCategory, poolIdList.size());
                    } else {
                        log.warn("{}类型下无有效池ID，跳过该类型处理", standardCategory);
                        continue;
                    }
                }

                // 初始化该类型下多池的分配数量
                int poolCount = poolIdList.size();
                if (poolCount <= 0) {
                    log.warn("{}类型下无有效池ID，跳过该类型处理", standardCategory);
                    continue;
                }

                for (String poolId : poolIdList) {
                    if (poolId == null || poolId.trim().isEmpty()) {
                        log.warn("无效池ID，跳过处理");
                        continue;
                    }

                    String poolInfoJson = videoCache.poolcache.getIfPresent(poolId);
                    VideoPoolConfig videoPoolConfig = null;
                    if (poolInfoJson != null && !poolInfoJson.trim().isEmpty()) {
                        // 缓存核心命中，反序列为videoPool实体
                        videoPoolConfig = objectMapper.readValue(poolInfoJson, VideoPoolConfig.class);
                        log.debug("缓存命中，获取到池ID：{} 的配置信息", poolId);
                    } else {
                        // 核心缓存没有命中
                        videoPoolConfig = videoService.queryPoolInfoFromDb(poolId);
                        if (videoPoolConfig != null) {
                            // 存入核心池缓存信息(更新缓存)
                            String jsonStr = objectMapper.writeValueAsString(videoPoolConfig);
                            videoCache.poolcache.put(poolId, jsonStr);
                            log.debug("缓存未命中，从数据库查询到池ID：{} 的配置信息，已存入缓存", poolId);
                        }
                    }
                    if (videoPoolConfig != null) {
                        resultPoolList.add(videoPoolConfig);
                    } else {
                        log.warn("池ID：{} 无有效配置，跳过视频查询", poolId);
                        continue;
                    }

                    List<Video> poolData = videoCache.poolVideoCache.getIfPresent(poolId);
                    if (poolData != null && !poolData.isEmpty()) {
                        log.debug("缓存命中，获取到池ID：{} 的视频数据，数量：{}", poolId, poolData.size());
                    } else {
                        poolData = videoService.findPoolVideobyPoolId(poolId);
                        if (!CollectionUtils.isEmpty(poolData)) {
                            videoCache.poolVideoCache.put(poolId, poolData);
                            log.debug("缓存未命中，从数据库查询到池ID：{} 的视频数据，已存入缓存，数量：{}", poolId, poolData.size());
                        } else {
                            log.warn("池ID：{} 无有效视频数据，跳过", poolId);
                            continue;
                        }
                    }
                    poolVideoMap.put(poolId, poolData);
                    int singlePoolMont = (int) Math.ceil((float) typeMont * videoPoolConfig.getGetPoolMount().doubleValue() / 100);
                    singlePoolMont = Math.max(0, singlePoolMont);

                    List<Video> singlePoolResult = finaldate(videoPoolConfig, poolVideoMap, singlePoolMont);
                    if (!CollectionUtils.isEmpty(singlePoolResult)) {

                        int currentTypeRemainMont = typeMont - currentTypeResultList.size();
                        if (singlePoolResult.size() > currentTypeRemainMont) {
                            singlePoolResult = singlePoolResult.subList(0, currentTypeRemainMont);
                        }

                        currentTypeResultList.addAll(singlePoolResult);

                        resultList.addAll(singlePoolResult);

                        log.debug("类型{} - 池ID：{} 筛选完成，新增视频数量：{}，当前类型累计：{}",
                                standardCategory, poolId, singlePoolResult.size(), currentTypeResultList.size());
                    }
                    if (currentTypeResultList.size() >= typeMont) {
                        log.debug("类型{}已凑够总分配数量{}，终止后续池遍历", standardCategory, typeMont);
                        break;
                    }
                }
                if (currentTypeResultList.size() > typeMont) {
                    currentTypeResultList = currentTypeResultList.subList(0, typeMont);
                    int excessCount = currentTypeResultList.size() - typeMont;
                    resultList = resultList.subList(0, resultList.size() - excessCount);
                    log.debug("类型{}抽取数量超额{}，已截断至{}", standardCategory, excessCount, typeMont);
                }

            } catch (Exception e) {
                log.warn("处理类型：{} 时出现异常：{}", standardCategory, e.getMessage(), e);
            }

            // 判断总结果是否已达到mont，达到则终止所有流程
            if (resultList.size() >= mont) {
                break;
            }
        }

        // 拿到其他的类型和数据随机抽取 ，数量mount*OtherValue
        List<Video> otherRandomVideos = new ArrayList<>();
        if (OtherValue != null) {
            try {
                BigDecimal extractRatio = OtherValue.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal theoreticalCount = extractRatio.multiply(new BigDecimal(mont));
                int otherExtractCount = Math.round(theoreticalCount.floatValue());
                int remainTotalMont = mont - resultList.size();
                otherExtractCount = Math.max(0, otherExtractCount);
                otherExtractCount = Math.min(otherExtractCount, mont);
                otherExtractCount = Math.min(otherExtractCount, remainTotalMont);

                if (otherExtractCount <= 0) {
                    log.debug("「其他」类型抽取数量为0，跳过处理");
                } else {
                    log.debug("「其他」类型开始处理，抽取数量：{}（占比{}%，总基准值{}）",
                            otherExtractCount, OtherValue, mont);

                    otherRandomVideos = randomByDb(otherExtractCount, resultList);

                    if (!CollectionUtils.isEmpty(otherRandomVideos)) {
                        resultList.addAll(otherRandomVideos);
                        log.debug("「其他」类型随机抽取完成，新增视频数量：{}，当前总结果数量：{}",
                                otherRandomVideos.size(), resultList.size());
                    }
                }
            } catch (Exception e) {
                log.warn("处理「其他」类型时出现异常：{}", e.getMessage(), e);
            }
        }

        // 最终截断总结果，确保不超过mont
        if (resultList.size() > mont) {
            resultList = resultList.subList(0, mont);
            log.debug("多类型筛选结果总数超过{}，已截断为{}条", mont, mont);
        }

        return resultList;
    }

    /**
     * 从数据库中获得配置信息
     */
    public List<Video> finaldate(VideoPoolConfig VideoPoolConfig, Map<String, List<Video>> poolVideoMap, int mont) {
        // 初始化返回结果
        List<Video> finalResultList = new ArrayList<>();
        // 参数校验 避免无效
        if (VideoPoolConfig == null || poolVideoMap == null || poolVideoMap.isEmpty() || mont <= 0) {
            log.warn("视频筛选参数无效：池配置/视频映射/抽取数量异常");
            return finalResultList;
        }
        // 先查询当前的配置信息然后放到缓存中去
        String category = VideoPoolConfig.getPoolCaetGory();
        String poolType = VideoPoolConfig.getPoolType();
        String poolId = VideoPoolConfig.getVideoPoolConfigId();
        String key = buildCacheKey(category, poolType);
        if (poolId == null || poolId.isEmpty()) {
            log.warn("视频池配置中有无效id，无法获取对应视频列表");
            return finalResultList;
        }
        List<Video> currentPoolVideos = poolVideoMap.get(poolId);
        if (currentPoolVideos == null || currentPoolVideos.isEmpty()) {
            log.warn("当前池（ID：{}）无有效视频列表，无法进行筛选", poolId);
            return finalResultList;
        }

        try {
            // 从缓存中获得当前的配置
            String configDate = videoCache.PoolExtractRule.getIfPresent(key);

            PoolExtractRule poolExtractRule = null;

            // 没有对应的配置就采用默认的配置（缓存未命中）
            if (configDate == null || configDate.isEmpty()) {
                log.info("缓存未命中，从数据库查询默认池配置：poolType={}", poolType);
                poolExtractRule = videoService.findPoolConfig(poolType);

                // 数据库查询结果兜底（避免null导致序列化异常）
                if (poolExtractRule == null) {
                    log.warn("数据库未查询到默认池配置，poolType={}", poolType);
                    return finalResultList;
                }

                // json序列化并放入缓存
                String jsonStr = objectMapper.writeValueAsString(poolExtractRule);
                videoCache.PoolExtractRule.put(key, jsonStr);
                log.info("池配置已存入缓存：key={}", key);
            } else {
                // 命中缓存解析缓存
                log.info("缓存命中，开始解析池配置：key={}", key);
                poolExtractRule = objectMapper.readValue(configDate, PoolExtractRule.class);
            }

            if (poolExtractRule != null) {
                // 按照视频权重给当前视频降序排序，权重为null排在末尾
                List<Video> sortedVideoList = currentPoolVideos.stream()
                        .sorted(Comparator.comparing(Video::getGlobalBaseWeight, Comparator.nullsLast(BigDecimal::compareTo)).reversed())
                        .collect(Collectors.toList());

                // 根据配置规则，将排序后的视频分为上中下三层
                List<List<Video>> videoLayers = splitVideoToLayers(sortedVideoList, poolExtractRule);
                // 分层结果兜底（避免索引越界）
                if (videoLayers == null || videoLayers.size() < 3) {
                    log.warn("视频分层结果异常，层数不足3层：实际层数={}", videoLayers == null ? 0 : videoLayers.size());
                    return finalResultList;
                }
                List<Video> topLayerVideos = videoLayers.get(0);
                List<Video> midLayerVideos = videoLayers.get(1);
                List<Video> bottomLayerVideos = videoLayers.get(2);

                Map<String, Integer> extractCountMap = calculateLayerExtractCount(mont, poolExtractRule);
                // 避免key不存在导致空指针
                int topExtractCount = extractCountMap.getOrDefault("top", 0);
                int midExtractCount = extractCountMap.getOrDefault("mid", 0);
                int bottomExtractCount = extractCountMap.getOrDefault("bottom", 0);
                topExtractCount = Math.max(1, topExtractCount); // 不足1补1
                midExtractCount = Math.max(1, midExtractCount);
                bottomExtractCount = Math.max(1, bottomExtractCount);
                topExtractCount = Math.min(topExtractCount, topLayerVideos.size());
                midExtractCount = Math.min(midExtractCount, midLayerVideos.size());
                bottomExtractCount = Math.min(bottomExtractCount, bottomLayerVideos.size());
                List<Video> topExtractVideos = randomExtractVideos(topLayerVideos, topExtractCount);
                List<Video> midExtractVideos = randomExtractVideos(midLayerVideos, midExtractCount);
                List<Video> bottomExtractVideos = randomExtractVideos(bottomLayerVideos, bottomExtractCount);
                finalResultList.addAll(topExtractVideos);
                finalResultList.addAll(midExtractVideos);
                finalResultList.addAll(bottomExtractVideos);

                if (finalResultList.size() > mont) {
                    finalResultList = finalResultList.subList(0, mont);
                    log.info("视频总数超出mont（{}），已截断至{}条", mont, finalResultList.size());
                }

                log.info("视频筛选完成：池ID={}，抽取总数={}（上层{}条、中层{}条、下层{}条）",
                        poolId, finalResultList.size(), topExtractVideos.size(), midExtractVideos.size(), bottomExtractVideos.size());
            } else {
                log.warn("池配置为空，无法进行视频筛选：key={}", key);
            }
        } catch (Exception e) {
            log.warn("视频筛选计算异常：{}", e.getMessage(), e);
        }
        return finalResultList;
    }

    /**
     * 构建配置缓存 Key（分类:池类型）
     */
    private String buildCacheKey(String category, String poolType) {
        String safeCategory = (category == null || category.trim().isEmpty()) ? "*" : category.trim();
        String safePoolType = (poolType == null || poolType.trim().isEmpty()) ? "" : poolType.trim();
        return String.format("%s:%s", safeCategory, safePoolType);
    }

    /**
     * 将排序后的视频分成上中下
     */
    private List<List<Video>> splitVideoToLayers(List<Video> sortedVideoList, PoolExtractRule poolExtractRule) {
        List<List<Video>> layers = new ArrayList<>(3);
        int totalVideoCount = sortedVideoList.size();

        BigDecimal topRatio = poolExtractRule.getTopWeightLayerRatio();
        BigDecimal midRatio = poolExtractRule.getMidWeightLayerRatio();
        BigDecimal bottomRatio = new BigDecimal("1.0").subtract(topRatio).subtract(midRatio);

        int topCount = Math.round(topRatio.multiply(new BigDecimal(totalVideoCount)).floatValue());
        int midCount = Math.round(midRatio.multiply(new BigDecimal(totalVideoCount)).floatValue());
        int bottomCount = totalVideoCount - topCount - midCount;

        topCount = Math.max(0, Math.min(topCount, totalVideoCount));
        midCount = Math.max(0, Math.min(midCount, totalVideoCount - topCount));
        bottomCount = Math.max(0, totalVideoCount - topCount - midCount);

        List<Video> topLayer = new ArrayList<>(sortedVideoList.subList(0, topCount));
        List<Video> midLayer = new ArrayList<>(sortedVideoList.subList(topCount, topCount + midCount));
        List<Video> bottomLayer = new ArrayList<>(sortedVideoList.subList(topCount + midCount, totalVideoCount));

        layers.add(topLayer);
        layers.add(midLayer);
        layers.add(bottomLayer);
        return layers;
    }

    /**
     * 计算每一层应该抽取的视频数量（实现不足1补1）
     */
    private Map<String, Integer> calculateLayerExtractCount(int mont, PoolExtractRule poolExtractRule) {
        Map<String, Integer> extractCountMap = new HashMap<>();

        BigDecimal topExtractRatio = poolExtractRule.getTopWeightExtractRatio();
        BigDecimal midExtractRatio = poolExtractRule.getMidWeightExtractRatio();
        // 原有比例计算逻辑不变，确保三层比例总和为1
        BigDecimal bottomExtractRatio = new BigDecimal("1.0").subtract(topExtractRatio).subtract(midExtractRatio);

        int topCount = (int) Math.ceil(topExtractRatio.multiply(new BigDecimal(mont)).floatValue());
        int midCount = (int) Math.ceil(midExtractRatio.multiply(new BigDecimal(mont)).floatValue());
        int bottomCount = mont - topCount - midCount;

        topCount = Math.max(1, topCount);
        midCount = Math.max(1, midCount);
        bottomCount = Math.max(1, bottomCount);
        extractCountMap.put("top", topCount);
        extractCountMap.put("mid", midCount);
        extractCountMap.put("bottom", bottomCount);
        return extractCountMap;
    }

    /**
     * 基础随机抽取方法
     */
    private List<Video> randomExtractVideos(List<Video> videoList, int extractCount) {
        List<Video> extractList = new ArrayList<>();

        if (CollectionUtils.isEmpty(videoList) || extractCount <= 0) {
            return extractList;
        }
        if (extractCount >= videoList.size()) {
            return new ArrayList<>(videoList);
        }

        List<Video> tempList = new ArrayList<>(videoList);
        for (int i = 0; i < extractCount; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(tempList.size());
            extractList.add(tempList.remove(randomIndex));
        }

        return extractList;
    }

    /**
     * 随机查询的方法
     * @param mount  需要返回的视频总数
     * @param noInVideo  要排除掉的视频
     * @return  随机筛选的视频列表
     */
    public List<Video> randomByDb(int mount, List<Video> noInVideo) {
        List<Video> RanDomVideo = new ArrayList<>();
        RanDomVideo = videoService.findVideoRandom(mount, noInVideo);
        return RanDomVideo;
    }

}