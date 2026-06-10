<template>
  <div class="history-page">
    <header class="history-header" :style="{ backgroundImage: `url(${headerBgImage})` }">
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

    <div class="history-container">
      <div class="history-content">
        <div class="page-header">
          <div class="header-title">
            <div class="title-icon">⏱️</div>
            <div class="title-text">
              <h1>{{ t('history.title') }}</h1>
              <p>{{ t('history.subtitle') }}</p>
            </div>
          </div>
          <div class="header-actions">
            <el-button type="danger" plain @click="clearAllHistory" :disabled="filteredHistory.length === 0">
              <el-icon><el-icon-delete /></el-icon>
              {{ t('history.clearAll') }}
            </el-button>
          </div>
        </div>

        <div class="filter-section">
          <div class="filter-header">
            <div class="filter-title">
              <el-icon class="filter-title-icon"><el-icon-filter /></el-icon>
              <span>历史记录</span>
            </div>
            <div class="view-toggle-wrapper">
              <div class="view-toggle">
                <div 
                  class="view-btn" 
                  :class="{ active: viewMode === 'grid' }" 
                  @click="viewMode = 'grid'"
                >
                  <el-icon><el-icon-grid /></el-icon>
                  <span class="view-btn-text">网格</span>
                </div>
                <div 
                  class="view-btn" 
                  :class="{ active: viewMode === 'list' }" 
                  @click="viewMode = 'list'"
                >
                  <el-icon><el-icon-list /></el-icon>
                  <span class="view-btn-text">列表</span>
                </div>
              </div>
            </div>
          </div>

          <div class="filter-content">
            <div class="filter-actions">
              <div class="search-box">
                <el-input
                  v-model="searchKeyword"
                  placeholder="搜索历史记录"
                  class="search-input"
                  clearable
                  size="large"
                >
                  <template #prefix>
                    <el-icon><el-icon-search /></el-icon>
                  </template>
                </el-input>
              </div>
              <div class="sort-buttons">
                <div 
                  class="sort-btn" 
                  :class="{ active: sortType === 'newest' }"
                  @click="sortType = 'newest'"
                >
                  <el-icon><el-icon-sort-down /></el-icon>
                  最新
                </div>
                <div 
                  class="sort-btn" 
                  :class="{ active: sortType === 'oldest' }"
                  @click="sortType = 'oldest'"
                >
                  <el-icon><el-icon-sort-up /></el-icon>
                  最早
                </div>
              </div>
            </div>
            <div class="filter-tips">
              <el-icon class="tip-icon"><el-icon-info-filled /></el-icon>
              <span class="tip-text">点击视频可继续观看，支持搜索和排序功能</span>
            </div>
          </div>
        </div>

        <div class="stats-bar">
          <div class="stat-item">
            <span class="stat-icon">📊</span>
            <span class="stat-value">{{ filteredHistory.length }}</span>
            <span class="stat-label">历史记录</span>
          </div>
          <div class="stat-item">
            <span class="stat-icon">🎬</span>
            <span class="stat-value">{{ getTypeCount('video') }}</span>
            <span class="stat-label">视频</span>
          </div>
          <div class="stat-item">
            <span class="stat-icon">⏰</span>
            <span class="stat-value">{{ totalWatchTime }}</span>
            <span class="stat-label">总观看时间</span>
          </div>
        </div>

        <el-skeleton :loading="loading" animated>
          <template #template>
            <div v-for="i in 12" :key="i" class="history-card skeleton-card">
              <div class="card-thumbnail">
                <el-skeleton-item variant="image" style="width: 100%; height: 160px" />
              </div>
              <div class="card-content">
                <el-skeleton-item variant="text" style="width: 80%" />
                <el-skeleton-item variant="text" style="width: 60%" />
              </div>
            </div>
          </template>
          
          <div v-if="!loading && filteredHistory.length > 0" class="history-list">

            <div v-if="viewMode === 'grid'" class="grid-view">
              <div 
                class="history-card" 
                v-for="item in filteredHistory" 
                :key="item.id"
                @click="handleItemClick(item)"
              >
                <div class="card-thumbnail">
                  <img :src="item.thumbnail" :alt="item.title" />
                  <div class="card-type-badge video">
                    {{ getTypeIcon('video') }}
                  </div>
                  <div class="card-progress-overlay">
                    <el-progress 
                      :percentage="item.progress" 
                      :stroke-width="4"
                      :show-text="false"
                      class="progress-bar"
                    />
                  </div>
                  <div class="card-duration" v-if="item.duration">{{ item.duration }}</div>
                  <div class="card-play-overlay">
                    <div class="play-btn">
                      <el-icon><el-icon-video-play /></el-icon>
                    </div>
                  </div>
                </div>
                <div class="card-content">
                  <h3 class="card-title">{{ item.title }}</h3>
                  <div class="card-meta">
                    <div class="meta-row">
                      <span class="meta-item publisher">{{ item.publisherName }}</span>
                      <span class="meta-item views">{{ item.viewCount || 0 }} 次观看</span>
                    </div>
                    <div class="type-tags" v-if="item.typeNames && item.typeNames.length > 0">
                      <el-tag v-for="(type, index) in item.typeNames" :key="index" size="small" effect="plain" class="type-tag">
                        {{ type }}
                      </el-tag>
                    </div>
                  </div>
                  <div class="card-footer">
                    <span class="card-time">
                      <el-icon><el-icon-clock /></el-icon>
                      {{ formatTime(item.watchTime) }}
                    </span>
                    <el-dropdown trigger="click" @click.stop>
                      <el-button text size="small" class="more-btn">
                        <el-icon><el-icon-more /></el-icon>
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item @click="removeFromHistory(item)">
                            <el-icon><el-icon-delete /></el-icon>
                            删除记录
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="list-view">
              <div 
                class="history-list-item" 
                v-for="item in filteredHistory" 
                :key="item.id"
                @click="handleItemClick(item)"
              >
                <div class="list-thumbnail">
                  <img :src="item.thumbnail" :alt="item.title" />
                  <div class="list-type-badge video">
                    {{ getTypeIcon('video') }}
                  </div>
                  <div class="list-duration" v-if="item.duration">{{ item.duration }}</div>
                </div>
                <div class="list-content">
                  <div class="list-main">
                    <h3 class="list-title">{{ item.title }}</h3>
                    <div class="list-meta-info">
                      <span class="publisher">{{ item.publisherName }}</span>
                      <span class="dot">·</span>
                      <span class="views">{{ item.viewCount || 0 }} 次观看</span>
                      <span class="dot">·</span>
                      <span class="watch-time">
                        <el-icon><el-icon-clock /></el-icon>
                        {{ formatTime(item.watchTime) }}
                      </span>
                    </div>
                    <div class="type-tags" v-if="item.typeNames && item.typeNames.length > 0">
                      <el-tag v-for="(type, index) in item.typeNames" :key="index" size="small" effect="plain" class="type-tag">
                        {{ type }}
                      </el-tag>
                    </div>
                  </div>
                  <div class="list-meta">
                    <div class="meta-row">
                      <span class="meta-item progress-text">
                        进度: {{ item.progress }}%
                      </span>
                    </div>
                    <el-progress 
                      :percentage="item.progress" 
                      :stroke-width="6"
                      class="list-progress"
                    />
                  </div>
                  <div class="list-actions">
                    <el-button type="primary" size="small" @click.stop="continueWatch(item)">
                      <el-icon><el-icon-video-play /></el-icon>
                      继续观看
                    </el-button>
                    <el-dropdown trigger="click" @click.stop>
                      <el-button size="small">
                        <el-icon><el-icon-more /></el-icon>
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item @click="removeFromHistory(item)">
                            <el-icon><el-icon-delete /></el-icon>
                            删除记录
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-skeleton>

        <div class="empty-state" v-if="!loading && filteredHistory.length === 0">
          <div class="empty-icon">📭</div>
          <h3 class="empty-title">暂时还没有历史记录</h3>
          <p class="empty-desc">您还没有观看过任何视频，开始探索吧！</p>
          <el-button type="primary" @click="goToFirst">
            <el-icon><el-icon-explore /></el-icon>
            返回首页
          </el-button>
        </div>

      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserInfoStore } from '@/stores/userinfo'
import { GetVideoHistory } from '@/api/Users'

const { t, locale } = useI18n()
const router = useRouter()
const userInfoStore = useUserInfoStore()

const currentNav = ref('home')
const searchKeyword = ref('')
const sortType = ref('newest')
const viewMode = ref('grid')
const currentPage = ref(1)
const pageSize = ref(12)
const loading = ref(false)

const headerBgImage = ref('https://lizhanghao.oss-cn-beijing.aliyuncs.com/%E8%8F%9C%E5%8D%95%E6%A0%8F/487-1800x70.jpg')

const userInfo = ref({
  id: '1',
  nickname: 'XSJ用户',
  avatar: 'https://picsum.photos/200/200?random=avatar',
})

const historyItems = ref([])

const currentLangText = computed(() => {
  const langMap = { zh: '中文', en: 'English', ja: '日本語' }
  return langMap[locale.value] || '中文'
})

const filteredHistory = computed(() => {
  let items = [...historyItems.value]

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    items = items.filter(item => 
      item.title.toLowerCase().includes(keyword)
    )
  }

  if (sortType.value === 'newest') {
    items.sort((a, b) => new Date(b.watchTime) - new Date(a.watchTime))
  } else if (sortType.value === 'oldest') {
    items.sort((a, b) => new Date(a.watchTime) - new Date(b.watchTime))
  }

  return items
})

const totalWatchTime = computed(() => {
  const totalSeconds = historyItems.value.reduce((acc, item) => {
    const videoTime = item.videoTime || 0
    const progress = item.progress || 0
    return acc + Math.floor(videoTime * progress / 100)
  }, 0)
  const hours = Math.floor(totalSeconds / 3600)
  const minutes = Math.floor((totalSeconds % 3600) / 60)
  const seconds = totalSeconds % 60
  
  if (hours > 0) {
    return `${hours}h ${minutes}m`
  } else if (minutes > 0) {
    return `${minutes}m ${seconds}s`
  } else {
    return `${seconds}s`
  }
})

const formatTime = (date) => {
  const now = new Date()
  const diff = now - new Date(date)
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return new Date(date).toLocaleDateString()
}

const getTypeIcon = (type) => {
  return '🎬'
}

const getTypeCount = (type) => {
  return historyItems.value.length
}

const getVideoDuration = (seconds) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
}

const fetchHistory = async () => {
  loading.value = true
  try {
    console.log('开始获取历史记录...')
    const response = await GetVideoHistory()
    console.log('获取历史记录响应:', response)
    if (response && response.code === 0) {
      console.log('历史记录数据:', response.data)
      historyItems.value = response.data.map((item, index) => ({
        id: index + 1,
        encode: item.enCode,
        title: item.videoName,
        thumbnail: item.videoImageUrl ? item.videoImageUrl.replace(/[\r\n`]/g, '') : '',
        duration: getVideoDuration(item.videoTime || 0),
        progress: item.duration || 0,
        videoTime: item.videoTime || 0,
        watchTime: item.watchTime,
        videoUrl: item.videoUrl ? item.videoUrl.replace(/[\r\n`]/g, '') : '',
        publisherName: item.publisherName,
        viewCount: item.viewCount,
        likeCount: item.likeCount,
        commentCount: item.commentCount,
        shareCount: item.shareCount,
        collectCount: item.collectCount,
        typeNames: item.typeNames
      }))
      console.log('处理后的历史记录:', historyItems.value)
    } else {
      console.error('获取历史记录失败，响应格式错误:', response)
      ElMessage.error('获取历史记录失败，响应格式错误')
    }
  } catch (error) {
    console.error('获取历史记录失败:', error)
    console.error('错误详情:', error.message, error.response)
    ElMessage.error('获取历史记录失败，请稍后重试')
  } finally {
    loading.value = false
    console.log('获取历史记录完成')
  }
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

const clearAllHistory = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有历史记录吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // 这里可以调用后端接口清空历史记录
    historyItems.value = []
    ElMessage.success('历史记录已清空')
  } catch {
    // 用户取消
  }
}

const handleItemClick = (video) => {
  console.log('点击视频:', video)
  console.log('视频encode:', video.encode)
  console.log('播放进度:', video.progress)
  
  if (video.encode) {
    const videoData = {
      ...video,
      startTime: video.progress || 0
    }
    sessionStorage.setItem('currentVideo', JSON.stringify(videoData))
    router.push({
      path: '/Video',
      query: { 
        encode: video.encode,
        progress: video.progress || 0
      }
    })
  } else {
    console.warn('视频缺少encode，无法跳转:', video)
    ElMessage.warning('视频信息不完整，无法播放')
  }
}

const continueWatch = (item) => {
  handleItemClick(item)
}

const removeFromHistory = (item) => {
  const index = historyItems.value.findIndex(i => i.id === item.id)
  if (index > -1) {
    historyItems.value.splice(index, 1)
    ElMessage.success('历史记录已删除')
  }
}

const goHome = () => router.push('/First')
const goToFirst = () => router.push('/First')
const goToAI = () => router.push('/ai/home')

onMounted(() => {
  fetchHistory()
})
</script>

<style scoped>
.history-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.history-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 80px;
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
  padding: 12px 22px;
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
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid white;
}

.history-container {
  padding-top: 80px;
  min-height: calc(100vh - 80px);
}

.history-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 24px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.header-title {
  display: flex;
  align-items: center;
  gap: 16px;
}

.title-icon {
  font-size: 48px;
}

.title-text h1 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 4px;
}

.title-text p {
  font-size: 14px;
  color: #888;
  margin: 0;
}

.filter-section {
  background: white;
  border-radius: 20px;
  padding: 0;
  margin-bottom: 24px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.filter-title {
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.filter-title-icon {
  font-size: 22px;
}

.view-toggle-wrapper {
  display: flex;
  align-items: center;
}

.view-toggle {
  display: flex;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  padding: 4px;
  backdrop-filter: blur(10px);
}

.view-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  font-weight: 500;
}

.view-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  color: white;
}

.view-btn.active {
  background: white;
  color: #667eea;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.view-btn-text {
  font-size: 14px;
}

.filter-content {
  padding: 24px;
}

.filter-actions {
  display: flex;
  gap: 16px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.search-box {
  flex: 1;
  max-width: 400px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.search-input :deep(.el-input__wrapper):hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.sort-buttons {
  display: flex;
  gap: 8px;
}

.sort-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  background: #f5f7fa;
  border-radius: 10px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.sort-btn:hover {
  background: #e8ecf1;
}

.sort-btn.active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.filter-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  margin-top: 16px;
  background: linear-gradient(135deg, #f0f4ff 0%, #e8f0fe 100%);
  border-radius: 10px;
  border-left: 4px solid #667eea;
}

.tip-icon {
  font-size: 16px;
  color: #667eea;
}

.tip-text {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}

.stats-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 28px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.08);
  transition: all 0.3s ease;
  flex: 1;
  min-width: 180px;
  position: relative;
  overflow: hidden;
}

.stat-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
}

.stat-item:hover::before {
  opacity: 1;
}

.stat-icon {
  font-size: 28px;
  transition: transform 0.3s ease;
}

.stat-item:hover .stat-icon {
  transform: scale(1.1);
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 12px;
  color: #888;
}

.history-list {
  margin-bottom: 24px;
}

.grid-view {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.skeleton-card {
  opacity: 0.8;
}

.history-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.history-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12);
}

.card-thumbnail {
  position: relative;
  padding-top: 56.25%;
  overflow: hidden;
}

.card-thumbnail img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.history-card:hover .card-thumbnail img {
  transform: scale(1.1);
}

.card-type-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 6px;
  font-size: 14px;
  backdrop-filter: blur(8px);
  opacity: 0.8;
  transition: all 0.3s ease;
}

.history-card:hover .card-type-badge {
  opacity: 0;
  transform: translateY(-5px);
}

.card-type-badge.video { 
  background: rgba(255, 107, 107, 0.7);
}

.card-progress-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.85), transparent);
}

.progress-bar :deep(.el-progress-bar__outer) {
  background-color: rgba(255, 255, 255, 0.3);
  height: 6px;
}

.progress-bar :deep(.el-progress-bar__inner) {
  background: linear-gradient(90deg, #667eea, #764ba2);
}

.card-duration {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.card-play-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.2);
  opacity: 0;
  transition: all 0.3s ease;
}

.history-card:hover .card-play-overlay {
  opacity: 1;
  background: rgba(0, 0, 0, 0.35);
}

.play-btn {
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
  transform: scale(0.8);
}

.history-card:hover .play-btn {
  transform: scale(1);
}

.play-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.5);
  transform: scale(1.1);
}

.card-content {
  padding: 16px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  margin-bottom: 12px;
}

.card-meta .meta-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.card-meta .meta-item {
  font-size: 12px;
  color: #888;
}

.card-meta .publisher {
  font-weight: 500;
  color: #666;
}

.type-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.type-tag {
  font-size: 11px;
  height: 20px;
  line-height: 20px;
  padding: 0 8px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.card-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #aaa;
}

.more-btn {
  padding: 4px;
}

.list-view {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.history-list-item {
  display: flex;
  gap: 20px;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  padding: 16px;
}

.history-list-item:hover {
  transform: translateX(5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.list-thumbnail {
  position: relative;
  width: 240px;
  height: 135px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
}

.list-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.history-list-item:hover .list-thumbnail img {
  transform: scale(1.1);
}

.list-type-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 6px;
  font-size: 14px;
  backdrop-filter: blur(8px);
  opacity: 0.8;
  transition: all 0.3s ease;
}

.history-list-item:hover .list-type-badge {
  opacity: 0;
  transform: translateY(-5px);
}

.list-type-badge.video { 
  background: rgba(255, 107, 107, 0.7);
}

.list-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 3px 6px;
  border-radius: 4px;
  font-size: 11px;
}

.list-content {
  flex: 1;
  display: flex;
  gap: 20px;
  min-width: 0;
}

.list-main {
  flex: 2;
}

.list-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
  line-height: 1.4;
}

.list-meta-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 13px;
  color: #888;
}

.list-meta-info .publisher {
  font-weight: 500;
  color: #666;
}

.list-meta-info .dot {
  color: #ccc;
}

.list-meta-info .watch-time {
  display: flex;
  align-items: center;
  gap: 4px;
}

.list-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 0 16px;
  border-left: 1px solid #f0f0f0;
}

.meta-row {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.list-meta .meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #888;
}

.progress-text {
  color: #667eea;
  font-weight: 500;
}

.list-progress {
  margin-top: auto;
}

.list-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  justify-content: center;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
  opacity: 0.6;
}

.empty-title {
  font-size: 24px;
  color: #333;
  margin: 0 0 12px;
}

.empty-desc {
  font-size: 16px;
  color: #888;
  margin: 0 0 24px;
}

@media (max-width: 1200px) {
  .filter-actions {
    flex-direction: column;
  }
  
  .search-box {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .header-inner {
    padding: 0 15px;
  }
  
  .header-center {
    display: none;
  }
  
  .history-content {
    padding: 15px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .header-title {
    flex-direction: column;
  }
  
  .filter-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .filter-title {
    justify-content: center;
  }
  
  .view-toggle {
    width: 100%;
    justify-content: center;
  }
  
  .view-btn {
    flex: 1;
    justify-content: center;
  }
  
  .stats-bar {
    flex-direction: column;
  }
  
  .stat-item {
    min-width: 100%;
  }
  
  .history-list-item {
    flex-direction: column;
  }
  
  .list-thumbnail {
    width: 100%;
    height: auto;
    padding-top: 56.25%;
  }
  
  .list-content {
    flex-direction: column;
  }
  
  .list-meta {
    border-left: none;
    border-top: 1px solid #f0f0f0;
    padding: 12px 0;
  }
  
  .list-actions {
    flex-direction: row;
  }
}
</style>
