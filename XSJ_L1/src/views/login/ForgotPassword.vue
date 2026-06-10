<template>
  <div class="forgot-page">
    <div class="bg-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
    </div>

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

    <div class="forgot-container">
      <div class="forgot-card">
        <div class="card-header">
          <div class="logo">
            <el-icon :size="48" color="#D4A76A"><Key /></el-icon>
          </div>
          <h1 class="title">{{ t('loginRegister.forgotPassword') }}</h1>
          <p class="subtitle">{{ t('loginRegister.forgotPasswordDesc') }}</p>
        </div>

        <div class="steps-indicator">
          <div class="step" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
            <div class="step-circle">
              <el-icon v-if="currentStep > 1"><Check /></el-icon>
              <span v-else>1</span>
            </div>
            <span class="step-label">{{ t('loginRegister.verifyEmail') }}</span>
          </div>
          <div class="step-line" :class="{ active: currentStep > 1 }"></div>
          <div class="step" :class="{ active: currentStep >= 2, completed: currentStep > 2 }">
            <div class="step-circle">
              <el-icon v-if="currentStep > 2"><Check /></el-icon>
              <span v-else>2</span>
            </div>
            <span class="step-label">{{ t('loginRegister.resetPassword') }}</span>
          </div>
          <div class="step-line" :class="{ active: currentStep > 2 }"></div>
          <div class="step" :class="{ active: currentStep >= 3 }">
            <div class="step-circle">
              <el-icon v-if="currentStep >= 3"><Check /></el-icon>
              <span v-else>3</span>
            </div>
            <span class="step-label">{{ t('loginRegister.complete') }}</span>
          </div>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="forgot-form"
          v-if="currentStep < 3"
        >
          <el-form-item prop="email">
            <el-input
              v-model="form.email"
              :placeholder="t('loginRegister.inputEmail')"
              size="large"
              :prefix-icon="Message"
            />
          </el-form-item>

          <el-form-item prop="emailCode">
            <div class="code-input-group">
              <el-input
                v-model="form.emailCode"
                :placeholder="t('loginRegister.inputEmailCode')"
                size="large"
                :prefix-icon="Key"
              />
              <el-button
                type="primary"
                size="large"
                :disabled="!form.email || countdown > 0"
                :loading="sendingCode"
                @click="handleSendCode"
              >
                {{ countdown > 0 ? `${countdown}s` : t('loginRegister.sendCode') }}
              </el-button>
            </div>
          </el-form-item>

          <el-form-item prop="newPassword">
            <el-input
              v-model="form.newPassword"
              type="password"
              :placeholder="t('loginRegister.inputNewPassword')"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
            <div class="password-strength" v-if="form.newPassword">
              <div class="strength-bar">
                <div 
                  class="strength-fill" 
                  :class="strengthLevel"
                  :style="{ width: strengthPercent + '%' }"
                ></div>
              </div>
              <span class="strength-text" :class="strengthLevel">{{ strengthText }}</span>
            </div>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              :placeholder="t('loginRegister.inputConfirmNewPassword')"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="loading"
              @click="handleSubmit"
            >
              {{ t('loginRegister.confirmReset') }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="success-content" v-else>
          <div class="success-icon">
            <el-icon :size="64" color="#67c23a"><CircleCheckFilled /></el-icon>
          </div>
          <h2>{{ t('loginRegister.resetPasswordSuccess') }}</h2>
          <p>{{ t('loginRegister.redirectToLogin') }}</p>
          <el-button type="primary" size="large" @click="goToLogin">
            {{ t('loginRegister.backToLogin') }}
          </el-button>
        </div>

        <div class="card-footer">
          <el-button text @click="goToLogin">
            <el-icon><ArrowLeft /></el-icon>
            {{ t('loginRegister.backToLogin') }}
          </el-button>
        </div>
      </div>

      <div class="tips-card">
        <div class="tips-header">
          <el-icon><InfoFilled /></el-icon>
          <span>{{ t('loginRegister.tips') }}</span>
        </div>
        <ul class="tips-list">
          <li>{{ t('loginRegister.forgotPasswordTip1') }}</li>
          <li>{{ t('loginRegister.forgotPasswordTip2') }}</li>
          <li>{{ t('loginRegister.forgotPasswordTip3') }}</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Message, Key, Lock, ArrowLeft, Connection, ArrowDown, Check, CircleCheckFilled, InfoFilled } from '@element-plus/icons-vue'
import { SendEmailCode, UpdatePasswordByEmail } from '@/api/Users.js'

const { t, locale } = useI18n()
const router = useRouter()

const langDropdownRef = ref(null)
const isLangDropdownOpen = ref(false)
const currentLangText = computed(() => {
  const langMap = { zh: '中文', en: 'English', ja: '日本語' }
  return langMap[locale.value] || '中文'
})

const toggleLangDropdown = () => {
  isLangDropdownOpen.value = !isLangDropdownOpen.value
}

const changeLocale = (lang) => {
  locale.value = lang
  localStorage.setItem('locale', lang)
  isLangDropdownOpen.value = false
  initRules()
}

const handleClickOutside = (e) => {
  if (langDropdownRef.value && !langDropdownRef.value.contains(e.target)) {
    isLangDropdownOpen.value = false
  }
}

const currentStep = ref(1)
const loading = ref(false)
const sendingCode = ref(false)
const countdown = ref(0)
let timer = null

const formRef = ref(null)
const form = ref({
  email: '',
  emailCode: '',
  newPassword: '',
  confirmPassword: ''
})

let rules = ref({})

const initRules = () => {
  rules.value = {
    email: [
      { required: true, message: t('loginRegister.pleaseInputEmail'), trigger: 'blur' },
      { type: 'email', message: t('loginRegister.emailError'), trigger: 'blur' }
    ],
    emailCode: [
      { required: true, message: t('loginRegister.inputEmailCode'), trigger: 'blur' },
      { min: 6, max: 6, message: t('loginRegister.codeLength'), trigger: 'blur' }
    ],
    newPassword: [
      { required: true, message: t('loginRegister.inputNewPassword'), trigger: 'blur' },
      { pattern: /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{6,16}$/, message: t('loginRegister.passwordRule'), trigger: 'blur' }
    ],
    confirmPassword: [
      { required: true, message: t('loginRegister.inputConfirmNewPassword'), trigger: 'blur' },
      {
        validator: (rule, value, callback) => {
          if (value !== form.value.newPassword) {
            callback(new Error(t('loginRegister.passwordNotMatch')))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ]
  }
}

const strengthPercent = computed(() => {
  const pwd = form.value.newPassword
  if (!pwd) return 0
  let strength = 0
  if (pwd.length >= 6) strength += 25
  if (pwd.length >= 10) strength += 15
  if (/[a-z]/.test(pwd)) strength += 15
  if (/[A-Z]/.test(pwd)) strength += 15
  if (/[0-9]/.test(pwd)) strength += 15
  if (/[^a-zA-Z0-9]/.test(pwd)) strength += 15
  return Math.min(strength, 100)
})

const strengthLevel = computed(() => {
  const percent = strengthPercent.value
  if (percent < 40) return 'weak'
  if (percent < 70) return 'medium'
  return 'strong'
})

const strengthText = computed(() => {
  const level = strengthLevel.value
  if (level === 'weak') return t('loginRegister.passwordWeak')
  if (level === 'medium') return t('loginRegister.passwordMedium')
  return t('loginRegister.passwordStrong')
})

const handleSendCode = async () => {
  if (!form.value.email) {
    ElMessage.warning(t('loginRegister.pleaseInputEmail'))
    return
  }

  sendingCode.value = true
  try {
    await SendEmailCode(form.value.email, 3)
    ElMessage.success(t('loginRegister.sendCodeSuccess'))
    currentStep.value = 2
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
        timer = null
      }
    }, 1000)
  } catch (err) {
    const errorMsg = typeof err === 'string' ? err : (err.response?.data?.message || err.message || '发送失败')
    ElMessage.error(errorMsg)
  } finally {
    sendingCode.value = false
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    await UpdatePasswordByEmail(
      form.value.email,
      form.value.emailCode,
      3,
      form.value.newPassword
    )

    currentStep.value = 3
    ElMessage.success(t('loginRegister.resetPasswordSuccess'))
  } catch (err) {
    if (err !== false) {
      const errorMsg = typeof err === 'string' ? err : (err.response?.data?.message || err.message || t('loginRegister.resetPasswordFail'))
      ElMessage.error(errorMsg)
    }
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  initRules()
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.forgot-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #F5F0E6 0%, #E8E0D0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(212, 167, 106, 0.15), rgba(139, 110, 60, 0.08));
  animation: float 8s ease-in-out infinite;
}

.bg-circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  left: -100px;
}

.bg-circle-2 {
  width: 300px;
  height: 300px;
  bottom: -50px;
  right: -50px;
  animation-delay: 2s;
}

.bg-circle-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 50%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-30px) rotate(180deg); }
}

.lang-dropdown-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 100;
}

.lang-dropdown {
  position: relative;
}

.lang-dropdown-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(212, 167, 106, 0.2);
  border-radius: 25px;
  color: #333;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.lang-dropdown-btn:hover {
  background: #D4A76A;
  color: white;
  border-color: #D4A76A;
  transform: translateY(-2px);
}

.lang-dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(212, 167, 106, 0.15);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  padding: 8px 0;
  min-width: 120px;
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
}

.lang-dropdown-item.active {
  color: #D4A76A;
  font-weight: 600;
  background: #F5F2EB;
}

.forgot-container {
  display: flex;
  gap: 30px;
  align-items: flex-start;
  position: relative;
  z-index: 1;
  max-width: 900px;
  width: 100%;
}

.forgot-card {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
}

.card-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  margin-bottom: 20px;
}

.title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 10px;
  background: linear-gradient(90deg, #D4A76A, #8B6E3C);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtitle {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.steps-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40px;
  padding: 20px;
  background: #F5F2EB;
  border-radius: 16px;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.step-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #E0D8C8;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s ease;
}

.step.active .step-circle {
  background: linear-gradient(135deg, #D4A76A, #C29458);
  color: white;
  box-shadow: 0 4px 15px rgba(212, 167, 106, 0.4);
}

.step.completed .step-circle {
  background: #8B6E3C;
  color: white;
}

.step-label {
  font-size: 12px;
  color: #999;
  font-weight: 500;
}

.step.active .step-label {
  color: #D4A76A;
}

.step-line {
  width: 50px;
  height: 3px;
  background: #E0D8C8;
  margin: 0 15px;
  margin-bottom: 28px;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.step-line.active {
  background: linear-gradient(90deg, #D4A76A, #8B6E3C);
}

.forgot-form {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.code-input-group {
  display: flex;
  gap: 12px;
}

.code-input-group .el-input {
  flex: 1;
}

.code-input-group .el-button {
  width: 120px;
}

.password-strength {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
}

.strength-bar {
  flex: 1;
  height: 4px;
  background: #E0D8C8;
  border-radius: 2px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.strength-fill.weak { background: #f56c6c; }
.strength-fill.medium { background: #e6a23c; }
.strength-fill.strong { background: #67c23a; }

.strength-text {
  font-size: 12px;
  font-weight: 500;
}

.strength-text.weak { color: #f56c6c; }
.strength-text.medium { color: #e6a23c; }
.strength-text.strong { color: #67c23a; }

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #D4A76A, #C29458);
  border: none;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(212, 167, 106, 0.4);
}

.success-content {
  text-align: center;
  padding: 40px 0;
  animation: fadeIn 0.5s ease;
}

.success-icon {
  margin-bottom: 20px;
}

.success-content h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.success-content p {
  color: #666;
  margin-bottom: 30px;
}

.card-footer {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #E0D8C8;
  text-align: center;
}

.tips-card {
  width: 280px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 25px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  height: fit-content;
}

.tips-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
}

.tips-header .el-icon {
  color: #D4A76A;
  font-size: 20px;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li {
  position: relative;
  padding-left: 20px;
  margin-bottom: 12px;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

.tips-list li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #D4A76A;
}

@media (max-width: 768px) {
  .forgot-container {
    flex-direction: column;
  }

  .tips-card {
    width: 100%;
  }

  .forgot-card {
    padding: 30px 20px;
  }

  .steps-indicator {
    padding: 15px;
  }

  .step-line {
    width: 30px;
    margin: 0 8px;
    margin-bottom: 28px;
  }

  .step-label {
    font-size: 11px;
  }
}
</style>
