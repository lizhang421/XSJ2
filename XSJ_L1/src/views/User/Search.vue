<template>
  <div class="search-page">
    <header class="search-header" :style="{ backgroundImage: `url(${headerBgImage})` }">
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

    <div class="search-container">
      <div class="search-content">
        <div class="page-header">
          <div class="header-title">
            <div class="title-icon">🔍</div>
            <div class="title-text">
              <h1>{{ t('search.title') }}</h1>
              <p>{{ t('search.subtitle') }}: "{{ searchKeyword }}"</p>
            </div>
          </div>
          <div class="result-count-group">
            <div class="result-count" v-if="searchResults.length > 0">
              <span class="count-number">{{ searchResults.length }}</span>
              <span class="count-text">个视频</span>
            </div>
            <div class="result-count" v-if="searchUsers.length > 0">
              <span class="count-number">{{ searchUsers.length }}</span>
              <span class="count-text">位用户</span>
            </div>
          </div>
        </div>

        <div class="filter-section">
          <div class="filter-header">
            <div class="filter-title">
              <el-icon class="filter-title-icon"><el-icon-filter /></el-icon>
              <span>{{ t('search.filter') }}</span>
            </div>
            <div class="view-toggle-wrapper">
              <div class="view-toggle">
                <div 
                  class="view-btn" 
                  :class="{ active: viewMode === 'grid' }" 
                  @click="viewMode = 'grid'"
                >
                  <el-icon><el-icon-grid /></el-icon>
                  <span class="view-btn-text">{{ t('search.gridView') }}</span>
                </div>
                <div 
                  class="view-btn" 
                  :class="{ active: viewMode === 'list' }" 
                  @click="viewMode = 'list'"
                >
                  <el-icon><el-icon-list /></el-icon>
                  <span class="view-btn-text">{{ t('search.listView') }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="filter-content">
            <div class="filter-actions">
              <div class="search-box">
                <el-input
                  v-model="searchKeyword"
                  :placeholder="t('search.placeholder')"
                  class="search-input"
                  clearable
                  size="large"
                  @keyup.enter="handleSearch"
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
                  {{ t('search.newest') }}
                </div>
                <div 
                  class="sort-btn" 
                  :class="{ active: sortType === 'popular' }"
                  @click="sortType = 'popular'"
                >
                  <el-icon><el-icon-trend-charts /></el-icon>
                  {{ t('search.popular') }}
                </div>
                <div 
                  class="sort-btn" 
                  :class="{ active: sortType === 'views' }"
                  @click="sortType = 'views'"
                >
                  <el-icon><el-icon-view /></el-icon>
                  {{ t('search.mostViewed') }}
                </div>
              </div>
            </div>
            <div class="filter-tips">
              <el-icon class="tip-icon"><el-icon-info-filled /></el-icon>
              <span class="tip-text">{{ t('search.tips') }}</span>
            </div>
          </div>
        </div>

        <el-skeleton :loading="loading" animated>
          <template #template>
            <div v-for="i in 12" :key="i" class="search-card skeleton-card">
              <div class="card-thumbnail">
                <el-skeleton-item variant="image" style="width: 100%; height: 160px" />
              </div>
              <div class="card-content">
                <el-skeleton-item variant="text" style="width: 80%" />
                <el-skeleton-item variant="text" style="width: 60%" />
              </div>
            </div>
          </template>
          
          <div v-if="!loading && searchUsers.length > 0" class="user-results-section">
            <div class="section-header">
              <div class="section-title">
                <el-icon class="section-icon"><el-icon-user /></el-icon>
                <span>相关用户</span>
              </div>
              <span class="user-count">共 {{ searchUsers.length }} 位用户</span>
            </div>
            <div class="user-list">
              <div 
                class="user-card" 
                v-for="user in searchUsers" 
                :key="user.encode"
                @click="handleUserClick(user)"
              >
                <div class="user-avatar-wrapper-search">
                  <img :src="getAvatarUrl(user.avatar, user.nickName)" :alt="user.nickName" class="user-avatar-search" />
                </div>
                <div class="user-info">
                  <h4 class="user-nickname">{{ user.nickName }}</h4>
                  <div class="user-meta">
                    <span v-if="user.school" class="meta-tag">
                      <el-icon><el-icon-school /></el-icon>
                      {{ user.school }}
                    </span>
                    <span v-if="user.leavel" class="meta-tag">
                      <el-icon><el-icon-reading /></el-icon>
                      {{ user.leavel }}
                    </span>
                    <span v-if="user.country" class="meta-tag">
                      <el-icon><el-icon-location /></el-icon>
                      {{ user.country }}
                    </span>
                  </div>
                </div>
                <div class="user-action">
                  <el-button type="primary" size="small" round>
                    <el-icon><el-icon-view /></el-icon>
                    查看主页
                  </el-button>
                </div>
              </div>
            </div>
          </div>
          
          <div v-if="!loading" class="search-list" v-show="filteredResults.length > 0">
            <div v-if="viewMode === 'grid'" class="grid-view">
              <div 
                class="search-card" 
                v-for="item in filteredResults" 
                :key="item.id"
                @click="handleItemClick(item)"
              >
                <div class="card-thumbnail">
                  <img :src="item.thumbnail" :alt="item.title" />
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
                      <span class="meta-item views">{{ item.viewCount || 0 }} {{ t('search.views') }}</span>
                    </div>
                  </div>
                  <div class="card-footer">
                    <span class="card-time">
                      <el-icon><el-icon-clock /></el-icon>
                      {{ formatTime(item.createTime) }}
                    </span>
                    <div class="card-stats">
                      <span class="stat">
                        <el-icon><el-icon-star /></el-icon>
                        {{ item.likeCount || 0 }}
                      </span>
                      <span class="stat">
                        <el-icon><el-icon-collection /></el-icon>
                        {{ item.collectCount || 0 }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="list-view">
              <div 
                class="search-list-item" 
                v-for="item in filteredResults" 
                :key="item.id"
                @click="handleItemClick(item)"
              >
                <div class="list-thumbnail">
                  <img :src="item.thumbnail" :alt="item.title" />
                  <div class="list-duration" v-if="item.duration">{{ item.duration }}</div>
                </div>
                <div class="list-content">
                  <div class="list-main">
                    <h3 class="list-title">{{ item.title }}</h3>
                    <div class="list-meta-info">
                      <span class="publisher">{{ item.publisherName }}</span>
                      <span class="dot">·</span>
                      <span class="views">{{ item.viewCount || 0 }} {{ t('search.views') }}</span>
                      <span class="dot">·</span>
                      <span class="watch-time">
                        <el-icon><el-icon-clock /></el-icon>
                        {{ formatTime(item.createTime) }}
                      </span>
                    </div>
                  </div>
                  <div class="list-stats">
                    <div class="stat-item">
                      <el-icon><el-icon-star /></el-icon>
                      <span>{{ item.likeCount || 0 }}</span>
                    </div>
                    <div class="stat-item">
                      <el-icon><el-icon-collection /></el-icon>
                      <span>{{ item.collectCount || 0 }}</span>
                    </div>
                    <div class="stat-item">
                      <el-icon><el-icon-chat-dot-round /></el-icon>
                      <span>{{ item.commentCount || 0 }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-skeleton>

        <div v-if="showRecommended" class="recommended-section">
          <div class="recommended-header">
            <div class="recommended-title">
              <el-icon class="recommend-icon"><el-icon-star-filled /></el-icon>
              <h2>{{ t('search.recommendTitle') }}</h2>
            </div>
            <p class="recommended-desc">{{ t('search.recommendDesc') }}</p>
          </div>
          
          <el-skeleton :loading="loadingRecommended" animated>
            <template #template>
              <div class="grid-view">
                <div v-for="i in 8" :key="i" class="search-card skeleton-card">
                  <div class="card-thumbnail">
                    <el-skeleton-item variant="image" style="width: 100%; height: 160px" />
                  </div>
                  <div class="card-content">
                    <el-skeleton-item variant="text" style="width: 80%" />
                    <el-skeleton-item variant="text" style="width: 60%" />
                  </div>
                </div>
              </div>
            </template>
            
            <div v-if="!loadingRecommended" class="grid-view">
              <div 
                class="search-card" 
                v-for="item in filteredRecommendedVideos" 
                :key="'rec-' + item.id"
                @click="handleItemClick(item)"
              >
                <div class="card-thumbnail">
                  <img :src="item.thumbnail" :alt="item.title" />
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
                      <span class="meta-item views">{{ item.viewCount || 0 }} {{ t('search.views') }}</span>
                    </div>
                  </div>
                  <div class="card-footer">
                    <span class="card-time">
                      <el-icon><el-icon-clock /></el-icon>
                      {{ formatTime(item.createTime) }}
                    </span>
                    <div class="card-stats">
                      <span class="stat">
                        <el-icon><el-icon-star /></el-icon>
                        {{ item.likeCount || 0 }}
                      </span>
                      <span class="stat">
                        <el-icon><el-icon-collection /></el-icon>
                        {{ item.collectCount || 0 }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-skeleton>
        </div>

        <div class="empty-state" v-if="!loading && filteredResults.length === 0 && searchUsers.length === 0 && filteredRecommendedVideos.length === 0">
          <div class="empty-icon">🔍</div>
          <h3 class="empty-title">{{ t('search.emptyTitle') }}</h3>
          <p class="empty-desc">{{ t('search.emptyDesc') }}</p>
          <el-button type="primary" @click="goToFirst">
            <el-icon><el-icon-explore /></el-icon>
            返回首页
          </el-button>
        </div>

        <div v-if="!loading && filteredResults.length === 0 && filteredRecommendedVideos.length > 0" class="recommended-section no-result-section">
          <div class="no-result-banner">
            <div class="no-result-icon">🔍</div>
            <div class="no-result-content">
              <h2 class="no-result-title">未找到"{{ searchKeyword }}"相关视频</h2>
              <p class="no-result-desc">没有找到相关视频，为您推荐以下热门视频</p>
            </div>
          </div>
          
          <div class="grid-view">
            <div 
              class="search-card" 
              v-for="item in filteredRecommendedVideos" 
              :key="'rec-' + item.id"
              @click="handleItemClick(item)"
            >
              <div class="card-thumbnail">
                <img :src="item.thumbnail" :alt="item.title" />
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
                    <span class="meta-item views">{{ item.viewCount || 0 }} {{ t('search.views') }}</span>
                  </div>
                </div>
                <div class="card-footer">
                  <span class="card-time">
                    <el-icon><el-icon-clock /></el-icon>
                    {{ formatTime(item.createTime) }}
                  </span>
                  <div class="card-stats">
                    <span class="stat">
                      <el-icon><el-icon-star /></el-icon>
                      {{ item.likeCount || 0 }}
                    </span>
                    <span class="stat">
                      <el-icon><el-icon-collection /></el-icon>
                      {{ item.collectCount || 0 }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserInfoStore } from '@/stores/userinfo'
import { SearchVideo, GetVideos } from '@/api/Users'

const { t, locale } = useI18n()
const router = useRouter()
const route = useRoute()
const userInfoStore = useUserInfoStore()

const currentNav = ref('home')
const searchKeyword = ref('')
const sortType = ref('newest')
const viewMode = ref('grid')
const loading = ref(false)

const headerBgImage = ref('https://lizhanghao.oss-cn-beijing.aliyuncs.com/%E8%8F%9C%E5%8D%95%E6%A0%8F/487-1800x70.jpg')

const searchResults = ref([])
const searchUsers = ref([])
const recommendedVideos = ref([])
const loadingRecommended = ref(false)

const currentLangText = computed(() => {
  const langMap = { zh: '中文', en: 'English', ja: '日本語' }
  return langMap[locale.value] || '中文'
})

const filteredResults = computed(() => {
  let items = [...searchResults.value]

  if (sortType.value === 'newest') {
    items.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  } else if (sortType.value === 'popular') {
    items.sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
  } else if (sortType.value === 'views') {
    items.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
  }

  return items
})

const showRecommended = computed(() => {
  return !loading.value && searchResults.value.length > 0 && searchResults.value.length < 5 && searchUsers.value.length === 0
})

const filteredRecommendedVideos = computed(() => {
  let items = [...recommendedVideos.value]

  if (sortType.value === 'newest') {
    items.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  } else if (sortType.value === 'popular') {
    items.sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
  } else if (sortType.value === 'views') {
    items.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
  }

  return items.slice(0, 8)
})

const formatTime = (date) => {
  const now = new Date()
  const diff = now - new Date(date)
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (minutes < 60) return `${minutes}${t('search.minutesAgo')}`
  if (hours < 24) return `${hours}${t('search.hoursAgo')}`
  if (days < 7) return `${days}${t('search.daysAgo')}`
  return new Date(date).toLocaleDateString()
}

const getVideoDuration = (seconds) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
}

const getAvatarUrl = (avatar, name) => {
  if (avatar) {
    return avatar.replace(/[\r\n`]/g, '').trim()
  }
  const colors = ['667eea', '764ba2', 'f093fb', 'f5576c', '4facfe', '00f2fe']
  const colorIndex = (name || '').length % colors.length
  return `https://ui-avatars.com/api/?name=${encodeURIComponent(name || 'User')}&background=${colors[colorIndex]}&color=fff&size=200`
}

const handleUserClick = (user) => {
  if (user.encode) {
    router.push('/user/' + user.encode)
  }
}

const fetchRecommendedVideos = async () => {
  loadingRecommended.value = true
  try {
    const response = await GetVideos(12)
    if (response && response.code === 0 && response.data) {
      recommendedVideos.value = response.data.map((item, index) => ({
        id: index + 1,
        encode: item.enCode,
        title: item.videoName,
        thumbnail: item.videoImageUrl ? item.videoImageUrl.replace(/[\r\n`]/g, '') : '',
        duration: getVideoDuration(item.videoTime || 0),
        videoTime: item.videoTime || 0,
        videoUrl: item.videoUrl ? item.videoUrl.replace(/[\r\n`]/g, '') : '',
        publisherName: item.publisherName,
        viewCount: item.viewCount,
        likeCount: item.likeCount,
        commentCount: item.commentCount,
        shareCount: item.shareCount,
        collectCount: item.collectCount,
        createTime: item.createTime
      }))
    }
  } catch (error) {
    console.error('获取推荐视频失败:', error)
  } finally {
    loadingRecommended.value = false
  }
}

const fetchSearchResults = async () => {
  if (!searchKeyword.value) {
    searchResults.value = []
    searchUsers.value = []
    return
  }

  loading.value = true
  try {
    console.log('开始搜索:', searchKeyword.value)
    const response = await SearchVideo(searchKeyword.value)
    console.log('搜索结果:', response)
    if (response && response.code === 0 && response.data) {
      const data = response.data
      
      searchResults.value = (data.video || []).map((item, index) => ({
        id: index + 1,
        encode: item.enCode,
        title: item.videoName,
        thumbnail: item.videoImageUrl ? item.videoImageUrl.replace(/[\r\n`]/g, '') : '',
        duration: getVideoDuration(item.videoTime || 0),
        videoTime: item.videoTime || 0,
        videoUrl: item.videoUrl ? item.videoUrl.replace(/[\r\n`]/g, '') : '',
        publisherName: item.publisherName,
        viewCount: item.viewCount,
        likeCount: item.likeCount,
        commentCount: item.commentCount,
        shareCount: item.shareCount,
        collectCount: item.collectCount,
        createTime: item.createTime
      }))
      
      searchUsers.value = (data.user || []).map((item, index) => ({
        id: index + 1,
        encode: item.enCode,
        nickName: item.nickName,
        realName: item.realName,
        avatar: item.avatar ? item.avatar.replace(/[\r\n`]/g, '').trim() : '',
        school: item.school || '',
        leavel: item.leavel || '',
        country: item.country || '',
        gender: item.gender || 0,
        createTime: item.createTime
      }))
      
      console.log('处理后的搜索结果:', searchResults.value)
      console.log('处理后的用户结果:', searchUsers.value)
      
      if (searchResults.value.length < 5) {
        fetchRecommendedVideos()
      }
    } else {
      searchResults.value = []
      searchUsers.value = []
      fetchRecommendedVideos()
    }
  } catch (error) {
    console.error('搜索失败:', error)
    searchResults.value = []
    searchUsers.value = []
    fetchRecommendedVideos()
  } finally {
    loading.value = false
    console.log('搜索完成')
  }
}

const handleSearch = () => {
  if (searchKeyword.value) {
    router.push({
      path: '/search',
      query: { key: searchKeyword.value }
    })
    fetchSearchResults()
  }
}

const changeLocale = async (lang) => {
  locale.value = lang
  localStorage.setItem('locale', lang)
  
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

const handleItemClick = (video) => {
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
    ElMessage.warning(t('search.videoInfoIncomplete'))
  }
}

const goHome = () => router.push('/First')
const goToFirst = () => router.push('/First')
const goToAI = () => router.push('/ai/home')

watch(() => route.query.key, (newKey) => {
  if (newKey) {
    searchKeyword.value = newKey
    fetchSearchResults()
  }
}, { immediate: true })

onMounted(() => {
  if (route.query.key) {
    searchKeyword.value = route.query.key
    fetchSearchResults()
  }
})
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.search-header {
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
  white-space: nowrap;
  flex-shrink: 0;
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

.search-container {
  padding-top: 80px;
  min-height: calc(100vh - 80px);
}

.search-content {
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

.result-count {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.result-count-group {
  display: flex;
  gap: 24px;
}

.count-number {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.count-text {
  font-size: 16px;
  color: #888;
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

.search-list {
  margin-bottom: 24px;
}

.user-results-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.section-icon {
  font-size: 22px;
  color: #667eea;
}

.user-count {
  font-size: 14px;
  color: #888;
}

.user-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-card:hover {
  background: linear-gradient(135deg, #f0f4ff 0%, #e8f0fe 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.user-avatar-wrapper-search {
  flex-shrink: 0;
}

.user-avatar-search {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-nickname {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
}

.user-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.user-meta .meta-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #888;
  padding: 4px 8px;
  background: white;
  border-radius: 6px;
}

.user-action {
  flex-shrink: 0;
}

.grid-view {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.skeleton-card {
  opacity: 0.8;
}

.search-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.search-card:hover {
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

.search-card:hover .card-thumbnail img {
  transform: scale(1.1);
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

.search-card:hover .card-play-overlay {
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

.search-card:hover .play-btn {
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

.card-stats {
  display: flex;
  gap: 12px;
}

.card-stats .stat {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #888;
}

.list-view {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-list-item {
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

.search-list-item:hover {
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

.search-list-item:hover .list-thumbnail img {
  transform: scale(1.1);
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

.list-stats {
  display: flex;
  gap: 20px;
  align-items: center;
}

.list-stats .stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #888;
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

.recommended-section {
  margin-top: 32px;
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.no-result-section {
  margin-top: 0;
}

.no-result-banner {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 32px;
  margin-bottom: 32px;
  background: linear-gradient(135deg, #fff5f5 0%, #fff0f6 50%, #f0f4ff 100%);
  border-radius: 16px;
  border: 2px solid rgba(102, 126, 234, 0.2);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.15);
}

.no-result-icon {
  font-size: 64px;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

.no-result-content {
  flex: 1;
}

.no-result-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.no-result-desc {
  font-size: 16px;
  color: #666;
  margin: 0;
  line-height: 1.6;
}

.recommended-header {
  margin-bottom: 24px;
  text-align: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.recommended-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 8px;
}

.recommended-title h2 {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.recommend-icon {
  font-size: 26px;
  color: #feca57;
}

.recommended-desc {
  font-size: 14px;
  color: #888;
  margin: 0;
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
  
  .search-content {
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
  
  .search-list-item {
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
  
  .list-stats {
    border-top: 1px solid #f0f0f0;
    padding-top: 12px;
  }
}
</style>
