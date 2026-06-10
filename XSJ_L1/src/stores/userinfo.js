import { defineStore } from "pinia"
import {ref} from 'vue'
import { UpdateLanguage } from '@/api/Users'

export const useUserInfoStore = defineStore('userInfo',()=>{
    const info = ref({})
    
    const setInfo = (newInfo)=>{
        info.value = newInfo
    }
    
    const removeInfo = ()=>{
        info.value={}
    }
    
    const updateLanguage = async (language) => {
        try {
            const result = await UpdateLanguage(language)
            if (result.code === 0) {
                if (info.value) {
                    info.value.language = language
                }
                return true
            }
            return false
        } catch (err) {
            console.error('更新语言设置失败:', err)
            return false
        }
    }

    return{info, setInfo, removeInfo, updateLanguage}
},{
    persist:true
})


//第一个接收一个参数
//第二个参数接收方法
export const  userUSersINfoStore =defineStore('usersInfo',()=>{
	//定义用户信息
   const info=ref({})	
   //定义修改用户信息的方法
   const setinfo=(newInfo)=>{
	   info.value=newInfo
   }
   const removeinfo=()=>{
	   info.value={}
   }
   return {info,setinfo,removeinfo}
},
{
	persist:true
}
)