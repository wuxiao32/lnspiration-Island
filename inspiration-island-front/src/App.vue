<template>
  <div class="app-shell">
    <!-- 全局导航栏——登录页隐藏 -->
    <NavBar v-if="showNav" ref="navBarRef" />
    <!-- 页面内容 -->
    <main :class="{ 'no-nav': !showNav }">
      <router-view @login-success="onLoginSuccess" />
    </main>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from './components/NavBar.vue'

const route = useRoute()
const navBarRef = ref(null)

// 登录页不显示导航栏
const showNav = computed(() => route.path !== '/login')

// 登录成功后通知导航栏刷新
const onLoginSuccess = () => {
  if (navBarRef.value) {
    navBarRef.value.refresh()
  }
}
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
main {
  flex: 1;
}
main.no-nav {
  /* 登录页不需要顶部间距 */
}
</style>
