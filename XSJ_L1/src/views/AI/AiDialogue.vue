<template>
  <div class="ai-dialogue" :class="currentTheme">
    <div class="dialogue-sidebar">
      <div class="sidebar-header">
        <div class="brand">
          <span class="brand-icon">🌍</span>
          <span class="brand-name">{{ t('brand.name') }}</span>
        </div>
        <button class="new-chat-btn" @click="createNewChat">
          <span class="btn-icon">✨</span>
          <span class="btn-text">{{ t('ai.newChat') }}</span>
        </button>
      </div>
      
      <div class="chat-history">
        <div class="history-header">
          <span>{{ t('ai.chatHistory') }}</span>
          <span class="history-count">{{ chatHistory.length }}</span>
        </div>
        
        <div class="history-list" v-if="chatHistory.length > 0">
          <div 
            class="history-item" 
            v-for="chat in chatHistory" 
            :key="chat.id"
            :class="{ active: currentSessionId === chat.id }"
            @click="selectChat(chat.id)"
          >
            <div class="history-icon">💬</div>
            <div class="history-info">
              <div class="history-title">{{ chat.title }}</div>
              <div class="history-meta">
                <span class="history-model">
                  <span class="model-emoji">{{ getModelEmoji(chat.aiId) }}</span>
                  <span class="model-name-text">{{ getModelName(chat.aiId) }}</span>
                </span>
                <span class="history-time">{{ chat.time }}</span>
              </div>
            </div>
            <div class="history-actions">
              <button class="action-btn" @click.stop="deleteChat(chat.id)">
                <el-icon><el-icon-delete /></el-icon>
              </button>
            </div>
          </div>
        </div>
        
        <div class="history-empty" v-else>
          <div class="empty-icon">📝</div>
          <div class="empty-text">暂无会话记录</div>
          <div class="empty-hint">点击上方按钮开始新对话</div>
        </div>
      </div>
      
      <div class="sidebar-footer">
        <el-dropdown trigger="click" @command="selectModel" class="model-dropdown">
          <div class="model-selector">
            <span class="model-label">{{ t('ai.currentModel') }}</span>
            <div class="model-value">
              <span class="model-emoji">{{ currentModel.emoji }}</span>
              <span class="model-name">{{ currentModel.name || '选择模型' }}</span>
              <el-icon class="model-arrow"><el-icon-arrow-down /></el-icon>
            </div>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item 
                v-for="model in aiModels" 
                :key="model.id" 
                :command="model.id"
                :class="{ active: currentModel.id === model.id }"
              >
                <span class="model-item-emoji">{{ model.emoji }}</span>
                <span class="model-item-name">{{ model.name }}</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <div class="user-info">
          <div class="user-avatar">👤</div>
          <div class="user-name">{{ t('ai.user') }}</div>
        </div>
      </div>
    </div>
    
    <div class="dialogue-main">
      <div class="main-header">
        <div class="header-title">
          <span class="title-icon">🤖</span>
          <span>{{ currentModel.name }}</span>
        </div>
        <div class="header-actions">
          <button class="header-btn home-btn" @click="goHome">
            <el-icon><el-icon-home-filled /></el-icon>
            <span>{{ t('ai.backHome') }}</span>
          </button>
          <el-dropdown trigger="click" @command="changeTheme" class="theme-dropdown">
            <div class="theme-switch">
              <span class="theme-icon">{{ currentThemeInfo.icon }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="sunrise">🌅 旭日初升</el-dropdown-item>
                <el-dropdown-item command="sunshine">☀️ 日曜中天</el-dropdown-item>
                <el-dropdown-item command="starlight">✨ 紫宸星阑</el-dropdown-item>
                <el-dropdown-item command="default">⚪ 默认</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <button class="header-btn" @click="clearMessages">
            <el-icon><el-icon-delete /></el-icon>
            <span>{{ t('ai.clearChat') }}</span>
          </button>
          <el-dropdown trigger="click" @command="changeLocale" class="lang-dropdown">
            <div class="lang-switch">
              <span class="lang-icon">🌐</span>
              <span class="lang-text">{{ currentLang }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="zh">🇨🇳 中文</el-dropdown-item>
                <el-dropdown-item command="en">🇺🇸 English</el-dropdown-item>
                <el-dropdown-item command="ja">🇯🇵 日本語</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <div class="messages-container" ref="messagesRef">
        <div class="messages-wrapper">
          <div v-if="messages.length === 0" class="welcome-section">
            <div class="welcome-avatar">{{ currentModel.emoji }}</div>
            <h2 class="welcome-title">你好！我是 {{ currentModel.name }}</h2>
            <p class="welcome-subtitle">有什么我可以帮助你的吗？</p>
            <div class="welcome-suggestions">
              <div class="suggestion-item" @click="useSuggestion('请介绍一下你自己')">
                <span class="suggestion-icon">👋</span>
                <span class="suggestion-text">请介绍一下你自己</span>
              </div>
              <div class="suggestion-item" @click="useSuggestion('你能帮我做什么？')">
                <span class="suggestion-icon">💡</span>
                <span class="suggestion-text">你能帮我做什么？</span>
              </div>
              <div class="suggestion-item" @click="useSuggestion('给我讲个有趣的故事')">
                <span class="suggestion-icon">📖</span>
                <span class="suggestion-text">给我讲个有趣的故事</span>
              </div>
              <div class="suggestion-item" @click="useSuggestion('帮我写一段代码')">
                <span class="suggestion-icon">💻</span>
                <span class="suggestion-text">帮我写一段代码</span>
              </div>
            </div>
          </div>
          
          <template v-else>
            <div 
              class="message" 
              v-for="msg in messages" 
              :key="msg.id"
              :class="msg.role"
            >
              <div class="message-avatar">
                <span v-if="msg.role === 'user'">👤</span>
                <span v-else class="ai-avatar">{{ currentModel.emoji }}</span>
              </div>
              <div class="message-content">
                <div class="message-header">
                  <span class="message-name">{{ msg.role === 'user' ? t('ai.you') : currentModel.name }}</span>
                  <span class="message-time">{{ msg.time }}</span>
                </div>
                <div class="message-text" v-html="formatMessage(msg.content)"></div>
                <div class="message-actions" v-if="msg.role === 'assistant'">
                  <button class="msg-action" @click="copyMessage(msg.content)">
                    <el-icon><el-icon-document-copy /></el-icon>
                  </button>
                  <button class="msg-action" @click="regenerate(msg.id)">
                    <el-icon><el-icon-refresh /></el-icon>
                  </button>
                </div>
              </div>
            </div>
            
            <div class="typing-indicator" v-if="isTyping && messages.length > 0 && messages[messages.length - 1].role === 'user'">
              <div class="typing-avatar">{{ currentModel.emoji }}</div>
              <div class="typing-dots">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </template>
        </div>
      </div>
      
      <div class="input-area">
        <div class="input-container">
          <div class="input-wrapper">
            <textarea 
              v-model="inputMessage"
              :placeholder="t('ai.inputPlaceholder')"
              @keydown.enter.exact.prevent="sendMessage"
              @keydown.enter.shift.exact="inputMessage += '\n'"
              rows="1"
              ref="textareaRef"
            ></textarea>
            <div class="input-actions">
              <button class="attach-btn" @click="attachFile">
                <el-icon><el-icon-paperclip /></el-icon>
              </button>
              <button 
                class="send-btn" 
                :class="{ active: inputMessage.trim() }"
                @click="sendMessage"
                :disabled="!inputMessage.trim() || isTyping"
              >
                <el-icon><el-icon-promotion /></el-icon>
              </button>
            </div>
          </div>
          <div class="input-hint">
            <span>{{ t('ai.inputHint') }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { getAiThemeService, updateAiThemeService, getAiModelsService, getConversationListService, getChatHistoryService } from '@/api/Ai'
import { userTokenStore } from '@/stores/token'

const { t, locale } = useI18n()
const router = useRouter()
const route = useRoute()

const messagesRef = ref(null)
const textareaRef = ref(null)
const inputMessage = ref('')
const isTyping = ref(false)
const currentSessionId = ref(null)
const currentTheme = ref('starlight')
const messages = ref([])

const themes = {
  sunrise: { id: 'sunrise', icon: '🌅', name: '旭日初升' },
  sunshine: { id: 'sunshine', icon: '☀️', name: '日曜中天' },
  starlight: { id: 'starlight', icon: '✨', name: '紫宸星阑' },
  default: { id: 'default', icon: '⚪', name: '默认' }
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
  try {
    const honeValue = Object.keys(themeMap).find(key => themeMap[key] === theme)
    if (honeValue) {
      await updateAiThemeService(honeValue)
    }
  } catch (error) {
    console.log('保存主题设置失败')
  }
}

const currentLang = computed(() => {
  const langMap = { zh: '中文', en: 'English', ja: '日本語' }
  return langMap[locale.value] || '中文'
})

const changeLocale = (lang) => {
  locale.value = lang
}

const defaultModelImages = ['🤖', '⚡', '🧠', '💎', '🌟', '🔮']

const aiModels = ref([])
const currentModel = ref({
  id: '',
  name: '',
  emoji: '🤖'
})

const processModels = (models) => {
  return models.map((model, index) => ({
    id: model.modelCode,
    name: model.modelName,
    emoji: model.image || defaultModelImages[index % defaultModelImages.length],
    status: model.status
  }))
}

const selectModel = (modelId) => {
  const model = aiModels.value.find(m => m.id === modelId)
  if (model && model.id !== currentModel.value.id) {
    currentModel.value = model
    currentSessionId.value = null
    messages.value = []
    ElMessage.success(`已切换到: ${model.name}，开始新对话`)
  }
}

const chatHistory = ref([])


const loadConversationList = async () => {
  try {
    const res = await getConversationListService()
    if (res.code === 0 && res.data) {
      chatHistory.value = res.data.map(item => ({
        id: item.userAndAi,
        title: item.convTitle,
        time: item.updateTime,
        aiId: item.aiId
      }))
    }
  } catch (error) {
    console.log('获取会话列表失败')
  }
}

const createNewChat = () => {
  currentSessionId.value = null
  messages.value = []
  ElMessage.success(t('ai.chatCreated'))
}

const useSuggestion = (text) => {
  inputMessage.value = text
  sendMessage()
}

const selectChat = async (id) => {
  currentSessionId.value = id
  messages.value = []
  
  const chat = chatHistory.value.find(c => c.id === id)
  if (chat && chat.aiId) {
    const model = aiModels.value.find(m => m.id === chat.aiId)
    if (model) {
      currentModel.value = model
    }
  }
  
  try {
    const res = await getChatHistoryService(id)
    if (res.code === 0 && res.data && res.data.length > 0) {
      messages.value = res.data.map(item => ({
        id: item.id,
        role: item.role === 'ai' ? 'assistant' : item.role,
        content: item.content || '',
        time: new Date(item.chatTime).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      }))
      await nextTick()
      scrollToBottom()
    }
  } catch (error) {
    console.log('加载历史消息失败', error)
  }
}

const deleteChat = (id) => {
  chatHistory.value = chatHistory.value.filter(c => c.id !== id)
  ElMessage.success(t('ai.chatDeleted'))
}

const getModelEmoji = (modelCode) => {
  const model = aiModels.value.find(m => m.id === modelCode)
  return model ? model.emoji : '🤖'
}

const getModelName = (modelCode) => {
  const model = aiModels.value.find(m => m.id === modelCode)
  if (model) {
    return model.name.length > 8 ? model.name.substring(0, 8) + '...' : model.name
  }
  return modelCode || 'AI'
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || isTyping.value) return
  
  const userMsg = {
    id: Date.now(),
    role: 'user',
    content: inputMessage.value.trim(),
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  
  messages.value.push(userMsg)
  const userContent = inputMessage.value.trim()
  inputMessage.value = ''
  
  await nextTick()
  scrollToBottom()
  
  isTyping.value = true
  
  const aiMsgId = Date.now() + 1
  const aiMsgIndex = messages.value.length
  messages.value.push({
    id: aiMsgId,
    role: 'assistant',
    content: '',
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  })
  
  try {
    const requestBody = {
      content: userContent,
      modelCode: currentModel.value.id
    }
    
    if (currentSessionId.value) {
      requestBody.sessionId = currentSessionId.value
    }
    
    const tokenStore = userTokenStore()
    const headers = {
      'Content-Type': 'application/json'
    }
    if (tokenStore.token) {
      headers['Authorization'] = tokenStore.token
    }
    
    const response = await fetch('/api/ai/chat/stream', {
      method: 'POST',
      headers,
      body: JSON.stringify(requestBody)
    })
    
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''
    
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      
      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''
      
      for (const line of lines) {
        if (!line.trim()) continue
        
        let jsonStr = line
        if (line.startsWith('data:')) {
          jsonStr = line.substring(5).trim()
        }
        
        if (!jsonStr || jsonStr === '[DONE]') continue
        
        try {
          const data = JSON.parse(jsonStr)
          if (data.chunkContent && data.chunkContent !== null && data.chunkContent !== 'null') {
            messages.value[aiMsgIndex].content += data.chunkContent
            await nextTick()
            scrollToBottom()
          }
          if (data.isFinish && data.sessionId) {
            currentSessionId.value = data.sessionId
            await loadConversationList()
          }
        } catch (e) {
          console.log('解析SSE数据失败', e, jsonStr)
        }
      }
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    aiMsg.content = '抱歉，发生了错误，请稍后重试。'
  } finally {
    isTyping.value = false
  }
}

const generateAIResponse = (question) => {
  const responses = [
    `这是一个很好的问题！关于"${question}"，让我来为你详细解答...\n\n首先，我们需要理解基本概念。然后，通过实践来加深理解。\n\n需要我进一步解释吗？`,
    `感谢你的提问！针对"${question}"，我有以下几点建议：\n\n1. 从基础开始，循序渐进\n2. 多动手实践\n3. 遇到问题及时记录和总结\n\n希望这些建议对你有帮助！`,
    `这是一个有趣的话题！关于"${question}"：\n\n**核心要点：**\n- 理解原理比记忆语法更重要\n- 实践是最好的学习方式\n- 保持好奇心和探索精神\n\n还有什么想了解的吗？`
  ]
  return responses[Math.floor(Math.random() * responses.length)]
}

const scrollToBottom = () => {
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

const formatMessage = (content) => {
  if (!content || content === 'null') return ''
  return content
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
    .replace(/`(.*?)`/g, '<code>$1</code>')
    .replace(/\n/g, '<br>')
}

const copyMessage = (content) => {
  navigator.clipboard.writeText(content)
  ElMessage.success(t('ai.copied'))
}

const regenerate = (id) => {
  ElMessage.info(t('ai.regenerating'))
}

const clearMessages = () => {
  messages.value = []
  ElMessage.success(t('ai.chatCleared'))
}

const goHome = () => {
  router.push('/ai/home')
}

const attachFile = () => {
  ElMessage.info(t('ai.attachHint'))
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
        currentModel.value = aiModels.value[0]
      }
    }
  } catch (error) {
    console.log('获取模型列表失败')
  }
  try {
    await loadConversationList()
  } catch (error) {
    console.log('获取会话列表失败')
  }
  
  const queryMessage = route.query.q
  const queryModel = route.query.model
  
  if (queryModel && aiModels.value.length > 0) {
    const model = aiModels.value.find(m => m.id === queryModel)
    if (model) {
      currentModel.value = model
    }
  }
  
  if (queryMessage) {
    inputMessage.value = queryMessage
    await nextTick()
    sendMessage()
  }
  
  if (textareaRef.value) {
    textareaRef.value.focus()
  }
})
</script>

<style scoped>
.ai-dialogue {
  display: flex;
  height: 100vh;
  font-family: 'Segoe UI', system-ui, sans-serif;
  transition: all 0.5s ease;
}

.ai-dialogue.sunrise {
  background: linear-gradient(180deg, #fdf6e3 0%, #fef3e2 30%, #fff5eb 60%, #fef7ed 100%);
}

.ai-dialogue.sunshine {
  background: linear-gradient(180deg, #fffbf0 0%, #fff8e7 30%, #fff4d6 60%, #ffefc2 100%);
}

.ai-dialogue.starlight {
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3e 100%);
}

.dialogue-sidebar {
  width: 280px;
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  flex-direction: column;
  backdrop-filter: blur(20px);
  transition: all 0.5s ease;
}

.sunrise .dialogue-sidebar {
  background: rgba(253, 246, 227, 0.95);
  border-right-color: rgba(180, 130, 80, 0.2);
}

.sunshine .dialogue-sidebar {
  background: linear-gradient(180deg, rgba(255, 250, 235, 0.98), rgba(255, 248, 225, 0.98));
  border-right-color: rgba(218, 165, 32, 0.2);
}

.starlight .dialogue-sidebar {
  background: rgba(15, 15, 35, 0.95);
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sunrise .sidebar-header {
  border-bottom-color: rgba(180, 130, 80, 0.2);
}

.sunshine .sidebar-header {
  border-bottom-color: rgba(218, 165, 32, 0.15);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.brand-icon {
  font-size: 28px;
  animation: globe-spin 10s linear infinite;
}

@keyframes globe-spin {
  from { transform: rotateY(0deg); }
  to { transform: rotateY(360deg); }
}

.brand-name {
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(90deg, #00d4ff, #7b2ff7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.sunrise .brand-name {
  background: linear-gradient(90deg, #c97b4a, #d4885a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.sunshine .brand-name {
  background: linear-gradient(90deg, #b8860b, #daa520, #ffd700);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.new-chat-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px;
  background: linear-gradient(135deg, #7b2ff7, #f107a3);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sunrise .new-chat-btn {
  background: linear-gradient(135deg, #c97b4a, #d4885a);
}

.sunshine .new-chat-btn {
  background: linear-gradient(135deg, #daa520, #ffd700);
}

.new-chat-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(123, 47, 247, 0.4);
}

.sunrise .new-chat-btn:hover {
  box-shadow: 0 8px 25px rgba(200, 150, 100, 0.4);
}

.sunshine .new-chat-btn:hover {
  box-shadow: 0 8px 25px rgba(218, 165, 32, 0.35);
}

.btn-icon {
  font-size: 18px;
}

.chat-history {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.history-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.sunrise .history-header {
  color: rgba(100, 70, 50, 0.6);
}

.sunshine .history-header {
  color: rgba(160, 130, 50, 0.8);
}

.history-count {
  background: rgba(123, 47, 247, 0.3);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 11px;
}

.sunrise .history-count {
  background: rgba(200, 150, 100, 0.3);
}

.sunshine .history-count {
  background: rgba(218, 165, 32, 0.2);
  color: #8b6914;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 8px;
}

.history-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-text{
  font-size: 16px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 8px;
}

.sunrise .empty-text{
  color: rgba(100, 70, 50, 0.6);
}

.sunshine .empty-text{
  color: rgba(160, 130, 50, 0.7);
}

.default .empty-text{
  color: rgba(100, 100, 100, 0.6);
}

.empty-hint{
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.sunrise .empty-hint{
  color: rgba(100, 70, 50, 0.4);
}

.sunshine .empty-hint{
  color: rgba(160, 130, 50, 0.5);
}

.default .empty-hint{
  color: rgba(100, 100, 100, 0.4);
}

.history-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.sunrise .history-item {
  background: rgba(200, 150, 100, 0.1);
}

.sunshine .history-item {
  background: rgba(255, 250, 235, 0.6);
  border: 1px solid transparent;
}

.history-item:hover {
  background: rgba(255, 255, 255, 0.08);
}

.sunrise .history-item:hover {
  background: rgba(200, 150, 100, 0.2);
}

.sunshine .history-item:hover {
  background: rgba(255, 248, 220, 0.9);
  border-color: rgba(218, 165, 32, 0.2);
}

.history-item.active {
  background: rgba(123, 47, 247, 0.2);
  border-color: rgba(123, 47, 247, 0.5);
}

.sunrise .history-item.active {
  background: rgba(200, 150, 100, 0.25);
  border-color: rgba(180, 130, 80, 0.5);
}

.sunshine .history-item.active {
  background: linear-gradient(135deg, rgba(255, 248, 220, 0.95), rgba(255, 245, 210, 0.95));
  border-color: rgba(218, 165, 32, 0.4);
}

.history-icon {
  font-size: 20px;
}

.history-info {
  flex: 1;
  min-width: 0;
}

.history-title {
  font-size: 14px;
  color: white;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sunrise .history-title {
  color: #3a2515;
}

.sunshine .history-title {
  color: #5a4805;
}

.history-time {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.4);
}

.history-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 4px;
}

.history-model {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
}

.model-emoji {
  font-size: 12px;
}

.model-name-text {
  max-width: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sunrise .history-time {
  color: rgba(100, 70, 50, 0.6);
}

.sunshine .history-time {
  color: rgba(160, 130, 50, 0.7);
}

.sunrise .history-model {
  color: rgba(100, 70, 50, 0.6);
}

.sunshine .history-model {
  color: rgba(160, 130, 50, 0.7);
}

.default .history-model {
  color: rgba(100, 100, 100, 0.6);
}

.history-actions {
  opacity: 0;
  transition: opacity 0.3s ease;
}

.history-item:hover .history-actions {
  opacity: 1;
}

.action-btn {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.sunrise .action-btn {
  color: rgba(100, 70, 50, 0.5);
}

.sunshine .action-btn {
  color: rgba(160, 130, 50, 0.7);
}

.action-btn:hover {
  color: #f107a3;
  background: rgba(241, 7, 163, 0.2);
}

.sunrise .action-btn:hover {
  color: #b87840;
  background: rgba(200, 150, 100, 0.2);
}

.sunshine .action-btn:hover {
  background: rgba(218, 165, 32, 0.15);
  color: #8b6914;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.sunrise .sidebar-footer {
  border-top-color: rgba(180, 130, 80, 0.2);
}

.sunshine .sidebar-footer {
  border-top-color: rgba(218, 165, 32, 0.15);
}

.model-selector {
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  margin-bottom: 12px;
}

.sunrise .model-selector {
  background: rgba(200, 150, 100, 0.1);
}

.sunshine .model-selector {
  background: rgba(255, 250, 230, 0.8);
  border: 1px solid rgba(218, 165, 32, 0.15);
}

.model-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.sunrise .model-label {
  color: rgba(100, 70, 50, 0.6);
}

.sunshine .model-label {
  color: rgba(160, 130, 50, 0.8);
}

.model-value {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
}

.model-emoji {
  font-size: 20px;
}

.model-arrow {
  margin-left: auto;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  transition: transform 0.3s ease;
}

.sunrise .model-arrow {
  color: rgba(100, 70, 50, 0.6);
}

.sunshine .model-arrow {
  color: rgba(160, 130, 50, 0.7);
}

.default .model-arrow {
  color: rgba(100, 100, 100, 0.6);
}

.model-dropdown {
  width: 100%;
}

.model-dropdown :deep(.el-dropdown-menu) {
  background: rgba(30, 20, 50, 0.98);
  border: 1px solid rgba(123, 47, 247, 0.2);
  border-radius: 12px;
  padding: 8px;
  min-width: 180px;
}

.sunrise .model-dropdown :deep(.el-dropdown-menu) {
  background: rgba(255, 248, 240, 0.98);
  border-color: rgba(200, 150, 100, 0.2);
}

.sunshine .model-dropdown :deep(.el-dropdown-menu) {
  background: rgba(255, 252, 235, 0.98);
  border-color: rgba(218, 165, 32, 0.2);
}

.default .model-dropdown :deep(.el-dropdown-menu) {
  background: rgba(255, 255, 255, 0.98);
  border-color: rgba(0, 0, 0, 0.1);
}

.model-dropdown :deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.9);
  transition: all 0.3s ease;
}

.sunrise .model-dropdown :deep(.el-dropdown-menu__item) {
  color: rgba(80, 60, 40, 0.9);
}

.sunshine .model-dropdown :deep(.el-dropdown-menu__item) {
  color: rgba(90, 70, 20, 0.9);
}

.default .model-dropdown :deep(.el-dropdown-menu__item) {
  color: rgba(60, 60, 60, 0.9);
}

.model-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(123, 47, 247, 0.2);
  color: #fff;
}

.sunrise .model-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(200, 150, 100, 0.2);
  color: #4a3525;
}

.sunshine .model-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(255, 248, 220, 0.95);
  color: #5a4805;
}

.default .model-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(0, 0, 0, 0.08);
  color: #333;
}

.model-dropdown :deep(.el-dropdown-menu__item.active) {
  background: rgba(123, 47, 247, 0.3);
  color: #fff;
}

.sunrise .model-dropdown :deep(.el-dropdown-menu__item.active) {
  background: rgba(200, 150, 100, 0.3);
  color: #4a3525;
}

.sunshine .model-dropdown :deep(.el-dropdown-menu__item.active) {
  background: rgba(218, 165, 32, 0.25);
  color: #5a4805;
}

.default .model-dropdown :deep(.el-dropdown-menu__item.active) {
  background: rgba(0, 0, 0, 0.1);
  color: #333;
}

.model-item-emoji {
  font-size: 18px;
}

.model-item-name {
  font-size: 14px;
}

.model-name {
  font-size: 14px;
  color: white;
  font-weight: 500;
}

.sunrise .model-name {
  color: #3a2515;
}

.sunshine .model-name {
  color: #5a4805;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 10px;
}

.sunrise .user-info {
  background: rgba(200, 150, 100, 0.1);
}

.sunshine .user-info {
  background: rgba(255, 250, 230, 0.8);
  border: 1px solid rgba(218, 165, 32, 0.15);
}

.user-avatar {
  font-size: 24px;
}

.user-name {
  font-size: 14px;
  color: white;
}

.sunrise .user-name {
  color: #3a2515;
}

.sunshine .user-name {
  color: #5a4805;
}

.dialogue-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.main-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: rgba(15, 15, 35, 0.8);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  transition: all 0.5s ease;
}

.sunrise .main-header {
  background: rgba(253, 246, 227, 0.95);
  border-bottom-color: rgba(180, 130, 80, 0.2);
}

.sunshine .main-header {
  background: linear-gradient(180deg, rgba(255, 252, 235, 0.98), rgba(255, 250, 225, 0.98));
  border-bottom-color: rgba(218, 165, 32, 0.2);
}

.header-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: white;
}

.sunrise .header-title {
  color: #3a2515;
}

.sunshine .header-title {
  color: #5a4805;
}

.title-icon {
  font-size: 24px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sunrise .header-btn {
  background: rgba(200, 150, 100, 0.15);
  border-color: rgba(180, 130, 80, 0.3);
  color: rgba(80, 60, 40, 0.8);
}

.sunshine .header-btn {
  background: rgba(255, 250, 230, 0.8);
  border-color: rgba(218, 165, 32, 0.2);
  color: rgba(90, 70, 20, 0.8);
}

.header-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.sunrise .header-btn:hover {
  background: rgba(200, 150, 100, 0.25);
  color: #3a2515;
}

.sunshine .header-btn:hover {
  background: rgba(255, 248, 220, 0.95);
  color: #5a4805;
}

.theme-dropdown {
  margin-left: 10px;
}

.theme-switch {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sunrise .theme-switch {
  background: rgba(200, 150, 100, 0.15);
  border-color: rgba(180, 130, 80, 0.3);
}

.sunshine .theme-switch {
  background: rgba(255, 250, 230, 0.8);
  border-color: rgba(218, 165, 32, 0.2);
}

.theme-switch:hover {
  background: rgba(255, 255, 255, 0.1);
}

.sunrise .theme-switch:hover {
  background: rgba(200, 150, 100, 0.25);
}

.sunshine .theme-switch:hover {
  background: rgba(255, 248, 220, 0.95);
}

.theme-icon {
  font-size: 18px;
}

.theme-dropdown :deep(.el-dropdown-menu) {
  background: rgba(15, 15, 35, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
}

.sunrise .theme-dropdown :deep(.el-dropdown-menu) {
  background: rgba(253, 246, 227, 0.98);
  border-color: rgba(180, 130, 80, 0.2);
}

.sunshine .theme-dropdown :deep(.el-dropdown-menu) {
  background: rgba(255, 252, 235, 0.98);
  border-color: rgba(218, 165, 32, 0.2);
}

.theme-dropdown :deep(.el-dropdown-menu__item) {
  color: rgba(255, 255, 255, 0.8);
  padding: 10px 16px;
}

.sunrise .theme-dropdown :deep(.el-dropdown-menu__item) {
  color: rgba(80, 60, 40, 0.9);
}

.sunshine .theme-dropdown :deep(.el-dropdown-menu__item) {
  color: rgba(90, 70, 20, 0.9);
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
  background: rgba(255, 248, 220, 0.95);
  color: #5a4805;
}

.lang-dropdown {
  margin-left: 10px;
}

.lang-switch {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: all 0.3s ease;
}

.sunrise .lang-switch {
  background: rgba(200, 150, 100, 0.15);
  border-color: rgba(180, 130, 80, 0.3);
  color: rgba(80, 60, 40, 0.8);
}

.sunshine .lang-switch {
  background: rgba(255, 250, 230, 0.8);
  border-color: rgba(218, 165, 32, 0.2);
  color: rgba(90, 70, 20, 0.8);
}

.lang-switch:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.sunrise .lang-switch:hover {
  background: rgba(200, 150, 100, 0.25);
  color: #3a2515;
}

.sunshine .lang-switch:hover {
  background: rgba(255, 248, 220, 0.95);
  color: #5a4805;
}

.lang-dropdown :deep(.el-dropdown-menu) {
  background: rgba(15, 15, 35, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
}

.sunrise .lang-dropdown :deep(.el-dropdown-menu) {
  background: rgba(253, 246, 227, 0.98);
  border-color: rgba(180, 130, 80, 0.2);
}

.sunshine .lang-dropdown :deep(.el-dropdown-menu) {
  background: rgba(255, 252, 235, 0.98);
  border-color: rgba(218, 165, 32, 0.2);
}

.lang-dropdown :deep(.el-dropdown-menu__item) {
  color: rgba(255, 255, 255, 0.8);
  padding: 10px 16px;
}

.sunrise .lang-dropdown :deep(.el-dropdown-menu__item) {
  color: rgba(80, 60, 40, 0.9);
}

.sunshine .lang-dropdown :deep(.el-dropdown-menu__item) {
  color: rgba(90, 70, 20, 0.9);
}

.lang-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(123, 47, 247, 0.3);
  color: white;
}

.sunrise .lang-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(200, 150, 100, 0.3);
  color: #3a2515;
}

.sunshine .lang-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(255, 248, 220, 0.95);
  color: #5a4805;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.messages-wrapper {
  max-width: 900px;
  margin: 0 auto;
}

.message {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  animation: message-in 0.3s ease;
}

@keyframes message-in {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-avatar {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.message.user .message-avatar {
  background: linear-gradient(135deg, #00d4ff, #7b2ff7);
  border-radius: 12px;
  font-size: 20px;
}

.sunrise .message.user .message-avatar {
  background: linear-gradient(135deg, #c97b4a, #d4885a);
}

.sunshine .message.user .message-avatar {
  background: linear-gradient(135deg, #b8860b, #daa520);
}

.ai-avatar {
  background: linear-gradient(135deg, #7b2ff7, #f107a3);
  border-radius: 12px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sunrise .ai-avatar {
  background: linear-gradient(135deg, #c97b4a, #d4885a);
}

.sunshine .ai-avatar {
  background: linear-gradient(135deg, #b8860b, #daa520);
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.message-name {
  font-weight: 600;
  color: white;
  font-size: 14px;
}

.sunrise .message-name {
  color: #3a2515;
}

.sunshine .message-name {
  color: #5a4805;
}

.message-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.sunrise .message-time {
  color: rgba(100, 70, 50, 0.5);
}

.sunshine .message-time {
  color: rgba(160, 130, 50, 0.6);
}

.message-text {
  background: rgba(255, 255, 255, 0.05);
  padding: 16px 20px;
  border-radius: 16px;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.7;
  font-size: 15px;
}

.sunrise .message-text {
  background: rgba(200, 150, 100, 0.15);
  color: rgba(60, 45, 30, 0.9);
}

.sunshine .message-text {
  background: rgba(255, 250, 230, 0.8);
  color: rgba(60, 50, 20, 0.9);
  border: 1px solid rgba(218, 165, 32, 0.1);
}

.message.user .message-text {
  background: linear-gradient(135deg, rgba(123, 47, 247, 0.3), rgba(0, 212, 255, 0.2));
  border: 1px solid rgba(123, 47, 247, 0.3);
}

.sunrise .message.user .message-text {
  background: linear-gradient(135deg, rgba(200, 150, 100, 0.25), rgba(180, 130, 80, 0.2));
  border-color: rgba(180, 130, 80, 0.3);
}

.sunshine .message.user .message-text {
  background: linear-gradient(135deg, rgba(255, 248, 220, 0.95), rgba(255, 245, 210, 0.95));
  border-color: rgba(218, 165, 32, 0.25);
}

.message-text :deep(pre) {
  background: rgba(0, 0, 0, 0.3);
  padding: 12px 16px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 12px 0;
}

.sunrise .message-text :deep(pre) {
  background: rgba(180, 130, 80, 0.15);
}

.sunshine .message-text :deep(pre) {
  background: rgba(255, 245, 210, 0.8);
}

.message-text :deep(code) {
  background: rgba(123, 47, 247, 0.2);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Fira Code', monospace;
  font-size: 13px;
}

.sunrise .message-text :deep(code) {
  background: rgba(200, 150, 100, 0.2);
}

.sunshine .message-text :deep(code) {
  background: rgba(255, 245, 210, 0.9);
}

.message-text :deep(pre code) {
  background: none;
  padding: 0;
}

.message-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.msg-action {
  background: rgba(255, 255, 255, 0.05);
  border: none;
  color: rgba(255, 255, 255, 0.5);
  padding: 6px 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sunrise .msg-action {
  background: rgba(200, 150, 100, 0.15);
  color: rgba(80, 60, 40, 0.6);
}

.sunshine .msg-action {
  background: rgba(255, 250, 230, 0.8);
  color: rgba(90, 70, 20, 0.7);
}

.msg-action:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.sunrise .msg-action:hover {
  background: rgba(200, 150, 100, 0.25);
  color: #3a2515;
}

.sunshine .msg-action:hover {
  background: rgba(255, 248, 220, 0.95);
  color: #5a4805;
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
}

.typing-avatar {
  font-size: 24px;
  background: linear-gradient(135deg, #7b2ff7, #f107a3);
  border-radius: 12px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sunrise .typing-avatar {
  background: linear-gradient(135deg, #c97b4a, #d4885a);
}

.sunshine .typing-avatar {
  background: linear-gradient(135deg, #b8860b, #daa520);
}

.typing-dots {
  display: flex;
  gap: 6px;
}

.typing-dots span {
  width: 8px;
  height: 8px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  animation: typing-bounce 1.4s ease-in-out infinite;
}

.sunrise .typing-dots span {
  background: rgba(200, 150, 100, 0.6);
}

.sunshine .typing-dots span {
  background: rgba(200, 180, 50, 0.6);
}

.typing-dots span:nth-child(1) { animation-delay: 0s; }
.typing-dots span:nth-child(2) { animation-delay: 0.2s; }
.typing-dots span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing-bounce {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-8px); }
}

.input-area {
  padding: 16px 24px 24px;
  background: rgba(15, 15, 35, 0.8);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  transition: all 0.5s ease;
}

.sunrise .input-area {
  background: rgba(253, 246, 227, 0.95);
  border-top-color: rgba(180, 130, 80, 0.2);
}

.sunshine .input-area {
  background: linear-gradient(180deg, rgba(255, 252, 235, 0.98), rgba(255, 250, 225, 0.98));
  border-top-color: rgba(218, 165, 32, 0.2);
}

.input-container {
  max-width: 900px;
  margin: 0 auto;
}

.input-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 12px 16px;
  transition: all 0.3s ease;
}

.sunrise .input-wrapper {
  background: rgba(200, 150, 100, 0.1);
  border-color: rgba(180, 130, 80, 0.3);
}

.sunshine .input-wrapper {
  background: rgba(255, 250, 230, 0.8);
  border-color: rgba(218, 165, 32, 0.2);
}

.input-wrapper:focus-within {
  border-color: rgba(123, 47, 247, 0.5);
  box-shadow: 0 0 0 3px rgba(123, 47, 247, 0.1);
}

.sunrise .input-wrapper:focus-within {
  border-color: rgba(180, 130, 80, 0.5);
  box-shadow: 0 0 0 3px rgba(200, 150, 100, 0.1);
}

.sunshine .input-wrapper:focus-within {
  border-color: rgba(218, 165, 32, 0.4);
  box-shadow: 0 0 0 3px rgba(218, 165, 32, 0.1);
}

.input-wrapper textarea {
  flex: 1;
  background: none;
  border: none;
  color: white;
  font-size: 15px;
  line-height: 1.5;
  resize: none;
  outline: none;
  min-height: 24px;
  max-height: 200px;
}

.sunrise .input-wrapper textarea {
  color: #3a2515;
}

.sunshine .input-wrapper textarea {
  color: #5a4805;
}

.input-wrapper textarea::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.sunrise .input-wrapper textarea::placeholder {
  color: rgba(100, 70, 50, 0.5);
}

.sunshine .input-wrapper textarea::placeholder {
  color: rgba(160, 130, 50, 0.6);
}

.input-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.attach-btn {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.5);
  padding: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sunrise .attach-btn {
  color: rgba(100, 70, 50, 0.5);
}

.sunshine .attach-btn {
  color: rgba(160, 130, 50, 0.7);
}

.attach-btn:hover {
  color: white;
  background: rgba(255, 255, 255, 0.1);
}

.sunrise .attach-btn:hover {
  color: #3a2515;
  background: rgba(200, 150, 100, 0.2);
}

.sunshine .attach-btn:hover {
  color: #5a4805;
  background: rgba(255, 248, 220, 0.9);
}

.send-btn {
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: rgba(255, 255, 255, 0.3);
  padding: 10px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.send-btn.active {
  background: linear-gradient(135deg, #7b2ff7, #f107a3);
  color: white;
}

.sunrise .send-btn.active {
  background: linear-gradient(135deg, #c97b4a, #d4885a);
}

.sunshine .send-btn.active {
  background: linear-gradient(135deg, #daa520, #ffd700);
}

.send-btn.active:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 15px rgba(123, 47, 247, 0.4);
}

.sunrise .send-btn.active:hover {
  box-shadow: 0 4px 15px rgba(200, 150, 100, 0.4);
}

.sunshine .send-btn.active:hover {
  box-shadow: 0 4px 15px rgba(218, 165, 32, 0.35);
}

.send-btn:disabled {
  cursor: not-allowed;
}

.input-hint {
  text-align: center;
  margin-top: 12px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.3);
}

.sunrise .input-hint {
  color: rgba(100, 70, 50, 0.4);
}

.sunshine .input-hint {
  color: rgba(160, 130, 50, 0.5);
}

.messages-container::-webkit-scrollbar {
  width: 6px;
}

.messages-container::-webkit-scrollbar-track {
  background: transparent;
}

.messages-container::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

.sunrise .messages-container::-webkit-scrollbar-thumb {
  background: rgba(180, 130, 80, 0.2);
}

.sunshine .messages-container::-webkit-scrollbar-thumb {
  background: rgba(218, 165, 32, 0.2);
}

.messages-container::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.2);
}

.welcome-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 40px 20px;
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.welcome-avatar {
  font-size: 80px;
  margin-bottom: 30px;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.welcome-title {
  font-size: 32px;
  font-weight: 600;
  color: white;
  margin-bottom: 15px;
  text-align: center;
}

.sunrise .welcome-title {
  color: #3a2515;
}

.sunshine .welcome-title {
  color: #5a4805;
}

.default .welcome-title {
  color: #333;
}

.welcome-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 40px;
  text-align: center;
}

.sunrise .welcome-subtitle {
  color: rgba(100, 70, 50, 0.6);
}

.sunshine .welcome-subtitle {
  color: rgba(160, 130, 50, 0.7);
}

.default .welcome-subtitle {
  color: rgba(100, 100, 100, 0.6);
}

.welcome-suggestions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  max-width: 600px;
  width: 100%;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sunrise .suggestion-item {
  background: rgba(200, 150, 100, 0.1);
  border-color: rgba(180, 130, 80, 0.2);
}

.sunshine .suggestion-item {
  background: rgba(255, 250, 235, 0.6);
  border-color: rgba(218, 165, 32, 0.2);
}

.default .suggestion-item {
  background: rgba(0, 0, 0, 0.02);
  border-color: rgba(0, 0, 0, 0.08);
}

.suggestion-item:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(123, 47, 247, 0.5);
  transform: translateY(-2px);
}

.sunrise .suggestion-item:hover {
  background: rgba(200, 150, 100, 0.2);
  border-color: rgba(180, 130, 80, 0.5);
}

.sunshine .suggestion-item:hover {
  background: rgba(255, 248, 220, 0.9);
  border-color: rgba(218, 165, 32, 0.4);
}

.default .suggestion-item:hover {
  background: rgba(0, 0, 0, 0.05);
  border-color: rgba(0, 0, 0, 0.15);
}

.suggestion-icon {
  font-size: 24px;
}

.suggestion-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.sunrise .suggestion-text {
  color: rgba(80, 60, 40, 0.8);
}

.sunshine .suggestion-text {
  color: rgba(90, 70, 20, 0.8);
}

.default .suggestion-text {
  color: rgba(60, 60, 60, 0.8);
}

.ai-dialogue.default {
  background: linear-gradient(180deg, #ffffff 0%, #fafafa 50%, #f5f5f5 100%);
}

.default .dialogue-sidebar {
  background: rgba(255, 255, 255, 0.98);
  border-right-color: rgba(0, 0, 0, 0.08);
}

.default .sidebar-header {
  background: rgba(255, 255, 255, 0.95);
  border-bottom-color: rgba(0, 0, 0, 0.06);
}

.default .brand-name {
  background: linear-gradient(90deg, #333, #666);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.default .new-chat-btn {
  background: linear-gradient(135deg, #666, #888);
}

.default .new-chat-btn:hover {
  background: linear-gradient(135deg, #555, #777);
}

.default .history-header {
  color: #333;
}

.default .history-count {
  background: rgba(0, 0, 0, 0.08);
  color: #666;
}

.default .history-item {
  background: rgba(0, 0, 0, 0.02);
  border-color: transparent;
}

.default .history-item:hover {
  background: rgba(0, 0, 0, 0.05);
  border-color: rgba(0, 0, 0, 0.08);
}

.default .history-item.active {
  background: rgba(0, 0, 0, 0.06);
  border-color: rgba(0, 0, 0, 0.1);
}

.default .history-title {
  color: #333;
}

.default .history-time {
  color: #999;
}

.default .action-btn {
  color: #999;
}

.default .action-btn:hover {
  background: rgba(0, 0, 0, 0.08);
  color: #666;
}

.default .sidebar-footer {
  background: rgba(255, 255, 255, 0.95);
  border-top-color: rgba(0, 0, 0, 0.06);
}

.default .model-selector {
  background: rgba(0, 0, 0, 0.03);
  border-color: rgba(0, 0, 0, 0.08);
}

.default .model-label {
  color: #999;
}

.default .model-name {
  color: #333;
}

.default .user-info {
  background: rgba(0, 0, 0, 0.03);
  border-color: rgba(0, 0, 0, 0.08);
}

.default .user-name {
  color: #333;
}

.default .main-header {
  background: rgba(255, 255, 255, 0.98);
  border-bottom-color: rgba(0, 0, 0, 0.08);
}

.default .header-title {
  color: #333;
}

.default .header-btn {
  background: rgba(0, 0, 0, 0.05);
  border-color: rgba(0, 0, 0, 0.1);
  color: rgba(60, 60, 60, 0.8);
}

.default .header-btn:hover {
  background: rgba(0, 0, 0, 0.1);
  color: #333;
}

.default .theme-switch {
  background: rgba(0, 0, 0, 0.05);
  border-color: rgba(0, 0, 0, 0.1);
}

.default .theme-switch:hover {
  background: rgba(0, 0, 0, 0.1);
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

.default .lang-switch {
  background: rgba(0, 0, 0, 0.05);
  border-color: rgba(0, 0, 0, 0.1);
  color: rgba(60, 60, 60, 0.8);
}

.default .lang-switch:hover {
  background: rgba(0, 0, 0, 0.1);
  color: #333;
}

.default .lang-dropdown :deep(.el-dropdown-menu) {
  background: rgba(255, 255, 255, 0.98);
  border-color: rgba(0, 0, 0, 0.1);
}

.default .lang-dropdown :deep(.el-dropdown-menu__item) {
  color: rgba(60, 60, 60, 0.9);
}

.default .lang-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: rgba(0, 0, 0, 0.08);
  color: #333;
}

.default .message.user .message-avatar {
  background: linear-gradient(135deg, #888, #aaa);
}

.default .ai-avatar {
  background: linear-gradient(135deg, #888, #aaa);
}

.default .message-name {
  color: #333;
}

.default .message-time {
  color: rgba(100, 100, 100, 0.5);
}

.default .message-text {
  background: rgba(0, 0, 0, 0.04);
  color: rgba(50, 50, 50, 0.9);
}

.default .message.user .message-text {
  background: linear-gradient(135deg, rgba(100, 100, 100, 0.1), rgba(150, 150, 150, 0.08));
  border-color: rgba(0, 0, 0, 0.1);
}

.default .message-text :deep(pre) {
  background: rgba(0, 0, 0, 0.05);
}

.default .message-text :deep(code) {
  background: rgba(0, 0, 0, 0.08);
}

.default .msg-action {
  background: rgba(0, 0, 0, 0.05);
  color: rgba(80, 80, 80, 0.6);
}

.default .msg-action:hover {
  background: rgba(0, 0, 0, 0.1);
  color: #333;
}

.default .typing-avatar {
  background: linear-gradient(135deg, #888, #aaa);
}

.default .typing-dots span {
  background: rgba(150, 150, 150, 0.6);
}

.default .input-area {
  background: rgba(255, 255, 255, 0.98);
  border-top-color: rgba(0, 0, 0, 0.08);
}

.default .input-wrapper {
  background: rgba(0, 0, 0, 0.03);
  border-color: rgba(0, 0, 0, 0.1);
}

.default .input-wrapper:focus-within {
  border-color: rgba(100, 100, 100, 0.3);
  box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.05);
}

.default .input-wrapper textarea {
  color: #333;
}

.default .input-wrapper textarea::placeholder {
  color: rgba(150, 150, 150, 0.6);
}

.default .attach-btn {
  color: rgba(120, 120, 120, 0.6);
}

.default .attach-btn:hover {
  color: #333;
  background: rgba(0, 0, 0, 0.08);
}

.default .send-btn.active {
  background: linear-gradient(135deg, #666, #888);
}

.default .send-btn.active:hover {
  box-shadow: 0 4px 15px rgba(100, 100, 100, 0.3);
}

.default .input-hint {
  color: rgba(150, 150, 150, 0.5);
}

.default .messages-container::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.15);
}
</style>
