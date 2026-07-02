<template>
  <div class="home-page">
    <!-- Hero 横幅 -->
    <section class="hero-banner">
      <div class="hero-content">
        <h1 class="hero-title">
          <span class="hero-icon">💡</span>
          灵感广场
        </h1>
        <p class="hero-subtitle">发现精彩创意，分享你的灵感瞬间</p>
        <div class="hero-stats">
          <div class="stat-item">
            <span class="stat-num">{{ inspirationList.length }}</span>
            <span class="stat-label">条灵感</span>
          </div>
        </div>
      </div>
      <div class="hero-bg-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </section>

    <!-- 灵感卡片列表 -->
    <section class="inspiration-feed">
      <TransitionGroup name="card-list" tag="div" class="feed-list">
        <InspirationCard
          v-for="item in inspirationList"
          :key="item.id"
          :inspiration="item"
          :like-count="likeCountMap[item.id] || 0"
          :collect-count="collectCountMap[item.id] || 0"
          :comment-list="commentMap[item.id] || []"
          :is-owner="currentUserId && item.userId === currentUserId"
          :current-user-id="currentUserId"
          @like="handleLike"
          @collect="handleCollect"
          @comment="handleComment"
          @edit="handleEdit"
          @delete="handleDelete"
          @delete-comment="handleDeleteComment"
        />
      </TransitionGroup>

      <div v-if="!inspirationList.length && !loading" class="empty-state">
        <div class="empty-icon">✨</div>
        <h3>还没有灵感</h3>
        <p>成为第一个分享灵感的人吧！</p>
        <el-button type="primary" @click="$router.push('/publish')">发布灵感</el-button>
      </div>

      <div v-if="loading" class="loading-state">
        <el-icon class="loading-icon" :size="32"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { getInspirationList, updateInspiration, deleteInspiration } from '@/api/inspiration'
import { toggleLike, getLikeCount } from '@/api/like'
import { toggleCollect, getCollectCount } from '@/api/collect'
import { publishComment, getCommentList, deleteComment } from '@/api/comment'
import InspirationCard from '@/components/InspirationCard.vue'

const router = useRouter()
const token = ref(localStorage.getItem('token'))
const currentUserId = ref(null)
const inspirationList = ref([])
const loading = ref(true)
const likeCountMap = reactive({})
const collectCountMap = reactive({})
const commentMap = reactive({})

// 从 token 解析出当前用户 ID（简单方式：存 localStorage）
onMounted(() => {
  const storedUserId = localStorage.getItem('userId')
  if (storedUserId) {
    currentUserId.value = Number(storedUserId)
  }
  loadList()
})

const loadList = async () => {
  loading.value = true
  try {
    const res = await getInspirationList()
    inspirationList.value = res.data || []
    for (const item of inspirationList.value) {
      loadCounts(item.id)
      loadComments(item.id)
    }
  } finally {
    loading.value = false
  }
}

const loadCounts = async (id) => {
  try {
    const [likeRes, colRes] = await Promise.all([
      getLikeCount(id),
      getCollectCount(id)
    ])
    likeCountMap[id] = likeRes.data
    collectCountMap[id] = colRes.data
  } catch { /* ignore */ }
}

const loadComments = async (id) => {
  try {
    const res = await getCommentList(id)
    commentMap[id] = res.data || []
  } catch { /* ignore */ }
}

const handleLike = async (id) => {
  if (!token.value) { ElMessage.warning('请先登录'); router.push('/login'); return }
  await toggleLike(id)
  await loadCounts(id)
}

const handleCollect = async (id) => {
  if (!token.value) { ElMessage.warning('请先登录'); router.push('/login'); return }
  await toggleCollect(id)
  await loadCounts(id)
}

const handleComment = async (id, text, clearFn) => {
  if (!token.value) { ElMessage.warning('请先登录'); router.push('/login'); return }
  if (!text || !text.trim()) { ElMessage.warning('评论不能为空'); return }
  await publishComment({ inspirationId: id, content: text })
  clearFn()
  await loadComments(id)
  ElMessage.success('评论成功')
}

// 编辑灵感
const handleEdit = async (payload) => {
  await updateInspiration(payload)
  ElMessage.success('修改成功')
  await loadList()
}

// 删除灵感
const handleDelete = async (id) => {
  await deleteInspiration(id)
  ElMessage.success('已删除')
  await loadList()
}

// 删除评论
const handleDeleteComment = async (commentId) => {
  await deleteComment(commentId)
  ElMessage.success('评论已删除')
  // 重新加载所有评论
  for (const item of inspirationList.value) {
    await loadComments(item.id)
  }
}
</script>

<style scoped>
.home-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px 60px;
}
.hero-banner {
  position: relative;
  margin: 24px 0 32px;
  padding: 40px 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  overflow: hidden;
  color: #fff;
}
.hero-content {
  position: relative;
  z-index: 1;
}
.hero-title {
  font-size: 32px;
  font-weight: 800;
  margin: 0 0 8px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 12px;
}
.hero-icon {
  font-size: 36px;
  animation: bounce 2s ease-in-out infinite;
}
@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}
.hero-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 20px;
}
.hero-stats {
  display: flex;
  gap: 24px;
}
.stat-item {
  display: flex;
  flex-direction: column;
}
.stat-num {
  font-size: 28px;
  font-weight: 800;
}
.stat-label {
  font-size: 13px;
  opacity: 0.8;
}
.hero-bg-shapes .shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255,255,255,0.1);
}
.shape-1 { width: 200px; height: 200px; top: -60px; right: -40px; }
.shape-2 { width: 120px; height: 120px; bottom: -30px; right: 80px; }
.shape-3 { width: 60px; height: 60px; top: 40px; right: 160px; }

.inspiration-feed { animation: fadeInUp 0.5s ease; }
.card-list-enter-active { transition: all 0.5s ease; }
.card-list-enter-from { opacity: 0; transform: translateY(30px); }

.empty-state { text-align: center; padding: 80px 20px; color: var(--text-muted); }
.empty-icon { font-size: 64px; margin-bottom: 16px; }
.empty-state h3 { font-size: 20px; color: var(--text-primary); margin: 0 0 8px; }
.empty-state p { font-size: 14px; margin-bottom: 24px; }

.loading-state { text-align: center; padding: 60px 20px; color: var(--text-muted); display: flex; flex-direction: column; align-items: center; gap: 12px; }
.loading-icon { animation: spin 1s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
</style>
