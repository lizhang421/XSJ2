<template>
  <div class="video-page">
    <!-- 顶部导航栏 -->
    <header class="video-header" :style="{ backgroundImage: `url(${headerBgImage})` }">
      <div class="header-inner">
        <!-- 左侧Logo区域 -->
        <div class="header-left">
          <div class="navbar-brand" @click="goBack">
            <h1 class="brand-logo">XSJ</h1>
            <div class="brand-name">
              <h2>{{ locale === 'en' ? 'New World AI Education' : t('brand.name') }}</h2>
              <p>{{ locale === 'en' ? 'AI-driven Education' : t('brand.englishName') }}</p>
            </div>
          </div>
        </div>
        
        <!-- 中央导航区域 -->
        <div class="header-center">
          <nav class="nav-menu">
            <a class="nav-item" :class="{ active: currentTab === 'home' }" @click="goToFirst">
              <span class="nav-icon">🏠</span>
              <span class="nav-text">{{ t('nav.home') }}</span>
            </a>
            <a class="nav-item" :class="{ active: currentTab === 'ai' }" @click="goToAI">
              <span class="nav-icon">🤖</span>
              <span class="nav-text">AI</span>
            </a>
          </nav>
          
          <!-- 搜索框 -->
          <div class="search-box" :class="{ 'show-history': showSearchHistory }">
            <input 
              type="text" 
              v-model="searchKeyword"
              :placeholder="t('video.searchPlaceholder')"
              class="search-input"
              @focus="showSearchHistory = true"
              @blur="hideSearchHistory"
              @keyup.enter="handleSearch"
            />
            <!-- 历史搜索记录 -->
            <div class="search-history" v-if="showSearchHistory && searchHistory.length > 0">
              <div class="history-header">
                <span>{{ t('video.searchHistory') || '搜索历史' }}</span>
                <span class="clear-history" @click="clearSearchHistory">{{ t('video.clearHistory') || '清空' }}</span>
              </div>
              <div class="history-list">
                <div 
                  class="history-item" 
                  v-for="(item, index) in searchHistory" 
                  :key="index"
                  @click="selectHistory(item)"
                >
                  <el-icon><el-icon-time /></el-icon>
                  <span>{{ item }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 右侧功能区 -->
        <div class="header-right">
          <div class="header-actions">
            <div class="action-item upload-btn" @click="handleUpload">
              <span class="action-icon">📤</span>
              <span class="action-text">{{ t('video.upload') }}</span>
            </div>
            <div class="action-item message-btn" @click="handleMessage">
              <span class="action-icon">💬</span>
              <span class="action-text">{{ t('video.message') }}</span>
            </div>
            <div class="action-item history-btn" @click="handleHistory">
              <span class="action-icon">⏱️</span>
              <span class="action-text">{{ t('video.history') }}</span>
            </div>
            <div class="action-item favorite-btn" @click="handleFavorite">
              <span class="action-icon">⭐</span>
              <span class="action-text">{{ t('video.favorite') }}</span>
            </div>
          </div>
          
          <!-- 语言切换 -->
          <div class="lang-switch">
            <el-dropdown trigger="click" @command="changeLocale" placement="bottom-end">
              <div class="lang-btn">
                <el-icon><el-icon-globe /></el-icon>
                <span class="lang-text">{{ currentLangText }}</span>
                <el-icon class="dropdown-arrow"><el-icon-arrow-down /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu class="lang-dropdown-menu">
                  <el-dropdown-item command="zh" :class="{ active: locale === 'zh' }">
                    <el-icon v-if="locale === 'zh'"><el-icon-check /></el-icon>
                    <span>🇨🇳 中文</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="en" :class="{ active: locale === 'en' }">
                    <el-icon v-if="locale === 'en'"><el-icon-check /></el-icon>
                    <span>🇺🇸 English</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="ja" :class="{ active: locale === 'ja' }">
                    <el-icon v-if="locale === 'ja'"><el-icon-check /></el-icon>
                    <span>🇯🇵 日本語</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          
          <!-- 用户头像 -->
          <div class="user-avatar-wrapper" @click="handleAvatarClick">
            <img :src="userInfoStore.info.avatar || 'https://picsum.photos/100/100?random=1'" :alt="userInfoStore.info.nickName || '用户头像'" class="user-avatar" />
          </div>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <div class="video-container" v-loading="loading" :element-loading-text="t('loginRegister.loading')">
      <!-- 左侧视频播放区域 -->
      <div class="video-main" v-if="!loading && videoData.encode">
        <!-- 视频播放器 -->
        <div class="video-player-wrapper">
          <div class="video-player">
            <video 
              ref="videoPlayer"
              :src="cleanUrl(videoData.videoUrl)"
              :poster="cleanUrl(videoData.thumbnail)"
              class="player"
              @error="handleVideoError"
              @canplay="handleVideoCanPlay"
              @play="handleVideoPlay"
              @pause="handleVideoPause"
              @ended="handleVideoEnded"
              @timeupdate="handleVideoTimeUpdate"
              @waiting="handleVideoWaiting"
              @stalled="handleVideoStalled"
            >
              {{ t('video.browserNotSupport') }}
            </video>
            
            <div v-if="videoError" class="video-error-overlay">
              <div class="error-content">
                <img src="https://picsum.photos/300/200?random=anime" alt="视频加载失败" class="error-image" />
                <div class="error-text">
                  <h3>{{ t('video.loadFailed') }}</h3>
                  <p>{{ t('video.loadFailedTip') }}</p>
                </div>
              </div>
            </div>
            
            <!-- 自定义视频控制栏 -->
            <div class="custom-video-controls">
              <!-- 进度条 -->
              <div class="progress-bar-container">
                <div class="progress-bar" @click="seekVideo">
                  <div class="progress-buffered" :style="{ width: bufferedPercent + '%' }"></div>
                  <div class="progress-played" :style="{ width: progressPercent + '%' }"></div>
                  <div class="progress-thumb" :style="{ left: progressPercent + '%' }"></div>
                </div>
              </div>
              
              <!-- 控制按钮 -->
              <div class="controls-row">
                <div class="controls-left">
                  <!-- 播放/暂停 -->
                  <button class="control-btn" @click="togglePlay">
                    <span v-if="isPlaying">⏸️</span>
                    <span v-else>▶️</span>
                  </button>
                  
                  <!-- 音量控制 -->
                  <div class="volume-control">
                    <button class="control-btn" @click="toggleMute">
                      <span v-if="isMuted">🔇</span>
                      <span v-else>🔊</span>
                    </button>
                    <input 
                      type="range" 
                      min="0" 
                      max="100" 
                      :value="volume * 100"
                      @input="changeVolume"
                      class="volume-slider"
                    />
                  </div>
                  
                  <!-- 时间显示 -->
                  <span class="time-display">
                    {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
                  </span>
                </div>
                
                <div class="controls-right">
                  <!-- 分辨率选择器 -->
                  <div v-if="resolutionList.length > 0" class="resolution-control">
                    <button class="control-btn resolution-toggle" @click="showResolutionMenu = !showResolutionMenu">
                      <span class="quality-icon">🎬</span>
                      <span class="quality-text">{{ currentResolution }}</span>
                    </button>
                    
                    <div v-if="showResolutionMenu" class="resolution-dropdown">
                      <div class="dropdown-header">画质选择</div>
                      <div 
                        v-for="item in resolutionList" 
                        :key="item.resolution"
                        class="dropdown-item"
                        :class="{ active: currentResolution === item.resolution }"
                        @click="changeResolution(item.resolution)"
                      >
                        <span class="item-label">{{ item.resolution }}</span>
                        <span v-if="item.size" class="item-size">{{ formatFileSize(item.size) }}</span>
                      </div>
                    </div>
                  </div>
                  
                  <!-- 全屏按钮 -->
                  <button class="control-btn" @click="toggleFullscreen">
                    <span v-if="isFullscreen">⛶</span>
                    <span v-else>⛶</span>
                  </button>
                </div>
              </div>
            </div>
            
            <!-- 卡顿提示 -->
            <div v-if="showBufferingTip" class="buffering-tip">
              <div class="tip-content">
                <span class="tip-icon">⚠️</span>
                <span class="tip-text">视频加载较慢，建议切换到较低画质</span>
                <button class="tip-btn" @click="switchToLowerQuality">切换画质</button>
                <button class="tip-close" @click="showBufferingTip = false">×</button>
              </div>
            </div>
          </div>
        </div>

        <!-- 视频信息 -->
        <div class="video-info">
          <h1 class="video-title">{{ videoData.title }}</h1>
          
          <!-- 视频统计 -->
          <div class="video-stats-wrapper">
            <div class="stats-decor-left">📊</div>
            <div class="video-stats">
              <div class="stat-item">
                <span class="stat-emoji">👁️</span>
                <span class="stat-value">{{ formatNumber(videoData.views || 0) }}</span>
                <span class="stat-label">{{ t('video.views') }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-emoji">💬</span>
                <span class="stat-value">{{ formatNumber(videoData.comments || 0) }}</span>
                <span class="stat-label">{{ t('video.comments') }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-emoji">⏱️</span>
                <span class="stat-value">{{ videoData.duration }}</span>
                <span class="stat-label">{{ t('video.duration') }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-emoji">👤</span>
                <span class="stat-value">{{ videoData.author }}</span>
                <span class="stat-label">{{ t('video.author') }}</span>
              </div>
            </div>
            <div class="stats-decor-right">✨</div>
          </div>

          <!-- 操作按钮 -->
          <div class="video-actions-wrapper">
            <div class="actions-decor">🎮</div>
            <div class="video-actions">
            <button class="action-btn-cartoon" :class="{ active: isLiked }" @click="handleLike">
              <div class="btn-icon">
                <span class="icon-emoji">{{ isLiked ? '❤️' : '🤍' }}</span>
              </div>
              <div class="btn-content">
                <span class="btn-label">{{ t('video.like') }}</span>
                <span class="btn-count">{{ formatNumber(videoData.likeCount || 0) }}</span>
              </div>
              <div class="btn-particles" v-if="isLiked">
                <span class="particle"></span>
                <span class="particle"></span>
                <span class="particle"></span>
                <span class="particle"></span>
                <span class="particle"></span>
              </div>
            </button>
            
            <button class="action-btn-cartoon collect-btn" :class="{ active: isCollected }" @click="handleCollect">
              <div class="btn-icon">
                <span class="icon-emoji">{{ isCollected ? '⭐' : '☆' }}</span>
              </div>
              <div class="btn-content">
                <span class="btn-label">{{ t('video.collect') }}</span>
                <span class="btn-count">{{ formatNumber(videoData.collectCount || 0) }}</span>
              </div>
              <div class="btn-particles" v-if="isCollected">
                <span class="particle star"></span>
                <span class="particle star"></span>
                <span class="particle star"></span>
              </div>
            </button>
            
            <button class="action-btn-cartoon share-btn" :class="{ active: isShared }" @click="handleShare">
              <div class="btn-icon">
                <span class="icon-emoji">🔗</span>
              </div>
              <div class="btn-content">
                <span class="btn-label">{{ t('video.share') }}</span>
                <span class="btn-count">{{ formatNumber(videoData.shareCount || 0) }}</span>
              </div>
              <div class="btn-particles" v-if="isShared">
                <span class="particle link"></span>
                <span class="particle link"></span>
              </div>
            </button>
            
            <button class="action-btn-cartoon report-btn" @click="reportVideo">
              <div class="btn-icon">
                <span class="icon-emoji">⚠️</span>
              </div>
              <div class="btn-content">
                <span class="btn-label">举报</span>
              </div>
            </button>
          </div>
          </div>

          <!-- 视频描述 -->
          <div class="video-description">
            <h3>{{ t('video.description') }}</h3>
            <p>{{ videoData.introduction || t('video.noIntroduction') }}</p>
          </div>
        </div>

        <!-- 评论区 -->
        <div class="comment-section">
          <h2 class="section-title">
            <el-icon><el-icon-chat-dot-round /></el-icon>
            {{ t('video.comments') }}
          </h2>
          
          <!-- 评论输入 -->
          <div class="comment-input-wrapper">
            <div class="comment-input-header">
              <span class="input-label">{{ t('video.writeComment') || '发表评论' }}</span>
            </div>
            <el-input
              v-model="commentText"
              type="textarea"
              :rows="3"
              :placeholder="t('video.commentPlaceholder')"
              class="comment-textarea"
              maxlength="500"
              show-word-limit
            />
            <div class="comment-input-actions">
              <span class="char-count">{{ commentText.length }}/500</span>
              <el-button type="primary" @click="submitComment" :loading="submittingComment" class="submit-btn">
                <el-icon><el-icon-promotion /></el-icon>
                {{ t('video.submit') }}
              </el-button>
            </div>
          </div>

          <!-- 评论列表 -->
          <div class="comment-list">
            <div class="comment-item" v-for="comment in comments" :key="comment.videoCommentId">
              <div class="comment-avatar">
                <img :src="getAvatarUrl(comment.avatar, comment.commentNickname)" :alt="comment.commentNickname" />
              </div>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-username">{{ comment.commentNickname }}</span>
                  <span class="comment-time">{{ formatCommentTime(comment.createTime) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <div class="comment-actions">
                  <el-button text size="small" @click="showReplyInput(comment)" class="action-btn">
                    <el-icon><el-icon-chat-dot-round /></el-icon>
                    <span>{{ t('video.reply') }}</span>
                  </el-button>
                  <el-button text size="small" @click="reportComment(comment)" class="action-btn">
                    <el-icon><el-icon-warning /></el-icon>
                    <span>举报</span>
                  </el-button>
                </div>
                
                <!-- 回复输入框 -->
                <div class="reply-input-wrapper" v-if="replyingTo && replyingTo.videoCommentId === comment.videoCommentId">
                  <el-input
                    v-model="replyText"
                    type="textarea"
                    :rows="2"
                    :placeholder="t('video.replyPlaceholder') || '写下你的回复...'"
                    class="reply-textarea"
                    maxlength="300"
                    show-word-limit
                  />
                  <div class="reply-input-actions">
                    <el-button size="small" @click="cancelReply">{{ t('video.cancel') || '取消' }}</el-button>
                    <el-button type="primary" size="small" @click="submitReply(comment)" :loading="submittingReply">
                      {{ t('video.reply') }}
                    </el-button>
                  </div>
                </div>
                
                <!-- 子评论列表 -->
                <div class="reply-list" v-if="comment.replyList && comment.replyList.length > 0">
                  <div class="reply-item" v-for="reply in comment.replyList" :key="reply.videoCommentId">
                    <div class="reply-avatar">
                      <img :src="getAvatarUrl(reply.avatar, reply.commentNickname)" :alt="reply.commentNickname" />
                    </div>
                    <div class="reply-content">
                      <div class="reply-header">
                        <span class="reply-username">{{ reply.commentNickname }}</span>
                        <span class="reply-to" v-if="reply.parentCommentNickname">
                          <el-icon><el-icon-right /></el-icon>
                          <span class="reply-to-name">{{ reply.parentCommentNickname }}</span>
                        </span>
                        <span class="reply-time">{{ formatCommentTime(reply.createTime) }}</span>
                      </div>
                      <p class="reply-text">{{ reply.content }}</p>
                      <div class="reply-actions">
                        <el-button text size="small" @click="showReplyInput(reply, comment)" class="action-btn">
                          <el-icon><el-icon-chat-dot-round /></el-icon>
                          <span>{{ t('video.reply') }}</span>
                        </el-button>
                        <el-button text size="small" @click="reportComment(reply)" class="action-btn">
                          <el-icon><el-icon-warning /></el-icon>
                          <span>举报</span>
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧推荐视频列表 -->
      <aside class="video-sidebar">
        <div class="flip-container" :class="{ 'flipped': isFlipped }">
          <div class="flip-card">
            <div class="flip-front recommend-section">
              <div class="author-videos-header">
                <div class="author-videos-left">
                  <el-icon class="author-videos-icon"><el-icon-user /></el-icon>
                  <h4>{{ authorSectionTitle }}</h4>
                </div>
              </div>
              
              <div class="author-info-card" @click="goToUserProfile">
                <div class="author-avatar-wrapper">
                  <img :src="getAvatarUrl(publisherInfo.avatar, publisherInfo.nickName)" alt="作者头像" class="author-avatar" />
                  <div class="member-badge" v-if="publisherInfo.uiMember === '1'">
                    <span>VIP</span>
                  </div>
                </div>
                <div class="author-details">
                  <div class="author-name-row">
                    <span class="author-name">{{ publisherInfo.nickName || '发布人' }}</span>
                    <span class="member-tag" v-if="publisherInfo.uiMember === '1'">👑</span>
                  </div>
                  <div class="author-meta">
                    <span class="meta-item" v-if="publisherInfo.school">{{ publisherInfo.school }}</span>
                    <span class="meta-item" v-if="publisherInfo.country">{{ publisherInfo.country }}</span>
                    <span class="meta-item">{{ authorVideos.length }} {{ t('video.videosCount') || '个视频' }}</span>
                  </div>
                </div>
              </div>
              
              <div class="recommend-list">
                <div 
                  class="recommend-item" 
                  v-for="video in authorVideos" 
                  :key="video.id"
                  @click="playRecommendVideo(video)"
                >
                  <div class="recommend-thumbnail">
                    <img :src="video.thumbnail" :alt="video.title" />
                    <div class="recommend-duration">{{ video.duration }}</div>
                  </div>
                  <div class="recommend-info">
                    <h4 class="recommend-title">{{ video.title }}</h4>
                    <div class="recommend-meta">
                      <span class="meta-item">
                        <el-icon><el-icon-view /></el-icon>
                        {{ formatNumber(video.views) }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="flip-action-wrapper">
                <button class="flip-action-btn" @click="toggleFlip">
                  <div class="btn-images">
                    <img :src="authorBtnSquareImage" alt="" class="btn-img-square" />
                  </div>
                  <div class="btn-content">
                    <span class="btn-title">{{ t('video.viewFiles') }}</span>
                    <span class="btn-subtitle">{{ t('video.viewFilesSubtitle') }}</span>
                  </div>
                  <div class="btn-arrow">
                    <img :src="authorBtnCircleImage" alt="" class="btn-img-circle" />
                  </div>
                </button>
              </div>
            </div>
            
            <div class="flip-back files-section">
              <div class="files-header">
                <div class="files-header-left">
                  <el-icon class="files-icon"><el-icon-folder /></el-icon>
                  <h4>{{ t('video.relatedFiles') }}</h4>
                </div>
              </div>
              
              <div class="files-grid" v-if="relatedFiles && relatedFiles.length > 0">
                <div 
                  class="file-card" 
                  :class="['file-type-' + file.resourceFormat]"
                  v-for="(file, index) in relatedFiles" 
                  :key="index"
                  @click="handleFileClick(file)"
                >
                  <div class="file-icon" :class="'icon-' + file.resourceFormat">
                    <span class="file-icon-emoji">{{ getFileEmoji(file.resourceFormat) }}</span>
                  </div>
                  <div class="file-info">
                    <span class="file-name">{{ file.resourceName }}</span>
                    <span class="file-size">{{ formatFileSize(file.resourceSize) }}</span>
                  </div>
                </div>
              </div>
              
              <div class="files-empty" v-else>
                <div class="empty-icon">📭</div>
                <p class="empty-text">{{ t('video.noFiles') }}</p>
              </div>
              
              <div class="flip-action-wrapper">
                <button class="flip-action-btn back-btn" @click="toggleFlip">
                  <div class="btn-images">
                    <img :src="authorBtnSquareImage" alt="" class="btn-img-square" />
                  </div>
                  <div class="btn-content">
                    <span class="btn-title">{{ t('video.viewRecommend') }}</span>
                    <span class="btn-subtitle">{{ t('video.viewRecommendSubtitle') }}</span>
                  </div>
                  <div class="btn-arrow">
                    <img :src="authorBtnCircleImage" alt="" class="btn-img-circle" />
                  </div>
                </button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 推荐视频区域 -->
        <div class="sidebar-recommend" v-if="showFloatingRecommend">
          <div class="sidebar-recommend-header">
            <div class="recommend-header-left">
              <el-icon class="recommend-icon"><el-icon-video-play /></el-icon>
              <h4>{{ t('video.recommend') }}</h4>
            </div>
            <div class="recommend-header-right">
              <el-button text size="small" @click="refreshRecommendVideos" :loading="recommendLoading" class="header-action-btn">
                <el-icon><el-icon-refresh /></el-icon>
              </el-button>
              <el-button text size="small" @click="showFloatingRecommend = false" class="header-action-btn">
                <el-icon><el-icon-close /></el-icon>
              </el-button>
              <img :src="recommendHeaderImage" alt="" class="recommend-header-img" />
            </div>
          </div>
          <div class="sidebar-recommend-list">
            <div 
              class="sidebar-recommend-item" 
              v-for="video in displayRecommendVideos" 
              :key="video.id"
              @click="playRecommendVideo(video)"
            >
              <div class="sidebar-recommend-thumbnail">
                <img :src="video.thumbnail" :alt="video.title" />
                <div class="sidebar-recommend-duration">{{ video.duration }}</div>
              </div>
              <div class="sidebar-recommend-info">
                <h5 class="sidebar-recommend-title">{{ video.title }}</h5>
                <div class="sidebar-recommend-meta">
                  <span>{{ video.author }}</span>
                  <span>{{ formatNumber(video.views) }} {{ t('video.views') }}</span>
                </div>
              </div>
            </div>
            <div v-if="loadingMoreRecommend" class="loading-more-recommend">
              <el-icon class="is-loading"><el-icon-loading /></el-icon>
              <span>加载更多...</span>
            </div>
          </div>
        </div>
      </aside>
    </div>
    
    <el-dialog 
      v-model="reportDialogVisible" 
      title="举报内容" 
      width="550px"
      :close-on-click-modal="false"
      class="report-dialog"
    >
      <div class="report-header">
        <div class="report-icon">
          <el-icon :size="40" color="#f56c6c"><el-icon-warning /></el-icon>
        </div>
        <div class="report-info">
          <h3>举报{{ reportForm.type === 1 ? '视频' : '评论' }}</h3>
          <p>我们会认真处理您的举报，感谢您为维护社区环境做出的贡献</p>
        </div>
      </div>
      
      <el-form :model="reportForm" label-position="top" class="report-form">
        <el-form-item label="举报原因" required>
          <el-select 
            v-model="reportForm.reason" 
            placeholder="请选择或输入举报原因"
            style="width: 100%"
            size="large"
            filterable
            allow-create
            default-first-option
          >
            <el-option label="🚫 垃圾广告" value="垃圾广告" />
            <el-option label="❌ 虚假信息" value="虚假信息" />
            <el-option label="⚖️ 违法违规" value="违法违规" />
            <el-option label="©️ 侵犯版权" value="侵犯版权" />
            <el-option label="💢 人身攻击" value="人身攻击" />
            <el-option label="📝 其他" value="其他" />
          </el-select>
          <div class="input-tip">可以从上方选择或直接输入举报原因</div>
        </el-form-item>
        
        <el-form-item label="详细说明" v-if="reportForm.reason === '其他' || !['垃圾广告', '虚假信息', '违法违规', '侵犯版权', '人身攻击', '其他'].includes(reportForm.reason)" required>
          <el-input 
            v-model="reportForm.detail" 
            type="textarea" 
            :rows="4"
            placeholder="请详细描述举报原因，帮助我们更好地处理您的举报"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="备注（选填）">
          <el-input 
            v-model="reportForm.remark" 
            type="textarea" 
            :rows="3"
            placeholder="可以补充其他信息，帮助我们更好地处理您的举报"
            maxlength="300"
            show-word-limit
          />
        </el-form-item>
        
        <el-alert
          type="info"
          :closable="false"
          show-icon
          style="margin-top: 16px;"
        >
          <template #title>
            <span style="font-size: 13px;">举报须知</span>
          </template>
          <div style="font-size: 12px; line-height: 1.6; margin-top: 4px;">
            • 恶意举报可能会导致您的账号受到限制<br>
            • 我们会在24小时内处理您的举报<br>
            • 举报结果会通过系统通知告知您
          </div>
        </el-alert>
      </el-form>
      
      <template #footer>
        <div class="report-footer">
          <el-button size="large" @click="reportDialogVisible = false">取消</el-button>
          <el-button 
            type="danger" 
            size="large" 
            @click="submitReport" 
            :loading="submittingReport"
          >
            <el-icon v-if="!submittingReport"><el-icon-warning /></el-icon>
            提交举报
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { GetVideos, GetVideoResource, SendVideoComment, VideoOperate, GetVideoOperateMessage, GetUserDate, InsertVideoHistory, ReportContent } from '@/api/Users'
import { getVideoResolution } from '@/api/Video'
import { useUserInfoStore } from '@/stores/userinfo'

const { t, locale } = useI18n()
const router = useRouter()
const route = useRoute()
const userInfoStore = useUserInfoStore()

const videoPlayer = ref(null)
const commentText = ref('')
const replyText = ref('')
const replyingTo = ref(null)
const submittingComment = ref(false)
const submittingReply = ref(false)
const loading = ref(false)
const videoError = ref(false)
const isFlipped = ref(false)
const currentTab = ref('home')
const showSearchHistory = ref(false)
const searchHistory = ref(['Vue3教程', 'Element Plus', 'TypeScript入门', 'React基础'])

const isLiked = ref(false)
const isCollected = ref(false)
const isShared = ref(false)

const reportDialogVisible = ref(false)
const submittingReport = ref(false)
const reportForm = ref({
  type: null,
  targetUserEncode: '',
  targetEncode: '',
  reason: '',
  detail: '',
  remark: ''
})

const videoStartTime = ref(null)
const totalWatchTime = ref(0)
const isPlaying = ref(false)
console.log('初始化视频播放时间跟踪:', { videoStartTime: videoStartTime.value, totalWatchTime: totalWatchTime.value, isPlaying: isPlaying.value })

const headerBgImage = ref('https://lizhanghao.oss-cn-beijing.aliyuncs.com/%E8%8F%9C%E5%8D%95%E6%A0%8F/487-1800x70.jpg')

const recommendHeaderImage = ref('https://lizhanghao.oss-cn-beijing.aliyuncs.com/%E5%B0%8F%E5%B0%BA%E5%AF%B8/mmexport1772727343305.gif')

const authorBtnSquareImage = ref('https://lizhanghao.oss-cn-beijing.aliyuncs.com/%E5%B0%8F%E5%B0%BA%E5%AF%B8/mmexport1772727343305.gif')
const authorBtnCircleImage = ref('https://lizhanghao.oss-cn-beijing.aliyuncs.com/%E5%B0%8F%E5%B0%BA%E5%AF%B8/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20260306001645_2515_4.jpg')

const parallaxOffset = ref({
  layer1: 0,
  layer2: 0,
  layer3: 0
})

const videoData = ref({
  encode: '',
  title: '',
  thumbnail: '',
  videoUrl: '',
  duration: '',
  author: '',
  views: 0,
  comments: 0,
  likeCount: 0,
  collectCount: 0,
  shareCount: 0,
  description: '',
  introduction: '',
  publishTime: '',
  tags: [],
  videoResolution: '',
  isMember: false
})

const resolutionList = ref([])
const currentResolution = ref('')
const showResolutionMenu = ref(false)

// 自定义控制栏状态
const currentTime = ref(0)
const duration = ref(0)
const volume = ref(1)
const isMuted = ref(false)
const isFullscreen = ref(false)
const progressPercent = ref(0)
const bufferedPercent = ref(0)
const showBufferingTip = ref(false)
const bufferingCount = ref(0)
const bufferingTimer = ref(null)

const publisherInfo = ref({
  encode: '',
  nickName: '',
  avatar: '',
  uiMember: '0',
  country: '',
  school: ''
})

const comments = ref([])

const recommendVideos = ref([
  {
    id: '1',
    title: 'Vue3 + TypeScript 实战开发教程',
    thumbnail: 'https://picsum.photos/200/112?random=1',
    duration: '15:30',
    author: '张老师',
    views: 125000
  },
  {
    id: '2',
    title: 'React Hooks 深入浅出',
    thumbnail: 'https://picsum.photos/200/112?random=2',
    duration: '12:45',
    author: '李老师',
    views: 86000
  },
  {
    id: '3',
    title: 'Python 数据分析实战',
    thumbnail: 'https://picsum.photos/200/112?random=3',
    duration: '18:20',
    author: '王老师',
    views: 92000
  },
  {
    id: '4',
    title: 'Node.js 后端开发入门',
    thumbnail: 'https://picsum.photos/200/112?random=4',
    duration: '20:15',
    author: '赵老师',
    views: 78000
  },
  {
    id: '5',
    title: 'Docker 容器化部署',
    thumbnail: 'https://picsum.photos/200/112?random=5',
    duration: '16:40',
    author: '孙老师',
    views: 65000
  },
  {
    id: '6',
    title: 'Git 版本控制详解',
    thumbnail: 'https://picsum.photos/200/112?random=6',
    duration: '18:30',
    author: '周老师',
    views: 89000
  },
  {
    id: '7',
    title: 'Webpack 打包优化',
    thumbnail: 'https://picsum.photos/200/112?random=7',
    duration: '22:15',
    author: '吴老师',
    views: 72000
  },
  {
    id: '8',
    title: 'CSS 动画特效制作',
    thumbnail: 'https://picsum.photos/200/112?random=8',
    duration: '15:45',
    author: '郑老师',
    views: 95000
  },
  {
    id: '9',
    title: 'JavaScript 高级特性',
    thumbnail: 'https://picsum.photos/200/112?random=9',
    duration: '28:20',
    author: '王老师',
    views: 120000
  },
  {
    id: '10',
    title: 'React Hooks 深入理解',
    thumbnail: 'https://picsum.photos/200/112?random=10',
    duration: '25:30',
    author: '李老师',
    views: 145000
  },
  {
    id: '11',
    title: 'MongoDB 数据库入门',
    thumbnail: 'https://picsum.photos/200/112?random=11',
    duration: '19:50',
    author: '张老师',
    views: 68000
  },
  {
    id: '12',
    title: 'GraphQL API 开发',
    thumbnail: 'https://picsum.photos/200/112?random=12',
    duration: '21:10',
    author: '刘老师',
    views: 55000
  },
  {
    id: '13',
    title: 'Jest 单元测试实战',
    thumbnail: 'https://picsum.photos/200/112?random=13',
    duration: '17:35',
    author: '陈老师',
    views: 48000
  },
  {
    id: '14',
    title: 'Nginx 服务器配置',
    thumbnail: 'https://picsum.photos/200/112?random=14',
    duration: '14:20',
    author: '杨老师',
    views: 62000
  },
  {
    id: '15',
    title: 'Redis 缓存应用',
    thumbnail: 'https://picsum.photos/200/112?random=15',
    duration: '16:55',
    author: '黄老师',
    views: 71000
  },
  {
    id: '16',
    title: 'Linux 命令行基础',
    thumbnail: 'https://picsum.photos/200/112?random=16',
    duration: '23:40',
    author: '赵老师',
    views: 83000
  },
  {
    id: '17',
    title: 'PostgreSQL 数据库优化',
    thumbnail: 'https://picsum.photos/200/112?random=17',
    duration: '20:25',
    author: '孙老师',
    views: 59000
  },
  {
    id: '18',
    title: 'Kubernetes 集群管理',
    thumbnail: 'https://picsum.photos/200/112?random=18',
    duration: '26:15',
    author: '周老师',
    views: 77000
  },
  {
    id: '19',
    title: 'Flutter 移动端开发',
    thumbnail: 'https://picsum.photos/200/112?random=19',
    duration: '24:30',
    author: '吴老师',
    views: 92000
  },
  {
    id: '20',
    title: 'Rust 编程语言入门',
    thumbnail: 'https://picsum.photos/200/112?random=20',
    duration: '30:00',
    author: '郑老师',
    views: 105000
  }
])

const authorVideos = ref([])

const showFloatingRecommend = ref(true)
const loadingMoreRecommend = ref(false)
const recommendPage = ref(0)
const recommendPageSize = 5
const recommendLoading = ref(false)

const formatVideoTime = (seconds) => {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}:${secs.toString().padStart(2, '0')}`
}

const fetchRecommendVideos = async () => {
  try {
    if (recommendLoading.value) return
    recommendLoading.value = true
    
    const res = await GetVideos(15)
    
    if (res.code === 0 && res.data) {
      recommendVideos.value = res.data.map(item => {
        return {
          id: item.enCode,
          encode: item.enCode,
          title: item.videoName,
          thumbnail: item.videoImageUrl,
          videoUrl: item.videoUrl,
          duration: formatVideoTime(item.videoTime || 0),
          author: item.publisherName || '发布人',
          views: item.viewCount || 0,
          comments: item.commentCount || 0,
          likeCount: item.likeCount || 0,
          collectCount: item.collectCount || 0,
          shareCount: item.shareCount || 0,
          videoResolution: item.videoResolution,
          isMember: item.isMember
        }
      })
    }
  } catch (error) {
    console.error('获取推荐视频失败:', error)
  } finally {
    recommendLoading.value = false
  }
}

const refreshRecommendVideos = async () => {
  recommendPage.value = 0
  await fetchRecommendVideos()
}

const fetchAuthorVideos = async () => {
  try {
    const res = await GetVideos(10)
    
    if (res.code === 0 && res.data) {
      authorVideos.value = res.data.map(item => ({
        id: item.enCode,
        encode: item.enCode,
        title: item.videoName,
        thumbnail: cleanResourceUrl(item.videoImageUrl),
        videoUrl: cleanResourceUrl(item.videoUrl),
        duration: formatVideoTime(item.videoTime || 0),
        views: item.viewCount || 0,
        likeCount: item.likeCount || 0,
        commentCount: item.commentCount || 0,
        videoResolution: item.videoResolution,
        isMember: item.isMember
      }))
      console.log('从推荐接口获取作者视频:', authorVideos.value)
    }
  } catch (error) {
    console.error('获取作者视频失败:', error)
  }
}

const displayRecommendVideos = computed(() => {
  return recommendVideos.value
})

const isCollectionSeries = computed(() => {
  return authorVideos.value.length > 0 && authorVideos.value[0].isCollection === 1
})

const authorSectionTitle = computed(() => {
  return isCollectionSeries.value ? t('video.seriesVideos') : t('video.authorVideos')
})

const handleFloatingScroll = (e) => {
  const { scrollTop, scrollHeight, clientHeight } = e.target
  if (scrollTop + clientHeight >= scrollHeight - 10) {
    loadMoreRecommendVideos()
  }
}

const loadMoreRecommendVideos = () => {
  if (loadingMoreRecommend.value) return
  loadingMoreRecommend.value = true
  setTimeout(() => {
    const maxPage = Math.ceil(recommendVideos.value.length / recommendPageSize)
    if (recommendPage.value >= maxPage - 1) {
      recommendPage.value = 0
    } else {
      recommendPage.value++
    }
    loadingMoreRecommend.value = false
  }, 500)
}

const relatedFiles = ref([])

const currentLangText = computed(() => {
  const langMap = {
    zh: '中文',
    en: 'English',
    ja: '日本語'
  }
  return langMap[locale.value] || '中文'
})

const changeLocale = async (lang) => {
  locale.value = lang
  localStorage.setItem('locale', lang)
  
  // 调用后端接口更新语言设置
  const langMap = {
    'zh': '中文',
    'en': '英文',
    'ja': '日文'
  }
  const result = await userInfoStore.updateLanguage(langMap[lang])
  if (result) {
    ElMessage.success(t('loginRegister.languageUpdated'))
  }
}

const hideSearchHistory = () => {
  setTimeout(() => {
    showSearchHistory.value = false
  }, 200)
}

const clearSearchHistory = () => {
  searchHistory.value = []
}

const selectHistory = (item) => {
  searchKeyword.value = item
  showSearchHistory.value = false
  handleSearch()
}

const goBack = () => {
  router.back()
}

const goToFirst = () => {
  router.push('/First')
}

const goToAI = () => {
  router.push('/ai/home')
}

const searchKeyword = ref('')

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/search',
      query: { key: searchKeyword.value.trim() }
    })
  }
}

const handleUpload = () => {
  router.push('/upload')
}

const handleMessage = () => {
  router.push('/chat')
}

const handleHistory = () => {
  router.push('/history')
}

const handleFavorite = () => {
  router.push('/favorite')
}

const handleAvatarClick = () => {
  router.push('/center')
}

const goToUserProfile = () => {
  if (publisherInfo.value.encode) {
    router.push({
      path: '/user/' + publisherInfo.value.encode
    })
  } else {
    ElMessage.warning('作者信息不完整，无法查看主页')
  }
}

const userAvatar = ref('https://picsum.photos/100/100?random=user')

const isVideoBackground = (url) => {
  if (!url) return false
  const videoExtensions = ['.mp4', '.webm', '.ogg', '.mov', '.avi']
  const lowerUrl = url.toLowerCase()
  return videoExtensions.some(ext => lowerUrl.includes(ext))
}

const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

const cleanUrl = (url) => {
  if (!url) return ''
  return url.replace(/[\r\n\s`]/g, '').trim()
}

const handleVideoError = () => {
  videoError.value = true
  console.error('视频加载失败')
}

const handleVideoCanPlay = () => {
  videoError.value = false
}

const toggleFlip = () => {
  isFlipped.value = !isFlipped.value
}

const handleHeaderMouseMove = (e) => {
  const header = e.currentTarget
  const rect = header.getBoundingClientRect()
  const centerX = rect.width / 2
  const mouseX = e.clientX - rect.left
  const offset = (mouseX - centerX) / 20
  
  parallaxOffset.value = {
    layer1: offset * 3,
    layer2: offset * 2,
    layer3: offset * 1
  }
}

const handleHeaderMouseLeave = () => {
  parallaxOffset.value = {
    layer1: 0,
    layer2: 0,
    layer3: 0
  }
}

const handleFileClick = (file) => {
  ElMessage.success(`已选择: ${file.resourceName}`)
}

const getFileEmoji = (format) => {
  const emojiMap = {
    pdf: '📄',
    png: '🖼️',
    jpg: '🖼️',
    jpeg: '🖼️',
    gif: '🖼️',
    zip: '📦',
    rar: '📦',
    '7z': '📦',
    doc: '📝',
    docx: '📝',
    xls: '📊',
    xlsx: '📊',
    ppt: '📽️',
    pptx: '📽️',
    mp4: '🎬',
    avi: '🎬',
    mov: '🎬',
    mp3: '🎵',
    wav: '🎵',
    txt: '📃'
  }
  return emojiMap[format?.toLowerCase()] || '📁'
}

const formatFileSize = (bytes) => {
  if (!bytes || bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const getDefaultAvatar = (name) => {
  return `https://ui-avatars.com/api/?name=${encodeURIComponent(name || 'User')}&background=e0e0e0&color=999999&size=128&font-size=0.4`
}

const getAvatarUrl = (avatar, name) => {
  if (avatar && avatar.trim()) {
    return avatar
  }
  return getDefaultAvatar(name)
}

const handleVideoPlay = () => {
  console.log('视频开始播放事件触发')
  isPlaying.value = true
  if (!videoStartTime.value) {
    videoStartTime.value = new Date()
    console.log('设置视频开始时间:', videoStartTime.value)
    console.log('当前总观看时间:', totalWatchTime.value)
  }
}

const handleVideoPause = () => {
  console.log('视频暂停')
  isPlaying.value = false
  if (videoStartTime.value) {
    const now = new Date()
    const currentWatchTime = Math.floor((now - videoStartTime.value) / 1000)
    totalWatchTime.value += currentWatchTime
    console.log('暂停时累计观看时间:', totalWatchTime.value)
    videoStartTime.value = null
  }
}

const handleVideoEnded = () => {
  console.log('视频播放结束')
  isPlaying.value = false
  if (videoStartTime.value) {
    const now = new Date()
    const currentWatchTime = Math.floor((now - videoStartTime.value) / 1000)
    totalWatchTime.value += currentWatchTime
    console.log('结束时累计观看时间:', totalWatchTime.value)
    videoStartTime.value = null
  }
  recordVideoHistory()
}

const handleVideoTimeUpdate = () => {
  updateProgress()
  
  // 实时更新观看时间
  if (videoStartTime.value) {
    const now = new Date()
    const currentWatchTime = Math.floor((now - videoStartTime.value) / 1000)
    // 实时计算当前观看时间，但不累计到总时间
    console.log('当前观看时间:', currentWatchTime)
  }
}

const recordVideoHistory = () => {
  // 基于视频长度的合理比例来判断是否记录历史
  if (videoData.value.encode) {
    console.log('开始记录视频历史，总观看时间:', totalWatchTime.value)
    // 尝试从视频数据中获取视频总时长（秒）
    let videoDuration = 0
    if (videoData.value.duration) {
      // 处理格式为"15:30"的时长
      const timeParts = videoData.value.duration.split(':')
      if (timeParts.length === 2) {
        videoDuration = parseInt(timeParts[0]) * 60 + parseInt(timeParts[1])
      } else if (timeParts.length === 3) {
        videoDuration = parseInt(timeParts[0]) * 3600 + parseInt(timeParts[1]) * 60 + parseInt(timeParts[2])
      }
    }
    
    // 计算最小观看时间：
    // - 短视频（< 2分钟）：至少观看10秒
    // - 中视频（2-10分钟）：至少观看20%的时长
    // - 长视频（> 10分钟）：至少观看15%的时长
    let minWatchTime = 10 // 默认10秒
    
    if (videoDuration > 0) {
      if (videoDuration < 120) { // 短视频
        minWatchTime = 10
      } else if (videoDuration < 600) { // 中视频
        minWatchTime = Math.max(10, Math.floor(videoDuration * 0.2))
      } else { // 长视频
        minWatchTime = Math.max(30, Math.floor(videoDuration * 0.15))
      }
    }
    
    console.log('最小观看时间阈值:', minWatchTime)
    // 只记录观看时间超过最小阈值的视频
    console.log('总观看时间（秒）:', totalWatchTime.value)
    if (totalWatchTime.value >= minWatchTime) { // 观看时间达到最小阈值
      // 计算播放百分比，保留整数并向后取整
      let watchPercentage = 0
      if (videoDuration > 0) {
        watchPercentage = Math.ceil((totalWatchTime.value / videoDuration) * 100)
        // 确保百分比不超过100%
        watchPercentage = Math.min(watchPercentage, 100)
      }
      
      console.log('视频总时长（秒）:', videoDuration)
      console.log('播放百分比:', watchPercentage)
      
      // 使用当前时间作为watchTime，确保时间是正确的
      const now = new Date()
      // 手动构建时间字符串，确保时区正确
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const hours = String(now.getHours()).padStart(2, '0')
      const minutes = String(now.getMinutes()).padStart(2, '0')
      const seconds = String(now.getSeconds()).padStart(2, '0')
      const watchTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
      console.log('记录历史数据:', {
        enCode: videoData.value.encode,
        watchTime: watchTime,
        duration: watchPercentage
      })
      
      InsertVideoHistory({
        enCode: videoData.value.encode,
        watchTime: watchTime,
        duration: watchPercentage
      }).then(result => {
        if (result.code === 0) {
          console.log('视频观看历史记录成功')
        } else {
          console.warn('视频观看历史记录失败:', result)
        }
      }).catch(error => {
        console.error('视频观看历史记录失败:', error)
      })
    } else {
      console.log('观看时间不足阈值，不记录历史')
    }
  }
}

const handleLike = async () => {
  if (!videoData.value.encode) {
    ElMessage.warning('视频信息不完整')
    return
  }
  
  try {
    const newStatus = isLiked.value ? 0 : 1
    const res = await VideoOperate(videoData.value.encode, 'like', newStatus)
    
    if (res.code === 0) {
      isLiked.value = !isLiked.value
      if (isLiked.value) {
        videoData.value.likeCount++
        ElMessage.success(t('video.likeSuccess'))
      } else {
        videoData.value.likeCount = Math.max(0, videoData.value.likeCount - 1)
        ElMessage.info('已取消点赞')
      }
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleCollect = async () => {
  if (!videoData.value.encode) {
    ElMessage.warning('视频信息不完整')
    return
  }
  
  try {
    const newStatus = isCollected.value ? 0 : 1
    const res = await VideoOperate(videoData.value.encode, 'collect', newStatus)
    
    if (res.code === 0) {
      isCollected.value = !isCollected.value
      if (isCollected.value) {
        videoData.value.collectCount++
        ElMessage.success(t('video.collectSuccess'))
      } else {
        videoData.value.collectCount = Math.max(0, videoData.value.collectCount - 1)
        ElMessage.info('已取消收藏')
      }
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleShare = async () => {
  if (!videoData.value.encode) {
    ElMessage.warning('视频信息不完整')
    return
  }
  
  try {
    const res = await VideoOperate(videoData.value.encode, 'share', 1)
    
    if (res.code === 0) {
      videoData.value.shareCount++
      ElMessage.success(t('video.shareSuccess'))
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('分享操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 检查用户是否被禁言（考虑禁言时间）

const reportVideo = () => {
  if (!videoData.value.encode) {
    ElMessage.warning('视频信息不完整')
    return
  }
  
  reportForm.value = {
    type: 1,
    targetUserEncode: publisherInfo.value.encode || videoData.value.userEncode || '',
    targetEncode: videoData.value.encode,
    reason: '',
    detail: '',
    remark: ''
  }
  reportDialogVisible.value = true
}

const reportComment = (comment) => {
  if (!comment.videoCommentId) {
    ElMessage.warning('评论信息不完整')
    return
  }
  
  const userEncode = comment.userEncode || comment.commentUserEncode || comment.userEnCode || comment.userId || comment.commentUserId || comment.id || ''
  
  reportForm.value = {
    type: 2,
    targetUserEncode: userEncode,
    targetEncode: comment.videoCommentId,
    reason: '',
    detail: '',
    remark: ''
  }
  reportDialogVisible.value = true
}

const submitReport = async () => {
  if (!reportForm.value.reason) {
    ElMessage.warning('请选择或输入举报原因')
    return
  }
  
  const isCustomReason = !['垃圾广告', '虚假信息', '违法违规', '侵犯版权', '人身攻击', '其他'].includes(reportForm.value.reason)
  
  if ((reportForm.value.reason === '其他' || isCustomReason) && !reportForm.value.detail) {
    ElMessage.warning('请填写详细说明')
    return
  }
  
  try {
    submittingReport.value = true
    
    let reason = reportForm.value.reason
    if (reportForm.value.reason === '其他' || isCustomReason) {
      reason = `${reportForm.value.reason}: ${reportForm.value.detail}`
    }
    
    const res = await ReportContent(
      reportForm.value.targetUserEncode,
      reportForm.value.type,
      reportForm.value.targetEncode,
      reason,
      reportForm.value.remark
    )
    
    if (res.code === 0) {
      ElMessage.success('举报成功，我们会尽快处理')
      reportDialogVisible.value = false
    } else {
      ElMessage.error(res.message || res.data || '举报失败')
    }
  } catch (error) {
    console.error('举报失败:', error)
    // 拦截器已经将错误信息提取出来，error可能是字符串或Error对象
    const errorMsg = typeof error === 'string' 
      ? error 
      : (error.response?.data?.message || 
         error.response?.data?.data || 
         error.message || 
         '举报失败')
    ElMessage.error(errorMsg)
  } finally {
    submittingReport.value = false
  }
}

const isUserBanned = () => {
  const userInfo = userInfoStore.info
  if (userInfo.accountStatusType === 1) {
    // 检查禁言是否已结束
    const endTimeStr = userInfo.endTime
    if (endTimeStr) {
      const endTime = new Date(endTimeStr)
      const now = new Date()
      // 如果当前时间大于禁言结束时间，则禁言已结束
      return now < endTime
    }
    return true
  }
  return false
}

const submitComment = async () => {
  if (!commentText.value.trim()) {
    ElMessage.warning(t('video.commentEmpty'))
    return
  }
  
  if (!videoData.value.encode) {
    ElMessage.warning('视频信息不完整')
    return
  }
  
  // 检查用户是否被禁言
  if (isUserBanned()) {
    ElMessage.error('账号已被禁言，无法发表评论')
    return
  }
  
  submittingComment.value = true
  
  try {
    const res = await SendVideoComment(videoData.value.encode, commentText.value.trim(), '')
    
    if (res.code === 0) {
      await VideoOperate(videoData.value.encode, 'comment', 1)
      ElMessage.success(t('video.commentSuccess'))
      commentText.value = ''
      await fetchVideoResource(videoData.value.encode)
    } else {
      ElMessage.error(res.message || '评论发送失败')
    }
  } catch (error) {
    console.error('发送评论失败:', error)
    ElMessage.error('评论发送失败')
  } finally {
    submittingComment.value = false
  }
}

const showReplyInput = (comment, parentComment = null) => {
  replyingTo.value = parentComment || comment
  replyText.value = ''
}

const cancelReply = () => {
  replyingTo.value = null
  replyText.value = ''
}

const submitReply = async (parentComment) => {
  if (!replyText.value.trim()) {
    ElMessage.warning(t('video.commentEmpty'))
    return
  }
  
  if (!videoData.value.encode) {
    ElMessage.warning('视频信息不完整')
    return
  }
  
  // 检查用户是否被禁言
  if (isUserBanned()) {
    ElMessage.error('账号已被禁言，无法发表回复')
    return
  }
  
  submittingReply.value = true
  
  try {
    const res = await SendVideoComment(videoData.value.encode, replyText.value.trim(), parentComment.videoCommentId)
    
    if (res.code === 0) {
      await VideoOperate(videoData.value.encode, 'comment', 1)
      ElMessage.success(t('video.replySuccess') || '回复成功')
      replyText.value = ''
      replyingTo.value = null
      await fetchVideoResource(videoData.value.encode)
    } else {
      ElMessage.error(res.message || '回复发送失败')
    }
  } catch (error) {
    console.error('发送回复失败:', error)
    ElMessage.error('回复发送失败')
  } finally {
    submittingReply.value = false
  }
}

const playRecommendVideo = (video) => {
  if (video.encode) {
    // 跳转前记录当前视频的播放历史
    console.log('点击推荐视频，准备跳转')
    console.log('跳转前播放状态:', { isPlaying: isPlaying.value, videoStartTime: videoStartTime.value, totalWatchTime: totalWatchTime.value })
    if (videoStartTime.value) {
      const now = new Date()
      const currentWatchTime = Math.floor((now - videoStartTime.value) / 1000)
      totalWatchTime.value += currentWatchTime
      console.log('跳转时累计观看时间:', totalWatchTime.value)
      videoStartTime.value = null
      isPlaying.value = false
    }
    recordVideoHistory()
    
    // 存储新视频信息并跳转
    sessionStorage.setItem('currentVideo', JSON.stringify(video))
    window.location.href = `/Video?encode=${video.encode}`
  }
}

const fetchVideoDetail = async (videoEncode) => {
  if (!videoEncode) return
  
  loading.value = true
  
  try {
    // TODO: 调用后端接口获取视频详情
    // const response = await api.getVideoDetail(videoEncode)
    // videoData.value = response.data
    
    // 临时模拟数据，等待后端接口实现
    await new Promise(resolve => setTimeout(resolve, 500))
    
    videoData.value = {
      encode: videoEncode,
      title: 'Vue3 + TypeScript 实战开发教程',
      thumbnail: 'https://picsum.photos/800/450?random=' + videoEncode,
      videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4',
      duration: '15:30',
      author: '张老师',
      views: 125000,
      comments: 256,
      likeCount: 1280,
      collectCount: 560,
      shareCount: 89,
      description: '这是一个精彩的视频教程，讲解了Vue3和TypeScript的核心概念和实战应用。本课程将从基础概念讲起，逐步深入到高级应用，帮助你快速掌握Vue3开发技能。',
      introduction: '这是一个精彩的视频教程，讲解了Vue3和TypeScript的核心概念和实战应用。',
      publishTime: '2024-01-15',
      tags: ['Vue3', 'TypeScript', '前端开发']
    }
    
    // 获取推荐视频列表
    await fetchRecommendVideos(videoEncode)
    
  } catch (error) {
    console.error('获取视频详情失败:', error)
    ElMessage.error('获取视频详情失败')
  } finally {
    loading.value = false
  }
}


watch(() => route.query.encode, (newEncode) => {
  if (newEncode) {
    loadVideoFromSession()
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}, { immediate: false })

const fetchVideoOperateMessage = async (encode) => {
  if (!encode) {
    console.warn('没有视频Encode参数，无法获取操作状态')
    return
  }
  
  try {
    console.log('正在获取视频操作状态，Encode:', encode)
    const res = await GetVideoOperateMessage(encode)
    console.log('视频操作状态返回:', res)
    
    if (res.code === 0 && res.data && res.data.length > 0) {
      const data = res.data[0]
      
      videoData.value.views = data.viewCount || 0
      videoData.value.likeCount = data.likeCount || 0
      videoData.value.comments = data.commentCount || 0
      videoData.value.shareCount = data.shareCount || 0
      videoData.value.collectCount = data.collectCount || 0
      
      if (data.isLike !== null && data.isLike !== undefined) {
        isLiked.value = data.isLike === 1
      }
      if (data.isCollect !== null && data.isCollect !== undefined) {
        isCollected.value = data.isCollect === 1
      }
      if (data.isShare !== null && data.isShare !== undefined) {
        isShared.value = data.isShare === 1
      }
      
      console.log('视频操作状态已更新:', {
        likeCount: videoData.value.likeCount,
        collectCount: videoData.value.collectCount,
        shareCount: videoData.value.shareCount,
        isLiked: isLiked.value,
        isCollected: isCollected.value,
        isShared: isShared.value
      })
    }
  } catch (error) {
    console.error('获取视频操作状态失败:', error)
  }
}

const fetchVideoResource = async (encode) => {
  if (!encode) {
    console.warn('没有视频Encode参数，无法获取视频资源')
    return
  }
  
  try {
    console.log('正在获取视频资源信息，Encode:', encode)
    const res = await GetVideoResource(encode)
    console.log('视频资源接口返回信息:', res)
    
    if (res.code === 0 && res.data) {
      console.log('视频资源数据:', res.data)
      
      if (res.data.commentList && res.data.commentList.length > 0) {
        comments.value = res.data.commentList
        console.log('评论数据已更新:', comments.value)
      }
      
      if (res.data.homeResourceList && res.data.homeResourceList.length > 0) {
        processHomeResources(res.data.homeResourceList)
      }
      
      if (res.data.videoFileList && res.data.videoFileList.length > 0) {
        relatedFiles.value = res.data.videoFileList.map(item => ({
          resourceName: item.resourceName,
          resourceFormat: item.resourceFormat,
          resourceUrl: cleanResourceUrl(item.resourceUrl),
          resourceSize: item.resourceSize,
          downloadCount: item.downloadCount,
          resourceDesc: item.resourceDesc
        }))
        console.log('文件列表已更新:', relatedFiles.value)
      }
      
      if (res.data.recommendVideoList && res.data.recommendVideoList.length > 0) {
        authorVideos.value = res.data.recommendVideoList.map(item => ({
          id: item.enCode,
          encode: item.enCode,
          title: item.videoName,
          thumbnail: cleanResourceUrl(item.videoImageUrl),
          videoUrl: cleanResourceUrl(item.videoUrl),
          duration: formatVideoTime(item.videoTime || 0),
          views: item.viewCount || 0,
          likeCount: item.likeCount || 0,
          commentCount: item.commentCount || 0,
          videoResolution: item.videoResolution,
          isMember: item.isMember,
          collectionInfo: item.collectionInfo
        }))
        console.log('作者相关视频已更新:', authorVideos.value)
      } else {
        await fetchAuthorVideos()
      }
      
      const publisherData = res.data.publisherInfoList || res.data.publisherInfo || res.data.userInfo || res.data.authorInfo
      if (publisherData && (Array.isArray(publisherData) ? publisherData.length > 0 : true)) {
        const publisher = Array.isArray(publisherData) ? publisherData[0] : publisherData
        console.log('作者信息原始数据:', publisher)
        console.log('所有字段:', Object.keys(publisher))
        publisherInfo.value = {
          encode: publisher.enCode || publisher.encode || publisher.userCode || publisher.publisherCode || publisher.publisherEnCode || '',
          nickName: publisher.nickName || publisher.nickname || publisher.name || '',
          avatar: cleanResourceUrl(publisher.avatar),
          uiMember: publisher.uiMember || publisher.isMember || '0',
          country: publisher.country || '',
          school: publisher.school || ''
        }
        console.log('作者信息已更新:', publisherInfo.value)
      } else {
        console.warn('未找到作者信息，res.data:', res.data)
        console.log('res.data的所有字段:', Object.keys(res.data))
      }
      
      if (res.data.introduction !== undefined) {
        videoData.value.introduction = res.data.introduction || ''
        console.log('视频简介已更新:', videoData.value.introduction)
      }
    } else {
      console.warn('获取视频资源返回异常:', res)
    }
  } catch (error) {
    console.error('获取视频资源失败:', error)
  }
}

const fetchVideoResolution = async (encode) => {
  if (!encode) {
    console.warn('没有视频Encode参数，无法获取分辨率列表')
    return
  }
  
  try {
    console.log('正在获取视频分辨率列表，Encode:', encode)
    const res = await getVideoResolution(encode)
    console.log('分辨率列表接口返回:', res)
    
    // 先添加原始视频作为默认选项
    const originalVideo = {
      resolution: '原始',
      width: null,
      height: null,
      videoUrl: videoData.value.videoUrl,
      size: null,
      format: 'mp4',
      isOriginal: true
    }
    
    if (res.code === 0 && res.data && res.data.length > 0) {
      // 将原始视频添加到列表开头
      resolutionList.value = [
        originalVideo,
        ...res.data.map(item => ({
          resolution: item.resolution,
          width: item.width,
          height: item.height,
          videoUrl: cleanResourceUrl(item.videoUrl),
          size: item.size,
          format: item.format,
          isOriginal: false
        }))
      ]
      
      // 默认选中原始视频
      currentResolution.value = '原始'
      console.log('分辨率列表已更新（包含原始视频）:', resolutionList.value)
      console.log('当前分辨率:', currentResolution.value)
    } else {
      // 如果没有转码版本，只显示原始视频
      resolutionList.value = [originalVideo]
      currentResolution.value = '原始'
      console.log('该视频没有多分辨率版本，只显示原始视频')
    }
  } catch (error) {
    console.error('获取视频分辨率失败:', error)
    // 即使获取失败，也显示原始视频选项
    if (videoData.value.videoUrl) {
      resolutionList.value = [{
        resolution: '原始',
        width: null,
        height: null,
        videoUrl: videoData.value.videoUrl,
        size: null,
        format: 'mp4',
        isOriginal: true
      }]
      currentResolution.value = '原始'
    }
  }
}

const changeResolution = (resolution) => {
  const selected = resolutionList.value.find(r => r.resolution === resolution)
  if (selected && videoPlayer.value) {
    const currentTime = videoPlayer.value.currentTime
    const wasPlaying = !videoPlayer.value.paused
    
    videoData.value.videoUrl = selected.videoUrl
    currentResolution.value = resolution
    showResolutionMenu.value = false
    
    videoPlayer.value.load()
    videoPlayer.value.currentTime = currentTime
    if (wasPlaying) {
      videoPlayer.value.play()
    }
    
    ElMessage.success(`已切换到 ${resolution}`)
    console.log('切换分辨率:', resolution, selected.videoUrl)
  }
}

// 自定义控制栏函数
const togglePlay = () => {
  if (videoPlayer.value) {
    if (videoPlayer.value.paused) {
      videoPlayer.value.play()
    } else {
      videoPlayer.value.pause()
    }
  }
}

const toggleMute = () => {
  if (videoPlayer.value) {
    videoPlayer.value.muted = !videoPlayer.value.muted
    isMuted.value = videoPlayer.value.muted
  }
}

const changeVolume = (event) => {
  const value = event.target.value / 100
  volume.value = value
  if (videoPlayer.value) {
    videoPlayer.value.volume = value
  }
}

const seekVideo = (event) => {
  if (videoPlayer.value) {
    const rect = event.currentTarget.getBoundingClientRect()
    const percent = (event.clientX - rect.left) / rect.width
    const time = percent * duration.value
    videoPlayer.value.currentTime = time
  }
}

const toggleFullscreen = () => {
  const playerWrapper = document.querySelector('.video-player-wrapper')
  
  if (!document.fullscreenElement) {
    if (playerWrapper.requestFullscreen) {
      playerWrapper.requestFullscreen()
    } else if (playerWrapper.webkitRequestFullscreen) {
      playerWrapper.webkitRequestFullscreen()
    } else if (playerWrapper.msRequestFullscreen) {
      playerWrapper.msRequestFullscreen()
    }
    isFullscreen.value = true
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    } else if (document.webkitExitFullscreen) {
      document.webkitExitFullscreen()
    } else if (document.msExitFullscreen) {
      document.msExitFullscreen()
    }
    isFullscreen.value = false
  }
}

const formatTime = (seconds) => {
  if (isNaN(seconds) || !isFinite(seconds)) return '00:00'
  
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = Math.floor(seconds % 60)
  
  if (h > 0) {
    return `${h}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
  }
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

// 卡顿检测
const handleVideoWaiting = () => {
  bufferingCount.value++
  console.log('视频缓冲中，次数:', bufferingCount.value)
  
  // 清除之前的定时器
  if (bufferingTimer.value) {
    clearTimeout(bufferingTimer.value)
  }
  
  // 如果3秒内缓冲超过3次，显示提示
  bufferingTimer.value = setTimeout(() => {
    if (bufferingCount.value >= 3) {
      showBufferingTip.value = true
      bufferingCount.value = 0
    }
  }, 3000)
}

const handleVideoStalled = () => {
  console.log('视频停滞')
  bufferingCount.value++
  
  if (bufferingCount.value >= 2 && !showBufferingTip.value) {
    showBufferingTip.value = true
  }
}

const switchToLowerQuality = () => {
  showBufferingTip.value = false
  
  // 找到比当前分辨率低的选项
  const currentIndex = resolutionList.value.findIndex(r => r.resolution === currentResolution.value)
  if (currentIndex < resolutionList.value.length - 1) {
    const lowerQuality = resolutionList.value[currentIndex + 1]
    changeResolution(lowerQuality.resolution)
  }
}

const updateProgress = () => {
  if (videoPlayer.value) {
    currentTime.value = videoPlayer.value.currentTime
    duration.value = videoPlayer.value.duration || 0
    progressPercent.value = (currentTime.value / duration.value) * 100 || 0
    
    // 更新缓冲进度
    if (videoPlayer.value.buffered.length > 0) {
      const bufferedEnd = videoPlayer.value.buffered.end(videoPlayer.value.buffered.length - 1)
      bufferedPercent.value = (bufferedEnd / duration.value) * 100 || 0
    }
  }
}

const cleanResourceUrl = (url) => {
  if (!url) return ''
  return url.replace(/[\r\n\s`]/g, '').trim()
}

const processHomeResources = (resourceList) => {
  resourceList.forEach(item => {
    const cleanUrl = cleanResourceUrl(item.address)
    
    switch (item.fileName) {
      case '菜单栏':
        headerBgImage.value = cleanUrl
        console.log('菜单栏背景图片已更新:', cleanUrl)
        break
      case '推荐视频':
        recommendHeaderImage.value = cleanUrl
        console.log('推荐视频区域图片已更新:', cleanUrl)
        break
      case '旋转正方形':
        authorBtnSquareImage.value = cleanUrl
        console.log('作者区域正方形图片已更新:', cleanUrl)
        break
      case '反转按钮圆形':
        authorBtnCircleImage.value = cleanUrl
        console.log('作者区域圆形图片已更新:', cleanUrl)
        break
      default:
        console.log('未匹配的资源:', item.fileName)
    }
  })
}

const formatCommentTime = (timeStr) => {
  if (!timeStr) return ''
  
  const commentTime = new Date(timeStr)
  const now = new Date()
  const diff = now - commentTime
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 30) return `${days}天前`
  
  return timeStr.split('T')[0]
}

const loadVideoFromSession = () => {
  const storedVideo = sessionStorage.getItem('currentVideo')
  if (storedVideo) {
    try {
      const video = JSON.parse(storedVideo)
      videoData.value = {
        encode: video.encode || '',
        title: video.title || '',
        thumbnail: video.thumbnail || '',
        videoUrl: video.videoUrl || '',
        duration: video.duration || '',
        author: video.author || '发布人',
        views: video.views || 0,
        comments: video.comments || 0,
        likeCount: video.likeCount || 0,
        collectCount: video.collectCount || 0,
        shareCount: video.shareCount || 0,
        description: video.description || '',
        introduction: video.introduction || '',
        publishTime: video.publishTime || '',
        tags: video.tags || [],
        videoResolution: video.videoResolution || '',
        isMember: video.isMember || false
      }
      videoError.value = false
      return video.encode || null
    } catch (e) {
      console.error('解析视频数据失败:', e)
      ElMessage.error('视频数据解析失败')
    }
  } else {
    ElMessage.warning('未找到视频信息')
    router.push('/First')
  }
  return null
}

onMounted(async () => {
  const encodeFromSession = loadVideoFromSession()
  fetchRecommendVideos()
  
  try {
    const userInfoResult = await GetUserDate()
    if (userInfoResult.code === 0 && userInfoResult.data && userInfoResult.data.length > 0) {
      userInfoStore.setInfo(userInfoResult.data[0])
      console.log('用户信息已更新:', userInfoStore.info)
    }
  } catch (err) {
    console.error('获取用户信息失败:', err)
  }
  
  const videoEncode = route.query.encode || encodeFromSession
  if (videoEncode) {
    fetchVideoResource(videoEncode)
    fetchVideoOperateMessage(videoEncode)
    fetchVideoResolution(videoEncode)
  }
  
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  if (videoStartTime.value) {
    const now = new Date()
    const currentWatchTime = Math.floor((now - videoStartTime.value) / 1000)
    totalWatchTime.value += currentWatchTime
    console.log('页面卸载时累计观看时间:', totalWatchTime.value)
    videoStartTime.value = null
    isPlaying.value = false
  }
  recordVideoHistory()
  document.removeEventListener('click', handleClickOutside)
})

const handleClickOutside = (event) => {
  const selector = document.querySelector('.resolution-selector')
  if (selector && !selector.contains(event.target)) {
    showResolutionMenu.value = false
  }
}
</script>

<style scoped>
.video-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

/* 顶部导航栏 - 背景图片配置 */
/* 
 * ============================================
 * 推荐图片尺寸: 1920 x 70 像素 (宽 x 高)
 * 格式: JPG 或 PNG
 * 提示: 图片会自动拉伸覆盖整个导航栏
 * 
 * 替换方法: 修改 headerBgImage 变量的值
 * 示例: headerBgImage.value = '你的图片地址'
 * ============================================
 */
.video-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 70px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  z-index: 100;
}

.header-inner {
  max-width: 1800px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
}

.header-left {
  display: flex;
  align-items: center;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 8px 16px;
  border-radius: 12px;
}

.navbar-brand:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: scale(1.02);
}

.brand-logo {
  font-size: 32px;
  font-weight: 800;
  color: #00d4aa;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.brand-name {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: max-content;
}

.brand-name h2 {
  font-size: 16px;
  color: white;
  margin: 0;
  font-weight: 600;
  white-space: nowrap;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.brand-name p {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.85);
  margin: 0;
  white-space: nowrap;
}

.header-center {
  display: flex;
  align-items: center;
  gap: 20px;
  flex: 1;
  justify-content: center;
  max-width: 600px;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: nowrap;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  font-size: 14px;
  color: white;
  cursor: pointer;
  border-radius: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  text-decoration: none;
  position: relative;
  overflow: hidden;
  font-weight: 500;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5), 0 2px 4px rgba(0, 0, 0, 0.3);
  white-space: nowrap;
  flex-shrink: 0;
}

@keyframes textGlow {
  0% {
    text-shadow: 0 0 10px rgba(255, 255, 255, 0.5), 0 2px 4px rgba(0, 0, 0, 0.3);
  }
  100% {
    text-shadow: 0 0 20px rgba(255, 255, 255, 0.8), 0 0 30px rgba(255, 200, 100, 0.5), 0 2px 4px rgba(0, 0, 0, 0.3);
  }
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #ff6b6b, #feca57);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 20px;
}

.nav-item::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.4s ease, height 0.4s ease;
}

.nav-item:hover::after {
  width: 200px;
  height: 200px;
}

.nav-item:hover::before {
  opacity: 1;
}

.nav-item:hover {
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.nav-item.active {
  color: white;
  font-weight: 600;
}

.nav-item.active::before {
  opacity: 1;
  background: linear-gradient(135deg, #ff6b6b, #feca57);
}

.nav-icon {
  font-size: 18px;
  position: relative;
  z-index: 1;
  transition: transform 0.3s ease;
}

.nav-item:hover .nav-icon {
  transform: scale(1.2) rotate(10deg);
}

.nav-text {
  position: relative;
  z-index: 1;
  white-space: nowrap;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
  width: 280px;
  height: 38px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  overflow: visible;
  transition: all 0.3s ease;
}

.search-box:focus-within {
  background: #fff;
  box-shadow: 0 0 0 2px #00a1d6, 0 4px 12px rgba(0, 161, 214, 0.2);
}

.search-input {
  flex: 1;
  height: 100%;
  padding: 0 16px;
  border: none;
  background: transparent;
  font-size: 14px;
  color: #333;
  outline: none;
}

.search-input::placeholder {
  color: #999;
}

.search-history {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  padding: 12px 0;
  z-index: 1000;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px 8px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 12px;
  color: #999;
}

.clear-history {
  color: #00a1d6;
  cursor: pointer;
}

.clear-history:hover {
  color: #00b5e2;
}

.history-list {
  max-height: 200px;
  overflow-y: auto;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #333;
  font-size: 14px;
}

.history-item:hover {
  background: #f5f5f5;
  color: #00a1d6;
}

.history-item .el-icon {
  color: #999;
  font-size: 14px;
}

.search-input {
  flex: 1;
  height: 100%;
  padding: 0 16px;
  border: none;
  background: transparent;
  font-size: 14px;
  color: #333;
  outline: none;
}

.search-input::placeholder {
  color: #999;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 6px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  font-size: 14px;
  color: white;
  cursor: pointer;
  border-radius: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  font-weight: 500;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5), 0 2px 4px rgba(0, 0, 0, 0.3);
  animation: textGlow 2s ease-in-out infinite alternate;
}

.action-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #ff6b6b, #feca57);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 16px;
}

.action-item::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.4s ease, height 0.4s ease;
}

.action-item:hover::after {
  width: 150px;
  height: 150px;
}

.action-item:hover::before {
  opacity: 1;
}

.action-item:hover {
  color: white;
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.action-icon {
  font-size: 16px;
  position: relative;
  z-index: 1;
  transition: transform 0.3s ease;
}

.action-item:hover .action-icon {
  transform: scale(1.2) rotate(10deg);
}

.action-text {
  position: relative;
  z-index: 1;
  font-weight: 500;
}

.upload-btn:hover {
  background: linear-gradient(135deg, #ff6b6b, #ee5a5a);
}

.upload-btn:hover::before {
  opacity: 0;
}

.message-btn:hover {
  background: linear-gradient(135deg, #4ecdc4, #44a08d);
}

.message-btn:hover::before {
  opacity: 0;
}

.history-btn:hover {
  background: linear-gradient(135deg, #a8e6cf, #88d8b0);
}

.history-btn:hover::before {
  opacity: 0;
}

.favorite-btn:hover {
  background: linear-gradient(135deg, #ffd93d, #f9c846);
}

.favorite-btn:hover::before {
  opacity: 0;
}

.lang-switch {
  margin-left: 8px;
}

.lang-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: #f1f2f3;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 14px;
  color: #333;
  position: relative;
  overflow: hidden;
}

.lang-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea, #764ba2);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.lang-btn:hover::before {
  opacity: 1;
}

.lang-btn:hover {
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.lang-btn .el-icon {
  font-size: 16px;
  position: relative;
  z-index: 1;
}

.lang-text {
  font-weight: 500;
  position: relative;
  z-index: 1;
}

.dropdown-arrow {
  font-size: 12px;
  transition: transform 0.3s ease;
  position: relative;
  z-index: 1;
}

.lang-btn:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.lang-dropdown-menu {
  background: #fff;
  border: 1px solid #e3e5e7;
  border-radius: 12px;
  padding: 8px 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.lang-dropdown-menu :deep(.el-dropdown-menu__item) {
  color: #333;
  padding: 10px 16px;
  font-size: 14px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.lang-dropdown-menu :deep(.el-dropdown-menu__item:hover) {
  background: #f4f4f4;
  color: #00a1d6;
}

.lang-dropdown-menu :deep(.el-dropdown-menu__item.is-active) {
  background: linear-gradient(135deg, #00a1d6, #00b5e2);
  color: white;
}

.user-avatar-wrapper {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 2px;
  border-radius: 50%;
  background: linear-gradient(135deg, #00a1d6, #00b5e2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar-wrapper:hover {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 4px 12px rgba(0, 161, 214, 0.4);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid white;
  display: block;
}

/* 主要内容区域 */
.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
  max-width: 600px;
}
.page-title {
  font-size: 20px;
  font-weight: 600;
  color: white;
  margin: 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.header-right {
  display: flex;
  align-items: center;
}

.search-box {
  position: relative;
  width: 280px;
}

.search-input {
  width: 100%;
}

.search-input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.25);
  border-radius: 25px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
}

.search-input :deep(.el-input__inner) {
  background: transparent;
  border: none;
  color: white;
  padding: 0 50px 0 50px;
  font-size: 15px;
  font-weight: 500;
}

.search-input :deep(.el-input__inner)::placeholder {
  color: rgba(255, 255, 255, 0.7);
  font-weight: 400;
}

.search-input :deep(.el-input__inner):focus {
  background: rgba(255, 255, 255, 0.35);
  box-shadow: 0 0 0 0 3px rgba(255, 255, 255, 0.3);
}

.search-icon {
  position: absolute;
  left: 18px;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(255, 255, 255, 0.8);
  font-size: 18px;
}

.nav-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
.nav-menu {
  display: flex;
  align-items: center;
  gap: 8px;
} padding: 8px 14px;
  color: white;
  border-radius: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(5px);
}

.nav-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.member-btn {
  background: linear-gradient(135deg, #ffd700, #ffaa00);
  color: #333;
}

.member-btn:hover {
  background: linear-gradient(135deg, #ffcc00, #ff9900);
  box-shadow: 0 4px 12px rgba(255, 170, 0, 0.3);
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease;
}

.avatar-wrapper:hover {
  transform: scale(1.1);
}

/* 主要内容区域 */
.video-container {
  display: flex;
  gap: 24px;
  max-width: 1600px;
  margin: 0 auto;
  padding: 90px 30px 30px;
}

/* 左侧视频播放区域 */
.video-main {
  flex: 1;
  min-width: 0;
}

.video-player-wrapper {
  background: #000;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.video-player {
  position: relative;
  width: 100%;
  padding-top: 56.25%;
}

.player {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: #000;
}

.custom-video-controls {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.9));
  padding: 20px 16px 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.video-player:hover .custom-video-controls {
  opacity: 1;
}

.progress-bar-container {
  margin-bottom: 12px;
  cursor: pointer;
}

.progress-bar {
  position: relative;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  overflow: hidden;
}

.progress-bar:hover {
  height: 6px;
}

.progress-buffered {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: rgba(255, 255, 255, 0.3);
  transition: width 0.1s ease;
}

.progress-played {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: #2196f3;
  transition: width 0.1s ease;
}

.progress-thumb {
  position: absolute;
  top: 50%;
  width: 12px;
  height: 12px;
  background: white;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  opacity: 0;
  transition: opacity 0.2s ease;
}

.progress-bar:hover .progress-thumb {
  opacity: 1;
}

.controls-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.controls-left,
.controls-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.control-btn {
  background: none;
  border: none;
  color: white;
  font-size: 20px;
  cursor: pointer;
  padding: 6px;
  border-radius: 4px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 4px;
}

.control-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: scale(1.1);
}

.volume-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.volume-slider {
  width: 80px;
  height: 4px;
  -webkit-appearance: none;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  outline: none;
}

.volume-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 12px;
  height: 12px;
  background: white;
  border-radius: 50%;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.volume-slider::-webkit-slider-thumb:hover {
  transform: scale(1.2);
}

.time-display {
  color: white;
  font-size: 13px;
  font-family: 'Monaco', 'Menlo', monospace;
  min-width: 100px;
}

.resolution-control {
  position: relative;
}

.resolution-toggle {
  font-size: 14px;
}

.quality-icon {
  font-size: 16px;
}

.quality-text {
  font-size: 13px;
  font-weight: 600;
}

.resolution-dropdown {
  position: absolute;
  bottom: 100%;
  right: 0;
  margin-bottom: 8px;
  background: rgba(0, 0, 0, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 8px;
  min-width: 160px;
  box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
  animation: fadeIn 0.2s ease;
}

.dropdown-header {
  padding: 10px 14px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.dropdown-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
}

.dropdown-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.dropdown-item.active {
  background: rgba(33, 150, 243, 0.2);
  border-left-color: #2196f3;
}

.item-label {
  font-size: 14px;
  font-weight: 600;
  color: white;
}

.item-size {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.6);
}

.buffering-tip {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.1);
  z-index: 100;
  animation: fadeIn 0.3s ease;
}

.tip-content {
  display: flex;
  align-items: center;
  gap: 12px;
  color: white;
}

.tip-icon {
  font-size: 24px;
}

.tip-text {
  font-size: 14px;
  white-space: nowrap;
}

.tip-btn {
  background: #2196f3;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tip-btn:hover {
  background: #1976d2;
  transform: scale(1.05);
}

.tip-close {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.6);
  font-size: 20px;
  cursor: pointer;
  padding: 4px;
  line-height: 1;
}

.tip-close:hover {
  color: white;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 视频信息 */
.video-info {
  background: linear-gradient(135deg, #fff8e7 0%, #fef3d0 50%, #fff5e0 100%);
  border-radius: 20px;
  padding: 24px;
  margin-top: 20px;
  box-shadow: 0 4px 20px rgba(255, 193, 7, 0.15);
  border: 2px solid rgba(255, 193, 7, 0.2);
}

.video-title {
  font-size: 24px;
  font-weight: 700;
  color: #1976d2;
  margin: 0 0 16px;
  line-height: 1.4;
  padding: 16px 20px;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 50%, #e1f5fe 100%);
  border-radius: 16px;
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.12);
  border: 2px solid rgba(33, 150, 243, 0.15);
}

.video-stats-wrapper {
  position: relative;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 50%, #e1f5fe 100%);
  border-radius: 20px;
  padding: 20px 24px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(33, 150, 243, 0.15);
  border: 2px solid rgba(33, 150, 243, 0.2);
  display: flex;
  align-items: center;
  gap: 16px;
}

.stats-decor-left, .stats-decor-right {
  font-size: 28px;
  animation: float-bounce 3s ease-in-out infinite;
}

.stats-decor-left {
  animation-delay: 0s;
}

.stats-decor-right {
  animation-delay: 1.5s;
}

@keyframes float-bounce {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-8px) rotate(10deg); }
}

.video-stats {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
  flex: 1;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 12px;
  border: 1px dashed rgba(33, 150, 243, 0.3);
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.9);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.15);
}

.stat-emoji {
  font-size: 18px;
  animation: emoji-wiggle 2s ease-in-out infinite;
}

@keyframes emoji-wiggle {
  0%, 100% { transform: scale(1) rotate(0deg); }
  25% { transform: scale(1.1) rotate(-5deg); }
  75% { transform: scale(1.1) rotate(5deg); }
}

.stat-value {
  font-weight: 700;
  color: #1976d2;
  font-size: 15px;
}

.stat-label {
  color: #64b5f6;
  font-size: 12px;
}

.video-actions-wrapper {
  position: relative;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 50%, #e1f5fe 100%);
  border-radius: 20px;
  padding: 20px 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(33, 150, 243, 0.15);
  border: 2px solid rgba(33, 150, 243, 0.2);
}

.actions-decor {
  position: absolute;
  top: -12px;
  right: 20px;
  font-size: 26px;
  background: #e0f7ff;
  padding: 0 10px;
  animation: game-bounce 2s ease-in-out infinite;
}

@keyframes game-bounce {
  0%, 100% { transform: translateY(0) rotate(-10deg); }
  50% { transform: translateY(-5px) rotate(10deg); }
}

.video-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.action-btn-cartoon {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 24px;
  border: none;
  border-radius: 50px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  background: linear-gradient(135deg, #f0f0f0 0%, #e8e8e8 100%);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1), inset 0 2px 0 rgba(255, 255, 255, 0.5);
  overflow: visible;
}

.action-btn-cartoon:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15), inset 0 2px 0 rgba(255, 255, 255, 0.5);
}

.action-btn-cartoon:active {
  transform: translateY(-2px) scale(0.98);
}

.action-btn-cartoon.active {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e8e 100%);
  color: white;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.4), inset 0 2px 0 rgba(255, 255, 255, 0.3);
}

.action-btn-cartoon.collect-btn.active {
  background: linear-gradient(135deg, #ffd93d 0%, #ffec8b 100%);
  color: #8b6914;
  box-shadow: 0 4px 20px rgba(255, 217, 61, 0.5), inset 0 2px 0 rgba(255, 255, 255, 0.4);
}

.action-btn-cartoon.share-btn.active {
  background: linear-gradient(135deg, #6c5ce7 0%, #a29bfe 100%);
  color: white;
  box-shadow: 0 4px 20px rgba(108, 92, 231, 0.4), inset 0 2px 0 rgba(255, 255, 255, 0.3);
}

.btn-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  transition: all 0.3s ease;
}

.action-btn-cartoon.active .btn-icon {
  background: rgba(255, 255, 255, 0.4);
  animation: icon-bounce 0.6s ease;
}

@keyframes icon-bounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.3); }
}

.icon-emoji {
  font-size: 20px;
  line-height: 1;
  transition: transform 0.3s ease;
}

.action-btn-cartoon:hover .icon-emoji {
  transform: scale(1.2) rotate(10deg);
}

.action-btn-cartoon.active .icon-emoji {
  animation: emoji-pop 0.5s ease;
}

@keyframes emoji-pop {
  0% { transform: scale(1); }
  50% { transform: scale(1.5); }
  100% { transform: scale(1); }
}

.btn-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.btn-label {
  font-size: 14px;
  font-weight: 600;
}

.btn-count {
  font-size: 11px;
  opacity: 0.8;
}

.btn-particles {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  pointer-events: none;
}

.particle {
  position: absolute;
  width: 8px;
  height: 8px;
  background: #ff6b6b;
  border-radius: 50%;
  animation: particle-fly 0.8s ease-out forwards;
}

.particle:nth-child(1) { animation-delay: 0s; --angle: 0deg; }
.particle:nth-child(2) { animation-delay: 0.05s; --angle: 72deg; }
.particle:nth-child(3) { animation-delay: 0.1s; --angle: 144deg; }
.particle:nth-child(4) { animation-delay: 0.15s; --angle: 216deg; }
.particle:nth-child(5) { animation-delay: 0.2s; --angle: 288deg; }

@keyframes particle-fly {
  0% {
    transform: translate(0, 0) scale(1);
    opacity: 1;
  }
  100% {
    transform: translate(
      calc(cos(var(--angle)) * 40px),
      calc(sin(var(--angle)) * 40px)
    ) scale(0);
    opacity: 0;
  }
}

.particle.star {
  background: #ffd93d;
  width: 10px;
  height: 10px;
  clip-path: polygon(50% 0%, 61% 35%, 98% 35%, 68% 57%, 79% 91%, 50% 70%, 21% 91%, 32% 57%, 2% 35%, 39% 35%);
}

.particle.link {
  background: #6c5ce7;
  width: 12px;
  height: 6px;
  border-radius: 3px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: 20px;
  font-weight: 500;
  transition: all 0.3s ease;
  color: white;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5), 0 2px 4px rgba(0, 0, 0, 0.3);
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.action-btn .count {
  font-size: 12px;
  opacity: 0.9;
}

.video-description {
  padding: 20px;
  background: linear-gradient(135deg, #e0f7ff 0%, #b3e5fc 50%, #e1f5fe 100%);
  border-radius: 16px;
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.12);
  border: 2px solid rgba(33, 150, 243, 0.15);
  margin-top: 20px;
}

.video-description h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1976d2;
  margin: 0 0 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.video-description h3::before {
  content: '📝';
}

.video-description p {
  color: #555;
  line-height: 1.7;
  margin: 0;
}

/* 评论区 */
.comment-section {
  background: linear-gradient(135deg, #fff8e7 0%, #fef3d0 50%, #fff5e0 100%);
  border-radius: 24px;
  padding: 28px;
  margin-top: 24px;
  box-shadow: 0 8px 32px rgba(255, 193, 7, 0.15);
  border: 2px solid rgba(255, 193, 7, 0.2);
  position: relative;
  overflow: hidden;
}

.comment-section::before {
  content: '🎨';
  position: absolute;
  top: 20px;
  right: 30px;
  font-size: 40px;
  opacity: 0.15;
  transform: rotate(15deg);
  animation: float-decor 4s ease-in-out infinite;
}

.comment-section::after {
  content: '✨';
  position: absolute;
  bottom: 30px;
  left: 20px;
  font-size: 35px;
  opacity: 0.12;
  transform: rotate(-10deg);
  animation: float-decor 5s ease-in-out infinite reverse;
}

@keyframes float-decor {
  0%, 100% { transform: rotate(15deg) translateY(0); }
  50% { transform: rotate(15deg) translateY(-10px); }
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 22px;
  font-weight: 700;
  color: #8b6914;
  margin: 0 0 24px;
  padding-bottom: 16px;
  border-bottom: 3px dashed #ffd93d;
  position: relative;
}

.section-title::before {
  content: '💭';
  font-size: 28px;
  margin-right: 4px;
  animation: bubble-pop 2s ease-in-out infinite;
}

@keyframes bubble-pop {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.15); }
}

.section-title .el-icon {
  display: none;
}

.comment-input {
  margin-bottom: 24px;
}

.comment-input-wrapper {
  background: linear-gradient(135deg, #ffffff 0%, #fffdf5 100%);
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(255, 193, 7, 0.15);
  border: 2px dashed #ffd93d;
  position: relative;
}

.comment-input-wrapper::before {
  content: '📝';
  position: absolute;
  top: -15px;
  left: 20px;
  font-size: 30px;
  background: #fff8e7;
  padding: 0 10px;
  animation: write-wiggle 1s ease-in-out infinite;
}

@keyframes write-wiggle {
  0%, 100% { transform: rotate(-5deg); }
  50% { transform: rotate(5deg); }
}

.comment-input-header {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px dotted #ffd93d;
}

.input-label {
  font-size: 16px;
  font-weight: 600;
  color: #d4a012;
  display: flex;
  align-items: center;
  gap: 8px;
}

.input-label::before {
  content: '🖊️';
}

.comment-textarea {
  margin-bottom: 12px;
}

.comment-textarea :deep(.el-textarea__inner) {
  border-radius: 16px;
  border: 2px dashed #e6c97a;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.95);
}

.comment-textarea :deep(.el-textarea__inner):focus {
  border-color: #ffd93d;
  box-shadow: 0 0 0 4px rgba(255, 217, 61, 0.2);
}

.comment-input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.char-count {
  font-size: 12px;
  color: #999;
}

.submit-btn {
  background: linear-gradient(135deg, #ffd93d, #ffb347);
  border: none;
  border-radius: 12px;
  padding: 10px 24px;
  transition: all 0.3s ease;
  color: #8b6914;
  font-weight: 600;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 217, 61, 0.4);
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, #ffffff 0%, #fffcf0 100%);
  border-radius: 20px;
  transition: all 0.3s ease;
  border: 2px dashed #e6d5a8;
  box-shadow: 0 4px 15px rgba(255, 193, 7, 0.08);
  position: relative;
  overflow: hidden;
}

.comment-item::before {
  content: '💬';
  position: absolute;
  top: 10px;
  right: 15px;
  font-size: 24px;
  opacity: 0.1;
  transform: rotate(10deg);
}

.comment-item:hover {
  background: linear-gradient(135deg, #ffffff 0%, #fff8e0 100%);
  border-color: #ffd93d;
  box-shadow: 0 6px 20px rgba(255, 193, 7, 0.15);
  transform: translateY(-2px);
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-avatar img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #ffd93d;
  box-shadow: 0 4px 12px rgba(255, 193, 7, 0.2);
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.comment-username {
  font-weight: 600;
  color: #333;
  font-size: 15px;
}

.comment-time {
  font-size: 12px;
  color: #999;
  background: rgba(102, 126, 234, 0.08);
  padding: 2px 8px;
  border-radius: 10px;
}

.comment-text {
  color: #555;
  line-height: 1.7;
  margin: 0 0 12px;
  font-size: 14px;
}

.comment-actions {
  display: flex;
  gap: 16px;
}

.action-btn {
  color: #888;
  transition: all 0.3s ease;
  font-size: 13px;
  padding: 4px 12px;
  border-radius: 20px;
  background: rgba(102, 126, 234, 0.05);
}

.action-btn:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.12);
  transform: scale(1.05);
}

.reply-input-wrapper {
  margin-top: 16px;
  padding: 20px;
  background: linear-gradient(135deg, #ffffff 0%, #fffdf5 100%);
  border-radius: 20px;
  border: 2px dashed #ffd93d;
  box-shadow: 0 4px 20px rgba(255, 193, 7, 0.15);
  position: relative;
}

.reply-input-wrapper::before {
  content: '💬';
  position: absolute;
  top: -12px;
  left: 20px;
  font-size: 24px;
  background: #fff8e7;
  padding: 0 10px;
  animation: bubble-float 2s ease-in-out infinite;
}

@keyframes bubble-float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-3px) scale(1.1); }
}

.reply-textarea :deep(.el-textarea__inner) {
  border-radius: 16px;
  border: 2px dashed #e6c97a;
  background: rgba(255, 255, 255, 0.95);
  transition: all 0.3s ease;
}

.reply-textarea :deep(.el-textarea__inner):focus {
  border-color: #ffd93d;
  box-shadow: 0 0 0 4px rgba(255, 217, 61, 0.2);
}

.reply-input-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
}

.reply-input-actions .el-button {
  border-radius: 10px;
  font-weight: 600;
}

.reply-input-actions .el-button--primary {
  background: linear-gradient(135deg, #ffd93d, #ffb347);
  border: none;
  color: #8b6914;
}

.reply-input-actions .el-button--primary:hover {
  box-shadow: 0 4px 12px rgba(255, 217, 61, 0.4);
  transform: translateY(-2px);
}

.reply-list {
  margin-top: 16px;
  padding-left: 16px;
  border-left: 3px dashed #ffd93d;
}

.reply-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #ffffff 0%, #fffcf0 100%);
  border-radius: 16px;
  border: 1px dashed #e6d5a8;
  margin-bottom: 10px;
  transition: all 0.3s ease;
  position: relative;
}

.reply-item::before {
  content: '💭';
  position: absolute;
  top: 8px;
  right: 12px;
  font-size: 16px;
  opacity: 0.15;
}

.reply-item:hover {
  background: linear-gradient(135deg, #ffffff 0%, #fff8e0 100%);
  border-color: #ffd93d;
  box-shadow: 0 4px 15px rgba(255, 193, 7, 0.12);
  transform: translateX(4px);
}

.reply-item:last-child {
  margin-bottom: 0;
}

.reply-avatar {
  flex-shrink: 0;
}

.reply-avatar img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #ffd93d;
  box-shadow: 0 2px 8px rgba(255, 193, 7, 0.2);
}

.reply-content {
  flex: 1;
  min-width: 0;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
  flex-wrap: wrap;
}

.reply-username {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.reply-to {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #999;
  font-size: 12px;
}

.reply-to-name {
  color: #409eff;
}

.reply-time {
  font-size: 12px;
  color: #999;
}

.reply-text {
  color: #666;
  line-height: 1.5;
  margin: 0 0 6px;
  font-size: 14px;
}

.reply-actions {
  display: flex;
  gap: 12px;
}

/* 右侧推荐视频列表 */
.video-sidebar {
  width: 360px;
  flex-shrink: 0;
}

.recommend-section {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 90px;
}

.sidebar-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recommend-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.recommend-item:hover {
  background: #f0f1f3;
  transform: translateX(4px);
}

.recommend-thumbnail {
  position: relative;
  width: 120px;
  height: 68px;
  flex-shrink: 0;
  border-radius: 8px;
  overflow: hidden;
}

.recommend-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.recommend-duration {
  position: absolute;
  bottom: 4px;
  right: 4px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
}

.recommend-info {
  flex: 1;
  min-width: 0;
}

.recommend-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.recommend-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

/* 视频加载失败提示 */
.video-error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.error-content {
  text-align: center;
  padding: 40px;
}

.error-image {
  width: 300px;
  height: 200px;
  object-fit: cover;
  border-radius: 16px;
  margin-bottom: 20px;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.error-text h3 {
  color: #fff;
  font-size: 24px;
  margin: 0 0 10px;
}

.error-text p {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  margin: 0;
}

/* 翻转动画 */
.flip-container {
  perspective: 1000px;
}

.flip-card {
  position: relative;
  width: 100%;
  height: 600px;
  transition: transform 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  transform-style: preserve-3d;
}

.flip-container.flipped .flip-card {
  transform: rotateY(180deg);
}

.flip-front,
.flip-back {
  width: 100%;
  height: 600px;
  backface-visibility: hidden;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  background: white;
  position: absolute;
  top: 0;
  left: 0;
  overflow: hidden;
}

.flip-front {
  z-index: 2;
}

.flip-back {
  transform: rotateY(180deg);
}

/* 配套资料区域头部 */
.files-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 18px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  position: relative;
  overflow: hidden;
  border-radius: 16px 16px 0 0;
}

.files-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.files-header:hover::before {
  left: 100%;
}

.files-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
  z-index: 1;
}

.files-icon {
  font-size: 20px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.files-header-left h4 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 作者其他视频区域头部 */
.author-videos-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 18px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  position: relative;
  overflow: hidden;
  border-radius: 16px 16px 0 0;
}

.author-videos-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.author-videos-header:hover::before {
  left: 100%;
}

.author-videos-left {
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
  z-index: 1;
}

.author-videos-icon {
  font-size: 20px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.author-videos-left h4 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 20px 0;
}

.sidebar-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

/* 翻转按钮样式 */
.flip-action-wrapper {
  padding: 16px 20px 20px;
  margin-top: auto;
}

.flip-action-btn {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 18px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.35);
  position: relative;
  overflow: hidden;
}

.flip-action-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.flip-action-btn:hover::before {
  left: 100%;
}

.flip-action-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.45);
}

.flip-action-btn:active {
  transform: translateY(-1px);
}

.flip-action-btn .btn-images {
  display: flex;
  gap: 4px;
}

.flip-action-btn .btn-img-square {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.3);
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.flip-action-btn:hover .btn-img-square {
  transform: scale(1.05) rotate(360deg);
  border-color: rgba(255, 255, 255, 0.5);
}

.flip-action-btn .btn-content {
  flex: 1;
  text-align: left;
}

.flip-action-btn .btn-title {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: white;
  margin-bottom: 2px;
}

.flip-action-btn .btn-subtitle {
  display: block;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.8);
}

.flip-action-btn .btn-arrow {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.flip-action-btn .btn-img-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.flip-action-btn:hover .btn-arrow {
  transform: translateX(3px);
}

.flip-action-btn:hover .btn-img-circle {
  transform: scale(1.1);
  border-color: rgba(255, 255, 255, 0.5);
}

.flip-action-btn.back-btn {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  box-shadow: 0 4px 15px rgba(245, 87, 108, 0.35);
}

.flip-action-btn.back-btn:hover {
  box-shadow: 0 8px 25px rgba(245, 87, 108, 0.45);
}

.flip-action-btn.back-btn:hover .btn-arrow {
  transform: translateX(-3px);
}

/* 推荐列表 */
.recommend-section {
  display: flex;
  flex-direction: column;
}

.recommend-list {
  flex: 1;
  padding: 0 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 450px;
  overflow-y: auto;
}

/* 文件网格 */
.files-section {
  display: flex;
  flex-direction: column;
}

.files-grid {
  flex: 1;
  padding: 0 20px;
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  max-height: 450px;
  overflow-y: auto;
}

.files-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  margin-top: 16px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-text {
  font-size: 14px;
  color: #999;
  text-align: center;
}

.file-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 10px;
  background: linear-gradient(145deg, #ffffff, #f0f0f0);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  border: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: relative;
  overflow: hidden;
  aspect-ratio: 1;
  min-width: 80px;
}

.file-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  opacity: 0;
  transition: all 0.6s ease;
}

.file-card::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    45deg,
    transparent 30%,
    rgba(255, 255, 255, 0.3) 50%,
    transparent 70%
  );
  transform: translateX(-100%) rotate(45deg);
  transition: transform 0.8s ease;
  pointer-events: none;
}

.file-card:hover::before {
  opacity: 1;
}

.file-card:hover::after {
  transform: translateX(100%) rotate(45deg);
}

.file-card:hover {
  border-color: transparent;
  transform: translateY(-10px) scale(1.05);
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.2),
    0 0 0 1px rgba(255, 255, 255, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.file-card:active {
  transform: translateY(-5px) scale(1.02);
  transition: all 0.15s ease;
}

/* PDF 文件样式 */
.file-type-pdf::before {
  background: linear-gradient(90deg, #ff416c, #ff4b2b);
}
.file-type-pdf:hover {
  background: linear-gradient(145deg, #ff416c, #ff4b2b);
}
.file-type-pdf .file-icon {
  background: linear-gradient(135deg, rgba(255, 65, 108, 0.15), rgba(255, 75, 43, 0.15));
  color: #ff416c;
}

/* Word 文件样式 */
.file-type-word::before {
  background: linear-gradient(90deg, #2b5876, #4e4376);
}
.file-type-word:hover {
  background: linear-gradient(145deg, #2b5876, #4e4376);
}
.file-type-word .file-icon {
  background: linear-gradient(135deg, rgba(43, 88, 118, 0.15), rgba(78, 67, 118, 0.15));
  color: #2b5876;
}

/* Excel 文件样式 */
.file-type-excel::before {
  background: linear-gradient(90deg, #11998e, #38ef7d);
}
.file-type-excel:hover {
  background: linear-gradient(145deg, #11998e, #38ef7d);
}
.file-type-excel .file-icon {
  background: linear-gradient(135deg, rgba(17, 153, 142, 0.15), rgba(56, 239, 125, 0.15));
  color: #11998e;
}

/* PPT 文件样式 */
.file-type-ppt::before {
  background: linear-gradient(90deg, #ff9966, #ff5e62);
}
.file-type-ppt:hover {
  background: linear-gradient(145deg, #ff9966, #ff5e62);
}
.file-type-ppt .file-icon {
  background: linear-gradient(135deg, rgba(255, 153, 102, 0.15), rgba(255, 94, 98, 0.15));
  color: #ff9966;
}

/* 图片文件样式 */
.file-type-image::before {
  background: linear-gradient(90deg, #667eea, #764ba2);
}
.file-type-image:hover {
  background: linear-gradient(145deg, #667eea, #764ba2);
}
.file-type-image .file-icon {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
  color: #667eea;
}

/* 视频文件样式 */
.file-type-video::before {
  background: linear-gradient(90deg, #f093fb, #f5576c);
}
.file-type-video:hover {
  background: linear-gradient(145deg, #f093fb, #f5576c);
}
.file-type-video .file-icon {
  background: linear-gradient(135deg, rgba(240, 147, 251, 0.15), rgba(245, 87, 108, 0.15));
  color: #f093fb;
}

/* 音频文件样式 */
.file-type-audio::before {
  background: linear-gradient(90deg, #00c6ff, #0072ff);
}
.file-type-audio:hover {
  background: linear-gradient(145deg, #00c6ff, #0072ff);
}
.file-type-audio .file-icon {
  background: linear-gradient(135deg, rgba(0, 198, 255, 0.15), rgba(0, 114, 255, 0.15));
  color: #00c6ff;
}

/* ZIP 压缩文件样式 */
.file-type-zip::before {
  background: linear-gradient(90deg, #f7971e, #ffd200);
}
.file-type-zip:hover {
  background: linear-gradient(145deg, #f7971e, #ffd200);
}
.file-type-zip .file-icon {
  background: linear-gradient(135deg, rgba(247, 151, 30, 0.15), rgba(255, 210, 0, 0.15));
  color: #f7971e;
}

.file-icon {
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  margin-bottom: 10px;
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
}

.file-icon::before {
  content: '';
  position: absolute;
  inset: -4px;
  border-radius: 18px;
  background: transparent;
  transition: all 0.6s ease;
  opacity: 0;
}

.file-card:hover .file-icon {
  background: rgba(255, 255, 255, 0.25);
  color: white;
  transform: scale(1.15) rotate(5deg);
}

.file-card:hover .file-icon::before {
  background: rgba(255, 255, 255, 0.1);
  opacity: 1;
  animation: pulse-ring 1.5s ease-out infinite;
}

@keyframes pulse-ring {
  0% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.3;
  }
  100% {
    transform: scale(1.2);
    opacity: 0;
  }
}

.file-info {
  text-align: center;
  width: 100%;
  transition: all 0.6s ease;
}

.file-name {
  font-size: 11px;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 3px;
  transition: all 0.6s ease;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.file-size {
  font-size: 10px;
  color: #999;
  transition: all 0.6s ease;
  opacity: 0.8;
}

.file-card:hover .file-name {
  color: white;
  transform: translateY(-2px);
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.file-card:hover .file-size {
  color: rgba(255, 255, 255, 0.85);
  opacity: 1;
}

.file-desc {
  font-size: 9px;
  color: #aaa;
  display: block;
  margin-top: 4px;
  line-height: 1.3;
  max-height: 24px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.file-card:hover .file-desc {
  color: rgba(255, 255, 255, 0.7);
}

.file-icon-emoji {
  font-size: 28px;
  line-height: 1;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .video-container {
    flex-direction: column;
  }
  
  .video-sidebar {
    width: 100%;
  }
  
  .recommend-section {
    position: static;
  }
  
  .recommend-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .video-header {
    padding: 0 15px;
  }
  
  .video-container {
    padding: 80px 15px 15px;
  }
  
  .video-title {
    font-size: 20px;
  }
  
  .video-stats {
    gap: 12px;
  }
  
  .video-actions {
    gap: 8px;
  }
  
  .action-btn {
    padding: 8px 16px;
    font-size: 14px;
  }
  
  .files-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .flip-card,
  .flip-front,
  .flip-back {
    height: auto;
    min-height: 500px;
  }
}

@media (max-width: 480px) {
  .files-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
  
  .file-card {
    padding: 14px 8px;
  }
  
  .file-icon {
    width: 44px;
    height: 44px;
  }
  
  .file-name {
    font-size: 10px;
  }
  
  .file-size {
    font-size: 9px;
  }
  
  .flip-action-btn {
    padding: 12px 14px;
    gap: 10px;
  }
  
  .flip-action-btn .btn-img-square {
    width: 36px;
    height: 36px;
  }
  
  .flip-action-btn .btn-img-circle {
    width: 28px;
    height: 28px;
  }
}

/* 作者信息卡片 */
.author-info-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.08));
  border-radius: 12px;
  margin: 12px;
  border: 1px solid rgba(102, 126, 234, 0.15);
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.author-info-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.3);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.12), rgba(118, 75, 162, 0.12));
}

.author-info-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2);
}

.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid transparent;
  background: linear-gradient(white, white) padding-box, linear-gradient(135deg, #667eea, #764ba2) border-box;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.author-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.author-meta .meta-item {
  font-size: 12px;
  color: #666;
}

.author-stats {
  font-size: 12px;
  color: #764ba2;
  font-weight: 500;
}

/* 右侧推荐视频区域 - 跟随页面滚动 */
.sidebar-recommend {
  position: relative;
  width: 100%;
  height: fit-content;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  margin-top: 100px;
}

.sidebar-recommend-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 18px;
  background: linear-gradient(135deg, #00d4aa 0%, #00a8e8 100%);
  color: white;
  position: relative;
  overflow: hidden;
}

.sidebar-recommend-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.sidebar-recommend-header:hover::before {
  left: 100%;
}

.recommend-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
  z-index: 1;
}

.recommend-icon {
  font-size: 20px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.recommend-header-left h4 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.recommend-header-right {
  position: relative;
  z-index: 1;
}

.recommend-header-img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease;
}

.recommend-header-img:hover {
  transform: scale(1.1) rotate(10deg);
}

.header-action-btn {
  color: white;
  opacity: 0.9;
  transition: all 0.3s ease;
  padding: 4px 8px;
}

.header-action-btn:hover {
  opacity: 1;
  transform: scale(1.1);
}

.header-action-btn:hover .el-icon {
  transform: rotate(180deg);
}

.sidebar-recommend-list {
  padding: 0;
  overflow: visible;
}

.sidebar-recommend-item {
  display: flex;
  gap: 12px;
  padding: 10px 14px;
  border-radius: 0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sidebar-recommend-item:hover {
  background: #f5f5f5;
  transform: translateX(-4px);
}

.sidebar-recommend-thumbnail {
  position: relative;
  width: 120px;
  height: 68px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.sidebar-recommend-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.sidebar-recommend-duration {
  position: absolute;
  bottom: 4px;
  right: 4px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
}

.sidebar-recommend-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
}

.sidebar-recommend-title {
  margin: 0 0 6px;
  font-size: 13px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.sidebar-recommend-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 11px;
  color: #999;
}

.loading-more-recommend {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  color: #999;
  font-size: 12px;
}

.loading-more-recommend .el-icon {
  font-size: 16px;
}

.report-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #fff5f5 0%, #fff 100%);
  border-radius: 12px;
  border: 1px solid #ffe0e0;
}

.report-icon {
  flex-shrink: 0;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 2px 12px rgba(245, 108, 108, 0.2);
}

.report-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e1e2d;
}

.report-info p {
  margin: 0;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

.report-form {
  margin-top: 20px;
}

.report-form .el-form-item__label {
  font-weight: 500;
  color: #1e1e2d;
  font-size: 14px;
}

.input-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
  line-height: 1.5;
}

.report-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 8px;
}

.report-footer .el-button {
  min-width: 100px;
}

.report-footer .el-button--danger {
  background: linear-gradient(135deg, #f56c6c 0%, #e64545 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.3);
}

.report-footer .el-button--danger:hover {
  background: linear-gradient(135deg, #e64545 0%, #d63030 100%);
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.4);
}
</style>