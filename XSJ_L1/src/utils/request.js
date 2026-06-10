//定制请求的实例
//导入axios  npm install axios
import axios from 'axios';
//定义一个变量,记录公共的前缀  ,  baseURL
const baseURL = '/api';
const instance = axios.create({baseURL})
import { ElMessage } from 'element-plus';
import { userTokenStore } from '@/stores/token';

//添加请求拦截器，在发送请求的时候自动把token放到请求头里面
instance.interceptors.request.use(
(config)=>{
	//请求前回调
	console.log('请求拦截器 - 配置:', config)
	const tokenStore=userTokenStore();
	console.log('请求拦截器 - tokenStore:', tokenStore)
	console.log('请求拦截器 - token:', tokenStore.token)
	//判断有没有touken 没有滚出去
	if(tokenStore.token){
		config.headers.Authorization=tokenStore.token
		console.log('请求拦截器 - 添加Authorization头:', config.headers.Authorization)
	}
	console.log('请求拦截器 - 最终配置:', config)
	return config
},
(err)=>{
	console.log('请求拦截器 - 错误:', err)
	Promise.reject(err)
})



//添加响应拦截器
instance.interceptors.response.use(
	async result=>{
		console.log('响应拦截器收到响应:', result)
		console.log('响应类型:', result instanceof Blob ? 'Blob' : typeof result)
		console.log('result.code:', result.code)
		console.log('result.data:', result.data)
		console.log('result.data instanceof Blob:', result.data instanceof Blob)
		
		// 如果result.data是blob类型的响应
		if (result.data instanceof Blob) {
			console.log('返回blob对象，type:', result.data.type)
			// 如果是图片blob（image/png），直接返回
			if (result.data.type.startsWith('image/')) {
				return result.data
			}
			// 如果是json blob（application/json），需要解析并处理错误信息
			if (result.data.type === 'application/json') {
				try {
					const text = await result.data.text()
					const json = JSON.parse(text)
					console.log('解析JSON blob:', json)
					
					// 判断是否是错误响应（code===1）
					if (json.code === 1) {
						const errorMessage = json.data || json.message || '操作失败'
						return Promise.reject(errorMessage)
					}
					// 判断是否是成功响应（code===0）
					if (json.code === 0) {
						return json
					}
					// 其他情况也当作错误处理
					return Promise.reject(json.message || '操作失败')
				} catch (e) {
					console.error('解析JSON blob失败:', e)
					return Promise.reject('服务器响应解析失败')
				}
			}
			// 其他类型的blob，直接返回
			return result.data
		}
		
		// 如果result.data不是blob类型的响应（普通对象响应）
		if (!result.data || typeof result.data !== 'object') {
			console.log('result.data不是对象，直接返回result')
			return result
		}
		
		// 判断是否是错误响应（code===1）
		if (result.data.code === 1) {
			console.log('检测到错误响应，错误信息:', result.data.data || result.data.message)
			// 返回错误信息，不显示弹窗，让页面自己处理
			const errorMessage = result.data.data || result.data.message || '操作失败'
			return Promise.reject(errorMessage)
		}
		
		// 判断是否是成功响应（code===0）
		if(result.data.code === 0){
			//console.log('检测到成功响应')
			return result.data;	
		}
		
		// 处理其他错误情况
		console.log('到达最后的错误处理')
		console.log('result:', result)
		console.log('result.data:', result.data)
		console.log('result.code:', result.code)
		
		// 尝试从多个位置获取错误信息
		let errorMessage = '操作失败'
		if (result.data) {
			errorMessage = result.data.message || result.data.data || result.data.msg || errorMessage
		} else if (result.message) {
			errorMessage = result.message
		} else if (result.code === 1) {
			errorMessage = result.data || result.message || errorMessage
		}
		
		// 不显示弹窗，让页面自己处理
		return Promise.reject(errorMessage)
	},
    //在这里进行未登录统一处理，如果返回状态码是401，就是未登录让他滚出去
	err=>{
		if(err.response.status===401){
			// 首页不需要登录，不跳转到登录页面
			const publicPaths = ['/', '/home', '/Home']
			if (publicPaths.includes(window.location.pathname)) {
				return Promise.reject(err)
			}
			
			ElMessage.error('请先登录')
			// 使用 window.location.href 直接跳转，避免触发路由守卫导致无限循环
			if (window.location.pathname !== '/login' && window.location.pathname !== '/forgot-password') {
				window.location.href = '/login'
			}
		}else{
			ElMessage.error('服务异常')
		}
		return Promise.reject(err)
	}
)

export default instance;
