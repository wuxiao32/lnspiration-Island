<template>
  <div class="profile-page">
    <!-- 加载中 -->
    <div v-if="loading" class="loading-state">
      <el-icon class="loading-icon" :size="32"><Loading /></el-icon>
      <span>加载中...</span>
    </div>

    <template v-else-if="profile">
      <!-- 用户信息卡片 -->
      <section class="profile-header">
        <div class="profile-bg"></div>
        <div class="profile-content">
          <Avatar :username="profile.username" :size="88" />
          <h1 class="profile-name">{{ profile.username }}</h1>
          <p class="profile-id">@{{ profile.id }}</p>
        </div>
      </section>

      <!-- 统计卡片 -->
      <div class="stats-row">
        <div class="stat-card">
          <span class="stat-icon">📝</span>
          <span class="stat-value">{{ profile.inspirationCount }}</span>
          <span class="stat-label">灵感</span>
        </div>
        <div class="stat-card">
          <span class="stat-icon">💬</span>
          <span class="stat-value">{{ profile.commentCount }}</span>
          <span class="stat-label">评论</span>
        </div>
        <div class="stat-card">
          <span class="stat-icon">❤️</span>
          <span class="stat-value">{{ totalLikes }}</span>
          <span class="stat-label">获赞</span>
        </div>
      </div>

      <!-- TA 的灵感列表 -->
      <section class="profile-inspirations">
        <h3 class="section-title">
          <span v-if="isSelf">我的灵感</span>
          <span v-else>TA 的灵感</span>
        </h3>

        <TransitionGroup name="card-list" tag="div">
          <InspirationCard
            v-for="item in userInspirations"
            :key="item.id"
            :inspiration="item"
            :is-owner="isSelf"
            :current-user-id="currentUserId"
            :like-count="likeCountMap[item.id] || 0"
            :collect-count="collectCountMap[item.id] || 0"
            :comment-list="commentMap[item.id] || []"
            @like="handleLike"
            @collect="handleCollect"
            @comment="handleComment"
            @edit="handleEdit"
            @delete="handleDelete"
            @delete-comment="handleDeleteComment"
          />
        </TransitionGroup>

        <div v-if="!userInspirations.length" class="empty-state">
          <span class="empty-icon">💭</span>
          <p v-if="isSelf">你还没有发布灵感</p>
          <p v-else>TA 还没有发布灵感</p>
        </div>
      </section>
    </template>

    <!-- 用户不存在 -->
    <div v-else class="empty-state">
      <div class="empty-icon">🔍</div>
      <h3>用户不存在</h3>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { getUserProfile } from '@/api/user'
import { getInspirationList, updateInspiration, deleteInspiration } from '@/api/inspiration'
import { toggleLike, getLikeCount } from '@/api/like'
import { toggleCollect, getCollectCount } from '@/api/collect'
import { publishComment, getCommentList, deleteComment } from '@/api/comment'
import Avatar from '@/components/Avatar.vue'
import InspirationCard from '@/components/InspirationCard.vue'

const route = useRoute()
const router = useRouter()
const profile = ref(null)
const loading = ref(true)
const currentUserId = ref(Number(localStorage.getItem('userId') || 0))
const userInspirations = ref([])
const likeCountMap = reactive({})
const collectCountMap = reactive({})
const commentMap = reactive({})

const isSelf = computed(() => {
  return currentUserId.value && profile.value && currentUserId.value === profile.value.id
})

const totalLikes = computed(() => {
  return Object.values(likeCountMap).reduce((sum, n) => sum + n, 0)
})

onMounted(async () => {
  const userId = Number(route.params.id)
  if (!userId) {
    loading.value = false
    return
  }
  try {
    const res = await getUserProfile(userId)
    profile.value = res.data
    // 加载该用户的所有灵感
    await loadUserInspirations()
  } catch {
    profile.value = null
  } finally {
    loading.value = false
  }
})

const loadUserInspirations = async () => {
  try {
    // 通过全量列表过滤（简单方案，实际可加后端接口）
    const res = await getInspirationList()
    const allList = res.data || []
    userInspirations.value = allList.filter(item => item.userId === profile.value.id)
    for (const item of userInspirations.value) {
      loadCounts(item.id)
      loadComments(item.id)
    }
  } catch { /* ignore */ }
}

const loadCounts = async (id) => {
  try {
    const [likeRes, colRes] = await Promise.all([
      getLikeCount(id), getCollectCount(id)
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
  if (!currentUserId.value) { ElMessage.warning('请先登录'); router.push('/login'); return }
  await toggleLike(id)
  await loadCounts(id)
}

const handleCollect = async (id) => {
  if (!currentUserId.value) { ElMessage.warning('请先登录'); router.push('/login'); return }
  await toggleCollect(id)
  await loadCounts(id)
}

const handleComment = async (id, text, clearFn) => {
  if (!currentUserId.value) { ElMessage.warning('请先登录'); router.push('/login'); return }
  if (!text || !text.trim()) { ElMessage.warning('评论不能为空'); return }
  await publishComment({ inspirationId: id, content: text })
  clearFn()
  await loadComments(id)
  ElMessage.success('评论成功')
}

const handleEdit = async (payload) => {
  await updateInspiration(payload)
  ElMessage.success('修改成功')
  await loadUserInspirations()
}

const handleDelete = async (id) => {
  await deleteInspiration(id)
  ElMessage.success('已删除')
  await loadUserInspirations()
}

const handleDeleteComment = async (commentId) => {
  await deleteComment(commentId)
  ElMessage.success('评论已删除')
  for (const item of userInspirations.value) {
    await loadComments(item.id)
  }
}
</script>

<style scoped>
.profile-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px 60px;
}

/* 用户头部 */
.profile-header {
  position: relative;
  margin: 24px 0 24px;
  border-radius: 20px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 2px 16px rgba(0,0,0,0.04);
  border: 1px solid rgba(0,0,0,0.06);
}
.profile-bg {
  height: 100px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
}
.profile-content {
  padding: 0 32px 28px;
  margin-top: -44px;
  position: relative;
  z-index: 1;
  text-align: center;
}
.profile-name {
  font-size: 26px;
  font-weight: 800;
  color: var(--text-primary);
  margin: 12px 0 4px;
}
.profile-id {
  font-size: 14px;
  color: var(--text-muted);
  margin: 0;
}

/* 统计 */
.stats-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 16px;
  margin-bottom: 32px;
}
.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 4px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
  border: 1px solid rgba(0,0,0,0.04);
}
.stat-icon { font-size: 24px; }
.stat-value { font-size: 28px; font-weight: 800; color: var(--text-primary); }
.stat-label { font-size: 13px; color: var(--text-muted); }

/* 灵感列表 */
.section-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 16px;
}

.empty-state { text-align: center; padding: 60px 20px; color: var(--text-muted); }
.empty-icon { font-size: 56px; display: block; margin-bottom: 12px; }

.loading-state { text-align: center; padding: 80px 20px; color: var(--text-muted); display: flex; flex-direction: column; align-items: center; gap: 12px; }
.loading-icon { animation: spin 1s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

.card-list-enter-active { transition: all 0.5s ease; }
.card-list-enter-from { opacity: 0; transform: translateY(30px); }
</style>
