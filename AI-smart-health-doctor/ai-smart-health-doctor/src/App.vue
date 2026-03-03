<template>
  <div class="common-layout">
    <el-container style="height: 100vh">
      <el-aside width="300px" style="background-color: #f4f4f5; padding: 20px;">
        <h3>就诊人列表</h3>
        <el-button type="primary" size="small" @click="loadProfiles" style="margin-bottom: 10px">刷新档案</el-button>
        <el-radio-group v-model="currentProfileId">
          <el-radio v-for="p in profiles" :key="p.id" :label="p.id" border style="margin-bottom: 10px; width: 100%">
            {{ p.realName }} ({{ p.relation }})
          </el-radio>
        </el-radio-group>
        <el-divider />
        <div v-if="selectedProfile">
          <p><b>既往病史：</b>{{ selectedProfile.medicalHistory || '暂无' }}</p>
        </div>
      </el-aside>

      <el-container>
        <el-header style="border-bottom: 1px solid #ddd; display: flex; align-items: center; justify-content: space-between;">
          <span>AI 智能健康顾问 (模型: gpt-5.1)</span>
          <el-button v-if="!isLoggedIn" type="primary" @click="loginDialogVisible = true">登录</el-button>
          <el-button v-else type="danger" @click="handleLogout">退出</el-button>
        </el-header>

        <el-main>
          <div class="chat-box" ref="chatBox">
            <div v-for="(msg, i) in messages" :key="i" :class="['msg', msg.role]">
              <div class="content">{{ msg.content }}</div>
            </div>
          </div>
        </el-main>

        <el-footer height="100px">
          <el-input v-model="userInput" type="textarea" placeholder="请详细描述您的症状..." @keyup.enter.native="handleChat">
            <template #append>
              <el-button @click="handleChat">发送</el-button>
            </template>
          </el-input>
          <el-button type="success" @click="handleSaveAndExit" style="margin-top: 10px">保存并生成病情总结</el-button>
        </el-footer>
      </el-container>
    </el-container>

    <el-dialog v-model="loginDialogVisible" title="用户登录" width="30%">
      <el-form :model="loginForm">
        <el-form-item label="用户名"><el-input v-model="loginForm.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="loginForm.password" type="password" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="doLogin">确认登录</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const isLoggedIn = ref(!!localStorage.getItem('token'))
const loginDialogVisible = ref(false)
const loginForm = ref({ username: '', password: '' })
const profiles = ref([])
const currentProfileId = ref(null)
const userInput = ref('')
const messages = ref([])

const selectedProfile = computed(() => profiles.value.find(p => p.id === currentProfileId.value))

// 1. 登录
const doLogin = async () => {
  const res = await axios.post('/api/auth/login', loginForm.value)
  if (res.data.code === 1) {
    localStorage.setItem('token', res.data.data.token)
    localStorage.setItem('userId', res.data.data.id)
    isLoggedIn.value = true
    loginDialogVisible.value = false
    loadProfiles()
  } else {
    ElMessage.error(res.data.msg)
  }
}

// 2. 加载就诊人档案
const loadProfiles = async () => {
  const userId = localStorage.getItem('userId')
  const res = await axios.get(`/api/auth/profiles/${userId}`)
  profiles.value = res.data.data
}

// 3. 聊天 (后端通过 profileId 注入病史)
const handleChat = async () => {
  if (!currentProfileId.value) return ElMessage.warning('请先在左侧选择就诊人')
  const userMsg = userInput.value
  messages.value.push({ role: 'user', content: userMsg })
  userInput.value = ''

  // 这里模拟调用 chat 接口
  messages.value.push({ role: 'assistant', content: 'AI 正在分析病史并回答...' })
}

// 4. 创新点：保存并自动总结
const handleSaveAndExit = async () => {
  const fullChatLog = messages.value.map(m => `${m.role}: ${m.content}`).join('\n')
  const res = await axios.post('/api/ai/save-summary', {
    profileId: currentProfileId.value,
    fullChatLog: fullChatLog
  })

  if (res.data.code === 1) {
    ElMessageBox.alert(`AI 总结：${res.data.data}`, '病情总结已归档')
    loadProfiles() // 刷新左侧病史
  }
}

const handleLogout = () => { localStorage.clear(); location.reload() }
</script>

<style>
.chat-box { height: 60vh; overflow-y: auto; padding: 20px; background: #fff; border: 1px solid #eee; }
.msg { margin-bottom: 15px; display: flex; }
.user { justify-content: flex-end; }
.user .content { background: #95ec69; padding: 10px; border-radius: 5px; }
.assistant .content { background: #f4f4f5; padding: 10px; border-radius: 5px; }
</style>