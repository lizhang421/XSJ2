<template>
  <div class="report-manage">
    <div class="page-header">
      <h1 class="page-title">举报管理</h1>
      <div class="header-actions">
        <el-tag type="danger" size="large">待处理: {{ pendingCount }}</el-tag>
      </div>
    </div>
    
    <div class="filter-bar">
      <el-select v-model="filterStatus" placeholder="处理状态" clearable style="width: 150px" @change="handleFilterChange">
        <el-option label="待审核" :value="3" />
        <el-option label="举报通过" :value="1" />
        <el-option label="举报不通过" :value="0" />
      </el-select>
      
      <el-select v-model="filterType" placeholder="举报类型" clearable style="width: 150px" @change="handleFilterChange">
        <el-option label="视频举报" :value="1" />
        <el-option label="评论举报" :value="2" />
      </el-select>
      
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        style="width: 260px"
        @change="handleFilterChange"
      />
      
      <el-button @click="resetFilters">重置</el-button>
    </div>
    
    <div class="table-container">
      <el-table :data="reportList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="举报ID" width="280" show-overflow-tooltip />
        <el-table-column label="举报类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.reportType === 1 ? 'primary' : 'warning'">
              {{ row.reportType === 1 ? '视频' : '评论' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="举报内容" min-width="250">
          <template #default="{ row }">
            <div class="report-content-cell">
              <template v-if="row.reportType === 1">
                <div class="video-info" v-if="row.videoName">
                  <img :src="formatUrl(row.videoImageUrl)" class="video-thumb" v-if="row.videoImageUrl" />
                  <span class="video-name">{{ row.videoName }}</span>
                </div>
                <span v-else>视频ID: {{ row.targetId }}</span>
              </template>
              <template v-else>
                <div class="comment-info" v-if="row.commentContent">
                  <el-icon><ChatDotRound /></el-icon>
                  <span>{{ row.commentContent }}</span>
                </div>
                <span v-else>评论ID: {{ row.targetId }}</span>
              </template>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="举报原因" width="150">
          <template #default="{ row }">
            <el-tooltip :content="row.remark" placement="top" v-if="row.remark">
              <span class="reason-text">{{ row.reason }}</span>
            </el-tooltip>
            <span v-else>{{ row.reason }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="举报时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
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
            <el-button type="primary" link @click="viewReportDetail(row)">详情</el-button>
            <template v-if="row.status === 3">
              <el-button type="success" link @click="handleReport(row, 1)">通过</el-button>
              <el-button type="danger" link @click="handleReport(row, 0)">不通过</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="totalReports"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
    
    <el-dialog v-model="detailDialogVisible" title="举报详情" width="600px">
      <div class="detail-content" v-if="currentReport">
        <div class="detail-item">
          <span class="label">举报ID：</span>
          <span class="value">{{ currentReport.id }}</span>
        </div>
        <div class="detail-item">
          <span class="label">举报类型：</span>
          <el-tag :type="currentReport.reportType === 1 ? 'primary' : 'warning'">
            {{ currentReport.reportType === 1 ? '视频举报' : '评论举报' }}
          </el-tag>
        </div>
        <div class="detail-item" v-if="currentReport.reportType === 1 && currentReport.videoName">
          <span class="label">视频信息：</span>
          <div class="video-detail">
            <img :src="formatUrl(currentReport.videoImageUrl)" class="video-thumb-large" v-if="currentReport.videoImageUrl" />
            <div>
              <p class="video-title">{{ currentReport.videoName }}</p>
              <p class="video-encode" v-if="currentReport.enCode">编码: {{ currentReport.enCode }}</p>
            </div>
          </div>
        </div>
        <div class="detail-item" v-if="currentReport.reportType === 2 && currentReport.commentContent">
          <span class="label">评论内容：</span>
          <div class="comment-detail">{{ currentReport.commentContent }}</div>
        </div>
        <div class="detail-item">
          <span class="label">举报原因：</span>
          <span class="value">{{ currentReport.reason }}</span>
        </div>
        <div class="detail-item" v-if="currentReport.remark">
          <span class="label">备注说明：</span>
          <span class="value">{{ currentReport.remark }}</span>
        </div>
        <div class="detail-item">
          <span class="label">举报时间：</span>
          <span class="value">{{ formatTime(currentReport.createTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">处理状态：</span>
          <el-tag :type="getStatusType(currentReport.status)">
            {{ getStatusText(currentReport.status) }}
          </el-tag>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <template v-if="currentReport && currentReport.status === 3">
          <el-button type="success" @click="handleReportFromDetail(1)">通过举报</el-button>
          <el-button type="danger" @click="handleReportFromDetail(0)">不通过</el-button>
        </template>
      </template>
    </el-dialog>
    
    <el-dialog v-model="handleDialogVisible" title="处理举报" width="500px">
      <div class="handle-info">
        <p><strong>举报类型：</strong>{{ currentReport?.reportType === 1 ? '视频举报' : '评论举报' }}</p>
        <p><strong>举报原因：</strong>{{ currentReport?.reason }}</p>
      </div>
      <el-divider />
      <el-form :model="handleForm" label-width="100px">
        <el-form-item label="处理结果">
          <el-radio-group v-model="handleForm.jieguo">
            <el-radio :value="1">通过举报</el-radio>
            <el-radio :value="0">举报不通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处罚措施" v-if="handleForm.jieguo === 1">
          <el-select v-model="handleForm.solvechufa" placeholder="请选择处罚措施（可选）" style="width: 100%">
            <el-option :value="0" label="不处罚" />
            <el-option :value="1" label="禁言" />
            <el-option :value="2" label="禁止发布视频" />
            <el-option :value="3" label="禁止登录" />
          </el-select>
          <div class="chufa-tip" v-if="handleForm.solvechufa > 0">
            <el-icon><Warning /></el-icon>
            <span>处罚时间将由系统自动计算</span>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle" :loading="submitting">确认提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Warning } from '@element-plus/icons-vue'
import { GetJubaoList, AuditJubao } from '@/admin/api/admin'

const loading = ref(false)
const submitting = ref(false)
const filterStatus = ref(null)
const filterType = ref(null)
const dateRange = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const totalReports = ref(0)

const reportList = ref([])

const detailDialogVisible = ref(false)
const handleDialogVisible = ref(false)
const currentReport = ref(null)
const handleForm = reactive({
  jieguo: 1,
  solvechufa: 0
})

const pendingCount = computed(() => {
  return reportList.value.filter(item => item.status === 3).length
})

const formatUrl = (url) => {
  if (!url) return ''
  return url.replace(/[\r\n`]/g, '').trim()
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

const getStatusType = (status) => {
  const typeMap = {
    3: 'warning',
    1: 'success',
    0: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    3: '待审核',
    1: '举报通过',
    0: '举报不通过'
  }
  return textMap[status] || '未知'
}

const fetchReportList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    
    if (filterStatus.value !== null && filterStatus.value !== undefined && filterStatus.value !== '') {
      params.status = filterStatus.value
    }
    if (filterType.value !== null && filterType.value !== undefined && filterType.value !== '') {
      params.type = filterType.value
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0] + ' 00:00:00'
      params.endTime = dateRange.value[1] + ' 23:59:59'
    }
    
    const res = await GetJubaoList(params)
    if (res.code === 0 && res.data) {
      const list = Array.isArray(res.data) ? res.data : (res.data.records || res.data.list || [])
      reportList.value = list.map(item => ({
        ...item,
        videoImageUrl: formatUrl(item.videoImageUrl)
      }))
      totalReports.value = res.data.total || list.length
    }
  } catch (error) {
    console.error('获取举报列表失败:', error)
    ElMessage.error('获取举报列表失败')
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  currentPage.value = 1
  fetchReportList()
}

const handleSearch = () => {
  currentPage.value = 1
  fetchReportList()
}

const resetFilters = () => {
  filterStatus.value = null
  filterType.value = null
  dateRange.value = null
  currentPage.value = 1
  fetchReportList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  fetchReportList()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchReportList()
}

const viewReportDetail = (report) => {
  currentReport.value = report
  detailDialogVisible.value = true
}

const handleReport = (report, jieguo) => {
  currentReport.value = report
  handleForm.jieguo = jieguo
  handleForm.solvechufa = 0
  handleDialogVisible.value = true
}

const handleReportFromDetail = (jieguo) => {
  detailDialogVisible.value = false
  handleForm.jieguo = jieguo
  handleForm.solvechufa = 0
  handleDialogVisible.value = true
}

const submitHandle = async () => {
  if (!currentReport.value) return
  
  const jieguoText = handleForm.jieguo === 1 ? '通过' : '驳回'
  const chufaText = handleForm.jieguo === 1 && handleForm.solvechufa > 0 
    ? `并处罚：${getChufaText(handleForm.solvechufa)}` 
    : ''
  
  try {
    await ElMessageBox.confirm(
      `确定要${jieguoText}该举报吗？${chufaText}`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    submitting.value = true
    const res = await AuditJubao(currentReport.value.id, handleForm.jieguo, handleForm.solvechufa)
    if (res.code === 0) {
      ElMessage.success('处理成功')
      handleDialogVisible.value = false
      fetchReportList()
    } else {
      ElMessage.error(res.message || '处理失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('处理举报失败:', error)
      ElMessage.error('处理失败')
    }
  } finally {
    submitting.value = false
  }
}

const getChufaText = (solvechufa) => {
  const textMap = {
    1: '禁言',
    2: '禁止发布视频',
    3: '禁止登录'
  }
  return textMap[solvechufa] || '无'
}

onMounted(() => {
  fetchReportList()
})
</script>

<style scoped>
.report-manage {
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
  flex-wrap: wrap;
}

.table-container {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.report-content-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.video-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.video-thumb {
  width: 60px;
  height: 34px;
  object-fit: cover;
  border-radius: 4px;
}

.video-name {
  color: #333;
  font-size: 13px;
}

.comment-info {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  color: #666;
  font-size: 13px;
}

.reason-text {
  color: #666;
}

.detail-content {
  padding: 10px 0;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
}

.detail-item .label {
  width: 100px;
  color: #999;
  flex-shrink: 0;
}

.detail-item .value {
  color: #333;
  flex: 1;
}

.video-detail {
  display: flex;
  gap: 12px;
}

.video-thumb-large {
  width: 120px;
  height: 68px;
  object-fit: cover;
  border-radius: 6px;
}

.video-title {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #333;
}

.video-encode {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.comment-detail {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 6px;
  color: #333;
  line-height: 1.6;
}

.handle-info {
  margin-bottom: 16px;
}

.handle-info p {
  margin: 8px 0;
  color: #666;
}

.chufa-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  padding: 8px 12px;
  background: #fff3e0;
  border-radius: 4px;
  font-size: 12px;
  color: #e6a23c;
}
</style>
