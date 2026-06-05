<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Notebook } from '@element-plus/icons-vue'
import { login, register } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import loginBg from '@/assets/login-bg.jpg'

const router = useRouter()
const userStore = useUserStore()

const isLogin = ref(true)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const loginFormRef = ref(null)
const registerFormRef = ref(null)

async function handleLogin() {
  try {
    await loginFormRef.value.validate()
    loading.value = true

    const data = await login(loginForm)
    userStore.setUser(data.user)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  try {
    await registerFormRef.value.validate()
    loading.value = true

    await register({
      username: registerForm.username,
      password: registerForm.password
    })
    ElMessage.success('注册成功，请登录')
    isLogin.value = true
    loginForm.username = registerForm.username
    registerForm.username = ''
    registerForm.password = ''
    registerForm.confirmPassword = ''
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}

function switchMode() {
  isLogin.value = !isLogin.value
}
</script>

<template>
  <div class="login-page">
    <!-- 背景层 -->
    <div class="bg-layer" :style="{ backgroundImage: `url(${loginBg})` }">
      <div class="bg-overlay" />
    </div>

    <!-- 浮动光斑 -->
    <div class="light-particles">
      <div class="particle p1"></div>
      <div class="particle p2"></div>
      <div class="particle p3"></div>
      <div class="particle p4"></div>
    </div>

    <!-- 玻璃拟态卡片 -->
    <div class="glass-card">
      <div class="card-header">
        <div class="logo-wrapper">
          <el-icon class="logo-icon"><Notebook /></el-icon>
        </div>
        <h1 class="title">书店管理系统</h1>
        <p class="subtitle">{{ isLogin ? '欢迎回来，请登录您的账户' : '创建新账户，开始管理书店' }}</p>
      </div>

      <div class="card-body">
        <!-- 登录表单 -->
        <el-form
          v-if="isLogin"
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="auth-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              :prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="loading"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 注册表单 -->
        <el-form
          v-else
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="auth-form"
          @submit.prevent="handleRegister"
        >
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="用户名"
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="密码"
              :prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="确认密码"
              :prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="loading"
              @click="handleRegister"
            >
              注 册
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="card-footer">
        <span class="switch-text">
          {{ isLogin ? '还没有账号？' : '已有账号？' }}
        </span>
        <el-button type="primary" link class="switch-btn" @click="switchMode">
          {{ isLogin ? '立即注册' : '立即登录' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ========== 页面容器 ========== */
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* ========== 背景层 ========== */
.bg-layer {
  position: absolute;
  inset: 0;
  z-index: 0;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.bg-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.75) 0%,
    rgba(248, 250, 252, 0.65) 50%,
    rgba(238, 242, 255, 0.7) 100%
  );
}

/* ========== 浮动光斑 ========== */
.light-particles {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 1;
}

.particle {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.12;
}

.p1 {
  width: 400px; height: 400px;
  top: -10%; left: -5%;
  background: radial-gradient(circle, #f59e0b, transparent);
  animation: drift-1 18s ease-in-out infinite;
}

.p2 {
  width: 300px; height: 300px;
  top: 50%; right: -8%;
  background: radial-gradient(circle, #3b82f6, transparent);
  animation: drift-2 22s ease-in-out infinite;
}

.p3 {
  width: 250px; height: 250px;
  bottom: -8%; left: 20%;
  background: radial-gradient(circle, #8b5cf6, transparent);
  animation: drift-3 20s ease-in-out infinite;
}

.p4 {
  width: 200px; height: 200px;
  top: 30%; left: 50%;
  background: radial-gradient(circle, #ec4899, transparent);
  animation: drift-4 16s ease-in-out infinite;
}

@keyframes drift-1 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(40px, -30px) scale(1.15); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
}

@keyframes drift-2 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-30px, -25px) scale(1.2); }
}

@keyframes drift-3 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(25px, 20px) scale(1.1); }
}

@keyframes drift-4 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-20px, -15px) scale(1.25); }
}

/* ========== Card ========== */
.glass-card {
  position: relative;
  z-index: 2;
  width: 440px;
  padding: 44px 40px 36px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow:
    0 4px 24px rgba(0, 0, 0, 0.08),
    0 1px 4px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

/* ========== Card header ========== */
.card-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-wrapper {
  width: 68px;
  height: 68px;
  margin: 0 auto 18px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(99, 102, 241, 0.08) 100%);
  border: 1px solid rgba(59, 130, 246, 0.15);
}

.logo-icon {
  font-size: 34px;
  color: #3b82f6;
}

.title {
  font-size: 26px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 8px;
  letter-spacing: 1px;
}

.subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* ========== 表单样式 ========== */
.auth-form {
  margin-bottom: 20px;
}

/* ========== Form ========== */
.auth-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.auth-form :deep(.el-form-item__error) {
  color: #ef4444;
}

/* Submit button */
.submit-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
  color: #fff;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.25);
  transition: all 0.25s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.35);
  background: linear-gradient(135deg, #2563eb 0%, #4f46e5 100%);
}

.submit-btn:active {
  transform: translateY(0);
}

/* ========== Card footer ========== */
.card-footer {
  text-align: center;
  padding-top: 22px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.switch-text {
  color: #64748b;
  font-size: 14px;
}

.switch-btn {
  font-size: 14px;
  font-weight: 600;
  margin-left: 4px;
  color: #3b82f6 !important;
  transition: color 0.2s;
}

.switch-btn:hover {
  color: #2563eb !important;
}

/* ========== 响应式 ========== */
@media (max-width: 500px) {
  .glass-card {
    width: 92%;
    padding: 32px 24px 28px;
    border-radius: 20px;
  }

  .title {
    font-size: 22px;
  }

  .logo-wrapper {
    width: 56px;
    height: 56px;
  }

  .logo-icon {
    font-size: 28px;
  }
}
</style>
