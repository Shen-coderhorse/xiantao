<template>
  <div class="tab-bar">
    <div
      v-for="tab in tabs"
      :key="tab.path"
      :class="['tab-item', { active: currentPath === tab.path }]"
      @click="navigateTo(tab.path)"
    >
      <div class="icon">{{ tab.icon }}</div>
      <div class="text">{{ tab.text }}</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TabBar',
  data() {
    return {
      tabs: [
        { path: '/index', icon: '🏠', text: '首页' },
        { path: '/orders', icon: '📦', text: '订单' },
        { path: '/publish', icon: '➕', text: '发布' },
        { path: '/profile', icon: '👤', text: '我的' }
      ]
    }
  },
  computed: {
    currentPath() {
      return this.$route.path
    }
  },
  methods: {
    navigateTo(path) {
      if (this.currentPath !== path) {
        this.$router.push(path)
      }
    }
  }
}
</script>

<style scoped>
.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: #fff;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: space-around;
  align-items: center;
  z-index: 1000;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 8px 12px;
}

.tab-item .icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.tab-item .text {
  font-size: 12px;
  color: #999;
}

.tab-item.active .text {
  color: #409eff;
}
</style>
