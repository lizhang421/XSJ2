<template>
  <div class="video-audit-page">
    <!-- 左侧视频列表 -->
    <div class="left-panel">
      <div class="panel-header">
        <h3>{{ currentFilterTitle }} ({{ currentFilterCount }})</h3>
        <el-select v-model="filterStatus" placeholder="筛选状态" size="small" @change="filterVideos">
          <el-option :label="'全部 (' + pendingVideos.length + ')'" value="all"></el-option>
          <el-option :label="'待处理 (' + pendingVideoCount + ')'" :value="0"></el-option>
          <el-option :label="'已处理 (' + processedVideoCount + ')'" :value="2"></el-option>
        </el-select>
      </div>
      
      <div class="video-list" ref="videoListRef">
        <div
          v-for="video in filteredVideos"
          :key="video.videoPendingId"
          :ref="el => { if (video.videoPendingId === currentVideo?.videoPendingId) currentVideoRef = el }"
          class="video-item"
          :class="{ active: currentVideo?.videoPendingId === video.videoPendingId }"
          @click="selectVideo(video)"
        >
          <img :src="video.videoImageUrl" :alt="video.videoName" class="video-thumb" />
          <div class="video-info">
            <h4 class="video-title">{{ video.videoName }}</h4>
            <p class="video-meta">
              <span>{{ video.typeName || '未分类' }}</span>
            </p>
            <el-tag :type="getVideoStatusType(video.reviewStatus, video.videoStatus)" size="small">
              {{ getVideoStatusText(video.reviewStatus, video.videoStatus) }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 中间视频播放区域 -->
    <div class="video-section">
      <div class="video-header">
        <h2 v-if="currentVideo">{{ currentVideo.videoName }}</h2>
        <div class="video-stats" v-if="currentVideo">
          <span><el-icon><VideoCamera /></el-icon> {{ currentVideo.videoFormat }}</span>
          <span><el-icon><Clock /></el-icon> {{ formatDate(currentVideo.publishTime) }}</span>
        </div>
      </div>
      
      <div class="video-container">
        <video
          v-if="currentVideo"
          ref="videoPlayer"
          :src="currentVideo.videoUrl"
          controls
          autoplay
          class="video-player"
          @loadedmetadata="onVideoLoaded"
        ></video>
        <div v-else class="no-video">
          <el-icon><VideoCamera /></el-icon>
          <p>暂无待审核视频</p>
        </div>
      </div>
      
      <div class="video-controls" v-if="currentVideo">
        <div class="control-item" v-if="currentVideo.reviewStatus === 0">
          <el-button
            type="success"
            size="large"
            @click="approveVideo"
            :loading="approving"
            class="control-btn"
          >
            <el-icon><Check /></el-icon>
            审核通过
          </el-button>
        </div>
        <div class="control-item" v-if="currentVideo.reviewStatus === 0">
          <el-button
            type="danger"
            size="large"
            @click="showRejectDialog"
            :loading="rejecting"
            class="control-btn"
          >
            <el-icon><Close /></el-icon>
            审核不通过
          </el-button>
        </div>
        <div class="control-item" v-if="currentVideo.reviewStatus === 2" style="flex: 1; max-width: 100%;">
          <el-alert
            :title="`该视频已处理 - ${currentVideo.videoStatus === 1 ? '已通过' : '未通过'}`"
            type="info"
            :closable="false"
            show-icon
          />
        </div>
      </div>
    </div>
    
    <!-- 右侧审核信息 -->
    <div class="right-panel">
      <div v-if="currentVideo" class="audit-info">
        <div class="info-section">
          <h3>视频详情</h3>
          <div class="info-item">
            <label>视频标题</label>
            <p class="info-value">{{ currentVideo.videoName }}</p>
          </div>
          <div class="info-item">
            <label>视频描述</label>
            <p class="info-value desc">{{ currentVideo.videoDesc || '暂无描述' }}</p>
          </div>
          <div class="info-item">
            <label>视频分类</label>
            <p class="info-value">{{ currentVideo.typeName || '未分类' }}</p>
          </div>
          <div class="info-item">
            <label>审核状态</label>
            <el-tag :type="getVideoStatusType(currentVideo.reviewStatus, currentVideo.videoStatus)" size="large">
              {{ getVideoStatusText(currentVideo.reviewStatus, currentVideo.videoStatus) }}
            </el-tag>
          </div>
          <div class="info-item">
            <label>发布时间</label>
            <p class="info-value">{{ formatDate(currentVideo.publishTime) }}</p>
          </div>
          <div class="info-item">
            <label>视频格式</label>
            <p class="info-value">{{ currentVideo.videoFormat }}</p>
          </div>
          <div class="info-item">
            <label>发布者ID</label>
            <p class="info-value">{{ currentVideo.publishCode }}</p>
          </div>
        </div>
        
        <div class="audit-section" v-if="currentVideo.reviewStatus === 0">
          <h3>快速标签</h3>
          <p class="section-tip">点击标签可快速标记不通过原因</p>
          <div class="tag-list">
            <el-tag
              v-for="tag in auditTags"
              :key="tag.auditTagId"
              :type="selectedTags.includes(tag.auditTagId) ? 'danger' : ''"
              @click="toggleTag(tag.auditTagId)"
              class="tag-item"
              size="large"
            >
              {{ tag.tagName }}
            </el-tag>
          </div>
        </div>
        
        <div class="quick-actions" v-if="currentVideo.reviewStatus === 0">
          <h3>快捷操作</h3>
          <div class="action-buttons">
            <el-button 
              v-if="selectedTags.length > 0"
              type="danger" 
              @click="quickReject"
              :loading="rejecting"
              size="large"
              class="action-btn"
            >
              <el-icon><Close /></el-icon>
              使用选中标签拒绝 ({{ selectedTags.length }})
            </el-button>
            <el-button 
              type="warning" 
              @click="selectedTags = []"
              size="large"
              class="action-btn"
            >
              <el-icon><RefreshLeft /></el-icon>
              清空选中标签
            </el-button>
          </div>
        </div>
        
        <div class="processed-info" v-if="currentVideo.reviewStatus === 2">
          <h3>审核信息</h3>
          <div class="info-item">
            <label>审核结果</label>
            <el-tag :type="currentVideo.videoStatus === 1 ? 'success' : 'danger'" size="large">
              {{ currentVideo.videoStatus === 1 ? '审核通过' : '审核未通过' }}
            </el-tag>
          </div>
          <div class="info-item" v-if="currentVideo.auditTime">
            <label>审核时间</label>
            <p class="info-value">{{ formatDate(currentVideo.auditTime) }}</p>
          </div>
          <div class="info-item" v-if="currentVideo.reviewerCode">
            <label>审核员</label>
            <p class="info-value">{{ currentVideo.reviewerCode }}</p>
          </div>
        </div>
      </div>
      
      <div v-else class="no-selection">
        <el-icon><Warning /></el-icon>
        <p>请选择一个视频进行审核</p>
      </div>
    </div>
    
    <!-- 不通过原因对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="审核不通过"
      width="500px"
    >
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="选择标签">
          <el-checkbox-group v-model="rejectForm.tags">
            <el-checkbox
              v-for="tag in auditTags"
              :key="tag.auditTagId"
              :label="tag.auditTagId"
            >
              {{ tag.tagName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="rejectVideo" :loading="rejecting">
          确定不通过
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { VideoCamera, Check, Close, Warning, CircleCheck, Clock, RefreshLeft } from '@element-plus/icons-vue'
import { GetAuditVideoList, GetAuditVideoResult, AuditVideo } from '@/admin/api/admin'

const pendingVideos = ref([])
const auditTags = ref([])
const currentVideo = ref(null)
const filterStatus = ref('all')
const selectedTags = ref([])
const videoListRef = ref(null)
const currentVideoRef = ref(null)
const videoPlayer = ref(null)

const approving = ref(false)
const rejecting = ref(false)
const rejectDialogVisible = ref(false)
const rejectForm = ref({
  tags: []
})

const filteredVideos = computed(() => {
  if (filterStatus.value === 'all') {
    return pendingVideos.value
  }
  return pendingVideos.value.filter(v => v.reviewStatus === filterStatus.value)
})

const pendingVideoCount = computed(() => {
  return pendingVideos.value.filter(v => v.reviewStatus === 0).length
})

const processedVideoCount = computed(() => {
  return pendingVideos.value.filter(v => v.reviewStatus === 2).length
})

const currentFilterTitle = computed(() => {
  const titleMap = {
    'all': '全部视频',
    0: '待处理视频',
    2: '已处理视频'
  }
  return titleMap[filterStatus.value] || '待处理视频'
})

const currentFilterCount = computed(() => {
  if (filterStatus.value === 'all') {
    return pendingVideos.value.length
  }
  return filteredVideos.value.length
})

const loadPendingVideos = async () => {
  try {
    const result = await GetAuditVideoList()
    if (result.code === 0) {
      pendingVideos.value = result.data || []
      if (pendingVideos.value.length > 0 && !currentVideo.value) {
        currentVideo.value = pendingVideos.value[0]
      }
    }
  } catch (error) {
    console.error('加载待审核视频失败:', error)
    ElMessage.error('加载待审核视频失败')
  }
}

const loadAuditTags = async () => {
  try {
    const result = await GetAuditVideoResult()
    if (result.code === 0) {
      auditTags.value = result.data || []
    }
  } catch (error) {
    console.error('加载审核标签失败:', error)
  }
}

const selectVideo = (video) => {
  currentVideo.value = video
  selectedTags.value = []
  
  // 自动播放视频
  nextTick(() => {
    if (videoPlayer.value) {
      videoPlayer.value.play().catch(err => {
        console.log('自动播放失败，需要用户交互:', err)
      })
    }
  })
  
  // 滚动到当前选中的视频
  nextTick(() => {
    if (currentVideoRef.value) {
      currentVideoRef.value.scrollIntoView({
        behavior: 'smooth',
        block: 'center'
      })
    }
  })
}

const filterVideos = () => {
  // 筛选逻辑已在computed中实现
}

const toggleTag = (tagId) => {
  const index = selectedTags.value.indexOf(tagId)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tagId)
  }
}

const approveVideo = async () => {
  if (!currentVideo.value) {
    console.error('没有当前视频')
    return
  }
  
  console.log('开始审核通过流程')
  console.log('当前视频:', currentVideo.value)
  console.log('videoPendingId:', currentVideo.value.videoPendingId)
  console.log('reviewStatus:', 1)
  console.log('videoAuditTagId:', [])
  
  approving.value = true
  try {
    console.log('调用AuditVideo API...')
    const result = await AuditVideo(
      currentVideo.value.videoPendingId,
      1,
      []
    )
    
    console.log('AuditVideo返回结果:', result)
    
    if (result.code === 0) {
      ElMessage.success('审核通过')
      
      await loadPendingVideos()
      
      if (pendingVideos.value.length > 0) {
        currentVideo.value = pendingVideos.value[0]
        nextTick(() => {
          if (videoPlayer.value) {
            videoPlayer.value.play().catch(err => {
              console.log('自动播放失败:', err)
            })
          }
        })
      } else {
        currentVideo.value = null
      }
    } else {
      ElMessage.error(result.message || '审核失败')
    }
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error(error.response?.data?.message || '审核失败')
  } finally {
    approving.value = false
  }
}

const showRejectDialog = () => {
  rejectForm.value = {
    tags: [...selectedTags.value]
  }
  rejectDialogVisible.value = true
}

const quickReject = async () => {
  if (!currentVideo.value) return
  
  if (selectedTags.value.length === 0) {
    ElMessage.warning('请至少选择一个审核标签')
    return
  }
  
  rejecting.value = true
  try {
    const result = await AuditVideo(
      currentVideo.value.videoPendingId,
      0,
      selectedTags.value
    )
    
    if (result.code === 0) {
      ElMessage.success('已标记为不通过')
      
      await loadPendingVideos()
      
      if (pendingVideos.value.length > 0) {
        currentVideo.value = pendingVideos.value[0]
        selectedTags.value = []
        nextTick(() => {
          if (videoPlayer.value) {
            videoPlayer.value.play().catch(err => {
              console.log('自动播放失败:', err)
            })
          }
        })
      } else {
        currentVideo.value = null
      }
    } else {
      ElMessage.error(result.message || '审核失败')
    }
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error(error.response?.data?.message || '审核失败')
  } finally {
    rejecting.value = false
  }
}

const rejectVideo = async () => {
  if (!currentVideo.value) return
  
  if (rejectForm.value.tags.length === 0) {
    ElMessage.warning('请至少选择一个审核标签')
    return
  }
  
  rejecting.value = true
  try {
    const result = await AuditVideo(
      currentVideo.value.videoPendingId,
      0,
      rejectForm.value.tags
    )
    
    if (result.code === 0) {
      ElMessage.success('已标记为不通过')
      
      await loadPendingVideos()
      
      if (pendingVideos.value.length > 0) {
        currentVideo.value = pendingVideos.value[0]
        nextTick(() => {
          if (videoPlayer.value) {
            videoPlayer.value.play().catch(err => {
              console.log('自动播放失败:', err)
            })
          }
        })
      } else {
        currentVideo.value = null
      }
      
      rejectDialogVisible.value = false
    } else {
      ElMessage.error(result.message || '审核失败')
    }
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error(error.response?.data?.message || '审核失败')
  } finally {
    rejecting.value = false
  }
}

const getVideoStatusType = (reviewStatus, videoStatus) => {
  if (reviewStatus === 0) {
    return 'warning'
  }
  if (reviewStatus === 2) {
    if (videoStatus === 1) {
      return 'success'
    } else if (videoStatus === 0) {
      return 'danger'
    } else {
      return 'info'
    }
  }
  return 'info'
}

const getVideoStatusText = (reviewStatus, videoStatus) => {
  if (reviewStatus === 0) {
    return '待处理'
  }
  if (reviewStatus === 2) {
    if (videoStatus === 1) {
      return '已通过'
    } else if (videoStatus === 0) {
      return '未通过'
    } else {
      return '等待结果'
    }
  }
  return '未知状态'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const onVideoLoaded = () => {
  // 视频加载完成
}

onMounted(() => {
  loadPendingVideos()
  loadAuditTags()
})
</script>

<style scoped>
.video-audit-page {
  display: flex;
  height: calc(100vh - 100px);
  gap: 16px;
  padding: 16px;
  background: #f0f2f5;
}

.left-panel {
  width: 300px;
  background: #fff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.panel-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fafafa;
}

.panel-header h3 {
  margin: 0;
  font-size: 15px;
  color: #333;
  font-weight: 600;
}

.video-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.video-item {
  display: flex;
  gap: 10px;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 8px;
  border: 2px solid transparent;
}

.video-item:hover {
  background: #f5f7fa;
}

.video-item.active {
  background: #e6f7ff;
  border-color: #1890ff;
}

.video-thumb {
  width: 100px;
  height: 56px;
  object-fit: cover;
  border-radius: 6px;
  flex-shrink: 0;
}

.video-info {
  flex: 1;
  min-width: 0;
}

.video-title {
  margin: 0 0 6px 0;
  font-size: 13px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
}

.video-meta {
  margin: 0 0 6px 0;
  font-size: 11px;
  color: #999;
}

.video-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.video-header {
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.video-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.video-stats {
  display: flex;
  gap: 20px;
  font-size: 13px;
}

.video-stats span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.video-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #000;
  min-height: 0;
}

.video-player {
  max-width: 100%;
  max-height: 100%;
  width: auto;
  height: 100%;
  outline: none;
}

.no-video {
  text-align: center;
  color: #666;
}

.no-video .el-icon {
  font-size: 64px;
  margin-bottom: 16px;
  color: #ccc;
}

.video-controls {
  padding: 16px 20px;
  background: #fafafa;
  border-top: 1px solid #f0f0f0;
  display: flex;
  gap: 16px;
  justify-content: center;
}

.control-item {
  flex: 1;
  max-width: 200px;
}

.control-btn {
  width: 100%;
  height: 48px;
  font-size: 15px;
  font-weight: 600;
}

.right-panel {
  width: 320px;
  background: #fff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.audit-info {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.info-section, .audit-section, .quick-actions, .processed-info {
  margin-bottom: 24px;
}

.info-section h3, .audit-section h3, .quick-actions h3, .processed-info h3 {
  margin: 0 0 16px 0;
  font-size: 15px;
  color: #333;
  padding-bottom: 10px;
  border-bottom: 2px solid #1890ff;
  font-weight: 600;
}

.info-item {
  margin-bottom: 14px;
}

.info-item label {
  display: block;
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
  font-weight: 500;
}

.info-value {
  font-size: 14px;
  color: #333;
  margin: 0;
  line-height: 1.5;
}

.info-value.desc {
  color: #666;
  background: #f5f7fa;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 13px;
}

.section-tip {
  font-size: 12px;
  color: #999;
  margin: 0 0 12px 0;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  cursor: pointer;
  transition: all 0.3s;
  user-select: none;
}

.tag-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-btn {
  width: 100%;
}

.no-selection {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
}

.no-selection .el-icon {
  font-size: 48px;
  margin-bottom: 12px;
  color: #ccc;
}

::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #d9d9d9;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #bfbfbf;
}
</style>
