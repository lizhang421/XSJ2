<template>
  <div class="user-profile-page">
    <header class="profile-header" :style="{ backgroundImage: `url(${headerBgImage})` }">
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
          
          <div class="user-avatar-wrapper active" @click="goToCenter">
            <img :src="userInfoStore.info.avatar || 'https://picsum.photos/100/100?random=1'" :alt="userInfoStore.info.nickName || '用户头像'" class="user-avatar" />
          </div>
        </div>
      </div>
    </header>

    <div class="profile-container">
      <div class="profile-content">
        <div class="profile-banner">
          <div class="banner-background">
            <video 
              v-if="isVideoBackground(profileData.backImage)" 
              class="banner-video"
              :src="profileData.backImage" 
              autoplay 
              muted 
              loop 
              playsinline
            ></video>
            <div 
              v-else 
              class="banner-image" 
              :style="profileData.backImage ? { backgroundImage: `url(${profileData.backImage})` } : {}"
            ></div>
            <div class="banner-gradient"></div>
          </div>
          <div class="banner-content">
            <div class="user-avatar-section">
              <div class="avatar-wrapper">
                <img :src="getAvatarUrl(profileData.avatar, profileData.nickName)" :alt="profileData.nickName" class="profile-avatar" />
                <div class="member-badge" v-if="profileData.uiMember === '1'">
                  <span>VIP</span>
                </div>
              </div>
            </div>
            
            <div class="user-info-section">
              <div class="user-name-row">
                <h1 class="user-nickname">{{ profileData.nickName || t('profile.defaultName') }}</h1>
                <span class="member-tag" v-if="profileData.uiMember === '1'">👑</span>
              </div>
              
              <div class="user-meta">
                <span class="meta-item" v-if="profileData.school">
                  <el-icon><el-icon-school /></el-icon>
                  {{ profileData.school }}
                </span>
                <span class="meta-item" v-if="profileData.country">
                  <el-icon><el-icon-location /></el-icon>
                  {{ profileData.country }}
                </span>
              </div>
              
              <p class="user-signature" v-if="profileData.signature">{{ profileData.signature }}</p>
              <p class="user-signature empty" v-else>{{ t('profile.noSignature') }}</p>
            </div>
            
            <div class="action-buttons" v-if="!isCurrentUser">
              <template v-if="isFriend && isFollowing">
                <el-button class="message-btn" @click="handleSendMessage">
                  <el-icon><el-icon-chat-dot-round /></el-icon>
                  {{ t('profile.sendMessage') }}
                </el-button>
                <el-button 
                  type="default" 
                  class="follow-btn following"
                  @click="handleFollow"
                  :loading="followLoading"
                >
                  <el-icon><el-icon-check /></el-icon>
                  {{ t('profile.following') }}
                </el-button>
              </template>
              <template v-else-if="!isFriend && !isFollowing">
                <el-button 
                  type="primary" 
                  class="follow-btn"
                  @click="handleFollow"
                  :loading="followLoading"
                >
                  {{ t('profile.follow') }}
                </el-button>
                <el-button 
                  type="primary"
                  class="friend-btn"
                  @click="handleFriend"
                  :loading="friendLoading"
                >
                  <el-icon><el-icon-user /></el-icon>
                  {{ t('profile.addFriend') }}
                </el-button>
              </template>
              <template v-else>
                <el-button 
                  :type="isFollowing ? 'default' : 'primary'" 
                  :class="['follow-btn', { following: isFollowing }]"
                  @click="handleFollow"
                  :loading="followLoading"
                >
                  <el-icon v-if="isFollowing"><el-icon-check /></el-icon>
                  {{ isFollowing ? t('profile.following') : t('profile.follow') }}
                </el-button>
                <el-button 
                  :type="isFriend ? 'default' : 'primary'"
                  :class="['friend-btn', { 'is-friend': isFriend }]"
                  @click="handleFriend"
                  :loading="friendLoading"
                  :disabled="friendStatus === 3"
                >
                  <el-icon><el-icon-user /></el-icon>
                  {{ isFriend ? t('profile.deleteFriend') : friendStatus === 3 ? t('profile.pendingFriend') : t('profile.addFriend') }}
                </el-button>
                <el-button class="message-btn" @click="handleSendMessage">
                  <el-icon><el-icon-chat-dot-round /></el-icon>
                  {{ t('profile.sendMessage') }}
                </el-button>
              </template>
            </div>
          </div>
        </div>

        <div class="stats-bar">
          <div class="stat-item" @click="showFollowers">
            <span class="stat-number">{{ formatNumber(profileData.followersCount || 0) }}</span>
            <span class="stat-label">{{ t('profile.followers') }}</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item" @click="showFollowing">
            <span class="stat-number">{{ formatNumber(profileData.followingCount || 0) }}</span>
            <span class="stat-label">{{ t('profile.following') }}</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-number">{{ formatNumber(profileData.videosCount || 0) }}</span>
            <span class="stat-label">{{ t('profile.works') }}</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-number">{{ formatNumber(profileData.likesCount || 0) }}</span>
            <span class="stat-label">{{ t('profile.likes') }}</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-number">{{ formatNumber(profileData.viewsCount || 0) }}</span>
            <span class="stat-label">{{ t('profile.views') || '播放' }}</span>
          </div>
        </div>

        <div class="content-section">
          <div class="tabs-header">
            <div 
              class="tab-item" 
              :class="{ active: activeTab === 'works' }" 
              @click="activeTab = 'works'"
            >
              <el-icon><el-icon-video-camera /></el-icon>
              <span>{{ t('profile.worksTab') }}</span>
            </div>
            <div 
              class="tab-item" 
              :class="{ active: activeTab === 'collections' }" 
              @click="activeTab = 'collections'"
            >
              <el-icon><el-icon-folder-opened /></el-icon>
              <span>{{ t('profile.collectionsTab') }}</span>
            </div>
          </div>

          <div class="tab-content">
            <el-skeleton :loading="loadingVideos" animated>
              <template #template>
                <div class="video-grid">
                  <div v-for="i in 8" :key="i" class="video-card skeleton">
                    <el-skeleton-item variant="image" style="width: 100%; height: 160px" />
                    <div class="card-content">
                      <el-skeleton-item variant="text" style="width: 80%" />
                      <el-skeleton-item variant="text" style="width: 60%" />
                    </div>
                  </div>
                </div>
              </template>
              
              <div v-if="activeTab === 'works'" class="video-grid">
                <div 
                  class="video-card" 
                  v-for="video in worksList" 
                  :key="video.id"
                  @click="playVideo(video)"
                >
                  <div class="card-thumbnail">
                    <img :src="video.thumbnail" :alt="video.title" />
                    <div class="card-duration" v-if="video.duration">{{ video.duration }}</div>
                    <div class="card-play-overlay">
                      <div class="play-btn">
                        <el-icon><el-icon-video-play /></el-icon>
                      </div>
                    </div>
                  </div>
                  <div class="card-content">
                    <h3 class="card-title">{{ video.title }}</h3>
                    <div class="card-meta">
                      <span class="meta-item">
                        <el-icon><el-icon-view /></el-icon>
                        {{ formatNumber(video.views) }}
                      </span>
                      <span class="meta-item">
                        <el-icon><el-icon-star /></el-icon>
                        {{ formatNumber(video.likes) }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              <div v-if="activeTab === 'collections'" class="video-grid">
                <div 
                  class="video-card" 
                  v-for="video in collectionsList" 
                  :key="video.id"
                  @click="playVideo(video)"
                >
                  <div class="card-thumbnail">
                    <img :src="video.thumbnail" :alt="video.title" />
                    <div class="card-duration" v-if="video.duration">{{ video.duration }}</div>
                    <div class="card-play-overlay">
                      <div class="play-btn">
                        <el-icon><el-icon-video-play /></el-icon>
                      </div>
                    </div>
                  </div>
                  <div class="card-content">
                    <h3 class="card-title">{{ video.title }}</h3>
                    <div class="card-meta">
                      <span class="meta-item">
                        <el-icon><el-icon-view /></el-icon>
                        {{ formatNumber(video.views) }}
                      </span>
                      <span class="meta-item">
                        <el-icon><el-icon-star /></el-icon>
                        {{ formatNumber(video.likes) }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </el-skeleton>

            <div class="empty-state" v-if="!loadingVideos && currentList.length === 0">
              <div class="empty-icon">{{ activeTab === 'works' ? '📹' : '📁' }}</div>
              <h3 class="empty-title">{{ activeTab === 'works' ? t('profile.noWorks') : t('profile.noCollections') }}</h3>
              <p class="empty-desc">{{ activeTab === 'works' ? t('profile.noWorksDesc') : t('profile.noCollectionsDesc') }}</p>
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
import { GetOtherUserData, GetOtherUserHome, AddFriend, DeleteFriend, CancelFollow, GetFriendApplyList, GetUserInfoMessage } from '@/api/Users'

const { t, locale } = useI18n()
const router = useRouter()
const route = useRoute()
const userInfoStore = useUserInfoStore()

const currentNav = ref('home')
const activeTab = ref('works')
const loading = ref(false)
const loadingVideos = ref(false)
const followLoading = ref(false)
const friendLoading = ref(false)
const isFollowing = ref(false)
const isFriend = ref(false)
const friendStatus = ref(0)

const headerBgImage = ref('https://lizhanghao.oss-cn-beijing.aliyuncs.com/%E8%8F%9C%E5%8D%95%E6%A0%8F/487-1800x70.jpg')

const profileData = ref({
  encode: '',
  nickName: '',
  avatar: '',
  uiMember: '0',
  country: '',
  school: '',
  signature: '',
  age: '',
  email: '',
  gender: 0,
  leavel: '',
  backImage: '',
  followersCount: 0,
  followingCount: 0,
  videosCount: 0,
  likesCount: 0,
  viewsCount: 0
})

const worksList = ref([])
const collectionsList = ref([])

const currentLangText = computed(() => {
  const langMap = { zh: '中文', en: 'English', ja: '日本語' }
  return langMap[locale.value] || '中文'
})

const isCurrentUser = computed(() => {
  return profileData.value.encode === userInfoStore.info.enCode
})

const currentList = computed(() => {
  return activeTab.value === 'works' ? worksList.value : collectionsList.value
})

const isVideoBackground = (url) => {
  if (!url) return false
  const videoExtensions = ['.mp4', '.webm', '.ogg', '.mov', '.avi']
  const lowerUrl = url.toLowerCase()
  return videoExtensions.some(ext => lowerUrl.includes(ext))
}

const getAvatarUrl = (avatar, name) => {
  if (avatar) {
    return avatar.replace(/[\r\n`]/g, '').trim()
  }
  const colors = ['667eea', '764ba2', 'f093fb', 'f5576c', '4facfe', '00f2fe']
  const colorIndex = (name || '').length % colors.length
  return `https://ui-avatars.com/api/?name=${encodeURIComponent(name || 'User')}&background=${colors[colorIndex]}&color=fff&size=200`
}

const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

const getVideoDuration = (seconds) => {
  if (!seconds) return '0:00'
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
}

const fetchProfileData = async () => {
  const encode = route.params.encode
  if (!encode) {
    ElMessage.error(t('profile.userNotFound'))
    router.push('/First')
    return
  }

  loading.value = true
  try {
    const response = await GetOtherUserData(encode)
    console.log('GetOtherUserData response:', response)
    console.log('response.data:', response?.data)
    console.log('isfollow:', response?.data?.isfollow)
    console.log('isfiiend:', response?.data?.isfiiend)
    if (response && response.code === 0 && response.data) {
      const data = response.data
      const userMessage = data.userMessage || {}
      
      console.log('data.isfollow type:', typeof data.isfollow, 'value:', data.isfollow)
      console.log('data.isfiiend type:', typeof data.isfiiend, 'value:', data.isfiiend)
      
      profileData.value = {
        encode: userMessage.enCode || encode,
        nickName: userMessage.nickName || '',
        avatar: userMessage.avatar ? userMessage.avatar.replace(/[\r\n`]/g, '').trim() : '',
        uiMember: userMessage.uiMember || '0',
        country: userMessage.country || '',
        school: userMessage.school || '',
        signature: userMessage.signature || userMessage.introduction || '',
        age: userMessage.age || '',
        email: userMessage.email || '',
        gender: userMessage.gender || 0,
        leavel: userMessage.leavel || '',
        backImage: userMessage.backImage ? userMessage.backImage.replace(/[\r\n`]/g, '').trim() : '',
        followersCount: userMessage.followersCount || userMessage.fansCount || 0,
        followingCount: userMessage.followingCount || userMessage.followCount || 0,
        videosCount: userMessage.videosCount || userMessage.videoCount || 0,
        likesCount: userMessage.likesCount || userMessage.likeCount || 0,
        viewsCount: userMessage.viewsCount || userMessage.viewCount || 0
      }
      
      if (userMessage.backImage) {
        headerBgImage.value = userMessage.backImage.replace(/[\r\n`]/g, '').trim()
      }
      
      isFollowing.value = data.isfollow === true || data.isfollow === 'true' || data.isfollow === 1
      isFriend.value = data.isfiiend === true || data.isfiiend === 'true' || data.isfiiend === 1
      
      console.log('设置后 isFollowing.value:', isFollowing.value)
      console.log('设置后 isFriend.value:', isFriend.value)
      
      if (isFriend.value) {
        friendStatus.value = 1
      } else {
        await checkFriendStatus(encode)
      }
    } else {
      ElMessage.error(t('profile.loadFailed'))
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error(t('profile.loadError'))
  } finally {
    loading.value = false
  }
}

const checkFriendStatus = async (encode) => {
  try {
    const res = await GetFriendApplyList()
    if (res.code === 0 && res.data) {
      const receivedList = res.data.friendapplyUserVOList || []
      const sentList = res.data.userapplyfriedVOList || []
      
      const receivedItem = receivedList.find(item => item.enCode === encode)
      if (receivedItem) {
        friendStatus.value = Number(receivedItem.status)
        return
      }
      
      const sentItem = sentList.find(item => item.enCode === encode)
      if (sentItem) {
        friendStatus.value = Number(sentItem.status)
        return
      }
      
      friendStatus.value = 0
    }
  } catch (error) {
    console.error('获取好友状态失败:', error)
  }
}

const checkFollowStatus = async (encode) => {
  try {
    const res = await GetUserInfoMessage()
    if (res.code === 0 && res.data) {
      const followList = res.data.userFollowDTOList || []
      const isFollow = followList.some(item => item.enCode === encode)
      isFollowing.value = isFollow
    }
  } catch (error) {
    console.error('获取关注状态失败:', error)
  }
}

const fetchWorksList = async () => {
  const encode = route.params.encode
  if (!encode) return

  loadingVideos.value = true
  try {
    const response = await GetOtherUserHome(encode)
    if (response && response.code === 0) {
      const data = response.data || {}
      const userVideoPost = data.userVideoPost || []
      const userVideoLike = data.userVideoLike || []
      
      worksList.value = userVideoPost.map((item, index) => ({
        id: item.enCode || index,
        encode: item.enCode,
        title: item.videoName,
        thumbnail: item.videoImageUrl ? item.videoImageUrl.replace(/[\r\n`]/g, '').trim() : '',
        duration: getVideoDuration(item.videoTime || 0),
        views: item.viewCount || 0,
        likes: item.likeCount || 0,
        createTime: item.createTime
      }))
      collectionsList.value = userVideoLike.map((item, index) => ({
        id: item.enCode || index,
        encode: item.enCode,
        title: item.videoName,
        thumbnail: item.videoImageUrl ? item.videoImageUrl.replace(/[\r\n`]/g, '').trim() : '',
        duration: getVideoDuration(item.videoTime || 0),
        views: item.viewCount || 0,
        likes: item.likeCount || 0,
        createTime: item.createTime
      }))
      
      const totalViews = userVideoPost.reduce((sum, item) => sum + (item.viewCount || 0), 0)
      const totalLikes = userVideoPost.reduce((sum, item) => sum + (item.likeCount || 0), 0)
      
      profileData.value.videosCount = userVideoPost.length
      profileData.value.likesCount = totalLikes
      profileData.value.viewsCount = totalViews
    }
  } catch (error) {
    console.error('获取作品列表失败:', error)
  } finally {
    loadingVideos.value = false
  }
}

const fetchCollectionsList = async () => {
  const encode = route.params.encode
  if (!encode) return

  loadingVideos.value = true
  try {
    const response = await GetUserCollections(encode, 1, 20)
    if (response && response.code === 0) {
      collectionsList.value = (response.data || []).map((item, index) => ({
        id: item.enCode || index,
        encode: item.enCode,
        title: item.videoName,
        thumbnail: item.videoImageUrl ? item.videoImageUrl.replace(/[\r\n`]/g, '').trim() : '',
        duration: getVideoDuration(item.videoTime || 0),
        views: item.viewCount || 0,
        likes: item.likeCount || 0,
        createTime: item.createTime
      }))
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
  } finally {
    loadingVideos.value = false
  }
}

const handleFollow = async () => {
  if (!profileData.value.encode) return

  followLoading.value = true
  try {
    const newStatus = isFollowing.value ? 0 : 1
    const response = await CancelFollow(profileData.value.encode, newStatus)
    if (response && response.code === 0) {
      isFollowing.value = !isFollowing.value
      if (isFollowing.value) {
        profileData.value.followersCount++
        ElMessage.success(t('profile.followSuccess'))
      } else {
        profileData.value.followersCount--
        ElMessage.success(t('profile.unfollowSuccess'))
      }
    }
  } catch (error) {
    console.error('关注操作失败:', error)
    ElMessage.error(t('profile.followError'))
  } finally {
    followLoading.value = false
  }
}

const handleFriend = async () => {
  if (!profileData.value.encode) return

  friendLoading.value = true
  try {
    if (isFriend.value) {
      const response = await DeleteFriend(profileData.value.encode)
      if (response && response.code === 0) {
        isFriend.value = false
        friendStatus.value = 2
        ElMessage.success(t('profile.deleteFriendSuccess'))
      }
    } else if (friendStatus.value === 0 || friendStatus.value === 2) {
      const response = await AddFriend(profileData.value.encode)
      if (response && response.code === 0) {
        friendStatus.value = 3
        ElMessage.success(t('profile.friendRequestSent'))
      }
    }
  } catch (error) {
    console.error('好友操作失败:', error)
    ElMessage.error(t('profile.friendError'))
  } finally {
    friendLoading.value = false
  }
}

const handleSendMessage = () => {
  router.push({
    path: '/chat',
    query: { user: profileData.value.encode }
  })
}

const playVideo = (video) => {
  if (video.encode) {
    sessionStorage.setItem('currentVideo', JSON.stringify(video))
    router.push({
      path: '/Video',
      query: { encode: video.encode }
    })
  }
}

const showFollowers = () => {
  ElMessage.info(t('profile.followersList'))
}

const showFollowing = () => {
  ElMessage.info(t('profile.followingList'))
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

const goHome = () => router.push('/First')
const goToFirst = () => router.push('/First')
const goToAI = () => router.push('/ai/home')
const goToCenter = () => router.push('/center')

watch(() => route.params.encode, (newEncode) => {
  if (newEncode) {
    fetchProfileData()
    fetchWorksList()
  }
}, { immediate: true })

watch(activeTab, (newTab) => {
  if (newTab === 'works' && worksList.value.length === 0) {
    fetchWorksList()
  } else if (newTab === 'collections' && collectionsList.value.length === 0) {
    fetchCollectionsList()
  }
})

onMounted(() => {
  fetchProfileData()
  fetchWorksList()
})
</script>

<style scoped>
.user-profile-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.profile-header {
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

.profile-container {
  padding-top: 80px;
  min-height: calc(100vh - 80px);
}

.profile-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px;
}

.profile-banner {
  position: relative;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.banner-background {
  height: 350px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.banner-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.banner-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.05) 0%, rgba(0, 0, 0, 0.4) 100%);
}

.banner-content {
  display: flex;
  align-items: flex-end;
  gap: 24px;
  padding: 0 40px 30px;
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 2;
}

.user-avatar-section {
  flex-shrink: 0;
}

.avatar-wrapper {
  position: relative;
}

.profile-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid white;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.member-badge {
  position: absolute;
  bottom: 5px;
  right: 5px;
  background: linear-gradient(135deg, #ffd700, #ffb347);
  color: #8b4513;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 700;
  border: 2px solid white;
}

.user-info-section {
  flex: 1;
  padding-bottom: 10px;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.user-nickname {
  font-size: 28px;
  font-weight: 700;
  color: white;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.member-tag {
  font-size: 20px;
}

.user-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.user-meta .meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
}

.user-signature {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  margin: 0;
  max-width: 500px;
}

.user-signature.empty {
  font-style: italic;
  opacity: 0.7;
}

.action-buttons {
  display: flex;
  gap: 12px;
  padding-bottom: 10px;
}

.follow-btn {
  min-width: 100px;
  height: 40px;
  border-radius: 20px;
  font-weight: 600;
}

.follow-btn.following {
  background: #f0f0f0;
  border-color: #e0e0e0;
  color: #666;
}

.follow-btn.following:hover {
  background: #ff6b6b;
  border-color: #ff6b6b;
  color: white;
}

.friend-btn {
  min-width: 100px;
  height: 40px;
  border-radius: 20px;
  font-weight: 600;
}

.friend-btn.is-friend {
  background: #f0f0f0;
  border-color: #e0e0e0;
  color: #666;
}

.friend-btn.is-friend:hover {
  background: #ff6b6b;
  border-color: #ff6b6b;
  color: white;
}

.message-btn {
  min-width: 100px;
  height: 40px;
  border-radius: 20px;
  font-weight: 600;
  background: white;
  border: 2px solid #667eea;
  color: #667eea;
}

.message-btn:hover {
  background: #667eea;
  color: white;
}

.stats-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40px;
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.stat-item:hover {
  transform: scale(1.05);
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 14px;
  color: #888;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: #e0e0e0;
}

.content-section {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.tabs-header {
  display: flex;
  border-bottom: 1px solid #f0f0f0;
  padding: 0 20px;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 20px 30px;
  font-size: 16px;
  font-weight: 500;
  color: #888;
  cursor: pointer;
  position: relative;
  transition: all 0.3s ease;
}

.tab-item:hover {
  color: #667eea;
}

.tab-item.active {
  color: #667eea;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 3px 3px 0 0;
}

.tab-content {
  padding: 24px;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.video-card {
  background: #fafafa;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.video-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12);
}

.video-card.skeleton {
  opacity: 0.8;
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

.video-card:hover .card-thumbnail img {
  transform: scale(1.1);
}

.card-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 3px 8px;
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

.video-card:hover .card-play-overlay {
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

.video-card:hover .play-btn {
  transform: scale(1);
}

.card-content {
  padding: 14px;
}

.card-title {
  font-size: 14px;
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
  display: flex;
  gap: 12px;
}

.card-meta .meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #888;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-title {
  font-size: 18px;
  color: #333;
  margin: 0 0 8px;
}

.empty-desc {
  font-size: 14px;
  color: #888;
  margin: 0;
}

@media (max-width: 768px) {
  .header-inner {
    padding: 0 15px;
  }
  
  .header-center {
    display: none;
  }
  
  .profile-content {
    padding: 15px;
  }
  
  .banner-content {
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 0 20px 20px;
  }
  
  .user-info-section {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .user-meta {
    justify-content: center;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .stats-bar {
    gap: 20px;
    padding: 16px;
  }
  
  .stat-number {
    font-size: 18px;
  }
  
  .stat-label {
    font-size: 12px;
  }
  
  .video-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 12px;
  }
}
</style>
