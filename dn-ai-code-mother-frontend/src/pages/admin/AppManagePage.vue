<template>
  <div id="appManagePage">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="应用名称">
        <a-input v-model:value="searchParams.appName" placeholder="输入应用名称" allow-clear />
      </a-form-item>
      <a-form-item label="创建者">
        <a-input v-model:value="searchParams.userId" placeholder="输入用户 ID" allow-clear />
      </a-form-item>
      <a-form-item label="生成类型">
        <a-select
          v-model:value="searchParams.codeGenType"
          class="search-select"
          placeholder="全部类型"
          allow-clear
        >
          <a-select-option
            v-for="option in CODE_GEN_TYPE_OPTIONS"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-space>
          <a-button type="primary" html-type="submit">搜索</a-button>
          <a-button html-type="button" @click="doReset">重置</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </div>

  <!-- 表格 -->
  <a-table
    row-key="id"
    :columns="columns"
    :data-source="data"
    :pagination="pagination"
    :scroll="{ x: 1290 }"
    @change="doTableChange"
  >
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'appName'">
        <div class="app-cell">
          <span class="app-name-text" :title="record.appName">{{ record.appName || '-' }}</span>
          <a-tooltip v-if="record.id" title="点击复制应用 ID" placement="topLeft">
            <button type="button" class="app-id" @click="copyAppId(record.id)">
              <span class="app-id-text">{{ record.id }}</span>
              <CopyOutlined class="app-id-copy" />
            </button>
          </a-tooltip>
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'cover'">
        <a-image
          v-if="record.cover"
          :src="record.cover"
          :width="72"
          :height="52"
          class="cover-image"
        />
        <div v-else class="no-cover">
          <PictureOutlined />
          <span>无封面</span>
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'initPrompt'">
        <a-tooltip :title="record.initPrompt || '-'" placement="topLeft">
          <span class="prompt-text">{{ record.initPrompt || '-' }}</span>
        </a-tooltip>
      </template>
      <template v-else-if="column.dataIndex === 'codeGenType'">
        <a-tag :color="record.codeGenType === 'html' ? 'purple' : 'blue'">
          {{ formatCodeGenType(record.codeGenType) }}
        </a-tag>
      </template>
      <template v-else-if="column.dataIndex === 'priority'">
        <a-tag v-if="isGoodApp(record)" color="gold">精选</a-tag>
        <span v-else class="muted-text">普通</span>
      </template>
      <template v-else-if="column.dataIndex === 'deployedTime'">
        <span v-if="record.deployedTime" class="time-text">
          {{ formatTime(record.deployedTime) }}
        </span>
        <span v-else class="muted-text">未部署</span>
      </template>
      <template v-else-if="column.dataIndex === 'user'">
        <UserInfo v-if="record.user" :user="record.user" size="small" />
        <span v-else class="muted-text">-</span>
      </template>
      <template v-else-if="column.dataIndex === 'createTime'">
        <span class="time-text">{{ formatTime(record.createTime) }}</span>
      </template>
      <template v-else-if="column.key === 'action'">
        <div class="table-actions">
          <a-tooltip title="编辑">
            <a-button type="text" class="action-btn" aria-label="编辑" @click="editApp(record)">
              <EditOutlined />
            </a-button>
          </a-tooltip>
          <a-tooltip :title="isGoodApp(record) ? '取消精选' : '设为精选'">
            <a-button
              type="text"
              class="action-btn feature-btn"
              :class="{ 'is-featured': isGoodApp(record) }"
              :loading="togglingId === String(record.id)"
              aria-label="切换精选"
              @click="toggleFeatured(record)"
            >
              <StarFilled v-if="isGoodApp(record)" />
              <StarOutlined v-else />
            </a-button>
          </a-tooltip>
          <a-popconfirm
            title="确定要删除这个应用吗？"
            ok-text="删除"
            cancel-text="取消"
            @confirm="deleteApp(record.id)"
          >
            <a-tooltip title="删除">
              <a-button type="text" danger class="action-btn" aria-label="删除">
                <DeleteOutlined />
              </a-button>
            </a-tooltip>
          </a-popconfirm>
        </div>
      </template>
    </template>
  </a-table>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  CopyOutlined,
  DeleteOutlined,
  EditOutlined,
  PictureOutlined,
  StarFilled,
  StarOutlined,
} from '@ant-design/icons-vue'
import { listAppVoByPageByAdmin, deleteAppByAdmin, updateAppByAdmin } from '@/api/appController'
import { CODE_GEN_TYPE_OPTIONS, formatCodeGenType } from '@/utils/codeGenTypes'
import { formatTime } from '@/utils/time'
import UserInfo from '@/components/UserInfo.vue'

const router = useRouter()
const GOOD_APP_PRIORITY = 99

const columns = [
  {
    title: '应用名称',
    dataIndex: 'appName',
    width: 190,
  },
  {
    title: '封面',
    dataIndex: 'cover',
    width: 104,
  },
  {
    title: '初始提示词',
    dataIndex: 'initPrompt',
    width: 190,
  },
  {
    title: '生成类型',
    dataIndex: 'codeGenType',
    width: 130,
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    width: 88,
  },
  {
    title: '部署时间',
    dataIndex: 'deployedTime',
    width: 170,
  },
  {
    title: '创建者',
    dataIndex: 'user',
    width: 120,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 170,
  },
  {
    title: '操作',
    key: 'action',
    width: 130,
    fixed: 'right' as const,
  },
]

// 数据
const data = ref<API.AppVO[]>([])
const total = ref(0)
const togglingId = ref('')

// 搜索条件
const searchParams = reactive<API.AppQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

const isGoodApp = (app: API.AppVO) => app.priority === GOOD_APP_PRIORITY

// 获取数据
const fetchData = async () => {
  try {
    const res = await listAppVoByPageByAdmin({
      ...searchParams,
    })
    if (res.data.data) {
      data.value = res.data.data.records ?? []
      total.value = res.data.data.totalRow ?? 0
    } else {
      message.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败：', error)
    message.error('获取数据失败')
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})

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

// 表格变化处理
const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索
const doSearch = () => {
  // 重置页码
  searchParams.pageNum = 1
  fetchData()
}

// 重置搜索条件
const doReset = () => {
  searchParams.appName = undefined
  searchParams.userId = undefined
  searchParams.codeGenType = undefined
  searchParams.pageNum = 1
  fetchData()
}

// 复制应用 ID
const copyAppId = async (id?: string | number) => {
  if (!id) return
  try {
    await navigator.clipboard.writeText(String(id))
    message.success('应用 ID 已复制')
  } catch {
    message.warning('复制失败，请手动复制')
  }
}

// 编辑应用
const editApp = (app: API.AppVO) => {
  router.push(`/app/edit/${app.id}`)
}

// 切换精选状态
const toggleFeatured = async (app: API.AppVO) => {
  if (!app.id) return

  const newPriority = isGoodApp(app) ? 0 : GOOD_APP_PRIORITY
  togglingId.value = String(app.id)

  try {
    const res = await updateAppByAdmin({
      id: app.id,
      priority: newPriority,
    })

    if (res.data.code === 0) {
      message.success(newPriority === GOOD_APP_PRIORITY ? '已设为精选' : '已取消精选')
      // 刷新数据
      await fetchData()
    } else {
      message.error('操作失败：' + res.data.message)
    }
  } catch (error) {
    console.error('操作失败：', error)
    message.error('操作失败')
  } finally {
    togglingId.value = ''
  }
}

// 删除应用
const deleteApp = async (id: number | undefined) => {
  if (!id) return

  try {
    const res = await deleteAppByAdmin({ id })
    if (res.data.code === 0) {
      message.success('删除成功')
      // 刷新数据
      await fetchData()
    } else {
      message.error('删除失败：' + res.data.message)
    }
  } catch (error) {
    console.error('删除失败：', error)
    message.error('删除失败')
  }
}
</script>

<style scoped>
/* 搜索区域卡片 */
#appManagePage {
  padding: 16px 20px;
  background: #fff;
  border: 1px solid #ebedf0;
  border-radius: 12px;
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.03),
    0 12px 32px rgba(15, 23, 42, 0.06);
}

/* 搜索表单 */
#appManagePage :deep(.ant-form-inline) {
  row-gap: 12px;
}

#appManagePage :deep(.ant-form-item) {
  margin-bottom: 0;
  margin-inline-end: 16px;
}

#appManagePage :deep(.ant-form-item-label > label) {
  font-size: 14px;
  color: #1f2329;
}

#appManagePage :deep(.ant-input) {
  width: 200px;
  height: 36px;
  padding: 0 12px;
  font-size: 14px;
  border-radius: 8px;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
}

#appManagePage :deep(.ant-input:hover) {
  border-color: #4096ff;
}

#appManagePage :deep(.ant-input:focus) {
  border-color: #1677ff;
  box-shadow: 0 0 0 3px rgba(22, 119, 255, 0.1);
}

/* allow-clear 输入框真正显示边框的是外层 affix 容器 */
#appManagePage :deep(.ant-input-affix-wrapper) {
  width: 200px;
  height: 36px;
  padding: 0 12px;
  border-radius: 8px;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
}

#appManagePage :deep(.ant-input-affix-wrapper .ant-input) {
  width: 100%;
  height: 100%;
  padding: 0;
  font-size: 14px;
  border-radius: 0;
  box-shadow: none;
}

#appManagePage :deep(.ant-input-affix-wrapper:hover) {
  border-color: #4096ff;
}

#appManagePage :deep(.ant-input-affix-wrapper:focus-within) {
  border-color: #1677ff;
  box-shadow: 0 0 0 3px rgba(22, 119, 255, 0.1);
}

#appManagePage :deep(.ant-select-selector) {
  height: 36px;
  padding: 2px 12px !important;
  border-radius: 8px;
}

#appManagePage :deep(.search-select) {
  width: 160px;
}

/* 搜索按钮 */
#appManagePage :deep(.ant-btn) {
  height: 36px;
  padding: 0 20px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
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
  white-space: nowrap;
  background: #fafafa;
  border-bottom: 1px solid #ebedf0;
}

/* 表格内容 */
.ant-table-wrapper :deep(.ant-table-tbody > tr > td) {
  padding: 14px 16px;
  color: #1f2329;
  border-bottom: 1px solid #f0f0f0;
}

.ant-table-wrapper :deep(.ant-table-tbody > tr.ant-table-row:hover > td) {
  background: #f8fafc;
}

/* 固定操作列的背景色跟随行 hover */
.ant-table-wrapper :deep(.ant-table-tbody > tr > td.ant-table-cell-fix-right) {
  background: #fff;
}

.ant-table-wrapper
  :deep(.ant-table-tbody > tr.ant-table-row:hover > td.ant-table-cell-fix-right) {
  background: #f8fafc;
}

/* 应用名称与 ID */
.app-cell {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.app-name-text {
  overflow: hidden;
  font-weight: 500;
  color: #1f2329;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.app-id {
  display: inline-flex;
  gap: 4px;
  align-items: center;
  max-width: 100%;
  padding: 0;
  overflow: hidden;
  font-family: 'JetBrains Mono', 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 11px;
  line-height: 16px;
  color: #94a3b8;
  text-align: left;
  cursor: pointer;
  background: none;
  border: 0;
  transition: color 0.2s;
}

.app-id:hover {
  color: #1677ff;
}

.app-id-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.app-id-copy {
  flex: none;
  font-size: 11px;
  opacity: 0.45;
  transition: opacity 0.2s;
}

.app-id:hover .app-id-copy {
  opacity: 1;
}

/* 封面 */
.cover-image :deep(.ant-image-img) {
  width: 72px;
  height: 52px;
  object-fit: cover;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
}

.no-cover {
  display: flex;
  flex-direction: column;
  gap: 2px;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 52px;
  font-size: 11px;
  color: #b0b7c3;
  background: #f8fafc;
  border: 1px dashed #e2e8f0;
  border-radius: 8px;
}

/* 提示词 */
.prompt-text {
  display: block;
  max-width: 100%;
  overflow: hidden;
  color: #64748b;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 时间与次要文字 */
.time-text {
  font-size: 13px;
  color: #64748b;
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
}

.muted-text {
  font-size: 13px;
  color: #b0b7c3;
  white-space: nowrap;
}

/* 标签 */
.ant-table-wrapper :deep(.ant-tag) {
  margin-inline-end: 0;
  padding: 1px 8px;
  font-size: 12px;
  line-height: 20px;
  white-space: nowrap;
  border-radius: 4px;
}

/* 操作按钮 */
.table-actions {
  display: flex;
  gap: 4px;
  align-items: center;
  justify-content: flex-end;
  white-space: nowrap;
}

.ant-table-wrapper :deep(.table-actions .ant-btn) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  padding: 0;
  font-size: 15px;
  color: #64748b;
  border-radius: 7px;
}

.ant-table-wrapper :deep(.table-actions .ant-btn:hover) {
  color: #1677ff;
  background: #f0f7ff;
}

.ant-table-wrapper :deep(.table-actions .ant-btn.ant-btn-dangerous) {
  color: #ff4d4f;
}

.ant-table-wrapper :deep(.table-actions .ant-btn.ant-btn-dangerous:hover) {
  color: #ff4d4f;
  background: #fff1f0;
}

.ant-table-wrapper :deep(.table-actions .feature-btn.is-featured) {
  color: #faad14;
  background: #fffbe6;
}

.ant-table-wrapper :deep(.table-actions .feature-btn.is-featured:hover) {
  color: #d48806;
  background: #fff7cc;
}

.ant-table-wrapper :deep(.ant-table-placeholder > td) {
  border-bottom: 0;
}

.ant-table-wrapper :deep(.ant-table-placeholder .ant-empty-description) {
  color: #8c8c8c;
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
  #appManagePage :deep(.ant-form-item) {
    margin-inline-end: 12px;
  }
}

@media (max-width: 768px) {
  #appManagePage {
    padding: 16px;
  }

  #appManagePage :deep(.ant-form-inline .ant-form-item) {
    width: 100%;
    margin-inline-end: 0;
    margin-bottom: 12px;
  }

  #appManagePage :deep(.ant-form-item-label) {
    flex: none;
  }

  #appManagePage :deep(.ant-form-item-control),
  #appManagePage :deep(.ant-input),
  #appManagePage :deep(.ant-input-affix-wrapper),
  #appManagePage :deep(.search-select) {
    width: 100%;
  }

  #appManagePage :deep(.ant-space) {
    display: flex;
    width: 100%;
  }

  #appManagePage :deep(.ant-space-item) {
    flex: 1;
  }

  #appManagePage :deep(.ant-btn) {
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
  #appManagePage {
    padding: 14px 12px;
    border-radius: 10px;
  }

  .ant-table-wrapper {
    padding: 14px 12px 4px;
  }
}
</style>
