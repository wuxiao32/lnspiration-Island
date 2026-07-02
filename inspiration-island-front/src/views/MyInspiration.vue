<template>
  <div class="my-page">
    <!-- 页面头 -->
    <section class="my-header">
      <div class="my-header-content">
        <div class="header-left">
          <div class="avatar-link" @click="$router.push(`/user/${currentUserId}`)">
            <Avatar :username="username" :size="64" />
          </div>
          <div class="header-info">
            <h2>{{ username }} 的灵感</h2>
            <p>共 {{ list.length }} 条灵感记录</p>
          </div>
        </div>
        <el-button type="primary" class="publish-btn" @click="$router.push('/publish')">
          <el-icon><Edit /></el-icon>
          发布新灵感
        </el-button>
      </div>
    </section>

    <!-- 统计卡片 -->
    <div class="stats-row" v-if="list.length">
      <div class="stat-card">
        <span class="stat-icon">📝</span>
        <span class="stat-value">{{ list.length }}</span>
        <span class="stat-label">灵感总数</span>
      </div>
      <div class="stat-card">
        <span class="stat-icon">💬</span>
        <span class="stat-value">{{ totalComments }}</span>
        <span class="stat-label">总评论</span>
      </div>
    </div>

    <!-- 灵感列表 -->
    <section class="my-list">
      <TransitionGroup name="card-list" tag="div">
        <InspirationCard
          v-for="item in list"
          :key="item.id"
          :inspiration="item"
          :is-owner="true"
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

      <div v-if="!list.length && !loading" class="empty-state">
        <div class="empty-icon">📭</div>
        <h3>还没有灵感</h3>
        <p>快去发布你的第一条灵感吧！</p>
        <el-button type="primary" @click="$router.push('/publish')">
          <el-icon><Edit /></el-icon> 发布灵感
        </el-button>
      </div>

      <div v-if="loading" class="loading-state">
        <el-icon class="loading-icon" :size="28"><Loading /></el-icon>
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
import { getMyInspiration, updateInspiration, deleteInspiration } from '@/api/inspiration'
import { toggleLike, getLikeCount } from '@/api/like'
import { toggleCollect, getCollectCount } from '@/api/collect'
import { publishComment, getCommentList, deleteComment } from '@/api/comment'
import Avatar from '@/components/Avatar.vue'
import InspirationCard from '@/components/InspirationCard.vue'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '用户')
const currentUserId = ref(Number(localStorage.getItem('userId') || 0))
const list = ref([])
const loading = ref(true)
const likeCountMap = reactive({})
const collectCountMap = reactive({})
const commentMap = reactive({})

const totalComments = computed(() => {
  return Object.values(commentMap).reduce((sum, comments) => sum + comments.length, 0)
})

const loadList = async () => {
  loading.value = true
  try {
    const res = await getMyInspiration()
    list.value = res.data || []
    for (const item of list.value) {
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
  await toggleLike(id)
  await loadCounts(id)
}

const handleCollect = async (id) => {
  await toggleCollect(id)
  await loadCounts(id)
}

const handleComment = async (id, text, clearFn) => {
  if (!text || !text.trim()) { ElMessage.warning('评论不能为空'); return }
  await publishComment({ inspirationId: id, content: text })
  clearFn()
  await loadComments(id)
  ElMessage.success('评论成功')
}

const handleEdit = async (payload) => {
  await updateInspiration(payload)
  ElMessage.success('修改成功')
  await loadList()
}

const handleDelete = async (id) => {
  await deleteInspiration(id)
  ElMessage.success('已删除')
  await loadList()
}

const handleDeleteComment = async (commentId) => {
  await deleteComment(commentId)
  ElMessage.success('评论已删除')
  for (const item of list.value) {
    await loadComments(item.id)
  }
}

onMounted(() => loadList())
</script>

<style scoped>
.my-page { max-width: 800px; margin: 0 auto; padding: 0 20px 60px; }

.my-header {
  background: #fff; border-radius: 20px; padding: 28px 32px;
  margin: 24px 0; box-shadow: 0 2px 16px rgba(0,0,0,0.04); border: 1px solid rgba(0,0,0,0.06);
}
.my-header-content { display: flex; align-items: center; justify-content: space-between; }
.header-left { display: flex; align-items: center; gap: 16px; }
.avatar-link { cursor: pointer; transition: transform 0.2s; }
.avatar-link:hover { transform: scale(1.08); }
.header-info h2 { font-size: 22px; font-weight: 700; color: var(--text-primary); margin: 0 0 4px; }
.header-info p { font-size: 14px; color: var(--text-muted); margin: 0; }
.publish-btn {
  border-radius: 12px; font-weight: 600;
  background: linear-gradient(135deg, #667eea, #764ba2); border: none; transition: all 0.3s;
}
.publish-btn:hover { transform: translateY(-1px); box-shadow: 0 8px 24px rgba(102,126,234,0.35); }

.stats-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 24px; }
.stat-card {
  background: #fff; border-radius: 16px; padding: 20px 24px;
  display: flex; flex-direction: column; gap: 4px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03); border: 1px solid rgba(0,0,0,0.04);
}
.stat-icon { font-size: 24px; }
.stat-value { font-size: 28px; font-weight: 800; color: var(--text-primary); }
.stat-label { font-size: 13px; color: var(--text-muted); }

.empty-state, .loading-state { text-align: center; padding: 60px 20px; color: var(--text-muted); }
.empty-icon { font-size: 64px; margin-bottom: 16px; }
.empty-state h3 { font-size: 20px; color: var(--text-primary); margin: 0 0 8px; }
.empty-state p { font-size: 14px; margin-bottom: 24px; }
.loading-icon { animation: spin 1s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

.card-list-enter-active { transition: all 0.5s ease; }
.card-list-enter-from { opacity: 0; transform: translateY(30px); }
</style>
