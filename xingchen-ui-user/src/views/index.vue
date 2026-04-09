<template>
  <div class="user-layout">
    <div class="user-sidebar">
      <div class="logo">星辰 · 用户端</div>
      <div class="menu">
        <div class="menu-item active">首页</div>
        <div class="menu-item" @click="router.push('/profile')">个人中心</div>
        <div class="menu-item" @click="handleLogout">退出登录</div>
      </div>
    </div>
    <div class="user-content">
      <div class="user-header">
        <div class="welcome">你好，{{ username }}！祝你今天愉快。</div>
      </div>
      <div class="dashboard">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card class="stats-card">
              <template #header>我的数据</template>
              <div class="stats-num">128</div>
              <div class="stats-desc">本月记录总数</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="stats-card accent">
              <template #header>待办事项</template>
              <div class="stats-num">5</div>
              <div class="stats-desc">需要你关注的事项</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="stats-card success">
              <template #header>已完成</template>
              <div class="stats-num">96%</div>
              <div class="stats-desc">本月目标进度</div>
            </el-card>
          </el-col>
        </el-row>
        
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span>最新公告</span>
              <el-button type="primary" link>查看更多</el-button>
            </div>
          </template>
          <div class="news-list">
            <div class="news-item">
              <span class="news-title">系统升级公告</span>
              <span class="news-date">2024-03-20</span>
            </div>
            <div class="news-item">
              <span class="news-title">温馨提示：保持良好心情哦</span>
              <span class="news-date">2024-03-19</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const username = ref('用户')

onMounted(async () => {
  try {
    const res = await request.get('/getInfo')
    username.value = res.user.nickName || res.user.userName
  } catch (error) {
    console.error(error)
  }
})

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

.dashboard {
  padding: 40px;
  overflow-y: auto;
}

.stats-card {
  text-align: center;
  margin-bottom: 24px;
  
  .stats-num {
    font-size: 36px;
    font-weight: bold;
    color: var(--primary);
    margin: 12px 0;
  }
  
  .stats-desc {
    color: var(--text-2);
    font-size: 14px;
  }
  
  &.accent .stats-num { color: var(--accent); }
  &.success .stats-num { color: var(--success); }
}

.content-card {
  margin-top: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
  }
}

.news-list {
  .news-item {
    display: flex;
    justify-content: space-between;
    padding: 16px 0;
    border-bottom: 1px solid var(--line);
    
    &:last-child {
      border-bottom: none;
    }
    
    .news-title {
      color: var(--text-1);
    }
    
    .news-date {
      color: var(--text-2);
      font-size: 14px;
    }
  }
}
</style>