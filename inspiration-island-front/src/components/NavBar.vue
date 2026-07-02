<template>
  <nav class="navbar">
    <div class="navbar-inner">
      <!-- 左侧 Logo -->
      <div class="nav-left" @click="$router.push('/home')">
        <span class="logo-icon">💡</span>
        <span class="logo-text">灵感岛</span>
      </div>

      <!-- 右侧操作 -->
      <div class="nav-right">
        <template v-if="token">
          <el-button class="nav-btn" @click="$router.push('/publish')">
            <el-icon><Edit /></el-icon>
            <span>发布灵感</span>
          </el-button>
          <el-button class="nav-btn" @click="$router.push('/my')">
            <el-icon><Document /></el-icon>
            <span>我的灵感</span>
          </el-button>

          <!-- 用户头像 + 下拉菜单 -->
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-badge">
              <Avatar :username="username" :size="36" />
              <span class="username-text">{{ username }}</span>
              <el-icon class="arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>
                  <div class="dropdown-user">
                    <Avatar :username="username" :size="40" />
                    <div>
                      <div class="dropdown-name">{{ username }}</div>
                      <div class="dropdown-hint">已登录</div>
                    </div>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item divided command="profile">
                  <el-icon><User /></el-icon> 个人主页
                </el-dropdown-item>
                <el-dropdown-item command="my">
                  <el-icon><Document /></el-icon> 我的灵感
                </el-dropdown-item>
                <el-dropdown-item command="publish">
                  <el-icon><Edit /></el-icon> 发布灵感
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <template v-else>
          <el-button class="nav-btn login-btn" type="primary" @click="$router.push('/login')">
            登录 / 注册
          </el-button>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Avatar from './Avatar.vue'
import { logout as logoutApi } from '@/api/user'

const router = useRouter()
const token = ref(localStorage.getItem('token'))
const username = ref('')

onMounted(() => {
  username.value = localStorage.getItem('username') || '用户'
})

window.addEventListener('storage', () => {
  token.value = localStorage.getItem('token')
  username.value = localStorage.getItem('username') || '用户'
})

const handleCommand = async (cmd) => {
  if (cmd === 'logout') {
    try { await logoutApi() } catch {}  // 调后端删 Redis key
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('userId')
    token.value = null
    username.value = ''
    ElMessage.success('已退出登录')
    router.push('/home')
  } else if (cmd === 'profile') {
    const userId = localStorage.getItem('userId')
    if (userId) router.push(`/user/${userId}`)
  } else if (cmd === 'my') {
    router.push('/my')
  } else if (cmd === 'publish') {
    router.push('/publish')
  }
}

defineExpose({
  refresh() {
    token.value = localStorage.getItem('token')
    username.value = localStorage.getItem('username') || '用户'
  }
})
</script>

<style scoped>
.navbar {
  position: sticky; top: 0; z-index: 1000;
  backdrop-filter: blur(20px); background: rgba(255,255,255,0.85);
  border-bottom: 1px solid rgba(0,0,0,0.06); box-shadow: 0 1px 8px rgba(0,0,0,0.04);
}
.navbar-inner {
  max-width: 1100px; margin: 0 auto; height: 60px;
  display: flex; align-items: center; justify-content: space-between; padding: 0 24px;
}
.nav-left { display: flex; align-items: center; gap: 10px; cursor: pointer; user-select: none; }
.logo-icon { font-size: 28px; animation: float 3s ease-in-out infinite; }
@keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-4px); } }
.logo-text {
  font-size: 22px; font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;
}
.nav-right { display: flex; align-items: center; gap: 12px; }
.nav-btn { border-radius: 10px; font-weight: 500; }
.login-btn {
  background: linear-gradient(135deg, #667eea, #764ba2); border: none; border-radius: 10px; padding: 8px 22px;
}
.login-btn:hover { background: linear-gradient(135deg, #764ba2, #667eea); }
.user-badge {
  display: flex; align-items: center; gap: 8px; padding: 4px 12px 4px 4px;
  border-radius: 24px; cursor: pointer; transition: background 0.2s;
}
.user-badge:hover { background: rgba(102, 126, 234, 0.08); }
.username-text { font-size: 14px; font-weight: 500; color: #333; }
.arrow { font-size: 12px; color: #999; transition: transform 0.2s; }
.dropdown-user { display: flex; align-items: center; gap: 12px; padding: 4px 0; }
.dropdown-name { font-weight: 600; font-size: 15px; color: #333; }
.dropdown-hint { font-size: 12px; color: #999; margin-top: 2px; }
</style>
