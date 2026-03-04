<template>
  <div class="login-container">
    <div class="login-content">
      <div class="brand-section">
        <div class="logo-circle">
          <el-icon :size="50" color="#fff"><FirstAidKit /></el-icon>
        </div>
        <h1 class="app-title">AI Smart Health Doctor</h1>
        <p class="app-subtitle">您的全天候智能医疗顾问</p>
      </div>

      <div class="form-section">
        <h2 class="form-title">{{ isRegisterMode ? '注册账户' : '欢迎登录' }}</h2>

        <el-form :model="form" class="auth-form" size="large">
          <el-form-item>
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              :prefix-icon="UserIcon"
            />
          </el-form-item>

          <el-form-item>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleAuth"
            />
          </el-form-item>

          <el-form-item v-if="isRegisterMode">
            <el-input
              v-model="confirmPassword"
              type="password"
              placeholder="确认密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-button
            type="primary"
            class="submit-btn"
            :loading="loading"
            @click="handleAuth"
          >
            {{ isRegisterMode ? '立即注册' : '登录系统' }}
          </el-button>

          <div class="form-footer">
            <el-button link type="primary" @click="toggleMode">
              {{ isRegisterMode ? '已有账号？去登录' : '没有账号？去注册' }}
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User as UserIcon, Lock, FirstAidKit } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const isRegisterMode = ref(false)
const loading = ref(false)
const confirmPassword = ref('')
const form = reactive({
  username: '',
  password: ''
})

const toggleMode = () => {
  isRegisterMode.value = !isRegisterMode.value
  form.username = ''
  form.password = ''
  confirmPassword.value = ''
}

const handleAuth = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  if (isRegisterMode.value && form.password !== confirmPassword.value) {
    ElMessage.warning('两次输入密码不一致')
    return
  }

  loading.value = true
  try {
    if (isRegisterMode.value) {
      const success = await userStore.register(form)
      if (success) {
        ElMessage.success('注册成功，请登录')
        toggleMode()
      }
    } else {
      const success = await userStore.login(form)
      if (success) {
        ElMessage.success('登录成功')
        router.push('/')
      }
    }
  } catch (e) {
    // request.ts handles errors
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  background-size: cover;
}

.login-content {
  display: flex;
  width: 900px;
  height: 550px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(0,0,0,0.1);
  overflow: hidden;
}

.brand-section {
  flex: 1;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  padding: 40px;
}

.logo-circle {
  width: 100px;
  height: 100px;
  background: rgba(255,255,255,0.2);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
  backdrop-filter: blur(5px);
}

.app-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 10px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.app-subtitle {
  font-size: 16px;
  opacity: 0.9;
}

.form-section {
  flex: 1;
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

.auth-form {
  max-width: 320px;
  margin: 0 auto;
  width: 100%;
}

.submit-btn {
  width: 100%;
  border-radius: 25px;
  margin-top: 20px;
  background: linear-gradient(to right, #4facfe, #00f2fe);
  border: none;
  font-weight: bold;
  letter-spacing: 1px;
}

.submit-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
  transition: all 0.3s;
}

.form-footer {
  margin-top: 20px;
  text-align: center;
}
</style>

