<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import zhCN from 'ant-design-vue/es/locale/zh_CN'

const loginUserStore = useLoginUserStore()
loginUserStore.fetchLoginUser()
</script>

<template>
  <a-config-provider :locale="zhCN">
    <BasicLayout />
  </a-config-provider>
</template>
