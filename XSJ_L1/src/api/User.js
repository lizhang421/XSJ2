import request from '@/utils/request'

//向后台发注册送请求接口
export const userRegisterService=(regesterData)=>{
	const {phone,password,confirmPassword}=regesterData
	const param=new URLSearchParams();
	param.append('phone',phone);
	param.append('password',password);
	param.append('confirmPassword',confirmPassword);
	return request.post('/Users/register',param)
}

//登录的请求，登录需要发送两个信息，接收身份信息通过信息进行不同页面的跳转
export const userloginService=(logindata)=>{
	const {phone,password}=logindata
	const param=new URLSearchParams();
	 param.append('phone',phone);
	 param.append('password',password);
	return request.post('/Users/login',param)
}

//管理员登录接口
export const adminLoginService=(logindata)=>{
	const {phone,password}=logindata
	const param=new URLSearchParams();
	 param.append('phone',phone);
	 param.append('password',password);
	return request.post('/admin/login',param)
}

//获得用户身份
export const getuserstatus=()=>{
	 return request.get('/Users/role')
}
