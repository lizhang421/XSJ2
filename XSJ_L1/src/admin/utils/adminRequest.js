import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/admin/router'

const baseURL = '/api'
const adminInstance = axios.create({ baseURL })

adminInstance.interceptors.request.use(
  (config) => {
    console.log('========== 管理员请求拦截器 ==========')
    console.log('请求URL:', config.url)
    console.log('请求方法:', config.method)
    console.log('请求数据:', config.data)
    console.log('请求头:', config.headers)
    
    const adminToken = localStorage.getItem('adminToken')
    console.log('从localStorage获取的adminToken:', adminToken)
    
    if (adminToken) {
      config.headers.Authorization = adminToken
      console.log('已添加Authorization头:', config.headers.Authorization)
    } else {
      console.warn('警告: 没有找到adminToken!')
    }
    
    console.log('最终请求配置:', config)
    console.log('======================================')
    
    return config
  },
  (err) => {
    console.error('请求拦截器错误:', err)
    return Promise.reject(err)
  }
)

adminInstance.interceptors.response.use(
  async (result) => {
    console.log('管理员响应拦截器收到响应:', result)
    
    if (result.data instanceof Blob) {
      if (result.data.type.startsWith('image/')) {
        return result.data
      }
      if (result.data.type === 'application/json') {
        try {
          const text = await result.data.text()
          const json = JSON.parse(text)
          if (json.code === 1) {
            const errorMsg = json.data || json.message || '操作失败'
            ElMessage.error(errorMsg)
            return Promise.reject(new Error(errorMsg))
          }
          if (json.code === 0) {
            return json
          }
          const errorMsg = json.message || '操作失败'
          ElMessage.error(errorMsg)
          return Promise.reject(new Error(errorMsg))
        } catch (e) {
          ElMessage.error('服务器响应解析失败')
          return Promise.reject(new Error('服务器响应解析失败'))
        }
      }
      return result.data
    }
    
    if (!result.data || typeof result.data !== 'object') {
      return result
    }
    
    if (result.data.code === 1) {
      const errorMsg = result.data.data || result.data.message || '操作失败'
      ElMessage.error(errorMsg)
      return Promise.reject(new Error(errorMsg))
    }
    
    if (result.data.code === 0) {
      return result.data
    }
    
    const errorMsg = result.data.message || '操作失败'
    ElMessage.error(errorMsg)
    return Promise.reject(new Error(errorMsg))
  },
  (err) => {
    console.error('响应拦截器错误:', err)
    
    if (err.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    } else if (err.response?.status === 401) {
      ElMessage.error('请先登录')
      localStorage.removeItem('adminToken')
      localStorage.removeItem('adminInfo')
      router.push('/sys-x7k9m2/login')
    } else if (err.response?.status === 403) {
      ElMessage.error('没有权限访问')
    } else if (err.response?.status === 404) {
      ElMessage.error('请求的资源不存在')
    } else if (err.response?.status >= 500) {
      ElMessage.error('服务器错误，请稍后重试')
    } else {
      ElMessage.error('服务异常')
    }
    return Promise.reject(err)
  }
)

export default adminInstance
