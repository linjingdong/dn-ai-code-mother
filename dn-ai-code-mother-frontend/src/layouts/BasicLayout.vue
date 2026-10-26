<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'

const route = useRoute()
// 首页、对话页需要铺满整个视口宽度
const isFullBleed = computed(() => route.meta.fullBleed === true)
// 对话页固定占满一屏，不展示页脚
const showFooter = computed(() => route.meta.hideFooter !== true)
</script>

<template>
  <a-layout class="basic-layout">
    <!-- 顶部导航栏 -->
    <GlobalHeader />
    <!-- 主要内容区域 -->
    <a-layout-content
      :class="['basic-layout__content', { 'basic-layout__content--full': isFullBleed }]"
    >
      <div :class="['basic-layout__container', { 'basic-layout__container--full': isFullBleed }]">
        <router-view />
      </div>
    </a-layout-content>
    <!-- 底部版权信息 -->
    <GlobalFooter v-if="showFooter" />
  </a-layout>
</template>

<style scoped>
.basic-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #f5f7fa;
}

.basic-layout__content {
  display: flex;
  flex: 1;
  flex-direction: column;
  padding: 24px;
  min-height: 0;
}

.basic-layout__container {
  display: flex;
  flex: 1;
  flex-direction: column;
  width: 100%;
  max-width: 1400px;
  min-height: 0;
  margin: 0 auto;
}

/* 首页、对话页铺满整个宽度 */
.basic-layout__content--full {
  padding: 0;
}

.basic-layout__container--full {
  max-width: none;
}

@media (max-width: 768px) {
  .basic-layout__content:not(.basic-layout__content--full) {
    padding: 16px;
  }
}
</style>
