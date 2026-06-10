
import request from '@/utils/request'

export const HomeresoultService=()=>{
	return request.get('/user/home')
}

