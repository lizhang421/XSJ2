<template>
  <div class="profile-page">
    <div class="page-header">
      <h1 class="page-title">个人中心</h1>
    </div>
    
    <div class="profile-content">
      <el-tabs v-model="activeTab" class="profile-tabs">
        <el-tab-pane label="个人信息" name="info">
          <div class="profile-section">
            <div class="avatar-section">
              <el-upload
                class="avatar-uploader"
                :action="uploadUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
                accept="image/*"
              >
                <el-avatar :size="100" :src="cleanUrl(profileForm.avatar) || cleanUrl(adminInfo.avatar)">
                  <el-icon><UserFilled /></el-icon>
                </el-avatar>
                <div class="avatar-overlay">
                  <el-icon><Camera /></el-icon>
                  <span>更换头像</span>
                </div>
              </el-upload>
              <p class="avatar-tip">点击头像更换，支持 jpg、png 格式，大小不超过 2MB</p>
            </div>
            
            <el-form :model="profileForm" label-width="100px" class="profile-form">
              <el-form-item label="用户编码">
                <el-input v-model="adminInfo.enCode" disabled />
              </el-form-item>
              
              <el-form-item label="真实姓名">
                <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
              </el-form-item>
              
              <el-form-item label="昵称">
                <el-input v-model="profileForm.nickName" placeholder="请输入昵称" />
              </el-form-item>
              
              <el-form-item label="手机号">
                <el-input v-model="adminInfo.phone" disabled />
              </el-form-item>
              
              <el-form-item label="邮箱">
                <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
              </el-form-item>
              
              <el-form-item label="性别">
                <el-radio-group v-model="profileForm.gender">
                  <el-radio :value="0">保密</el-radio>
                  <el-radio :value="1">男</el-radio>
                  <el-radio :value="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
              
              <el-form-item label="学校">
                <el-input v-model="profileForm.school" placeholder="请输入学校" />
              </el-form-item>
              
              <el-form-item label="学历">
                <el-select v-model="profileForm.leavel" placeholder="请选择学历" style="width: 100%">
                  <el-option label="高中" value="高中" />
                  <el-option label="专科" value="专科" />
                  <el-option label="本科" value="本科" />
                  <el-option label="硕士" value="硕士" />
                  <el-option label="博士" value="博士" />
                </el-select>
              </el-form-item>
              
              <el-form-item label="地址">
                <el-input v-model="profileForm.address" placeholder="请输入地址" />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="handleSaveProfile" :loading="saving">保存修改</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="修改密码" name="password">
          <div class="profile-section">
            <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px" class="profile-form">
              <el-form-item label="当前密码" prop="oldPassword">
                <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" show-password />
              </el-form-item>
              
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
              </el-form-item>
              
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">修改密码</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled, Camera } from '@element-plus/icons-vue'
import { useAdminStore } from '@/admin/stores/admin'
import { UpdateAdminProfile } from '@/admin/api/admin'
import { UpdatePassword } from '@/api/Users'

const adminStore = useAdminStore()
const adminInfo = computed(() => adminStore.adminInfo)

const activeTab = ref('info')
const saving = ref(false)
const changingPassword = ref(false)
const passwordFormRef = ref(null)

const uploadUrl = 'https://oss-xsj.com/upload'
const uploadHeaders = {
  Authorization: localStorage.getItem('adminToken')
}

const profileForm = reactive({
  avatar: '',
  realName: '',
  nickName: '',
  email: '',
  gender: 0,
  school: '',
  leavel: '',
  address: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const cleanUrl = (url) => {
  if (!url) return ''
  return url.replace(/`/g, '').trim()
}

const initProfileForm = () => {
  if (adminInfo.value) {
    profileForm.avatar = adminInfo.value.avatar || ''
    profileForm.realName = adminInfo.value.realName || ''
    profileForm.nickName = adminInfo.value.nickName || ''
    profileForm.email = adminInfo.value.email || ''
    profileForm.gender = adminInfo.value.gender || 0
    profileForm.school = adminInfo.value.school || ''
    profileForm.leavel = adminInfo.value.leavel || ''
    profileForm.address = adminInfo.value.address || ''
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleAvatarSuccess = (response) => {
  if (response.code === 0 && response.data) {
    profileForm.avatar = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

const handleSaveProfile = async () => {
  saving.value = true
  try {
    const updateData = {}
    
    if (profileForm.avatar && profileForm.avatar !== adminInfo.value.avatar) {
      updateData.avatar = cleanUrl(profileForm.avatar)
    }
    if (profileForm.realName && profileForm.realName !== adminInfo.value.realName) {
      updateData.realName = profileForm.realName
    }
    if (profileForm.nickName && profileForm.nickName !== adminInfo.value.nickName) {
      updateData.nickName = profileForm.nickName
    }
    if (profileForm.email && profileForm.email !== adminInfo.value.email) {
      updateData.email = profileForm.email
    }
    if (profileForm.gender !== adminInfo.value.gender) {
      updateData.gender = profileForm.gender
    }
    if (profileForm.school && profileForm.school !== adminInfo.value.school) {
      updateData.school = profileForm.school
    }
    if (profileForm.leavel && profileForm.leavel !== adminInfo.value.leavel) {
      updateData.leavel = profileForm.leavel
    }
    if (profileForm.address && profileForm.address !== adminInfo.value.address) {
      updateData.address = profileForm.address
    }
    
    if (Object.keys(updateData).length === 0) {
      ElMessage.info('没有需要修改的内容')
      return
    }
    
    const res = await UpdateAdminProfile(updateData)
    if (res.code === 0) {
      ElMessage.success('保存成功')
      const newInfo = { ...adminInfo.value, ...updateData }
      adminStore.setAdminInfo(newInfo)
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    changingPassword.value = true
    
    const res = await UpdatePassword(passwordForm.oldPassword, passwordForm.newPassword)
    
    if (res.code === 0) {
      ElMessage.success('密码修改成功')
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    } else {
      ElMessage.error(res.data || res.message || '密码修改失败')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error(typeof error === 'string' ? error : (error.response?.data?.data || error.response?.data?.message || '密码修改失败'))
  } finally {
    changingPassword.value = false
  }
}

onMounted(() => {
  initProfileForm()
})
</script>

<style scoped>
.profile-page {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e1e2d;
  margin: 0;
}

.profile-content {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.profile-tabs {
  min-height: 500px;
}

.profile-section {
  padding: 20px 0;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40px;
  gap: 12px;
}

.avatar-uploader {
  position: relative;
  cursor: pointer;
}

.avatar-uploader :deep(.el-upload) {
  position: relative;
  border-radius: 50%;
  overflow: hidden;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.3s;
  border-radius: 50%;
  color: #fff;
  font-size: 12px;
}

.avatar-uploader:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay .el-icon {
  font-size: 20px;
}

.avatar-tip {
  font-size: 12px;
  color: #999;
}

.profile-form {
  max-width: 600px;
  margin: 0 auto;
}
</style>
