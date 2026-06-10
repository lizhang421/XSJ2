<template>
  <div class="dashboard-container" :class="{ 'fullscreen': isFullscreen }">
    <div class="dashboard-bg">
      <div class="bg-grid"></div>
      <div class="bg-particles">
        <div v-for="i in 60" :key="i" class="particle" :style="getParticleStyle(i)"></div>
      </div>
      <div class="scan-line"></div>
      <div class="corner-decoration top-left"></div>
      <div class="corner-decoration top-right"></div>
      <div class="corner-decoration bottom-left"></div>
      <div class="corner-decoration bottom-right"></div>
    </div>
    
    <div class="dashboard-header">
      <div class="header-left">
        <div class="logo-section">
          <div class="logo-ring">
            <span class="logo-icon">🛡️</span>
          </div>
          <div class="logo-text-group">
            <span class="logo-text">XSJ数据监控中心</span>
            <span class="logo-sub">DATA MONITORING CENTER</span>
          </div>
        </div>
      </div>
      <div class="header-center">
        <div class="main-title">
          <div class="title-bg"></div>
          <h1>新世界AI教育数据可视化平台</h1>
        </div>
      </div>
      <div class="header-right">
        <div class="datetime-panel">
          <div class="date">{{ currentDate }}</div>
          <div class="time">{{ currentTime }}</div>
        </div>
        <el-button class="fullscreen-btn" @click="toggleFullscreen">
          <el-icon><FullScreen v-if="!isFullscreen" /><Close v-else /></el-icon>
        </el-button>
      </div>
    </div>
    
    <div class="dashboard-body">
      <div class="dashboard-left">
        <div class="panel user-panel">
          <div class="panel-header">
            <div class="header-decoration"></div>
            <span class="panel-icon">👥</span>
            <span class="panel-title">用户数据统计</span>
          </div>
          <div class="panel-content">
            <div class="user-stats-grid">
              <div class="stat-box">
                <div class="stat-icon blue">📊</div>
                <div class="stat-info">
                  <div class="stat-number">{{ animatedNumbers.totalUsers }}</div>
                  <div class="stat-label">总用户数</div>
                </div>
                <div class="stat-trend up">+{{ dashboardData.userGrowth }}%</div>
              </div>
              <div class="stat-box">
                <div class="stat-icon green">🆕</div>
                <div class="stat-info">
                  <div class="stat-number">{{ animatedNumbers.todayUsers }}</div>
                  <div class="stat-label">今日新增</div>
                </div>
                <div class="stat-trend up">+{{ dashboardData.todayUserGrowth }}%</div>
              </div>
            </div>
            <div class="chart-container" ref="userGrowthChartRef"></div>
          </div>
        </div>
        
        <div class="panel">
          <div class="panel-header">
            <div class="header-decoration"></div>
            <span class="panel-icon">📊</span>
            <span class="panel-title">AI模型使用率</span>
          </div>
          <div class="panel-content ai-usage-panel">
            <div class="ai-usage-header">
              <div class="ai-model-count">
                <span class="count-value">{{ aiData.modelCount }}</span>
                <span class="count-label">模型数量</span>
              </div>
            </div>
            <div class="ai-usage-chart" ref="aiUsageChartRef"></div>
          </div>
        </div>
        
        <div class="panel">
          <div class="panel-header">
            <div class="header-decoration"></div>
            <span class="panel-icon">🤖</span>
            <span class="panel-title">AI数据统计</span>
          </div>
          <div class="panel-content">
            <div class="ai-stats-grid">
              <div class="ai-stat-box">
                <div class="ai-stat-icon">💬</div>
                <div class="ai-stat-info">
                  <div class="ai-stat-value">{{ formatNumber(aiData.commentCount) }}</div>
                  <div class="ai-stat-label">评论数量</div>
                </div>
              </div>
              <div class="ai-stat-box">
                <div class="ai-stat-icon">📁</div>
                <div class="ai-stat-info">
                  <div class="ai-stat-value">{{ aiData.totalFileCount }}</div>
                  <div class="ai-stat-label">文件总数</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="dashboard-center">
        <div class="center-top">
          <div class="panel main-panel">
            <div class="panel-header">
              <div class="header-decoration"></div>
              <span class="panel-icon">🎬</span>
              <span class="panel-title">视频数据统计</span>
            </div>
            <div class="panel-content">
              <div class="video-stats-row">
                <div class="video-stat-card">
                  <div class="video-stat-bg"></div>
                  <div class="video-stat-icon">🎥</div>
                  <div class="video-stat-content">
                    <div class="video-stat-value">{{ videoData.totalVideos }}</div>
                    <div class="video-stat-label">视频总数</div>
                  </div>
                  <div class="video-stat-ring">
                    <svg viewBox="0 0 100 100">
                      <circle cx="50" cy="50" r="45" class="ring-bg" />
                      <circle cx="50" cy="50" r="45" class="ring-progress" :style="{ strokeDashoffset: 283 - (283 * 85 / 100) }" />
                    </svg>
                  </div>
                </div>
                <div class="video-stat-card">
                  <div class="video-stat-bg green"></div>
                  <div class="video-stat-icon">✅</div>
                  <div class="video-stat-content">
                    <div class="video-stat-value">{{ videoData.passedVideos }}</div>
                    <div class="video-stat-label">已通过</div>
                  </div>
                  <div class="video-stat-ring">
                    <svg viewBox="0 0 100 100">
                      <circle cx="50" cy="50" r="45" class="ring-bg" />
                      <circle cx="50" cy="50" r="45" class="ring-progress green" :style="{ strokeDashoffset: 283 - (283 * videoData.passRate / 100) }" />
                    </svg>
                  </div>
                </div>
                <div class="video-stat-card">
                  <div class="video-stat-bg orange"></div>
                  <div class="video-stat-icon">⏳</div>
                  <div class="video-stat-content">
                    <div class="video-stat-value">{{ videoData.pendingVideos }}</div>
                    <div class="video-stat-label">待审核</div>
                  </div>
                  <div class="video-stat-ring">
                    <svg viewBox="0 0 100 100">
                      <circle cx="50" cy="50" r="45" class="ring-bg" />
                      <circle cx="50" cy="50" r="45" class="ring-progress orange" :style="{ strokeDashoffset: 283 - (283 * videoData.pendingRate / 100) }" />
                    </svg>
                  </div>
                </div>
                <div class="video-stat-card">
                  <div class="video-stat-bg purple"></div>
                  <div class="video-stat-icon">👁️</div>
                  <div class="video-stat-content">
                    <div class="video-stat-value">{{ formatNumber(videoData.totalViews) }}</div>
                    <div class="video-stat-label">总播放量</div>
                  </div>
                  <div class="video-stat-ring">
                    <svg viewBox="0 0 100 100">
                      <circle cx="50" cy="50" r="45" class="ring-bg" />
                      <circle cx="50" cy="50" r="45" class="ring-progress purple" :style="{ strokeDashoffset: 283 - (283 * 72 / 100) }" />
                    </svg>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="center-middle">
          <div class="panel">
            <div class="panel-header">
              <div class="header-decoration"></div>
              <span class="panel-icon">📈</span>
              <span class="panel-title">平台数据趋势</span>
            </div>
            <div class="panel-content">
              <div class="chart-container large" ref="trendChartRef"></div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="dashboard-right">
        <div class="panel">
          <div class="panel-header">
            <div class="header-decoration"></div>
            <span class="panel-icon">🎓</span>
            <span class="panel-title">用户学历分布</span>
          </div>
          <div class="panel-content">
            <div class="chart-container" ref="educationChartRef"></div>
          </div>
        </div>
        
        <div class="panel">
          <div class="panel-header">
            <div class="header-decoration"></div>
            <span class="panel-icon">🌍</span>
            <span class="panel-title">用户语言分布</span>
          </div>
          <div class="panel-content">
            <div class="chart-container" ref="languageChartRef"></div>
          </div>
        </div>
        
        <div class="panel">
          <div class="panel-header">
            <div class="header-decoration"></div>
            <span class="panel-icon">📂</span>
            <span class="panel-title">内容分类统计</span>
          </div>
          <div class="panel-content">
            <div class="category-list">
              <div class="category-item" v-for="(item, index) in categoryList" :key="index">
                <div class="category-icon" :style="{ background: item.color }">{{ item.icon }}</div>
                <div class="category-info">
                  <div class="category-name">{{ item.name }}</div>
                  <div class="category-bar">
                    <div class="category-progress" :style="{ width: item.percent + '%', background: item.color }"></div>
                  </div>
                </div>
                <div class="category-value">{{ item.count }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, computed, nextTick } from 'vue'
import * as echarts from 'echarts'
import { FullScreen, Close } from '@element-plus/icons-vue'
import { GetDataEase } from '@/admin/api/admin'

const isFullscreen = ref(false)
const userGrowthChartRef = ref(null)
const trendChartRef = ref(null)
const educationChartRef = ref(null)
const languageChartRef = ref(null)
const aiUsageChartRef = ref(null)

let userGrowthChart = null
let trendChart = null
let educationChart = null
let languageChart = null
let aiUsageChart = null
let timeInterval = null
let numberInterval = null

const currentDate = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
})

const currentTime = ref('')

const dashboardData = reactive({
  totalUsers: 0,
  userGrowth: 0,
  todayUsers: 0,
  todayUserGrowth: 0
})

const videoData = reactive({
  totalVideos: 0,
  passedVideos: 0,
  pendingVideos: 0,
  totalViews: 0,
  passRate: 0,
  pendingRate: 0
})

const aiData = reactive({
  modelCount: 0,
  commentCount: 0,
  totalFileCount: 0
})

const animatedNumbers = reactive({
  totalUsers: 0,
  todayUsers: 0
})

const userWeekGrowthData = ref([0, 0, 0, 0, 0, 0, 0])
const yearStatsData = ref({
  months: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
  userGrowth: [],
  videoUpload: [],
  viewCount: []
})
const aiUsageData = ref([])
const educationData = ref([])
const languageData = ref([])
const categoryList = ref([])

const fetchDashboardData = async () => {
  try {
    const res = await GetDataEase()
    if (res.code === 0 && res.data) {
      const data = res.data
      
      if (data.userData) {
        dashboardData.totalUsers = data.userData.totalUsers || 0
        dashboardData.userGrowth = data.userData.userGrowth || 0
        dashboardData.todayUsers = data.userData.todayUsers || 0
        dashboardData.todayUserGrowth = data.userData.todayUserGrowth || 0
      }
      
      if (data.videoData) {
        videoData.totalVideos = data.videoData.totalVideos || 0
        videoData.passedVideos = data.videoData.passedVideos || 0
        videoData.pendingVideos = data.videoData.pendingVideos || 0
        videoData.totalViews = data.videoData.totalViews || 0
        videoData.passRate = data.videoData.passRate || 0
        videoData.pendingRate = data.videoData.pendingRate || 0
      }
      
      if (data.aiCommentData) {
        aiData.modelCount = data.aiCommentData.modelCount || 0
        aiData.commentCount = data.aiCommentData.commentCount || 0
        aiData.totalFileCount = data.aiCommentData.totalFileCount || 0
      }
      
      if (data.aiUsageData) {
        aiUsageData.value = data.aiUsageData
      }
      
      if (data.userWeekGrowthData) {
        userWeekGrowthData.value = data.userWeekGrowthData
      }
      
      if (data.yearStatsData) {
        yearStatsData.value = data.yearStatsData
      }
      
      if (data.educationData) {
        educationData.value = data.educationData
      }
      
      if (data.languageData) {
        languageData.value = data.languageData
      }
      
      if (data.videoTypeData) {
        categoryList.value = data.videoTypeData.map((item, index) => {
          const colors = [
            'linear-gradient(135deg, #00d4ff, #0099ff)',
            'linear-gradient(135deg, #ff6b6b, #ff4757)',
            'linear-gradient(135deg, #4ecdc4, #26a69a)',
            'linear-gradient(135deg, #ffd93d, #ff9500)',
            'linear-gradient(135deg, #c44dff, #9c27b0)'
          ]
          const icons = ['💻', '🎵', '📐', '📚', '🎨']
          return {
            name: item.name,
            icon: icons[index % icons.length],
            count: item.count,
            percent: item.percent,
            color: colors[index % colors.length]
          }
        })
      }
      
      setTimeout(() => {
        nextTick(() => {
          initUserGrowthChart()
          initTrendChart()
          initEducationChart()
          initLanguageChart()
          initAiUsageChart()
          animateNumbers()
          setTimeout(() => {
            userGrowthChart?.resize()
            trendChart?.resize()
            educationChart?.resize()
            languageChart?.resize()
            aiUsageChart?.resize()
          }, 50)
        })
      }, 100)
    }
  } catch (error) {
    console.error('获取大屏数据失败:', error)
  }
}

const getParticleStyle = (index) => {
  const size = Math.random() * 4 + 2
  return {
    width: size + 'px',
    height: size + 'px',
    left: Math.random() * 100 + '%',
    top: Math.random() * 100 + '%',
    animationDelay: Math.random() * 5 + 's',
    animationDuration: (Math.random() * 10 + 10) + 's'
  }
}

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour12: false })
}

const formatNumber = (num) => {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num
}

const animateNumbers = () => {
  const targets = { ...dashboardData }
  const duration = 2000
  const steps = 60
  const interval = duration / steps
  
  let step = 0
  numberInterval = setInterval(() => {
    step++
    const progress = step / steps
    const easeProgress = 1 - Math.pow(1 - progress, 3)
    
    animatedNumbers.totalUsers = Math.floor(targets.totalUsers * easeProgress)
    animatedNumbers.todayUsers = Math.floor(targets.todayUsers * easeProgress)
    
    if (step >= steps) clearInterval(numberInterval)
  }, interval)
}

const initUserGrowthChart = () => {
  if (!userGrowthChartRef.value) return
  userGrowthChart = echarts.init(userGrowthChartRef.value)
  
  const option = {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis', backgroundColor: 'rgba(0,0,0,0.8)', borderColor: '#00d4ff', textStyle: { color: '#fff' } },
    grid: { left: '5%', right: '5%', bottom: '10%', top: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      axisLine: { lineStyle: { color: 'rgba(0,212,255,0.3)' } },
      axisLabel: { color: 'rgba(255,255,255,0.6)', fontSize: 10 }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { color: 'rgba(255,255,255,0.6)', fontSize: 10 },
      splitLine: { lineStyle: { color: 'rgba(0,212,255,0.1)' } }
    },
    series: [{
      data: userWeekGrowthData.value,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: { color: '#00d4ff', width: 2, shadowColor: 'rgba(0,212,255,0.5)', shadowBlur: 10 },
      itemStyle: { color: '#00d4ff', borderColor: '#fff', borderWidth: 1 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(0,212,255,0.4)' },
          { offset: 1, color: 'rgba(0,212,255,0.05)' }
        ])
      }
    }]
  }
  userGrowthChart.setOption(option)
}

const initTrendChart = () => {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)
  
  const months = yearStatsData.value.months || ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  const userGrowth = yearStatsData.value.userGrowth || []
  const videoUpload = yearStatsData.value.videoUpload || []
  const viewCount = yearStatsData.value.viewCount || []
  
  const option = {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis', backgroundColor: 'rgba(0,0,0,0.8)', borderColor: '#00d4ff', textStyle: { color: '#fff' } },
    legend: { data: ['用户增长', '视频上传', '播放量'], textStyle: { color: 'rgba(255,255,255,0.6)', fontSize: 10 }, top: 5, itemWidth: 15, itemHeight: 8 },
    grid: { left: '5%', right: '5%', bottom: '10%', top: '20%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: months,
      axisLine: { lineStyle: { color: 'rgba(0,212,255,0.3)' } },
      axisLabel: { color: 'rgba(255,255,255,0.6)', fontSize: 10 }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { color: 'rgba(255,255,255,0.6)', fontSize: 10 },
      splitLine: { lineStyle: { color: 'rgba(0,212,255,0.1)' } }
    },
    series: [
      { name: '用户增长', type: 'line', smooth: true, data: userGrowth, lineStyle: { color: '#00d4ff', width: 2 }, itemStyle: { color: '#00d4ff' }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(0,212,255,0.3)' }, { offset: 1, color: 'rgba(0,212,255,0.05)' }]) } },
      { name: '视频上传', type: 'line', smooth: true, data: videoUpload, lineStyle: { color: '#ff6b6b', width: 2 }, itemStyle: { color: '#ff6b6b' }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(255,107,107,0.3)' }, { offset: 1, color: 'rgba(255,107,107,0.05)' }]) } },
      { name: '播放量', type: 'line', smooth: true, data: viewCount, lineStyle: { color: '#4ecdc4', width: 2 }, itemStyle: { color: '#4ecdc4' }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(78,205,196,0.3)' }, { offset: 1, color: 'rgba(78,205,196,0.05)' }]) } }
    ]
  }
  trendChart.setOption(option)
}

const initEducationChart = () => {
  if (!educationChartRef.value) return
  educationChart = echarts.init(educationChartRef.value)
  
  const colors = [
    new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#c44dff' }, { offset: 1, color: '#9c27b0' }]),
    new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#00d4ff' }, { offset: 1, color: '#0099ff' }]),
    new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#4ecdc4' }, { offset: 1, color: '#26a69a' }]),
    new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#ffd93d' }, { offset: 1, color: '#ff9500' }]),
    new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#ff6b6b' }, { offset: 1, color: '#ff4757' }])
  ]
  
  const defaultData = [
    { value: 320, name: '博士' },
    { value: 856, name: '硕士' },
    { value: 4521, name: '本科' },
    { value: 3245, name: '专科' },
    { value: 1236, name: '高中' }
  ]
  
  const data = educationData.value.length > 0 ? educationData.value : defaultData
  const yAxisData = data.map(item => item.name).reverse()
  const seriesData = data.map((item, index) => ({
    value: item.value,
    itemStyle: { color: colors[data.length - 1 - index] }
  })).reverse()
  
  const option = {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis', backgroundColor: 'rgba(0,0,0,0.8)', borderColor: '#00d4ff', textStyle: { color: '#fff' }, axisPointer: { type: 'shadow' } },
    grid: { left: '5%', right: '10%', bottom: '10%', top: '10%', containLabel: true },
    xAxis: { type: 'value', axisLine: { show: false }, axisLabel: { color: 'rgba(255,255,255,0.6)', fontSize: 10 }, splitLine: { lineStyle: { color: 'rgba(0,212,255,0.1)' } } },
    yAxis: { type: 'category', data: yAxisData, axisLine: { lineStyle: { color: 'rgba(0,212,255,0.3)' } }, axisLabel: { color: 'rgba(255,255,255,0.8)', fontSize: 10 } },
    series: [{
      type: 'bar',
      data: seriesData,
      barWidth: 12,
      itemStyle: { borderRadius: [0, 4, 4, 0] },
      label: { show: true, position: 'right', color: 'rgba(255,255,255,0.8)', fontSize: 10 }
    }]
  }
  educationChart.setOption(option)
}

const initLanguageChart = () => {
  if (!languageChartRef.value) return
  languageChart = echarts.init(languageChartRef.value)
  
  const colors = ['#00d4ff', '#ff6b6b', '#4ecdc4', '#ffd93d', '#c44dff']
  
  const defaultData = [
    { value: 68, name: '中文' },
    { value: 18, name: '英语' },
    { value: 8, name: '日语' },
    { value: 4, name: '韩语' },
    { value: 2, name: '其他' }
  ]
  
  const data = languageData.value.length > 0 ? languageData.value : defaultData
  const seriesData = data.map((item, index) => ({
    value: item.value,
    name: item.name,
    itemStyle: { color: colors[index % colors.length] }
  }))
  
  const option = {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'item', backgroundColor: 'rgba(0,0,0,0.8)', borderColor: '#00d4ff', textStyle: { color: '#fff' } },
    series: [{
      type: 'pie',
      radius: ['40%', '65%'],
      center: ['50%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 8, borderColor: '#0a1628', borderWidth: 2 },
      label: { show: true, position: 'outside', color: 'rgba(255,255,255,0.8)', fontSize: 10, formatter: '{b}: {d}%' },
      labelLine: { lineStyle: { color: 'rgba(0,212,255,0.5)' } },
      data: seriesData
    }]
  }
  languageChart.setOption(option)
}

const initAiUsageChart = () => {
  if (!aiUsageChartRef.value) return
  aiUsageChart = echarts.init(aiUsageChartRef.value)
  
  const colors = ['#00d4ff', '#ff6b6b', '#4ecdc4', '#ffd93d', '#c44dff', '#00ff88']
  
  const defaultData = [
    { value: 18, name: 'qwen-plus' },
    { value: 6, name: 'qwen3.5-plus' },
    { value: 4, name: 'doubao-seed' },
    { value: 3, name: 'tongyi-xiaomi' }
  ]
  
  const data = aiUsageData.value.length > 0 ? aiUsageData.value : defaultData
  const seriesData = data.map((item, index) => ({
    value: item.value,
    name: item.name,
    itemStyle: { color: colors[index % colors.length] }
  }))
  
  const option = {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'item', backgroundColor: 'rgba(0,0,0,0.8)', borderColor: '#00d4ff', textStyle: { color: '#fff' } },
    series: [{
      type: 'pie',
      radius: ['35%', '60%'],
      center: ['50%', '50%'],
      roseType: 'radius',
      itemStyle: { borderRadius: 5, borderColor: '#0a1628', borderWidth: 2 },
      label: { show: true, color: 'rgba(255,255,255,0.8)', fontSize: 9, formatter: '{b}\n{d}%' },
      labelLine: { lineStyle: { color: 'rgba(0,212,255,0.5)' }, length: 6, length2: 6 },
      data: seriesData
    }]
  }
  aiUsageChart.setOption(option)
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

const handleResize = () => {
  userGrowthChart?.resize()
  trendChart?.resize()
  educationChart?.resize()
  languageChart?.resize()
  aiUsageChart?.resize()
}

onMounted(() => {
  updateTime()
  timeInterval = setInterval(updateTime, 1000)
  fetchDashboardData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  clearInterval(timeInterval)
  clearInterval(numberInterval)
  window.removeEventListener('resize', handleResize)
  userGrowthChart?.dispose()
  trendChart?.dispose()
  educationChart?.dispose()
  languageChart?.dispose()
  aiUsageChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  position: relative;
  width: 100%;
  height: 100vh;
  min-height: 800px;
  overflow: hidden;
  background: linear-gradient(135deg, #0a1628 0%, #1a2a4a 50%, #0d1f3c 100%);
  color: #fff;
}

.dashboard-container.fullscreen { position: fixed; top: 0; left: 0; z-index: 9999; }

.dashboard-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.bg-grid {
  position: absolute;
  width: 100%;
  height: 100%;
  background-image: linear-gradient(rgba(0,212,255,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(0,212,255,0.03) 1px, transparent 1px);
  background-size: 50px 50px;
}

.bg-particles { position: absolute; width: 100%; height: 100%; }

.particle {
  position: absolute;
  background: radial-gradient(circle, rgba(0,212,255,0.8) 0%, transparent 70%);
  border-radius: 50%;
  animation: float 15s infinite ease-in-out;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); opacity: 0.3; }
  25% { transform: translate(20px, -30px) scale(1.2); opacity: 0.6; }
  50% { transform: translate(-10px, -50px) scale(0.8); opacity: 0.4; }
  75% { transform: translate(30px, -20px) scale(1.1); opacity: 0.5; }
}

.scan-line {
  position: absolute;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(0,212,255,0.5), transparent);
  animation: scan 4s linear infinite;
}

@keyframes scan {
  0% { top: 0; opacity: 0; }
  50% { opacity: 1; }
  100% { top: 100%; opacity: 0; }
}

.corner-decoration {
  position: absolute;
  width: 80px;
  height: 80px;
  border: 2px solid rgba(0,212,255,0.3);
}

.corner-decoration.top-left { top: 10px; left: 10px; border-right: none; border-bottom: none; }
.corner-decoration.top-right { top: 10px; right: 10px; border-left: none; border-bottom: none; }
.corner-decoration.bottom-left { bottom: 10px; left: 10px; border-right: none; border-top: none; }
.corner-decoration.bottom-right { bottom: 10px; right: 10px; border-left: none; border-top: none; }

.dashboard-header {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 25px;
  background: linear-gradient(180deg, rgba(0,212,255,0.1) 0%, transparent 100%);
  border-bottom: 1px solid rgba(0,212,255,0.2);
}

.header-left { flex: 1; }

.logo-section { display: flex; align-items: center; gap: 12px; }

.logo-ring {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  border: 2px solid rgba(0,212,255,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,212,255,0.1);
  animation: pulse-ring 2s infinite;
}

@keyframes pulse-ring {
  0%, 100% { box-shadow: 0 0 0 0 rgba(0,212,255,0.4); }
  50% { box-shadow: 0 0 0 10px rgba(0,212,255,0); }
}

.logo-icon { font-size: 22px; }

.logo-text-group { display: flex; flex-direction: column; }

.logo-text { font-size: 16px; font-weight: 600; background: linear-gradient(90deg, #00d4ff, #00ff88); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
.logo-sub { font-size: 10px; color: rgba(255,255,255,0.5); letter-spacing: 2px; }

.header-center { flex: 2; text-align: center; }

.main-title { position: relative; display: inline-block; padding: 8px 40px; }

.title-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent, rgba(0,212,255,0.1), transparent);
  border: 1px solid rgba(0,212,255,0.3);
  border-radius: 4px;
}

.main-title h1 {
  position: relative;
  font-size: 22px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(90deg, #fff, #00d4ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 4px;
}

.header-right { flex: 1; display: flex; justify-content: flex-end; align-items: center; gap: 15px; }

.datetime-panel {
  text-align: right;
  padding: 8px 15px;
  background: rgba(0,0,0,0.2);
  border-radius: 8px;
  border: 1px solid rgba(0,212,255,0.2);
}

.date { font-size: 12px; color: rgba(255,255,255,0.7); }
.time { font-size: 20px; font-weight: 600; color: #00d4ff; font-family: 'Courier New', monospace; }

.fullscreen-btn {
  background: rgba(0,212,255,0.1);
  border: 1px solid rgba(0,212,255,0.3);
  color: #00d4ff;
  width: 36px;
  height: 36px;
  border-radius: 8px;
}

.fullscreen-btn:hover { background: rgba(0,212,255,0.2); border-color: #00d4ff; }

.dashboard-body { position: relative; display: flex; padding: 12px; height: calc(100vh - 65px); gap: 12px; }

.dashboard-left, .dashboard-right { width: 300px; display: flex; flex-direction: column; gap: 12px; }
.dashboard-center { flex: 1; display: flex; flex-direction: column; gap: 12px; }
.center-top { height: 160px; }
.center-middle { flex: 1; }
.center-middle .panel { height: 100%; display: flex; flex-direction: column; }
.center-middle .panel-content { flex: 1; display: flex; flex-direction: column; }

.panel {
  background: linear-gradient(180deg, rgba(0,212,255,0.05) 0%, rgba(0,0,0,0.2) 100%);
  border: 1px solid rgba(0,212,255,0.2);
  border-radius: 10px;
  overflow: hidden;
  position: relative;
}

.panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, #00d4ff, transparent);
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border-bottom: 1px solid rgba(0,212,255,0.1);
  background: rgba(0,212,255,0.05);
  position: relative;
}

.header-decoration {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 16px;
  background: linear-gradient(180deg, #00d4ff, #00ff88);
  border-radius: 0 2px 2px 0;
}

.panel-icon { font-size: 14px; }
.panel-title { font-size: 13px; font-weight: 600; color: #fff; margin-left: 8px; }
.panel-content { padding: 10px; }

.user-stats-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 8px; margin-bottom: 10px; }

.stat-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background: rgba(0,0,0,0.2);
  border-radius: 8px;
  border: 1px solid rgba(0,212,255,0.1);
  position: relative;
  overflow: hidden;
}

.stat-icon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.stat-icon.blue { background: rgba(0,212,255,0.2); }
.stat-icon.green { background: rgba(0,255,136,0.2); }
.stat-icon.orange { background: rgba(255,217,61,0.2); }
.stat-icon.purple { background: rgba(196,77,255,0.2); }

.stat-info { flex: 1; }
.stat-number { font-size: 18px; font-weight: 700; color: #00d4ff; }
.stat-label { font-size: 10px; color: rgba(255,255,255,0.6); }
.stat-trend { font-size: 10px; padding: 2px 6px; border-radius: 4px; }
.stat-trend.up { background: rgba(0,255,136,0.2); color: #00ff88; }
.stat-badge { font-size: 9px; padding: 2px 6px; background: rgba(0,212,255,0.2); color: #00d4ff; border-radius: 4px; }

.chart-container { height: 120px; }
.chart-container.large { height: 100%; flex: 1; min-height: 200px; }

.video-stats-row { display: flex; gap: 10px; height: 100%; padding: 5px 0; }

.video-stat-card {
  flex: 1;
  position: relative;
  padding: 12px;
  background: rgba(0,0,0,0.2);
  border-radius: 10px;
  border: 1px solid rgba(0,212,255,0.1);
  overflow: hidden;
}

.video-stat-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0,212,255,0.1) 0%, transparent 100%);
}

.video-stat-bg.green { background: linear-gradient(135deg, rgba(0,255,136,0.1) 0%, transparent 100%); }
.video-stat-bg.orange { background: linear-gradient(135deg, rgba(255,217,61,0.1) 0%, transparent 100%); }
.video-stat-bg.purple { background: linear-gradient(135deg, rgba(196,77,255,0.1) 0%, transparent 100%); }

.video-stat-icon { font-size: 20px; margin-bottom: 8px; position: relative; }
.video-stat-content { position: relative; }
.video-stat-value { font-size: 22px; font-weight: 700; color: #fff; }
.video-stat-label { font-size: 11px; color: rgba(255,255,255,0.6); }

.video-stat-ring {
  position: absolute;
  right: 8px;
  bottom: 8px;
  width: 45px;
  height: 45px;
}

.video-stat-ring svg { transform: rotate(-90deg); width: 100%; height: 100%; }
.ring-bg { fill: none; stroke: rgba(0,212,255,0.1); stroke-width: 6; }
.ring-progress { fill: none; stroke: #00d4ff; stroke-width: 6; stroke-linecap: round; stroke-dasharray: 283; transition: stroke-dashoffset 1.5s ease; }
.ring-progress.green { stroke: #00ff88; }
.ring-progress.orange { stroke: #ffd93d; }
.ring-progress.purple { stroke: #c44dff; }

.ai-stats-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }

.ai-stat-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(0,0,0,0.2);
  border-radius: 12px;
  border: 1px solid rgba(0,212,255,0.2);
  transition: all 0.3s;
}

.ai-stat-box:hover { background: rgba(0,212,255,0.1); border-color: rgba(0,212,255,0.4); }

.ai-stat-icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(0,212,255,0.2), rgba(196,77,255,0.2));
  border-radius: 12px;
  font-size: 22px;
}

.ai-stat-info { flex: 1; }

.ai-stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  text-shadow: 0 0 10px rgba(0,212,255,0.5);
}

.ai-stat-label { font-size: 12px; color: rgba(255,255,255,0.6); margin-top: 4px; }

.ai-usage-panel { display: flex; flex-direction: column; }
.ai-usage-header { display: flex; justify-content: flex-end; margin-bottom: 8px; }
.ai-model-count { 
  display: flex; 
  align-items: center; 
  gap: 8px; 
  padding: 6px 12px; 
  background: rgba(0,212,255,0.1); 
  border-radius: 8px; 
  border: 1px solid rgba(0,212,255,0.2);
}
.ai-model-count .count-value { 
  font-size: 20px; 
  font-weight: 700; 
  color: #00d4ff; 
  text-shadow: 0 0 10px rgba(0,212,255,0.5);
}
.ai-model-count .count-label { font-size: 11px; color: rgba(255,255,255,0.6); }
.ai-usage-chart { height: 160px; }

.category-list { display: flex; flex-direction: column; gap: 8px; }

.category-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  background: rgba(0,0,0,0.2);
  border-radius: 6px;
}

.category-icon {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.category-info { flex: 1; }
.category-name { font-size: 11px; color: rgba(255,255,255,0.8); margin-bottom: 4px; }
.category-bar { height: 4px; background: rgba(255,255,255,0.1); border-radius: 2px; overflow: hidden; }
.category-progress { height: 100%; border-radius: 2px; transition: width 1s ease; }
.category-value { font-size: 12px; font-weight: 600; color: #00d4ff; }
</style>
