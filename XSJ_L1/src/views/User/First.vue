<template>
  <div class="home-page">
    <!-- 顶部导航栏 -->
    <header class="home-header">
      <div class="header-left">
        <div class="logo">
          <el-icon class="logo-icon"><VideoPlay /></el-icon>
          <span class="logo-text">XSJ AI教学</span>
        </div>
        <nav class="header-nav">
          <a href="#" class="nav-item active" @click.prevent="goToVideo">{{ t('nav.home') }}</a>
          <a href="#" class="nav-item" @click.prevent="goToAI">{{ t('nav.ai') }}</a>
          <a href="#" class="nav-item" @click.prevent="goToHistory">{{ t('nav.history') }}</a>
          <a href="#" class="nav-item" @click.prevent="goToUpload">{{ t('nav.upload') }}</a>
          <a href="#" class="nav-item message-nav" @click.prevent="goToChat">
            <el-icon><el-icon-message /></el-icon>
            {{ t('video.message') }}
            <span class="unread-badge" v-if="totalUnreadCount > 0">{{ totalUnreadCount > 99 ? '99+' : totalUnreadCount }}</span>
          </a>
        </nav>
      </div>
      
      <div class="header-center">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            :placeholder="t('home.searchPlaceholder')"
            prefix-icon="el-icon-search"
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button class="search-btn" @click="handleSearch">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
      
      <div class="header-right">
        <div class="header-actions">
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
                    <el-icon><el-icon-check /></el-icon>
                    <span>{{ t('system.chinese') }}</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="en" :class="{ active: locale === 'en' }">
                    <el-icon><el-icon-check /></el-icon>
                    <span>{{ t('system.english') }}</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="ja" :class="{ active: locale === 'ja' }">
                    <el-icon><el-icon-check /></el-icon>
                    <span>{{ t('system.japanese') }}</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
          
        <div class="user-info">
          <div class="learning-stats">
            <el-icon class="stats-icon"><TrendCharts /></el-icon>
            <span class="stats-text">今日学习 2.5小时</span>
          </div>
          <div class="user-avatar" @click="toggleUserMenu">
            <img :src="userInfoStore.info.avatar || 'https://picsum.photos/100/100?random=1'" :alt="userInfoStore.info.nickName || '用户头像'" />
            <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
          </div>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧边栏 -->
      <aside class="sidebar">
        <!-- AI助手卡片 -->
        <div class="ai-assistant-card">
          <div class="card-header">
            <el-icon class="ai-icon"><el-icon-magic-stick /></el-icon>
            <span class="card-title">{{ t('ai.title') }}</span>
          </div>
          <div class="ai-chat-box">
            <div class="chat-messages">
              <div class="message ai-message">
                <div class="message-avatar">
                  <el-icon><el-icon-magic-stick /></el-icon>
                </div>
                <div class="message-content">
                  <p>{{ t('ai.greeting') }}</p>
                </div>
              </div>
            </div>
            <div class="chat-input-area">
              <el-input
                v-model="chatMessage"
                type="textarea"
                :rows="3"
                :placeholder="t('ai.placeholder')"
                class="chat-input"
                @keydown.enter.exact.prevent="sendChatMessage"
              />
              <el-button type="primary" class="send-btn" @click="sendChatMessage">
                <el-icon><el-icon-promotion /></el-icon>
                {{ t('ai.send') }}
              </el-button>
            </div>
          </div>
        </div>

        <!-- 学习进度 -->
        <div class="progress-card">
          <div class="card-header">
            <el-icon class="progress-icon"><el-icon-data-line /></el-icon>
            <span class="card-title">{{ t('sidebar.learningProgress') }}</span>
          </div>
          <div class="progress-list" v-if="learningProgressList.length > 0">
            <div class="progress-item" v-for="item in learningProgressList" :key="item.encode" @click="handleProgressClick(item)">
              <div class="progress-thumbnail">
                <img :src="item.thumbnail || 'https://picsum.photos/80/45?random=1'" :alt="item.title" />
                <div class="progress-overlay">
                  <el-icon><el-icon-video-play /></el-icon>
                </div>
              </div>
              <div class="progress-info">
                <span class="progress-name">{{ item.title }}</span>
                <el-progress :percentage="item.progress" :stroke-width="6" :show-text="false" />
                <span class="progress-percent">{{ item.progress }}%</span>
              </div>
            </div>
          </div>
          <div class="empty-progress" v-else>
            <el-icon class="empty-icon"><el-icon-video-pause /></el-icon>
            <span>暂无学习进度</span>
          </div>
        </div>

      </aside>

      <!-- 中间内容区 -->
      <main class="content-area">
        <!-- 分类导航 -->
        <div class="category-nav">
          <div class="category-item active" @click="handleCategoryClick('')">全部</div>
          <div class="category-item" @click="handleCategoryClick('前端开发')">前端开发</div>
          <div class="category-item" @click="handleCategoryClick('后端开发')">后端开发</div>
          <div class="category-item" @click="handleCategoryClick('人工智能')">人工智能</div>
          <div class="category-item" @click="handleCategoryClick('数据科学')">数据科学</div>
          <div class="category-item" @click="handleCategoryClick('移动开发')">移动开发</div>
          <div class="category-item" @click="handleCategoryClick('设计')">设计</div>
          <div class="category-item" @click="handleCategoryClick('')">更多</div>
        </div>

        <!-- 轮播图 -->
        <Carousel :items="carouselItems" @click="handleCarouselClick" />

        <!-- 推荐视频 -->
        <div class="video-section">
          <div class="section-header">
            <h2 class="section-title">
              <el-icon><el-icon-star /></el-icon>
              {{ t('home.recommended') }}
            </h2>
            <div class="section-actions">
              <el-button text class="refresh-btn" @click="handleRefreshVideos">
                <el-icon><el-icon-refresh /></el-icon>
                {{ t('home.refresh') }}
              </el-button>
            </div>
          </div>
          
          <div class="video-grid">
            <div class="video-card" v-for="video in recommendedVideos" :key="video.id" @click="handleVideoClick(video)">
              <div class="video-thumbnail">
                <img :src="video.thumbnail" :alt="video.title" />
                <div class="video-duration">{{ video.duration }}</div>
                <div class="video-overlay">
                  <el-icon class="play-icon"><el-icon-video-play /></el-icon>
                </div>
              </div>
              <div class="video-info">
                <h3 class="video-title">{{ video.title }}</h3>
                <div class="video-meta">
                  <div class="meta-item">
                    <el-icon class="meta-icon"><el-icon-user /></el-icon>
                    <span>{{ video.author }}</span>
                  </div>
                  <div class="meta-item">
                    <el-icon class="meta-icon"><el-icon-view /></el-icon>
                    <span>{{ formatNumber(video.views) }}{{ t('home.views') }}</span>
                  </div>
                  <div class="meta-item" v-if="video.publisherName">
                    <el-icon class="meta-icon"><el-icon-user /></el-icon>
                    <span>{{ video.publisherName }}</span>
                  </div>
                </div>
                <div class="video-tags">
                  <span class="tag">Vue3</span>
                  <span class="tag">TypeScript</span>
                  <span class="tag">实战</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 热门课程 -->
        <div class="video-section hot-courses-section">
          <div class="section-header">
            <h2 class="section-title">
              <el-icon><el-icon-fire-filled /></el-icon>
              {{ t('home.hotCourses') }}
            </h2>
          </div>
          
          <div class="hot-video-grid" v-infinite-scroll="loadMoreVideos" :infinite-scroll-disabled="loading" :infinite-scroll-distance="200">
            <div class="hot-video-card" v-for="video in hotVideos" :key="video.id" @click="handleVideoClick(video)">
              <div class="hot-thumbnail">
                <img :src="video.thumbnail" :alt="video.title" />
                <div class="hot-duration">{{ video.duration }}</div>
                <div class="hot-overlay">
                  <el-icon class="hot-play-icon"><el-icon-video-play /></el-icon>
                </div>
              </div>
              <div class="hot-info">
                <h4 class="hot-title">{{ video.title }}</h4>
                <div class="hot-meta">
                  <span class="hot-meta-item">
                    <el-icon><el-icon-user /></el-icon>
                    <span :class="{ 'system-text': isSystemText('home.author') }">{{ isSystemText('home.author') ? t('home.author') : '' }}: {{ video.author }}</span>
                  </span>
                  <span class="hot-meta-item">
                    <el-icon><el-icon-view /></el-icon>
                    <span :class="{ 'system-text': isSystemText('home.views') }">{{ formatNumber(video.views) }}{{ isSystemText('home.views') ? t('home.views') : '' }}</span>
                  </span>
                </div>
              </div>
            </div>
            <div v-if="loading" class="loading-more">
              <el-icon class="is-loading"><el-icon-loading /></el-icon>
              <span>加载中...</span>
              
            </div>
          </div>
        </div>
      </main>

      <!-- 右侧边栏 -->
      <aside class="right-sidebar">
        <!-- 日历卡片 -->
        <div class="calendar-card">
          <div class="card-header">
            <el-icon class="calendar-icon"><el-icon-calendar /></el-icon>
            <span class="card-title">{{ currentDate }}</span>
          </div>
          <el-calendar v-model="calendarDate" class="custom-calendar">
            <template #date-cell="{ data }">
              <div class="calendar-cell" :class="{ 'is-today': isToday(data.day) }">
                {{ data.day.split('-')[2] }}
              </div>
            </template>
          </el-calendar>
        </div>

        <!-- 每日一言 -->
        <div class="quote-card">
          <div class="card-header">
            <el-icon class="quote-icon"><el-icon-chat-dot-round /></el-icon>
            <span class="card-title">每日一言</span>
          </div>
          <div class="quote-content">
            <p class="quote-text">"{{ dailyQuote.text }}"</p>
            <p class="quote-author">—— {{ dailyQuote.author }}</p>
          </div>
        </div>

        <!-- 热门标签 -->
        <div class="tags-card">
          <div class="card-header">
            <el-icon class="tags-icon"><el-icon-price-tag /></el-icon>
            <span class="card-title">热门标签</span>
          </div>
          <div class="tag-cloud">
            <span class="cloud-tag" v-for="tag in hotTags" :key="tag" @click="handleTagClick(tag)">{{ tag }}</span>
          </div>
        </div>
        
        <!-- 回到顶部按钮 -->
        <el-tooltip 
          :content="t('home.backToTop')" 
          placement="left" 
          effect="dark"
          :popper-class="'back-to-top-tooltip'"
        >
          <div class="back-to-top" :class="{ 'show': showBackToTop }" @click="scrollToTop">
            <el-icon class="back-icon"><el-icon-top /></el-icon>
          </div>
        </el-tooltip>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Carousel from '@/components/Carousel.vue'
import { GetHomeTop, GetVideos, GetVideoHistory, GetReplyMessages, GetMyInteract, GetFriendApplyList, GetUserAuditInfo } from '@/api/Users'
import { useUserInfoStore } from '@/stores/userinfo'
import { getAiModelsService } from '@/api/Ai'

const { t, locale } = useI18n()
const router = useRouter()
const userInfoStore = useUserInfoStore()

const searchKeyword = ref('')
const chatMessage = ref('')
const showBackToTop = ref(false)
const aiModels = ref([])
const selectedModel = ref(null)

const replyUnread = ref(0)
const interactUnread = ref(0)
const friendApplyUnread = ref(0)
const systemUnread = ref(0)

const totalUnreadCount = computed(() => {
  return replyUnread.value + interactUnread.value + friendApplyUnread.value + systemUnread.value
})

const currentLangText = computed(() => {
  const langMap = {
    zh: '中文',
    en: 'English',
    ja: '日本語'
  }
  return langMap[locale.value] || '中文'
})

// 切换语言方法（首页不调用后端接口）
const changeLocale = (lang) => {
  locale.value = lang
  localStorage.setItem('locale', lang)
  ElMessage.success(lang === 'zh' ? '语言已切换' : (lang === 'en' ? 'Language changed' : '言語が変更されました'))
}

const handleScroll = () => {
  showBackToTop.value = window.scrollY > 300
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  fetchVideosData(false)
  fetchCarouselData()
  fetchAiModels()
  fetchLearningProgress()
  fetchUnreadCounts()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const fetchAiModels = async () => {
  try {
    const res = await getAiModelsService()
    if (res.code === 0 && res.data) {
      aiModels.value = res.data.map(model => ({
        id: model.modelCode,
        name: model.modelName,
        image: model.image || '🤖'
      }))
    }
  } catch (error) {
    console.log('获取AI模型列表失败')
  }
}

const sendChatMessage = () => {
  if (!chatMessage.value.trim()) {
    ElMessage.warning(t('ai.inputPlaceholder'))
    return
  }
  
  let modelToUse = null
  if (aiModels.value.length > 0) {
    if (selectedModel.value) {
      modelToUse = aiModels.value.find(m => m.id === selectedModel.value)
    }
    if (!modelToUse) {
      const randomIndex = Math.floor(Math.random() * aiModels.value.length)
      modelToUse = aiModels.value[randomIndex]
    }
  }
  
  const query = {
    message: chatMessage.value.trim(),
    modelCode: modelToUse?.id || '',
    modelName: modelToUse?.name || ''
  }
  
  chatMessage.value = ''
  
  router.push({
    path: '/ai/dialogue',
    query: {
      q: query.message,
      model: query.modelCode
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}

const goToRegister = () => {
  router.push('/login#register')
}

const goToHistory = () => {
  router.push('/history')
}

const goToUpload = () => {
  router.push('/upload')
}

const goToFavorite = () => {
  router.push('/center?tab=collect')
}

const goToChat = () => {
  router.push('/chat')
}

const rankingList = ref([
  { name: '小明', score: 2890, avatar: 'https://picsum.photos/40/40?random=1' },
  { name: '小红', score: 2650, avatar: 'https://picsum.photos/40/40?random=2' },
  { name: '小王', score: 2480, avatar: 'https://picsum.photos/40/40?random=3' },
  { name: '小李', score: 2340, avatar: 'https://picsum.photos/40/40?random=4' },
  { name: '小张', score: 2190, avatar: 'https://picsum.photos/40/40?random=5' }
])

const hotTags = ref([
  'Vue3', 'React', 'Python', 'AI', '机器学习', '前端开发', '后端开发', '数据科学', 'TypeScript', 'Node.js', 'Docker'
])

const calendarDate = ref(new Date())
const currentDate = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth() + 1
  const day = now.getDate()
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const weekDay = weekDays[now.getDay()]
  return `${year}年${month}月${day}日 ${weekDay}`
})

const isToday = (dateStr) => {
  const today = new Date()
  const [year, month, day] = dateStr.split('-')
  return today.getFullYear() === parseInt(year) &&
         today.getMonth() + 1 === parseInt(month) &&
         today.getDate() === parseInt(day)
}

const dailyQuote = ref({
  text: '学习是一种态度，坚持是一种力量',
  author: 'XSJ AI教学'
})

const learningProgressList = ref([])

const fetchLearningProgress = async () => {
  try {
    const response = await GetVideoHistory()
    if (response && response.code === 0 && response.data) {
      const unfinishedVideos = response.data
        .filter(item => (item.duration || 0) < 100)
        .slice(0, 3)
        .map(item => ({
          encode: item.enCode,
          title: item.videoName,
          progress: item.duration || 0,
          thumbnail: item.videoImageUrl ? item.videoImageUrl.replace(/[\r\n`]/g, '') : '',
          videoUrl: item.videoUrl ? item.videoUrl.replace(/[\r\n`]/g, '') : ''
        }))
      learningProgressList.value = unfinishedVideos
    }
  } catch (error) {
    console.error('获取学习进度失败:', error)
  }
}

const fetchUnreadCounts = async () => {
  try {
    const [replyRes, interactRes, applyRes, auditRes] = await Promise.all([
      GetReplyMessages(),
      GetMyInteract(),
      GetFriendApplyList(),
      GetUserAuditInfo()
    ])
    
    if (replyRes.code === 0 && replyRes.data) {
      replyUnread.value = (replyRes.data.replyMe || []).filter(item => item.isRead === 0).length
    }
    
    if (interactRes.code === 0 && interactRes.data) {
      interactUnread.value = (interactRes.data.otherInteractDetailVO || []).filter(item => item.isRead === 0).length
    }
    
    if (applyRes.code === 0 && applyRes.data) {
      const receivedList = applyRes.data.friendapplyUserVOList || []
      friendApplyUnread.value = receivedList.filter(item => Number(item.status) === 3).length
    }
    
    if (auditRes.code === 0 && auditRes.data) {
      const reviewUnread = (auditRes.data.UserReview || []).filter(item => item.isRead === 0).length
      const banUnread = (auditRes.data.accountStatusList || []).filter(item => item.isRead === 0).length
      systemUnread.value = reviewUnread + banUnread
    }
    
    console.log('=== 未读消息数量统计 ===')
    console.log('回复我的未读:', replyUnread.value)
    console.log('互动未读:', interactUnread.value)
    console.log('好友申请未读:', friendApplyUnread.value)
    console.log('系统通知未读:', systemUnread.value)
    console.log('总未读:', totalUnreadCount.value)
  } catch (error) {
    console.error('获取未读消息数量失败:', error)
  }
}

const handleProgressClick = (video) => {
  if (video.encode) {
    router.push({
      path: '/Video',
      query: { encode: video.encode }
    })
  }
}

const carouselItems = ref([])

const cleanUrl = (url) => {
  if (!url) return ''
  let cleanUrl = url.trim()
  cleanUrl = cleanUrl.replace(/^(URL\s*\r?\n?\s*)/i, '')
  cleanUrl = cleanUrl.replace(/`/g, '')
  return cleanUrl.trim()
}

const fetchCarouselData = async () => {
  try {
    const res = await GetHomeTop()
    if (res.code === 0 && res.data) {
      carouselItems.value = res.data.map(item => ({
        id: item.homeResourcesId,
        image: cleanUrl(item.address),
        jumpUrl: cleanUrl(item.jumUrl),
        fileName: item.fileName
      }))
    }
  } catch (error) {
    console.error('获取轮播图数据失败:', error)
  }
}

const hotVideos = ref([])
const loading = ref(false)
const page = ref(1)
const initialLoad = ref(true)

const recommendedVideos = ref([])

const formatVideoTime = (seconds) => {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}:${secs.toString().padStart(2, '0')}`
}

const fetchVideosData = async (isLoadMore = false) => {
  try {
    if (loading.value) return
    loading.value = true
    
    const mount = isLoadMore ? 15 : 28
    console.log('fetchVideosData - 开始获取视频数据')
    console.log('fetchVideosData - mount参数:', mount)
    console.log('fetchVideosData - 是否加载更多:', isLoadMore)
    
    const res = await GetVideos(mount)
    
    if (res.code === 0 && res.data) {
      const videos = res.data.map(item => {
        return {
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
          isMember: item.isMember,
          isLiked: item.isLiked || 0,
          isCollected: item.isCollected || 0,
          isShared: item.isShared || 0
        }
      })
      
      if (isLoadMore) {
        hotVideos.value = [...hotVideos.value, ...videos]
      } else {
        recommendedVideos.value = videos.slice(0, 8)
        hotVideos.value = videos.slice(0, 20)
      }
      
      console.log('fetchVideosData - 推荐视频数量:', recommendedVideos.value.length)
      console.log('fetchVideosData - 热门视频数量:', hotVideos.value.length)
    }
  } catch (error) {
    console.error('获取视频数据失败:', error)
  } finally {
    loading.value = false
  }
}

const loadMoreVideos = () => {
  fetchVideosData(true)
}

const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

const isSystemText = (key) => {
  const systemTextKeys = [
    'sidebar.learningProgress',
    'sidebar.learningStats',
    'sidebar.learningRanking',
    'sidebar.todayPlan',
    'sidebar.hotTags',
    'home.author',
    'home.views',
    'home.duration',
    'time.completedTime',
    'time.inProgressTime',
    'time.pendingTime'
  ]
  return systemTextKeys.includes(key)
}

const handleVideoClick = (video) => {
  console.log('点击视频:', video)
  console.log('视频encode:', video.encode)
  
  if (video.encode) {
    sessionStorage.setItem('currentVideo', JSON.stringify(video))
    router.push({
      path: '/Video',
      query: { encode: video.encode }
    })
  } else {
    console.warn('视频缺少encode，无法跳转:', video)
    ElMessage.warning('视频信息不完整，无法播放')
  }
}

const handleCarouselClick = (item) => {
  console.log('点击轮播图:', item)
  if (item.jumpUrl) {
    // TODO: 目标页面还没有建立，暂时使用window.open跳转
    // 后续可以改为router.push跳转到对应的路由
    window.open(item.jumpUrl, '_blank')
  }
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleSearch = () => {
  console.log('搜索:', searchKeyword.value)
  if (searchKeyword.value && searchKeyword.value.trim()) {
    router.push({
      path: '/search',
      query: { key: searchKeyword.value.trim() }
    })
  }
}

const goToVideo = () => {
  router.push('/user/video')
}

 const goToAI = () => {
  router.push('/ai/home')
}

const toggleUserMenu = () => {
  router.push('/center')
}

const handleTagClick = (tag) => {
  router.push({
    path: '/search',
    query: { key: tag }
  })
}

const handleCategoryClick = (category) => {
  if (category) {
    router.push({
      path: '/search',
      query: { key: category }
    })
  }
}

const handleRefreshVideos = async () => {
  recommendedVideos.value = []
  hotVideos.value = []
  await fetchVideosData(false)
  ElMessage.success('刷新成功')
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eaf6 100%);
}

/* 顶部导航栏 */
.home-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  z-index: 1000;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 40px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 700;
  color: #667eea;
  cursor: pointer;
}

.logo-icon {
  font-size: 28px;
  color: #667eea;
}

.logo-text {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.header-nav {
  display: flex;
  gap: 32px;
}

.nav-item {
  color: #666;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  padding: 8px 0;
  position: relative;
  transition: all 0.3s ease;
}

.nav-item:hover {
  color: #667eea;
}

.nav-item.active {
  color: #667eea;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 2px;
}

.message-nav {
  display: flex;
  align-items: center;
  gap: 4px;
  position: relative;
}

.unread-badge {
  position: absolute;
  top: -8px;
  right: -12px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: linear-gradient(135deg, #f56c6c, #e64545);
  color: white;
  font-size: 11px;
  font-weight: 600;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(245, 108, 108, 0.4);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

.header-center {
  flex: 1;
  max-width: 600px;
  margin: 0 40px;
}

.search-box {
  width: 100%;
}

.search-input {
  border-radius: 24px;
  height: 40px;
}

.search-btn {
  border-radius: 0 24px 24px 0;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border: none;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.lang-switch {
  position: relative;
}

.lang-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.lang-btn:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(118, 75, 162, 0.2));
  border-color: rgba(102, 126, 234, 0.4);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.lang-text {
  font-size: 14px;
  font-weight: 600;
  color: #667eea;
}

.dropdown-arrow {
  font-size: 12px;
  color: #666;
  transition: transform 0.3s ease;
}

.lang-btn:hover .dropdown-arrow {
  transform: rotate(180deg);
  color: #667eea;
}

.lang-dropdown-menu {
  min-width: 150px;
}

.lang-dropdown-menu .el-dropdown-menu__item {
  padding: 12px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.lang-dropdown-menu .el-dropdown-menu__item.is-active {
  background: linear-gradient(90deg, #667eea, #764ba2);
  color: white;
}

.lang-dropdown-menu .el-dropdown-menu__item .el-icon-check {
  color: #667eea;
}

.lang-dropdown-menu .el-dropdown-menu__item.is-active .el-icon-check {
  color: white;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.learning-stats {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  border-radius: 20px;
  font-size: 14px;
  color: #667eea;
}

.stats-icon {
  font-size: 18px;
}

.stats-text {
  font-weight: 600;
}

.user-avatar {
  position: relative;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}

.user-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid rgba(102, 126, 234, 0.3);
  object-fit: cover;
}

.dropdown-icon {
  font-size: 12px;
  color: #666;
  transition: transform 0.3s ease;
}

.user-avatar:hover .dropdown-icon {
  transform: rotate(180deg);
}

/* 主要内容区域 */
.main-content {
  display: flex;
  padding-top: 84px;
  gap: 24px;
  max-width: 1920px;
  margin: 0 auto;
  padding-left: 16px;
  padding-right: 16px;
}

/* 左侧边栏 */
.sidebar {
  width: 320px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.ai-assistant-card,
.progress-card,
.stats-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.ai-icon,
.progress-icon,
.stats-icon,
.ranking-icon,
.plan-icon,
.tags-icon {
  font-size: 20px;
  color: #667eea;
}

.ai-chat-box {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chat-messages {
  flex: 1;
  min-height: 200px;
  max-height: 300px;
  overflow-y: auto;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.ai-message {
  align-items: flex-start;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.message-content {
  background: rgba(102, 126, 234, 0.1);
  padding: 12px 16px;
  border-radius: 12px;
  max-width: calc(100% - 48px);
}

.message-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: #666;
}

.chat-input-area {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.chat-input {
  flex: 1;
}

.chat-input :deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 2px solid rgba(102, 126, 234, 0.2);
  transition: all 0.3s ease;
  font-size: 14px;
  line-height: 1.6;
  resize: none;
}

.chat-input :deep(.el-textarea__inner):focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.chat-input :deep(.el-textarea__inner)::placeholder {
  color: #999;
}

.send-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 12px;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.send-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
  background: linear-gradient(135deg, #764ba2, #667eea);
}

/* 学习进度 */
.progress-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.progress-item {
  display: flex;
  gap: 12px;
  padding: 10px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(102, 126, 234, 0.03);
}

.progress-item:hover {
  background: rgba(102, 126, 234, 0.1);
  transform: translateX(4px);
}

.progress-thumbnail {
  position: relative;
  width: 80px;
  height: 45px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.progress-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.progress-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.progress-item:hover .progress-overlay {
  opacity: 1;
}

.progress-overlay .el-icon {
  font-size: 20px;
  color: white;
}

.progress-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.progress-name {
  font-size: 13px;
  color: #333;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.progress-percent {
  font-size: 12px;
  color: #667eea;
  font-weight: 600;
}

.empty-progress {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px 20px;
  color: #999;
  gap: 10px;
}

.empty-progress .empty-icon {
  font-size: 36px;
  color: #ddd;
}

.empty-progress span {
  font-size: 14px;
}

/* 学习统计 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.stat-item {
  text-align: center;
  padding: 12px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
  border-radius: 12px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #666;
}

/* 中间内容区 */
.content-area {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 分类导航 */
.category-nav {
  display: flex;
  gap: 16px;
  padding: 16px 0;
  overflow-x: auto;
  white-space: nowrap;
}

.category-item {
  padding: 10px 24px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 24px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.category-item:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border-color: rgba(102, 126, 234, 0.2);
}

.category-item.active {
  background: linear-gradient(90deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 视频区域 */
.video-section {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.section-title .el-icon {
  color: #f56c6c;
}

.refresh-btn {
  color: #667eea;
}

/* 视频网格 */
.video-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.video-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.video-card:hover {
  transform: translateY(-4px);
}

.video-thumbnail {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  aspect-ratio: 16/9;
  margin-bottom: 12px;
}

.video-thumbnail img {
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
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
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
  font-size: 48px;
  color: white;
}

.video-info {
  padding: 0 4px;
}

.video-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 12px 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.video-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: flex-start;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #999;
}

.meta-icon {
  font-size: 14px;
}

.system-text {
  color: #666;
  font-weight: 500;
}

.video-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  padding: 4px 12px;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

/* 热门视频网格 */
.hot-video-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  width: 100%;
}

.hot-courses-section {
  width: calc(100% + 120px);
  margin-left: -70px;
  margin-right: -50px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  padding: 20px 50px;
  border-radius: 16px;
}

.hot-video-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.hot-video-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.hot-thumbnail {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
  overflow: hidden;
  border-radius: 12px;
}

.hot-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.hot-video-card:hover .hot-thumbnail img {
  transform: scale(1.05);
}

.hot-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  backdrop-filter: blur(4px);
}

.hot-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.hot-video-card:hover .hot-overlay {
  opacity: 1;
}

.hot-play-icon {
  font-size: 48px;
  color: white;
  transform: scale(0.8);
  transition: transform 0.3s ease;
}

.hot-video-card:hover .hot-play-icon {
  transform: scale(1);
}

.hot-info {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.hot-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.3s ease;
}

.hot-video-card:hover .hot-title {
  color: #667eea;
}

.hot-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: auto;
}

.hot-meta-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.hot-meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #999;
}

/* 回到顶部按钮 */
.back-to-top {
  position: fixed;
  bottom: 60px;
  right: 60px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 100;
  border: 2px solid rgba(255, 255, 255, 0.2);
  opacity: 0;
  transform: translateY(20px);
  pointer-events: none;
}

.back-to-top.show {
  opacity: 1;
  transform: translateY(0);
  pointer-events: auto;
}

.back-to-top:hover {
  transform: translateY(-6px) scale(1.05);
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.5);
  background: linear-gradient(135deg, #764ba2, #667eea);
}

.back-icon {
  font-size: 28px;
  transition: transform 0.3s ease;
}

.back-to-top:hover .back-icon {
  transform: translateY(-2px);
}

/* 回到顶部按钮tooltip样式 */
:deep(.back-to-top-tooltip) {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  border: none !important;
  border-radius: 8px !important;
  padding: 8px 16px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3) !important;
}

:deep(.back-to-top-tooltip .el-popper__arrow::before) {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  border: none !important;
}

/* 加载更多 */
.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 12px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
  border-radius: 16px;
  margin: 20px 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.loading-more .is-loading {
  animation: rotate 1s linear infinite;
  color: #667eea;
  font-size: 24px;
}

.loading-more span {
  font-size: 16px;
  font-weight: 500;
  color: #667eea;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 右侧边栏 */
.video-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.video-list-item {
  display: flex;
  gap: 16px;
  padding: 12px;
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.video-list-item:hover {
  background: rgba(102, 126, 234, 0.05);
  transform: translateX(4px);
}

.list-thumbnail {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  width: 160px;
  aspect-ratio: 16/9;
}

.list-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.list-duration {
  position: absolute;
  bottom: 4px;
  right: 4px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.list-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.list-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
  line-height: 1.4;
}

.list-desc {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.list-meta {
  display: flex;
  gap: 16px;
}

.meta-text {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #999;
}

/* 右侧边栏 */
.right-sidebar {
  width: 320px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.ranking-card,
.plan-card,
.tags-card,
.calendar-card,
.quote-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.calendar-icon,
.quote-icon {
  font-size: 20px;
  color: #667eea;
}

.custom-calendar {
  border: none;
  background: transparent;
}

.custom-calendar :deep(.el-calendar__header) {
  display: none;
}

.custom-calendar :deep(.el-calendar-table) {
  thead th {
    font-size: 12px;
    color: #999;
    font-weight: 500;
  }
}

.custom-calendar :deep(.el-calendar-day) {
  padding: 0;
  height: 32px;
}

.calendar-cell {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  color: #333;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.calendar-cell:hover {
  background: rgba(102, 126, 234, 0.1);
}

.calendar-cell.is-today {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  font-weight: 600;
}

.quote-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quote-text {
  font-size: 15px;
  color: #333;
  line-height: 1.8;
  font-style: italic;
  margin: 0;
}

.quote-author {
  font-size: 13px;
  color: #999;
  text-align: right;
  margin: 0;
}

/* 排行榜 */
.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.ranking-item:hover {
  background: rgba(102, 126, 234, 0.05);
}

.rank-number {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  color: white;
  flex-shrink: 0;
}

.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ff8c00);
}

.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #808080);
}

.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #8b4513);
}

.rank-number:not(.rank-1):not(.rank-2):not(.rank-3) {
  background: #999;
}

.rank-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.rank-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.rank-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.rank-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.rank-score {
  font-size: 12px;
  color: #667eea;
  font-weight: 500;
}

/* 学习计划 */
.plan-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.plan-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 10px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.plan-item:hover {
  background: rgba(102, 126, 234, 0.05);
}

.plan-item.completed {
  background: rgba(103, 194, 58, 0.1);
}

.plan-item.in-progress {
  background: rgba(102, 126, 234, 0.1);
}

.check-icon {
  font-size: 20px;
  flex-shrink: 0;
  margin-top: 2px;
}

.plan-item.completed .check-icon {
  color: #67c23a;
}

.plan-item.in-progress .check-icon {
  color: #667eea;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.plan-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.plan-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.plan-time {
  font-size: 12px;
  color: #999;
}

/* 热门标签 */
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.cloud-tag {
  padding: 8px 16px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  color: #667eea;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.cloud-tag:hover {
  background: linear-gradient(90deg, #667eea, #764ba2);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 响应式 */
@media (max-width: 1400px) {
  .video-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 1100px) {
  .sidebar,
  .right-sidebar {
    width: 280px;
  }
  
  .video-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .home-header {
    padding: 0 16px;
  }
  
  .sidebar,
  .right-sidebar {
    display: none;
  }
  
  .main-content {
    padding-top: 80px;
  }
  
  .video-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .header-nav {
    display: none;
  }
}
</style>
