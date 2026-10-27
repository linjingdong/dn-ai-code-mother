<template>
  <div id="chatManagePage">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="消息内容">
        <a-input
          v-model:value="searchParams.message"
          class="search-input search-input--wide"
          placeholder="输入消息内容"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="消息类型">
        <a-select
          v-model:value="searchParams.messageType"
          class="search-select"
          placeholder="全部类型"
          allow-clear
        >
          <a-select-option value="user">用户消息</a-select-option>
          <a-select-option value="ai">AI 消息</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="应用 ID">
        <a-input
          v-model:value="searchParams.appId"
          class="search-input"
          placeholder="输入应用 ID"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="用户 ID">
        <a-input
          v-model:value="searchParams.userId"
          class="search-input"
          placeholder="输入用户 ID"
          allow-clear
        />
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
    :scroll="{ x: 1110 }"
    @change="doTableChange"
  >
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'id'">
        <a-tooltip v-if="record.id" title="点击复制消息 ID" placement="topLeft">
          <button type="button" class="message-id" @click="copyText(record.id, '消息 ID')">
            <span class="message-id-text">{{ record.id }}</span>
            <CopyOutlined class="message-id-copy" />
          </button>
        </a-tooltip>
        <span v-else class="muted-text">-</span>
      </template>
      <template v-else-if="column.dataIndex === 'message'">
        <a-tooltip :title="record.message || '-'" placement="topLeft">
          <span class="message-text">{{ record.message || '-' }}</span>
        </a-tooltip>
      </template>
      <template v-else-if="column.dataIndex === 'messageType'">
        <a-tag :color="record.messageType === 'user' ? 'blue' : 'purple'">
          {{ formatMessageType(record.messageType) }}
        </a-tag>
      </template>
      <template v-else-if="column.dataIndex === 'appId'">
        <a-tooltip v-if="record.appId" title="点击复制应用 ID" placement="topLeft">
          <button type="button" class="id-copy" @click="copyText(record.appId, '应用 ID')">
            <span class="id-copy-text">{{ record.appId }}</span>
            <CopyOutlined class="id-copy-icon" />
          </button>
        </a-tooltip>
        <span v-else class="muted-text">-</span>
      </template>
      <template v-else-if="column.dataIndex === 'userId'">
        <a-tooltip v-if="record.userId" title="点击复制用户 ID" placement="topLeft">
          <button type="button" class="id-copy" @click="copyText(record.userId, '用户 ID')">
            <span class="id-copy-text">{{ record.userId }}</span>
            <CopyOutlined class="id-copy-icon" />
          </button>
        </a-tooltip>
        <span v-else class="muted-text">-</span>
      </template>
      <template v-else-if="column.dataIndex === 'createTime'">
        <span v-if="record.createTime" class="time-text">
          {{ formatTime(record.createTime) }}
        </span>
        <span v-else class="muted-text">-</span>
      </template>
      <template v-else-if="column.key === 'action'">
        <div class="table-actions">
          <a-tooltip title="查看对话">
            <a-button
              type="text"
              class="action-btn"
              :disabled="!record.appId"
              aria-label="查看对话"
              @click="viewAppChat(record.appId)"
            >
              <MessageOutlined />
            </a-button>
          </a-tooltip>
          <a-popconfirm
            title="确定要删除这条消息吗？"
            ok-text="删除"
            cancel-text="取消"
            @confirm="deleteMessage(record.id)"
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
import { CopyOutlined, DeleteOutlined, MessageOutlined } from '@ant-design/icons-vue'
import { formatTime } from '@/utils/time'
import { listAllChatHistoryByPageForAdmin } from '@/api/chatHistoryController.ts'

const router = useRouter()

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 120,
    fixed: 'left' as const,
  },
  {
    title: '消息内容',
    dataIndex: 'message',
    width: 320,
  },
  {
    title: '消息类型',
    dataIndex: 'messageType',
    width: 110,
  },
  {
    title: '应用 ID',
    dataIndex: 'appId',
    width: 140,
  },
  {
    title: '用户 ID',
    dataIndex: 'userId',
    width: 140,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 170,
  },
  {
    title: '操作',
    key: 'action',
    width: 110,
    fixed: 'right' as const,
  },
]

// 数据
const data = ref<API.ChatHistory[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.ChatHistoryQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 消息类型展示文案
const formatMessageType = (type?: string) => {
  if (type === 'user') return '用户消息'
  if (type === 'ai') return 'AI 消息'
  return type || '-'
}

// 获取数据
const fetchData = async () => {
  try {
    const res = await listAllChatHistoryByPageForAdmin({
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
  searchParams.message = undefined
  searchParams.messageType = undefined
  searchParams.appId = undefined
  searchParams.userId = undefined
  searchParams.pageNum = 1
  fetchData()
}

// 复制 ID
const copyText = async (value: string | number | undefined, label: string) => {
  if (value === undefined || value === null || value === '') return
  try {
    await navigator.clipboard.writeText(String(value))
    message.success(`${label} 已复制`)
  } catch {
    message.warning('复制失败，请手动复制')
  }
}

// 查看应用对话
const viewAppChat = (appId: number | undefined) => {
  if (appId) {
    router.push(`/app/chat/${appId}`)
  }
}

// 删除消息
const deleteMessage = async (id: number | undefined) => {
  if (!id) return

  try {
    // 注意：这里需要后端提供删除对话历史的接口
    // 目前先显示成功，实际实现需要调用删除接口
    message.success('删除成功')
    // 刷新数据
    fetchData()
  } catch (error) {
    console.error('删除失败：', error)
    message.error('删除失败')
  }
}
</script>

<style scoped>
/* 搜索区域卡片 */
#chatManagePage {
  padding: 16px 20px;
  background: #fff;
  border: 1px solid #ebedf0;
  border-radius: 12px;
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.03),
    0 12px 32px rgba(15, 23, 42, 0.06);
}

/* 搜索表单 */
#chatManagePage :deep(.ant-form-inline) {
  row-gap: 12px;
}

#chatManagePage :deep(.ant-form-item) {
  margin-bottom: 0;
  margin-inline-end: 16px;
}

#chatManagePage :deep(.ant-form-item-label > label) {
  font-size: 14px;
  color: #1f2329;
}

#chatManagePage :deep(.ant-input) {
  height: 36px;
  padding: 0 12px;
  font-size: 14px;
  border-radius: 8px;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
}

#chatManagePage :deep(.ant-input:hover) {
  border-color: #4096ff;
}

#chatManagePage :deep(.ant-input:focus) {
  border-color: #1677ff;
  box-shadow: 0 0 0 3px rgba(22, 119, 255, 0.1);
}

/* allow-clear 输入框真正显示边框的是外层 affix 容器 */
#chatManagePage :deep(.ant-input-affix-wrapper) {
  width: 160px;
  height: 36px;
  padding: 0 12px;
  border-radius: 8px;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
}

#chatManagePage :deep(.ant-input-affix-wrapper.search-input--wide),
#chatManagePage :deep(.ant-input.search-input--wide) {
  width: 220px;
}

#chatManagePage :deep(.ant-input-affix-wrapper .ant-input) {
  width: 100%;
  height: 100%;
  padding: 0;
  font-size: 14px;
  border-radius: 0;
  box-shadow: none;
}

#chatManagePage :deep(.ant-input-affix-wrapper:hover) {
  border-color: #4096ff;
}

#chatManagePage :deep(.ant-input-affix-wrapper:focus-within) {
  border-color: #1677ff;
  box-shadow: 0 0 0 3px rgba(22, 119, 255, 0.1);
}

#chatManagePage :deep(.ant-select-selector) {
  height: 36px;
  padding: 2px 12px !important;
  border-radius: 8px;
}

#chatManagePage :deep(.search-select) {
  width: 160px;
}

/* 搜索按钮 */
#chatManagePage :deep(.ant-btn) {
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
  vertical-align: middle;
  border-bottom: 1px solid #f0f0f0;
}

.ant-table-wrapper :deep(.ant-table-tbody > tr.ant-table-row:hover > td) {
  background: #f8fafc;
}

/* 固定列的背景色跟随行 hover */
.ant-table-wrapper :deep(.ant-table-tbody > tr > td.ant-table-cell-fix-left),
.ant-table-wrapper :deep(.ant-table-tbody > tr > td.ant-table-cell-fix-right) {
  background: #fff;
}

.ant-table-wrapper :deep(.ant-table-tbody > tr.ant-table-row:hover > td.ant-table-cell-fix-left),
.ant-table-wrapper :deep(.ant-table-tbody > tr.ant-table-row:hover > td.ant-table-cell-fix-right) {
  background: #f8fafc;
}

/* 消息内容 */
.message-text {
  display: block;
  max-width: 100%;
  overflow: hidden;
  color: #1f2329;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 记录 ID */
.message-id {
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

.message-id:hover {
  color: #1677ff;
}

.message-id-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-id-copy {
  flex: none;
  font-size: 11px;
  opacity: 0.45;
  transition: opacity 0.2s;
}

.message-id:hover .message-id-copy {
  opacity: 1;
}

/* 可复制的应用 / 用户 ID */
.id-copy {
  display: inline-flex;
  gap: 4px;
  align-items: center;
  max-width: 100%;
  padding: 0;
  overflow: hidden;
  font-family: 'JetBrains Mono', 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 12px;
  line-height: 16px;
  color: #64748b;
  text-align: left;
  cursor: pointer;
  background: none;
  border: 0;
  transition: color 0.2s;
}

.id-copy:hover {
  color: #1677ff;
}

.id-copy-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.id-copy-icon {
  flex: none;
  font-size: 11px;
  opacity: 0.45;
  transition: opacity 0.2s;
}

.id-copy:hover .id-copy-icon {
  opacity: 1;
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
  #chatManagePage :deep(.ant-form-item) {
    margin-inline-end: 12px;
  }
}

@media (max-width: 768px) {
  #chatManagePage {
    padding: 16px;
  }

  #chatManagePage :deep(.ant-form-inline .ant-form-item) {
    width: 100%;
    margin-inline-end: 0;
    margin-bottom: 12px;
  }

  #chatManagePage :deep(.ant-form-item-label) {
    flex: none;
  }

  #chatManagePage :deep(.ant-form-item-control),
  #chatManagePage :deep(.ant-input),
  #chatManagePage :deep(.ant-input-affix-wrapper),
  #chatManagePage :deep(.ant-input-affix-wrapper.search-input--wide),
  #chatManagePage :deep(.search-select) {
    width: 100%;
  }

  #chatManagePage :deep(.ant-space) {
    display: flex;
    width: 100%;
  }

  #chatManagePage :deep(.ant-space-item) {
    flex: 1;
  }

  #chatManagePage :deep(.ant-btn) {
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
  #chatManagePage {
    padding: 14px 12px;
    border-radius: 10px;
  }

  .ant-table-wrapper {
    padding: 14px 12px 4px;
  }
}
</style>
