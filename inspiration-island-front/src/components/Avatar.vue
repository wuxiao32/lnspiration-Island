<template>
  <div
    class="avatar"
    :style="avatarStyle"
    :title="username"
  >
    <span class="avatar-text">{{ initial }}</span>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  username: { type: String, required: true },
  size: { type: Number, default: 40 }
})

// 根据用户名生成固定的颜色
const avatarColor = computed(() => {
  const colors = [
    ['#667eea', '#764ba2'],
    ['#f093fb', '#f5576c'],
    ['#4facfe', '#00f2fe'],
    ['#43e97b', '#38f9d7'],
    ['#fa709a', '#fee140'],
    ['#a18cd1', '#fbc2eb'],
    ['#fad0c4', '#ffd1ff'],
    ['#ffecd2', '#fcb69f'],
    ['#ff9a9e', '#fecfef'],
    ['#a1c4fd', '#c2e9fb'],
    ['#d4fc79', '#96e6a1'],
    ['#84fab0', '#8fd3f4']
  ]
  let hash = 0
  for (let i = 0; i < props.username.length; i++) {
    hash = props.username.charCodeAt(i) + ((hash << 5) - hash)
  }
  const idx = Math.abs(hash) % colors.length
  return colors[idx]
})

const initial = computed(() => {
  return props.username.charAt(0).toUpperCase()
})

const avatarStyle = computed(() => ({
  width: props.size + 'px',
  height: props.size + 'px',
  background: `linear-gradient(135deg, ${avatarColor.value[0]}, ${avatarColor.value[1]})`,
  fontSize: (props.size * 0.45) + 'px'
}))
</script>

<style scoped>
.avatar {
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  flex-shrink: 0;
  user-select: none;
}
.avatar:hover {
  transform: scale(1.08);
  box-shadow: 0 4px 15px rgba(0,0,0,0.2);
}
.avatar-text {
  color: #fff;
  font-weight: 700;
  font-family: 'Segoe UI', system-ui, sans-serif;
}
</style>
