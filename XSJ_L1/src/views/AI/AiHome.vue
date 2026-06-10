<template>
  <div class="ai-home" :class="currentTheme" @mousemove="handleMouseMove">
    <div class="ai-header">
      <div class="header-content">
        <div class="logo-section">
          <div class="brand-logo">
            <div class="brand-icon">🌍</div>
            <div class="brand-text">
              <span class="brand-name">{{ t('brand.name') }}</span>
              <span class="brand-english">{{ t('brand.englishName') }}</span>
            </div>
          </div>
        </div>
        <div class="header-nav">
          <a href="#" class="nav-link" @click.prevent="goToHome">
            <el-icon><el-icon-house /></el-icon>
            <span>{{ t('nav.home') }}</span>
          </a>
          <el-dropdown trigger="click" @command="changeTheme" class="theme-dropdown">
            <div class="theme-switch">
              <span class="theme-icon">{{ currentThemeInfo.icon }}</span>
              <span class="theme-text">{{ currentThemeInfo.name }}</span>
              <el-icon class="theme-arrow"><el-icon-arrow-down /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="sunrise" :class="{ active: currentTheme === 'sunrise' }">
                  <span class="theme-item-icon">🌅</span>
                  <span class="theme-item-text">{{ t('ai.themeSunrise') }}</span>
                </el-dropdown-item>
                <el-dropdown-item command="sunshine" :class="{ active: currentTheme === 'sunshine' }">
                  <span class="theme-item-icon">☀️</span>
                  <span class="theme-item-text">{{ t('ai.themeSunshine') }}</span>
                </el-dropdown-item>
                <el-dropdown-item command="starlight" :class="{ active: currentTheme === 'starlight' }">
                  <span class="theme-item-icon">✨</span>
                  <span class="theme-item-text">{{ t('ai.themeStarlight') }}</span>
                </el-dropdown-item>
                <el-dropdown-item command="default" :class="{ active: currentTheme === 'default' }">
                  <span class="theme-item-icon">⚪</span>
                  <span class="theme-item-text">{{ t('ai.themeDefault') }}</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-dropdown trigger="click" @command="changeLocale" class="lang-dropdown">
            <div class="lang-switch">
              <span class="lang-icon">🌐</span>
              <span class="lang-text">{{ currentLang }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="zh">�� 中文</el-dropdown-item>
                <el-dropdown-item command="en">🇺🇸 English</el-dropdown-item>
                <el-dropdown-item command="ja">🇯🇵 日本語</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <div class="ai-hero">
      <div class="hero-bg-effects">
        <div class="grid-bg"></div>
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
        <div class="floating-shape shape-3"></div>
        <div class="floating-shape shape-4"></div>
        <div class="floating-shape shape-5"></div>
        <div class="glow-orb orb-1"></div>
        <div class="glow-orb orb-2"></div>
        <div class="glow-orb orb-3"></div>
        <div class="particle p1"></div>
        <div class="particle p2"></div>
        <div class="particle p3"></div>
        <div class="particle p4"></div>
        <div class="particle p5"></div>
        <div class="particle p6"></div>
        <div class="particle p7"></div>
        <div class="particle p8"></div>
        <div class="wave-line w1"></div>
        <div class="wave-line w2"></div>
        <div class="wave-line w3"></div>
        
        <div class="theme-effects sunrise-effects" v-if="currentTheme === 'sunrise'">
          <div class="light-particle lp1"></div>
          <div class="light-particle lp2"></div>
          <div class="light-particle lp3"></div>
          <div class="light-particle lp4"></div>
          <div class="light-particle lp5"></div>
          <div class="warm-glow"></div>
        </div>
        
        <div class="theme-effects sunshine-effects" v-if="currentTheme === 'sunshine'">
          <div class="bright-spot bs1"></div>
          <div class="bright-spot bs2"></div>
          <div class="bright-spot bs3"></div>
          <div class="golden-shimmer"></div>
        </div>
      </div>
      
      <div class="hero-content">
        <transition name="brand-fade" mode="out-in">
          <div v-if="!hasInteracted" class="brand-intro">
            <div class="intro-logo">
              <div class="intro-icon">🌍</div>
              <div class="intro-glow"></div>
            </div>
            <h1 class="intro-title">
              <span class="intro-main">{{ t('brand.name') }}</span>
              <span class="intro-sub">{{ t('brand.englishName') }}</span>
            </h1>
            <div class="intro-hint">
              <span class="hint-icon">👆</span>
              <span class="hint-text">{{ t('ai.moveHint') }}</span>
            </div>
          </div>
          
          <div v-else class="main-content">
            <div class="hero-badge">
              <span class="badge-icon">✨</span>
              <span>{{ t('ai.badge') }}</span>
            </div>
            <h1 class="hero-title">
              <span class="title-line">{{ t('ai.title1') }}</span>
              <span class="title-highlight">{{ t('ai.title2') }}</span>
            </h1>
            <p class="hero-description">{{ t('ai.description') }}</p>
            
            <div class="hero-stats">
              <div class="stat-item">
                <span class="stat-number">10+</span>
                <span class="stat-label">{{ t('ai.models') }}</span>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <span class="stat-number">24/7</span>
                <span class="stat-label">{{ t('ai.availability') }}</span>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <span class="stat-number">∞</span>
                <span class="stat-label">{{ t('ai.questions') }}</span>
              </div>
            </div>
          </div>
        </transition>
      </div>
    </div>

    <transition name="section-fade">
      <div v-if="hasInteracted" class="ai-features">
        <div class="section-header">
          <span class="section-icon">🎯</span>
          <h2>{{ t('ai.featuresTitle') }}</h2>
        </div>
        
        <div class="features-grid">
          <div class="feature-card" v-for="(feature, index) in features" :key="index">
            <div class="feature-icon">{{ feature.icon }}</div>
            <h3 class="feature-title">{{ t(`ai.feature${index + 1}Title`) }}</h3>
            <p class="feature-desc">{{ t(`ai.feature${index + 1}Desc`) }}</p>
            <div class="feature-glow"></div>
          </div>
        </div>
      </div>
    </transition>

    <transition name="section-fade">
      <div v-if="hasInteracted" class="ai-models">
        <div class="section-header">
          <span class="section-icon">🧠</span>
          <h2>{{ t('ai.modelsTitle') }}</h2>
          <p class="section-subtitle">{{ t('ai.modelsSubtitle') }}</p>
        </div>
        
        <div class="models-container">
          <div 
            class="model-card" 
            v-for="model in aiModels" 
            :key="model.id"
            :class="{ active: selectedModel === model.id }"
            @click="selectModel(model.id)"
          >
            <div class="model-header">
              <div class="model-avatar">
                <span class="avatar-emoji">{{ model.emoji }}</span>
              </div>
              <div class="model-badge" v-if="model.popular">{{ t('ai.popular') }}</div>
            </div>
            <div class="model-info">
              <h3 class="model-name">{{ model.name }}</h3>
              <p class="model-desc">{{ model.description }}</p>
              <div class="model-tags">
                <span class="tag" v-for="tag in model.tags" :key="tag">{{ tag }}</span>
              </div>
            </div>
            <div class="model-select">
              <div class="select-circle">
                <el-icon v-if="selectedModel === model.id"><el-icon-check /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <transition name="section-fade">
      <div v-if="hasInteracted" class="ai-cta">
        <div class="cta-content">
          <div class="cta-decor-left">🚀</div>
          <div class="cta-text">
            <h2>{{ t('ai.ctaTitle') }}</h2>
            <p>{{ t('ai.ctaDesc') }}</p>
          </div>
          <div class="cta-decor-right">💡</div>
        </div>
        <button class="start-btn" @click="startChat">
          <span class="btn-icon">💬</span>
          <span class="btn-text">{{ t('ai.startChat') }}</span>
          <span class="btn-arrow">→</span>
        </button>
      </div>
    </transition>

    <div class="ai-footer">
      <p>{{ t('ai.footer') }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { getAiThemeService, updateAiThemeService, getAiModelsService } from '@/api/Ai'

const { t, locale } = useI18n()
const router = useRouter()

const hasInteracted = ref(false)
const selectedModel = ref('gpt4')
const currentTheme = ref('starlight')

const themes = {
  sunrise: {
    id: 'sunrise',
    icon: '🌅',
    name: '旭日初升'
  },
  sunshine: {
    id: 'sunshine',
    icon: '☀️',
    name: '日曜中天'
  },
  starlight: {
    id: 'starlight',
    icon: '✨',
    name: '紫宸星阑'
  },
  default: {
    id: 'default',
    icon: '⚪',
    name: '默认'
  }
}

const themeMap = {
  1: 'sunrise',
  2: 'sunshine',
  3: 'starlight',
  4: 'default'
}

const currentThemeInfo = computed(() => themes[currentTheme.value] || themes.starlight)

const changeTheme = async (theme) => {
  currentTheme.value = theme
  ElMessage.success(`${t('ai.themeChanged')}: ${themes[theme].name}`)
  try {
    const honeValue = Object.keys(themeMap).find(key => themeMap[key] === theme)
    if (honeValue) {
      await updateAiThemeService(honeValue)
    }
  } catch (error) {
    console.log('保存主题设置失败')
  }
}

onMounted(async () => {
  try {
    const res = await getAiThemeService()
    if (res.code === 0 && res.data) {
      const themeKey = themeMap[res.data]
      if (themeKey) {
        currentTheme.value = themeKey
      }
    }
  } catch (error) {
    console.log('获取主题失败，使用默认主题')
  }
  try {
    const modelRes = await getAiModelsService()
    if (modelRes.code === 0 && modelRes.data) {
      aiModels.value = processModels(modelRes.data)
      if (aiModels.value.length > 0) {
        selectedModel.value = aiModels.value[0].id
      }
    }
  } catch (error) {
    console.log('获取模型列表失败')
  }
})

const currentLang = computed(() => {
  const langMap = {
    zh: '中文',
    en: 'English',
    ja: '日本語'
  }
  return langMap[locale.value] || '中文'
})

const changeLocale = (lang) => {
  locale.value = lang
}

const features = [
  { icon: '📚' },
  { icon: '🎨' },
  { icon: '💻' },
  { icon: '📊' }
]

const defaultModelImages = ['🤖', '⚡', '🧠', '💎', '🌟', '🔮']
const defaultIntroductions = [
  '强大的AI助手，为您提供智能问答服务',
  '专业的学习辅导，助您掌握知识要点',
  '创意写作专家，激发您的无限灵感',
  '代码分析能手，解决编程难题'
]

const aiModels = ref([])

const processModels = (models) => {
  return models.map((model, index) => ({
    id: model.modelCode,
    name: model.modelName,
    emoji: model.image || defaultModelImages[index % defaultModelImages.length],
    description: model.introduction || defaultIntroductions[index % defaultIntroductions.length],
    tags: [],
    popular: index === 0,
    status: model.status
  }))
}

const handleMouseMove = () => {
  if (!hasInteracted.value) {
    hasInteracted.value = true
  }
}

const selectModel = (modelId) => {
  selectedModel.value = modelId
  const model = aiModels.value.find(m => m.id === modelId)
  ElMessage.success(`${t('ai.modelSelected')}: ${model.name}`)
}

const startChat = () => {
  router.push({
    path: '/ai/dialogue',
    query: { model: selectedModel.value }
  })
}

const goToHome = () => {
  router.push('/First')
}
</script>

<style scoped>
.ai-home {
  min-height: 100vh;
  overflow-x: hidden;
  position: relative;
  transition: all 0.8s ease;
}

.ai-home.sunrise {
  background: linear-gradient(180deg, 
    #fdf6e3 0%, 
    #fef3e2 15%,
    #fff5eb 30%,
    #fff8f0 45%,
    #fffbf5 60%,
    #fff9f0 75%,
    #fef7ed 90%,
    #fdf4e8 100%
  );
}

.ai-home.sunshine {
  background: linear-gradient(180deg,
    #fffef5 0%,
    #fffde8 15%,
    #fffcd0 30%,
    #fffdb8 45%,
    #fffea0 60%,
    #f7f48a 75%,
    #e8e478 90%,
    #d4d06a 100%
  );
}

.ai-home.starlight {
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3e 50%, #0f0f2d 100%);
}

.ai-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.8s ease;
}

.sunrise .ai-header {
  background: rgba(253, 246, 227, 0.95);
  border-bottom-color: rgba(200, 150, 100, 0.15);
}

.sunshine .ai-header {
  background: rgba(255, 255, 255, 0.98);
  border-bottom-color: rgba(0, 0, 0, 0.08);
}

.starlight .ai-header {
  background: rgba(10, 10, 26, 0.9);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 16px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 16px;
}

.brand-icon {
  font-size: 36px;
  animation: globe-rotate 10s linear infinite;
}

@keyframes globe-rotate {
  0% { transform: rotateY(0deg); }
  100% { transform: rotateY(360deg); }
}

.brand-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-name {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(90deg, #00d4ff, #7b2ff7, #f107a3);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sunrise .brand-name {
  background: linear-gradient(90deg, #c97b4a, #d4885a, #b86b3a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sunshine .brand-name {
  background: linear-gradient(90deg, #b8860b, #daa520, #b8860b);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.brand-english {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 1px;
}

.sunrise .brand-english {
  color: rgba(150, 100, 70, 0.6);
}

.sunshine .brand-english {
  color: rgba(180, 160, 40, 0.6);
}

.header-nav {
  display: flex;
  gap: 20px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  border-radius: 25px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.sunrise .nav-link {
  color: rgba(100, 70, 50, 0.8);
  background: rgba(200, 150, 100, 0.1);
  border-color: rgba(200, 150, 100, 0.2);
}

.sunshine .nav-link {
  color: rgba(70, 60, 20, 0.8);
  background: rgba(200, 180, 50, 0.1);
  border-color: rgba(200, 180, 50, 0.2);
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.15);
  color: white;
  transform: translateY(-2px);
}

.sunrise .nav-link:hover {
  background: rgba(200, 150, 100, 0.2);
  color: #5a3a2a;
}

.sunshine .nav-link:hover {
  background: rgba(200, 180, 50, 0.2);
  color: #2a2505;
}

.theme-dropdown {
  margin-left: 10px;
}

.theme-switch {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  color: rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sunrise .theme-switch {
  color: rgba(100, 70, 50, 0.8);
  background: rgba(200, 150, 100, 0.15);
  border-color: rgba(180, 130, 80, 0.3);
}

.sunshine .theme-switch {
  color: rgba(100, 90, 30, 0.8);
  background: rgba(200, 180, 50, 0.15);
  border-color: rgba(180, 160, 40, 0.3);
}

.theme-switch:hover {
  background: rgba(255, 255, 255, 0.15);
  color: white;
  transform: translateY(-2px);
}

.sunrise .theme-switch:hover {
  background: rgba(200, 150, 100, 0.25);
  color: #3a2515;
}

.sunshine .theme-switch:hover {
  background: rgba(200, 180, 50, 0.25);
  color: #2a2505;
}

.theme-icon {
  font-size: 16px;
}

.theme-text {
  font-size: 14px;
  font-weight: 500;
}

.theme-arrow {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.theme-dropdown :deep(.el-dropdown-menu) {
  background: rgba(15, 15, 35, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 8px;
  backdrop-filter: blur(20px);
}

.sunrise .theme-dropdown :deep(.el-dropdown-menu) {
  background: rgba(253, 246, 227, 0.98);
  border-color: rgba(180, 130, 80, 0.2);
}

.sunshine .theme-dropdown :deep(.el-dropdown-menu) {
  background: rgba(255, 254, 245, 0.98);
  border-color: rgba(180, 160, 40, 0.2);
}

.theme-dropdown :deep(.el-dropdown-menu__item) {
  color: rgba(255, 255, 255, 0.8);
  padding: 10px 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.3s ease;
}

.sunrise .theme-dropdown :deep(.el-dropdown-menu__item) {
  color: rgba(80, 60, 40, 0.9);
}

.sunshine .theme-dropdown :deep(.el-dropdown-menu__item) {
  color: rgba(70, 60, 20, 0.9);
}

.theme-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(123, 47, 247, 0.3);
  color: white;
}

.sunrise .theme-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(200, 150, 100, 0.3);
  color: #3a2515;
}

.sunshine .theme-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(200, 180, 50, 0.3);
  color: #2a2505;
}

.theme-dropdown :deep(.el-dropdown-menu__item.active) {
  background: rgba(123, 47, 247, 0.5);
  color: white;
}

.sunrise .theme-dropdown :deep(.el-dropdown-menu__item.active) {
  background: rgba(200, 150, 100, 0.4);
  color: #3a2515;
}

.sunshine .theme-dropdown :deep(.el-dropdown-menu__item.active) {
  background: rgba(200, 180, 50, 0.4);
  color: #2a2505;
}

.theme-item-icon {
  font-size: 18px;
}

.theme-item-text {
  font-size: 14px;
}

.lang-dropdown {
  margin-left: 10px;
}

.lang-switch {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  color: rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sunrise .lang-switch {
  color: rgba(100, 70, 50, 0.8);
  background: rgba(200, 150, 100, 0.15);
  border-color: rgba(180, 130, 80, 0.3);
}

.sunshine .lang-switch {
  color: rgba(100, 90, 30, 0.8);
  background: rgba(200, 180, 50, 0.15);
  border-color: rgba(180, 160, 40, 0.3);
}

.lang-switch:hover {
  background: rgba(255, 255, 255, 0.15);
  color: white;
  transform: translateY(-2px);
}

.sunrise .lang-switch:hover {
  background: rgba(200, 150, 100, 0.25);
  color: #3a2515;
}

.sunshine .lang-switch:hover {
  background: rgba(200, 180, 50, 0.25);
  color: #2a2505;
}

.lang-icon {
  font-size: 16px;
}

.lang-text {
  font-size: 14px;
  font-weight: 500;
}

.lang-arrow {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.lang-dropdown :deep(.el-dropdown-menu) {
  background: rgba(15, 15, 45, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 8px;
  backdrop-filter: blur(20px);
}

.lang-dropdown :deep(.el-dropdown-menu__item) {
  color: rgba(255, 255, 255, 0.8);
  padding: 10px 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.3s ease;
}

.lang-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(123, 47, 247, 0.3);
  color: white;
}

.lang-dropdown :deep(.el-dropdown-menu__item.active) {
  background: rgba(123, 47, 247, 0.5);
  color: white;
}

.lang-flag {
  font-size: 18px;
}

.ai-hero {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 120px 40px 80px;
}

.hero-bg-effects {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.grid-bg {
  position: absolute;
  inset: 0;
  background-image: 
    linear-gradient(rgba(123, 47, 247, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(123, 47, 247, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  animation: grid-move 20s linear infinite;
}

@keyframes grid-move {
  0% { transform: translate(0, 0); }
  100% { transform: translate(60px, 60px); }
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
  transition: background 0.5s ease;
}

.shape-1 {
  width: 500px;
  height: 500px;
  top: 5%;
  left: 5%;
  animation: float-1 15s ease-in-out infinite;
}

.sunrise .shape-1 { background: radial-gradient(circle, rgba(255, 200, 150, 0.3), transparent); }
.sunshine .shape-1 { background: radial-gradient(circle, rgba(200, 200, 200, 0.15), transparent); }
.starlight .shape-1 { background: radial-gradient(circle, #00d4ff, transparent); }

.shape-2 {
  width: 400px;
  height: 400px;
  top: 50%;
  right: 5%;
  animation: float-2 18s ease-in-out infinite;
}

.sunrise .shape-2 { background: radial-gradient(circle, rgba(255, 180, 130, 0.25), transparent); }
.sunshine .shape-2 { background: radial-gradient(circle, rgba(180, 180, 180, 0.12), transparent); }
.starlight .shape-2 { background: radial-gradient(circle, #7b2ff7, transparent); }

.shape-3 {
  width: 350px;
  height: 350px;
  bottom: 10%;
  left: 20%;
  animation: float-3 20s ease-in-out infinite;
}

.sunrise .shape-3 { background: radial-gradient(circle, rgba(255, 160, 100, 0.2), transparent); }
.sunshine .shape-3 { background: radial-gradient(circle, rgba(160, 160, 160, 0.1), transparent); }
.starlight .shape-3 { background: radial-gradient(circle, #f107a3, transparent); }

.shape-4 {
  width: 300px;
  height: 300px;
  top: 20%;
  right: 20%;
  animation: float-4 22s ease-in-out infinite;
}

.sunrise .shape-4 { background: radial-gradient(circle, rgba(255, 220, 180, 0.25), transparent); }
.sunshine .shape-4 { background: radial-gradient(circle, rgba(170, 170, 170, 0.1), transparent); }
.starlight .shape-4 { background: radial-gradient(circle, #00ff88, transparent); }

.shape-5 {
  width: 250px;
  height: 250px;
  bottom: 30%;
  right: 30%;
  animation: float-5 25s ease-in-out infinite;
}

.sunrise .shape-5 { background: radial-gradient(circle, rgba(255, 190, 140, 0.2), transparent); }
.sunshine .shape-5 { background: radial-gradient(circle, rgba(150, 150, 150, 0.08), transparent); }
.starlight .shape-5 { background: radial-gradient(circle, #ffd700, transparent); }

@keyframes float-1 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(80px, 50px) scale(1.1); }
  66% { transform: translate(-30px, 80px) scale(0.9); }
}

@keyframes float-2 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(-60px, -40px) scale(1.05); }
  66% { transform: translate(40px, -70px) scale(1.15); }
}

@keyframes float-3 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(50px, -60px) scale(1.08); }
  66% { transform: translate(-40px, 30px) scale(0.95); }
}

@keyframes float-4 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-70px, 50px) scale(1.12); }
}

@keyframes float-5 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(60px, -40px) scale(1.1); }
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  animation: orb-pulse 4s ease-in-out infinite;
}

.orb-1 {
  width: 150px;
  height: 150px;
  background: radial-gradient(circle, rgba(0, 212, 255, 0.5), transparent);
  top: 15%;
  left: 30%;
  animation-delay: 0s;
}

.orb-2 {
  width: 120px;
  height: 120px;
  background: radial-gradient(circle, rgba(123, 47, 247, 0.5), transparent);
  top: 60%;
  right: 25%;
  animation-delay: 1.5s;
}

.orb-3 {
  width: 100px;
  height: 100px;
  background: radial-gradient(circle, rgba(241, 7, 163, 0.5), transparent);
  bottom: 20%;
  left: 40%;
  animation-delay: 3s;
}

@keyframes orb-pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.5); opacity: 0.8; }
}

.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: white;
  border-radius: 50%;
  animation: particle-float 20s linear infinite;
  box-shadow: 0 0 10px currentColor;
}

.p1 { left: 5%; top: 20%; animation-delay: 0s; color: #00d4ff; }
.p2 { left: 15%; top: 40%; animation-delay: 2s; color: #7b2ff7; }
.p3 { left: 25%; top: 60%; animation-delay: 4s; color: #f107a3; }
.p4 { left: 35%; top: 80%; animation-delay: 6s; color: #00ff88; }
.p5 { left: 55%; top: 15%; animation-delay: 8s; color: #ffd700; }
.p6 { left: 65%; top: 35%; animation-delay: 10s; color: #00d4ff; }
.p7 { left: 75%; top: 55%; animation-delay: 12s; color: #7b2ff7; }
.p8 { left: 85%; top: 75%; animation-delay: 14s; color: #f107a3; }

@keyframes particle-float {
  0% { transform: translateY(0) translateX(0) scale(1); opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { transform: translateY(-200px) translateX(100px) scale(0.5); opacity: 0; }
}

.wave-line {
  position: absolute;
  width: 200%;
  height: 2px;
  left: -50%;
  background: linear-gradient(90deg, transparent, rgba(123, 47, 247, 0.3), transparent);
  animation: wave-move 8s linear infinite;
}

.w1 { top: 30%; animation-delay: 0s; }
.w2 { top: 50%; animation-delay: 2s; }
.w3 { top: 70%; animation-delay: 4s; }

@keyframes wave-move {
  0% { transform: translateX(-25%); }
  100% { transform: translateX(25%); }
}

.theme-effects {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.sunrise-effects .light-particle {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 200, 150, 0.8), transparent);
  animation: light-float 8s ease-in-out infinite;
}

.sunrise-effects .lp1 {
  width: 80px;
  height: 80px;
  top: 20%;
  left: 15%;
  animation-delay: 0s;
}

.sunrise-effects .lp2 {
  width: 60px;
  height: 60px;
  top: 40%;
  right: 20%;
  animation-delay: 1.5s;
}

.sunrise-effects .lp3 {
  width: 100px;
  height: 100px;
  bottom: 30%;
  left: 25%;
  animation-delay: 3s;
}

.sunrise-effects .lp4 {
  width: 50px;
  height: 50px;
  top: 60%;
  right: 30%;
  animation-delay: 4.5s;
}

.sunrise-effects .lp5 {
  width: 70px;
  height: 70px;
  bottom: 20%;
  right: 15%;
  animation-delay: 6s;
}

@keyframes light-float {
  0%, 100% { transform: translateY(0) scale(1); opacity: 0.6; }
  50% { transform: translateY(-30px) scale(1.2); opacity: 1; }
}

.sunrise-effects .warm-glow {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at 50% 100%, rgba(255, 180, 120, 0.15), transparent 60%);
  animation: glow-breathe 4s ease-in-out infinite;
}

@keyframes glow-breathe {
  0%, 100% { opacity: 0.5; }
  50% { opacity: 1; }
}

.sunshine-effects .bright-spot {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 240, 150, 0.9), transparent);
  animation: spot-pulse 3s ease-in-out infinite;
}

.sunshine-effects .bs1 {
  width: 120px;
  height: 120px;
  top: 15%;
  right: 20%;
  animation-delay: 0s;
}

.sunshine-effects .bs2 {
  width: 80px;
  height: 80px;
  top: 35%;
  left: 15%;
  animation-delay: 1s;
}

.sunshine-effects .bs3 {
  width: 100px;
  height: 100px;
  bottom: 25%;
  right: 25%;
  animation-delay: 2s;
}

@keyframes spot-pulse {
  0%, 100% { transform: scale(1); opacity: 0.7; }
  50% { transform: scale(1.3); opacity: 1; }
}

.sunshine-effects .golden-shimmer {
  position: absolute;
  inset: 0;
  background: linear-gradient(45deg, 
    transparent 0%,
    rgba(255, 230, 100, 0.1) 25%,
    transparent 50%,
    rgba(255, 220, 80, 0.1) 75%,
    transparent 100%
  );
  background-size: 200% 200%;
  animation: shimmer-move 6s ease-in-out infinite;
}

@keyframes shimmer-move {
  0% { background-position: 0% 0%; }
  50% { background-position: 100% 100%; }
  100% { background-position: 0% 0%; }
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  max-width: 1000px;
  width: 100%;
}

.brand-intro {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30px;
}

.intro-logo {
  position: relative;
  width: 150px;
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.intro-icon {
  font-size: 80px;
  animation: intro-icon-float 3s ease-in-out infinite;
  position: relative;
  z-index: 1;
}

@keyframes intro-icon-float {
  0%, 100% { transform: translateY(0) rotateY(0deg); }
  50% { transform: translateY(-15px) rotateY(180deg); }
}

.intro-glow {
  position: absolute;
  inset: -20px;
  background: radial-gradient(circle, rgba(123, 47, 247, 0.4), transparent 70%);
  border-radius: 50%;
  animation: glow-pulse 2s ease-in-out infinite;
}

@keyframes glow-pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.2); opacity: 0.8; }
}

.intro-title {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.intro-main {
  font-size: 52px;
  font-weight: 800;
  background: linear-gradient(90deg, #00d4ff, #7b2ff7, #f107a3, #00d4ff);
  background-size: 300% 100%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradient-flow 5s linear infinite;
}

.sunrise .intro-main {
  background: linear-gradient(90deg, #c97b4a, #d4885a, #b86b3a, #c97b4a);
  background-size: 300% 100%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sunshine .intro-main {
  background: linear-gradient(90deg, #b8860b, #daa520, #b8860b, #daa520);
  background-size: 300% 100%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

@keyframes gradient-flow {
  0% { background-position: 0% 50%; }
  100% { background-position: 300% 50%; }
}

.intro-sub {
  font-size: 24px;
  font-weight: 300;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 4px;
}

.sunrise .intro-sub {
  color: rgba(100, 70, 50, 0.7);
}

.sunshine .intro-sub {
  color: rgba(100, 90, 30, 0.7);
}

.intro-hint {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 30px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  animation: hint-bounce 2s ease-in-out infinite;
}

.sunrise .intro-hint {
  background: rgba(200, 150, 100, 0.1);
  border-color: rgba(200, 150, 100, 0.2);
}

.sunshine .intro-hint {
  background: rgba(200, 180, 50, 0.1);
  border-color: rgba(200, 180, 50, 0.2);
}

@keyframes hint-bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.hint-icon {
  font-size: 20px;
  animation: point-down 1s ease-in-out infinite;
}

.hint-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.sunrise .hint-text {
  color: rgba(100, 70, 50, 0.6);
}

.sunshine .hint-text {
  color: rgba(100, 90, 30, 0.6);
}

@keyframes point-down {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(5px); }
}

.hint-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.main-content {
  animation: content-enter 0.8s ease-out;
}

@keyframes content-enter {
  0% { opacity: 0; transform: translateY(30px); }
  100% { opacity: 1; transform: translateY(0); }
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  background: rgba(123, 47, 247, 0.2);
  border: 1px solid rgba(123, 47, 247, 0.4);
  border-radius: 30px;
  margin-bottom: 24px;
  animation: badge-pulse 2s ease-in-out infinite;
}

@keyframes badge-pulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(123, 47, 247, 0.4); }
  50% { box-shadow: 0 0 0 10px rgba(123, 47, 247, 0); }
}

.badge-icon {
  font-size: 16px;
}

.hero-badge span:last-child {
  color: #c4b5fd;
  font-size: 14px;
  font-weight: 500;
}

.sunrise .hero-badge {
  background: rgba(200, 150, 100, 0.2);
  border-color: rgba(200, 150, 100, 0.4);
}

.sunrise .hero-badge span:last-child {
  color: #b89060;
}

.sunshine .hero-badge {
  background: rgba(200, 180, 50, 0.2);
  border-color: rgba(200, 180, 50, 0.4);
}

.sunshine .hero-badge span:last-child {
  color: #a09030;
}

.hero-title {
  margin: 0 0 24px;
  line-height: 1.2;
}

.title-line {
  display: block;
  font-size: 48px;
  font-weight: 700;
  color: white;
  margin-bottom: 8px;
}

.sunrise .title-line {
  color: #4a3020;
}

.sunshine .title-line {
  color: #2a2505;
}

.title-highlight {
  display: block;
  font-size: 56px;
  font-weight: 800;
  background: linear-gradient(90deg, #00d4ff, #7b2ff7, #f107a3);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradient-shift 5s ease infinite;
  background-size: 200% 200%;
}

.sunrise .title-highlight {
  background: linear-gradient(90deg, #c97b4a, #d4885a, #b86b3a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sunshine .title-highlight {
  background: linear-gradient(90deg, #b8860b, #daa520, #b8860b);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

@keyframes gradient-shift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.hero-description {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.6;
  margin-bottom: 40px;
  max-width: 700px;
  margin-left: auto;
  margin-right: auto;
}

.sunrise .hero-description {
  color: rgba(80, 60, 40, 0.8);
}

.sunshine .hero-description {
  color: rgba(70, 60, 20, 0.8);
}

.hero-stats {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 40px;
  padding: 24px 40px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.sunrise .hero-stats {
  background: rgba(200, 150, 100, 0.1);
  border-color: rgba(200, 150, 100, 0.2);
}

.sunshine .hero-stats {
  background: rgba(200, 180, 50, 0.1);
  border-color: rgba(200, 180, 50, 0.2);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.stat-number {
  font-size: 32px;
  font-weight: 800;
  background: linear-gradient(90deg, #00d4ff, #7b2ff7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sunrise .stat-number {
  background: linear-gradient(90deg, #c97b4a, #d4885a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sunshine .stat-number {
  background: linear-gradient(90deg, #b8860b, #daa520);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.sunrise .stat-label {
  color: rgba(100, 70, 50, 0.7);
}

.sunshine .stat-label {
  color: rgba(100, 90, 30, 0.7);
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
}

.sunrise .stat-divider {
  background: rgba(200, 150, 100, 0.3);
}

.sunshine .stat-divider {
  background: rgba(200, 180, 50, 0.3);
}

.brand-fade-enter-active,
.brand-fade-leave-active {
  transition: all 0.8s ease;
}

.brand-fade-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.brand-fade-leave-to {
  opacity: 0;
  transform: translateY(-30px) scale(0.95);
}

.section-fade-enter-active {
  transition: all 0.8s ease;
}

.section-fade-enter-from {
  opacity: 0;
  transform: translateY(40px);
}

.ai-features {
  padding: 80px 40px;
  max-width: 1400px;
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: 50px;
}

.section-icon {
  font-size: 40px;
  display: block;
  margin-bottom: 16px;
}

.section-header h2 {
  font-size: 36px;
  font-weight: 700;
  color: white;
  margin: 0;
}

.sunrise .section-header h2 {
  color: #4a3020;
}

.sunshine .section-header h2 {
  color: #3a3010;
}

.section-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.6);
  margin-top: 12px;
}

.sunrise .section-subtitle {
  color: rgba(80, 60, 40, 0.7);
}

.sunshine .section-subtitle {
  color: rgba(70, 60, 20, 0.7);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.feature-card {
  position: relative;
  padding: 32px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  text-align: center;
  transition: all 0.4s ease;
  overflow: hidden;
}

.sunrise .feature-card {
  background: rgba(200, 150, 100, 0.15);
  border-color: rgba(180, 130, 80, 0.3);
}

.sunshine .feature-card {
  background: rgba(200, 180, 50, 0.1);
  border-color: rgba(200, 180, 50, 0.2);
}

.feature-card:hover {
  transform: translateY(-8px);
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(123, 47, 247, 0.5);
}

.sunrise .feature-card:hover {
  background: rgba(200, 150, 100, 0.25);
  border-color: rgba(180, 130, 80, 0.5);
}

.sunshine .feature-card:hover {
  background: rgba(200, 180, 50, 0.2);
  border-color: rgba(200, 180, 50, 0.4);
}

.feature-glow {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at center, rgba(123, 47, 247, 0.2), transparent 70%);
  opacity: 0;
  transition: opacity 0.4s ease;
}

.sunrise .feature-glow {
  background: radial-gradient(circle at center, rgba(200, 150, 100, 0.2), transparent 70%);
}

.sunshine .feature-glow {
  background: radial-gradient(circle at center, rgba(200, 180, 50, 0.15), transparent 70%);
}

.feature-card:hover .feature-glow {
  opacity: 1;
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 20px;
  display: block;
}

.feature-title {
  font-size: 18px;
  font-weight: 600;
  color: white;
  margin: 0 0 12px;
}

.sunrise .feature-title {
  color: #3a2515;
}

.sunshine .feature-title {
  color: #2a2505;
}

.feature-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  line-height: 1.6;
  margin: 0;
}

.sunrise .feature-desc {
  color: rgba(60, 45, 30, 0.8);
}

.sunshine .feature-desc {
  color: rgba(50, 45, 15, 0.8);
}

.ai-models {
  padding: 80px 40px;
  max-width: 1400px;
  margin: 0 auto;
}

.models-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.model-card {
  position: relative;
  display: flex;
  gap: 24px;
  padding: 28px;
  background: rgba(255, 255, 255, 0.03);
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.4s ease;
}

.sunrise .model-card {
  background: rgba(200, 150, 100, 0.15);
  border-color: rgba(180, 130, 80, 0.3);
}

.sunshine .model-card {
  background: rgba(200, 180, 50, 0.1);
  border-color: rgba(200, 180, 50, 0.2);
}

.model-card:hover {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(123, 47, 247, 0.4);
  transform: translateX(8px);
}

.sunrise .model-card:hover {
  background: rgba(200, 150, 100, 0.25);
  border-color: rgba(180, 130, 80, 0.5);
}

.sunshine .model-card:hover {
  background: rgba(200, 180, 50, 0.2);
  border-color: rgba(200, 180, 50, 0.4);
}

.model-card.active {
  background: rgba(123, 47, 247, 0.1);
  border-color: #7b2ff7;
  box-shadow: 0 0 30px rgba(123, 47, 247, 0.3);
}

.sunrise .model-card.active {
  background: rgba(200, 150, 100, 0.3);
  border-color: #b87840;
  box-shadow: 0 0 30px rgba(200, 150, 100, 0.3);
}

.sunshine .model-card.active {
  background: rgba(200, 180, 50, 0.25);
  border-color: #b8860b;
  box-shadow: 0 0 30px rgba(200, 180, 50, 0.3);
}

.model-header {
  position: relative;
}

.model-avatar {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, rgba(123, 47, 247, 0.3), rgba(0, 212, 255, 0.3));
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid rgba(255, 255, 255, 0.1);
}

.sunrise .model-avatar {
  background: linear-gradient(135deg, rgba(200, 150, 100, 0.4), rgba(180, 130, 80, 0.4));
  border-color: rgba(180, 130, 80, 0.3);
}

.sunshine .model-avatar {
  background: linear-gradient(135deg, rgba(200, 180, 50, 0.3), rgba(180, 160, 40, 0.3));
  border-color: rgba(200, 180, 50, 0.3);
}

.avatar-emoji {
  font-size: 32px;
}

.model-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  padding: 4px 10px;
  background: linear-gradient(90deg, #f107a3, #7b2ff7);
  border-radius: 10px;
  font-size: 10px;
  font-weight: 600;
  color: white;
}

.sunrise .model-badge {
  background: linear-gradient(90deg, #d4885a, #c97b4a);
}

.sunshine .model-badge {
  background: linear-gradient(90deg, #daa520, #b8860b);
}

.model-info {
  flex: 1;
}

.model-name {
  font-size: 20px;
  font-weight: 700;
  color: white;
  margin: 0 0 8px;
}

.sunrise .model-name {
  color: #3a2515;
}

.sunshine .model-name {
  color: #2a2505;
}

.model-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  line-height: 1.5;
  margin: 0 0 16px;
}

.sunrise .model-desc {
  color: rgba(60, 45, 30, 0.8);
}

.sunshine .model-desc {
  color: rgba(50, 45, 15, 0.8);
}

.model-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.sunrise .tag {
  background: rgba(180, 130, 80, 0.2);
  color: rgba(80, 60, 40, 0.9);
}

.sunshine .tag {
  background: rgba(180, 160, 40, 0.2);
  color: rgba(70, 60, 20, 0.9);
}

.model-select {
  display: flex;
  align-items: center;
}

.select-circle {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.model-card.active .select-circle {
  background: #7b2ff7;
  border-color: #7b2ff7;
}

.select-circle .el-icon {
  color: white;
  font-size: 16px;
}

.ai-cta {
  padding: 80px 40px;
  max-width: 1000px;
  margin: 0 auto;
  text-align: center;
}

.cta-content {
  position: relative;
  padding: 48px;
  background: linear-gradient(135deg, rgba(123, 47, 247, 0.2), rgba(0, 212, 255, 0.1));
  border-radius: 30px;
  border: 1px solid rgba(123, 47, 247, 0.3);
  margin-bottom: 32px;
}

.sunrise .cta-content {
  background: linear-gradient(135deg, rgba(200, 150, 100, 0.25), rgba(180, 130, 80, 0.15));
  border-color: rgba(180, 130, 80, 0.4);
}

.sunshine .cta-content {
  background: linear-gradient(135deg, rgba(200, 180, 50, 0.25), rgba(180, 160, 40, 0.15));
  border-color: rgba(180, 160, 40, 0.4);
}

.cta-decor-left, .cta-decor-right {
  position: absolute;
  font-size: 48px;
  opacity: 0.5;
  animation: decor-bounce 2s ease-in-out infinite;
}

.cta-decor-left {
  top: 20px;
  left: 30px;
}

.cta-decor-right {
  bottom: 20px;
  right: 30px;
  animation-delay: 1s;
}

@keyframes decor-bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.cta-text h2 {
  font-size: 32px;
  font-weight: 700;
  color: white;
  margin: 0 0 12px;
}

.sunrise .cta-text h2 {
  color: #3a2515;
}

.sunshine .cta-text h2 {
  color: #2a2505;
}

.cta-text p {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0;
}

.sunrise .cta-text p {
  color: rgba(60, 45, 30, 0.8);
}

.sunshine .cta-text p {
  color: rgba(50, 45, 15, 0.8);
}

.start-btn {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 18px 48px;
  background: linear-gradient(90deg, #7b2ff7, #f107a3);
  border: none;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.4s ease;
  position: relative;
  overflow: hidden;
}

.sunrise .start-btn {
  background: linear-gradient(90deg, #c97b4a, #d4885a);
}

.sunshine .start-btn {
  background: linear-gradient(90deg, #b8860b, #daa520);
}

.start-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, #f107a3, #7b2ff7);
  opacity: 0;
  transition: opacity 0.4s ease;
}

.sunrise .start-btn::before {
  background: linear-gradient(90deg, #d4885a, #c97b4a);
}

.sunshine .start-btn::before {
  background: linear-gradient(90deg, #daa520, #b8860b);
}

.start-btn:hover::before {
  opacity: 1;
}

.start-btn:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 40px rgba(123, 47, 247, 0.5);
}

.sunrise .start-btn:hover {
  box-shadow: 0 10px 40px rgba(200, 150, 100, 0.5);
}

.sunshine .start-btn:hover {
  box-shadow: 0 10px 40px rgba(200, 180, 50, 0.5);
}

.btn-icon, .btn-text, .btn-arrow {
  position: relative;
  z-index: 1;
}

.btn-icon {
  font-size: 24px;
}

.btn-text {
  font-size: 18px;
  font-weight: 600;
  color: white;
}

.btn-arrow {
  font-size: 20px;
  color: white;
  transition: transform 0.3s ease;
}

.start-btn:hover .btn-arrow {
  transform: translateX(5px);
}

.ai-footer {
  text-align: center;
  padding: 40px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.sunrise .ai-footer {
  border-top-color: rgba(180, 130, 80, 0.2);
}

.sunshine .ai-footer {
  border-top-color: rgba(180, 160, 40, 0.2);
}

.ai-footer p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.sunrise .ai-footer p {
  color: rgba(100, 70, 50, 0.6);
}

.sunshine .ai-footer p {
  color: rgba(100, 90, 30, 0.6);
}

@media (max-width: 1200px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .header-content {
    padding: 12px 20px;
  }
  
  .intro-main {
    font-size: 36px;
  }
  
  .intro-sub {
    font-size: 16px;
  }
  
  .title-line {
    font-size: 32px;
  }
  
  .title-highlight {
    font-size: 40px;
  }
  
  .hero-stats {
    flex-direction: column;
    gap: 20px;
  }
  
  .stat-divider {
    width: 60px;
    height: 1px;
  }
  
  .features-grid,
  .models-container {
    grid-template-columns: 1fr;
  }
  
  .model-card {
    flex-direction: column;
    text-align: center;
  }
  
  .model-tags {
    justify-content: center;
  }
}

.ai-home.default {
  background: linear-gradient(180deg, #ffffff 0%, #fafafa 30%, #f5f5f5 60%, #f0f0f0 100%);
}

.default .ai-header {
  background: rgba(255, 255, 255, 0.98);
  border-bottom-color: rgba(0, 0, 0, 0.08);
}

.default .brand-name {
  background: linear-gradient(90deg, #333, #666);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.default .brand-english {
  color: rgba(100, 100, 100, 0.6);
}

.default .nav-link {
  color: rgba(60, 60, 60, 0.8);
  background: rgba(0, 0, 0, 0.03);
  border-color: rgba(0, 0, 0, 0.08);
}

.default .nav-link:hover {
  background: rgba(0, 0, 0, 0.06);
  color: #333;
}

.default .theme-switch {
  color: rgba(60, 60, 60, 0.8);
  background: rgba(0, 0, 0, 0.03);
  border-color: rgba(0, 0, 0, 0.08);
}

.default .theme-switch:hover {
  background: rgba(0, 0, 0, 0.06);
}

.default .theme-dropdown :deep(.el-dropdown-menu) {
  background: rgba(255, 255, 255, 0.98);
  border-color: rgba(0, 0, 0, 0.1);
}

.default .theme-dropdown :deep(.el-dropdown-menu__item) {
  color: rgba(60, 60, 60, 0.9);
}

.default .theme-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(0, 0, 0, 0.08);
  color: #333;
}

.default .theme-dropdown :deep(.el-dropdown-menu__item.active) {
  background: rgba(0, 0, 0, 0.1);
  color: #333;
}

.default .lang-switch {
  color: rgba(60, 60, 60, 0.8);
  background: rgba(0, 0, 0, 0.03);
  border-color: rgba(0, 0, 0, 0.08);
}

.default .lang-switch:hover {
  background: rgba(0, 0, 0, 0.06);
}

.default .shape-1 { background: radial-gradient(circle, rgba(200, 200, 200, 0.15), transparent); }
.default .shape-2 { background: radial-gradient(circle, rgba(180, 180, 180, 0.12), transparent); }
.default .shape-3 { background: radial-gradient(circle, rgba(160, 160, 160, 0.1), transparent); }
.default .shape-4 { background: radial-gradient(circle, rgba(170, 170, 170, 0.1), transparent); }
.default .shape-5 { background: radial-gradient(circle, rgba(150, 150, 150, 0.08), transparent); }

.default .intro-main {
  color: #333;
}

.default .intro-sub {
  color: rgba(80, 80, 80, 0.8);
}

.default .intro-hint {
  color: rgba(100, 100, 100, 0.6);
}

.default .hint-text {
  color: rgba(100, 100, 100, 0.5);
}

.default .hero-badge {
  background: rgba(0, 0, 0, 0.05);
  border-color: rgba(0, 0, 0, 0.1);
}

.default .hero-badge span:last-child {
  color: #666;
}

.default .title-line {
  color: #333;
}

.default .title-highlight {
  background: linear-gradient(90deg, #333, #666);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.default .hero-description {
  color: rgba(80, 80, 80, 0.8);
}

.default .hero-stats {
  background: rgba(0, 0, 0, 0.03);
  border-color: rgba(0, 0, 0, 0.08);
}

.default .stat-number {
  color: #333;
}

.default .stat-label {
  color: #666;
}

.default .stat-divider {
  background: rgba(0, 0, 0, 0.1);
}

.default .section-header h2 {
  color: #333;
}

.default .section-subtitle {
  color: rgba(80, 80, 80, 0.7);
}

.default .feature-card {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(0, 0, 0, 0.08);
}

.default .feature-card:hover {
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(0, 0, 0, 0.15);
  transform: translateY(-5px);
}

.default .feature-glow {
  background: linear-gradient(135deg, rgba(100, 100, 100, 0.1), transparent);
}

.default .feature-title {
  color: #333;
}

.default .feature-desc {
  color: rgba(80, 80, 80, 0.8);
}

.default .model-card {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(0, 0, 0, 0.08);
}

.default .model-card:hover {
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(0, 0, 0, 0.15);
}

.default .model-card.active {
  border-color: rgba(100, 100, 100, 0.4);
  background: rgba(255, 255, 255, 0.98);
}

.default .model-avatar {
  background: linear-gradient(135deg, #888, #aaa);
}

.default .model-badge {
  background: rgba(0, 0, 0, 0.08);
  color: #666;
}

.default .model-name {
  color: #333;
}

.default .model-desc {
  color: rgba(80, 80, 80, 0.7);
}

.default .tag {
  background: rgba(0, 0, 0, 0.05);
  color: #666;
}

.default .cta-content {
  background: rgba(0, 0, 0, 0.03);
  border-color: rgba(0, 0, 0, 0.08);
}

.default .cta-text h2 {
  color: #333;
}

.default .cta-text p {
  color: rgba(80, 80, 80, 0.8);
}

.default .start-btn {
  background: linear-gradient(135deg, #666, #888);
}

.default .start-btn::before {
  background: linear-gradient(135deg, #555, #777);
}

.default .start-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(100, 100, 100, 0.3);
}

.default .ai-footer {
  background: rgba(255, 255, 255, 0.95);
  border-top-color: rgba(0, 0, 0, 0.08);
}

.default .ai-footer p {
  color: rgba(100, 100, 100, 0.6);
}
</style>
