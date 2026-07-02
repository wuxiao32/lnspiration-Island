<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle c1"></div>
      <div class="bg-circle c2"></div>
      <div class="bg-circle c3"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- 品牌区 -->
      <div class="brand-section">
        <div class="brand-avatar">
          <span>💡</span>
        </div>
        <h1 class="brand-title">灵感岛</h1>
        <p class="brand-desc">记录每一个灵感瞬间</p>
      </div>

      <!-- Tab 切换 -->
      <div class="tab-row">
        <button
          :class="['tab-btn', { active: activeTab === 'login' }]"
          @click="activeTab = 'login'"
        >
          登录
        </button>
        <button
          :class="['tab-btn', { active: activeTab === 'reg' }]"
          @click="activeTab = 'reg'"
        >
          注册
        </button>
        <div class="tab-indicator" :class="activeTab"></div>
      </div>

      <!-- 登录表单 -->
      <Transition name="form-fade" mode="out-in">
        <div v-if="activeTab === 'login'" key="login" class="form-wrap">
          <div class="input-group">
            <label class="input-label">用户名</label>
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
              clearable
            />
          </div>
          <div class="input-group">
            <label class="input-label">密码</label>
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
              show-password
              @keyup.enter="handleLogin"
            />
          </div>
          <el-button
            type="primary"
            size="large"
            class="submit-btn"
            :loading="loginLoading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </div>

        <!-- 注册表单 -->
        <div v-else key="reg" class="form-wrap">
          <div class="input-group">
            <label class="input-label">用户名</label>
            <el-input
              v-model="regForm.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
              clearable
            />
          </div>
          <div class="input-group">
            <label class="input-label">密码</label>
            <el-input
              v-model="regForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
              show-password
              @keyup.enter="handleReg"
            />
          </div>
          <el-button
            type="primary"
            size="large"
            class="submit-btn reg-btn"
            :loading="regLoading"
            @click="handleReg"
          >
            注 册
          </el-button>
        </div>
      </Transition>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login, register } from '@/api/user'

const router = useRouter()

// 解码 JWT payload 获取 userId
const decodeJwt = (token) => {
  try {
    const payload = token.split('.')[1]
    const decoded = JSON.parse(atob(payload))
    return decoded.userId
  } catch { return null }
}
const activeTab = ref('login')
const loginLoading = ref(false)
const regLoading = ref(false)

const loginForm = reactive({ username: '', password: '' })
const regForm = reactive({ username: '', password: '' })

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  loginLoading.value = true
  try {
    const res = await login(loginForm)
    const token = res.data
    localStorage.setItem('token', token)
    localStorage.setItem('username', loginForm.username)
    // 从 JWT 解析 userId 并保存
    const userId = decodeJwt(token)
    if (userId) localStorage.setItem('userId', String(userId))
    ElMessage.success('登录成功')
    router.push('/home')
  } catch {
    /* 错误已在拦截器中处理 */
  } finally {
    loginLoading.value = false
  }
}

const handleReg = async () => {
  if (!regForm.username || !regForm.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (regForm.password.length < 4) {
    ElMessage.warning('密码至少4位')
    return
  }
  regLoading.value = true
  try {
    await register(regForm)
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    regForm.username = ''
    regForm.password = ''
  } catch {
    /* 错误已在拦截器中处理 */
  } finally {
    regLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf4 100%);
  overflow: hidden;
}

/* ========== 背景装饰圆 ========== */
.bg-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.4;
}
.c1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(102,126,234,0.2), transparent 70%);
  top: -150px;
  right: -100px;
}
.c2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(245,87,108,0.15), transparent 70%);
  bottom: -100px;
  left: -80px;
}
.c3 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(118,75,162,0.2), transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

/* ========== 登录卡片 ========== */
.login-card {
  position: relative;
  z-index: 1;
  width: 420px;
  max-width: 90vw;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 40px 36px;
  box-shadow:
    0 8px 32px rgba(0,0,0,0.08),
    0 2px 8px rgba(0,0,0,0.04);
  border: 1px solid rgba(255,255,255,0.6);
}

/* ========== 品牌区 ========== */
.brand-section {
  text-align: center;
  margin-bottom: 28px;
}
.brand-avatar {
  width: 72px;
  height: 72px;
  margin: 0 auto 14px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  box-shadow: 0 8px 24px rgba(102,126,234,0.35);
}
.brand-title {
  font-size: 28px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 4px;
}
.brand-desc {
  font-size: 14px;
  color: #999;
  margin: 0;
}

/* ========== Tab 切换 ========== */
.tab-row {
  display: flex;
  position: relative;
  background: #f0f0f5;
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 24px;
}
.tab-btn {
  flex: 1;
  padding: 10px 0;
  border: none;
  background: transparent;
  font-size: 15px;
  font-weight: 600;
  color: #999;
  cursor: pointer;
  border-radius: 10px;
  position: relative;
  z-index: 1;
  transition: color 0.3s;
}
.tab-btn.active {
  color: #fff;
}
.tab-indicator {
  position: absolute;
  top: 4px;
  bottom: 4px;
  width: calc(50% - 4px);
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 10px;
  transition: transform 0.3s ease;
}
.tab-indicator.login {
  transform: translateX(0);
}
.tab-indicator.reg {
  transform: translateX(100%);
}

/* ========== 表单 ========== */
.input-group {
  margin-bottom: 20px;
}
.input-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #555;
  margin-bottom: 8px;
}
.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 14px;
  margin-top: 8px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  letter-spacing: 4px;
  transition: all 0.3s;
}
.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(102,126,234,0.4);
}

/* 表单切换动画 */
.form-fade-enter-active,
.form-fade-leave-active {
  transition: all 0.25s ease;
}
.form-fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}
.form-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
