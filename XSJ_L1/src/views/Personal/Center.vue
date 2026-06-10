<template>
  <div class="center-page">
    <header class="center-header" :style="{ backgroundImage: `url(${headerBgImage})` }">
      <div class="header-inner">
        <div class="header-left">
          <div class="navbar-brand" @click="goHome">
            <h1 class="brand-logo">XSJ</h1>
            <div class="brand-name">
              <h2>{{ locale === 'en' ? 'New World AI Education' : t('brand.name') }}</h2>
              <p>{{ locale === 'en' ? 'AI-driven Education' : t('brand.englishName') }}</p>
            </div>
          </div>
        </div>
        
        <div class="header-center">
          <nav class="nav-menu">
            <a class="nav-item" :class="{ active: currentNav === 'home' }" @click="goToFirst">
              <span class="nav-icon">🏠</span>
              <span class="nav-text">{{ t('nav.home') }}</span>
            </a>
            <a class="nav-item" :class="{ active: currentNav === 'ai' }" @click="goToAI">
              <span class="nav-icon">🤖</span>
              <span class="nav-text">AI</span>
            </a>
          </nav>
        </div>
        
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
          </div>
          
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
          
          <div class="user-avatar-wrapper active">
            <img :src="userInfoStore.info.avatar || 'https://picsum.photos/100/100?random=1'" :alt="userInfoStore.info.nickName || '用户头像'" class="user-avatar" />
          </div>
        </div>
      </div>
    </header>

    <div class="center-container">
      <div class="center-content">
        <div class="user-profile-section">
          <div class="profile-banner">
            <div class="banner-media">
              <video 
                v-if="isVideoBackground" 
                :src="backgroundImage" 
                autoplay 
                loop 
                muted 
                playsinline
                class="banner-video"
              ></video>
              <img 
                v-else-if="backgroundImage && !isVideoBackground"
                :src="backgroundImage"
                class="banner-gif"
                alt="背景图"
              />
              <div 
                v-else
                class="banner-image" 
                :style="{ backgroundImage: `url(${defaultBannerImage})` }"
              ></div>
            </div>
            <div class="banner-overlay"></div>
            
            <div class="profile-content">
              <div class="profile-main">
                <div class="avatar-section">
                  <div class="avatar-wrapper">
                    <img :src="userInfoStore.info.avatar || 'https://picsum.photos/200/200?random=avatar'" :alt="userInfoStore.info.nickName || '用户'" class="user-avatar-large" />
                    <div class="avatar-badge" v-if="userInfoStore.info.uiMember === '1'">
                      <span>VIP</span>
                    </div>
                  </div>
                  <button class="edit-profile-btn" @click="handleEditProfile">
                    <el-icon><el-icon-edit /></el-icon>
                    <span>{{ t('center.editProfile') }}</span>
                  </button>
                  <button class="view-profile-btn" @click="handleViewProfile">
                    <el-icon><el-icon-user /></el-icon>
                    <span>查看个人资料</span>
                  </button>
                </div>
                
                <div class="info-section">
                  <div class="info-card">
                    <div class="user-header">
                      <h1 class="user-nickname">{{ userInfoStore.info.nickName || '用户' }}</h1>
                      <div class="user-badges">
                        <span class="badge member-badge" v-if="userInfoStore.info.uiMember === '1'">
                          <span class="badge-icon">👑</span>
                          {{ t('center.member') }}
                        </span>
                      </div>
                    </div>
                    
                    <p class="user-signature" v-if="userInfoStore.info.signature || userInfoStore.info.introduction">
                      {{ userInfoStore.info.signature || userInfoStore.info.introduction }}
                    </p>
                    
                    <div class="user-meta-compact">
                      <div class="meta-row" v-if="userInfoStore.info.school">
                        <span class="meta-label">学校</span>
                        <span class="meta-value">{{ userInfoStore.info.school }}</span>
                      </div>
                      <div class="meta-row" v-if="userInfoStore.info.leavel">
                        <span class="meta-label">学历</span>
                        <span class="meta-value">{{ userInfoStore.info.leavel }}</span>
                      </div>
                      <div class="meta-row" v-if="userInfoStore.info.address">
                        <span class="meta-label">地址</span>
                        <span class="meta-value">{{ userInfoStore.info.address }}</span>
                      </div>
                      <div class="meta-row" v-if="userInfoStore.info.email">
                        <span class="meta-label">邮箱</span>
                        <span class="meta-value">{{ userInfoStore.info.email }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="stats-section">
                <div class="stats-grid">
                  <div class="stat-card" @click="handleStatClick('following')">
                    <div class="stat-icon">👥</div>
                    <div class="stat-info">
                      <span class="stat-value">{{ formatNumber(userInfo.following) }}</span>
                      <span class="stat-label">{{ t('center.following') }}</span>
                    </div>
                  </div>
                  <div class="stat-card" @click="handleStatClick('videos')">
                    <div class="stat-icon">🎬</div>
                    <div class="stat-info">
                      <span class="stat-value">{{ formatNumber(userInfo.videoCount) }}</span>
                      <span class="stat-label">{{ t('center.videos') }}</span>
                    </div>
                  </div>
                  <div class="stat-card" @click="handleStatClick('likes')">
                    <div class="stat-icon">❤️</div>
                    <div class="stat-info">
                      <span class="stat-value">{{ formatNumber(userInfo.likeCount) }}</span>
                      <span class="stat-label">{{ t('center.likes') }}</span>
                    </div>
                  </div>
                  <div class="stat-card" @click="handleStatClick('views')">
                    <div class="stat-icon">👁️</div>
                    <div class="stat-info">
                      <span class="stat-value">{{ formatNumber(userInfo.viewCount) }}</span>
                      <span class="stat-label">{{ t('center.views') }}</span>
                    </div>
                  </div>
                  <div class="stat-card" @click="handleStatClick('collections')">
                    <div class="stat-icon">⭐</div>
                    <div class="stat-info">
                      <span class="stat-value">{{ formatNumber(userInfo.collectCount) }}</span>
                      <span class="stat-label">{{ t('center.collections') }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="content-section">
          <div class="tab-menu">
            <div class="tab-list">
              <div 
                class="tab-item" 
                :class="{ active: activeTab === 'home' }"
                @click="activeTab = 'home'"
              >
                <span class="tab-icon">🏠</span>
                <span class="tab-text">{{ t('center.tabHome') }}</span>
                <span class="tab-count" v-if="userInfo.videoCount">{{ userInfo.videoCount }}</span>
              </div>
              <div 
                class="tab-item" 
                :class="{ active: activeTab === 'upload' }"
                @click="activeTab = 'upload'"
              >
                <span class="tab-icon">📤</span>
                <span class="tab-text">{{ t('center.tabUpload') }}</span>
                <span class="tab-count" v-if="userInfo.uploadCount">{{ userInfo.uploadCount }}</span>
              </div>
              <div 
                class="tab-item" 
                :class="{ active: activeTab === 'following' }"
                @click="activeTab = 'following'"
              >
                <span class="tab-icon">👥</span>
                <span class="tab-text">{{ t('center.tabFollowing') }}</span>
                <span class="tab-count" v-if="userInfo.following">{{ userInfo.following }}</span>
              </div>
              <div 
                class="tab-item" 
                :class="{ active: activeTab === 'collection' }"
                @click="activeTab = 'collection'"
              >
                <span class="tab-icon">⭐</span>
                <span class="tab-text">{{ t('center.tabCollection') }}</span>
                <span class="tab-count" v-if="userInfo.collectCount">{{ userInfo.collectCount }}</span>
              </div>
              <div 
                class="tab-item" 
                :class="{ active: activeTab === 'like' }"
                @click="activeTab = 'like'"
              >
                <span class="tab-icon">❤️</span>
                <span class="tab-text">{{ t('center.tabLike') }}</span>
                <span class="tab-count" v-if="userInfo.likeCount">{{ userInfo.likeCount }}</span>
              </div>
            </div>
            
            <div class="tab-actions">
              <el-input
                v-model="searchKeyword"
                :placeholder="t('center.searchPlaceholder')"
                class="search-input"
                clearable
              >
                <template #prefix>
                  <el-icon><el-icon-search /></el-icon>
                </template>
              </el-input>
              <el-select v-model="sortType" class="sort-select" :placeholder="t('center.sortBy')">
                <el-option :label="t('center.sortNewest')" value="newest" />
                <el-option :label="t('center.sortPopular')" value="popular" />
                <el-option :label="t('center.sortViews')" value="views" />
              </el-select>
            </div>
          </div>

          <div class="tab-content">
            <div v-if="activeTab === 'home'" class="content-panel home-panel">
              <div class="video-grid" v-if="userVideos.length > 0">
                <div 
                  class="video-card" 
                  v-for="video in filteredVideos" 
                  :key="video.encode"
                  @click="playVideo(video)"
                >
                  <div class="video-thumbnail">
                    <img :src="video.thumbnail" :alt="video.title" />
                    <div class="video-duration">{{ video.duration }}</div>
                    <div class="video-overlay">
                      <div class="play-icon">▶</div>
                      <div class="video-hover-info">
                        <div class="hover-meta">
                          <span class="meta-item">
                            <el-icon><el-icon-view /></el-icon>
                            {{ formatNumber(video.views) }}
                          </span>
                          <span class="meta-item">
                            <el-icon><el-icon-star /></el-icon>
                            {{ formatNumber(video.likeCount) }}
                          </span>
                          <span class="meta-item">
                            <el-icon><el-icon-collection /></el-icon>
                            {{ formatNumber(video.collectCount) }}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="video-info">
                    <h4 class="video-title">{{ video.title }}</h4>
                    <div class="video-actions-row">
                      <el-button size="small" text @click.stop="handleDeleteVideo(video)" title="删除">
                        <el-icon><el-icon-delete /></el-icon>
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
              <div class="empty-state" v-else>
                <div class="empty-icon">📭</div>
                <p class="empty-text">{{ t('center.noVideos') }}</p>
              </div>
            </div>

            <div v-if="activeTab === 'upload'" class="content-panel upload-panel">
              <div class="upload-filter-bar">
                <div class="filter-tabs">
                  <span 
                    class="filter-tab" 
                    :class="{ active: uploadFilterStatus === 2 }"
                    @click="uploadFilterStatus = 2"
                  >
                    {{ t('center.filterPending') }}
                    <span class="filter-count" v-if="pendingVideoCount > 0">{{ pendingVideoCount }}</span>
                  </span>
                  <span 
                    class="filter-tab" 
                    :class="{ active: uploadFilterStatus === 1 }"
                    @click="uploadFilterStatus = 1"
                  >
                    {{ t('center.filterApproved') }}
                    <span class="filter-count" v-if="publishedVideoCount > 0">{{ publishedVideoCount }}</span>
                  </span>
                  <span 
                    class="filter-tab" 
                    :class="{ active: uploadFilterStatus === 0 }"
                    @click="uploadFilterStatus = 0"
                  >
                    {{ t('center.filterRejected') }}
                    <span class="filter-count" v-if="rejectedVideoCount > 0">{{ rejectedVideoCount }}</span>
                  </span>
                  <span 
                    class="filter-tab" 
                    :class="{ active: uploadFilterStatus === -1 }"
                    @click="uploadFilterStatus = -1"
                  >
                    {{ t('center.filterAll') }}
                    <span class="filter-count">{{ userVideoPending.length }}</span>
                  </span>
                </div>
              </div>
              <div class="video-grid" v-if="uploadedVideos.length > 0">
                <div 
                  class="video-card" 
                  v-for="video in uploadedVideos" 
                  :key="video.encode"
                  @click="playVideo(video)"
                >
                  <div class="video-thumbnail">
                    <img :src="video.thumbnail" :alt="video.title" />
                    <div class="video-duration">{{ video.duration }}</div>
                    <div class="video-status-badge" :class="getStatusBadgeClass(video.videoStatus)">
                      {{ getStatusBadgeText(video.videoStatus) }}
                    </div>
                    <div class="video-overlay">
                      <div class="play-icon">▶</div>
                      <div class="video-hover-info">
                        <h4 class="hover-title">{{ video.title }}</h4>
                        <div class="hover-meta">
                          <span class="meta-item">
                            <el-icon><el-icon-view /></el-icon>
                            {{ formatNumber(video.views) }}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="video-info">
                    <h4 class="video-title">{{ video.title }}</h4>
                    <div class="video-actions-row">
                      <el-button size="small" text @click.stop="editVideo(video)">
                        <el-icon><el-icon-edit /></el-icon>
                      </el-button>
                      <el-button size="small" text @click.stop="deleteVideo(video)">
                        <el-icon><el-icon-delete /></el-icon>
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
              <div class="empty-state" v-else>
                <div class="empty-icon">📤</div>
                <p class="empty-text">{{ t('center.noUploads') }}</p>
                <el-button type="primary" @click="handleUpload">
                  <el-icon><el-icon-upload /></el-icon>
                  {{ t('video.upload') }}
                </el-button>
              </div>
            </div>

            <div v-if="activeTab === 'following'" class="content-panel following-panel">
              <div class="user-grid" v-if="followingList.length > 0">
                <div class="user-card" v-for="user in followingList" :key="user.id">
                  <div class="user-avatar-wrapper">
                    <img :src="user.avatar" :alt="user.nickname" class="user-avatar" />
                    <div class="online-dot" v-if="user.online"></div>
                  </div>
                  <div class="user-info">
                    <h4 class="user-name">{{ user.nickname }}</h4>
                    <p class="user-desc">{{ user.signature || t('center.noSignature') }}</p>
                    <div class="user-stats">
                      <span>{{ formatNumber(user.followers) }} {{ t('center.followers') }}</span>
                      <span>{{ user.videoCount }} {{ t('center.videos') }}</span>
                    </div>
                  </div>
                  <el-button 
                    :type="user.isFollowing ? 'default' : 'primary'" 
                    size="small"
                    @click="toggleFollow(user)"
                  >
                    {{ user.isFollowing ? t('center.following') : t('center.follow') }}
                  </el-button>
                </div>
              </div>
              <div class="empty-state" v-else>
                <div class="empty-icon">👥</div>
                <p class="empty-text">{{ t('center.noFollowing') }}</p>
              </div>
            </div>

            <div v-if="activeTab === 'collection'" class="content-panel collection-panel">
              <div class="video-grid" v-if="collectedVideos.length > 0">
                <div 
                  class="video-card" 
                  v-for="video in filteredCollectedVideos" 
                  :key="video.encode"
                  @click="playVideo(video)"
                >
                  <div class="video-thumbnail">
                    <img :src="video.thumbnail" :alt="video.title" />
                    <div class="video-duration">{{ video.duration }}</div>
                    <div class="video-overlay">
                      <div class="play-icon">▶</div>
                      <div class="video-hover-info">
                        <div class="hover-meta">
                          <span class="meta-item">
                            <el-icon><el-icon-view /></el-icon>
                            {{ formatNumber(video.views) }}
                          </span>
                          <span class="meta-item">
                            <el-icon><el-icon-star /></el-icon>
                            {{ formatNumber(video.likeCount) }}
                          </span>
                          <span class="meta-item">
                            <el-icon><el-icon-collection /></el-icon>
                            {{ formatNumber(video.collectCount) }}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="video-info">
                    <h4 class="video-title">{{ video.title }}</h4>
                  </div>
                </div>
              </div>
              <div class="empty-state" v-else>
                <div class="empty-icon">⭐</div>
                <p class="empty-text">{{ t('center.noCollections') }}</p>
              </div>
            </div>

            <div v-if="activeTab === 'like'" class="content-panel like-panel">
              <div class="video-grid" v-if="filteredLikedVideos.length > 0">
                <div 
                  class="video-card" 
                  v-for="video in filteredLikedVideos" 
                  :key="video.encode"
                  @click="playVideo(video)"
                >
                  <div class="video-thumbnail">
                    <img :src="video.thumbnail" :alt="video.title" />
                    <div class="video-duration">{{ video.duration }}</div>
                    <div class="like-badge">❤️</div>
                    <div class="video-overlay">
                      <div class="play-icon">▶</div>
                      <div class="video-hover-info">
                        <div class="hover-meta">
                          <span class="meta-item">
                            <el-icon><el-icon-view /></el-icon>
                            {{ formatNumber(video.views) }}
                          </span>
                          <span class="meta-item">
                            <el-icon><el-icon-star /></el-icon>
                            {{ formatNumber(video.likeCount) }}
                          </span>
                          <span class="meta-item">
                            <el-icon><el-icon-collection /></el-icon>
                            {{ formatNumber(video.collectCount) }}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="video-info">
                    <h4 class="video-title">{{ video.title }}</h4>
                    <div class="video-author">
                      <img :src="video.authorAvatar" class="author-avatar" />
                      <span>{{ video.author }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="empty-state" v-else>
                <div class="empty-icon">❤️</div>
                <p class="empty-text">{{ t('center.noLikes') }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 视频预览对话框 -->
  <el-dialog
    v-model="previewDialogVisible"
    :title="previewVideo ? previewVideo.title : '视频预览'"
    width="1000px"
    :close-on-click-modal="false"
    class="preview-dialog"
  >
    <div v-if="previewVideo" class="preview-content">
      <div class="preview-left">
        <div class="preview-video-wrapper">
          <video :src="previewVideo.videoUrl" controls class="preview-video-player"></video>
        </div>
        <div class="preview-info">
          <h3 class="preview-title">{{ previewVideo.title }}</h3>
          <div class="preview-meta">
            <span class="meta-item">
              <el-icon><el-icon-view /></el-icon>
              {{ formatNumber(previewVideo.views) }} 观看
            </span>
            <span class="meta-item">
              <el-icon><el-icon-star /></el-icon>
              {{ formatNumber(previewVideo.likeCount) }} 点赞
            </span>
            <span class="meta-item">
              <el-icon><el-icon-clock /></el-icon>
              {{ previewVideo.duration }}
            </span>
          </div>
          <p class="preview-status" v-if="previewVideo.videoStatus === 2">
            <el-icon><el-icon-info-filled /></el-icon>
            视频正在审核中，暂时无法进行其他操作
          </p>
          <p class="preview-status rejected" v-else-if="previewVideo.videoStatus === 0">
            <el-icon><el-icon-warning-filled /></el-icon>
            审核不通过
          </p>
        </div>
      </div>
      
      <div class="preview-right">
        <div class="resource-section">
          <div class="resource-header">
            <h4>视频资源</h4>
            <el-button 
              v-if="previewVideo.videoStatus === 1" 
              type="primary" 
              size="small" 
              @click="showUploadResourceDialog = true"
            >
              <el-icon><el-icon-upload /></el-icon>
              上传资源
            </el-button>
          </div>
          
          <div class="resource-list" v-if="videoResources.length > 0">
            <div class="resource-item" v-for="(res, index) in videoResources" :key="index">
              <div class="resource-icon">
                <el-icon v-if="getResourceIcon(res.type)"><component :is="getResourceIcon(res.type)" /></el-icon>
              </div>
              <div class="resource-info">
                <span class="resource-name">{{ res.name }}</span>
                <span class="resource-meta">{{ res.format }} · {{ formatFileSize(res.size) }}</span>
              </div>
              <div class="resource-actions">
                <el-button type="primary" link size="small" @click="downloadResource(res)" title="下载">
                  <el-icon><el-icon-download /></el-icon>
                </el-button>
                <el-button type="danger" link size="small" @click="handleDeleteResource(res)" title="删除">
                  <el-icon><el-icon-delete /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
          <div class="resource-empty" v-else>
            <el-icon class="empty-icon"><el-icon-folder-opened /></el-icon>
            <p v-if="previewVideo.videoStatus === 1">暂无资源，点击上方按钮上传</p>
            <p v-else-if="previewVideo.videoStatus === 2">视频审核中，审核通过后可上传资源</p>
            <p v-else-if="previewVideo.videoStatus === 0">视频审核不通过，无法上传资源</p>
            <p v-else>暂无资源</p>
          </div>
          
          <div class="video-actions-footer">
            <el-button type="danger" @click="handleDeleteVideo(previewVideo)">
              <el-icon><el-icon-delete /></el-icon>
              删除视频
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
  
  <!-- 上传资源对话框 -->
  <el-dialog
    v-model="showUploadResourceDialog"
    title="上传视频资源"
    width="480px"
    :close-on-click-modal="false"
    class="upload-resource-dialog"
    append-to-body
  >
    <div class="resource-upload-content">
      <div class="form-row">
        <label class="form-label">资源名称 <span class="required">*</span></label>
        <el-input v-model="resourceForm.name" placeholder="请输入资源名称" />
      </div>
      
      <div class="form-row">
        <label class="form-label">资源类型 <span class="required">*</span></label>
        <el-select v-model="resourceForm.type" placeholder="请选择资源类型" style="width: 100%">
          <el-option label="文档 (DOCUMENT)" value="DOCUMENT" />
          <el-option label="代码 (CODE)" value="CODE" />
          <el-option label="媒体 (MEDIA)" value="MEDIA" />
          <el-option label="其他 (OTHER)" value="OTHER" />
        </el-select>
      </div>
      
      <div class="form-row">
        <label class="form-label">资源描述</label>
        <el-input v-model="resourceForm.description" type="textarea" :rows="2" placeholder="请输入资源描述（可选）" />
      </div>
      
      <div class="form-row">
        <label class="form-label">选择文件 <span class="required">*</span></label>
        <el-upload
          class="resource-uploader"
          drag
          :auto-upload="false"
          :on-change="handleResourceFileChange"
          :show-file-list="false"
          accept=".pdf,.zip,.mp4,.abr,.jpg,.jpeg,.docx,.xlsx,.txt"
        >
          <div v-if="!resourceForm.file" class="upload-placeholder">
            <div class="upload-icon-large">
              <el-icon><el-icon-upload-filled /></el-icon>
            </div>
            <p class="upload-text">拖拽文件到此处或点击上传</p>
            <p class="upload-hint">支持格式：pdf, zip, mp4, abr, jpg, docx, xlsx, txt</p>
            <p class="upload-hint">最大文件大小：200MB</p>
          </div>
          <div v-else class="file-selected">
            <el-icon class="file-icon"><el-icon-document /></el-icon>
            <div class="file-info">
              <span class="file-name">{{ resourceForm.file.name }}</span>
              <span class="file-size">{{ formatFileSize(resourceForm.file.size) }}</span>
            </div>
            <el-button type="danger" size="small" @click.stop="removeResourceFile" class="remove-file-btn">
              <el-icon><el-icon-delete /></el-icon>
            </el-button>
          </div>
        </el-upload>
      </div>
      
      <div v-if="uploadProgress > 0" class="progress-row">
        <el-progress :percentage="uploadProgress" :status="uploadStatus" />
      </div>
    </div>
    
    <template #footer>
      <el-button @click="showUploadResourceDialog = false">取消</el-button>
      <el-button type="primary" @click="submitResource" :loading="uploadingResource" :disabled="!resourceForm.file || !resourceForm.name || !resourceForm.type">
        确认上传
      </el-button>
    </template>
  </el-dialog>
  
  <el-dialog
    v-model="editDialogVisible"
    title="编辑个人信息"
    width="600px"
    :close-on-click-modal="false"
  >
    <el-form :model="editForm" :rules="editFormRules" ref="editFormRef" label-width="100px" class="edit-form">
      <el-form-item label="头像">
        <div class="avatar-upload">
          <img :src="editForm.avatar || 'https://picsum.photos/100/100?random=avatar'" class="avatar-preview" />
          <div class="upload-buttons">
            <el-button type="primary" size="small" @click="handleAvatarUpload" :loading="uploadingAvatar">
              <el-icon><el-icon-upload /></el-icon>
              选择头像
            </el-button>
            <el-button 
              v-if="editForm.avatar && editForm.avatar !== originalAvatar" 
              type="success" 
              size="small" 
              @click="confirmAvatarChange"
              :loading="savingAvatar"
            >
              确定修改
            </el-button>
          </div>
          <input type="file" ref="avatarInput" @change="handleAvatarChange" accept="image/*" style="display: none;" />
        </div>
      </el-form-item>
      
      <el-form-item label="背景图">
        <div class="background-upload">
          <img v-if="editForm.backImage" :src="editForm.backImage" class="background-preview" />
          <div v-else class="background-placeholder">暂无背景图</div>
          <div class="upload-tip">支持格式：JPG、PNG、GIF、WEBP、MP4，最大10MB</div>
          <div class="upload-buttons">
            <el-button type="primary" size="small" @click="handleBackgroundUpload" :loading="uploadingBackground">
              <el-icon><el-icon-upload /></el-icon>
              选择背景图
            </el-button>
            <el-button 
              v-if="editForm.backImage && editForm.backImage !== originalBackImage" 
              type="success" 
              size="small" 
              @click="confirmBackgroundChange"
              :loading="savingBackground"
            >
              确定修改
            </el-button>
          </div>
          <input type="file" ref="backgroundInput" @change="handleBackgroundChange" accept="image/jpeg,image/png,image/gif,image/webp,video/mp4" style="display: none;" />
        </div>
      </el-form-item>
      
      <el-form-item label="昵称" prop="nickName">
        <el-input v-model="editForm.nickName" placeholder="请输入昵称" maxlength="20" show-word-limit />
      </el-form-item>
      
      <el-form-item label="真实姓名" prop="realName">
        <el-input v-model="editForm.realName" placeholder="请输入真实姓名" maxlength="20" />
      </el-form-item>
      
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="editForm.phone" placeholder="请输入手机号" maxlength="11" />
      </el-form-item>
      
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="editForm.email" placeholder="请输入邮箱" maxlength="50" />
      </el-form-item>
      
      <el-form-item label="性别">
        <el-radio-group v-model="editForm.gender">
          <el-radio :label="0">未知</el-radio>
          <el-radio :label="1">男</el-radio>
          <el-radio :label="2">女</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item label="生日">
        <el-input v-model="editForm.birthday" placeholder="请输入生日（如：1990-01-01）" maxlength="10" />
      </el-form-item>
      
      <el-form-item label="年龄">
        <el-input-number v-model="editForm.age" :min="0" :max="150" />
      </el-form-item>
      
      <el-form-item label="学校" prop="school">
        <el-input v-model="editForm.school" placeholder="请输入学校名称" maxlength="50" />
      </el-form-item>
      
      <el-form-item label="学历" prop="leavel">
        <el-input v-model="editForm.leavel" placeholder="请输入学历水平" maxlength="20" />
      </el-form-item>
      
      <el-form-item label="地址" prop="address">
        <el-input v-model="editForm.address" placeholder="请输入地址" maxlength="100" />
      </el-form-item>
      
      <el-form-item label="国家" prop="country">
        <el-input v-model="editForm.country" placeholder="请输入国家" maxlength="30" />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <div style="display: flex; justify-content: space-between; width: 100%;">
        <el-button type="warning" plain @click="editDialogVisible = false; passwordDialogVisible = true">
          <el-icon><el-icon-lock /></el-icon>
          修改密码
        </el-button>
        <div>
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveProfile" :loading="saving">保存</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
  
  <el-dialog
    v-model="passwordDialogVisible"
    title="修改密码"
    width="450px"
    :close-on-click-modal="false"
  >
    <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
      <el-form-item label="当前密码" prop="oldPassword">
        <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" show-password />
      </el-form-item>
      
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
      </el-form-item>
      
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="passwordDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">确认修改</el-button>
    </template>
  </el-dialog>
  
  <el-dialog
    v-model="viewProfileDialogVisible"
    title="个人资料详情"
    width="600px"
  >
    <div class="profile-detail">
      <div class="detail-header">
        <img :src="userInfoStore.info.avatar || 'https://picsum.photos/200/200?random=avatar'" class="detail-avatar" />
        <div class="detail-info">
          <h2>{{ userInfoStore.info.nickName || '用户' }}</h2>
          <p v-if="userInfoStore.info.signature || userInfoStore.info.introduction">
            {{ userInfoStore.info.signature || userInfoStore.info.introduction }}
          </p>
        </div>
      </div>
      
      <el-divider />
      
      <div class="detail-section">
        <h3>基本信息</h3>
        <div class="detail-grid">
          <div class="detail-item" v-if="userInfoStore.info.realName">
            <span class="detail-label">真实姓名</span>
            <span class="detail-value">{{ userInfoStore.info.realName }}</span>
          </div>
          <div class="detail-item" v-if="userInfoStore.info.gender">
            <span class="detail-label">性别</span>
            <span class="detail-value">{{ userInfoStore.info.gender === 1 ? '男' : userInfoStore.info.gender === 2 ? '女' : '未知' }}</span>
          </div>
          <div class="detail-item" v-if="userInfoStore.info.age">
            <span class="detail-label">年龄</span>
            <span class="detail-value">{{ userInfoStore.info.age }}岁</span>
          </div>
          <div class="detail-item" v-if="userInfoStore.info.birthday">
            <span class="detail-label">生日</span>
            <span class="detail-value">{{ userInfoStore.info.birthday }}</span>
          </div>
          <div class="detail-item" v-if="userInfoStore.info.phone">
            <span class="detail-label">手机号</span>
            <span class="detail-value">{{ userInfoStore.info.phone }}</span>
          </div>
          <div class="detail-item" v-if="userInfoStore.info.email">
            <span class="detail-label">邮箱</span>
            <span class="detail-value">{{ userInfoStore.info.email }}</span>
          </div>
        </div>
      </div>
      
      <div class="detail-section">
        <h3>教育信息</h3>
        <div class="detail-grid">
          <div class="detail-item" v-if="userInfoStore.info.school">
            <span class="detail-label">学校</span>
            <span class="detail-value">{{ userInfoStore.info.school }}</span>
          </div>
          <div class="detail-item" v-if="userInfoStore.info.leavel">
            <span class="detail-label">学历</span>
            <span class="detail-value">{{ userInfoStore.info.leavel }}</span>
          </div>
        </div>
      </div>
      
      <div class="detail-section">
        <h3>其他信息</h3>
        <div class="detail-grid">
          <div class="detail-item" v-if="userInfoStore.info.address">
            <span class="detail-label">地址</span>
            <span class="detail-value">{{ userInfoStore.info.address }}</span>
          </div>
          <div class="detail-item" v-if="userInfoStore.info.country">
            <span class="detail-label">国家</span>
            <span class="detail-value">{{ userInfoStore.info.country }}</span>
          </div>
          <div class="detail-item" v-if="userInfoStore.info.language">
            <span class="detail-label">使用语言</span>
            <span class="detail-value">{{ userInfoStore.info.language }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <template #footer>
      <el-button type="primary" @click="viewProfileDialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserInfoStore } from '@/stores/userinfo'
import { GetUserInfoMessage, CancelFollow, UpdateUserInfo, GetUserDate, UpdatePassword, UploadVideoResource, GetVideoResources, DeleteVideo, DeleteVideoResource } from '@/api/Users'
import { uploadToOSS } from '@/utils/oss'

const { t, locale } = useI18n()
const router = useRouter()
const route = useRoute()
const userInfoStore = useUserInfoStore()

const currentNav = ref('home')
const activeTab = ref('home')
const uploadFilterStatus = ref(2)
const previewDialogVisible = ref(false)
const previewVideo = ref(null)
const searchKeyword = ref('')
const sortType = ref('newest')
const loading = ref(false)
const viewProfileDialogVisible = ref(false)

const showUploadResourceDialog = ref(false)
const videoResources = ref([])
const uploadingResource = ref(false)
const uploadProgress = ref(0)
const uploadStatus = ref('')
const resourceForm = reactive({
  name: '',
  type: '',
  description: '',
  file: null
})

const headerBgImage = ref('https://lizhanghao.oss-cn-beijing.aliyuncs.com/%E8%8F%9C%E5%8D%95%E6%A0%8F/487-1800x70.jpg')
const defaultBannerImage = 'https://picsum.photos/1200/300?random=banner'

const backgroundImage = computed(() => {
  const backImage = userInfoStore.info.backImage
  if (backImage) {
    return backImage.trim().replace(/[\r\n`]/g, '').trim()
  }
  return defaultBannerImage
})

const isVideoBackground = computed(() => {
  const backImage = userInfoStore.info.backImage
  if (!backImage) return false
  const trimmedUrl = backImage.trim().replace(/[\r\n`]/g, '').trim().toLowerCase()
  return trimmedUrl.endsWith('.mp4') || trimmedUrl.endsWith('.webm') || trimmedUrl.endsWith('.mov')
})

const userInfo = ref({
  id: '1',
  nickname: 'XSJ用户',
  avatar: 'https://picsum.photos/200/200?random=avatar',
  signature: '热爱学习，热爱生活~',
  school: '清华大学',
  location: '北京',
  joinDate: '2024-01-01',
  level: 6,
  isMember: true,
  verified: true,
  following: 128,
  followers: 1024,
  videoCount: 36,
  likeCount: 5280,
  viewCount: 125000,
  collectCount: 256,
  uploadCount: 12
})

const userFollowList = ref([])
const userVideoPost = ref([])
const userVideoPending = ref([])
const userVideoCollect = ref([])
const userVideoLike = ref([])

const fetchUserInfoMessage = async () => {
  loading.value = true
  try {
    const result = await GetUserInfoMessage()
    if (result && result.code === 0 && result.data) {
      userFollowList.value = result.data.userFollowDTOList || []
      userVideoPost.value = result.data.userVideoPost || []
      userVideoPending.value = result.data.userVideoPending || []
      userVideoCollect.value = result.data.userVideoCollect || []
      userVideoLike.value = result.data.userVideoLike || []
      
      const totalViews = userVideoPost.value.reduce((sum, video) => sum + (video.viewCount || 0), 0)
      
      userInfo.value.videoCount = userVideoPost.value.length
      userInfo.value.collectCount = userVideoCollect.value.length
      userInfo.value.likeCount = userVideoLike.value.length
      userInfo.value.viewCount = totalViews
      userInfo.value.following = userFollowList.value.length
      userInfo.value.uploadCount = userVideoPending.value.length
    }
  } catch (err) {
    console.error('获取用户信息失败:', err)
  } finally {
    loading.value = false
  }
}

const refreshUserData = async () => {
  try {
    const result = await GetUserDate()
    if (result && result.code === 0 && result.data && result.data.length > 0) {
      const userData = result.data[0]
      userInfoStore.setInfo(userData)
    }
  } catch (err) {
    console.error('刷新用户基本信息失败:', err)
  }
}

const formatVideoTime = (seconds) => {
  if (!seconds) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const formatVideoUrl = (url) => {
  if (!url) return ''
  let cleanedUrl = url.trim().replace(/\r?\n/g, '').replace(/`/g, '')
  try {
    cleanedUrl = decodeURIComponent(cleanedUrl)
  } catch (e) {
    console.warn('URL解码失败:', e)
  }
  return cleanedUrl
}

const userVideos = computed(() => {
  return userVideoPost.value.map(video => ({
    encode: video.enCode,
    title: video.videoName,
    thumbnail: formatVideoUrl(video.videoImageUrl),
    videoUrl: formatVideoUrl(video.videoUrl),
    duration: formatVideoTime(video.videoTime),
    author: video.publisherName || '发布人',
    views: video.viewCount || 0,
    comments: video.commentCount || 0,
    likeCount: video.likeCount || 0,
    collectCount: video.collectCount || 0,
    shareCount: video.shareCount || 0,
    videoResolution: video.videoResolution,
    isMember: video.isMember,
    isLiked: video.isLiked || 0,
    isCollected: video.isCollected || 0,
    isShared: video.isShared || 0,
    introduction: video.introduction
  }))
})

const uploadedVideos = computed(() => {
  const allVideos = userVideoPending.value.map(video => ({
    encode: video.enCode,
    title: video.videoName,
    thumbnail: formatVideoUrl(video.videoImageUrl),
    videoUrl: formatVideoUrl(video.videoUrl),
    duration: formatVideoTime(video.videoTime),
    author: video.publisherName || '发布人',
    views: video.viewCount || 0,
    likeCount: video.likeCount || 0,
    videoStatus: video.videoStatus,
    status: video.videoStatus === 1 ? 'published' : (video.videoStatus === 2 ? 'reviewing' : 'rejected')
  }))
  
  if (uploadFilterStatus.value === -1) {
    return allVideos
  }
  return allVideos.filter(video => video.videoStatus === uploadFilterStatus.value)
})

const pendingVideoCount = computed(() => {
  return userVideoPending.value.filter(video => video.videoStatus === 2).length
})

const publishedVideoCount = computed(() => {
  return userVideoPending.value.filter(video => video.videoStatus === 1).length
})

const rejectedVideoCount = computed(() => {
  return userVideoPending.value.filter(video => video.videoStatus === 0).length
})

const followingList = computed(() => {
  return userFollowList.value.map(user => ({
    id: user.enCode,
    nickname: user.nickName,
    avatar: formatVideoUrl(user.avatar),
    signature: user.school || '',
    followers: user.fansMount,
    videoCount: user.videoMount,
    online: false,
    isFollowing: true
  }))
})

const collectionFolders = ref([
  { id: 1, name: '默认收藏夹', count: 0, thumbnails: [] }
])

const likedVideos = computed(() => {
  return userVideoLike.value.map(video => ({
    encode: video.enCode,
    title: video.videoName,
    thumbnail: formatVideoUrl(video.videoImageUrl),
    videoUrl: formatVideoUrl(video.videoUrl),
    duration: formatVideoTime(video.videoTime),
    author: video.publisherName || '发布人',
    authorAvatar: formatVideoUrl(video.publisherAvatar),
    views: video.viewCount || 0,
    comments: video.commentCount || 0,
    likeCount: video.likeCount || 0,
    collectCount: video.collectCount || 0,
    shareCount: video.shareCount || 0,
    videoResolution: video.videoResolution,
    isMember: video.isMember,
    isLiked: video.isLiked || 0,
    isCollected: video.isCollected || 0,
    isShared: video.isShared || 0
  }))
})

const collectedVideos = computed(() => {
  return userVideoCollect.value.map(video => ({
    encode: video.enCode,
    title: video.videoName,
    thumbnail: formatVideoUrl(video.videoImageUrl),
    videoUrl: formatVideoUrl(video.videoUrl),
    duration: formatVideoTime(video.videoTime),
    author: video.publisherName || '发布人',
    authorAvatar: formatVideoUrl(video.publisherAvatar),
    views: video.viewCount || 0,
    comments: video.commentCount || 0,
    likeCount: video.likeCount || 0,
    collectCount: video.collectCount || 0,
    shareCount: video.shareCount || 0,
    videoResolution: video.videoResolution,
    isMember: video.isMember,
    isLiked: video.isLiked || 0,
    isCollected: video.isCollected || 0,
    isShared: video.isShared || 0,
    introduction: video.introduction
  }))
})

const currentLangText = computed(() => {
  const langMap = { zh: '中文', en: 'English', ja: '日本語' }
  return langMap[locale.value] || '中文'
})

const filteredVideos = computed(() => {
  let videos = userVideos.value || []
  if (searchKeyword.value) {
    videos = videos.filter(v => v.title.toLowerCase().includes(searchKeyword.value.toLowerCase()))
  }
  if (sortType.value === 'popular') {
    videos = [...videos].sort((a, b) => b.likeCount - a.likeCount)
  } else if (sortType.value === 'views') {
    videos = [...videos].sort((a, b) => b.views - a.views)
  }
  return videos
})

const filteredCollectedVideos = computed(() => {
  let videos = collectedVideos.value || []
  if (searchKeyword.value) {
    videos = videos.filter(v => v.title.toLowerCase().includes(searchKeyword.value.toLowerCase()))
  }
  if (sortType.value === 'popular') {
    videos = [...videos].sort((a, b) => b.likeCount - a.likeCount)
  } else if (sortType.value === 'views') {
    videos = [...videos].sort((a, b) => b.views - a.views)
  }
  return videos
})

const filteredLikedVideos = computed(() => {
  let videos = likedVideos.value || []
  if (searchKeyword.value) {
    videos = videos.filter(v => v.title.toLowerCase().includes(searchKeyword.value.toLowerCase()))
  }
  if (sortType.value === 'popular') {
    videos = [...videos].sort((a, b) => b.likeCount - a.likeCount)
  } else if (sortType.value === 'views') {
    videos = [...videos].sort((a, b) => b.views - a.views)
  }
  return videos
})

const formatNumber = (num) => {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}

const getStatusBadgeClass = (videoStatus) => {
  const classMap = {
    0: 'status-rejected',
    1: 'status-published',
    2: 'status-reviewing'
  }
  return classMap[videoStatus] || 'status-reviewing'
}

const getStatusBadgeText = (videoStatus) => {
  const textMap = {
    0: '✗ 审核失败',
    1: '✓ 审核通过',
    2: '📝 等待审核'
  }
  return textMap[videoStatus] || '📝 等待审核'
}

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

const goHome = () => router.push('/First')
const goToFirst = () => router.push('/First')
const goToAI = () => router.push('/ai/home')

const handleUpload = () => router.push('/upload')
const handleMessage = () => router.push('/chat')
const handleHistory = () => router.push('/history')

const editDialogVisible = ref(false)
const saving = ref(false)
const passwordDialogVisible = ref(false)
const changingPassword = ref(false)
const passwordFormRef = ref(null)
const editFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePhone = (rule, value, callback) => {
  if (value && !/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  if (value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error('请输入正确的邮箱格式'))
  } else {
    callback()
  }
}

const editFormRules = {
  nickName: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在2到20个字符', trigger: 'blur' }
  ],
  realName: [
    { max: 20, message: '真实姓名不能超过20个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ],
  email: [
    { validator: validateEmail, trigger: 'blur' }
  ],
  school: [
    { max: 50, message: '学校名称不能超过50个字符', trigger: 'blur' }
  ],
  leavel: [
    { max: 20, message: '学历不能超过20个字符', trigger: 'blur' }
  ],
  address: [
    { max: 100, message: '地址不能超过100个字符', trigger: 'blur' }
  ],
  country: [
    { max: 30, message: '国家不能超过30个字符', trigger: 'blur' }
  ]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}
const uploadingAvatar = ref(false)
const uploadingBackground = ref(false)
const savingAvatar = ref(false)
const savingBackground = ref(false)
const originalAvatar = ref('')
const originalBackImage = ref('')
const avatarInput = ref(null)
const backgroundInput = ref(null)
const editForm = ref({
  realName: '',
  nickName: '',
  phone: '',
  email: '',
  avatar: '',
  gender: 0,
  birthday: '',
  school: '',
  leavel: '',
  address: '',
  country: '',
  age: null,
  backImage: ''
})

const handleEditProfile = () => {
  const avatarUrl = userInfoStore.info.avatar ? userInfoStore.info.avatar.replace(/[\r\n`]/g, '').trim() : ''
  const backImageUrl = userInfoStore.info.backImage ? userInfoStore.info.backImage.replace(/[\r\n`]/g, '').trim() : ''
  
  originalAvatar.value = avatarUrl
  originalBackImage.value = backImageUrl
  
  editForm.value = {
    realName: userInfoStore.info.realName || '',
    nickName: userInfoStore.info.nickName || '',
    phone: userInfoStore.info.phone || '',
    email: userInfoStore.info.email || '',
    avatar: avatarUrl,
    gender: userInfoStore.info.gender || 0,
    birthday: userInfoStore.info.birthday || '',
    school: userInfoStore.info.school || '',
    leavel: userInfoStore.info.leavel || '',
    address: userInfoStore.info.address || '',
    country: userInfoStore.info.country || '',
    age: userInfoStore.info.age || null,
    backImage: backImageUrl
  }
  editDialogVisible.value = true
}

const handleViewProfile = () => {
  viewProfileDialogVisible.value = true
}

const handleAvatarUpload = () => {
  avatarInput.value?.click()
}

const handleBackgroundUpload = () => {
  backgroundInput.value?.click()
}

const handleAvatarChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('请选择图片文件')
    return
  }
  
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB')
    return
  }
  
  uploadingAvatar.value = true
  try {
    const result = await uploadToOSS(file, 'image')
    if (result.success) {
      editForm.value.avatar = result.url
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(result.error || '头像上传失败')
    }
  } catch (error) {
    console.error('上传头像失败:', error)
    ElMessage.error('头像上传失败')
  } finally {
    uploadingAvatar.value = false
  }
  event.target.value = ''
}

const handleBackgroundChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  const isImage = file.type.startsWith('image/')
  const isVideo = file.type.startsWith('video/')
  if (!isImage && !isVideo) {
    ElMessage.error('请选择图片或视频文件')
    return
  }
  
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB')
    return
  }
  
  uploadingBackground.value = true
  try {
    const fileType = file.type.startsWith('video/') ? 'video' : 'image'
    const result = await uploadToOSS(file, fileType)
    if (result.success) {
      editForm.value.backImage = result.url
      ElMessage.success('背景图上传成功')
    } else {
      ElMessage.error(result.error || '背景图上传失败')
    }
  } catch (error) {
    console.error('上传背景图失败:', error)
    ElMessage.error('背景图上传失败')
  } finally {
    uploadingBackground.value = false
  }
  event.target.value = ''
}

const confirmAvatarChange = async () => {
  if (!editForm.value.avatar) {
    ElMessage.warning('请先选择头像')
    return
  }
  
  savingAvatar.value = true
  try {
    const result = await UpdateUserInfo({ avatar: editForm.value.avatar })
    if (result.code === 0) {
      originalAvatar.value = editForm.value.avatar
      userInfoStore.setInfo({
        ...userInfoStore.info,
        avatar: editForm.value.avatar
      })
      ElMessage.success(result.message || '头像修改成功')
    } else {
      ElMessage.error(result.data || result.message || '头像修改失败')
    }
  } catch (error) {
    console.error('修改头像失败:', error)
    if (typeof error === 'string') {
      ElMessage.error(error)
    } else {
      ElMessage.error(error.response?.data?.data || error.response?.data?.message || error.message || '头像修改失败')
    }
  } finally {
    savingAvatar.value = false
  }
}

const confirmBackgroundChange = async () => {
  if (!editForm.value.backImage) {
    ElMessage.warning('请先选择背景图')
    return
  }
  
  savingBackground.value = true
  try {
    const result = await UpdateUserInfo({ backImage: editForm.value.backImage })
    if (result.code === 0) {
      originalBackImage.value = editForm.value.backImage
      userInfoStore.setInfo({
        ...userInfoStore.info,
        backImage: editForm.value.backImage
      })
      ElMessage.success(result.message || '背景图修改成功')
    } else {
      ElMessage.error(result.data || result.message || '背景图修改失败')
    }
  } catch (error) {
    console.error('修改背景图失败:', error)
    if (typeof error === 'string') {
      ElMessage.error(error)
    } else {
      ElMessage.error(error.response?.data?.data || error.response?.data?.message || error.message || '背景图修改失败')
    }
  } finally {
    savingBackground.value = false
  }
}

const handleSaveProfile = async () => {
  if (!editFormRef.value) return
  
  try {
    await editFormRef.value.validate()
  } catch (error) {
    return
  }
  
  saving.value = true
  try {
    const changedData = {}
    const fields = ['realName', 'nickName', 'phone', 'email', 'gender', 'birthday', 'school', 'leavel', 'address', 'country', 'age']
    
    fields.forEach(field => {
      if (editForm.value[field] !== userInfoStore.info[field]) {
        changedData[field] = editForm.value[field]
      }
    })
    
    if (Object.keys(changedData).length === 0) {
      ElMessage.warning('没有修改任何信息')
      saving.value = false
      return
    }
    
    const result = await UpdateUserInfo(changedData)
    
    if (result.code === 0) {
      userInfoStore.setInfo({
        ...userInfoStore.info,
        ...changedData
      })
      
      editDialogVisible.value = false
      ElMessage.success(result.message || '保存成功')
    } else {
      ElMessage.error(result.message || '保存失败')
    }
  } catch (error) {
    console.error('保存用户信息失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    changingPassword.value = true
    
    const res = await UpdatePassword(passwordForm.oldPassword, passwordForm.newPassword)
    
    if (res.code === 0) {
      ElMessage.success('密码修改成功')
      passwordDialogVisible.value = false
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    } else {
      ElMessage.error(res.data || res.message || '密码修改失败')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error(typeof error === 'string' ? error : (error.response?.data?.data || error.response?.data?.message || '密码修改失败'))
  } finally {
    changingPassword.value = false
  }
}

const handleStatClick = (type) => ElMessage.info(`${t('center.viewDetail')}: ${type}`)

const playVideo = (video) => {
  console.log('点击视频:', video)
  console.log('视频encode:', video.encode)
  console.log('视频状态:', video.reviewStatus)
  console.log('当前标签:', activeTab.value)
  
  if (video.encode) {
    if (activeTab.value === 'upload' || activeTab.value === 'home') {
      previewVideo.value = video
      previewDialogVisible.value = true
      fetchVideoResources()
    } else {
      const videoData = {
        encode: video.encode,
        title: video.title,
        thumbnail: video.thumbnail,
        videoUrl: video.videoUrl,
        duration: video.duration,
        author: video.author,
        views: video.views,
        comments: 0,
        likeCount: video.likeCount,
        collectCount: 0,
        shareCount: 0,
        description: '',
        introduction: '',
        publishTime: '',
        tags: [],
        videoResolution: '',
        isMember: false
      }
      sessionStorage.setItem('currentVideo', JSON.stringify(videoData))
      router.push({
        path: '/Video',
        query: { encode: video.encode }
      })
    }
  } else {
    console.warn('视频缺少encode，无法跳转:', video)
    ElMessage.warning('视频信息不完整，无法播放')
  }
}

const editVideo = (video) => ElMessage.info(`${t('center.editVideo')}: ${video.title}`)

const deleteVideo = async (video) => {
  try {
    await ElMessageBox.confirm(`确定要删除视频"${video.title}"吗？`, '删除视频', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const result = await DeleteVideo(video.encode)
    if (result.code === 0) {
      ElMessage.success('视频删除成功')
      await fetchUserInfoMessage()
    } else {
      ElMessage.error(result.data || result.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除视频失败:', error)
      if (typeof error === 'string') {
        ElMessage.error(error)
      } else {
        ElMessage.error('删除失败')
      }
    }
  }
}

const handleDeleteVideo = async (video) => {
  if (!video) return
  try {
    await ElMessageBox.confirm(`确定要删除视频"${video.title}"吗？`, '删除视频', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const result = await DeleteVideo(video.encode)
    if (result.code === 0) {
      ElMessage.success('视频删除成功')
      previewDialogVisible.value = false
      await fetchUserInfoMessage()
    } else {
      ElMessage.error(result.data || result.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除视频失败:', error)
      if (typeof error === 'string') {
        ElMessage.error(error)
      } else {
        ElMessage.error('删除失败')
      }
    }
  }
}

const formatFileSize = (bytes) => {
  if (!bytes || bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const getResourceIcon = (type) => {
  const iconMap = {
    DOCUMENT: 'el-icon-document',
    CODE: 'el-icon-document-copy',
    MEDIA: 'el-icon-video-play',
    OTHER: 'el-icon-files'
  }
  return iconMap[type] || 'el-icon-files'
}

const handleResourceFileChange = (file) => {
  if (!file || !file.raw) return
  
  const maxSize = 200 * 1024 * 1024
  if (file.raw.size > maxSize) {
    ElMessage.error('文件大小不能超过200MB')
    return
  }
  
  resourceForm.file = file.raw
  if (!resourceForm.name) {
    resourceForm.name = file.name.substring(0, file.name.lastIndexOf('.')) || file.name
  }
}

const removeResourceFile = () => {
  resourceForm.file = null
}

const submitResource = async () => {
  if (!resourceForm.file || !resourceForm.name || !resourceForm.type) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  if (!previewVideo.value || !previewVideo.value.encode) {
    ElMessage.error('视频信息不完整')
    return
  }
  
  uploadingResource.value = true
  uploadProgress.value = 0
  uploadStatus.value = ''
  
  try {
    const result = await uploadToOSS(resourceForm.file, 'resource', (progress) => {
      uploadProgress.value = progress
    })
    
    if (!result.success) {
      throw new Error(result.error || '上传失败')
    }
    
    const fileExt = resourceForm.file.name.substring(resourceForm.file.name.lastIndexOf('.') + 1).toLowerCase()
    
    const uploadResult = await UploadVideoResource(
      result.url,
      previewVideo.value.encode,
      resourceForm.name,
      resourceForm.type,
      fileExt,
      resourceForm.file.size,
      resourceForm.description
    )
    
    if (uploadResult.code === 0) {
      ElMessage.success('资源上传成功')
      showUploadResourceDialog.value = false
      resourceForm.name = ''
      resourceForm.type = ''
      resourceForm.description = ''
      resourceForm.file = null
      uploadProgress.value = 0
      uploadStatus.value = 'success'
      await fetchVideoResources()
    } else {
      throw new Error(uploadResult.message || '上传失败')
    }
  } catch (error) {
    console.error('上传资源失败:', error)
    ElMessage.error(error.message || '上传失败')
    uploadStatus.value = 'exception'
  } finally {
    uploadingResource.value = false
  }
}

const fetchVideoResources = async () => {
  if (!previewVideo.value || !previewVideo.value.encode) return
  if (previewVideo.value.videoStatus !== 1) {
    videoResources.value = []
    return
  }
  
  try {
    const result = await GetVideoResources(previewVideo.value.encode)
    if (result.code === 0 && result.data) {
      videoResources.value = result.data.map(item => ({
        id: item.videoResourcesId,
        name: item.resourceName,
        format: item.resourceFormat,
        fileurl: item.resourceUrl ? item.resourceUrl.replace(/[\r\n`]/g, '').trim() : '',
        size: item.resourceSize,
        description: item.resourceDesc,
        downloadCount: item.downloadCount,
        type: getTypeFromFormat(item.resourceFormat)
      }))
    }
  } catch (error) {
    console.error('获取资源列表失败:', error)
  }
}

const getTypeFromFormat = (format) => {
  if (!format) return 'OTHER'
  const formatLower = format.toLowerCase()
  const docFormats = ['pdf', 'doc', 'docx', 'txt', 'xlsx', 'xls', 'ppt', 'pptx']
  const codeFormats = ['js', 'ts', 'py', 'java', 'cpp', 'c', 'html', 'css', 'json', 'xml']
  const mediaFormats = ['mp4', 'mp3', 'avi', 'mov', 'wav', 'flv', 'mkv', 'abr', 'jpg', 'jpeg', 'png', 'gif']
  
  if (docFormats.includes(formatLower)) return 'DOCUMENT'
  if (codeFormats.includes(formatLower)) return 'CODE'
  if (mediaFormats.includes(formatLower)) return 'MEDIA'
  return 'OTHER'
}

const downloadResource = (resource) => {
  if (resource.fileurl) {
    window.open(resource.fileurl, '_blank')
  }
}

const handleDeleteResource = async (resource) => {
  try {
    await ElMessageBox.confirm('确定要删除这个资源吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const result = await DeleteVideoResource(resource.id)
    if (result.code === 0) {
      ElMessage.success('删除成功')
      await fetchVideoResources()
    } else {
      ElMessage.error(result.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除资源失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const toggleFollow = async (user) => {
  try {
    const status = user.isFollowing ? 0 : 1
    const result = await CancelFollow(user.id, status)
    if (result.code === 0) {
      ElMessage.success(user.isFollowing ? t('center.unfollowSuccess') : t('center.followSuccess'))
      await fetchUserInfoMessage()
    } else {
      ElMessage.error(result.message || '操作失败')
    }
  } catch (error) {
    console.error('关注/取消关注失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

const editFolder = (folder) => ElMessage.info(`${t('center.editFolder')}: ${folder.name}`)
const deleteFolder = (folder) => ElMessage.info(`${t('center.deleteFolder')}: ${folder.name}`)

onMounted(() => {
  refreshUserData()
  fetchUserInfoMessage()
  if (route.query.tab) {
    activeTab.value = route.query.tab
  }
})
</script>

<style scoped>
.center-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #e8f4f8 0%, #d4e5ed 50%, #c9dce8 100%);
  position: relative;
}

.center-page::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 10% 20%, rgba(102, 126, 234, 0.15) 0%, transparent 40%),
    radial-gradient(circle at 90% 80%, rgba(118, 75, 162, 0.15) 0%, transparent 40%),
    radial-gradient(circle at 50% 50%, rgba(240, 147, 251, 0.1) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

.center-page::after {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23667eea' fill-opacity='0.03'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  pointer-events: none;
  z-index: 0;
}

.center-header {
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
  max-width: 1920px;
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
  flex: 1;
  justify-content: center;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  font-size: 15px;
  color: white;
  cursor: pointer;
  border-radius: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  text-decoration: none;
  position: relative;
  overflow: hidden;
  font-weight: 500;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5), 0 2px 4px rgba(0, 0, 0, 0.3);
  animation: textGlow 2s ease-in-out infinite alternate;
}

@keyframes textGlow {
  0% { text-shadow: 0 0 10px rgba(255, 255, 255, 0.5), 0 2px 4px rgba(0, 0, 0, 0.3); }
  100% { text-shadow: 0 0 20px rgba(255, 255, 255, 0.8), 0 0 30px rgba(255, 200, 100, 0.5), 0 2px 4px rgba(0, 0, 0, 0.3); }
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

.nav-item:hover::before {
  opacity: 1;
}

.nav-item:hover {
  color: white;
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
}

.nav-item.active::before {
  opacity: 1;
}

.nav-icon {
  font-size: 18px;
  position: relative;
  z-index: 1;
}

.nav-text {
  position: relative;
  z-index: 1;
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
}

.action-text {
  position: relative;
  z-index: 1;
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
  transition: all 0.3s ease;
  font-size: 14px;
  color: #333;
}

.lang-btn:hover {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  transform: translateY(-2px);
}

.lang-text {
  font-weight: 500;
}

.dropdown-arrow {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.lang-btn:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.user-avatar-wrapper {
  cursor: pointer;
  transition: all 0.3s ease;
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

.user-avatar-wrapper.active {
  background: linear-gradient(135deg, #ff6b6b, #feca57);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid white;
}

.center-container {
  padding-top: 70px;
  min-height: calc(100vh - 70px);
}

.center-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px;
}

.user-profile-section {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.profile-banner {
  min-height: 400px;
  position: relative;
  overflow: hidden;
}

.banner-media {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.banner-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-gif {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}

.banner-image {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
}

.banner-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.2) 0%, rgba(0, 0, 0, 0.5) 100%);
}

.profile-content {
  position: relative;
  z-index: 1;
  padding: 40px;
  min-height: 400px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.profile-main {
  display: flex;
  gap: 24px;
  align-items: flex-start;
  padding: 20px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.avatar-wrapper {
  position: relative;
}

.user-avatar-large {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.avatar-badge {
  position: absolute;
  bottom: 5px;
  right: 5px;
  background: linear-gradient(135deg, #ffd700, #ffaa00);
  color: #333;
  font-size: 10px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 10px;
  border: 2px solid white;
}

.edit-profile-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.9);
  color: #333;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.edit-profile-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  background: white;
}

.view-profile-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.7);
  color: #333;
  border: none;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.view-profile-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  background: white;
}

.info-section {
  flex: 1;
  min-width: 0;
}

.info-card {
  background: transparent;
  padding: 20px;
}

.user-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.user-nickname {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.user-badges {
  display: flex;
  gap: 8px;
}

.badge {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  backdrop-filter: blur(10px);
}

.badge-icon {
  font-size: 14px;
}

.member-badge {
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.9), rgba(255, 170, 0, 0.9));
  color: #333;
}

.user-signature {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0 0 12px 0;
  text-shadow: 0 1px 5px rgba(0, 0, 0, 0.5);
  font-style: italic;
}

.user-meta-compact {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 0;
  font-size: 13px;
}

.meta-label {
  color: rgba(255, 255, 255, 0.7);
  min-width: 40px;
  font-weight: 500;
}

.meta-value {
  color: rgba(255, 255, 255, 0.95);
  font-weight: 400;
}

.user-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 0 1px 5px rgba(0, 0, 0, 0.5);
}

.meta-item .el-icon {
  font-size: 16px;
}

.stats-section {
  margin-top: auto;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-card:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.stat-icon {
  font-size: 28px;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: white;
  text-shadow: 0 1px 5px rgba(0, 0, 0, 0.3);
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
}

.user-nickname {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.user-badges {
  display: flex;
  gap: 8px;
}

.badge {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.badge-icon {
  font-size: 14px;
}

.level-badge {
  background: linear-gradient(135deg, #e0f7ff, #b3e5fc);
  color: #1976d2;
}

.member-badge {
  background: linear-gradient(135deg, #fff8e1, #ffecb3);
  color: #f57c00;
}

.verify-badge {
  background: linear-gradient(135deg, #e8f5e9, #c8e6c9);
  color: #388e3c;
}

.stats-section {
  margin-top: auto;
}

.content-section {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.tab-menu {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #f8f9fa, #ffffff);
}

.tab-list {
  display: flex;
  gap: 8px;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  position: relative;
}

.tab-item:hover {
  background: #f0f0f0;
  color: #333;
}

.tab-item.active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.tab-icon {
  font-size: 16px;
}

.tab-count {
  background: rgba(0, 0, 0, 0.1);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.tab-item.active .tab-count {
  background: rgba(255, 255, 255, 0.2);
}

.tab-actions {
  display: flex;
  gap: 12px;
}

.search-input {
  width: 200px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
}

.sort-select {
  width: 120px;
}

.sort-select :deep(.el-input__wrapper) {
  border-radius: 20px;
}

.tab-content {
  padding: 24px;
  min-height: 400px;
}

.upload-filter-bar {
  margin-bottom: 20px;
}

.filter-tabs {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-tab {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.filter-tab:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.filter-tab.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
}

.filter-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
}

.filter-tab.active .filter-count {
  background: rgba(255, 255, 255, 0.25);
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.video-card {
  background: #f8f9fa;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.video-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.video-thumbnail {
  position: relative;
  padding-top: 56.25%;
  background: #000;
}

.video-thumbnail img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.video-card:hover .video-thumbnail img {
  transform: scale(1.05);
}

.video-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.video-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.video-card:hover .video-overlay {
  opacity: 1;
}

.play-icon {
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #333;
  transition: transform 0.3s ease;
}

.video-card:hover .play-icon {
  transform: scale(1.1);
}

.video-hover-info {
  position: absolute;
  bottom: 0;
  right: 0;
  left: 0;
  padding: 12px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
  color: white;
  opacity: 0;
  transition: opacity 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.video-card:hover .video-hover-info {
  opacity: 1;
}

.hover-title {
  font-size: 14px;
  font-weight: 600;
  margin: 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.hover-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  opacity: 0.9;
}

.hover-meta .meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.video-status-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  backdrop-filter: blur(8px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.video-status-badge.status-published {
  background: rgba(76, 175, 80, 0.95);
  color: white;
  border: 1px solid rgba(76, 175, 80, 0.3);
}

.video-status-badge.status-reviewing {
  background: rgba(255, 152, 0, 0.95);
  color: white;
  border: 1px solid rgba(255, 152, 0, 0.3);
}

.video-status-badge.status-rejected {
  background: rgba(244, 67, 54, 0.95);
  color: white;
  border: 1px solid rgba(244, 67, 54, 0.3);
}

.like-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  font-size: 16px;
}

.video-info {
  padding: 16px;
  background: #ffffff;
  transition: background 0.3s ease;
}

.video-card:hover .video-info {
  background: #f8f9fa;
}

.video-title {
  font-size: 15px;
  font-weight: 600;
  color: #000000;
  margin: 0 0 10px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: all 0.3s ease;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.video-card:hover .video-title {
  color: #000000;
  transform: translateY(-2px);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
}

.video-meta {
  display: flex;
  gap: 14px;
  font-size: 12px;
  color: #888;
  transition: color 0.3s ease;
}

.video-card:hover .video-meta {
  color: #666;
}

.video-meta .meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  transition: transform 0.3s ease;
}

.video-card:hover .video-meta .meta-item {
  transform: translateY(-1px);
}

.video-date {
  font-size: 12px;
  color: #aaa;
  margin-top: 6px;
}

.video-author {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}

.author-avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
}

.video-author span {
  font-size: 12px;
  color: #666;
}

.video-actions-row {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.user-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.user-card:hover {
  background: #f0f1f3;
}

.user-card .user-avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.user-card .user-avatar {
  width: 60px;
  height: 60px;
}

.online-dot {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  background: #4caf50;
  border: 2px solid white;
  border-radius: 50%;
}

.user-card .user-info {
  flex: 1;
  min-width: 0;
}

.user-card .user-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px;
}

.user-card .user-desc {
  font-size: 12px;
  color: #888;
  margin: 0 0 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-card .user-stats {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #aaa;
}

.collection-folders {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.folder-card {
  background: #f8f9fa;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.folder-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.folder-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
}

.folder-icon {
  font-size: 32px;
}

.folder-info {
  flex: 1;
}

.folder-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px;
}

.folder-count {
  font-size: 12px;
  color: #888;
}

.folder-preview {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2px;
  padding: 0 2px 2px;
}

.preview-thumb {
  width: 100%;
  aspect-ratio: 16/9;
  object-fit: cover;
  border-radius: 4px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-text {
  font-size: 16px;
  color: #888;
  margin: 0 0 20px;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .header-inner {
    padding: 0 15px;
  }
  
  .header-center {
    display: none;
  }
  
  .header-actions {
    display: none;
  }
  
  .center-content {
    padding: 15px;
  }
  
  .profile-main {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .info-section {
    padding-top: 20px;
  }
  
  .user-header {
    flex-direction: column;
  }
  
  .user-meta {
    justify-content: center;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .tab-menu {
    flex-direction: column;
    gap: 12px;
  }
  
  .tab-list {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .tab-actions {
    width: 100%;
  }
  
  .search-input {
    flex: 1;
  }
}

.preview-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
}

.preview-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
}

.preview-dialog :deep(.el-dialog__body) {
  padding: 20px;
  max-height: 70vh;
  overflow: hidden;
}

.preview-content {
  display: flex;
  gap: 20px;
  height: 100%;
  max-height: calc(70vh - 40px);
}

.preview-left {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.preview-right {
  width: 320px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
}

.preview-video-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.preview-video-player {
  width: 100%;
  max-height: 350px;
  display: block;
}

.preview-info {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-top: 16px;
}

.preview-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 0;
}

.preview-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.preview-meta .meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.preview-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fff3e0;
  color: #ff9800;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
}

.preview-status.rejected {
  background: #ffebee;
  color: #f44336;
}

.resource-section {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e0e0e0;
  flex-shrink: 0;
}

.resource-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.resource-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.resource-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: white;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.resource-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.resource-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 8px;
  color: white;
  font-size: 20px;
}

.resource-info {
  flex: 1;
  min-width: 0;
}

.resource-name {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.resource-meta {
  display: block;
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.resource-actions {
  display: flex;
  gap: 8px;
}

.resource-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #999;
  flex: 1;
  min-height: 0;
}

.resource-empty .empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
  color: #ddd;
}

.resource-empty p {
  margin: 0;
  font-size: 14px;
}

.video-actions-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e0e0e0;
  display: flex;
  justify-content: center;
}

.upload-resource-dialog :deep(.el-dialog__body) {
  padding: 16px 20px;
  max-height: 60vh;
  overflow-y: auto;
}

.resource-upload-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.form-label .required {
  color: #f56c6c;
}

.resource-uploader {
  width: 100%;
}

.resource-uploader :deep(.el-upload) {
  width: 100%;
}

.resource-uploader :deep(.el-upload-dragger) {
  width: 100%;
  height: 140px;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.resource-uploader :deep(.el-upload-dragger:hover) {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.upload-icon-large {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  margin-bottom: 12px;
}

.upload-icon-large .el-icon {
  font-size: 24px;
  color: white;
}

.upload-text {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.upload-hint {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.file-selected {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  width: 100%;
}

.file-icon {
  font-size: 32px;
  color: #667eea;
}

.file-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.file-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-size {
  font-size: 12px;
  color: #999;
}

.remove-file-btn {
  flex-shrink: 0;
}

.progress-row {
  margin-top: 8px;
}

.edit-form {
  max-height: 60vh;
  overflow-y: auto;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.upload-buttons {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e0e0e0;
}

.background-upload {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.background-preview {
  width: 100%;
  max-width: 400px;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #e0e0e0;
}

.background-placeholder {
  width: 100%;
  max-width: 400px;
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 8px;
  border: 2px dashed #d0d0d0;
  color: #999;
}

.pending-file-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  padding: 8px 12px;
  background: #f0f9eb;
  border-radius: 6px;
  font-size: 13px;
  color: #67c23a;
}

.pending-file-info span {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
  line-height: 1.5;
}

.profile-detail {
  padding: 10px 0;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.detail-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #e0e0e0;
}

.detail-info h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
}

.detail-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 12px;
  color: #999;
  font-weight: 500;
}

.detail-value {
  font-size: 14px;
  color: #333;
  font-weight: 400;
}
</style>
