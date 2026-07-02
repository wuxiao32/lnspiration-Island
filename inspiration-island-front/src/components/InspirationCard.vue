<template>
  <div class="inspiration-card" :class="{ 'expanded': showComments }">
    <!-- 卡片头部：头像 + 用户信息 -->
    <div class="card-top">
      <div class="avatar-link" @click="$router.push(`/user/${inspiration.userId}`)">
        <Avatar :username="inspiration.username || '匿'" :size="44" />
      </div>
      <div class="card-meta">
        <span class="card-author link" @click="$router.push(`/user/${inspiration.userId}`)">
          {{ inspiration.username || '匿名用户' }}
        </span>
        <span class="card-time">{{ formatTime(inspiration.createTime) }}</span>
      </div>
      <!-- 更多操作（仅作者可见） -->
      <el-dropdown v-if="isOwner" trigger="click" class="card-more">
        <el-button circle size="small" class="more-btn">
          <el-icon><MoreFilled /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="showEditDialog = true">
              <el-icon><Edit /></el-icon> 编辑
            </el-dropdown-item>
            <el-dropdown-item divided @click="handleDelete">
              <el-icon style="color: #f56c6c;"><Delete /></el-icon>
              <span style="color: #f56c6c;">删除</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 标题 -->
    <h3 class="card-title">{{ inspiration.title }}</h3>

    <!-- 内容 -->
    <p class="card-content">{{ inspiration.content }}</p>

    <!-- 操作栏 -->
    <div class="card-actions">
      <button class="action-btn" @click="$emit('like', inspiration.id)">
        <span class="action-icon">❤️</span>
        <span>{{ likeCount }}</span>
      </button>
      <button class="action-btn" @click="$emit('collect', inspiration.id)">
        <span class="action-icon">⭐</span>
        <span>{{ collectCount }}</span>
      </button>
      <button class="action-btn" @click="showComments = !showComments">
        <span class="action-icon">💬</span>
        <span>{{ commentList.length }} 评论</span>
      </button>
    </div>

    <!-- 评论区 -->
    <Transition name="slide">
      <div v-if="showComments" class="comment-section">
        <div class="comment-list">
          <div v-for="c in commentList" :key="c.id" class="comment-item">
            <Avatar :username="c.username || '匿'" :size="28" />
            <div class="comment-body">
              <span class="comment-author">{{ c.username || '匿名' }}</span>
              <span class="comment-text">{{ c.content }}</span>
            </div>
            <!-- 删除评论（仅作者可见） -->
            <el-button
              v-if="currentUserId && c.userId === currentUserId"
              class="comment-delete"
              :icon="Delete"
              circle
              size="small"
              @click="handleDeleteComment(c.id)"
            />
          </div>
          <div v-if="!commentList.length" class="comment-empty">暂无评论，来说两句吧~</div>
        </div>

        <div class="comment-input-row">
          <el-input
            v-model="commentText"
            placeholder="写下你的想法..."
            size="small"
            class="comment-input"
            @keyup.enter="submitComment"
          />
          <el-button type="primary" size="small" class="comment-submit" @click="submitComment">
            发送
          </el-button>
        </div>
      </div>
    </Transition>

    <!-- 编辑灵感弹窗 -->
    <el-dialog v-model="showEditDialog" title="编辑灵感" width="580px" destroy-on-close>
      <el-form :model="editForm" label-position="top">
        <el-form-item label="标题">
          <el-input v-model="editForm.title" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="editForm.content" type="textarea" :rows="8" maxlength="2000" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveEdit">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { MoreFilled, Edit, Delete } from '@element-plus/icons-vue'
import Avatar from './Avatar.vue'

const props = defineProps({
  inspiration: { type: Object, required: true },
  likeCount: { type: Number, default: 0 },
  collectCount: { type: Number, default: 0 },
  commentList: { type: Array, default: () => [] },
  isOwner: { type: Boolean, default: false },
  currentUserId: { type: Number, default: null }
})

const emit = defineEmits(['like', 'collect', 'comment', 'edit', 'delete', 'delete-comment'])

const showComments = ref(false)
const commentText = ref('')
const showEditDialog = ref(false)
const saving = ref(false)

const editForm = reactive({
  title: '',
  content: ''
})

// 打开编辑弹窗时填充当前内容
const openEditDialog = () => {
  editForm.title = props.inspiration.title
  editForm.content = props.inspiration.content
  showEditDialog.value = true
}

// 点击编辑菜单项
const handleEditClick = () => {
  openEditDialog()
}

const handleSaveEdit = async () => {
  if (!editForm.title || !editForm.content) {
    ElMessage.warning('标题和内容不能为空')
    return
  }
  saving.value = true
  try {
    await emit('edit', {
      id: props.inspiration.id,
      title: editForm.title,
      content: editForm.content
    })
    showEditDialog.value = false
  } finally {
    saving.value = false
  }
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这条灵感吗？删除后不可恢复。', '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    emit('delete', props.inspiration.id)
  } catch { /* 取消 */ }
}

const handleDeleteComment = async (commentId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    emit('delete-comment', commentId)
  } catch { /* 取消 */ }
}

const submitComment = () => {
  const text = commentText.value
  if (!text || !text.trim()) {
    ElMessage.warning('评论不能为空')
    return
  }
  emit('comment', props.inspiration.id, text, () => {
    commentText.value = ''
  })
}

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}
</script>

<style scoped>
.inspiration-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  border: 1px solid rgba(0,0,0,0.06);
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  transition: all 0.3s ease;
}
.inspiration-card:hover {
  box-shadow: 0 8px 30px rgba(0,0,0,0.08);
  transform: translateY(-2px);
  border-color: rgba(102, 126, 234, 0.2);
}
.card-top {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.avatar-link {
  cursor: pointer;
  transition: transform 0.2s;
}
.avatar-link:hover {
  transform: scale(1.08);
}
.card-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.card-author {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}
.card-author.link {
  cursor: pointer;
  transition: color 0.2s;
}
.card-author.link:hover {
  color: #667eea;
}
.card-time {
  font-size: 12px;
  color: #999;
}
.more-btn {
  border: none;
  color: #999;
}
.card-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 10px 0;
  line-height: 1.4;
}
.card-content {
  color: #555;
  line-height: 1.8;
  font-size: 15px;
  white-space: pre-wrap;
  word-break: break-word;
}
.card-actions {
  display: flex;
  gap: 8px;
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}
.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: 1px solid #eee;
  border-radius: 20px;
  background: #fafafa;
  cursor: pointer;
  font-size: 13px;
  color: #666;
  transition: all 0.2s;
}
.action-btn:hover {
  background: #f0f0ff;
  border-color: #d0d0f0;
  color: #667eea;
}
.action-icon {
  font-size: 15px;
}
/* 评论区 */
.comment-section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}
.comment-list {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: 12px;
}
.comment-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f9f9f9;
}
.comment-item:last-child {
  border-bottom: none;
}
.comment-body {
  flex: 1;
}
.comment-author {
  font-size: 13px;
  font-weight: 600;
  color: #667eea;
  margin-right: 8px;
}
.comment-text {
  font-size: 14px;
  color: #555;
}
.comment-empty {
  text-align: center;
  color: #ccc;
  font-size: 13px;
  padding: 20px;
}
.comment-delete {
  opacity: 0;
  transition: opacity 0.2s;
  color: #f56c6c;
  border: none;
}
.comment-item:hover .comment-delete {
  opacity: 1;
}
.comment-input-row {
  display: flex;
  gap: 8px;
}
.comment-input {
  flex: 1;
}
.comment-submit {
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
}
/* 过渡动画 */
.slide-enter-active, .slide-leave-active {
  transition: all 0.3s ease;
}
.slide-enter-from, .slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
