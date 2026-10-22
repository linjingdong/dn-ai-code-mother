<template>
  <div id="userLoginPage">
    <h2 class="title">豆泥 AI 应用生成 - 用户登录</h2>
    <div class="desc">不写一行代码，生成完整应用</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
      </a-form-item>
      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, message: '密码不能小于 8 位' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>
      <div class="tips">
        没有账号？
        <RouterLink to="/user/register">去注册</RouterLink>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">登录</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { useRouter } from 'vue-router'
import { userLogin } from '@/api/userController.ts'
import { message } from 'ant-design-vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const res = await userLogin(values)
  if (res.data.code === 0 && res.data.data) {
    await loginUserStore.fetchLoginUser()
    message.success('登录成功')
    router.push({
      path: '/',
      replace: true,
    })
  } else {
    message.error('登录失败' + res.data.message)
  }
}
</script>

<style scoped>
#userLoginPage {
  max-width: 400px;
  /* 让约 368px 高的卡片在内容区内垂直居中 */
  margin: max(24px, calc(50vh - 258px)) auto 40px;
  padding: 40px 36px 32px;
  background: #fff;
  border: 1px solid #ebedf0;
  border-radius: 12px;
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.03),
    0 12px 32px rgba(15, 23, 42, 0.06);
}

.title {
  margin: 0 0 8px;
  font-size: 22px;
  font-weight: 600;
  line-height: 1.4;
  color: #1f2329;
  text-align: center;
  letter-spacing: 0.2px;
  text-wrap: balance;
}

.desc {
  margin: 0 0 32px;
  font-size: 13px;
  color: #8c8c8c;
  text-align: center;
}

/* 表单项间距 */
#userLoginPage :deep(.ant-form-item) {
  margin-bottom: 18px;
}

#userLoginPage :deep(.ant-form-item:last-child) {
  margin-bottom: 0;
}

#userLoginPage :deep(.ant-form-item-explain-error) {
  margin-top: 4px;
  font-size: 12px;
}

/* 输入框：账号框与密码框统一高度 */
#userLoginPage :deep(.ant-input),
#userLoginPage :deep(.ant-input-affix-wrapper) {
  height: 44px;
  padding: 0 12px;
  font-size: 14px;
  border-radius: 8px;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
}

#userLoginPage :deep(.ant-input:hover),
#userLoginPage :deep(.ant-input-affix-wrapper:hover) {
  border-color: #4096ff;
}

#userLoginPage :deep(.ant-input:focus),
#userLoginPage :deep(.ant-input-affix-wrapper-focused) {
  border-color: #1677ff;
  box-shadow: 0 0 0 3px rgba(22, 119, 255, 0.1);
}

/* 重置密码框内部 input，避免双层 padding 导致高度不等 */
#userLoginPage :deep(.ant-input-affix-wrapper > input.ant-input) {
  height: 100%;
  padding: 0;
  background: transparent;
  border: 0;
  border-radius: 0;
  box-shadow: none;
}

/* 校验失败状态 */
#userLoginPage :deep(.ant-input-status-error),
#userLoginPage :deep(.ant-input-affix-wrapper-status-error) {
  border-color: #ff4d4f;
}

#userLoginPage :deep(.ant-input-status-error:focus),
#userLoginPage :deep(.ant-input-affix-wrapper-status-error.ant-input-affix-wrapper-focused) {
  border-color: #ff4d4f;
  box-shadow: 0 0 0 3px rgba(255, 77, 79, 0.1);
}

/* 登录按钮 */
#userLoginPage :deep(.ant-btn) {
  height: 44px;
  font-size: 15px;
  font-weight: 500;
  border-radius: 8px;
}

/* 去注册 */
.tips {
  margin: 0 0 20px;
  font-size: 13px;
  color: #8c8c8c;
  text-align: right;
}

.tips a {
  font-weight: 500;
  color: #1677ff;
  text-decoration: none;
  transition: color 0.2s;
}

.tips a:hover {
  color: #4096ff;
  text-decoration: underline;
}

@media (max-width: 768px) {
  #userLoginPage {
    margin: 24px auto;
    padding: 32px 24px 24px;
  }

  .title {
    font-size: 20px;
  }

  .desc {
    margin-bottom: 28px;
  }
}

@media (max-width: 480px) {
  #userLoginPage {
    margin: 16px auto;
    padding: 28px 20px 20px;
    border-radius: 10px;
  }

  .title {
    font-size: 19px;
  }

  .desc {
    margin-bottom: 24px;
    font-size: 12px;
  }
}
</style>
