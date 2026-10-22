<script setup lang="ts">
import GlobalFooter from '@/components/GlobalFooter.vue'
import GlobalHeader from '@/components/GlobalHeader.vue'
</script>

<template>
  <a-layout class="basic-layout">
    <!-- 顶部导航栏 -->
    <GlobalHeader />

    <!-- 主要内容区域 -->
    <a-layout-content class="basic-layout__content">
      <div class="basic-layout__container">
        <router-view />
      </div>
    </a-layout-content>

    <!-- 底部版权信息 -->
    <GlobalFooter />
  </a-layout>
</template>

<style scoped>
.basic-layout {
  min-height: 100vh;
  background: #f5f7fa;
}

.basic-layout__content {
  flex: 1;
  padding: 24px;
}

.basic-layout__container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
}

@media (max-width: 768px) {
  .basic-layout__content {
    padding: 16px;
  }
}
</style>
