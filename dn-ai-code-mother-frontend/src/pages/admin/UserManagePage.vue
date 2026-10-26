<template>
  <div id="userManagePage">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.userName" placeholder="输入用户名" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-divider />
    <!-- 表格 -->
  </div>

  <a-table :columns="columns" :data-source="data" :pagination="pagination" @change="doTableChange">
    <template #headerCell="{ column }">
      <template v-if="column.key === 'name'">
        <span>
          <smile-outlined />
          Name
        </span>
      </template>
    </template>

    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'userAvatar'">
        <a-image :src="record.userAvatar" :width="35" />
      </template>
      <template v-else-if="column.dataIndex === 'userProfile'">
        <span class="user-profile-text" :title="record.userProfile">
          {{ record.userProfile }}
        </span>
      </template>
      <template v-else-if="column.dataIndex === 'userRole'">
        <div v-if="record.userRole === 'admin'">
          <a-tag color="green">管理员</a-tag>
        </div>
        <div v-else>
          <a-tag color="blue">普通用户</a-tag>
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'createTime'">
        {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
      </template>
      <template v-else-if="column.key === 'action'">
        <a-button danger @click="doDelete(record.id)">删除</a-button>
      </template>
    </template>
  </a-table>
</template>

<script lang="ts" setup>
import { SmileOutlined, DownOutlined } from '@ant-design/icons-vue'
import { deleteUser, listUserVoByPage } from '@/api/userController.ts'
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 数据
const data = ref<API.UserVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  const res = await listUserVoByPage({
    ...searchParams,
  })
  if (res.data.data) {
    data.value = res.data.data.records ?? []
    total.value = res.data.data.totalRow ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}

// 表格变化处理
const doTableChange = (page: any) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 获取数据
const doSearch = () => {
  // 重置页码
  searchParams.pageNum = 1
  fetchData()
}

// 删除数据
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deleteUser({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    await fetchData()
  } else {
    message.error('删除失败：' + res.data.message)
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* 搜索区域卡片 */
#userManagePage {
  padding: 16px 20px;
  background: #fff;
  border: 1px solid #ebedf0;
  border-radius: 12px;
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.03),
    0 12px 32px rgba(15, 23, 42, 0.06);
}

/* 搜索表单 */
#userManagePage :deep(.ant-form-inline) {
  row-gap: 12px;
}

#userManagePage :deep(.ant-form-item) {
  margin-bottom: 0;
}

#userManagePage :deep(.ant-form-item-label > label) {
  font-size: 14px;
  color: #1f2329;
}

#userManagePage :deep(.ant-input) {
  width: 200px;
  height: 36px;
  padding: 0 12px;
  font-size: 14px;
  border-radius: 8px;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
}

#userManagePage :deep(.ant-input:hover) {
  border-color: #4096ff;
}

#userManagePage :deep(.ant-input:focus) {
  border-color: #1677ff;
  box-shadow: 0 0 0 3px rgba(22, 119, 255, 0.1);
}

/* 搜索按钮 */
#userManagePage :deep(.ant-btn) {
  height: 36px;
  padding: 0 20px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
}

/* 隐藏搜索区与表格之间的默认分隔线，改用卡片间距区分 */
.ant-divider {
  display: none;
}

/* 表格卡片 */
.ant-table-wrapper {
  margin-top: 20px;
  padding: 20px 24px 8px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #ebedf0;
  border-radius: 12px;
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.03),
    0 12px 32px rgba(15, 23, 42, 0.06);
}

.ant-table-wrapper :deep(.ant-table) {
  font-size: 14px;
}

/* 表头 */
.ant-table-wrapper :deep(.ant-table-thead > tr > th) {
  padding: 12px 16px;
  font-size: 13px;
  font-weight: 600;
  color: #1f2329;
  background: #fafafa;
  border-bottom: 1px solid #ebedf0;
}

/* 表格内容 */
.ant-table-wrapper :deep(.ant-table-tbody > tr > td) {
  padding: 12px 16px;
  color: #1f2329;
  border-bottom: 1px solid #f0f0f0;
}

.ant-table-wrapper :deep(.ant-table-tbody > tr.ant-table-row:hover > td) {
  background: #f8fafc;
}

/* 简介过长时省略显示，悬停标题可查看完整内容 */
.user-profile-text {
  display: block;
  max-width: 220px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ant-table-wrapper :deep(.ant-table-placeholder > td) {
  border-bottom: 0;
}

.ant-table-wrapper :deep(.ant-table-placeholder .ant-empty-description) {
  color: #8c8c8c;
}

/* 头像预览 */
.ant-table-wrapper :deep(.ant-image-img) {
  object-fit: cover;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
}

/* 角色标签 */
.ant-table-wrapper :deep(.ant-tag) {
  padding: 1px 10px;
  font-size: 12px;
  line-height: 20px;
  border-radius: 4px;
}

/* 删除按钮 */
.ant-table-wrapper :deep(.ant-table .ant-btn) {
  height: 32px;
  padding: 0 14px;
  font-size: 13px;
  font-weight: 500;
  border-radius: 8px;
}

/* 分页 */
.ant-table-wrapper :deep(.ant-pagination) {
  margin: 20px 0 12px;
}

.ant-table-wrapper :deep(.ant-pagination .ant-pagination-item),
.ant-table-wrapper :deep(.ant-pagination .ant-pagination-prev),
.ant-table-wrapper :deep(.ant-pagination .ant-pagination-next) {
  border-radius: 8px;
}

.ant-table-wrapper :deep(.ant-pagination .ant-pagination-total-text) {
  font-size: 13px;
  color: #8c8c8c;
}

@media (max-width: 992px) {
  /* 小屏下表格横向滚动，避免列被挤压 */
  .ant-table-wrapper :deep(.ant-table-content) {
    overflow-x: auto;
  }

  .ant-table-wrapper :deep(.ant-table-content table) {
    min-width: 900px;
  }
}

@media (max-width: 768px) {
  #userManagePage {
    padding: 16px;
  }

  #userManagePage :deep(.ant-form-inline .ant-form-item) {
    width: 100%;
    margin-inline-end: 0;
    margin-bottom: 12px;
  }

  #userManagePage :deep(.ant-form-item-label) {
    flex: none;
  }

  #userManagePage :deep(.ant-input) {
    width: 100%;
  }

  #userManagePage :deep(.ant-btn) {
    width: 100%;
  }

  .ant-table-wrapper {
    padding: 16px 16px 4px;
    border-radius: 10px;
  }

  .ant-table-wrapper :deep(.ant-table-thead > tr > th),
  .ant-table-wrapper :deep(.ant-table-tbody > tr > td) {
    padding: 10px 12px;
  }
}

@media (max-width: 480px) {
  #userManagePage {
    padding: 14px 12px;
    border-radius: 10px;
  }

  .ant-table-wrapper {
    padding: 14px 12px 4px;
  }
}
</style>
