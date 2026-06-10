/*
 Navicat Premium Dump SQL

 Source Server         : mysql80
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : xsjf1

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 10/06/2026 14:21:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for accountstatus
-- ----------------------------
DROP TABLE IF EXISTS `accountstatus`;
CREATE TABLE `accountstatus`  (
  `AccountStatuId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号状态表主键',
  `AccountStatusType` int NOT NULL COMMENT '1禁言，2禁止发布视频3禁止开课4禁止购买会员5禁止购买课程6禁止添加好友7禁止登录',
  `StartTime` datetime NULL DEFAULT (now()) COMMENT '开始时间',
  `EndTime` datetime NULL DEFAULT ((now() + interval 1 day)) COMMENT '结束时间（当前时间+24小时）',
  `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `CreateTime` datetime NULL DEFAULT NULL,
  `UserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `IsRead` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`AccountStatuId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for aiconfig
-- ----------------------------
DROP TABLE IF EXISTS `aiconfig`;
CREATE TABLE `aiconfig`  (
  `AiConfigId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID（建议用字符串，兼容分布式ID）',
  `ModelCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模型唯一标识（如qwen-plus、qwen-turbo）',
  `ModelName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模型名称（给用户展示，如通义千问-增强版）',
  `ApiKey` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'API密钥（如sk-7d99f8b407354665816eab1507c68b19）',
  `BaseUrl` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'AI接口地址（如https://dashscope.aliyuncs.com/...）',
  `Model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模型参数（接口调用时传的model值，如qwen-plus）',
  `Status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态（1=启用，0=禁用）',
  `CreateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UpdateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `Introduction` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '模型简介',
  `Image` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT 'ai头像图片地址',
  PRIMARY KEY (`AiConfigId`) USING BTREE,
  UNIQUE INDEX `uk_model_code`(`ModelCode` ASC) USING BTREE COMMENT '模型标识唯一，避免重复配置'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI模型配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chatsession
-- ----------------------------
DROP TABLE IF EXISTS `chatsession`;
CREATE TABLE `chatsession`  (
  `ChatSessionId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话ID',
  `SessionCode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话唯一标识',
  `UserId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户1',
  `ToUserId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户2',
  `LastMessage` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后一条消息',
  `LastMessageTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后消息时间',
  `UnreadCount` int NULL DEFAULT 0 COMMENT '未读消息数',
  `CreateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `UserIsDelete` tinyint NULL DEFAULT 0 COMMENT 'UserId是否删除（0未删 1已删）',
  `ToUserIsDelete` tinyint NULL DEFAULT 0 COMMENT 'ToUserId是否删除（0未删 1已删）',
  `userLastMsgId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送方最后看到的消息ID',
  `toUserLastMsgId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接收方最后看到的消息ID',
  PRIMARY KEY (`ChatSessionId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '聊天会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for friendrelation
-- ----------------------------
DROP TABLE IF EXISTS `friendrelation`;
CREATE TABLE `friendrelation`  (
  `FriendRelationId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键',
  `UserId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID/用户唯一标识',
  `FriendId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '好友ID',
  `Status` tinyint NOT NULL DEFAULT 1 COMMENT '//1是同意 2 是删除 3是待确认，4是不同意，',
  `CreateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UpdateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `UserRemark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'UserId对FriendId的备注',
  `FriendRemark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'FriendId对UserId的备注',
  PRIMARY KEY (`FriendRelationId`) USING BTREE,
  UNIQUE INDEX `idx_user_friend`(`UserId` ASC, `FriendId` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '好友关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for homeresources
-- ----------------------------
DROP TABLE IF EXISTS `homeresources`;
CREATE TABLE `homeresources`  (
  `HomeResourcesId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '首页资源主键id',
  `Type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资源类型（如mp3/mp4/图片等）',
  `FileName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件名字',
  `Address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件存储的阿里云地址',
  `IsUse` tinyint NOT NULL DEFAULT 0 COMMENT '资源是否启用（0=禁用，1=启用）',
  `ResourceAddress` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源应该展示的位置（如首页轮播/音频区等）',
  `UploadPeople` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '上传人id',
  `UploadTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '资源上传时间（创建时自动填充）',
  `UpdateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '资源更新时间（修改时自动更新）',
  `JumUrl` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '要跳转的位置',
  `longer` int NULL DEFAULT (NULL) COMMENT '图片资源长度',
  `wide` int NULL DEFAULT (NULL) COMMENT '图片资源宽',
  PRIMARY KEY (`HomeResourcesId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '首页资源表（管理员维护浏览器展示资源）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for poolextractrule
-- ----------------------------
DROP TABLE IF EXISTS `poolextractrule`;
CREATE TABLE `poolextractrule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID，自增',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '视频分类（如：音乐、美术），通配符*表示所有分类',
  `pool_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '池类型（如：爆款池、长尾池、冷启动池）',
  `top_weight_layer_ratio` decimal(5, 4) NOT NULL DEFAULT 0.3000 COMMENT '上权重层分层占比（0~1）',
  `mid_weight_layer_ratio` decimal(5, 4) NOT NULL DEFAULT 0.5000 COMMENT '中权重层分层占比（0~1）',
  `bottom_weight_layer_ratio` decimal(5, 4) NOT NULL DEFAULT 0.2000 COMMENT '下权重层分层占比（0~1）',
  `top_weight_extract_ratio` decimal(5, 4) NOT NULL DEFAULT 0.5000 COMMENT '上权重层抽取占比（0~1）',
  `mid_weight_extract_ratio` decimal(5, 4) NOT NULL DEFAULT 0.3000 COMMENT '中权重层抽取占比（0~1）',
  `bottom_weight_extract_ratio` decimal(5, 4) NOT NULL DEFAULT 0.2000 COMMENT '下权重层抽取占比（0~1）',
  `layer_ratio_check` tinyint(1) GENERATED ALWAYS AS (if((((`top_weight_layer_ratio` + `mid_weight_layer_ratio`) + `bottom_weight_layer_ratio`) = 1),1,0)) STORED COMMENT '分层占比校验：1=总和=1（合法），0=不合法（无效配置）' NULL,
  `extract_ratio_check` tinyint(1) GENERATED ALWAYS AS (if((((`top_weight_extract_ratio` + `mid_weight_extract_ratio`) + `bottom_weight_extract_ratio`) = 1),1,0)) STORED COMMENT '抽取占比校验：1=总和=1（合法），0=不合法（无效配置）' NULL,
  `top_weight_min_threshold` int NOT NULL DEFAULT 0 COMMENT '上权重层最低权重阈值，0不限制',
  `mid_weight_min_threshold` int NOT NULL DEFAULT 0 COMMENT '中权重层最低权重阈值，0不限制',
  `bottom_weight_min_threshold` int NOT NULL DEFAULT 0 COMMENT '下权重层最低权重阈值，0不限制',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '生效状态：1=生效，0=失效',
  `version` int NOT NULL DEFAULT 1 COMMENT '配置版本号（乐观锁，避免并发修改冲突）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '配置备注（如：2026.01.15 音乐爆款池 3:5:2分层 5:3:2抽取）',
  `create_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'admin' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'admin' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_category_pool_type`(`category` ASC, `pool_type` ASC) USING BTREE COMMENT '避免同一分类+池类型重复配置',
  INDEX `idx_pool_type`(`pool_type` ASC) USING BTREE,
  INDEX `idx_enable_valid`(`enable` ASC, `layer_ratio_check` ASC, `extract_ratio_check` ASC) USING BTREE COMMENT '优化合法生效配置查询'
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '池内视频三层选取规则配置表（优化版）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for report
-- ----------------------------
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report`  (
  `Id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '举报主键ID(字符串)',
  `ReportType` int NOT NULL COMMENT '资源类型：01视频 02评论',
  `TargetId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '被举报资源ID(视频ID/评论ID)',
  `CreateUserId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '举报人ID',
  `TargetUserId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '被举报人ID',
  `Reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '举报原因',
  `Status` tinyint NULL DEFAULT 0 COMMENT '后台处理状态、 0 举报失败 1 举报成功 3 待处理',
  `UserIsRead` tinyint NULL DEFAULT 0 COMMENT '举报人：0未读 1已读',
  `TargetUserIsRead` tinyint NULL DEFAULT 0 COMMENT '被举报人：0未读 1已读',
  `CreateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报创建时间',
  `UpdateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `Remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细信息',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '举报总表(视频/评论合一)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sysscoreconfig
-- ----------------------------
DROP TABLE IF EXISTS `sysscoreconfig`;
CREATE TABLE `sysscoreconfig`  (
  `Id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID，自增',
  `Category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频分类（如：音乐、美术），通配符*表示所有分类',
  `FinishRateWeight` decimal(5, 2) NOT NULL COMMENT '完播率系数（0~100）',
  `LikeRateWeight` decimal(5, 2) NOT NULL COMMENT '点赞率系数（0~100）',
  `CommentRateWeight` decimal(5, 2) NOT NULL COMMENT '评论率系数（0~100）',
  `CollectRateWeight` decimal(5, 2) NOT NULL COMMENT '收藏率系数（0~100）',
  `ShareRateWeight` decimal(5, 2) NOT NULL COMMENT '分享率系数（0~100）',
  `TimeScoreWeight` decimal(5, 2) NOT NULL COMMENT '时间衰减系数（0~100）',
  `ScoreWindowHours` int NOT NULL DEFAULT 168 COMMENT '分数统计时间窗口（小时，默认7天=168h）',
  `TotalWeightCheck` tinyint(1) NOT NULL DEFAULT 0 COMMENT '总权重校验：1=总和=100（合法），0=不合法（无效）',
  `Enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '生效状态：1=生效，0=失效',
  `Version` int NOT NULL DEFAULT 1 COMMENT '配置版本号（乐观锁，避免并发修改冲突）',
  `Remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置备注（如：2026.01.15 编程类默认系数 40+25+15+10+10）',
  `CreateBy` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
  `CreateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UpdateBy` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '更新人',
  `UpdateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`Id`) USING BTREE,
  UNIQUE INDEX `uk_category_enable`(`Category` ASC, `Enable` ASC) USING BTREE COMMENT '同一分类同一生效状态唯一，避免同分类多套生效配置冲突',
  INDEX `idx_category`(`Category` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频100分制系数配置表（单条配置、带强校验、分类隔离）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `UserId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键（用户唯一标识）',
  `RealName` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `NickName` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `Phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号（注册账号，唯一）',
  `EnCode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '编码（自定义唯一标识）',
  `PassWord` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码（建议加密存储）',
  `Email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `Avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `Gender` int NULL DEFAULT NULL COMMENT '性别（0=未知，1=男，2=女）',
  `Birthday` datetime NULL DEFAULT NULL COMMENT '出生日期',
  `DeleteMark` int NOT NULL DEFAULT 0 COMMENT '删除标记（0=未删除，1=已删除）',
  `Status` int NOT NULL DEFAULT 1 COMMENT '账号状态（1=正常，2=禁用，3=存在异常状态）',
  `RoleBase` int NOT NULL DEFAULT 0 COMMENT '基础角色ID 角色身份表0 用户 1 管理员 2 超级管理员',
  `UiMember` int NOT NULL DEFAULT (0) COMMENT '是否存在会员0不存在 1 存在',
  `CreatTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（自动填充）',
  `UpdateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动更新）',
  `RegisterIp` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '注册ip地址',
  `UserTagWeight` json NULL COMMENT '存储用户的兴趣权重',
  `School` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '用户所在或毕业学校',
  `Leavel` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '用户学历水平',
  `Address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '用户地址',
  `Country` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '用户国家',
  `Language` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '中文' COMMENT '用户使用语言',
  `Age` int NULL DEFAULT (NULL) COMMENT '年龄',
  `AiTone` int NULL DEFAULT (1) COMMENT '用户ai界面选择的色系',
  `BackImage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户的背景图片',
  PRIMARY KEY (`UserId`) USING BTREE,
  UNIQUE INDEX `Phone`(`Phone` ASC) USING BTREE,
  UNIQUE INDEX `EnCode`(`EnCode` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for userandai
-- ----------------------------
DROP TABLE IF EXISTS `userandai`;
CREATE TABLE `userandai`  (
  `UserAndAi` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对话记录唯一标识（主键）',
  `UserId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关联用户表的用户ID',
  `ConvTitle` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '新对话' COMMENT '对话标题',
  `CreatTime` time NULL DEFAULT (curtime()) COMMENT '对话创建时间（仅时分秒）',
  `UpdateTime` time NULL DEFAULT (curtime()) COMMENT '最后消息更新时间',
  `IsDeleted` int NULL DEFAULT 1 COMMENT '删除状态（1=未删除，0=已删除）',
  `AiId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关联AI类型表的AI ID（仅业务关联，无外键约束）',
  PRIMARY KEY (`UserAndAi`) USING BTREE,
  INDEX `idx_user_deleted`(`UserId` ASC, `IsDeleted` ASC) USING BTREE,
  INDEX `idx_user_update`(`UserId` ASC, `UpdateTime` DESC) USING BTREE,
  INDEX `idx_ai_id`(`AiId` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户与AI对话会话表（关联MongoDB对话内容）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for userfollow
-- ----------------------------
DROP TABLE IF EXISTS `userfollow`;
CREATE TABLE `userfollow`  (
  `UserFollowId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户关注表主键',
  `UserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关注者ID',
  `FollowedUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '被关注者ID',
  `Status` int NOT NULL DEFAULT 0 COMMENT '1关注中 0取消关注',
  `StartTime` datetime NULL DEFAULT (now()) COMMENT '关注开始时间',
  `EndTime` datetime NULL DEFAULT NULL COMMENT '取消关注时间',
  `CreatTime` datetime NULL DEFAULT (now()) COMMENT '创建时间',
  PRIMARY KEY (`UserFollowId`) USING BTREE,
  UNIQUE INDEX `UK_User_Followed`(`UserId` ASC, `FollowedUserId` ASC) USING BTREE,
  INDEX `IDX_FollowedUserId`(`FollowedUserId` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for userreview
-- ----------------------------
DROP TABLE IF EXISTS `userreview`;
CREATE TABLE `userreview`  (
  `Id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID',
  `UserId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户Encode',
  `TargetType` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '审核类型：AVATAR-头像 BACKGROUND-背景 VIDEO-视频',
  `Status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态 0=待审核 1=通过 2=拒绝',
  `Content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注/拒绝原因',
  `IsRead` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读 0=未读 1=已读',
  `CreateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UpdateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `RecourseUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源地址',
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户审核记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for uservideointeract
-- ----------------------------
DROP TABLE IF EXISTS `uservideointeract`;
CREATE TABLE `uservideointeract`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID',
  `userId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `videoId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频ID',
  `isLike` tinyint NOT NULL DEFAULT 0 COMMENT '是否点赞 0-未点赞 1-已点赞',
  `isCollect` tinyint NOT NULL DEFAULT 0 COMMENT '是否收藏 0-未收藏 1-已收藏',
  `isComment` tinyint NOT NULL DEFAULT 0 COMMENT '是否评论 0-未评论 1-已评论',
  `isShare` tinyint NOT NULL DEFAULT 0 COMMENT '是否转发 0-未转发 1-已转发',
  `playPercent` int NOT NULL DEFAULT 0 COMMENT '播放进度（百分比 0-100）',
  `lastPosition` int NOT NULL DEFAULT 0 COMMENT '上次播放位置（秒）',
  `interactScore` int NOT NULL DEFAULT 0 COMMENT '互动总分（推荐算法用）',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `IsRead` int NOT NULL DEFAULT 0 COMMENT '是否已读',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ukUserVideo`(`userId` ASC, `videoId` ASC) USING BTREE COMMENT '唯一约束：一用户一视频仅一条记录',
  INDEX `idxVideoId`(`videoId` ASC) USING BTREE COMMENT '视频维度查询索引',
  INDEX `idxUserId`(`userId` ASC) USING BTREE COMMENT '用户维度查询索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户-视频互动状态表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for userviewhistory
-- ----------------------------
DROP TABLE IF EXISTS `userviewhistory`;
CREATE TABLE `userviewhistory`  (
  `id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID',
  `userId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `videoId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频ID',
  `watchTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '观看时间',
  `duration` int NOT NULL DEFAULT 0 COMMENT '本次观看时长（秒）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idxUserTime`(`userId` ASC, `watchTime` ASC) USING BTREE COMMENT '用户+时间索引（历史列表核心查询）',
  INDEX `idxVideoId`(`videoId` ASC) USING BTREE COMMENT '视频维度统计索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户视频浏览历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for video
-- ----------------------------
DROP TABLE IF EXISTS `video`;
CREATE TABLE `video`  (
  `VideoId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频主键Id',
  `EnCode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频唯一编码',
  `VideoName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频名称',
  `VideoImageUrl` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频封面地址',
  `VideoUrl` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频地址',
  `PublisherId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发布人Id',
  `ReviewStatus` int NOT NULL DEFAULT (0) COMMENT '审核状态',
  `IsCollection` int NULL DEFAULT (NULL) COMMENT '是否属于集合',
  `CollectionId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '所属集合id',
  `VideoResolution` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '视频分辨率',
  `VideoTime` decimal(10, 2) NULL DEFAULT NULL COMMENT '视频时长（单位：分钟，保留2位小数）',
  `VideoSize` int NULL DEFAULT (NULL) COMMENT '视频文件大小',
  `VideoFormat` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '视频格式',
  `ReviewerId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '审核人代码',
  `ReviewerRemark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '审核人备注',
  `SortWeight` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (50) COMMENT '排序权重，大于50推荐小于不推荐',
  `ViewCount` int NOT NULL DEFAULT (0) COMMENT '播放量',
  `LikeCount` int NOT NULL DEFAULT (0) COMMENT '点赞',
  `CommentCount` int NOT NULL DEFAULT (0) COMMENT '评论',
  `ShareCount` int NOT NULL DEFAULT (0) COMMENT '分享',
  `CollectCount` int NOT NULL DEFAULT (0) COMMENT '收藏',
  `IsFeature` int NULL DEFAULT (0) COMMENT '是否精选',
  `CreateTime` datetime NOT NULL DEFAULT (now()) COMMENT '创建时间',
  `UpdateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动更新）',
  `IsDelete` int NULL DEFAULT (0) COMMENT '是否删除',
  `VideoStatus` int NULL DEFAULT (0) COMMENT '视频状态',
  `IsMember` int(1) UNSIGNED ZEROFILL NULL DEFAULT (0) COMMENT '是否需要会员',
  `IsSelect` int NULL DEFAULT (0) COMMENT '是否精选',
  `GlobalBaseWeight` decimal(8, 2) NULL DEFAULT NULL COMMENT '视频全局权重',
  `ColdStartData` json NULL COMMENT '冷启动核心数据',
  `CurrentPoolType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '_utf8mb4\\\'\\\'' COMMENT '视频当前归属池',
  `VideoSort` int NULL DEFAULT (NULL) COMMENT '再集合中的排序',
  `Introduction` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '视频简介',
  PRIMARY KEY (`VideoId` DESC) USING BTREE,
  UNIQUE INDEX `EnCode`(`EnCode` ASC) USING BTREE,
  UNIQUE INDEX `EnCode_2`(`EnCode` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for videoandpoolcofig
-- ----------------------------
DROP TABLE IF EXISTS `videoandpoolcofig`;
CREATE TABLE `videoandpoolcofig`  (
  `VideoAndPoolId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频与池关联表',
  `VideoId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频Id',
  `PoolId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '池Id',
  `JoinPoolTime` datetime NULL DEFAULT (now()) COMMENT '入池时间',
  `LeavePoolTime` datetime NULL DEFAULT (NULL) COMMENT '出池时间',
  `JoinReason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '入池原因',
  `LeaveReason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '出池原因',
  `Status` int NULL DEFAULT 1 COMMENT '状态是否生效这条',
  PRIMARY KEY (`VideoAndPoolId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for videoandtype
-- ----------------------------
DROP TABLE IF EXISTS `videoandtype`;
CREATE TABLE `videoandtype`  (
  `VideoAndTypeId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频与类型关联表',
  `VideoId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频表Id',
  `TypeId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型Id',
  `CreateTime` datetime NULL DEFAULT (now()) COMMENT '创建时间',
  `UpdateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`VideoAndTypeId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for videoaudittag
-- ----------------------------
DROP TABLE IF EXISTS `videoaudittag`;
CREATE TABLE `videoaudittag`  (
  `AuditTagId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签主键ID（建议用UUID）',
  `TagName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称（如：暴力、低俗、广告、合规）',
  `TagCode` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签编码（如：VIOLENCE、LOWBROW）',
  `TagType` int NOT NULL DEFAULT 1 COMMENT '标签类型：1-违禁标签 2-合规标签 3-人工复核标签',
  `IsDelete` int NOT NULL DEFAULT 0 COMMENT '软删除：0-未删 1-已删',
  `CreateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UpdateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`AuditTagId`) USING BTREE,
  UNIQUE INDEX `TagCode`(`TagCode` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频审核标签主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for videocomment
-- ----------------------------
DROP TABLE IF EXISTS `videocomment`;
CREATE TABLE `videocomment`  (
  `VideoCommentId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论ID（主键）',
  `VideoId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关联视频ID',
  `UserId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论发布者ID',
  `ParentId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '父评论ID，空字符串表示根评论（直接评论视频）',
  `Content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容（扩容到2000字符，满足长评论需求）',
  `LikeCount` int NOT NULL DEFAULT 0 COMMENT '点赞数，默认0',
  `Status` int NOT NULL DEFAULT 1 COMMENT '评论状态：1-正常，2-屏蔽，3-删除',
  `CreateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论创建时间（改用DATETIME，记录完整时间）',
  `IsRead` int NOT NULL DEFAULT 0 COMMENT '用户是否已读',
  PRIMARY KEY (`VideoCommentId`) USING BTREE,
  INDEX `idx_video_id`(`VideoId` ASC) USING BTREE,
  INDEX `idx_user_id`(`UserId` ASC) USING BTREE,
  INDEX `idx_parent_id`(`ParentId` ASC) USING BTREE,
  INDEX `idx_create_time`(`CreateTime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for videogroup
-- ----------------------------
DROP TABLE IF EXISTS `videogroup`;
CREATE TABLE `videogroup`  (
  `groupId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '系列组ID（主键），格式：GROUP_ART_B001_001',
  `groupName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '系列组名称，如：「二次元场景绘制全教程」',
  `groupDesc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '系列组描述，说明该系列的核心内容',
  `groupCoverUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '系列组封面图URL',
  `groupStatus` tinyint NOT NULL DEFAULT 0 COMMENT '系列状态：0-正常 1-禁用 2-删除',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `createUser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人（关联用户表EnCode）',
  `updateUser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人（关联用户表EnCode）',
  PRIMARY KEY (`groupId`) USING BTREE,
  INDEX `idx_groupName`(`groupName` ASC) USING BTREE,
  INDEX `idx_groupStatus`(`groupStatus` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频系列/组表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for videopending
-- ----------------------------
DROP TABLE IF EXISTS `videopending`;
CREATE TABLE `videopending`  (
  `VideoPendingId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键id',
  `EnCode` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频编码',
  `VideoName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频名称/标题',
  `VideoDesc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频描述简介',
  `VideoImageUrl` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频默认地址',
  `VideoUrl` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频地址',
  `VideoTypeId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联视频分类ID（对应videotype表VideoTypeId）',
  `PublisherId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发布人id',
  `PublishCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发布人代码',
  `ReviewStatus` int NOT NULL DEFAULT (0) COMMENT '012步骤向后走 等待进行完成',
  `PublishTime` datetime NOT NULL DEFAULT (now()) COMMENT '上传时间',
  `VideoTime` int NULL DEFAULT (NULL) COMMENT '视频时长',
  `VideoSize` int NULL DEFAULT (NULL) COMMENT '视频文件大小',
  `VideoFormat` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '视频格式',
  `IsCollection` int NULL DEFAULT (NULL) COMMENT '是否属于集合',
  `CollectionId` int NULL DEFAULT (NULL) COMMENT '所属集合id',
  `VideoStatus` int NOT NULL DEFAULT (2) COMMENT '通过1，违禁0，等待结果2',
  `AuditTime` datetime NULL DEFAULT (NULL) COMMENT '开始审核时间',
  `EndAuditTime` datetime NULL DEFAULT (NULL) COMMENT '审核结束时间',
  `IsDelete` int NOT NULL DEFAULT (0) COMMENT '软删除删除1 不删除0',
  `ReviewerCode` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '审核人代码',
  `ReviewerId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT (NULL) COMMENT '审核人id',
  `CreateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UpdateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动更新）',
  PRIMARY KEY (`VideoPendingId`) USING BTREE,
  UNIQUE INDEX `EnCode`(`EnCode` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频表的前置表--不通过外键关联' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for videopendingandaudittag
-- ----------------------------
DROP TABLE IF EXISTS `videopendingandaudittag`;
CREATE TABLE `videopendingandaudittag`  (
  `VideoPendingAndAuditTagId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关联表主键',
  `AuditTagId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型表主键',
  `VideoPendingId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '临时表主键',
  PRIMARY KEY (`VideoPendingAndAuditTagId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '临时视频表与审核结果类型表的中间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for videopoolconfig
-- ----------------------------
DROP TABLE IF EXISTS `videopoolconfig`;
CREATE TABLE `videopoolconfig`  (
  `VideoPoolConfigId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频池主键配置类主键id',
  `PoolCaetGory` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '池所属品类',
  `PoolType` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '池形',
  `PoolFullName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '池全称',
  `EntryRules` json NOT NULL COMMENT '入池规则',
  `TrafficRatio` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '该池流量分配比例',
  `PoolStatus` int NULL DEFAULT (0) COMMENT '池状态',
  `CreateTime` datetime NULL DEFAULT (now()) COMMENT '配置创建时间',
  `UpdateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `PoolCode` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '池代码',
  `GetPoolMount` decimal(5, 2) NULL DEFAULT 25.00 COMMENT '配置选取规则，从这个池中拿百分之多少的数据',
  PRIMARY KEY (`VideoPoolConfigId`) USING BTREE,
  UNIQUE INDEX `PoolCode`(`PoolCode` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频值配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for videoresolution
-- ----------------------------
DROP TABLE IF EXISTS `videoresolution`;
CREATE TABLE `videoresolution`  (
  `Id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID',
  `VideoId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关联Video表的VideoId',
  `Resolution` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分辨率 360P 480P 720P',
  `VideoUrl` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '该清晰度视频地址',
  `Width` int NULL DEFAULT NULL COMMENT '视频宽度',
  `Height` int NULL DEFAULT NULL COMMENT '视频高度',
  `Bitrate` bigint NULL DEFAULT NULL COMMENT '码率',
  `Size` bigint NULL DEFAULT NULL COMMENT '文件大小字节',
  `Format` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'mp4' COMMENT '视频格式',
  `CreateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `Duration` int NULL DEFAULT 0 COMMENT '时长',
  PRIMARY KEY (`Id`) USING BTREE,
  INDEX `idx_VideoId`(`VideoId` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频分辨率表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for videoresources
-- ----------------------------
DROP TABLE IF EXISTS `videoresources`;
CREATE TABLE `videoresources`  (
  `VideoResourcesId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源主键ID（建议用UUID或雪花ID）',
  `VideoId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关联视频表的主键ID',
  `ResourceName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源名称（如：Java核心知识点.pdf）',
  `ResourceType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源类型（枚举：DOCUMENT/CODE/MEDIA/OTHER）',
  `ResourceFormat` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '文件格式（如：pdf、java、png、mp4）',
  `ResourceUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源存储地址（云存储URL/本地路径）',
  `ResourceThumUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '资源封面/缩略图Url（仅图片/PDF可用）',
  `ResourceSize` bigint NULL DEFAULT 0 COMMENT '资源大小（字节）',
  `DownloadCount` int NULL DEFAULT 0 COMMENT '资源下载数量',
  `UploadTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间（修正拼写+优化为DATETIME类型）',
  `UserId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '上传人ID（视频发布人/文件上传人）',
  `IsDelete` tinyint NULL DEFAULT 0 COMMENT '是否删除（0-未删，1-已删）',
  `IsPublic` tinyint NULL DEFAULT 1 COMMENT '是否公开（0-仅管理员可见，1-所有人可见）',
  `ResourceDesc` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '文件描述（资源说明）',
  PRIMARY KEY (`VideoResourcesId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频配套教学资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for videotype
-- ----------------------------
DROP TABLE IF EXISTS `videotype`;
CREATE TABLE `videotype`  (
  `VideoTypeId` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键id',
  `TypeCode` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '代码',
  `TypeName` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型名称',
  `CreateTime` datetime NULL DEFAULT (now()) COMMENT '创建时间',
  `UpdateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `PoolCaetGory` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '池所属品类',
  PRIMARY KEY (`VideoTypeId`) USING BTREE,
  UNIQUE INDEX `TypeCode`(`TypeCode` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
