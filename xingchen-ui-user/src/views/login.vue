<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>星辰 · 用户端</h2>
        <p>愿你的一天充满阳光与温暖</p>
      </div>
      <el-form :model="loginForm" @submit.prevent="handleLogin">
        <el-form-item>
          <el-input v-model="loginForm.username" placeholder="请输入账号" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item v-if="captchaEnabled">
          <div class="captcha-wrapper">
            <el-input v-model="loginForm.code" placeholder="请输入验证码" prefix-icon="CircleCheck" style="flex: 1" />
            <div class="captcha-img">
              <img :src="codeUrl" @click="getCode" alt="验证码" />
            </div>
          </div>
        </el-form-item>
        <el-button type="primary" class="login-button" :loading="loading" @click="handleLogin">登 录</el-button>
      </el-form>
      <div class="login-footer">
        <span>还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const captchaEnabled = ref(true)
const codeUrl = ref('')
const loginForm = reactive({
  username: '',
  password: '',
  code: '',
  uuid: ''
})

const getCode = async () => {
  try {
    const res = await request.get('/captchaImage')
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = 'data:image/gif;base64,' + res.img
      loginForm.uuid = res.uuid
    }
  } catch (error) {
    console.error('获取验证码失败', error)
  }
}

onMounted(() => {
  getCode()
})

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  if (captchaEnabled.value && !loginForm.code) {
    ElMessage.warning('请输入验证码')
    return
  }
  
  loading.value = true
  try {
    const res = await request.post('/login', {
      username: loginForm.username,
      password: loginForm.password,
      code: loginForm.code,
      uuid: loginForm.uuid
    })
    if (res.token) {
      localStorage.setItem('token', res.token)
      ElMessage.success('欢迎回来')
      router.push('/')
    }
  } catch (error) {
    console.error(error)
    if (captchaEnabled.value) {
      getCode()
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #FFF9F2 0%, #E5F1F0 100%);
}

.login-card {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(125, 185, 182, 0.1);
  text-align: center;
}

.login-header {
  margin-bottom: 40px;
  h2 {
    color: var(--primary);
    font-size: 28px;
    margin-bottom: 8px;
  }
  p {
    color: var(--text-2);
    font-size: 14px;
  }
}

.captcha-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.captcha-img {
  width: 110px;
  height: 40px;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  img {
    width: 100%;
    height: 100%;
  }
}

.login-button {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 20px;
}

.login-footer {
  margin-top: 24px;
  font-size: 14px;
  color: var(--text-2);
  a {
    color: var(--primary);
    text-decoration: none;
    font-weight: 600;
    margin-left: 4px;
    &:hover {
      color: var(--primary-hover);
    }
  }
}
</style>