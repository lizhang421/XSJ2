import request from '@/utils/request'

export const getAiThemeService = () => {
  return request.get('/ai/hone')
}

export const updateAiThemeService = (hone) => {
  const formData = new FormData()
  formData.append('hone', hone)
  return request.post('/ai/hone2', formData)
}

export const getAiModelsService = () => {
  return request.get('/ai/ai-model')
}

export const getConversationListService = () => {
  return request.get('/ai/chat/conversation')
}

export const getChatHistoryService = (sessionId) => {
  return request.get('/ai/chat/history', { params: { sessionId } })
}
