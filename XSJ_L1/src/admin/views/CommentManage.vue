<template>
  <div class="comment-manage">
    <div class="page-header">
      <h1 class="page-title">评论管理</h1>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索评论内容"
          prefix-icon="Search"
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
        />
      </div>
    </div>
    
    <div class="filter-bar">
      <el-select v-model="filterStatus" placeholder="评论状态" clearable style="width: 150px">
        <el-option label="正常" :value="1" />
        <el-option label="异常" :value="2" />
        <el-option label="已删除" :value="3" />
      </el-select>
      
      <el-date-picker
        v-model="filterDate"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        style="width: 300px"
        format="YYYY年MM月DD日"
        value-format="YYYY-MM-DD HH:mm:ss"
      />
      
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
      
      <el-button @click="resetFilters">重置</el-button>
    </div>
    
    <div class="table-container">
      <el-table :data="commentList" style="width: 100%" v-loading="loading">
        <el-table-column prop="videoCommentId" label="评论ID" width="200" show-overflow-tooltip />
        <el-table-column label="评论用户" width="180">
          <template #default="{ row }">
            <div class="user-info-cell">
              <img :src="cleanUrl(row.userAvatar)" class="user-avatar" @error="handleImageError" />
              <span class="user-name">{{ row.userNickName || '未知用户' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="评论内容" min-width="300">
          <template #default="{ row }">
            <div class="comment-content">
              <div class="main-content">{{ row.content }}</div>
              <div v-if="row.originalContent" class="original-content">
                回复：{{ row.originalContent }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="所属视频" width="200">
          <template #default="{ row }">
            <div class="video-info-cell">
              <img :src="cleanUrl(row.videoImageUrl)" class="video-cover" @error="handleImageError" />
              <span class="video-name">{{ row.videoName || '未知视频' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评论时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewCommentDetail(row)">详情</el-button>
            <el-button 
              v-if="row.status !== 2" 
              type="danger" 
              link 
              @click="handleBlockComment(row)"
            >
              屏蔽
            </el-button>
            <el-button 
              v-if="row.status === 2" 
              type="success" 
              link 
              @click="handleRestoreComment(row)"
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
          :total="totalComments"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
    
    <el-dialog 
      v-model="detailDialogVisible" 
      title="评论详情" 
      width="800px"
      :close-on-click-modal="false"
      class="comment-detail-dialog"
    >
      <div v-if="currentComment" class="comment-detail-content">
        <div class="detail-section">
          <h3 class="section-title">用户信息</h3>
          <div class="user-detail-card">
            <img :src="cleanUrl(currentComment.userAvatar)" class="user-avatar-large" @error="handleImageError" />
            <div class="user-info-grid">
              <div class="info-item">
                <span class="label">用户昵称：</span>
                <span class="value">{{ currentComment.userNickName || '未知用户' }}</span>
              </div>
              <div class="info-item">
                <span class="label">用户编码：</span>
                <span class="value">{{ currentComment.userEncode }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h3 class="section-title">评论内容</h3>
          <div class="comment-detail-box">
            <div class="comment-main">
              <div class="comment-text">{{ currentComment.content }}</div>
              <div v-if="currentComment.originalContent" class="original-comment">
                <div class="original-label">回复的原评论：</div>
                <div class="original-text">{{ currentComment.originalContent }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h3 class="section-title">所属视频</h3>
          <div class="video-detail-card">
            <img :src="cleanUrl(currentComment.videoImageUrl)" class="video-cover-large" @error="handleImageError" />
            <div class="video-info-grid">
              <div class="info-item">
                <span class="label">视频名称：</span>
                <span class="value">{{ currentComment.videoName }}</span>
              </div>
              <div class="info-item">
                <span class="label">视频编码：</span>
                <span class="value">{{ currentComment.videoEncode }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h3 class="section-title">评论信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">评论ID：</span>
              <span class="value">{{ currentComment.videoCommentId }}</span>
            </div>
            <div class="info-item">
              <span class="label">评论状态：</span>
              <el-tag :type="getStatusType(currentComment.status)">
                {{ getStatusText(currentComment.status) }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">评论时间：</span>
              <span class="value">{{ formatTime(currentComment.createTime) }}</span>
            </div>
            <div class="info-item">
              <span class="label">是否已读：</span>
              <el-tag :type="currentComment.isRead === 1 ? 'success' : 'warning'">
                {{ currentComment.isRead === 1 ? '已读' : '未读' }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button 
          v-if="currentComment && currentComment.status !== 2" 
          type="danger" 
          @click="handleBlockComment(currentComment)"
        >
          紧急屏蔽
        </el-button>
        <el-button 
          v-if="currentComment && currentComment.status === 2" 
          type="success" 
          @click="handleRestoreComment(currentComment)"
        >
          恢复评论
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { GetCommentList, DeleteComment } from '@/admin/api/admin'

const loading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref(null)
const filterDate = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalComments = ref(0)

const commentList = ref([])
const detailDialogVisible = ref(false)
const currentComment = ref(null)

watch([filterStatus, filterDate], () => {
  currentPage.value = 1
  fetchCommentList()
})

const getStatusType = (status) => {
  const typeMap = {
    1: 'success',
    2: 'warning',
    3: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    1: '正常',
    2: '屏蔽',
    3: '已删除'
  }
  return textMap[status] || '未知'
}

const cleanUrl = (url) => {
  if (!url) return ''
  return url.replace(/`/g, '').trim()
}

const handleImageError = (e) => {
  e.target.src = 'https://via.placeholder.com/40x40?text=User'
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

const fetchCommentList = async () => {
  loading.value = true
  try {
    let startTime = null
    let endTime = null
    
    if (filterDate.value && filterDate.value.length === 2) {
      startTime = filterDate.value[0]
      endTime = filterDate.value[1]
    }
    
    const res = await GetCommentList(
      currentPage.value,
      pageSize.value,
      filterStatus.value,
      searchKeyword.value,
      startTime,
      endTime
    )
    
    if (res.code === 0) {
      commentList.value = res.data.list || []
      totalComments.value = res.data.total || 0
    }
  } catch (err) {
    console.error('获取评论列表失败:', err)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchCommentList()
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = null
  filterDate.value = []
  currentPage.value = 1
  fetchCommentList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  fetchCommentList()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchCommentList()
}

const viewCommentDetail = (comment) => {
  currentComment.value = comment
  detailDialogVisible.value = true
}

const handleBlockComment = async (comment) => {
  try {
    await ElMessageBox.confirm(
      `确定要屏蔽该评论吗？\n\n评论内容：${comment.content}`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await DeleteComment(comment.videoCommentId, 2)
    
    if (res.code === 0) {
      ElMessage.success('评论已屏蔽')
      detailDialogVisible.value = false
      await fetchCommentList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('屏蔽失败:', error)
    }
  }
}

const handleRestoreComment = async (comment) => {
  try {
    await ElMessageBox.confirm(
      `确定要恢复该评论吗？\n\n评论内容：${comment.content}`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    const res = await DeleteComment(comment.videoCommentId, 1)
    
    if (res.code === 0) {
      ElMessage.success('评论已恢复')
      detailDialogVisible.value = false
      await fetchCommentList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('恢复失败:', error)
    }
  }
}

onMounted(() => {
  fetchCommentList()
})
</script>

<style scoped>
.comment-manage { padding: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-size: 24px; font-weight: 700; color: #1e1e2d; margin: 0; }
.filter-bar { display: flex; gap: 16px; margin-bottom: 24px; }
.table-container { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05); }
.user-info-cell { display: flex; align-items: center; gap: 8px; }
.user-avatar { width: 32px; height: 32px; border-radius: 50%; object-fit: cover; }
.user-name { font-size: 14px; color: #333; }
.comment-content { display: flex; flex-direction: column; gap: 4px; }
.main-content { color: #333; }
.original-content { font-size: 12px; color: #999; padding: 4px 8px; background: #f5f7fa; border-radius: 4px; }
.video-info-cell { display: flex; align-items: center; gap: 8px; }
.video-cover { width: 60px; height: 34px; border-radius: 4px; object-fit: cover; }
.video-name { font-size: 13px; color: #333; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 120px; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }

.comment-detail-dialog .comment-detail-content {
  max-height: 600px;
  overflow-y: auto;
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
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.user-detail-card {
  display: flex;
  gap: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;
}

.user-avatar-large {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-info-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.comment-detail-box {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;
}

.comment-main {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-text {
  font-size: 15px;
  color: #333;
  line-height: 1.6;
}

.original-comment {
  padding: 12px;
  background: #fff;
  border-radius: 8px;
  border-left: 3px solid #667eea;
}

.original-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.original-text {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.video-detail-card {
  display: flex;
  gap: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;
}

.video-cover-large {
  width: 120px;
  height: 68px;
  border-radius: 8px;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.video-info-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-item .label {
  font-size: 14px;
  color: #666;
  min-width: 80px;
}

.info-item .value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
</style>
