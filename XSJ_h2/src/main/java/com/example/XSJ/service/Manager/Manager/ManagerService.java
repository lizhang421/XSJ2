package com.example.XSJ.service.Manager.Manager;

import com.example.XSJ.Mapper.System.ManagerMapper;
import com.example.XSJ.Mapper.UserMapper.UserMapper;
import com.example.XSJ.Mapper.VideoMapper.VideoMapper;
import com.example.XSJ.Pojo.MV.*;
import com.example.XSJ.Pojo.PoolExtractRule;
import com.example.XSJ.Pojo.Result;
import com.example.XSJ.Pojo.System.*;
import com.example.XSJ.Pojo.User.Report;
import com.example.XSJ.Pojo.User.ReportVO;
import com.example.XSJ.Pojo.User.User;
import com.example.XSJ.Pojo.User.UserUpdateDTO;
import com.example.XSJ.Pojo.UserDTO.UserMessageDTO;
import com.example.XSJ.Pojo.Video.Video;
import com.example.XSJ.Pojo.Video.VideoAuditTag;
import com.example.XSJ.Pojo.Video.VideoPoolConfig;
import com.example.XSJ.Pojo.VideoDTo.VideoPendingDTO;
import com.example.XSJ.Pojo.VideoDTo.VideoResolutionDTO;
import com.example.XSJ.ServiceImpl.Manager.Manager.ManagerServiceImpl;
import com.example.XSJ.utils.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.XSJ.utils.RegexPatterns.END_ADDRESS;

@Slf4j
@Service
public class ManagerService implements ManagerServiceImpl {

    @Autowired
    private ManagerMapper managerMapper;

    private static final Random RANDOM = new Random();

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AliVideoUtil aliVideoUtil;

    @Autowired
    private AliOssUtil aliOssUtil;

    @Autowired
    private VideoMapper videoMapper;


    @Override
    public User findmanagermessagebyphone(String phone) {

        return managerMapper.getUserMessageByPhone(phone);
    }

    @Override
    public List<VideoPendingDTO> getauditvideo() {
        //获取用户应该审核的视频auditVideo
        List<VideoPendingDTO> videoPendingDTOS = managerMapper.getauditvideo();
        return videoPendingDTOS;
    }


    @Override
    public void auditVideo(String videoPendingId, int reviewStatus, List<String> videoAuditTagId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        String UserEncode = map.get("EnCode").toString();
        String managerid = userMapper.getuserid(UserEncode);

        if (reviewStatus == 0) {
            managerMapper.auditVideo(videoPendingId, reviewStatus, managerid, UserEncode);
            managerMapper.insertAuditStatus(videoPendingId, videoAuditTagId);
            return;
        }
        if (reviewStatus == 1) {
            String videoId = "V_" + UUID.randomUUID().toString();
            managerMapper.insertVideoFromPending(videoPendingId, videoId, UserEncode);
            int rows = managerMapper.auditVideoandType(videoPendingId, videoId);
            if (rows <= 0) {
                throw new RuntimeException("视频类型关联失败，未找到待审核视频或类型信息");
            }
            Video video = managerMapper.getVideoById(videoId);
            try {
                String videoUrl = video.getVideoUrl();
                File tempFile = aliVideoUtil.downloadVideo(videoUrl);
                String localPath = tempFile.getAbsolutePath();
                FFmpegUtils.VideoInfo videoInfo = FFmpegUtils.getFullVideoInfo(localPath);
                long duration = videoInfo.getDuration();
                String coverUrl = video.getVideoImageUrl();
                if (coverUrl == null || coverUrl.trim().isEmpty()) {
                    String coverPath = localPath.replace(".mp4", "_cover.jpg");
                    try {
                        FFmpegUtils.extractFirstFrame(localPath, coverPath);
                    } catch (Exception e) {
                        throw new RuntimeException("截取视频封面失败");
                    }
                    String newCoverUrl;
                    try (FileInputStream fis = new FileInputStream(coverPath)) {
                        MultipartFile multipartFile = new MockMultipartFile(
                                "file",
                                new File(coverPath).getName(),
                                "image/jpeg",
                                fis
                        );
                        newCoverUrl = aliOssUtil.upload(multipartFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("封面上传OSS失败");
                    }
                    managerMapper.updateVideoCover(videoId, newCoverUrl);
                    new File(coverPath).delete();
                }

                //  更新视频时长到 video 表
                managerMapper.updateVideoDuration(videoId, duration);
                // 池的初始化
                String poolCaetGory = managerMapper.getpendingpool(videoPendingId);
                String poolid = managerMapper.getpoolid(poolCaetGory);
                String vapid = "VP_" + UUID.randomUUID().toString();
                managerMapper.insertPool(vapid, poolid, videoId);

                // 转码 480p 720p 1080p
                FileInputStream fis = new FileInputStream(tempFile);
                MockMultipartFile multipartFile = new MockMultipartFile(
                        "file", tempFile.getName(), "video/mp4", fis
                );
                boolean transcodeSuccess = VideoTranscodeUtil.transcodevideo(multipartFile, videoId);
                if (!transcodeSuccess) {
                    throw new RuntimeException("视频转码失败");
                }

                uploadAndSaveResolution(videoId, "480p", 854, 480);
                uploadAndSaveResolution(videoId, "720p", 1280, 720);
                uploadAndSaveResolution(videoId, "1080p", 1920, 1080);

                tempFile.delete();
                fis.close();

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("视频处理失败：" + e.getMessage());
            }
        }

        managerMapper.updateAuditStatus(videoPendingId, reviewStatus);
    }

    @Override
    public List<UserReview> getShenHeImage(List<Integer> status) {
        return managerMapper.getShenHeImage(status);
    }

    @Override
    public List<VideoAuditTag> getauditVideoResult() {
        return managerMapper.getauditVideoResult();
    }

    @Override
    public void uploadShenHeImageResult(String userviewid, String videoAuditTagId, int result) {
        try {
            if (result == 1) {
                //审核通过就把用户头像和背景图片添加到用户的数据库
                managerMapper.uploadShenHeImage(userviewid);
                managerMapper.updateReviewStatus(userviewid, 1, "审核通过");
            }
            if (result == 2) {
                managerMapper.updateReviewStatus(userviewid, 2, videoAuditTagId);
            }
        } catch (Exception e) {
            log.error("上传审核结果失败", e);
            throw new RuntimeException("上传审核结果失败");
        }
    }


    @Override
    public SysScoreConfig getSysScoreConfig() {
        return managerMapper.getSysScoreConfig();
    }

    @Override
    public List<Video> getVideoList(int currentPage, int offset) {
        return managerMapper.getVideoList(currentPage, offset);
    }

    @Override
    public void updateVideoSortWeight(Video video) {
        managerMapper.updateVideoSortWeight(video);
    }

    @Override
    public VideoPoolConfig getVideoPoolConfig(String videoId) {
        return managerMapper.getVideoPoolConfig(videoId);
    }

    @Override
    public String getRealPoolIdByCategoryAndType(String useCategory, String targetPoolType) {
        return managerMapper.getRealPoolIdByCategoryAndType(useCategory, targetPoolType);
    }

    @Override
    public void leavePool(String videoId, String oldPoolId, String s) {
        managerMapper.leavePool(videoId, oldPoolId, s);
    }

    @Override
    public boolean isInPool(String videoId, String targetPoolId) {
        return managerMapper.isInPool(videoId, targetPoolId) > 0;
    }

    @Override
    public void joinPool(String videoId, String targetPoolId, String s) {
        managerMapper.joinPool(videoId, targetPoolId, s);
    }

    @Override
    public void updateVideoCurrentPool(Video video) {
        managerMapper.updateVideoCurrentPool(video);
    }

    @Override
    public String getCurrentVideoPoolId(String videoId) {
        return managerMapper.getCurrentVideoPoolId(videoId);
    }

    @Override
    public void leaveAllPools(String videoId, String s) {
        managerMapper.leaveAllPools(videoId, s);
    }

    @Override
    public Result getUserList(int pageNum, int pageSize, Integer status, String key, Integer roleBase) {
        //查询一共有剁手用户
        try {
            int offset = (pageNum - 1) * pageSize;


            List<UserMessageDTO> userList = managerMapper.getUserList(offset, pageSize, status, roleBase, key);


            int total = managerMapper.getUserCount(status, roleBase, key);
            Map<String, Object> result = new HashMap<>();
            result.put("total", total);
            result.put("userList", userList);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Result.error();
        }
    }

    @Override
    public Result updateUserStatus(String encode, Integer status) {
        //紧急封禁解封账号
        try {
            if (encode == null || status == 3) {
                return Result.error("数据异常");
            }
            User user = managerMapper.getuserdatebyencode(encode);
            int Userolebase = user.getRoleBase();

            Map<String, Object> date = ThreadLocalUtil.get();
            String MyEncode = date.get("EnCode").toString();

            User myuser = managerMapper.getuserdatebyencode(MyEncode);
            if (myuser.getRoleBase() < Userolebase || myuser.getRoleBase() == Userolebase) {
                return Result.error("当前权限不足，不支持修改改用户");
            }

            managerMapper.updateUserStatus(encode, status);
            return Result.success();

        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            return Result.error();
        }

    }

    @Override
    public Result getManagerMessage() {
        //管理员的个人信息 、、之后优化的话可以在这里添加管理员的审核历史记录等等
        Map<String, Object> date = ThreadLocalUtil.get();
        String UserEncode = date.get("EnCode").toString();

        try {
            UserMessageDTO usermessage = managerMapper.getManagerMessage(UserEncode);
            return Result.success(usermessage);
        } catch (Exception e) {

            log.error("获取管理员信息失败", e);
            return Result.error();
        }
    }

    /**
     * 超级管理员获取系统配置
     * 1 是池内选取规则 上中下的抽取规则
     * 2 是视频评分记录规则
     *
     * @return
     */
    @Override
    public Result ManagerSysConfig() {
        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            Integer rolebase = (int) map.get("RoleBase");
            if (rolebase != 2) {
                return Result.error("权限不足");
            }
            Map<String, Object> map1 = new HashMap<>();
            List<PoolExtractRule> sysConfig = managerMapper.getSysConfig();
            SysScoreConfig sysScoreConfig = managerMapper.getSysScoreConfig();
            map1.put("sysConfig", sysConfig);
            map1.put("sysScoreConfig", sysScoreConfig);
            return Result.success(map1);
        } catch (Exception e) {
            log.error("获取系统配置失败", e);
            return Result.error();
        }
    }

    public Result updatesysSortWeight(SysScoewConfig sysScoewConfig) {

        Map<String, Object> date = ThreadLocalUtil.get();
        String UserEncode = date.get("EnCode").toString();

        if (sysScoewConfig == null) {
            return Result.error("参数异常，数据不能为空");
        }
        if (sysScoewConfig.getId() == null) {
            return Result.error("请选择要修改的配置项");
        }

        BigDecimal finishRate = getNotNullBigDecimal(sysScoewConfig.getFinishRateWeight());
        BigDecimal likeRate = getNotNullBigDecimal(sysScoewConfig.getLikeRateWeight());
        BigDecimal commentRate = getNotNullBigDecimal(sysScoewConfig.getCommentRateWeight());
        BigDecimal collectRate = getNotNullBigDecimal(sysScoewConfig.getCollectRateWeight());
        BigDecimal shareRate = getNotNullBigDecimal(sysScoewConfig.getShareRateWeight());
        BigDecimal timeScoreWeight = getNotNullBigDecimal(sysScoewConfig.getTimeScoreWeight());

        BigDecimal totalWeight = finishRate.add(likeRate)
                .add(commentRate)
                .add(collectRate)
                .add(shareRate)
                .add(timeScoreWeight);

        if (totalWeight.compareTo(new BigDecimal("100.00")) != 0) {
            return Result.error("权重总和必须等于100.00，当前总和：" + totalWeight);
        }

        Integer scoreWindowHours = sysScoewConfig.getScoreWindowHours();
        if (scoreWindowHours == null || scoreWindowHours < 0) {
            return Result.error("时间窗口不能为负数");
        }

        sysScoewConfig.setTotalWeightCheck(1);
        sysScoewConfig.setUpdateBy(UserEncode);

        int rows = managerMapper.updateById(sysScoewConfig);
        if (rows > 0) {
            return Result.success("修改成功");
        } else {
            return Result.error("修改失败");
        }
    }

    @Override
    public Result updatePoolConfig(PoolExtractRuleUpdateDTO poolExtractRuleUpdateDTO) {

        // 获取当前登录人
        Map<String, Object> date = ThreadLocalUtil.get();
        String UserEncode = date.get("EnCode").toString();


        if (poolExtractRuleUpdateDTO == null) {
            return Result.error("参数异常，数据不能为空");
        }
        if (poolExtractRuleUpdateDTO.getId() == null) {
            return Result.error("请选择要修改的配置项");
        }


        BigDecimal topLayer = getNotNullBigDecimal(poolExtractRuleUpdateDTO.getTopWeightLayerRatio());
        BigDecimal midLayer = getNotNullBigDecimal(poolExtractRuleUpdateDTO.getMidWeightLayerRatio());
        BigDecimal bottomLayer = getNotNullBigDecimal(poolExtractRuleUpdateDTO.getBottomWeightLayerRatio());
        BigDecimal topExtract = getNotNullBigDecimal(poolExtractRuleUpdateDTO.getTopWeightExtractRatio());
        BigDecimal midExtract = getNotNullBigDecimal(poolExtractRuleUpdateDTO.getMidWeightExtractRatio());
        BigDecimal bottomExtract = getNotNullBigDecimal(poolExtractRuleUpdateDTO.getBottomWeightExtractRatio());


        BigDecimal layerTotal = topLayer.add(midLayer).add(bottomLayer);
        if (layerTotal.compareTo(new BigDecimal("1.00")) != 0) {
            return Result.error("分层占比总和必须等于1.00，当前：" + layerTotal);
        }


        BigDecimal extractTotal = topExtract.add(midExtract).add(bottomExtract);
        if (extractTotal.compareTo(new BigDecimal("1.00")) != 0) {
            return Result.error("抽取占比总和必须等于1.00，当前：" + extractTotal);
        }


        PoolExtractRule rule = new PoolExtractRule();
        BeanUtils.copyProperties(poolExtractRuleUpdateDTO, rule);


        rule.setUpdateBy(UserEncode);
        rule.setUpdateTime(new Date());

        int rows = managerMapper.updatePoolById(rule);
        if (rows > 0) {
            return Result.success("修改成功");
        } else {
            return Result.error("修改失败");
        }
    }

    @Override
    public Result addPool(PoolExtractRuleUpdateDTO poolExtractRuleUpdateDTO) {


        Map<String, Object> date = ThreadLocalUtil.get();
        String userEncode = date.get("EnCode").toString();

        if (poolExtractRuleUpdateDTO == null) {
            return Result.error("参数不能为空");
        }


        BigDecimal topLayer = getNotNullBigDecimal(poolExtractRuleUpdateDTO.getTopWeightLayerRatio());
        BigDecimal midLayer = getNotNullBigDecimal(poolExtractRuleUpdateDTO.getMidWeightLayerRatio());
        BigDecimal bottomLayer = getNotNullBigDecimal(poolExtractRuleUpdateDTO.getBottomWeightLayerRatio());
        BigDecimal topExtract = getNotNullBigDecimal(poolExtractRuleUpdateDTO.getTopWeightExtractRatio());
        BigDecimal midExtract = getNotNullBigDecimal(poolExtractRuleUpdateDTO.getMidWeightExtractRatio());
        BigDecimal bottomExtract = getNotNullBigDecimal(poolExtractRuleUpdateDTO.getBottomWeightExtractRatio());


        BigDecimal layerTotal = topLayer.add(midLayer).add(bottomLayer);
        if (layerTotal.compareTo(new BigDecimal("1.00")) != 0) {
            return Result.error("分层占比总和必须等于1.00，当前：" + layerTotal);
        }

        BigDecimal extractTotal = topExtract.add(midExtract).add(bottomExtract);
        if (extractTotal.compareTo(new BigDecimal("1.00")) != 0) {
            return Result.error("抽取占比总和必须等于1.00，当前：" + extractTotal);
        }


        PoolExtractRule rule = new PoolExtractRule();
        BeanUtils.copyProperties(poolExtractRuleUpdateDTO, rule);


        rule.setCreateBy(userEncode);
        rule.setCreateTime(new Date());
        rule.setUpdateBy(userEncode);
        rule.setUpdateTime(new Date());


        int rows = managerMapper.insert(rule);
        if (rows > 0) {
            return Result.success("新增池规则成功");
        } else {
            return Result.error("新增失败");
        }
    }

    @Override
    public Result deletePool(Long poolId) {

        PoolExtractRule rule = managerMapper.selectById(poolId);


        if ("*".equals(rule.getCategory())) {
            return Result.error("全局通配池不允许删除");
        }

        int rows = managerMapper.deletePoolById(poolId);

        if (rows > 0) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败，数据不存在");
        }
    }

    @Override
    public Result getjubao(int pageNum, int pageSize, Integer status, Integer type, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 开启分页
            PageHelper.startPage(pageNum, pageSize);

            // 查询数据
            List<ReportVO> list = managerMapper.getjubao(status, type, startTime, endTime);

            // 分页包装
            PageInfo<ReportVO> pageInfo = new PageInfo<>(list);

            return Result.success(pageInfo);
        } catch (Exception e) {
            log.error("管理员获取举报列表异常", e);
            return Result.error("获取举报信息失败，请稍后重试");
        }
    }


    @Override
    public Result solvejubao(String jubaoId, Integer jieguo, Integer solvechufa) {
        try {
            Report report = managerMapper.selectreportbyid(jubaoId);

            if (report == null) {
                return Result.error("举报信息不存在");
            }

            if (report.getStatus() != 3) {
                return Result.error("举报信息不是待处理状态");
            }

            if (jieguo == 1) {
                //举报成功，修改举报状态，然后查看对应的资源，如果是评论就删除评论如果是视频就下架视频
                int rows = managerMapper.updateReportStatus(jubaoId, jieguo);
                //处理资源
                if (report.getReportType() == 1) {
                    //视频资源，那就下架对应的视频//这里修改的装填是2 说明是被举报下架的
                    int me = managerMapper.updateVideoStatus(report.getTargetId());

                }
                if (report.getReportType() == 2) {
                    //评论资源，那就删除对应的评论
                    //这个时候传的是评论的id
                    int me = managerMapper.deleteComment(report.getTargetId());

                }

                //处理处罚，如果有处罚那么久处罚，没有就不
                String userid = report.getTargetUserId();
                //添加处罚结果
                String accountId = "AU_" + UUID.randomUUID().toString();
                int iup = managerMapper.insertUserPunishment(accountId, userid, solvechufa, "管理员审核处理");


            }
            if (jieguo == 0) {
                int rows = managerMapper.updateReportStatus(jubaoId, jieguo);

            }
            return Result.success("举报成功");

        } catch (Exception e) {
            log.error("管理员处理举报异常", e);
            return Result.error("处理举报信息失败，请稍后重试");
        }
    }

    @Override
    public Result updateUserMessage(UserUpdateDTO userUpdateDTO) {
        try {
            int count = managerMapper.checkPhoneOrEmailExist(userUpdateDTO.getPhone(), userUpdateDTO.getEmail());
            if (count > 0) {
                return Result.error("手机号或邮箱已存在");
            }

            Map<String, Object> map = ThreadLocalUtil.get();
            String userEncode = (String) map.get("EnCode");

            int rows = managerMapper.updateUserMessage(userEncode, userUpdateDTO);
            if (rows > 0) {
                return Result.success("修改成功");
            }
            return Result.error("修改失败");
        } catch (Exception e) {
            log.error("修改用户信息异常", e);
            return Result.error("修改用户信息失败，请稍后重试");

        }


    }

    @Override
    public Result getSysResourceConfig() {
        try {
            List<HomeResources> list = managerMapper.getSysResourceConfig();
            return Result.success(list);

        } catch (Exception e) {
            log.error("获取系统资源配置异常", e);
            return Result.error("获取系统资源配置失败，请稍后重试");
        }
    }

    @Override
    public Result updateSysResourceConfig(String homeresourse, String url) {

        try {

            managerMapper.updatehomerecourse(homeresourse, url);
            return Result.success("修改成功");

        } catch (Exception e) {
            log.error("修改系统资源配置异常", e);
            return Result.error("修改系统资源配置失败，请稍后重试");
        }
    }

    @Override
    public Result getdataease() {
        DashboardResponseDTO result = new DashboardResponseDTO();
        Map<String, Object> userMap = userMapper.getUserStats();
        UserStatsDTO user = new UserStatsDTO();
        user.setTotalUsers(((Number) userMap.getOrDefault("totalUsers", 0)).intValue());
        user.setTodayUsers(((Number) userMap.getOrDefault("todayUsers", 0)).intValue());
        user.setUserGrowth(getDoubleValue(userMap, "userGrowth"));
        user.setTodayUserGrowth(getDoubleValue(userMap, "todayUserGrowth"));
        result.setUserData(user);
        Map<String, Object> videoMap = videoMapper.getVideoStats();
        VideoStatsDTO video = new VideoStatsDTO();
        video.setTotalVideos(((Number) videoMap.getOrDefault("totalVideos", 0)).intValue());
        video.setPassedVideos(((Number) videoMap.getOrDefault("passedVideos", 0)).intValue());
        video.setPendingVideos(((Number) videoMap.getOrDefault("pendingVideos", 0)).intValue());
        video.setTotalViews(((Number) videoMap.getOrDefault("totalViews", 0)).longValue());
        video.setPassRate(getDoubleValue(videoMap, "passRate"));
        video.setPendingRate(getDoubleValue(videoMap, "pendingRate"));
        result.setVideoData(video);
        Map<String, Object> weekMap = userMapper.getUserWeekGrowth();
        List<Integer> weekGrowth = Arrays.asList(
                ((Number) weekMap.getOrDefault("day1", 0)).intValue(),
                ((Number) weekMap.getOrDefault("day2", 0)).intValue(),
                ((Number) weekMap.getOrDefault("day3", 0)).intValue(),
                ((Number) weekMap.getOrDefault("day4", 0)).intValue(),
                ((Number) weekMap.getOrDefault("day5", 0)).intValue(),
                ((Number) weekMap.getOrDefault("day6", 0)).intValue(),
                ((Number) weekMap.getOrDefault("day7", 0)).intValue()
        );
        result.setUserWeekGrowthData(weekGrowth);
        List<String> monthList = Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月");
        List<Integer> userMonth = new ArrayList<>(Collections.nCopies(12, 0));
        List<Integer> videoMonth = new ArrayList<>(Collections.nCopies(12, 0));
        List<Integer> viewMonth = new ArrayList<>(Collections.nCopies(12, 0));

        // 用户月度数据
        for (Map<String, Object> map : userMapper.getUserMonthTrend()) {
            int month = Integer.parseInt(map.get("month").toString().split("-")[1]);
            userMonth.set(month - 1, ((Number) map.get("num")).intValue());
        }

        // 视频月度数据
        for (Map<String, Object> map : userMapper.getVideoMonthTrend()) {
            int month = Integer.parseInt(map.get("month").toString().split("-")[1]);
            videoMonth.set(month - 1, ((Number) map.get("num")).intValue());
        }

        // 播放量月度数据
        for (Map<String, Object> map : userMapper.getViewMonthTrend()) {
            int month = Integer.parseInt(map.get("month").toString().split("-")[1]);
            viewMonth.set(month - 1, ((Number) map.get("num")).intValue());
        }

        YearStatsDTO yearStats = new YearStatsDTO();
        yearStats.setMonths(monthList);
        yearStats.setUserGrowth(userMonth);
        yearStats.setVideoUpload(videoMonth);
        yearStats.setViewCount(viewMonth);
        result.setYearStatsData(yearStats);

        // ===================== 【真实】学历分布 =====================
        List<Map<String, Object>> eduList = userMapper.getEducationDistribution();
        List<PieItemDTO> educationData = new ArrayList<>();
        for (Map<String, Object> map : eduList) {
            PieItemDTO dto = new PieItemDTO();
            dto.setName((String) map.get("name"));
            dto.setValue(((Number) map.get("value")).intValue());
            educationData.add(dto);
        }
        result.setEducationData(educationData);

        // ===================== 【真实】语言分布 =====================
        List<Map<String, Object>> langList = userMapper.getLanguageDistribution();
        List<PieItemDTO> languageData = new ArrayList<>();
        for (Map<String, Object> map : langList) {
            PieItemDTO dto = new PieItemDTO();
            dto.setName((String) map.get("name"));
            dto.setValue(((Number) map.get("value")).intValue());
            languageData.add(dto);
        }
        result.setLanguageData(languageData);


        List<Map<String, Object>> typeMapList = videoMapper.getVideoTypeDistribution();
        List<VideoTypeStatsDTO> videoTypeList = new ArrayList<>();
        for (Map<String, Object> map : typeMapList) {
            VideoTypeStatsDTO dto = new VideoTypeStatsDTO();
            dto.setName((String) map.getOrDefault("name", "未知分类"));
            dto.setCount(((Number) map.getOrDefault("count", 0)).intValue());
            Number percentNum = (Number) map.getOrDefault("percent", 0);
            dto.setPercent(percentNum != null ? percentNum.intValue() : 0);
            videoTypeList.add(dto);
        }
        result.setVideoTypeData(videoTypeList);


        AiCommentStatsDTO aiComment = new AiCommentStatsDTO();

        Integer modelCount = videoMapper.getAiModelCount();
        aiComment.setModelCount(modelCount != null ? modelCount : 0);

        Integer commentCount = videoMapper.getTotalCommentCount();
        aiComment.setCommentCount(commentCount != null ? commentCount : 0);

        Integer fileCount = videoMapper.getTotalFileCount();
        aiComment.setTotalFileCount(fileCount != null ? fileCount : 0);

        result.setAiCommentData(aiComment);

        List<Map<String, Object>> aiUsageList = userMapper.getAiUsageDistribution();
        List<PieItemDTO> aiUsageData = new ArrayList<>();
        for (Map<String, Object> map : aiUsageList) {
            PieItemDTO dto = new PieItemDTO();
            dto.setName((String) map.getOrDefault("name", "未知模型"));
            dto.setValue(((Number) map.getOrDefault("value", 0)).intValue());
            aiUsageData.add(dto);
        }
        result.setAiUsageData(aiUsageData);

        return Result.success(result);
    }

    @Override
    public Result instermanager(String phone, String password, String nickname, String realname) {

        if (userMapper.checkphone(phone) > 0) {
            return Result.error("手机号已存在");
        }

        String id = UUID.randomUUID().toString();


        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        // 8位随机大写字母
        for (int i = 0; i < 8; i++) {
            char c = (char) (random.nextInt(26) + 'A');
            sb.append(c);
        }


        sb.append("_");

        for (int i = 0; i < 4; i++) {
            sb.append(random.nextInt(10));
        }

        String encode = sb.toString();

        // 密码加密
        String newpasswordpas = PasswordEncoder.encode(password);

        try {
            // 注意：insertmanager 方法要多加一个 encode 参数
            userMapper.insertmanager(id, phone, newpasswordpas, nickname, realname, encode);
            return Result.success();
        } catch (Exception e) {
            log.error("添加管理员失败", e);
            return Result.error();
        }
    }


    private String generateRandomEncode() {
        StringBuilder sb = new StringBuilder();

        // 1. 生成 8位 随机大写字母 A-Z
        for (int i = 0; i < 8; i++) {
            char c = (char) (RANDOM.nextInt(26) + 'A');
            sb.append(c);
        }

        // 2. 生成 4位 随机数字（自动补0，保证4位）
        for (int i = 0; i < 4; i++) {
            sb.append(RANDOM.nextInt(10));
        }

        return sb.toString();
    }

    // ===================== 工具方法：安全转数字，永不报错 =====================
    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object obj = map.get(key);
        if (obj == null) return 0.0;
        return ((Number) obj).doubleValue();
    }


    /**
     * 辅助方法：BigDecimal 空值安全处理
     */
    private BigDecimal getNotNullBigDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }


    /**
     * 上传转码后的视频到阿里云，并保存分辨率记录
     *
     * @param videoId 视频ID
     * @param type    480p/720p/1080p
     * @param width   宽
     * @param height  高
     */
    private void uploadAndSaveResolution(String videoId, String type, int width, int height) {
        try {
            // 本地转码后的文件路径 和VideoTranscodeUtil 一致
            String localPath = END_ADDRESS + videoId + "_" + type + ".mp4";
            File file = new File(localPath);

            if (!file.exists()) {
                return;
            }

            String ossUrl = aliOssUtil.uploadLocalVideo(file);

            // 保存到分辨率表（真实OSS地址）
            VideoResolutionDTO dto = new VideoResolutionDTO();
            dto.setId("R_" + UUID.randomUUID().toString());
            dto.setVideoId(videoId);
            dto.setResolution(type);
            dto.setVideoUrl(ossUrl); // 真实阿里云地址
            dto.setWidth(width);
            dto.setHeight(height);
            dto.setSize(file.length());
            dto.setFormat("mp4");

            managerMapper.insertVideoResolution(dto);

            // 上传完成后删除本地文件
            file.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}



