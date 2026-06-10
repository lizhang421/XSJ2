<template>
  <div class="auth-page" :class="{ 'show-panel': isShowPanel }">
  
       <!-- 背景装饰元素 -->
       <div class="bg-decoration">
         <div class="bg-circle bg-circle-1"></div>
         <div class="bg-circle bg-circle-2"></div>
         <div class="bg-circle bg-circle-3"></div>
         <div class="bg-particle bg-particle-1"></div>
         <div class="bg-particle bg-particle-2"></div>
         <div class="bg-particle bg-particle-3"></div>
       </div>

       <!-- 右上角语言切换下拉按钮（修正图标使用方式） -->
       <div class="lang-dropdown-container">
         <div class="lang-dropdown" ref="langDropdownRef">
           <button class="lang-dropdown-btn" @click="toggleLangDropdown">
             <el-icon><Connection /></el-icon>
             <span>{{ currentLangText }}</span>
             <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
           </button>
           <div class="lang-dropdown-menu" v-show="isLangDropdownOpen">
             <button class="lang-dropdown-item" @click="changeLocale('zh')" :class="{ active: locale === 'zh' }">
               <span>中文</span>
             </button>
             <button class="lang-dropdown-item" @click="changeLocale('en')" :class="{ active: locale === 'en' }">
               <span>English</span>
             </button>
             <button class="lang-dropdown-item" @click="changeLocale('ja')" :class="{ active: locale === 'ja' }">
               <span>日本語</span>
             </button>
           </div>
         </div>
       </div>

    <!-- 左侧视频播放器区域 -->
    <div class="video-section">
      <div class="video-container">
        <video
          class="bg-video"
          autoplay
          loop
          muted
          playsinline
          poster="https://picsum.photos/1920/1080?random=10"
        >
          <source
            src="https://www.w3school.com.cn/i/movie.mp4"
            type="video/mp4"
          />
          {{ t('loginRegister.switchLanguage') }}
        </video>
        
        <!-- 视频装饰元素 -->
        <div class="video-decoration">
          <div class="video-line video-line-1"></div>
          <div class="video-line video-line-2"></div>
          <div class="video-line video-line-3"></div>
          <div class="video-dot video-dot-1"></div>
          <div class="video-dot video-dot-2"></div>
        </div>
      </div>
      
      <div class="video-mask">
        <div class="video-text">
          <h2>{{ t('brand.name') }}</h2>
          <p>{{ t('brand.slogan') }}</p>
        </div>
      </div>
    </div>

    <!-- 右侧登录注册面板（模糊玻璃效果） -->
    <div class="auth-panel">
      <!-- 切换标签 -->
      <div class="auth-tabs" :data-active="activeTab">
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'login' }"
          @click="switchTab('login')"
        >
          {{ t('loginRegister.login') }}
        </button>
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'register' }"
          @click="switchTab('register')"
        >
          {{ t('loginRegister.register') }}
        </button>
      </div>

      <!-- 登录表单 -->
      <div class="auth-form" v-if="activeTab === 'login'">
        <!-- 登录方式切换：手机号/邮箱 -->
        <div class="login-type">
          <button
            class="type-btn"
            :class="{ active: loginType === 'phone' }"
            @click="switchLoginType('phone')"
          >
            {{ t('loginRegister.phoneLogin') }}
          </button>
          <button
            class="type-btn"
            :class="{ active: loginType === 'email' }"
            @click="switchLoginType('email')"
          >
            {{ t('loginRegister.emailLogin') }}
          </button>
        </div>

        <!-- 手机号登录表单 -->
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          label-width="0"
          v-if="loginType === 'phone'"
        >
          <el-form-item prop="phone">
            <el-input
              v-model="loginForm.phone"
              :placeholder="t('loginRegister.inputPhone')"
              prefix-icon="el-icon-mobile-phone"
              class="auth-input"
            />
          </el-form-item>

          <el-form-item prop="captcha">
            <div class="captcha-group">
              <el-input
                v-model="loginForm.captcha"
                :placeholder="t('loginRegister.inputCaptcha')"
                prefix-icon="el-icon-shield"
                class="auth-input captcha-input"
              />
              <img
                :src="captchaImg"
                :alt="t('loginRegister.captchaRefresh')"
                class="captcha-img"
                @click="refreshCaptcha"
                :title="t('loginRegister.captchaRefresh')"
              />
            </div>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              :type="showLoginPassword ? 'text' : 'password'"
              :placeholder="t('loginRegister.inputLoginPassword')"
              prefix-icon="el-icon-lock"
              class="auth-input"
            >
              <template #suffix>
                <el-icon 
                  class="password-toggle" 
                  @click="showLoginPassword = !showLoginPassword"
                >
                  <component :is="showLoginPassword ? 'View' : 'Hide'" />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <div class="form-actions">
            <el-checkbox v-model="loginForm.rememberMe">
              {{ t('loginRegister.rememberMe') }}
            </el-checkbox>
          </div>

          <el-form-item>
            <el-button
              type="primary"
              class="submit-btn"
              @click="handlePhoneLogin"
              :loading="isLoading"
            >
              {{ isLoading ? t('loginRegister.loading') : t('loginRegister.loginBtn') }}
            </el-button>
          </el-form-item>

          <div class="forgot-password-link">
            <span @click="goToForgotPassword">{{ t('loginRegister.forgotPassword') }}</span>
          </div>
        </el-form>

        <!-- 邮箱登录表单 -->
        <el-form
          ref="loginEmailFormRef"
          :model="loginEmailForm"
          :rules="loginEmailRules"
          label-width="0"
          v-if="loginType === 'email'"
        >
          <el-form-item prop="email">
            <el-input
              v-model="loginEmailForm.email"
              :placeholder="t('loginRegister.inputEmail')"
              prefix-icon="el-icon-message"
              class="auth-input"
            />
          </el-form-item>

          <el-form-item prop="emailCode">
            <div class="code-group">
              <el-input
                v-model="loginEmailForm.emailCode"
                :placeholder="t('loginRegister.inputEmailCode')"
                prefix-icon="el-icon-mail"
                class="auth-input"
              />
              <el-button
                class="send-code-btn"
                :disabled="!canSendEmailCode || emailCodeCount > 0"
                @click="sendEmailCode"
              >
                {{ emailCodeCount > 0 ? t('loginRegister.resendCode', { count: emailCodeCount }) : t('loginRegister.sendCode') }}
              </el-button>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="submit-btn"
              @click="handleEmailLogin"
              :loading="isLoading"
            >
              {{ isLoading ? t('loginRegister.loading') : t('loginRegister.loginBtn') }}
            </el-button>
          </el-form-item>

          <div class="forgot-password-link">
            <span @click="goToForgotPassword">{{ t('loginRegister.forgotPassword') }}</span>
          </div>
        </el-form>
        
        <!-- 管理员登录表单 -->
        <el-form
          ref="adminLoginFormRef"
          :model="adminLoginForm"
          :rules="adminLoginRules"
          label-width="0"
          v-if="activeTab === 'admin'"
        >
          <el-form-item prop="phone">
            <el-input
              v-model="adminLoginForm.phone"
              :placeholder="t('loginRegister.inputPhone')"
              prefix-icon="el-icon-mobile-phone"
              class="auth-input"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="adminLoginForm.password"
              :type="showAdminPassword ? 'text' : 'password'"
              :placeholder="t('loginRegister.inputLoginPassword')"
              prefix-icon="el-icon-lock"
              class="auth-input"
            >
              <template #suffix>
                <el-icon 
                  class="password-toggle" 
                  @click="showAdminPassword = !showAdminPassword"
                >
                  <component :is="showAdminPassword ? 'View' : 'Hide'" />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              class="submit-btn"
              @click="handleAdminLogin"
              :loading="isLoading"
            >
              {{ isLoading ? t('loginRegister.loading') : t('loginRegister.loginBtn') }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 注册表单 -->
      <div class="auth-form" v-if="activeTab === 'register'">
        <!-- 注册方式切换：手机号/邮箱 -->
        <div class="login-type">
          <button
            class="type-btn"
            :class="{ active: registerType === 'phone' }"
            @click="switchRegisterType('phone')"
          >
            {{ t('loginRegister.phoneRegister') }}
          </button>
          <button
            class="type-btn"
            :class="{ active: registerType === 'email' }"
            @click="switchRegisterType('email')"
          >
            {{ t('loginRegister.emailRegister') }}
          </button>
        </div>

        <!-- 手机号注册表单 -->
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          label-width="0"
          v-if="registerType === 'phone'"
        >
          <el-form-item prop="phone">
            <el-input
              v-model="registerForm.phone"
         :placeholder="t('loginRegister.inputPhone')"
              prefix-icon="el-icon-mobile-phone"
              class="auth-input"
            />
          </el-form-item>

          <el-form-item prop="captchaCode">
            <div class="captcha-group">
              <el-input
                v-model="registerForm.captchaCode"
                :placeholder="t('loginRegister.inputCaptcha')"
                prefix-icon="el-icon-shield"
                class="auth-input captcha-input"
              />
              <img
                :src="captchaImg"
                :alt="t('loginRegister.captchaRefresh')"
                class="captcha-img"
                @click="refreshCaptcha"
                :title="t('loginRegister.captchaRefresh')"
              />
            </div>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              :type="showRegisterPassword ? 'text' : 'password'"
              :placeholder="t('loginRegister.inputRegisterPassword')"
              prefix-icon="el-icon-lock"
              class="auth-input"
            >
              <template #suffix>
                <el-icon 
                  class="password-toggle" 
                  @click="showRegisterPassword = !showRegisterPassword"
                >
                  <component :is="showRegisterPassword ? 'View' : 'Hide'" />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="rewpassword">
            <el-input
              v-model="registerForm.rewpassword"
              :type="showRegisterConfirmPassword ? 'text' : 'password'"
              :placeholder="t('loginRegister.inputConfirmPassword')"
              prefix-icon="el-icon-lock"
              class="auth-input"
            >
              <template #suffix>
                <el-icon 
                  class="password-toggle" 
                  @click="showRegisterConfirmPassword = !showRegisterConfirmPassword"
                >
                  <component :is="showRegisterConfirmPassword ? 'View' : 'Hide'" />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="submit-btn"
              @click="handlePhoneRegister"
              :loading="isLoading"
            >
              {{ isLoading ? t('loginRegister.loading') : t('loginRegister.registerBtn') }}
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 邮箱注册表单 -->
        <el-form
          ref="registerEmailFormRef"
          :model="registerEmailForm"
          :rules="registerEmailRules"
          label-width="0"
          v-if="registerType === 'email'"
        >
          <el-form-item prop="email">
            <el-input
              v-model="registerEmailForm.email"
              :placeholder="t('loginRegister.inputEmail')"
              prefix-icon="el-icon-mail"
              class="auth-input"
            />
          </el-form-item>

          <el-form-item prop="emailCode">
            <div class="code-group">
              <el-input
                v-model="registerEmailForm.emailCode"
                :placeholder="t('loginRegister.inputEmailCode')"
                prefix-icon="el-icon-message"
                class="auth-input"
              />
              <el-button
                class="send-code-btn"
                :disabled="!canSendRegEmailCode || regEmailCodeCount > 0"
                @click="sendRegEmailCode"
              >
                {{ regEmailCodeCount > 0 ? t('loginRegister.resendCode', { count: regEmailCodeCount }) : t('loginRegister.sendCode') }}
              </el-button>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="submit-btn"
              @click="handleEmailRegister"
              :loading="isLoading"
            >
              {{ isLoading ? t('loginRegister.loading') : t('loginRegister.registerBtn') }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 回到顶部按钮 -->
     <el-backtop target=".auth-page" :visibility-height="300" class="backtop">
        <el-icon size="20"><ArrowUp /></el-icon>
        <span class="backtop-text">{{ t('loginRegister.backToTop') }}</span>
      </el-backtop>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import router from '@/router'
import { ElMessage } from 'element-plus'
import {GetImageCode, PhoneRegister, PhoneLogin, SendEmailCode, EmailRegister, EmailLogin, GetUserDate} from '@/api/Users.js'
import { userTokenStore } from '@/stores/token';
import { useUserInfoStore } from '@/stores/userinfo';

const tokenStore=userTokenStore()
const userInfoStore = useUserInfoStore()





// 多语言hooks
const { t, locale } = useI18n()

// 语言下拉菜单控制
const isLangDropdownOpen = ref(false)
const langDropdownRef = ref(null)
const currentLangText = computed(() => {
  const langMap = {
    zh: '中文',
    en: 'English',
    ja: '日本語'
  }
  return langMap[locale.value] || '中文'
})

// 切换语言方法（登录页面不调用后端接口）
const changeLocale = (lang) => {
  locale.value = lang
  localStorage.setItem('locale', lang)
  isLangDropdownOpen.value = false
}

// 切换下拉菜单显示/隐藏
const toggleLangDropdown = () => {
  isLangDropdownOpen.value = !isLangDropdownOpen.value
}

// 点击页面其他区域关闭下拉菜单
const handleClickOutside = (e) => {
  if (langDropdownRef.value && !langDropdownRef.value.contains(e.target)) {
    isLangDropdownOpen.value = false
  }
}

// 联系信息多语言配置
const contactInfo = ref({
  zh: { /* 中文数据 */ },
  en: { /* 英文数据 */ },
  ja: { /* 日语数据 */ }
})
const currentContactInfo = ref(contactInfo.value.zh)

// 监听locale变化，更新动态数据
watch(locale, (newLang) => {
  currentContactInfo.value = contactInfo.value[newLang] || contactInfo.value.zh
}, { immediate: true })

// 滚动控制：页面滚动到顶部时显示登录注册面板
const isShowPanel = ref(false)
const activeTab = ref('login') // 登录/注册标签切换
const loginType = ref('phone') // 登录方式：phone/email
const registerType = ref('phone') // 注册方式：phone/email

// 密码显示/隐藏控制
const showLoginPassword = ref(false)
const showRegisterPassword = ref(false)
const showRegisterConfirmPassword = ref(false)
const showRegisterEmailPassword = ref(false)
const showRegisterEmailConfirmPassword = ref(false)

// 加载状态
const isLoading = ref(false)

// 图形验证码相关
const captchaImg = ref('') // 图形验证码图片地址

const refreshCaptcha = async () => {
  try {
    //从后端获取图形验证码
    let result = await GetImageCode()  
    
    //处理后端传递过来的数据
    if (result && result.type && result.type.startsWith('image/')) {
      // 如果是图片blob，显示图片
      const blob = result
      const imgUrl = URL.createObjectURL(blob)
      captchaImg.value = imgUrl
    } else {
      captchaImg.value =  'https://lizhanghao.oss-cn-beijing.aliyuncs.com/preview.jpg'
    }
  } catch (error) {
    console.error('获取验证码失败:', error)
    // 不显示错误消息，使用默认图片
    captchaImg.value =  'https://lizhanghao.oss-cn-beijing.aliyuncs.com/preview.jpg'
  }
}



// 倒计时相关
const emailCodeCount = ref(0) // 邮箱登录验证码倒计时
const regEmailCodeCount = ref(0) // 邮箱注册验证码倒计时
let emailCodeTimer = null
let regEmailCodeTimer = null

// 表单数据 - 手机号登录
const loginForm = ref({
  phone: '',
  captcha: '',
  password: '',
  rememberMe: false
})
// 表单数据 - 邮箱登录
const loginEmailForm = ref({
  email: '',
  emailCode: ''
})
// 表单数据 - 管理员登录
const adminLoginForm = ref({
  phone: '',
  password: ''
})
// 表单数据 - 手机号注册
const registerForm = ref({
  phone: '',
  captchaCode: '',
  password: '',
  rewpassword: ''
})
// 表单数据 - 邮箱注册
const registerEmailForm = ref({
  email: '',
  emailCode: ''
})

// 密码显示/隐藏控制
const showAdminPassword = ref(false)

// 表单校验规则初始化
let loginRules = ref({})
let loginEmailRules = ref({})
let registerRules = ref({})
let registerEmailRules = ref({})
let adminLoginRules = ref({})

// 初始化校验规则
const initValidationRules = () => {
  loginRules.value = {
    phone: [
      { required: true, message: t('loginRegister.pleaseInputPhone'), trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$/, message: t('loginRegister.phoneError'), trigger: 'blur' }
    ],
    captcha: [
      { required: true, message: t('loginRegister.pleaseInputCaptcha'), trigger: 'blur' },
      { min: 4, max: 4, message: t('loginRegister.captchaLength'), trigger: 'blur' }
    ],
    password: [
      { required: true, message: t('loginRegister.inputPassword'), trigger: 'blur' },
      { pattern: /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{6,16}$/, message: t('loginRegister.passwordRule'), trigger: 'blur' }
    ]
  }
  loginEmailRules.value = {
    email: [
      { required: true, message: t('loginRegister.pleaseInputEmail'), trigger: 'blur' },
      { type: 'email', message: t('loginRegister.emailError'), trigger: 'blur' }
    ],
    emailCode: [
      { required: true, message: t('loginRegister.inputEmailCode'), trigger: 'blur' },
      { min: 6, max: 6, message: t('loginRegister.codeLength'), trigger: 'blur' }
    ]
  }
  registerRules.value = {
    phone: [
      { required: true, message: t('loginRegister.pleaseInputPhone'), trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$/, message: t('loginRegister.phoneError'), trigger: 'blur' }
    ],
    captchaCode: [
      { required: true, message: t('loginRegister.pleaseInputCaptcha'), trigger: 'blur' },
      { min: 4, max: 4, message: t('loginRegister.captchaLength'), trigger: 'blur' }
    ],
    password: [
      { required: true, message: t('loginRegister.inputPassword'), trigger: 'blur' },
      { pattern: /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{6,16}$/, message: t('loginRegister.passwordRule'), trigger: 'blur' }
    ],
    rewpassword: [
      { required: true, message: t('loginRegister.inputConfirmPassword'), trigger: 'blur' },
      {
        validator: (rule, value, callback) => {
          if (value !== registerForm.value.password) {
            callback(new Error(t('loginRegister.passwordNotMatch')))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ]
  }
  registerEmailRules.value = {
    email: [
      { required: true, message: t('loginRegister.pleaseInputEmail'), trigger: 'blur' },
      { type: 'email', message: t('loginRegister.emailError'), trigger: 'blur' }
    ],
    emailCode: [
      { required: true, message: t('loginRegister.inputEmailCode'), trigger: 'blur' },
      { min: 6, max: 6, message: t('loginRegister.codeLength'), trigger: 'blur' }
    ]
  }
}

// 表单Ref
const loginFormRef = ref(null)
const loginEmailFormRef = ref(null)
const registerFormRef = ref(null)
const registerEmailFormRef = ref(null)

// 切换登录/注册标签
const switchTab = (tab) => {
  activeTab.value = tab
  // 切换时重置表单
  if (tab === 'login') {
    loginFormRef.value?.resetFields()
    loginEmailFormRef.value?.resetFields()
    // 手动清空注册表单数据
    registerForm.value = {
      phone: '',
      captchaCode: '',
      password: '',
      rewpassword: ''
    }
    registerEmailForm.value = {
      email: '',
      emailCode: ''
    }
  } else if (tab === 'register') {
    registerFormRef.value?.resetFields()
    registerEmailFormRef.value?.resetFields()
    // 手动清空登录表单数据
    loginForm.value = {
      phone: '',
      captcha: '',
      password: '',
      rememberMe: false
    }
    loginEmailForm.value = {
      email: '',
      emailCode: ''
    }
  }
}

// 返回登录
const backToLogin = () => {
  switchTab('login')
}

// 切换登录方式
const switchLoginType = (type) => {
  loginType.value = type
  // 切换时重置对应表单
  if (type === 'phone') {
    loginFormRef.value?.resetFields()
    refreshCaptcha() // 刷新图形验证码
  } else {
    loginEmailFormRef.value?.resetFields()
  }
}

// 切换注册方式
const switchRegisterType = (type) => {
  registerType.value = type
  // 切换时重置对应表单
  if (type === 'phone') {
    registerFormRef.value?.resetFields()
    refreshCaptcha() // 刷新图形验证码
  } else {
    registerEmailFormRef.value?.resetFields()
  }
}

// 倒计时函数
const startCountdown = (countRef, timerRef) => {
  countRef.value = 60
  timerRef = setInterval(() => {
    countRef.value--
    if (countRef.value <= 0) {
      clearInterval(timerRef)
      timerRef = null
    }
  }, 1000)
}

// 邮箱登录 - 发送邮箱验证码
const sendEmailCode = async () => {
  if (!loginEmailForm.value.email) {
    ElMessage.warning(t('loginRegister.pleaseInputEmail'))
    return
  }

  try {
    await SendEmailCode(loginEmailForm.value.email, 1)
    ElMessage.success(t('loginRegister.sendCodeSuccess'))
    startCountdown(emailCodeCount, emailCodeTimer)
  } catch (err) {
    console.error('发送邮箱验证码失败:', err)
    const errorMsg = typeof err === 'string' ? err : 
                    (err.response?.data?.message || err.message || '发送验证码失败')
    ElMessage.error(errorMsg)
  }
}

// 邮箱注册 - 发送邮箱验证码
const sendRegEmailCode = async () => {
  if (!registerEmailForm.value.email) {
    ElMessage.warning(t('loginRegister.pleaseInputEmail'))
    return
  }

  try {
    await SendEmailCode(registerEmailForm.value.email, 2)
    ElMessage.success(t('loginRegister.sendCodeSuccess'))
    startCountdown(regEmailCodeCount, regEmailCodeTimer)
  } catch (err) {
    console.error('发送邮箱验证码失败:', err)
    const errorMsg = typeof err === 'string' ? err : 
                    (err.response?.data?.message || err.message || '发送验证码失败')
    ElMessage.error(errorMsg)
  }
}


// 手机号登录逻辑
const handlePhoneLogin = async () => {
  try {
    isLoading.value = true
    await loginFormRef.value.validate()
    
    try {
      const result = await PhoneLogin(
        loginForm.value.phone,
        loginForm.value.password,
        loginForm.value.captcha
      )
      console.log('登录成功，返回数据:', result)
      ElMessage.success(t('loginRegister.loginSuccess'))
      //登录成功之后把token保存到store
      tokenStore.setToken(result.data)
      
      // 将当前选择的语言保存到数据库
      const langMapToBackend = {
        'zh': '中文',
        'en': '英文',
        'ja': '日文'
      }
      const currentLang = locale.value
      try {
        await userInfoStore.updateLanguage(langMapToBackend[currentLang])
        console.log('语言设置已保存到数据库:', langMapToBackend[currentLang])
      } catch (err) {
        console.error('保存语言设置失败:', err)
      }
      
      // 获取用户信息
      try {
        const userInfoResult = await GetUserDate()
        console.log('获取用户信息成功:', userInfoResult)
        if (userInfoResult.code === 0 && userInfoResult.data && userInfoResult.data.length > 0) {
          const userInfo = userInfoResult.data[0]
          
          // 检查用户封禁状态 - status字段
          // status=2: 紧急封禁，禁止登录
          // status=3: 存在异常状态，显示信息但允许登录
          if (userInfo.status === '2' || userInfo.status === 2) {
            tokenStore.removeToken()
            userInfoStore.removeInfo()
            const banReason = userInfo.reason || '未知原因'
            const endTime = userInfo.endTime ? new Date(userInfo.endTime).toLocaleString('zh-CN') : '未知'
            ElMessage.error(`账号已被紧急封禁，禁止登录！\n原因：${banReason}\n封禁至：${endTime}`)
            router.push('/Home')
            return
          }
          
          // status=3: 异常状态，显示警告但允许登录
          if (userInfo.status === '3' || userInfo.status === 3) {
            const banReason = userInfo.reason || '未知原因'
            const endTime = userInfo.endTime ? new Date(userInfo.endTime).toLocaleString('zh-CN') : '未知'
            ElMessage.warning(`账号存在异常状态！\n原因：${banReason}\n限制至：${endTime}\n请尽快联系管理员处理`)
          }
          
          userInfoStore.setInfo(userInfo)
          console.log('用户信息已存储到 Pinia store')
          router.push('/First')

        } else {
          console.error('获取用户信息失败:', userInfoResult)
          router.push('/First')
        }
      } catch (err) {
        console.error('获取用户信息失败:', err)
        router.push('/First')
      }

    } catch (err) {
      console.error('登录失败:', err)
      const errorMsg = typeof err === 'string' ? err : 
                      (err.response?.data?.message || err.message || '登录失败，请检查账号密码')
      ElMessage.error(errorMsg)
      refreshCaptcha() // 刷新验证码
    }
  } catch (err) {
    ElMessage.warning(t('loginRegister.pleaseCompleteForm'))
  } finally {
    isLoading.value = false
  }
}

// 邮箱登录逻辑
const handleEmailLogin = async () => {
  try {
    isLoading.value = true
    await loginEmailFormRef.value.validate()
    
    try {
      const result = await EmailLogin(
        loginEmailForm.value.email,
        loginEmailForm.value.emailCode,
        1 // type=1 表示邮箱登录
      )
      console.log('邮箱登录成功，返回数据:', result)
      ElMessage.success(t('loginRegister.loginSuccess'))
      // 登录成功之后把token保存到store
      tokenStore.setToken(result.data)
      
      // 将当前选择的语言保存到数据库
      const langMapToBackend = {
        'zh': '中文',
        'en': '英文',
        'ja': '日文'
      }
      const currentLang = locale.value
      try {
        await userInfoStore.updateLanguage(langMapToBackend[currentLang])
        console.log('语言设置已保存到数据库:', langMapToBackend[currentLang])
      } catch (err) {
        console.error('保存语言设置失败:', err)
      }
      
      // 获取用户信息
      try {
        const userInfoResult = await GetUserDate()
        console.log('获取用户信息成功:', userInfoResult)
        if (userInfoResult.code === 0 && userInfoResult.data && userInfoResult.data.length > 0) {
          const userInfo = userInfoResult.data[0]
          
          // 检查用户封禁状态 - status字段
          if (userInfo.status === '2' || userInfo.status === 2) {
            tokenStore.removeToken()
            userInfoStore.removeInfo()
            const banReason = userInfo.reason || '未知原因'
            const endTime = userInfo.endTime ? new Date(userInfo.endTime).toLocaleString('zh-CN') : '未知'
            ElMessage.error(`账号已被紧急封禁，禁止登录！\n原因：${banReason}\n封禁至：${endTime}`)
            router.push('/Home')
            return
          }
          
          if (userInfo.status === '3' || userInfo.status === 3) {
            const banReason = userInfo.reason || '未知原因'
            const endTime = userInfo.endTime ? new Date(userInfo.endTime).toLocaleString('zh-CN') : '未知'
            ElMessage.warning(`账号存在异常状态！\n原因：${banReason}\n限制至：${endTime}\n请尽快联系管理员处理`)
          }
          
          userInfoStore.setInfo(userInfo)
          console.log('用户信息已存储到 Pinia store')
          router.push('/First')
        } else {
          console.error('获取用户信息失败:', userInfoResult)
          router.push('/First')
        }
      } catch (err) {
        console.error('获取用户信息失败:', err)
        router.push('/First')
      }
      
    } catch (err) {
      console.error('邮箱登录失败:', err)
      ElMessage.error(err)
    }
  } catch (err) {
    ElMessage.warning(t('loginRegister.pleaseCompleteForm'))
  } finally {
    isLoading.value = false
  }
}

// 手机号注册逻辑
const handlePhoneRegister = async () => {
  try {
    isLoading.value = true
    await registerFormRef.value.validate()
    //打印日志
    console.log(registerForm.value)
    
    try {
      await PhoneRegister(
        registerForm.value.phone,
        registerForm.value.password,
        registerForm.value.rewpassword,
        registerForm.value.captchaCode
      )
      ElMessage.success(t('loginRegister.registerSuccess'))
      switchTab('login') // 注册成功切换到登录页
    } catch (err) {
      console.error('注册失败:', err)
      const errorMsg = typeof err === 'string' ? err : 
                      (err.response?.data?.message || err.message || t('loginRegister.registerFail'))
      ElMessage.error(errorMsg)
      refreshCaptcha() // 刷新验证码
    }
  } catch (err) {
    ElMessage.warning(t('loginRegister.pleaseCompleteForm'))
  } finally {
    isLoading.value = false
  }
}



// 邮箱注册逻辑
const handleEmailRegister = async () => {
  try {
    isLoading.value = true
    await registerEmailFormRef.value.validate()
    console.log('邮箱注册表单:', registerEmailForm.value)
    
    try {
      await EmailRegister(
        registerEmailForm.value.email,
        registerEmailForm.value.emailCode,
        2
      )
      ElMessage.success(t('loginRegister.registerSuccess'))
      switchTab('login') // 注册成功切换到登录页
    } catch (err) {
      console.error('邮箱注册失败:', err)
      const errorMsg = typeof err === 'string' ? err : 
                      (err.response?.data?.message || err.message || t('loginRegister.registerFail'))
      ElMessage.error(errorMsg)
    }
  } catch (err) {
    ElMessage.warning(t('loginRegister.pleaseCompleteForm'))
  } finally {
    isLoading.value = false
  }
}

// 滚动监听：页面滚动到顶部显示登录注册面板
const handleScroll = () => {
  const scrollTop = document.documentElement.scrollTop || document.body.scrollTop
  isShowPanel.value = scrollTop <= 100 // 滚动到顶部100px内显示面板
}

// 计算属性：是否可发送验证码
const canSendEmailCode = ref(() => !!loginEmailForm.value.email)
const canSendRegEmailCode = ref(() => !!registerEmailForm.value.email)

// 监听表单变化，更新可发送状态
watch([loginEmailForm, registerEmailForm], () => {
  canSendEmailCode.value = !!loginEmailForm.value.email
  canSendRegEmailCode.value = !!registerEmailForm.value.email
}, { deep: true })

// 监听语言变化，更新校验规则
watch(locale, () => {
  initValidationRules()
})

// 跳转到忘记密码页面
const goToForgotPassword = () => {
  router.push('/forgot-password')
}

// 初始化
onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  refreshCaptcha() // 初始化图形验证码
  handleScroll() // 初始检查滚动位置
  initValidationRules() // 初始化校验规则
  document.addEventListener('click', handleClickOutside) // 绑定点击外部关闭下拉菜单
})

// 销毁时清除定时器和监听
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  clearInterval(emailCodeTimer)
  clearInterval(regEmailCodeTimer)
  document.removeEventListener('click', handleClickOutside) // 移除点击外部事件
})
</script>

<style scoped>
/* 背景装饰元素 */
.bg-decoration {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(212, 167, 106, 0.1), rgba(139, 110, 60, 0.05));
  animation: float 6s ease-in-out infinite;
}

.bg-circle-1 {
  width: 300px;
  height: 300px;
  top: 10%;
  left: 15%;
  animation-delay: 0s;
}

.bg-circle-2 {
  width: 200px;
  height: 200px;
  top: 60%;
  left: 40%;
  animation-delay: 2s;
}

.bg-circle-3 {
  width: 150px;
  height: 150px;
  top: 30%;
  left: 70%;
  animation-delay: 4s;
}

.bg-particle {
  position: absolute;
  width: 8px;
  height: 8px;
  background: rgba(212, 167, 106, 0.3);
  border-radius: 50%;
  animation: particle 8s ease-in-out infinite;
}

.bg-particle-1 {
  top: 20%;
  left: 25%;
  animation-delay: 1s;
}

.bg-particle-2 {
  top: 50%;
  left: 60%;
  animation-delay: 3s;
}

.bg-particle-3 {
  top: 70%;
  left: 35%;
  animation-delay: 5s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

@keyframes particle {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.3;
  }
  25% {
    transform: translate(50px, -50px) scale(1.5);
    opacity: 0.6;
  }
  50% {
    transform: translate(100px, 0) scale(1);
    opacity: 0.3;
  }
  75% {
    transform: translate(50px, 50px) scale(1.5);
    opacity: 0.6;
  }
}

/* 全局样式 */
.auth-page {
  width: 100vw;
  min-height: 100vh;
  background-color: #F5F0E6; /* 清新米黄色 */
  display: flex;
  position: relative;
  overflow: hidden;
}

/* 语言下拉菜单样式（右上角悬浮） */
.lang-dropdown-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 999; /* 确保在最上层 */
}

.lang-dropdown {
  position: relative;
}

.lang-dropdown-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(212, 167, 106, 0.2);
  border-radius: 30px;
  color: #333;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.lang-dropdown-btn:hover {
  background: #D4A76A;
  color: white;
  border-color: #D4A76A;
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(212, 167, 106, 0.15);
}

.dropdown-arrow {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.lang-dropdown-btn:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.lang-dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(212, 167, 106, 0.15);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  padding: 8px 0;
  min-width: 120px;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

.lang-dropdown-item {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 10px 16px;
  background: transparent;
  border: none;
  color: #666;
  font-size: 14px;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
}

.lang-dropdown-item:hover {
  background: #F5F2EB;
  color: #D4A76A;
  padding-left: 20px;
}

.lang-dropdown-item.active {
  color: #D4A76A;
  font-weight: 600;
  background: #F5F2EB;
}

/* 淡入动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 加入我们按钮样式 */
.navbar-actions {
  position: fixed;
  top: 20px;
  right: 180px; /* 避开语言按钮 */
  z-index: 998;
}

.join-btn {
  background-color: rgba(212, 167, 106, 0.9);
  border: none;
  color: white;
  padding: 8px 20px;
  border-radius: 30px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.join-btn:hover {
  background-color: #C29458;
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(212, 167, 106, 0.15);
}

/* 滚动显示面板的过渡特效 */
.auth-page .auth-panel {
  transform: translateX(100%);
  opacity: 0;
  transition: all 0.8s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.auth-page.show-panel .auth-panel {
  transform: translateX(0);
  opacity: 1;
}

/* 左侧视频区域 */
.video-section {
  flex: 1;
  height: 100vh;
  position: relative;
  overflow: hidden;
}

.video-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.bg-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.9);
}

/* 视频装饰元素 */
.video-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.video-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, rgba(212, 167, 106, 0.3), transparent);
  height: 2px;
  animation: lineMove 4s ease-in-out infinite;
}

.video-line-1 {
  width: 200px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.video-line-2 {
  width: 150px;
  top: 50%;
  left: 30%;
  animation-delay: 1.5s;
}

.video-line-3 {
  width: 180px;
  top: 75%;
  left: 20%;
  animation-delay: 3s;
}

.video-dot {
  position: absolute;
  width: 12px;
  height: 12px;
  background: rgba(212, 167, 106, 0.4);
  border-radius: 50%;
  animation: dotPulse 3s ease-in-out infinite;
}

.video-dot-1 {
  top: 35%;
  left: 45%;
  animation-delay: 0.5s;
}

.video-dot-2 {
  top: 65%;
  left: 60%;
  animation-delay: 2s;
}

@keyframes lineMove {
  0%, 100% {
    transform: translateX(0);
    opacity: 0.3;
  }
  50% {
    transform: translateX(50px);
    opacity: 0.8;
  }
}

@keyframes dotPulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.4;
  }
  50% {
    transform: scale(1.5);
    opacity: 0.8;
  }
}

.video-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to right, rgba(245, 240, 230, 0.8), transparent);
  display: flex;
  align-items: center;
  padding-left: 80px;
}

.video-text {
  color: #333;
  max-width: 500px;
  animation: textFadeIn 1s ease-out;
}

.video-text h2 {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 20px;
  background: linear-gradient(90deg, #D4A76A, #8B6E3C);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 4px 12px rgba(212, 167, 106, 0.2);
  letter-spacing: 1px;
}

.video-text p {
  font-size: 18px;
  line-height: 1.6;
  color: #666;
  text-shadow: 0 2px 4px rgba(255, 255, 255, 0.5);
}

@keyframes textFadeIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 右侧登录注册面板（模糊玻璃效果增强） */
.auth-panel {
  position: fixed;
  top: 0;
  right: 0;
  width: 450px;
  height: 100vh;
  background: rgba(255, 255, 255, 0.85); /* 增强不透明度 */
  backdrop-filter: blur(20px); /* 增强模糊效果 */
  -webkit-backdrop-filter: blur(20px);
  border-left: 1px solid rgba(255, 255, 255, 0.7); /* 增强边框 */
  box-shadow: -8px 0 30px rgba(0, 0, 0, 0.08); /* 增强阴影 */
  padding: 60px 40px;
  z-index: 100;
  overflow-y: auto;
}

/* 标签切换 */
.auth-tabs {
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
  gap: 20px;
  position: relative;
}

.auth-tabs::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: calc(50% - 10px);
  height: 3px;
  background: linear-gradient(90deg, #D4A76A, #8B6E3C);
  border-radius: 3px;
  transition: transform 0.3s ease;
  box-shadow: 0 2px 8px rgba(212, 167, 106, 0.3);
}

.auth-tabs[data-active="login"]::after {
  transform: translateX(0);
}

.auth-tabs[data-active="register"]::after {
  transform: translateX(calc(100% + 20px));
}

.tab-btn {
  padding: 10px 30px;
  border: none;
  background: transparent;
  font-size: 18px;
  font-weight: 600;
  color: #666;
  cursor: pointer;
  position: relative;
  transition: all 0.3s ease;
}

.tab-btn.active {
  color: #D4A76A;
  text-shadow: 0 0 10px rgba(212, 167, 106, 0.2);
}

.tab-btn:hover {
  color: #D4A76A;
  transform: translateY(-2px);
}

/* 登录方式切换 */
.login-type {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  gap: 15px;
}

.type-btn {
  padding: 8px 20px;
  border: 1px solid #E0D8C8;
  background: transparent;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
}

.type-btn.active {
  background-color: #D4A76A;
  color: #fff;
  border-color: #D4A76A;
  box-shadow: 0 4px 12px rgba(212, 167, 106, 0.3);
  transform: translateY(-2px);
}

.type-btn:hover {
  border-color: #D4A76A;
  color: #D4A76A;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(212, 167, 106, 0.15);
}

/* 表单样式 */
.auth-form {
  width: 100%;
  animation: slideIn 0.5s ease-out;
}

/* 表单项样式 */
.el-form-item {
  margin-bottom: 20px;
  position: relative;
  padding-top: 20px; /* 为错误提示预留空间 */
}

/* 错误提示样式 - 显示在输入框上方 */
.el-form-item__error {
  position: absolute;
  bottom: 100%;
  left: 0;
  margin-bottom: 5px;
  color: #f56c6c;
  font-size: 12px;
  line-height: 1.4;
  padding: 4px 8px;
  background: rgba(245, 108, 108, 0.08);
  border-radius: 4px;
  z-index: 10;
  transform: translateY(-100%);
  white-space: nowrap;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.auth-input {
  height: 50px;
  border-radius: 8px;
  border: 1px solid #E0D8C8;
  background: rgba(255, 255, 255, 0.6);
  padding: 0 15px;
  margin-bottom: 20px;
  font-size: 14px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

/* 输入框聚焦特效增强 */
.auth-input:focus {
  border-color: #D4A76A;
  box-shadow: 0 0 0 4px rgba(212, 167, 106, 0.15), 0 4px 12px rgba(212, 167, 106, 0.1);
  outline: none;
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.9);
}

.auth-input:hover {
  border-color: #D4A76A;
  box-shadow: 0 2px 8px rgba(212, 167, 106, 0.08);
}

/* 密码切换按钮样式 */
.password-toggle {
  cursor: pointer;
  transition: all 0.3s ease;
  color: #999;
  
  &:hover {
    color: #D4A76A;
    transform: scale(1.1);
  }
}

/* 表单操作区域 */
.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  .el-checkbox {
    color: #666;
    font-size: 14px;
    
    &:hover {
      color: #D4A76A;
    }
  }
}

/* 验证码组合 */
.captcha-group {
  display: flex;
  gap: 10px;
}

.captcha-input {
  flex: 1;
}

.captcha-img {
  width: 120px;
  height: 50px;
  border-radius: 8px;
  cursor: pointer;
  object-fit: cover;
  transition: all 0.3s ease;
  border: 2px solid rgba(212, 167, 106, 0.2);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.captcha-img:hover {
  transform: scale(1.05) rotate(2deg);
  box-shadow: 0 4px 12px rgba(212, 167, 106, 0.3);
  border-color: rgba(212, 167, 106, 0.4);
}

.captcha-img:active {
  transform: scale(0.95);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
}

.code-group {
  display: flex;
  gap: 10px;
}

.send-code-btn {
  width: 120px;
  height: 50px;
  border-radius: 8px;
  background-color: #F5F0E6;
  color: #666;
  border: 1px solid #E0D8C8;
  transition: all 0.3s ease;
}

.send-code-btn:not(:disabled):hover {
  background-color: #D4A76A;
  color: #fff;
  border-color: #D4A76A;
}

/* 提交按钮（增强视觉层级） */
.submit-btn {
  width: 100%;
  height: 50px;
  border-radius: 8px;
  background: linear-gradient(135deg, #D4A76A 0%, #C29458 100%);
  color: #fff;
  border: none;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  margin-top: 10px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(212, 167, 106, 0.3);
}

.submit-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
  transition: 0.5s;
}

.submit-btn:hover::before {
  left: 100%;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(212, 167, 106, 0.4);
  background: linear-gradient(135deg, #C29458 0%, #A87E4A 100%);
}

.submit-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 10px rgba(212, 167, 106, 0.2);
}

/* 回到顶部按钮（优化样式） */
.backtop {
  position: fixed;
  bottom: 40px;
  right: 500px;
  z-index: 99;
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  border: 1px solid #E0D8C8;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #D4A76A;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 5px;
}

.backtop:hover {
  background: #D4A76A;
  color: #fff;
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(212, 167, 106, 0.3);
}

.backtop-text {
  font-size: 10px;
  margin-top: 2px;
}

/* 忘记密码链接样式 */
.forgot-password-link {
  text-align: center;
  margin-top: 15px;
}

.forgot-password-link span {
  color: #D4A76A;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.forgot-password-link span:hover {
  color: #8B6E3C;
  text-decoration: underline;
}

/* 响应式适配 */
@media (max-width: 992px) {
  .auth-page {
    flex-direction: column;
  }

  .video-section {
    height: 50vh;
  }

  .video-mask {
    padding-left: 40px;
  }

  .video-text h2 {
    font-size: 32px;
  }

  .auth-panel {
    position: relative;
    width: 100%;
    height: auto;
    border-left: none;
    border-top: 1px solid rgba(255, 255, 255, 0.7);
    box-shadow: 0 -8px 30px rgba(0, 0, 0, 0.08);
  }

  .backtop {
    right: 20px;
    bottom: 20px;
  }

  .lang-dropdown-container {
    right: 15px;
    top: 15px;
  }

  .navbar-actions {
    right: 120px;
    top: 15px;
  }

  .join-btn {
    padding: 6px 12px;
    font-size: 12px;
  }
}

@media (max-width: 576px) {
  .auth-panel {
    padding: 40px 20px;
  }

  .video-mask {
    padding-left: 20px;
  }

  .video-text h2 {
    font-size: 24px;
  }

  .video-text p {
    font-size: 14px;
  }

  .auth-input {
    height: 45px;
  }

  .send-code-btn {
    width: 100px;
    height: 45px;
    font-size: 12px;
  }

  .captcha-img {
    height: 45px;
  }

  .navbar-actions {
    display: none; /* 移动端隐藏加入我们按钮 */
  }
}
</style>