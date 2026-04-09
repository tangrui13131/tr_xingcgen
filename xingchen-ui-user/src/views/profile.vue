<template>
  <div class="user-layout">
    <div class="user-sidebar">
      <div class="logo">星辰 · 用户端</div>
      <div class="menu">
        <div class="menu-item" @click="router.push('/')">首页</div>
        <div class="menu-item active">个人中心</div>
        <div class="menu-item" @click="handleLogout">退出登录</div>
      </div>
    </div>
    <div class="user-content">
      <div class="user-header">
        <div class="welcome">个人中心</div>
      </div>
      <div class="profile-container">
        <el-row :gutter="30">
          <el-col :span="8">
            <el-card class="profile-card">
              <div class="user-avatar">
                <img :src="userForm.avatar || defaultAvatar" alt="头像" />
              </div>
              <div class="user-info-brief">
                <h3>{{ userForm.nickName || userForm.userName }}</h3>
                <p>{{ userForm.email || '暂无邮箱' }}</p>
              </div>
              <el-divider />
              <div class="user-stats-brief">
                <div class="stat-item">
                  <span class="label">角色</span>
                  <span class="value">{{ roles.join(', ') }}</span>
                </div>
                <div class="stat-item">
                  <span class="label">注册时间</span>
                  <span class="value">{{ userForm.createTime || '2024-03-01' }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="16">
            <el-card class="edit-card">
              <template #header>
                <div class="card-header">
                  <span>基本资料</span>
                </div>
              </template>
              <el-form :model="userForm" label-width="100px" class="edit-form">
                <el-form-item label="用户昵称">
                  <el-input v-model="userForm.nickName" placeholder="请输入昵称" />
                </el-form-item>
                <el-form-item label="手机号码">
                  <el-input v-model="userForm.phonenumber" placeholder="请输入手机号码" />
                </el-form-item>
                <el-form-item label="邮箱地址">
                  <el-input v-model="userForm.email" placeholder="请输入邮箱" />
                </el-form-item>
                <el-form-item label="性别">
                  <el-radio-group v-model="userForm.sex">
                    <el-radio label="0">男</el-radio>
                    <el-radio label="1">女</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleUpdate" :loading="saving">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-card>

            <el-card class="edit-card mt-20">
              <template #header>
                <div class="card-header">
                  <span>修改密码</span>
                </div>
              </template>
              <el-form :model="pwdForm" label-width="100px" class="edit-form">
                <el-form-item label="旧密码">
                  <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入旧密码" show-password />
                </el-form-item>
                <el-form-item label="新密码">
                  <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" show-password />
                </el-form-item>
                <el-form-item label="确认密码">
                  <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleUpdatePwd" :loading="savingPwd">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-col>
        </el-row>
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
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const saving = ref(false)
const savingPwd = ref(false)
const roles = ref([])

const userForm = reactive({
  userName: '',
  nickName: '',
  phonenumber: '',
  email: '',
  sex: '0',
  avatar: '',
  createTime: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

onMounted(async () => {
  await getUserProfile()
})

const getUserProfile = async () => {
  try {
    const res = await request.get('/system/user/profile')
    Object.assign(userForm, res.data)
    roles.value = res.roleGroup ? [res.roleGroup] : []
  } catch (error) {
    console.error(error)
  }
}

const handleUpdate = async () => {
  saving.value = true
  try {
    await request.put('/system/user/profile', {
      nickName: userForm.nickName,
      phonenumber: userForm.phonenumber,
      email: userForm.email,
      sex: userForm.sex
    })
    ElMessage.success('资料修改成功')
  } catch (error) {
    console.error(error)
  } finally {
    saving.value = false
  }
}

const handleUpdatePwd = async () => {
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  savingPwd.value = true
  try {
    await request.put('/system/user/profile/updatePwd', {
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码修改成功')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  } catch (error) {
    console.error(error)
  } finally {
    savingPwd.value = false
  }
}

const handleLogout = async () => {
  try {
    await request.post('/logout')
    localStorage.removeItem('token')
    ElMessage.success('已安全退出')
    router.push('/login')
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped lang="scss">
.user-layout {
  display: flex;
  height: 100vh;
}

.user-sidebar {
  width: 240px;
  background-color: var(--bg-1);
  border-right: 1px solid var(--line);
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  
  .logo {
    color: var(--primary);
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 60px;
    text-align: center;
  }
  
  .menu-item {
    padding: 16px 24px;
    margin-bottom: 12px;
    border-radius: 12px;
    cursor: pointer;
    color: var(--text-2);
    font-weight: 500;
    transition: all 0.3s;
    
    &:hover {
      background-color: var(--primary-light);
      color: var(--primary);
    }
    
    &.active {
      background-color: var(--primary);
      color: #fff;
    }
  }
}

.user-content {
  flex: 1;
  background-color: var(--bg-0);
  display: flex;
  flex-direction: column;
}

.user-header {
  height: 80px;
  padding: 0 40px;
  display: flex;
  align-items: center;
  background-color: var(--bg-1);
  border-bottom: 1px solid var(--line);
  
  .welcome {
    font-size: 18px;
    color: var(--text-1);
    font-weight: 600;
  }
}

.profile-container {
  padding: 40px;
  overflow-y: auto;
}

.profile-card {
  text-align: center;
  padding: 20px 0;
  
  .user-avatar {
    width: 120px;
    height: 120px;
    margin: 0 auto 20px;
    border-radius: 50%;
    overflow: hidden;
    border: 4px solid var(--primary-light);
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
  
  .user-info-brief {
    h3 {
      color: var(--text-1);
      margin-bottom: 8px;
    }
    p {
      color: var(--text-2);
      font-size: 14px;
    }
  }
  
  .user-stats-brief {
    padding: 0 20px;
    .stat-item {
      display: flex;
      justify-content: space-between;
      margin-bottom: 12px;
      font-size: 14px;
      .label {
        color: var(--text-2);
      }
      .value {
        color: var(--text-1);
        font-weight: 500;
      }
    }
  }
}

.edit-card {
  .card-header {
    font-weight: 600;
    color: var(--text-1);
  }
}

.edit-form {
  padding: 20px 0;
}

.mt-20 {
  margin-top: 20px;
}
</style>