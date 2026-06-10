<template>
  <div class="upload-page">
    <!-- 顶部导航栏 -->
    <header class="upload-header-nav">
      <div class="header-inner">
        <!-- 左侧Logo区域 -->
        <div class="header-left">
          <div class="navbar-brand" @click="goHome">
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
            <a class="nav-item" :class="{ active: currentNav === 'home' }" @click="goHome">
              <span class="nav-icon">🏠</span>
              <span class="nav-text">{{ t('nav.home') }}</span>
            </a>
            <a class="nav-item" :class="{ active: currentNav === 'ai' }" @click="goToAI">
              <span class="nav-icon">🤖</span>
              <span class="nav-text">AI</span>
            </a>
          </nav>
        </div>
        
        <!-- 右侧功能区 -->
        <div class="header-right">
          <div class="header-actions">
            <div class="action-item upload-btn active" @click="currentNav = 'upload'">
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
                <el-icon><Location /></el-icon>
                <span class="lang-text">{{ currentLangText }}</span>
                <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu class="lang-dropdown-menu">
                  <el-dropdown-item command="zh" :class="{ active: locale === 'zh' }">
                    <el-icon v-if="locale === 'zh'"><CircleCheck /></el-icon>
                    <span>🇨🇳 中文</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="en" :class="{ active: locale === 'en' }">
                    <el-icon v-if="locale === 'en'"><CircleCheck /></el-icon>
                    <span>🇺🇸 English</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="ja" :class="{ active: locale === 'ja' }">
                    <el-icon v-if="locale === 'ja'"><CircleCheck /></el-icon>
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

    <div class="upload-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="title-icon">🎬</span>
          {{ t('upload.title') }}
        </h1>
        <p class="page-subtitle">{{ t('upload.subtitle') }}</p>
      </div>
    </div>

    <div class="upload-container">
      <div class="upload-main">
          <div class="upload-steps">
          <div class="step" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
            <div class="step-number">1</div>
            <div class="step-text">{{ t('upload.step1') }}</div>
          </div>
          <div class="step-line" :class="{ active: currentStep > 1 }"></div>
          <div class="step" :class="{ active: currentStep >= 2, completed: currentStep > 2 }">
            <div class="step-number">2</div>
            <div class="step-text">{{ t('upload.step2') }}</div>
          </div>
          <div class="step-line" :class="{ active: currentStep > 2 }"></div>
          <div class="step" :class="{ active: currentStep >= 3 }">
            <div class="step-number">3</div>
            <div class="step-text">{{ t('upload.step3') }}</div>
          </div>
        </div>

        <div class="upload-content">
          <div v-if="currentStep === 1" class="step-content">
            <div class="upload-area video-upload">
              <div class="upload-title">
                <span class="upload-icon">📹</span>
                {{ t('upload.videoUpload') }}
              </div>
              <el-upload
                class="uploader"
                drag
                :auto-upload="false"
                :on-change="handleVideoChange"
                :show-file-list="false"
                accept="video/*"
              >
                <div v-if="!uploadForm.videoFile" class="upload-placeholder">
                  <div class="upload-icon-large">
                    <el-icon><Upload /></el-icon>
                  </div>
                  <p class="upload-text">{{ t('upload.dragVideo') }}</p>
                  <p class="upload-hint">{{ t('upload.supportFormats') }}</p>
                </div>
                <div v-else class="video-preview">
                  <video :src="videoPreviewUrl" controls class="preview-video"></video>
                  <div class="video-info">
                    <span class="video-name">{{ uploadForm.videoFile.name }}</span>
                    <span class="video-size">{{ formatFileSize(uploadForm.videoFile.size) }}</span>
                  </div>
                  <el-button type="danger" @click.stop="removeVideo" class="remove-btn">
                    <el-icon><Delete /></el-icon>
                    {{ t('upload.remove') }}
                  </el-button>
                </div>
              </el-upload>
            </div>

            <div class="upload-area cover-upload">
              <div class="upload-title">
                <span class="upload-icon">🖼️</span>
                {{ t('upload.coverUpload') }}
              </div>
              <el-upload
                class="uploader cover-uploader"
                :auto-upload="false"
                :on-change="handleCoverChange"
                :show-file-list="false"
                accept="image/*"
              >
                <div v-if="!uploadForm.coverFile" class="upload-placeholder cover-placeholder">
                  <div class="upload-icon-large">
                    <el-icon><Picture /></el-icon>
                  </div>
                  <p class="upload-text">{{ t('upload.uploadCover') }}</p>
                  <p class="upload-hint">{{ t('upload.coverTips') }}</p>
                </div>
                <div v-else class="cover-preview">
                  <img :src="coverPreviewUrl" class="preview-image" />
                  <el-button type="danger" @click.stop="removeCover" class="remove-btn">
                    <el-icon><Delete /></el-icon>
                    {{ t('upload.remove') }}
                  </el-button>
                </div>
              </el-upload>
            </div>

            <div class="step-actions">
              <el-button type="primary" size="large" @click="nextStep" :disabled="!uploadForm.videoFile">
                {{ t('upload.nextStep') }}
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>

          <div v-if="currentStep === 2" class="step-content">
            <div class="form-section">
              <div class="form-group">
                <label class="form-label">
                  <span class="label-icon">📝</span>
                  {{ t('upload.titleLabel') }}
                </label>
                <el-input
                  v-model="uploadForm.videoName"
                  :placeholder="t('upload.titlePlaceholder')"
                  maxlength="50"
                  show-word-limit
                  size="large"
                />
              </div>

              <div class="form-group">
                <label class="form-label">
                  <span class="label-icon">🎬</span>
                  视频类型
                </label>
                <el-select
                  v-model="uploadForm.videoTypeId"
                  placeholder="请选择视频类型"
                  size="large"
                  style="width: 100%"
                >
                  <el-option
                    v-for="type in videoTypes"
                    :key="type.videoTypeId"
                    :label="type.typeName"
                    :value="type.videoTypeId"
                  />
                </el-select>
              </div>

              <div class="form-group">
                <label class="form-label">
                  <span class="label-icon">📄</span>
                  {{ t('upload.description') }}
                </label>
                <el-input
                  v-model="uploadForm.videoDesc"
                  type="textarea"
                  :rows="4"
                  :placeholder="t('upload.descriptionPlaceholder')"
                  maxlength="500"
                  show-word-limit
                />
              </div>
            </div>

            <div class="step-actions">
              <el-button size="large" @click="prevStep">
                <el-icon><ArrowLeft /></el-icon>
                {{ t('upload.prevStep') }}
              </el-button>
              <el-button type="primary" size="large" @click="nextStep" :disabled="!uploadForm.videoName || !uploadForm.videoTypeId">
                {{ t('upload.nextStep') }}
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>

          <div v-if="currentStep === 3" class="step-content">
            <div class="preview-section">
              <div class="preview-title">
                <span class="preview-icon">✅</span>
                {{ t('upload.previewTitle') }}
              </div>
              
              <div class="preview-card">
                <div class="preview-cover">
                  <img v-if="uploadForm.coverFile" :src="coverPreviewUrl" class="cover-image" />
                  <div v-else class="cover-placeholder">
                    <el-icon><VideoCamera /></el-icon>
                  </div>
                </div>
                <div class="preview-info">
                  <h3 class="preview-video-title">{{ uploadForm.videoName || t('upload.untitled') }}</h3>
                  <div class="preview-meta">
                    <span class="meta-item">
                      <el-icon><Folder /></el-icon>
                      {{ getVideoTypeName(uploadForm.videoTypeId) }}
                    </span>
                  </div>
                  <p class="preview-desc">{{ uploadForm.videoDesc || t('upload.noDescription') }}</p>
                </div>
              </div>
            </div>

            <div class="step-actions">
              <el-button size="large" @click="prevStep">
                <el-icon><ArrowLeft /></el-icon>
                {{ t('upload.prevStep') }}
              </el-button>
              <el-button type="primary" size="large" @click="submitForm" :loading="uploading" :disabled="uploading">
                <el-icon><Check /></el-icon>
                {{ uploading ? `${t('upload.uploading')} ${uploadProgress}%` : t('upload.submit') }}
              </el-button>
            </div>
            
            <div v-if="uploading" class="upload-progress-bar">
              <el-progress :percentage="uploadProgress" :stroke-width="8" :show-text="false" />
              <p class="progress-text">{{ getProgressText() }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="upload-sidebar">
        <div class="sidebar-card">
          <div class="sidebar-title">
            <span class="sidebar-icon">💡</span>
            {{ t('upload.tips') }}
          </div>
          <ul class="tips-list">
            <li>{{ t('upload.tip1') }}</li>
            <li>{{ t('upload.tip2') }}</li>
            <li>{{ t('upload.tip3') }}</li>
            <li>{{ t('upload.tip4') }}</li>
          </ul>
        </div>

        <div class="sidebar-card">
          <div class="sidebar-title">
            <span class="sidebar-icon">📊</span>
            {{ t('upload.formatInfo') }}
          </div>
          <div class="format-list">
            <div class="format-item">
              <span class="format-label">{{ t('upload.videoFormat') }}</span>
              <span class="format-value">MP4, AVI, MOV, MKV</span>
            </div>
            <div class="format-item">
              <span class="format-label">{{ t('upload.coverFormat') }}</span>
              <span class="format-value">JPG, PNG, GIF</span>
            </div>
            <div class="format-item">
              <span class="format-label">{{ t('upload.maxSize') }}</span>
              <span class="format-value">2GB</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Upload, Delete, ArrowRight, ArrowLeft, Check, VideoCamera, Picture, Folder, Location, ArrowDown, CircleCheck } from '@element-plus/icons-vue'
import { ElMessage, ElDropdown, ElDropdownMenu, ElDropdownItem, ElProgress, ElRadioGroup, ElRadioButton, ElAlert } from 'element-plus'
import { useUserInfoStore } from '@/stores/userinfo'
import { uploadToOSS } from '@/utils/oss'
import { uploadVideoInfo, getVideoTypeList } from '@/api/Video'

const router = useRouter()
const { t, locale } = useI18n()
const userInfoStore = useUserInfoStore()

const currentStep = ref(1)
const uploading = ref(false)
const uploadProgress = ref(0)
const currentNav = ref('upload')
const videoTypes = ref([])

const uploadForm = reactive({
  videoFile: null,
  coverFile: null,
  videoUrl: '',
  coverUrl: '',
  videoName: '',
  videoDesc: '',
  videoTypeId: '',
  isCollection: 0,
  collectionId: '1'
})

const currentLangText = computed(() => {
  const langMap = {
    zh: '中文',
    en: 'English',
    ja: '日本語'
  }
  return langMap[locale.value] || '中文'
})

const changeLocale = (lang) => {
  locale.value = lang
  localStorage.setItem('locale', lang)
  ElMessage.success(t('loginRegister.languageUpdated') || '语言已切换')
}

const handleFavorite = () => {
  router.push('/center')
}

const goHome = () => {
  router.push('/First')
}

const goToAI = () => {
  router.push('/ai/home')
}

const handleMessage = () => {
  ElMessage.info(t('video.message') || '消息功能开发中...')
}

const handleHistory = () => {
  router.push('/history')
}

const getVideoTypeName = (typeId) => {
  const type = videoTypes.value.find(t => t.videoTypeId === typeId)
  return type ? type.typeName : '未选择'
}

const loadVideoTypes = async () => {
  try {
    const result = await getVideoTypeList()
    if (result.code === 0 && result.data) {
      videoTypes.value = result.data
    }
  } catch (error) {
    console.error('获取视频类型失败:', error)
  }
}

onMounted(() => {
  loadVideoTypes()
})

const getProgressText = () => {
  if (uploadProgress.value < 20) {
    return t('upload.uploading') || '正在上传视频到阿里云OSS...'
  } else if (uploadProgress.value < 50) {
    return t('upload.uploading') || '视频上传完成，正在上传封面...'
  } else if (uploadProgress.value < 80) {
    return t('upload.uploading') || '文件上传完成，正在提交信息...'
  } else {
    return t('upload.uploadSuccess') || '上传完成！'
  }
}

const videoPreviewUrl = computed(() => {
  return uploadForm.videoFile ? URL.createObjectURL(uploadForm.videoFile) : ''
})

const coverPreviewUrl = computed(() => {
  return uploadForm.coverFile ? URL.createObjectURL(uploadForm.coverFile) : ''
})

const categories = computed(() => [
  { value: 'education', label: t('upload.categoryEducation') },
  { value: 'technology', label: t('upload.categoryTechnology') },
  { value: 'art', label: t('upload.categoryArt') },
  { value: 'music', label: t('upload.categoryMusic') },
  { value: 'sports', label: t('upload.categorySports') },
  { value: 'other', label: t('upload.categoryOther') }
])

const getCategoryLabel = (value) => {
  const category = categories.value.find(c => c.value === value)
  return category ? category.label : value
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const handleVideoChange = (file) => {
  uploadForm.videoFile = file.raw
}

const handleCoverChange = (file) => {
  uploadForm.coverFile = file.raw
}

const removeVideo = () => {
  uploadForm.videoFile = null
}

const removeCover = () => {
  uploadForm.coverFile = null
}

const nextStep = () => {
  if (currentStep.value < 3) {
    currentStep.value++
  }
}

const prevStep = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

const submitForm = async () => {
  if (!uploadForm.videoFile) {
    ElMessage.error(t('upload.videoRequired'))
    return
  }

  uploading.value = true
  uploadProgress.value = 0
  
  try {
    ElMessage({ message: t('upload.uploading'), type: 'info' })
    
    uploadProgress.value = 10
    
    const videoResult = await uploadToOSS(uploadForm.videoFile, 'video', (progress) => {
      uploadProgress.value = 10 + Math.round(progress * 0.4)
    })
    
    if (!videoResult.success) {
      throw new Error(videoResult.error || '视频上传失败')
    }
    
    uploadForm.videoUrl = videoResult.url
    uploadProgress.value = 50
    
    let coverUrl = ''
    if (uploadForm.coverFile) {
      const coverResult = await uploadToOSS(uploadForm.coverFile, 'image', (progress) => {
        uploadProgress.value = 50 + Math.round(progress * 0.3)
      })
      
      if (!coverResult.success) {
        throw new Error(coverResult.error || '封面上传失败')
      }
      
      coverUrl = coverResult.url
    }
    
    uploadProgress.value = 80
    
    const videoData = {
      videoName: uploadForm.videoName,
      videoDesc: uploadForm.videoDesc,
      videoUrl: uploadForm.videoUrl,
      videoImageUrl: coverUrl,
      videoTypeId: uploadForm.videoTypeId,
      isCollection: uploadForm.isCollection,
      collectionId: uploadForm.collectionId
    }
    
    const result = await uploadVideoInfo(videoData)
    
    uploadProgress.value = 100
    
    if (result.code === 0) {
      ElMessage.success('视频上传成功！正在审核中，请耐心等待审核结果')
      setTimeout(() => {
        uploading.value = false
        uploadProgress.value = 0
        router.push('/center')
      }, 2000)
    } else {
      throw new Error(result.msg || '提交失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error(error.message || t('upload.uploadFailed') || '上传失败，请重试')
    uploading.value = false
    uploadProgress.value = 0
  }
}
</script>

<style scoped>
.upload-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding-bottom: 40px;
}

.upload-header-nav {
  background-color: white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 80px;
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
}

.navbar-brand:hover {
  transform: translateY(-2px);
}

.brand-logo {
  font-size: 32px;
  font-weight: 800;
  color: #D4A76A;
  margin: 0;
}

.brand-name {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-name h2 {
  font-size: 16px;
  color: #333;
  margin: 0;
  font-weight: 600;
  white-space: nowrap;
}

.brand-name p {
  font-size: 11px;
  color: #999;
  margin: 0;
  white-space: nowrap;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-menu {
  display: flex;
  gap: 30px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  text-decoration: none;
  color: #666;
  font-size: 15px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.nav-item:hover {
  background-color: #F5F2EB;
  color: #D4A76A;
}

.nav-item.active {
  background-color: #F5F2EB;
  color: #D4A76A;
  font-weight: 600;
}

.nav-icon {
  font-size: 18px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: #666;
}

.action-item:hover {
  background-color: #F5F2EB;
  color: #D4A76A;
}

.action-item.active {
  background-color: #D4A76A;
  color: white;
}

.action-icon {
  font-size: 16px;
}

.lang-switch {
  position: relative;
}

.lang-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: #666;
  background: white;
}

.lang-btn:hover {
  border-color: #D4A76A;
  color: #D4A76A;
}

.lang-text {
  font-size: 14px;
}

.dropdown-arrow {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.lang-dropdown-menu {
  min-width: 140px;
}

.lang-dropdown-menu .el-dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
}

.lang-dropdown-menu .el-dropdown-item.active {
  background-color: #F5F2EB;
  color: #D4A76A;
  font-weight: 600;
}

.user-avatar-wrapper {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.user-avatar-wrapper:hover {
  border-color: #D4A76A;
  transform: scale(1.05);
}

.user-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-header {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  padding: 40px 0;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  color: white;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}

.title-icon {
  font-size: 40px;
}

.page-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

.upload-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
  display: flex;
  gap: 30px;
}

.upload-main {
  flex: 1;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.upload-steps {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  border-bottom: 1px solid #eee;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #ddd;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s ease;
}

.step.active .step-number {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.step.completed .step-number {
  background: #52c41a;
  color: white;
}

.step-text {
  font-size: 14px;
  color: #999;
  font-weight: 500;
}

.step.active .step-text {
  color: #667eea;
}

.step-line {
  width: 80px;
  height: 3px;
  background: #ddd;
  margin: 0 15px;
  margin-bottom: 28px;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.step-line.active {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.upload-content {
  padding: 30px;
}

.step-content {
  animation: fadeIn 0.3s ease;
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

.upload-area {
  margin-bottom: 30px;
}

.upload-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.upload-icon {
  font-size: 20px;
}

.uploader {
  width: 100%;
}

:deep(.el-upload-dragger) {
  border: 2px dashed #d9d9d9;
  border-radius: 12px;
  background: #fafafa;
  transition: all 0.3s ease;
}

:deep(.el-upload-dragger:hover) {
  border-color: #667eea;
  background: #f0f2ff;
}

.upload-placeholder {
  padding: 60px 20px;
  text-align: center;
}

.upload-icon-large {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-icon-large .el-icon {
  font-size: 36px;
  color: white;
}

.upload-text {
  font-size: 18px;
  color: #333;
  margin: 0 0 10px 0;
  font-weight: 500;
}

.upload-hint {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.video-preview {
  padding: 20px;
  text-align: center;
}

.preview-video {
  max-width: 100%;
  max-height: 300px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.video-info {
  margin-top: 15px;
  display: flex;
  justify-content: center;
  gap: 20px;
}

.video-name {
  font-weight: 500;
  color: #333;
}

.video-size {
  color: #999;
}

.cover-uploader :deep(.el-upload) {
  width: 100%;
}

.cover-placeholder {
  padding: 40px 20px;
}

.cover-preview {
  padding: 20px;
  text-align: center;
  position: relative;
}

.preview-image {
  max-width: 300px;
  max-height: 200px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.remove-btn {
  margin-top: 15px;
}

.form-section {
  max-width: 700px;
  margin: 0 auto;
}

.form-group {
  margin-bottom: 24px;
}

.form-row {
  display: flex;
  gap: 20px;
}

.form-row .form-group {
  flex: 1;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.label-icon {
  font-size: 16px;
}

.switch-group {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  background: #f5f7fa;
  border-radius: 12px;
}

.switch-group .form-label {
  margin-bottom: 0;
}

.step-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid #eee;
}

.step-actions .el-button {
  min-width: 140px;
}

.upload-progress-bar {
  margin-top: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 12px;
}

.progress-text {
  text-align: center;
  margin-top: 10px;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.preview-section {
  max-width: 800px;
  margin: 0 auto;
}

.preview-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 25px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.preview-icon {
  font-size: 24px;
}

.preview-card {
  display: flex;
  gap: 30px;
  padding: 25px;
  background: #f5f7fa;
  border-radius: 16px;
}

.preview-cover {
  flex-shrink: 0;
}

.cover-image {
  width: 240px;
  height: 160px;
  object-fit: cover;
  border-radius: 12px;
}

.cover-placeholder {
  width: 240px;
  height: 160px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-placeholder .el-icon {
  font-size: 48px;
  color: white;
}

.preview-info {
  flex: 1;
}

.preview-video-title {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin: 0 0 15px 0;
}

.preview-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
}

.preview-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 15px 0;
}

.preview-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
}

.tag {
  padding: 4px 12px;
  background: white;
  border-radius: 20px;
  font-size: 12px;
  color: #667eea;
  border: 1px solid #667eea;
}

.vip-badge {
  display: inline-block;
  padding: 6px 16px;
  background: linear-gradient(135deg, #ffd700 0%, #ffaa00 100%);
  color: #333;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.upload-sidebar {
  width: 300px;
  flex-shrink: 0;
}

.sidebar-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.sidebar-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.sidebar-icon {
  font-size: 20px;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li {
  padding: 10px 0;
  padding-left: 20px;
  position: relative;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  border-bottom: 1px solid #f0f0f0;
}

.tips-list li:last-child {
  border-bottom: none;
}

.tips-list li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #667eea;
  font-weight: bold;
}

.format-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.format-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.format-label {
  font-size: 14px;
  color: #666;
}

.format-value {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

@media (max-width: 1200px) {
  .upload-container {
    flex-direction: column;
  }

  .upload-sidebar {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 28px;
  }

  .upload-steps {
    padding: 20px;
  }

  .step-line {
    width: 40px;
  }

  .upload-content {
    padding: 20px;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .preview-card {
    flex-direction: column;
  }

  .cover-image,
  .cover-placeholder {
    width: 100%;
    height: 200px;
  }
}
</style>
