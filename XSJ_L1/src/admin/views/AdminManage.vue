<template>
  <div class="admin-manage-page">
    <div class="page-header">
      <h1 class="page-title">管理员管理</h1>
      <el-button type="primary" @click="handleAdd">
        <Plus style="width: 14px; height: 14px; margin-right: 4px;" />
        添加管理员
      </el-button>
    </div>
    
    <div class="filter-bar">
      <el-select v-model="filterRole" placeholder="管理员角色" clearable style="width: 150px">
        <el-option label="管理员" :value="1" />
        <el-option label="超级管理员" :value="2" />
      </el-select>
      
      <el-input
        v-model="searchKeyword"
        placeholder="搜索管理员"
        prefix-icon="Search"
        clearable
        style="width: 300px"
        @keyup.enter="handleSearch"
      />
      
      <el-button type="primary" @click="handleSearch">
        <Search style="width: 14px; height: 14px; margin-right: 4px;" />
        搜索
      </el-button>
      
      <el-button @click="resetFilters">重置</el-button>
    </div>
    
    <div class="table-container">
      <el-table :data="adminList" style="width: 100%" v-loading="loading">
        <el-table-column prop="enCode" label="管理员编码" width="200" />
        <el-table-column label="管理员信息" width="280">
          <template #default="{ row }">
            <div class="admin-info-cell">
              <img :src="cleanUrl(row.avatar)" class="admin-avatar" @error="handleImageError" />
              <div class="admin-detail">
                <span class="admin-name">{{ row.nickName || row.realName || '未知管理员' }}</span>
                <span class="admin-email">{{ row.email || '暂无邮箱' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.roleBase)">
              {{ getRoleText(row.roleBase) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button 
              v-if="row.status !== '3'" 
              type="danger" 
              link 
              @click="handleDisable(row)"
            >
              禁用
            </el-button>
            <el-button 
              v-if="row.status === '3'" 
              type="success" 
              link 
              @click="handleEnable(row)"
            >
              启用
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="totalAdmins"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
    
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="adminForm" :rules="adminRules" ref="adminFormRef" label-width="100px">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="adminForm.phone" placeholder="请输入手机号" :disabled="isEdit" />
        </el-form-item>
        
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="adminForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="adminForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        
        <el-form-item label="昵称" prop="nickName">
          <el-input v-model="adminForm.nickName" placeholder="请输入昵称" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="adminForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="角色" prop="roleBase">
          <el-select v-model="adminForm.roleBase" placeholder="请选择角色">
            <el-option label="管理员" :value="1" />
            <el-option label="超级管理员" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { InsertManager } from '@/admin/api/admin'

const loading = ref(false)
const searchKeyword = ref('')
const filterRole = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const totalAdmins = ref(0)
const adminList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加管理员')
const isEdit = ref(false)
const submitting = ref(false)
const adminFormRef = ref(null)
const adminForm = reactive({
  phone: '',
  password: '',
  realName: '',
  nickName: '',
  email: '',
  roleBase: 1
})
const adminRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  nickName: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  roleBase: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}
const getRoleType = (role) => {
  const typeMap = {
    1: 'primary',
    2: 'danger'
  }
  return typeMap[role] || 'info'
}
const getRoleText = (role) => {
  const textMap = {
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
  e.target.src = 'https://via.placeholder.com/40x40?text=Admin'
}
const fetchAdminList = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    adminList.value = []
    totalAdmins.value = 0
  } catch (err) {
    console.error('获取管理员列表失败:', err)
  } finally {
    loading.value = false
  }
}
const handleSearch = () => {
  currentPage.value = 1
  fetchAdminList()
}
const resetFilters = () => {
  searchKeyword.value = ''
  filterRole.value = null
  currentPage.value = 1
  fetchAdminList()
}
const handleSizeChange = (size) => {
  pageSize.value = size
  fetchAdminList()
}
const handlePageChange = (page) => {
  currentPage.value = page
  fetchAdminList()
}
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '添加管理员'
  adminForm.phone = ''
  adminForm.password = ''
  adminForm.realName = ''
  adminForm.nickName = ''
  adminForm.email = ''
  adminForm.roleBase = 1
  dialogVisible.value = true
}
const handleEdit = (admin) => {
  isEdit.value = true
  dialogTitle.value = '编辑管理员'
  adminForm.phone = admin.phone
  adminForm.password = ''
  adminForm.realName = admin.realName
  adminForm.nickName = admin.nickName
  adminForm.email = admin.email
  adminForm.roleBase = parseInt(admin.roleBase)
  dialogVisible.value = true
}
const handleSubmit = async () => {
  try {
    console.log('=== handleSubmit 开始 ===')
    console.log('adminFormRef:', adminFormRef.value)
    
    await adminFormRef.value.validate()
    
    console.log('表单验证通过')
    console.log('isEdit:', isEdit.value)
    console.log('adminForm:', adminForm)
    
    submitting.value = true
    
    if (isEdit.value) {
      ElMessage.success('编辑成功')
    } else {
      console.log('准备调用 InsertManager API')
      console.log('参数:', {
        phone: adminForm.phone,
        password: adminForm.password,
        nickname: adminForm.nickName,
        realname: adminForm.realName
      })
      
      const result = await InsertManager(
        adminForm.phone,
        adminForm.password,
        adminForm.nickName,
        adminForm.realName
      )
      
      console.log('InsertManager 返回结果:', result)
      
      if (result.code === 0) {
        ElMessage.success(result.message || '添加成功')
      } else {
        ElMessage.error(result.message || '添加失败')
        return
      }
    }
    
    dialogVisible.value = false
    fetchAdminList()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}
const handleDisable = async (admin) => {
  try {
    await ElMessageBox.confirm(`确定要禁用管理员 ${admin.nickName || admin.realName} 吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('已禁用')
    fetchAdminList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('禁用失败:', error)
    }
  }
}
const handleEnable = async (admin) => {
  try {
    await ElMessageBox.confirm(`确定要启用管理员 ${admin.nickName || admin.realName} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('已启用')
    fetchAdminList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('启用失败:', error)
    }
  }
}
onMounted(() => {
  fetchAdminList()
})
</script>

<style scoped>
.admin-manage-page {
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

.admin-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.admin-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.admin-detail {
  display: flex;
  flex-direction: column;
}

.admin-name {
  font-weight: 500;
  color: #333;
}

.admin-email {
  font-size: 12px;
  color: #999;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
