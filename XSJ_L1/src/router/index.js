import {createRouter,createWebHistory} from 'vue-router'
import loginVue from '@/views/login/login.vue';
import ForgotPasswordVue from '@/views/login/ForgotPassword.vue';
import HomeVue from '@/views/Home.vue'
import FirstVue from '@/views/User/First.vue';
import VideoVue from '@/views/User/Video.vue';
import SqlVue from '@/views/User/sql.vue';
import UploadVue from '@/views/User/upload.vue';
import AiHomeVue from '@/views/AI/AiHome.vue';
import AiDialogueVue from '@/views/AI/AiDialogue.vue';
import CenterVue from '@/views/Personal/Center.vue';
import HistoryVue from '@/views/Personal/History.vue';
import SearchVue from '@/views/User/Search.vue';
import UserProfileVue from '@/views/User/UserProfile.vue';
import ChatVue from '@/views/User/Chat.vue';

const routes = [
	{ path: '/', component: HomeVue },
	{ path: '/home', redirect: '/' },
	{ path: '/Home', redirect: '/' },
	{ path: '/login', component: loginVue },
	{ path: '/forgot-password', component: ForgotPasswordVue },
	{ path: '/First', component: FirstVue },
	{ path: '/Video', component: VideoVue },
	{ path: '/sql', component: SqlVue },
	{ path: '/upload', component: UploadVue },
	{ path: '/ai/home', component: AiHomeVue },
	{ path: '/ai/dialogue', component: AiDialogueVue },
	{ path: '/center', component: CenterVue },
	{ path: '/history', component: HistoryVue },
	{ path: '/search', component: SearchVue },
	{ path: '/user/:encode', component: UserProfileVue, name: 'UserProfile' },
	{ path: '/chat', component: ChatVue, name: 'Chat' }
]

const router = createRouter({
	history: createWebHistory(),
	routes: routes
})

export default router
