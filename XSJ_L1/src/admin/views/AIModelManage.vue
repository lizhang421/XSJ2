<template>
  <div class="ai-model-manage">
    <div class="page-header">
      <h2>AI模型管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加模型
      </el-button>
    </div>
    
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="模型名称">
          <el-input 
            v-model="searchForm.modelName" 
            placeholder="支持模糊搜索" 
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        
        <el-form-item label="模型标识">
          <el-input 
            v-model="searchForm.modelCode" 
            placeholder="精确匹配" 
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-select 
            v-model="searchForm.status" 
            placeholder="全部"
            clearable
            style="width: 200px"
            @change="handleSearch"
          >
            <el-option label="全部" :value="null" />
            <el-option label="使用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="简介">
          <el-input 
            v-model="searchForm.introduction" 
            placeholder="支持模糊搜索" 
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <div class="model-list">
      <el-table :data="modelList" style="width: 100%" v-loading="loading">
        <el-table-column prop="modelName" label="模型名称" width="180" />
        <el-table-column prop="modelCode" label="模型标识" width="150" />
        <el-table-column prop="model" label="模型参数" width="150" />
        <el-table-column prop="baseUrl" label="API地址" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '使用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'" 
              size="small" 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '使用' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="模型名称" prop="modelName">
          <el-input v-model="formData.modelName" placeholder="如：通义千问-增强版" />
        </el-form-item>
        
        <el-form-item label="模型标识" prop="modelCode">
          <el-input v-model="formData.modelCode" placeholder="如：qwen-plus" />
        </el-form-item>
        
        <el-form-item label="模型参数" prop="model">
          <div class="form-item-content">
            <el-input v-model="formData.model" placeholder="必须与AI平台模型名称完全一致，如：qwen-plus、doubao-pro-32k" />
            <div class="form-item-tip">
              <el-icon><el-icon-warning /></el-icon>
              <span>⚠️ 重要：此字段必须与AI平台的模型名称完全一致，否则API调用会失败</span>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="API密钥" prop="apiKey">
          <el-input v-model="formData.apiKey" placeholder="如：sk-7d99f8b407354665816eab1507c68b19" show-password />
        </el-form-item>
        
        <el-form-item label="API地址" prop="baseUrl">
          <el-input v-model="formData.baseUrl" placeholder="如：https://dashscope.aliyuncs.com/..." />
        </el-form-item>
        
        <el-form-item label="模型图片" prop="image">
          <div class="image-upload">
            <img v-if="formData.image" :src="formData.image" class="image-preview" />
            <div v-else class="image-placeholder">暂无图片</div>
            <el-input v-model="formData.image" placeholder="请输入图片URL" style="margin-top: 10px;" />
          </div>
        </el-form-item>
        
        <el-form-item label="模型简介" prop="introduction">
          <el-input 
            v-model="formData.introduction" 
            type="textarea" 
            :rows="3"
            placeholder="请输入模型简介"
          />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { GetAIModelList, AddAIModel, UpdateAIModel, DeleteAIModel } from '@/admin/api/admin'

const loading = ref(false)
const submitting = ref(false)
const modelList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加模型')
const formRef = ref(null)

const searchForm = ref({
  modelName: '',
  modelCode: '',
  status: null,
  introduction: ''
})

const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = ref({
  aiConfigId: '',
  modelName: '',
  modelCode: '',
  model: '',
  apiKey: '',
  baseUrl: '',
  image: '',
  introduction: '',
  status: 1
})

const rules = {
  modelName: [
    { required: true, message: '请输入模型名称', trigger: 'blur' }
  ],
  modelCode: [
    { required: true, message: '请输入模型标识', trigger: 'blur' }
  ],
  model: [
    { required: true, message: '请输入模型参数，必须与AI平台模型名称完全一致', trigger: 'blur' }
  ],
  apiKey: [
    { required: true, message: '请输入API密钥', trigger: 'blur' }
  ],
  baseUrl: [
    { required: true, message: '请输入API地址', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const loadModelList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize
    }
    
    if (searchForm.value.modelName) {
      params.modelName = searchForm.value.modelName
    }
    if (searchForm.value.modelCode) {
      params.modelCode = searchForm.value.modelCode
    }
    if (searchForm.value.status !== null && searchForm.value.status !== '') {
      params.status = searchForm.value.status
    }
    if (searchForm.value.introduction) {
      params.introduction = searchForm.value.introduction
    }
    
    const result = await GetAIModelList(params)
    if (result.code === 0) {
      modelList.value = result.data?.list || result.data || []
      pagination.value.total = result.data?.total || 0
    } else {
      ElMessage.error(result.message || '获取模型列表失败')
    }
  } catch (error) {
    console.error('获取模型列表失败:', error)
    ElMessage.error('获取模型列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.pageNum = 1
  loadModelList()
}

const handleReset = () => {
  searchForm.value = {
    modelName: '',
    modelCode: '',
    status: null,
    introduction: ''
  }
  pagination.value.pageNum = 1
  loadModelList()
}

const handleSizeChange = (val) => {
  pagination.value.pageSize = val
  loadModelList()
}

const handleCurrentChange = (val) => {
  pagination.value.pageNum = val
  loadModelList()
}

const handleAdd = () => {
  dialogTitle.value = '添加模型'
  formData.value = {
    aiConfigId: '',
    modelName: '',
    modelCode: '',
    model: '',
    apiKey: '',
    baseUrl: '',
    image: '',
    introduction: '',
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑模型'
  formData.value = {
    aiConfigId: row.aiConfigId,
    modelName: row.modelName,
    modelCode: row.modelCode,
    model: row.model,
    apiKey: row.apiKey,
    baseUrl: row.baseUrl,
    image: row.image,
    introduction: row.introduction,
    status: row.status
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        let result
        if (formData.value.aiConfigId) {
          // 编辑模式：只传必要的字段
          const updateData = {
            aiConfigId: formData.value.aiConfigId,
            modelCode: formData.value.modelCode,
            modelName: formData.value.modelName,
            apiKey: formData.value.apiKey,
            baseUrl: formData.value.baseUrl,
            model: formData.value.model,
            status: formData.value.status,
            image: formData.value.image,
            introduction: formData.value.introduction
          }
          result = await UpdateAIModel(updateData)
        } else {
          // 添加模式：传所有字段
          result = await AddAIModel(formData.value)
        }
        
        if (result.code === 0) {
          ElMessage.success(formData.value.aiConfigId ? '修改成功' : '添加成功')
          dialogVisible.value = false
          loadModelList()
        } else {
          ElMessage.error(result.message || '操作失败')
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '使用' : '禁用'
  
  try {
    await ElMessageBox.confirm(`确定要${action}该模型吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用更新接口
    const updateData = {
      aiConfigId: row.aiConfigId,
      modelCode: row.modelCode,
      modelName: row.modelName,
      apiKey: row.apiKey,
      baseUrl: row.baseUrl,
      model: row.model,
      status: newStatus,
      image: row.image,
      introduction: row.introduction
    }
    
    const result = await UpdateAIModel(updateData)
    if (result.code === 0) {
      ElMessage.success(`${action}成功`)
      loadModelList()
    } else {
      ElMessage.error(result.message || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该模型吗？删除后无法恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const result = await DeleteAIModel(row.aiConfigId, 0)
    if (result.code === 0) {
      ElMessage.success('删除成功')
      loadModelList()
    } else {
      ElMessage.error(result.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadModelList()
})
</script>

<style scoped>
.ai-model-manage {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.search-form .el-select {
  width: 200px;
}

.search-form .el-input {
  width: 200px;
}

.model-list {
  margin-top: 20px;
}

.pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.image-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.image-preview {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.image-placeholder {
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 8px;
  border: 1px dashed #d0d0d0;
  color: #999;
  font-size: 14px;
}

.form-item-content {
  width: 100%;
}

.form-item-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  padding: 8px 12px;
  background: #fff3e0;
  border-left: 3px solid #ff9800;
  border-radius: 4px;
  font-size: 13px;
  color: #e65100;
}

.form-item-tip .el-icon {
  font-size: 16px;
  color: #ff9800;
}
</style>
