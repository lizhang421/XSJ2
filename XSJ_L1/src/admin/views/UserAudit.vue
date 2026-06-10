<template>
  <div class="user-audit-page">
    <div class="left-panel">
      <div class="panel-header">
        <h3>{{ currentFilterTitle }} ({{ currentFilterCount }})</h3>
        <el-select v-model="filterStatus" placeholder="筛选状态" size="small" @change="filterUsers">
          <el-option :label="'全部 (' + pendingUsers.length + ')'" value="all"></el-option>
          <el-option :label="'待审核 (' + pendingCount + ')'" :value="0"></el-option>
          <el-option :label="'已通过 (' + approvedCount + ')'" :value="1"></el-option>
          <el-option :label="'已拒绝 (' + rejectedCount + ')'" :value="2"></el-option>
        </el-select>
      </div>
      
      <div class="user-list" ref="userListRef">
        <div
          v-for="user in filteredUsers"
          :key="user.id"
          :ref="el => { if (user.id === currentUser?.id) currentUserRef = el }"
          class="user-item"
          :class="{ active: currentUser?.id === user.id }"
          @click="selectUser(user)"
        >
          <div class="user-avatar">
            <el-icon v-if="user.targetType === 'AVATAR'" :size="40"><User /></el-icon>
            <el-icon v-else :size="40"><Picture /></el-icon>
          </div>
          <div class="user-info">
            <h4 class="user-title">{{ getTargetTypeName(user.targetType) }}</h4>
            <p class="user-meta">
              <span>{{ formatDate(user.createTime) }}</span>
            </p>
            <el-tag :type="getStatusType(user.status)" size="small">
              {{ getStatusText(user.status) }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>
    
    <div class="image-section">
      <div class="image-header">
        <h2 v-if="currentUser">{{ getTargetTypeName(currentUser.targetType) }}审核</h2>
        <div class="image-stats" v-if="currentUser">
          <span><el-icon><Clock /></el-icon> {{ formatDate(currentUser.createTime) }}</span>
        </div>
      </div>
      
      <div class="image-container">
        <div v-if="currentUser" class="image-preview">
          <img 
            :src="getUserImage(currentUser)" 
            :alt="getTargetTypeName(currentUser.targetType)"
            class="preview-image"
          />
        </div>
        <div v-else class="no-image">
          <el-icon><Picture /></el-icon>
          <p>暂无待审核内容</p>
        </div>
      </div>
      
      <div class="image-controls" v-if="currentUser && currentUser.status === 0">
        <div class="control-item">
          <el-button
            type="success"
            size="large"
            @click="approveUser"
            :loading="approving"
            class="control-btn"
          >
            <el-icon><Check /></el-icon>
            审核通过
          </el-button>
        </div>
        <div class="control-item">
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
      </div>
      <div class="image-controls" v-if="currentUser && currentUser.status !== 0">
        <div class="control-item" style="flex: 1; max-width: 100%;">
          <el-alert
            :title="`该内容已处理 - ${currentUser.status === 1 ? '已通过' : '已拒绝'}`"
            type="info"
            :closable="false"
            show-icon
          />
        </div>
      </div>
    </div>
    
    <div class="right-panel">
      <div v-if="currentUser" class="audit-info">
        <div class="info-section">
          <h3>审核详情</h3>
          <div class="info-item">
            <label>审核类型</label>
            <p class="info-value">{{ getTargetTypeName(currentUser.targetType) }}</p>
          </div>
          <div class="info-item">
            <label>用户ID</label>
            <p class="info-value">{{ currentUser.userId }}</p>
          </div>
          <div class="info-item">
            <label>审核状态</label>
            <el-tag :type="getStatusType(currentUser.status)" size="large">
              {{ getStatusText(currentUser.status) }}
            </el-tag>
          </div>
          <div class="info-item">
            <label>提交时间</label>
            <p class="info-value">{{ formatDate(currentUser.createTime) }}</p>
          </div>
          <div class="info-item" v-if="currentUser.updateTime">
            <label>处理时间</label>
            <p class="info-value">{{ formatDate(currentUser.updateTime) }}</p>
          </div>
        </div>
        
        <div class="audit-section" v-if="currentUser.status === 0">
          <h3>快速标签</h3>
          <p class="section-tip">选择一个标签标记不通过原因</p>
          <div class="tag-list">
            <el-tag
              v-for="tag in auditTags"
              :key="tag.auditTagId"
              :type="selectedTag === tag.auditTagId ? 'danger' : ''"
              @click="selectedTag = tag.auditTagId"
              class="tag-item"
              size="large"
            >
              {{ tag.tagName }}
            </el-tag>
          </div>
        </div>
        
        <div class="quick-actions" v-if="currentUser.status === 0">
          <h3>快捷操作</h3>
          <div class="action-buttons">
            <el-button 
              v-if="selectedTag"
              type="danger" 
              @click="quickReject"
              :loading="rejecting"
              size="large"
              class="action-btn"
            >
              <el-icon><Close /></el-icon>
              使用选中标签拒绝
            </el-button>
            <el-button 
              type="warning" 
              @click="selectedTag = ''"
              size="large"
              class="action-btn"
            >
              <el-icon><RefreshLeft /></el-icon>
              清空选中标签
            </el-button>
          </div>
        </div>
      </div>
      
      <div v-else class="no-selection">
        <el-icon><Warning /></el-icon>
        <p>请选择一个内容进行审核</p>
      </div>
    </div>
    
    <el-dialog
      v-model="rejectDialogVisible"
      title="审核不通过"
      width="500px"
    >
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="选择标签">
          <el-radio-group v-model="rejectForm.tag">
            <el-radio
              v-for="tag in auditTags"
              :key="tag.auditTagId"
              :label="tag.auditTagId"
            >
              {{ tag.tagName }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="rejectUser" :loading="rejecting">
          确定不通过
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Picture, Check, Close, Warning, Clock, RefreshLeft } from '@element-plus/icons-vue'
import { GetShenHeImageList, AuditUserImage } from '@/admin/api/admin'
import { GetAuditVideoResult } from '@/admin/api/admin'

const pendingUsers = ref([])
const auditTags = ref([])
const currentUser = ref(null)
const filterStatus = ref('all')
const selectedTag = ref('')
const userListRef = ref(null)
const currentUserRef = ref(null)

const approving = ref(false)
const rejecting = ref(false)
const rejectDialogVisible = ref(false)
const rejectForm = ref({
  tag: ''
})

const filteredUsers = computed(() => {
  if (filterStatus.value === 'all') {
    return pendingUsers.value
  }
  return pendingUsers.value.filter(u => u.status === filterStatus.value)
})

const pendingCount = computed(() => {
  return pendingUsers.value.filter(u => u.status === 0).length
})

const approvedCount = computed(() => {
  return pendingUsers.value.filter(u => u.status === 1).length
})

const rejectedCount = computed(() => {
  return pendingUsers.value.filter(u => u.status === 2).length
})

const currentFilterTitle = computed(() => {
  const titleMap = {
    'all': '全部',
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return titleMap[filterStatus.value] || '待审核'
})

const currentFilterCount = computed(() => {
  if (filterStatus.value === 'all') {
    return pendingUsers.value.length
  }
  return filteredUsers.value.length
})

const loadPendingUsers = async () => {
  try {
    const result = await GetShenHeImageList([0, 1, 2])
    if (result.code === 0) {
      pendingUsers.value = result.data || []
      if (pendingUsers.value.length > 0 && !currentUser.value) {
        const firstPending = pendingUsers.value.find(u => u.status === 0)
        currentUser.value = firstPending || pendingUsers.value[0]
      }
    }
  } catch (error) {
    console.error('加载待审核用户失败:', error)
    ElMessage.error('加载待审核用户失败')
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

const selectUser = (user) => {
  currentUser.value = user
  selectedTag.value = ''
  
  nextTick(() => {
    if (currentUserRef.value) {
      currentUserRef.value.scrollIntoView({
        behavior: 'smooth',
        block: 'center'
      })
    }
  })
}

const filterUsers = () => {
}

const approveUser = async () => {
  if (!currentUser.value) {
    console.error('没有当前用户')
    return
  }
  
  approving.value = true
  try {
    const result = await AuditUserImage(
      currentUser.value.id,
      1,
      ''
    )
    
    if (result.code === 0) {
      ElMessage.success('审核通过')
      
      const index = pendingUsers.value.findIndex(
        u => u.id === currentUser.value.id
      )
      if (index > -1) {
        pendingUsers.value.splice(index, 1)
      }
      
      if (pendingUsers.value.length > 0) {
        currentUser.value = pendingUsers.value[0]
      } else {
        currentUser.value = null
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
    tag: selectedTag.value || ''
  }
  rejectDialogVisible.value = true
}

const quickReject = async () => {
  if (!currentUser.value) return
  
  if (!selectedTag.value) {
    ElMessage.warning('请选择一个审核标签')
    return
  }
  
  rejecting.value = true
  try {
    const result = await AuditUserImage(
      currentUser.value.id,
      2,
      selectedTag.value
    )
    
    if (result.code === 0) {
      ElMessage.success('已标记为不通过')
      
      const index = pendingUsers.value.findIndex(
        u => u.id === currentUser.value.id
      )
      if (index > -1) {
        pendingUsers.value.splice(index, 1)
      }
      
      if (pendingUsers.value.length > 0) {
        currentUser.value = pendingUsers.value[0]
        selectedTag.value = ''
      } else {
        currentUser.value = null
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

const rejectUser = async () => {
  if (!currentUser.value) return
  
  if (!rejectForm.value.tag) {
    ElMessage.warning('请选择一个审核标签')
    return
  }
  
  rejecting.value = true
  try {
    const result = await AuditUserImage(
      currentUser.value.id,
      2,
      rejectForm.value.tag
    )
    
    if (result.code === 0) {
      ElMessage.success('已标记为不通过')
      
      const index = pendingUsers.value.findIndex(
        u => u.id === currentUser.value.id
      )
      if (index > -1) {
        pendingUsers.value.splice(index, 1)
      }
      
      if (pendingUsers.value.length > 0) {
        currentUser.value = pendingUsers.value[0]
        nextTick(() => {
          selectedTag.value = ''
        })
      } else {
        currentUser.value = null
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

const getStatusType = (status) => {
  const types = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return texts[status] || '未知'
}

const getTargetTypeName = (type) => {
  const types = {
    'AVATAR': '头像',
    'BACKGROUND': '背景图',
    'COVER': '封面图'
  }
  return types[type] || '其他'
}

const getUserImage = (user) => {
  if (user.recourseUrl) {
    return user.recourseUrl.trim().replace(/[\r\n`]/g, '')
  }
  return ''
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  loadPendingUsers()
  loadAuditTags()
})
</script>

<style scoped>
.user-audit-page {
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

.user-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.user-item {
  display: flex;
  gap: 10px;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 8px;
  border: 2px solid transparent;
}

.user-item:hover {
  background: #f5f7fa;
}

.user-item.active {
  background: #e6f7ff;
  border-color: #1890ff;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: #1890ff;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-title {
  margin: 0 0 6px 0;
  font-size: 13px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
}

.user-meta {
  margin: 0 0 6px 0;
  font-size: 11px;
  color: #999;
}

.image-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.image-header {
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.image-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.image-stats {
  display: flex;
  gap: 20px;
  font-size: 13px;
}

.image-stats span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.image-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  min-height: 0;
  padding: 20px;
}

.image-preview {
  max-width: 100%;
  max-height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-image {
  max-width: 100%;
  max-height: 100%;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.no-image {
  text-align: center;
  color: #666;
}

.no-image .el-icon {
  font-size: 64px;
  margin-bottom: 16px;
  color: #ccc;
}

.image-controls {
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

.info-section, .audit-section, .quick-actions {
  margin-bottom: 24px;
}

.info-section h3, .audit-section h3, .quick-actions h3 {
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
