<template>
  <a-layout-header class="global-header">
    <div class="global-header__inner">
      <!-- 左侧：Logo和标题 -->
      <RouterLink to="/" class="global-header__brand">
        <img class="global-header__logo" src="@/assets/logo.png" alt="Logo" />
        <h1 class="global-header__title">豆泥搭搭</h1>
      </RouterLink>

      <!-- 中间：导航菜单 -->
      <a-menu
        v-if="!isMobile"
        class="global-header__menu"
        mode="horizontal"
        :items="menuItems"
        :selected-keys="selectedKeys"
        @click="handleMenuClick"
      />

      <!-- 右侧：用户操作区域 -->
      <div class="global-header__actions">
        <div v-if="loginUserStore.loginUser.id">
          <a-dropdown>
            <a-space>
              <a-avatar :src="loginUserStore.loginUser.userAvatar" />
              {{ loginUserStore.loginUser.userName ?? '无名' }}
            </a-space>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="doLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>

        <div v-else>
          <a-button type="primary" shape="round" href="/user/login">登录</a-button>
        </div>
        <a-button
          v-if="isMobile"
          class="global-header__trigger"
          type="text"
          aria-label="打开导航菜单"
          @click="drawerOpen = true"
        >
          ☰
        </a-button>
      </div>
    </div>

    <!-- 移动端导航菜单 -->
    <a-drawer v-model:open="drawerOpen" title="导航菜单" placement="right" :width="240">
      <a-menu
        class="global-header__drawer-menu"
        mode="inline"
        :items="menuItems"
        :selected-keys="selectedKeys"
        @click="handleMenuClick"
      />
    </a-drawer>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Grid, message } from 'ant-design-vue'
import type { MenuProps } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { LogoutOutlined } from '@ant-design/icons-vue'
import { userLogout } from '@/api/userController.ts'
import { HomeOutlined } from '@ant-design/icons-vue'
import ChatManagePage from '@/pages/admin/ChatManagePage.vue'

const loginUserStore = useLoginUserStore()
const router = useRouter()
const screens = Grid.useBreakpoint()
const isMobile = computed(() => !screens.value.md)
const drawerOpen = ref(false)

// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
// 监听路由变化，更新当前选中菜单
router.afterEach((to) => {
  selectedKeys.value = [to.path]
})

// 菜单配置项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/admin/appManage',
    label: '应用管理',
    title: '应用管理',
  },
  {
    key: '/admin/chatManage',
    label: '对话管理',
    title: '对话管理',
  },
  {
    key: '/about',
    label: h('a', { href: 'https://github.com/linjingdong', target: '_blank' }, '关于'),
    title: '关于我们',
  },
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))

// 处理菜单点击
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  drawerOpen.value = false
  // 跳转到对应页面
  if (key.startsWith('/')) {
    router.push(key)
  }
}

const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
.global-header {
  position: sticky;
  top: 0;
  z-index: 100;
  height: 64px;
  padding: 0 24px;
  line-height: 64px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.global-header__inner {
  display: flex;
  align-items: center;
  gap: 24px;
  width: 100%;
  height: 100%;
  margin: 0 auto;
}

.global-header__brand {
  display: flex;
  flex-shrink: 0;
  gap: 10px;
  align-items: center;
  color: rgba(0, 0, 0, 0.88);
  text-decoration: none;
}

.global-header__logo {
  width: 40px;
  height: 40px;
  object-fit: contain;
  border-radius: 8px;
}

.global-header__title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.2;
  white-space: nowrap;
  transition: color 0.2s;
}

.global-header__brand:hover .global-header__title {
  color: #1677ff;
}

.global-header__menu {
  flex: 1;
  min-width: 0;
  line-height: 64px;
  background: transparent;
  border-bottom: none;
}

.global-header__actions {
  display: flex;
  flex-shrink: 0;
  gap: 8px;
  align-items: center;
  margin-left: auto;
}

.global-header__trigger {
  font-size: 18px;
  line-height: 1;
}

.global-header__drawer-menu {
  border-inline-end: none;
}

@media (max-width: 768px) {
  .global-header {
    padding: 0 16px;
  }

  .global-header__inner {
    gap: 12px;
  }

  .global-header__logo {
    width: 34px;
    height: 34px;
  }
}

@media (max-width: 480px) {
  .global-header__title {
    display: none;
  }
}
</style>
