<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h2>加入星辰</h2>
        <p>开启你的治愈之旅</p>
      </div>
      <el-form :model="registerForm" @submit.prevent="handleRegister">
        <el-form-item>
          <el-input v-model="registerForm.username" placeholder="请输入账号" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次确认密码" prefix-icon="Check" show-password />
        </el-form-item>
        <el-form-item v-if="captchaEnabled">
          <div class="captcha-wrapper">
            <el-input v-model="registerForm.code" placeholder="请输入验证码" prefix-icon="CircleCheck" style="flex: 1" />
            <div class="captcha-img">
              <img :src="codeUrl" @click="getCode" alt="验证码" />
            </div>
          </div>
        </el-form-item>
        <el-button type="primary" class="register-button" :loading="loading" @click="handleRegister">注 册</el-button>
      </el-form>
      <div class="register-footer">
        <span>已有账号？</span>
        <router-link to="/login">返回登录</router-link>
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
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  code: '',
  uuid: ''
})

const getCode = async () => {
  try {
    const res = await request.get('/captchaImage')
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = 'data:image/gif;base64,' + res.img
      registerForm.uuid = res.uuid
    }
  } catch (error) {
    console.error('获取验证码失败', error)
  }
}

onMounted(() => {
  getCode()
})

const handleRegister = async () => {
  if (!registerForm.username || !registerForm.password || !registerForm.confirmPassword) {
    ElMessage.warning('请填写完整注册信息')
    return
  }
  
  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.warning('两次密码输入不一致')
    return
  }

  if (captchaEnabled.value && !registerForm.code) {
    ElMessage.warning('请输入验证码')
    return
  }
  
  loading.value = true
  try {
    const res = await request.post('/register', {
      username: registerForm.username,
      password: registerForm.password,
      code: registerForm.code,
      uuid: registerForm.uuid
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
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
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #FFF9F2 0%, #E5F1F0 100%);
}

.register-card {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(125, 185, 182, 0.1);
  text-align: center;
}

.register-header {
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

.register-button {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 20px;
}

.register-footer {
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