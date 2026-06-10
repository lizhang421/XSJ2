<template>
  <div class="system-resource-config">
    <div class="page-header">
      <h1 class="page-title">系统资源配置</h1>
      <p class="page-desc">管理系统首页各类资源图片和链接配置，资源数据固定，仅支持修改</p>
    </div>

    <div class="filter-bar">
      <el-select v-model="filterResourceAddress" placeholder="资源位置筛选" clearable style="width: 200px">
        <el-option label="全部" value="" />
        <el-option v-for="item in resourceAddressOptions" :key="item" :label="item" :value="item" />
      </el-select>
      <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 120px; margin-left: 10px">
        <el-option label="全部" value="" />
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-input 
        v-model="searchKey" 
        placeholder="搜索资源ID/文件名" 
        clearable 
        style="width: 200px; margin-left: 10px"
      />
    </div>

    <div class="table-container" v-loading="loading">
      <el-table 
        :data="filteredData" 
        stripe 
        border 
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: '600' }"
      >
        <el-table-column prop="homeResourcesId" label="资源ID" width="150" fixed />
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 'image' ? 'primary' : 'success'" size="small">
              {{ row.type === 'image' ? '图片' : '视频' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileName" label="文件名" min-width="150" show-overflow-tooltip />
        <el-table-column label="资源预览" width="100">
          <template #default="{ row }">
            <div class="table-preview" @click="handlePreview(row)">
              <img 
                v-if="row.type === 'image'"
                :src="row.address" 
                class="preview-thumb"
                @error="handleImageError($event)"
              />
              <div v-else class="thumb-video">
                <el-icon><VideoPlay /></el-icon>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="resourceAddress" label="资源位置" width="140" show-overflow-tooltip />
        <el-table-column prop="jumUrl" label="跳转链接" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.jumUrl" class="link-text" @click="openUrl(row.jumUrl)">
              {{ row.jumUrl }}
            </span>
            <span v-else class="no-link">-</span>
          </template>
        </el-table-column>
        <el-table-column label="尺寸" width="100">
          <template #default="{ row }">
            <span v-if="row.longer && row.wide">{{ row.longer }}×{{ row.wide }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="isUse" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isUse === 1 ? 'success' : 'danger'" size="small">
              {{ row.isUse === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="170">
          <template #default="{ row }">
            {{ formatDate(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button type="warning" link size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="filteredData.length"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </div>

    <el-dialog 
      v-model="detailDialogVisible" 
      :title="`资源详情 - ${detailData?.homeResourcesId || ''}`" 
      width="800px"
      destroy-on-close
    >
      <div class="detail-container" v-if="detailData">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="detail-preview-section">
              <h4 class="section-title">资源预览</h4>
              <div class="preview-large">
                <img 
                  v-if="detailData.type === 'image'"
                  :src="detailData.address" 
                  class="large-image"
                  @click="openPreviewImage(detailData.address)"
                  @error="handleImageError($event)"
                />
                <video 
                  v-else 
                  :src="detailData.address" 
                  controls 
                  class="large-video"
                />
              </div>
              <p class="preview-tip" v-if="detailData.type === 'image'">点击图片可放大查看</p>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-info-section">
              <h4 class="section-title">基本信息</h4>
              <el-descriptions :column="1" border size="small">
                <el-descriptions-item label="资源ID">{{ detailData.homeResourcesId }}</el-descriptions-item>
                <el-descriptions-item label="资源类型">
                  <el-tag :type="detailData.type === 'image' ? 'primary' : 'success'" size="small">
                    {{ detailData.type === 'image' ? '图片' : '视频' }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="文件名">{{ detailData.fileName }}</el-descriptions-item>
                <el-descriptions-item label="资源位置">{{ detailData.resourceAddress }}</el-descriptions-item>
                <el-descriptions-item label="启用状态">
                  <el-tag :type="detailData.isUse === 1 ? 'success' : 'danger'" size="small">
                    {{ detailData.isUse === 1 ? '启用' : '禁用' }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="尺寸" v-if="detailData.longer || detailData.wide">
                  {{ detailData.longer || 0 }} × {{ detailData.wide || 0 }} px
                </el-descriptions-item>
              </el-descriptions>

              <h4 class="section-title" style="margin-top: 20px">链接信息</h4>
              <el-descriptions :column="1" border size="small">
                <el-descriptions-item label="资源地址">
                  <div class="address-text">
                    <span class="address-content">{{ detailData.address }}</span>
                    <el-button type="primary" link size="small" @click="copyToClipboard(detailData.address)">
                      复制
                    </el-button>
                  </div>
                </el-descriptions-item>
                <el-descriptions-item label="跳转链接">
                  <div v-if="detailData.jumUrl" class="address-text">
                    <span class="address-content">{{ detailData.jumUrl }}</span>
                    <el-button type="primary" link size="small" @click="copyToClipboard(detailData.jumUrl)">
                      复制
                    </el-button>
                  </div>
                  <span v-else class="no-link">无</span>
                </el-descriptions-item>
              </el-descriptions>

              <h4 class="section-title" style="margin-top: 20px">其他信息</h4>
              <el-descriptions :column="1" border size="small">
                <el-descriptions-item label="上传人">{{ detailData.uploadPeople }}</el-descriptions-item>
                <el-descriptions-item label="上传时间">{{ formatDate(detailData.uploadTime) }}</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{ formatDate(detailData.updateTime) }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-dialog>

    <el-dialog 
      v-model="editDialogVisible" 
      title="编辑资源配置" 
      width="550px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form :model="editForm" label-width="100px" v-if="editForm">
        <el-form-item label="资源ID">
          <el-input v-model="editForm.homeResourcesId" disabled />
        </el-form-item>
        <el-form-item label="资源类型">
          <el-input v-model="editForm.type" disabled />
        </el-form-item>
        <el-form-item label="资源位置">
          <el-input v-model="editForm.resourceAddress" disabled />
        </el-form-item>
        <el-form-item label="文件名">
          <el-input v-model="editForm.fileName" placeholder="请输入文件名/描述" />
        </el-form-item>
        <el-form-item label="资源地址">
          <div class="upload-section">
            <el-input 
              v-model="editForm.address" 
              placeholder="请输入资源地址或上传文件"
              class="url-input"
            >
              <template #append>
                <el-upload
                  :show-file-list="false"
                  :before-upload="(file) => beforeUpload(file, editForm.type)"
                  :http-request="handleUpload"
                  accept="image/*,video/*"
                >
                  <el-button :loading="uploading">
                    <el-icon v-if="!uploading"><Upload /></el-icon>
                    {{ uploading ? '上传中...' : '上传' }}
                  </el-button>
                </el-upload>
              </template>
            </el-input>
            <el-progress 
              v-if="uploadProgress > 0 && uploadProgress < 100" 
              :percentage="uploadProgress" 
              :stroke-width="3"
              style="margin-top: 8px"
            />
          </div>
        </el-form-item>
        <el-form-item label="当前预览" v-if="editForm.address">
          <div class="current-preview">
            <img 
              v-if="editForm.type === 'image'"
              :src="editForm.address" 
              class="edit-preview-img"
              @error="handleImageError($event)"
            />
            <video v-else :src="editForm.address" class="edit-preview-video" controls />
          </div>
        </el-form-item>
        <el-form-item label="跳转链接" v-if="editForm.resourceAddress?.includes('轮播')">
          <el-input v-model="editForm.jumUrl" placeholder="请输入跳转链接（可选）" clearable />
        </el-form-item>
        <el-form-item label="尺寸">
          <el-row :gutter="10">
            <el-col :span="12">
              <el-input v-model.number="editForm.longer" placeholder="长度" type="number">
                <template #append>px</template>
              </el-input>
            </el-col>
            <el-col :span="12">
              <el-input v-model.number="editForm.wide" placeholder="宽度" type="number">
                <template #append>px</template>
              </el-input>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="启用状态">
          <el-switch 
            v-model="editForm.isUse" 
            :active-value="1" 
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存修改</el-button>
      </template>
    </el-dialog>

    <el-dialog 
      v-model="previewDialogVisible" 
      title="资源预览" 
      width="90%"
      top="5vh"
      destroy-on-close
    >
      <div class="preview-dialog-content" v-if="previewUrl">
        <img 
          v-if="isImageType(previewUrl)"
          :src="previewUrl" 
          class="preview-full"
          @error="handleImageError($event)"
        />
        <video v-else :src="previewUrl" controls class="preview-full" />
      </div>
    </el-dialog>

    <el-dialog 
      v-model="imagePreviewVisible" 
      title="图片预览" 
      width="90%"
      top="5vh"
      destroy-on-close
    >
      <div class="image-preview-container">
        <img :src="imagePreviewUrl" class="full-image" @error="handleImageError($event)" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Picture, VideoPlay, View, Edit, Upload } from '@element-plus/icons-vue'
import { GetSysResourceConfig, UpdateSysResource } from '@/admin/api/admin'
import { uploadToOSS } from '@/utils/oss'

const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const uploadProgress = ref(0)
const resourceList = ref([])

const filterResourceAddress = ref('')
const filterStatus = ref('')
const searchKey = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

const detailDialogVisible = ref(false)
const detailData = ref(null)

const editDialogVisible = ref(false)
const editForm = ref(null)

const previewDialogVisible = ref(false)
const previewUrl = ref('')

const imagePreviewVisible = ref(false)
const imagePreviewUrl = ref('')

const pendingFile = ref(null)
const pendingFileType = ref('image')

const resourceAddressOptions = computed(() => {
  const options = new Set()
  resourceList.value.forEach(item => {
    if (item.resourceAddress) {
      options.add(item.resourceAddress)
    }
  })
  return Array.from(options)
})

const filteredData = computed(() => {
  let data = [...resourceList.value]
  
  if (filterResourceAddress.value) {
    data = data.filter(item => item.resourceAddress === filterResourceAddress.value)
  }
  
  if (filterStatus.value !== '' && filterStatus.value !== null) {
    data = data.filter(item => item.isUse === filterStatus.value)
  }
  
  if (searchKey.value) {
    const key = searchKey.value.toLowerCase()
    data = data.filter(item => 
      item.homeResourcesId?.toLowerCase().includes(key) ||
      item.fileName?.toLowerCase().includes(key)
    )
  }
  
  return data
})

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const isImageType = (url) => {
  if (!url) return true
  const imageExts = ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.bmp', '.svg']
  const lowerUrl = url.toLowerCase()
  return imageExts.some(ext => lowerUrl.includes(ext)) || !lowerUrl.match(/\.(mp4|webm|avi|mov)$/i)
}

const handleImageError = (e) => {
  e.target.src = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiB2aWV3Qm94PSIwIDAgMTAwIDEwMCI+PHJlY3Qgd2lkdGg9IjEwMCIgaGVpZ2h0PSIxMDAiIGZpbGw9IiNmNWY3ZmEiLz48dGV4dCB4PSI1MCIgeT0iNTAiIGZpbGw9IiM5MDkzOTkiIGZvbnQtc2l6ZT0iMTIiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj7lm77niYfliqDovb3lpLHotKU8L3RleHQ+PC9zdmc+'
}

const cleanUrl = (url) => {
  if (!url) return ''
  return url.replace(/`/g, '').replace(/\r?\n/g, '').trim()
}

const fetchResourceConfig = async () => {
  loading.value = true
  try {
    const res = await GetSysResourceConfig()
    if (res.code === 0) {
      resourceList.value = (res.data || []).map(item => ({
        ...item,
        address: cleanUrl(item.address),
        jumUrl: cleanUrl(item.jumUrl)
      }))
    } else {
      ElMessage.error(res.message || '获取资源配置失败')
    }
  } catch (error) {
    console.error('获取资源配置失败:', error)
    ElMessage.error('获取资源配置失败')
  } finally {
    loading.value = false
  }
}

const handlePreview = (row) => {
  previewUrl.value = cleanUrl(row.address)
  previewDialogVisible.value = true
}

const openPreviewImage = (url) => {
  imagePreviewUrl.value = cleanUrl(url)
  imagePreviewVisible.value = true
}

const handleDetail = (row) => {
  detailData.value = {
    ...row,
    address: cleanUrl(row.address),
    jumUrl: cleanUrl(row.jumUrl)
  }
  detailDialogVisible.value = true
}

const handleEdit = (row) => {
  editForm.value = { 
    ...row,
    address: cleanUrl(row.address),
    jumUrl: cleanUrl(row.jumUrl)
  }
  uploadProgress.value = 0
  editDialogVisible.value = true
}

const beforeUpload = (file, type) => {
  pendingFileType.value = type || 'image'
  const isImage = file.type.startsWith('image/')
  const isVideo = file.type.startsWith('video/')
  
  if (!isImage && !isVideo) {
    ElMessage.error('只能上传图片或视频文件!')
    return false
  }
  
  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isLt50M) {
    ElMessage.error('文件大小不能超过 50MB!')
    return false
  }
  
  return true
}

const handleUpload = async (options) => {
  const file = options.file
  uploading.value = true
  uploadProgress.value = 0
  
  try {
    const result = await uploadToOSS(file, pendingFileType.value, (progress) => {
      uploadProgress.value = progress
    })
    
    if (result.success) {
      editForm.value.address = result.url
      ElMessage.success('上传成功!')
    } else {
      ElMessage.error(result.error || '上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
    uploadProgress.value = 0
  }
}

const handleSave = async () => {
  if (!editForm.value.address) {
    ElMessage.warning('请输入或上传资源地址')
    return
  }
  
  saving.value = true
  try {
    const res = await UpdateSysResource(editForm.value.homeResourcesId, editForm.value.address)
    if (res.code === 0) {
      ElMessage.success('保存成功')
      editDialogVisible.value = false
      fetchResourceConfig()
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

const openUrl = (url) => {
  const cleanLink = cleanUrl(url)
  if (cleanLink) {
    window.open(cleanLink, '_blank')
  }
}

const copyToClipboard = (text) => {
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

onMounted(() => {
  fetchResourceConfig()
})
</script>

<style scoped>
.system-resource-config {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.page-desc {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.filter-bar {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.table-container {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.table-preview {
  width: 60px;
  height: 40px;
  cursor: pointer;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #EBEEF5;
}

.preview-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumb-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
}

.thumb-video {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #409EFF;
  font-size: 20px;
}

.link-text {
  color: #409EFF;
  cursor: pointer;
  word-break: break-all;
}

.link-text:hover {
  text-decoration: underline;
}

.no-link {
  color: #909399;
}

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.detail-container {
  padding: 10px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px 0;
  padding-left: 10px;
  border-left: 3px solid #409EFF;
}

.detail-preview-section {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
}

.preview-large {
  width: 100%;
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.large-image {
  max-width: 100%;
  max-height: 300px;
  cursor: pointer;
  transition: transform 0.3s;
}

.large-image:hover {
  transform: scale(1.02);
}

.large-video {
  max-width: 100%;
  max-height: 300px;
}

.preview-tip {
  text-align: center;
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.detail-info-section {
  max-height: 400px;
  overflow-y: auto;
}

.address-text {
  word-break: break-all;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.address-content {
  flex: 1;
  word-break: break-all;
}

.upload-section {
  width: 100%;
}

.url-input {
  width: 100%;
}

.current-preview {
  width: 100%;
  max-height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
}

.edit-preview-img {
  max-width: 100%;
  max-height: 150px;
  object-fit: contain;
}

.edit-preview-video {
  max-width: 100%;
  max-height: 150px;
}

.preview-dialog-content {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.preview-full {
  max-width: 100%;
  max-height: 70vh;
}

.image-preview-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.full-image {
  max-width: 100%;
  max-height: 80vh;
  object-fit: contain;
}
</style>
