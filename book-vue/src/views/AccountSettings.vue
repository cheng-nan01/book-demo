<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { changePassword, changeUsername } from '@/api/auth'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

// 修改密码
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordLoading = ref(false)

async function handleChangePassword() {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword) {
    ElMessage.warning('请填写完整')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }
  passwordLoading.value = true
  try {
    await changePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch {
    // axios 拦截器已处理错误提示
  } finally {
    passwordLoading.value = false
  }
}

// 修改用户名
const usernameForm = ref({
  username: '',
  password: ''
})
const usernameLoading = ref(false)

async function handleChangeUsername() {
  if (!usernameForm.value.username || !usernameForm.value.password) {
    ElMessage.warning('请填写完整')
    return
  }
  usernameLoading.value = true
  try {
    await changeUsername({
      username: usernameForm.value.username,
      password: usernameForm.value.password
    })
    ElMessage.success('用户名修改成功')
    userStore.userInfo.username = usernameForm.value.username
    usernameForm.value = { username: '', password: '' }
  } catch {
    // axios 拦截器已处理错误提示
  } finally {
    usernameLoading.value = false
  }
}
</script>

<template>
  <div class="account-settings">
    <h2 class="page-title">账户设置</h2>

    <el-row :gutter="24">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span class="card-title">修改密码</span>
          </template>
          <el-form label-width="90px" size="default">
            <el-form-item label="当前密码">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                show-password
                placeholder="输入当前密码"
              />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                show-password
                placeholder="输入新密码"
              />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                show-password
                placeholder="再次输入新密码"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                :loading="passwordLoading"
                @click="handleChangePassword"
              >
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span class="card-title">修改用户名</span>
          </template>
          <el-form label-width="90px" size="default">
            <el-form-item label="当前用户名">
              <el-input :model-value="userStore.username" disabled />
            </el-form-item>
            <el-form-item label="新用户名">
              <el-input
                v-model="usernameForm.username"
                placeholder="输入新用户名"
              />
            </el-form-item>
            <el-form-item label="登录密码">
              <el-input
                v-model="usernameForm.password"
                type="password"
                show-password
                placeholder="输入密码确认身份"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                :loading="usernameLoading"
                @click="handleChangeUsername"
              >
                修改用户名
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.account-settings {
  max-width: 900px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 24px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #334155;
}

:deep(.el-card) {
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}
</style>
