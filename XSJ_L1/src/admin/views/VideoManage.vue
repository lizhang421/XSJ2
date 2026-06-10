<template>
  <div class="video-manage">
    <div class="page-header">
      <h1 class="page-title">视频管理</h1>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索视频名称"
          prefix-icon="Search"
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
        />
      </div>
    </div>
    
    <div class="filter-bar">
      <el-select v-model="filterStatus" placeholder="审核状态" clearable style="width: 150px" @change="handleStatusChange">
        <el-option label="审核通过" :value="1" />
        <el-option label="待审核/未通过" :value="0" />
      </el-select>
      
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
      
      <el-button @click="resetFilters">重置</el-button>
    </div>
    
    <div class="table-container">
      <el-table :data="videoList" style="width: 100%" v-loading="loading || approving">
        <el-table-column prop="enCode" label="视频编码" width="200" />
        <el-table-column label="视频信息" width="320">
          <template #default="{ row }">
            <div class="video-info-cell">
              <img :src="cleanUrl(row.videoImageUrl)" class="video-cover" @error="handleImageError" />
              <div class="video-detail">
                <span class="video-title">{{ row.videoName }}</span>
                <span class="video-desc" v-if="filterStatus === 0">{{ row.videoDesc || row.introduction || '暂无描述' }}</span>
                <span class="video-author" v-else>{{ row.introduction || '暂无简介' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="播放量" width="100" v-if="filterStatus === 1">
          <template #default="{ row }">
            {{ formatNumber(row.viewCount) }}
          </template>
        </el-table-column>
        <el-table-column label="点赞数" width="100" v-if="filterStatus === 1">
          <template #default="{ row }">
            {{ formatNumber(row.likeCount) }}
          </template>
        </el-table-column>
        <el-table-column label="视频状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getVideoStatusType(row.videoStatus)">
              {{ getVideoStatusText(row.videoStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="120" v-if="filterStatus === 0">
          <template #default="{ row }">
            <el-tag :type="getReviewStatusType(row.reviewStatus)">
              {{ getReviewStatusText(row.reviewStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="上传时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.publishTime || row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewVideoDetail(row)" :disabled="approving">详情</el-button>
            <el-button type="success" link @click="handleApprove(row)" v-if="filterStatus === 0 && row.reviewStatus === 0" :disabled="approving">通过</el-button>
            <el-button type="warning" link @click="handleReject(row)" v-if="filterStatus === 0 && row.reviewStatus === 0" :disabled="approving">拒绝</el-button>
            <el-button 
              v-if="row.videoStatus !== 1" 
              type="danger" 
              link 
              @click="openBanDialog(row)" 
              :disabled="approving"
            >
              紧急封禁
            </el-button>
            <el-button 
              v-if="row.videoStatus === 1" 
              type="success" 
              link 
              @click="handleRestoreVideo(row)" 
              :disabled="approving"
            >
              恢复
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="totalVideos"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
    
    <el-dialog 
      v-model="detailDialogVisible" 
      title="视频详情" 
      width="1000px"
      :close-on-click-modal="false"
      class="video-detail-dialog"
    >
      <div v-if="detailLoading" class="detail-loading">
        <el-skeleton :rows="10" animated />
      </div>
      <div v-else class="video-detail-content">
        <div class="video-preview-section" v-if="currentVideo">
          <div class="video-player-wrapper">
            <video 
              :src="cleanUrl(currentVideo.videoUrl)" 
              controls 
              class="video-player"
              :poster="cleanUrl(currentVideo.videoImageUrl)"
            ></video>
          </div>
          <div class="video-basic-info">
            <h2 class="video-title-main">{{ currentVideo.videoName }}</h2>
            <div class="video-meta-info">
              <div class="meta-item">
                <el-icon><View /></el-icon>
                <span>{{ formatNumber(currentVideo.viewCount || 0) }} 播放</span>
              </div>
              <div class="meta-item">
                <el-icon><Star /></el-icon>
                <span>{{ formatNumber(currentVideo.likeCount || 0) }} 点赞</span>
              </div>
              <div class="meta-item">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ formatNumber(currentVideo.commentCount || 0) }} 评论</span>
              </div>
              <div class="meta-item">
                <el-icon><Share /></el-icon>
                <span>{{ formatNumber(currentVideo.shareCount || 0) }} 分享</span>
              </div>
              <div class="meta-item">
                <el-icon><CollectionTag /></el-icon>
                <span>{{ formatNumber(currentVideo.collectCount || 0) }} 收藏</span>
              </div>
            </div>
            <div class="video-desc-info" v-if="currentVideo.introduction || currentVideo.videoDesc">
              <p>{{ currentVideo.introduction || currentVideo.videoDesc }}</p>
            </div>
            <div class="video-status-info">
              <el-tag :type="getVideoStatusType(currentVideo.videoStatus)" size="large">
                {{ getVideoStatusText(currentVideo.videoStatus) }}
              </el-tag>
              <el-tag v-if="filterStatus === 0" :type="getReviewStatusType(currentVideo.reviewStatus)" size="large" style="margin-left: 8px;">
                {{ getReviewStatusText(currentVideo.reviewStatus) }}
              </el-tag>
            </div>
          </div>
        </div>
        
        <div v-if="videoDetail" class="detail-sections-wrapper">
          <div class="detail-section" v-if="videoDetail.videoAuditTagList && videoDetail.videoAuditTagList.length > 0">
            <h3 class="section-title">
              <el-icon><WarningFilled /></el-icon>
              审核不通过原因
            </h3>
            <div class="audit-tags">
              <el-tag 
                v-for="tag in videoDetail.videoAuditTagList" 
                :key="tag.auditTagId"
                type="danger"
                size="large"
                class="audit-tag"
              >
                {{ tag.tagName }}
              </el-tag>
            </div>
          </div>
        
        <div class="detail-section" v-if="videoDetail.videoTypeList && videoDetail.videoTypeList.length > 0">
          <h3 class="section-title">
            <el-icon><VideoCamera /></el-icon>
            视频类型
          </h3>
          <div class="type-list">
            <el-tag 
              v-for="type in videoDetail.videoTypeList" 
              :key="type.videoTypeId"
              type="primary"
              class="type-tag"
            >
              {{ type.typeName }}
            </el-tag>
          </div>
        </div>
        
        <div class="detail-section" v-if="videoDetail.videoFileDTOList && videoDetail.videoFileDTOList.length > 0">
          <h3 class="section-title">
            <el-icon><Document /></el-icon>
            附带文件
          </h3>
          <div class="file-list">
            <div v-for="file in videoDetail.videoFileDTOList" :key="file.fileId" class="file-item">
              <el-icon><DocumentCopy /></el-icon>
              <span>{{ file.fileName || '未知文件' }}</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section" v-if="videoDetail.UserMessageDTO">
          <h3 class="section-title">
            <el-icon><User /></el-icon>
            作者信息
          </h3>
          <div class="author-info">
            <div class="author-avatar">
              <img :src="cleanUrl(videoDetail.UserMessageDTO.avatar)" @error="handleImageError" />
            </div>
            <div class="author-details">
              <div class="author-name">
                <span class="nickname">{{ videoDetail.UserMessageDTO.nickName || '未知用户' }}</span>
                <el-tag v-if="videoDetail.UserMessageDTO.uiMember === '1'" type="warning" size="small">VIP</el-tag>
              </div>
              <div class="author-meta">
                <span v-if="videoDetail.UserMessageDTO.school">
                  <el-icon><School /></el-icon>
                  {{ videoDetail.UserMessageDTO.school }}
                </span>
                <span v-if="videoDetail.UserMessageDTO.leavel">
                  <el-icon><Reading /></el-icon>
                  {{ videoDetail.UserMessageDTO.leavel }}
                </span>
                <span v-if="videoDetail.UserMessageDTO.email">
                  <el-icon><Message /></el-icon>
                  {{ videoDetail.UserMessageDTO.email }}
                </span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="detail-section" v-if="videoDetail.userMessageDTOList && videoDetail.userMessageDTOList.length > 0">
          <h3 class="section-title">
            <el-icon><View /></el-icon>
            观看用户操作 ({{ videoDetail.userMessageDTOList.length }})
          </h3>
          <div class="user-operation-grid">
            <div 
              v-for="user in videoDetail.userMessageDTOList" 
              :key="user.enCode" 
              class="user-operation-card"
            >
              <div class="user-avatar-wrapper">
                <img :src="cleanUrl(user.avatar)" :alt="user.nickName" class="user-avatar" @error="handleImageError" />
                <div class="operation-icons">
                  <el-icon v-if="user.isLike === 1" class="like-icon"><Star /></el-icon>
                  <el-icon v-if="user.isCollect === 1" class="collect-icon"><CollectionTag /></el-icon>
                </div>
              </div>
              <div class="user-info">
                <div class="user-name">{{ user.nickName || '未知用户' }}</div>
                <div class="user-actions">
                  <span v-if="user.isLike === 1" class="action-badge like">已点赞</span>
                  <span v-if="user.isCollect === 1" class="action-badge collect">已收藏</span>
                  <span v-if="user.isComment === 1" class="action-badge comment">已评论</span>
                  <span v-if="user.isShare === 1" class="action-badge share">已分享</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="detail-empty" v-if="!videoDetail.videoAuditTagList && !videoDetail.videoTypeList && !videoDetail.UserMessageDTO && !videoDetail.userMessageDTOList">
          <el-empty description="暂无详细信息" />
        </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <el-dialog 
      v-model="banDialogVisible" 
      title="紧急封禁视频" 
      width="600px"
      :close-on-click-modal="false"
      class="ban-dialog"
    >
      <div v-if="currentBanVideo" class="ban-content">
        <div class="video-info-section">
          <h3 class="info-title">视频信息</h3>
          <div class="video-info-item">
            <label>视频名称：</label>
            <span>{{ currentBanVideo.videoName }}</span>
          </div>
          <div class="video-info-item">
            <label>视频编码：</label>
            <span>{{ currentBanVideo.enCode }}</span>
          </div>
          <div class="video-info-item">
            <label>视频简介：</label>
            <p class="video-introduction">{{ currentBanVideo.introduction || currentBanVideo.videoDesc || '暂无简介' }}</p>
          </div>
        </div>
        
        <div class="tags-section">
          <h3 class="info-title">选择封禁原因</h3>
          <p class="section-tip">点击标签可标记封禁原因（可多选）</p>
          <div class="tag-list">
            <el-tag
              v-for="tag in auditTags"
              :key="tag.auditTagId"
              :type="selectedBanTags.includes(tag.auditTagId) ? 'danger' : ''"
              @click="toggleBanTag(tag.auditTagId)"
              class="tag-item"
              size="large"
            >
              {{ tag.tagName }}
            </el-tag>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="banDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="handleBanVideo" :loading="banning" :disabled="selectedBanTags.length === 0 || banCountdown > 0">
          {{ banCountdown > 0 ? `确认封禁 (${banCountdown}s)` : '确认封禁' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { GetAdminVideoList, AuditVideo, GetVideoDetail, GetAuditVideoResult, DeleteVideo } from '@/admin/api/admin'

const loading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref(1)
const currentPage = ref(1)
const pageSize = ref(10)
const totalVideos = ref(0)
const approving = ref(false)
const detailDialogVisible = ref(false)
const detailLoading = ref(false)
const videoDetail = ref(null)
const currentVideo = ref(null)
const banDialogVisible = ref(false)
const currentBanVideo = ref(null)
const auditTags = ref([])
const selectedBanTags = ref([])
const banning = ref(false)
const banCountdown = ref(0)
let banCountdownTimer = null

const videoList = ref([])
const allVideoList = ref([])

const filteredVideoList = computed(() => {
  if (!searchKeyword.value) {
    return allVideoList.value
  }
  
  const keyword = searchKeyword.value.toLowerCase()
  return allVideoList.value.filter(video => {
    return (
      (video.videoName && video.videoName.toLowerCase().includes(keyword)) ||
      (video.enCode && video.enCode.toLowerCase().includes(keyword)) ||
      (video.introduction && video.introduction.toLowerCase().includes(keyword)) ||
      (video.videoDesc && video.videoDesc.toLowerCase().includes(keyword))
    )
  })
})

const getVideoStatusType = (status) => {
  if (filterStatus.value === 0) {
    const typeMap = { 0: 'danger', 1: 'success', 2: 'warning' }
    return typeMap[status] || 'info'
  } else {
    const typeMap = { 0: 'success', 1: 'danger', 2: 'warning' }
    return typeMap[status] || 'info'
  }
}

const getVideoStatusText = (status) => {
  if (filterStatus.value === 0) {
    const textMap = { 0: '审核不通过', 1: '审核通过', 2: '等待结果' }
    return textMap[status] || '未知'
  } else {
    const textMap = { 0: '正常', 1: '异常', 2: '已下架' }
    return textMap[status] || '未知'
  }
}

const getReviewStatusType = (status) => {
  const typeMap = { 0: 'warning', 1: 'primary', 2: 'success' }
  return typeMap[status] || 'info'
}

const getReviewStatusText = (status) => {
  const textMap = { 0: '等待审核', 1: '正在审核', 2: '审核完毕' }
  return textMap[status] || '未知'
}

const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toString()
}

const formatTime = (time) => {
  if (!time) return '暂无时间'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const handleImageError = (e) => {
  e.target.src = 'https://via.placeholder.com/80x45?text=No+Image'
}

const cleanUrl = (url) => {
  if (!url) return ''
  return url.replace(/`/g, '').trim()
}

const fetchVideoList = async () => {
  loading.value = true
  try {
    const res = await GetAdminVideoList(currentPage.value, pageSize.value, filterStatus.value)
    if (res.code === 0) {
      if (filterStatus.value === 1) {
        allVideoList.value = res.data.videoVOList || []
      } else {
        allVideoList.value = res.data.videoPendingDTOList || []
      }
      videoList.value = allVideoList.value
      totalVideos.value = res.data.count || 0
    } else {
      ElMessage.error(res.data || res.message || '获取视频列表失败')
    }
  } catch (error) {
    console.error('获取视频列表失败:', error)
    ElMessage.error('获取视频列表失败')
  } finally {
    loading.value = false
  }
}

const handleStatusChange = () => {
  currentPage.value = 1
  searchKeyword.value = ''
  fetchVideoList()
}

const handleSearch = () => {
  currentPage.value = 1
  videoList.value = filteredVideoList.value
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = 1
  currentPage.value = 1
  fetchVideoList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  fetchVideoList()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchVideoList()
}

const viewVideoDetail = async (video) => {
  currentVideo.value = video
  detailDialogVisible.value = true
  detailLoading.value = true
  videoDetail.value = null
  
  try {
    const where = filterStatus.value === 1 ? 1 : 2
    const res = await GetVideoDetail(video.enCode, where)
    
    if (res.code === 0) {
      videoDetail.value = res.data
    } else {
      ElMessage.error(res.data || res.message || '获取视频详情失败')
      detailDialogVisible.value = false
    }
  } catch (error) {
    console.error('获取视频详情失败:', error)
    ElMessage.error('获取视频详情失败')
    detailDialogVisible.value = false
  } finally {
    detailLoading.value = false
  }
}

const handleApprove = async (video) => {
  try {
    await ElMessageBox.confirm(`确定要通过视频 ${video.videoName} 的审核吗？\n\n注意：审核过程可能需要几分钟时间，请耐心等待。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    approving.value = true
    const loadingMessage = ElMessage({
      message: '正在审核视频，请稍候...',
      type: 'info',
      duration: 0,
      showClose: true
    })
    
    try {
      const res = await AuditVideo(video.videoPendingId, 1, [])
      
      loadingMessage.close()
      
      if (res.code === 0) {
        ElMessage.success('审核通过成功')
        await fetchVideoList()
      } else {
        ElMessage.error(res.data || res.message || '审核失败')
      }
    } catch (error) {
      loadingMessage.close()
      console.error('审核失败:', error)
      if (error.code === 'ECONNABORTED') {
        ElMessage.error('审核超时，请稍后刷新查看结果')
      } else {
        ElMessage.error('审核失败，请重试')
      }
    } finally {
      approving.value = false
    }
  } catch (error) {
    // 用户取消操作
  }
}

const handleReject = async (video) => {
  try {
    await ElMessageBox.confirm(`确定要拒绝视频 ${video.videoName} 吗？\n\n注意：审核过程可能需要几分钟时间，请耐心等待。`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    approving.value = true
    const loadingMessage = ElMessage({
      message: '正在处理拒绝操作，请稍候...',
      type: 'info',
      duration: 0,
      showClose: true
    })
    
    try {
      const res = await AuditVideo(video.videoPendingId, 2, [])
      
      loadingMessage.close()
      
      if (res.code === 0) {
        ElMessage.success('已拒绝该视频')
        await fetchVideoList()
      } else {
        ElMessage.error(res.data || res.message || '操作失败')
      }
    } catch (error) {
      loadingMessage.close()
      console.error('操作失败:', error)
      if (error.code === 'ECONNABORTED') {
        ElMessage.error('操作超时，请稍后刷新查看结果')
      } else {
        ElMessage.error('操作失败，请重试')
      }
    } finally {
      approving.value = false
    }
  } catch (error) {
    // 用户取消操作
  }
}

const openBanDialog = async (video) => {
  currentBanVideo.value = video
  selectedBanTags.value = []
  banCountdown.value = 3
  banDialogVisible.value = true
  
  if (banCountdownTimer) {
    clearInterval(banCountdownTimer)
  }
  
  banCountdownTimer = setInterval(() => {
    banCountdown.value--
    if (banCountdown.value <= 0) {
      clearInterval(banCountdownTimer)
      banCountdownTimer = null
    }
  }, 1000)
  
  if (auditTags.value.length === 0) {
    try {
      const res = await GetAuditVideoResult()
      if (res.code === 0) {
        auditTags.value = res.data || []
      }
    } catch (error) {
      console.error('获取审核标签失败:', error)
      ElMessage.error('获取审核标签失败')
    }
  }
}

const toggleBanTag = (tagId) => {
  const index = selectedBanTags.value.indexOf(tagId)
  if (index > -1) {
    selectedBanTags.value.splice(index, 1)
  } else {
    selectedBanTags.value.push(tagId)
  }
}

const handleBanVideo = async () => {
  if (selectedBanTags.value.length === 0) {
    ElMessage.warning('请选择封禁原因')
    return
  }
  
  banning.value = true
  
  try {
    const res = await DeleteVideo(
      currentBanVideo.value.enCode,
      selectedBanTags.value,
      1
    )
    
    if (res.code === 0) {
      ElMessage.success('视频已紧急封禁')
      banDialogVisible.value = false
      await fetchVideoList()
    } else {
      ElMessage.error(res.message || '封禁失败')
    }
  } catch (error) {
    console.error('封禁失败:', error)
    ElMessage.error('封禁失败')
  } finally {
    banning.value = false
  }
}

const handleRestoreVideo = async (video) => {
  try {
    await ElMessageBox.confirm(
      `确定要恢复视频 ${video.videoName} 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    approving.value = true
    
    const res = await DeleteVideo(
      video.enCode,
      [],
      0
    )
    
    if (res.code === 0) {
      ElMessage.success('视频已恢复')
      await fetchVideoList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('恢复失败:', error)
    }
  } finally {
    approving.value = false
  }
}

onMounted(() => {
  fetchVideoList()
})

onUnmounted(() => {
  if (banCountdownTimer) {
    clearInterval(banCountdownTimer)
    banCountdownTimer = null
  }
})
</script>

<style scoped>
.video-manage { padding: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-size: 24px; font-weight: 700; color: #1e1e2d; margin: 0; }
.filter-bar { display: flex; gap: 16px; margin-bottom: 24px; }
.table-container { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05); }
.video-info-cell { display: flex; align-items: center; gap: 12px; }
.video-cover { width: 80px; height: 45px; border-radius: 4px; object-fit: cover; cursor: pointer; }
.video-detail { display: flex; flex-direction: column; flex: 1; min-width: 0; }
.video-title { font-weight: 500; color: #333; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.video-desc { font-size: 12px; color: #999; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.video-author { font-size: 12px; color: #999; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }

.video-detail-dialog .detail-loading {
  padding: 40px;
}

.video-detail-content {
  max-height: 600px;
  overflow-y: auto;
}

.video-preview-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 2px solid #f0f0f0;
}

.video-player-wrapper {
  width: 100%;
  margin-bottom: 16px;
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.video-player {
  width: 100%;
  height: 450px;
  object-fit: contain;
  display: block;
}

.video-basic-info {
  padding: 0 8px;
}

.video-title-main {
  font-size: 20px;
  font-weight: 700;
  color: #1e1e2d;
  margin: 0 0 16px 0;
  line-height: 1.4;
}

.video-meta-info {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
}

.meta-item .el-icon {
  font-size: 16px;
  color: #999;
}

.video-desc-info {
  background: #f5f7fa;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.video-desc-info p {
  margin: 0;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.video-status-info {
  display: flex;
  align-items: center;
}

.detail-sections-wrapper {
  margin-top: 24px;
}

.detail-section {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.section-title .el-icon {
  font-size: 18px;
  color: #667eea;
}

.audit-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.audit-tag {
  font-size: 14px;
  padding: 8px 16px;
}

.type-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.type-tag {
  font-size: 14px;
  padding: 6px 14px;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
}

.file-item .el-icon {
  font-size: 18px;
  color: #667eea;
}

.author-info {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;
}

.author-avatar {
  flex-shrink: 0;
}

.author-avatar img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.author-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.author-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name .nickname {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.author-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  color: #666;
}

.author-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.author-meta .el-icon {
  font-size: 14px;
  color: #999;
}

.user-operation-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 16px;
}

.user-operation-card {
  background: #f5f7fa;
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  transition: all 0.3s ease;
}

.user-operation-card:hover {
  background: #e8eaf6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.user-avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 12px;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.operation-icons {
  position: absolute;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 4px;
  background: white;
  border-radius: 12px;
  padding: 2px 6px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.operation-icons .el-icon {
  font-size: 14px;
}

.like-icon {
  color: #f56c6c;
}

.collect-icon {
  color: #e6a23c;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.user-name {
  font-size: 13px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: center;
}

.action-badge {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
  white-space: nowrap;
}

.action-badge.like {
  background: #fef0f0;
  color: #f56c6c;
}

.action-badge.collect {
  background: #fdf6ec;
  color: #e6a23c;
}

.action-badge.comment {
  background: #ecf5ff;
  color: #409eff;
}

.action-badge.share {
  background: #f0f9ff;
  color: #67c23a;
}

.detail-empty {
  padding: 40px 0;
}

.ban-dialog .ban-content {
  max-height: 500px;
  overflow-y: auto;
}

.video-info-section {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.info-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.video-info-item {
  margin-bottom: 12px;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.video-info-item label {
  font-weight: 500;
  color: #666;
  min-width: 80px;
  flex-shrink: 0;
}

.video-info-item span {
  color: #333;
  flex: 1;
}

.video-introduction {
  margin: 0;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  max-height: 120px;
  overflow-y: auto;
}

.tags-section {
  margin-bottom: 16px;
}

.section-tip {
  font-size: 13px;
  color: #999;
  margin: 0 0 12px 0;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-item {
  cursor: pointer;
  transition: all 0.3s ease;
}

.tag-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
</style>
