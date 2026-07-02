<template>
  <div class="publish-page">
    <div class="publish-card">
      <!-- 页面标题 -->
      <div class="page-header">
        <div class="header-icon">✍️</div>
        <h2>发布灵感</h2>
        <p>写下你此刻的想法，与世界分享</p>
      </div>

      <!-- 表单 -->
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="给你的灵感取个名字..."
            size="large"
            maxlength="100"
            show-word-limit
            class="title-input"
          />
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            placeholder="尽情写下你的灵感内容..."
            :rows="10"
            maxlength="2000"
            show-word-limit
            class="content-input"
          />
        </el-form-item>

        <div class="form-actions">
          <el-button size="large" @click="$router.push('/home')">
            取消
          </el-button>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="submit-btn"
            @click="submit"
          >
            <el-icon><Promotion /></el-icon>
            发布灵感
          </el-button>
        </div>
      </el-form>
    </div>

    <!-- 灵感提示 -->
    <div class="tips-card">
      <h4>💡 灵感提示</h4>
      <ul>
        <li>分享你的创意想法、读书笔记、生活感悟</li>
        <li>好的标题更容易引起共鸣</li>
        <li>内容越具体，越有分享价值</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Promotion } from '@element-plus/icons-vue'
import { publishInspiration } from '@/api/inspiration'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ title: '', content: '' })

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在2-100个字符之间', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' },
    { min: 10, max: 2000, message: '内容至少10个字符', trigger: 'blur' }
  ]
}

const submit = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await publishInspiration(form)
    ElMessage.success('发布成功！')
    router.push('/home')
  } catch {
    /* 错误已处理 */
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.publish-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px 20px 60px;
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 24px;
  align-items: start;
}

@media (max-width: 768px) {
  .publish-page {
    grid-template-columns: 1fr;
  }
}

/* ========== 主卡片 ========== */
.publish-card {
  background: #fff;
  border-radius: 20px;
  padding: 36px 32px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.04);
  border: 1px solid rgba(0,0,0,0.06);
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}
.header-icon {
  font-size: 48px;
  margin-bottom: 12px;
}
.page-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 6px;
}
.page-header p {
  font-size: 14px;
  color: var(--text-muted);
  margin: 0;
}

.title-input :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 2px #f0f0f5;
  border-radius: 12px;
  transition: all 0.3s;
}
.title-input :deep(.el-input__wrapper:hover),
.title-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--primary-light);
}

.content-input :deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 2px solid #f0f0f5;
  font-size: 15px;
  line-height: 1.8;
  resize: vertical;
  transition: all 0.3s;
  font-family: var(--font-sans);
}
.content-input :deep(.el-textarea__inner:hover),
.content-input :deep(.el-textarea__inner:focus) {
  border-color: var(--primary-light);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 8px;
}
.submit-btn {
  padding: 12px 32px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  transition: all 0.3s;
}
.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(102,126,234,0.35);
}

/* ========== 提示卡片 ========== */
.tips-card {
  background: linear-gradient(135deg, #ffecd2, #fcb69f);
  border-radius: 16px;
  padding: 24px;
  position: sticky;
  top: 84px;
}
.tips-card h4 {
  font-size: 16px;
  font-weight: 700;
  color: #8b5e3c;
  margin: 0 0 12px;
}
.tips-card ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.tips-card li {
  font-size: 13px;
  color: #8b5e3c;
  padding: 8px 0;
  border-bottom: 1px solid rgba(139,94,60,0.15);
  line-height: 1.6;
}
.tips-card li:last-child {
  border-bottom: none;
}
.tips-card li::before {
  content: '• ';
  color: #e8a87c;
  font-weight: bold;
}
</style>
