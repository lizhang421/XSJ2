<template>
  <div class="user-manage">
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户"
          prefix-icon="Search"
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
        />
        <el-button v-if="isSuperAdmin" type="success" @click="showAddAdminDialog">
          <el-icon><Plus /></el-icon>
          添加管理员
        </el-button>
      </div>
    </div>
    
    <div class="filter-bar">
      <el-select v-model="filterStatus" placeholder="账号状态" clearable style="width: 150px">
        <el-option label="正常" :value="1" />
        <el-option label="禁用" :value="2" />
        <el-option label="异常" :value="3" />
      </el-select>
      
      <el-select v-model="filterRole" placeholder="账号身份" clearable style="width: 150px">
        <el-option label="用户" :value="0" />
        <el-option label="管理员" :value="1" />
        <el-option label="超级管理员" :value="2" />
      </el-select>
      
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
      
      <el-button @click="resetFilters">重置</el-button>
    </div>
    
    <div class="table-container">
      <el-table :data="userList" style="width: 100%" v-loading="loading">
        <el-table-column prop="enCode" label="用户编码" width="200" />
        <el-table-column label="用户信息" width="280">
          <template #default="{ row }">
            <div class="user-info-cell">
              <img :src="cleanUrl(row.avatar)" class="user-avatar" @error="handleImageError" />
              <div class="user-detail">
                <span class="user-name">{{ row.nickName || row.realName || '未知用户' }}</span>
                <span class="user-email">{{ row.email || '暂无邮箱' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="账号身份" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.roleBase)">
              {{ getRoleText(row.roleBase) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="账号状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="280">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewUserDetail(row)">详情</el-button>
            <el-button 
              v-if="row.status !== '2' && row.status !== '3'" 
              type="danger" 
              link 
              @click="handleBanUser(row)"
            >
              紧急封禁
            </el-button>
            <el-button 
              v-if="row.status === '3'" 
              type="warning" 
              link 
              @click="handleReplyUser(row)"
            >
              回复用户
            </el-button>
            <el-button 
              v-if="row.status === '2' || row.status === '3'" 
              type="success" 
              link 
              @click="handleRestoreUser(row)"
            >
              恢复正常
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="totalUsers"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
    
    <el-dialog v-model="addAdminDialogVisible" title="添加管理员" width="500px">
      <el-form :model="addAdminForm" :rules="addAdminRules" ref="addAdminFormRef" label-width="100px">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addAdminForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="addAdminForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickName">
          <el-input v-model="addAdminForm.nickName" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="addAdminForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addAdminDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddAdmin" :loading="addAdminLoading">确认添加</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="userDetailDialogVisible" title="用户详情" width="600px" class="user-detail-dialog">
      <div class="user-detail-content" v-if="currentUserDetail">
        <div class="detail-header">
          <img :src="cleanUrl(currentUserDetail.avatar)" class="detail-avatar" @error="handleImageError" />
          <div class="detail-basic">
            <h3 class="detail-name">{{ currentUserDetail.nickName || currentUserDetail.realName || '未知用户' }}</h3>
            <div class="detail-tags">
              <el-tag :type="getRoleType(currentUserDetail.roleBase)">{{ getRoleText(currentUserDetail.roleBase) }}</el-tag>
              <el-tag :type="getStatusType(currentUserDetail.status)">{{ getStatusText(currentUserDetail.status) }}</el-tag>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h4 class="section-title">基本信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="item-label">用户编码</span>
              <span class="item-value">{{ currentUserDetail.enCode || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">手机号</span>
              <span class="item-value">{{ currentUserDetail.phone || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">邮箱</span>
              <span class="item-value">{{ currentUserDetail.email || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">真实姓名</span>
              <span class="item-value">{{ currentUserDetail.realName || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">昵称</span>
              <span class="item-value">{{ currentUserDetail.nickName || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">性别</span>
              <span class="item-value">{{ currentUserDetail.sex === 1 ? '男' : currentUserDetail.sex === 2 ? '女' : '未知' }}</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h4 class="section-title">账号信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="item-label">账号身份</span>
              <span class="item-value">{{ getRoleText(currentUserDetail.roleBase) }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">账号状态</span>
              <span class="item-value">{{ getStatusText(currentUserDetail.status) }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">注册时间</span>
              <span class="item-value">{{ currentUserDetail.createTime || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">最后登录</span>
              <span class="item-value">{{ currentUserDetail.lastLoginTime || '-' }}</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section" v-if="currentUserDetail.signature">
          <h4 class="section-title">个人签名</h4>
          <p class="signature-text">{{ currentUserDetail.signature }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="userDetailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <el-dialog 
      v-model="banConfirmDialogVisible" 
      title="严重警告" 
      width="450px"
      :close-on-click-modal="false"
    >
      <div class="ban-confirm-content">
        <el-icon class="warning-icon"><Warning /></el-icon>
        <p class="warning-text">确定要紧急封禁用户 <strong>{{ banTargetUser?.nickName || banTargetUser?.realName }}</strong> 吗？</p>
        <p class="warning-desc">⚠️ 警告：当前操作可能对用户造成严重影响，请谨慎操作！</p>
      </div>
      <template #footer>
        <el-button @click="banConfirmDialogVisible = false">取消</el-button>
        <el-button 
          type="danger" 
          :disabled="banCountdown > 0" 
          :loading="banLoading"
          @click="confirmBanUser"
        >
          {{ banCountdown > 0 ? `确认封禁 (${banCountdown}s)` : '确认封禁' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Warning } from '@element-plus/icons-vue'
import { GetUserList, UpdateUserStatus, InsertManager } from '@/admin/api/admin'
import { useAdminStore } from '@/admin/stores/admin'

const adminStore = useAdminStore()

const isSuperAdmin = computed(() => {
  return adminStore.adminInfo?.roleBase === '2' || adminStore.adminInfo?.roleBase === 2
})

const loading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref(null)
const filterRole = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const totalUsers = ref(0)

const userList = ref([])

const addAdminDialogVisible = ref(false)
const addAdminLoading = ref(false)
const addAdminFormRef = ref(null)
const addAdminForm = ref({
  phone: '',
  password: '',
  nickName: '',
  realName: ''
})

const addAdminRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  nickName: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ]
}

const userDetailDialogVisible = ref(false)
const currentUserDetail = ref(null)

const banConfirmDialogVisible = ref(false)
const banTargetUser = ref(null)
const banCountdown = ref(3)
const banLoading = ref(false)
let banCountdownTimer = null

watch([filterStatus, filterRole], () => {
  currentPage.value = 1
  fetchUserList()
})

const getRoleType = (role) => {
  const typeMap = {
    0: 'info',
    1: 'primary',
    2: 'danger'
  }
  return typeMap[role] || 'info'
}

const getRoleText = (role) => {
  const textMap = {
    0: '用户',
    1: '管理员',
    2: '超级管理员'
  }
  return textMap[role] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    '1': 'success',
    '2': 'danger',
    '3': 'warning'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    '1': '正常',
    '2': '禁用',
    '3': '异常'
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

const fetchUserList = async () => {
  loading.value = true
  try {
    const res = await GetUserList(
      currentPage.value,
      pageSize.value,
      filterStatus.value,
      searchKeyword.value,
      filterRole.value
    )
    
    if (res.code === 0) {
      userList.value = res.data.userList || []
      totalUsers.value = res.data.total || 0
    } else {
      ElMessage.error(res.data || res.message || '获取用户列表失败')
    }
  } catch (err) {
    console.error('获取用户列表失败:', err)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchUserList()
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = null
  filterRole.value = null
  currentPage.value = 1
}

const handleSizeChange = (size) => {
  pageSize.value = size
  fetchUserList()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchUserList()
}

const viewUserDetail = (user) => {
  currentUserDetail.value = user
  userDetailDialogVisible.value = true
}

const handleBanUser = (user) => {
  banTargetUser.value = user
  banCountdown.value = 3
  banConfirmDialogVisible.value = true
  
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
}

const confirmBanUser = async () => {
  banLoading.value = true
  try {
    const res = await UpdateUserStatus(banTargetUser.value.enCode, 2)
    
    if (res.code === 0) {
      ElMessage.success('用户已紧急封禁')
      banConfirmDialogVisible.value = false
      await fetchUserList()
    } else {
      ElMessage.error(res.message || '封禁失败')
    }
  } catch (error) {
    console.error('封禁失败:', error)
    ElMessage.error('封禁失败')
  } finally {
    banLoading.value = false
  }
}

const handleRestoreUser = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要恢复用户 ${user.nickName || user.realName} 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    const res = await UpdateUserStatus(user.enCode, 1)
    
    if (res.code === 0) {
      ElMessage.success('用户已恢复')
      await fetchUserList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('恢复失败:', error)
    }
  }
}

const handleReplyUser = async (user) => {
  try {
    const { value: replyContent } = await ElMessageBox.prompt(
      `回复用户 ${user.nickName || user.realName} 的异常状态申诉`,
      '回复用户',
      {
        confirmButtonText: '发送回复',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputPlaceholder: '请输入回复内容...',
        inputValidator: (value) => {
          if (!value || value.trim() === '') {
            return '回复内容不能为空'
          }
          return true
        }
      }
    )
    
    if (replyContent) {
      const res = await UpdateUserStatus(user.enCode, 1, replyContent)
      
      if (res.code === 0) {
        ElMessage.success('回复已发送，用户状态已恢复正常')
        await fetchUserList()
      } else {
        ElMessage.error(res.message || '回复失败')
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('回复失败:', error)
    }
  }
}

const showAddAdminDialog = () => {
  addAdminForm.value = {
    phone: '',
    password: '',
    nickName: '',
    realName: ''
  }
  addAdminDialogVisible.value = true
}

const handleAddAdmin = async () => {
  if (!addAdminFormRef.value) return
  
  try {
    await addAdminFormRef.value.validate()
  } catch {
    return
  }
  
  addAdminLoading.value = true
  try {
    const res = await InsertManager(
      addAdminForm.value.phone,
      addAdminForm.value.password,
      addAdminForm.value.nickName,
      addAdminForm.value.realName
    )
    if (res.code === 0) {
      ElMessage.success(res.message || '添加管理员成功')
      addAdminDialogVisible.value = false
      fetchUserList()
    } else {
      ElMessage.error(res.message || '添加管理员失败')
    }
  } catch (error) {
    console.error('添加管理员失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '添加管理员失败')
  } finally {
    addAdminLoading.value = false
  }
}

onMounted(() => {
  fetchUserList()
})

onUnmounted(() => {
  if (banCountdownTimer) {
    clearInterval(banCountdownTimer)
    banCountdownTimer = null
  }
})
</script>

<style scoped>
.user-manage {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e1e2d;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.table-container {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.user-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 500;
  color: #333;
}

.user-email {
  font-size: 12px;
  color: #999;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.user-detail-dialog .user-detail-content {
  padding: 10px 0;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.detail-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #409eff;
}

.detail-basic {
  flex: 1;
}

.detail-name {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px 0;
}

.detail-tags {
  display: flex;
  gap: 8px;
}

.detail-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 0;
  padding-left: 10px;
  border-left: 3px solid #409eff;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-label {
  font-size: 12px;
  color: #999;
}

.item-value {
  font-size: 14px;
  color: #333;
}

.signature-text {
  margin: 0;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.ban-confirm-content {
  text-align: center;
  padding: 20px 0;
}

.warning-icon {
  font-size: 48px;
  color: #e6a23c;
  margin-bottom: 16px;
}

.warning-text {
  font-size: 16px;
  color: #333;
  margin-bottom: 12px;
}

.warning-text strong {
  color: #f56c6c;
}

.warning-desc {
  font-size: 14px;
  color: #e6a23c;
  background: #fdf6ec;
  padding: 10px 15px;
  border-radius: 6px;
}
</style>
