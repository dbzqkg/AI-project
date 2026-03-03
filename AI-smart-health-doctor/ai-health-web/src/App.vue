<template>
  <div class="app-container">
    <!-- 未登录/注册 遮罩层 -->
    <div v-if="!isLoggedIn" class="login-overlay">
      <el-card class="login-card">
        <template #header>
          <div class="card-header">
            <h2>{{ isLoginMode ? 'AI 健康医生 - 登录' : '注册新账号' }}</h2>
          </div>
        </template>

        <el-form :model="authForm" label-width="80px">
          <el-form-item label="用户名">
            <el-input v-model="authForm.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="authForm.password" type="password" placeholder="请输入密码" show-password @keyup.enter="handleLogin"></el-input>
          </el-form-item>
          <el-form-item v-if="!isLoginMode" label="确认密码">
            <el-input v-model="authForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password></el-input>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="isLoginMode ? handleLogin() : handleRegister()" style="width: 100%">
              {{ isLoginMode ? '登 录' : '注 册' }}
            </el-button>
          </el-form-item>

          <div class="toggle-mode">
            <el-link type="primary" :underline="false" @click="isLoginMode = !isLoginMode">
              {{ isLoginMode ? '没有账号？点击注册' : '已有账号？点击登录' }}
            </el-link>
          </div>
        </el-form>
      </el-card>
    </div>

    <!-- 登录后的主界面布局 -->
    <el-container v-else class="main-layout">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="logo">校园医疗 AI 智能健康医生 <span>(GPT-5.1)</span></div>
        <div class="header-right">
          <span class="welcome-text">欢迎, {{ currentUser }}</span>
          <el-button type="danger" size="small" plain @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>

      <el-container class="content-container">
        <!-- 左侧：就诊人管理区 -->
        <el-aside width="260px" class="sidebar">
          <div class="sidebar-header">
            <h3>就诊人档案</h3>
            <div>
              <el-button type="primary" size="small" icon="Plus" circle @click="showAddProfileDialog = true" title="新增档案"></el-button>
              <el-button type="success" size="small" icon="Refresh" circle @click="fetchProfiles" title="刷新列表"></el-button>
            </div>
          </div>

          <el-menu class="patient-menu" :default-active="currentProfileId ? currentProfileId.toString() : ''">
            <el-menu-item v-for="p in profileList" :key="p.id" :index="p.id.toString()" @click="selectProfile(p)">
              <el-icon><Avatar /></el-icon>
              <span>{{ p.realName }} ({{ p.relation }})</span>
            </el-menu-item>
            <el-menu-item v-if="profileList.length === 0" index="0" disabled>
              暂无档案，请点击右上角 '+' 添加
            </el-menu-item>
          </el-menu>

          <div class="sidebar-footer">
            <el-button type="warning" icon="DocumentChecked" class="save-btn" @click="saveCondition" :disabled="!currentProfileId || chatHistory.length === 0">
              保存并生成病情总结
            </el-button>
          </div>
        </el-aside>

        <!-- 右侧：聊天主界面 -->
        <el-main class="chat-main">
          <div v-if="!currentProfileId" class="empty-state">
            <el-empty description="请先在左侧选择或添加就诊人档案" />
          </div>

          <div v-else class="chat-container">
            <!-- 顶部显示当前就诊人简要信息 -->
            <div class="chat-header">
              正在为 <strong>{{ currentProfile?.realName }}</strong> ({{ currentProfile?.age }}岁 / {{ currentProfile?.gender }}) 问诊中...
              <span v-if="currentProfile?.medicalHistory" class="history-tag">既往病史: {{ currentProfile?.medicalHistory }}</span>
            </div>

            <!-- 聊天记录展��区 -->
            <div class="message-list" ref="messageListRef">
              <div v-for="(msg, index) in chatHistory" :key="index" :class="['message-item', msg.role === 'user' ? 'message-user' : 'message-ai']">
                <div class="avatar">{{ msg.role === 'user' ? '我' : 'AI' }}</div>
                <div class="bubble">{{ msg.content }}</div>
              </div>
            </div>

            <!-- 底部输入区 -->
            <div class="input-area">
              <div class="input-box">
                <el-input
                    v-model="inputText"
                    type="textarea"
                    :rows="3"
                    resize="none"
                    placeholder="请详细描述您的症状..."
                    @keyup.enter.native.prevent="sendMessage"
                    :disabled="isGenerating"
                ></el-input>
                <el-button type="primary" class="send-btn" icon="Position" @click="sendMessage" :loading="isGenerating">
                  {{ isGenerating ? 'AI思考中' : '发送' }}
                </el-button>
              </div>
            </div>
          </div>
        </el-main>
      </el-container>
    </el-container>

    <!-- 新增档案弹窗 -->
    <el-dialog v-model="showAddProfileDialog" title="新增就诊人档案" width="500px">
      <el-form :model="newProfile" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="newProfile.realName" placeholder="如: 张三"></el-input>
        </el-form-item>
        <el-form-item label="关系">
          <el-input v-model="newProfile.relation" placeholder="如: 本人 / 父亲 / 母亲"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="newProfile.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="newProfile.age" :min="1" :max="120" />
        </el-form-item>
        <el-form-item label="既往病史">
          <el-input v-model="newProfile.medicalHistory" type="textarea" placeholder="选填，如: 高血压、糖尿病..."></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddProfileDialog = false">取消</el-button>
          <el-button type="primary" @click="submitAddProfile">确定添加</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { Refresh, Avatar, Position, DocumentChecked, Plus } from '@element-plus/icons-vue'

// 这里的 baseUrl 必须和你的 Spring Boot ���口一致
const API_BASE_URL = 'http://localhost:8080'

// --- 状态管理 ---
const isLoggedIn = ref(false)
const isLoginMode = ref(true)
const authForm = reactive({ username: '', password: '', confirmPassword: '' })
const currentUser = ref('')

// 档案相关
const profileList = ref<any[]>([])
const currentProfileId = ref<number | null>(null)
const currentProfile = ref<any>(null)
const showAddProfileDialog = ref(false)
const newProfile = reactive({ realName: '', relation: '本人', gender: '男', age: 20, medicalHistory: '' })

// 聊天相关
const chatHistory = ref<{role: string, content: string}[]>([])
const inputText = ref('')
const messageListRef = ref<HTMLElement | null>(null)
const isGenerating = ref(false)

// 封装带有 token 的 axios
const getAuthHeaders = () => ({
  'token': localStorage.getItem('token') || ''
})

// --- 登录与注册逻辑 ---
const handleLogin = async () => {
  if (!authForm.username || !authForm.password) {
    ElMessage.warning('请输入用户名和密码！')
    return
  }
  try {
    // 接口文档 2.1 用户登录
    const res = await axios.post(`${API_BASE_URL}/api/auth/login`, {
      username: authForm.username,
      password: authForm.password
    })

    // 根据接口文档，状态码为 1 表示成功
    if (res.data.code === 1) {
      ElMessage.success('登录成功！')
      localStorage.setItem('token', res.data.data.token)
      currentUser.value = res.data.data.username
      isLoggedIn.value = true
      fetchProfiles() // 登录成功拉取档案列表
    } else {
      ElMessage.error(res.data.msg || '登录失败')
    }
  } catch (error: any) {
    ElMessage.error('网络请求失败，请确保 Spring Boot 已启动。错误信息：' + error.message)
  }
}

const handleRegister = async () => {
  if (!authForm.username || !authForm.password) {
    ElMessage.warning('请填写完整注册信息')
    return
  }
  if (authForm.password !== authForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致！')
    return
  }
  try {
    // 假设后端注册接口为 /api/auth/register (文档未提及，若没有会报404)
    const res = await axios.post(`${API_BASE_URL}/api/auth/register`, {
      username: authForm.username,
      password: authForm.password
    })
    if (res.data.code === 1) {
      ElMessage.success('注册成功，请登录！')
      isLoginMode.value = true
    } else {
      ElMessage.error(res.data.msg || '注册失败')
    }
  } catch (error: any) {
    ElMessage.error('注册接口调用失败 (可能后端未实现注册接口)：' + error.message)
  }
}

const handleLogout = () => {
  isLoggedIn.value = false
  localStorage.removeItem('token')
  authForm.password = ''
  chatHistory.value = []
  currentProfileId.value = null
}

// --- 档案管理 (Patient Profiles) ---
const fetchProfiles = async () => {
  try {
    // 接口文档 3.1 获取就诊人列表
    const res = await axios.get(`${API_BASE_URL}/api/profiles`, {
      headers: getAuthHeaders()
    })
    if (res.data.code === 1) {
      profileList.value = res.data.data || []
      ElMessage.success('档案列表已刷新')
    } else if (res.data.msg === 'NOT_LOGIN') {
      ElMessage.error('登录已过期，请重新登录')
      handleLogout()
    }
  } catch (error: any) {
    ElMessage.error('获取档案列表失败：' + error.message)
  }
}

const submitAddProfile = async () => {
  if (!newProfile.realName) {
    ElMessage.warning('请填写姓名')
    return
  }
  try {
    // 接口文档 3.2 添加就诊人
    const res = await axios.post(`${API_BASE_URL}/api/profiles`, newProfile, {
      headers: getAuthHeaders()
    })
    if (res.data.code === 1) {
      ElMessage.success('档案添加成功')
      showAddProfileDialog.value = false
      fetchProfiles() // 刷新列表
    } else {
      ElMessage.error(res.data.msg || '添加失败')
    }
  } catch (error: any) {
    ElMessage.error('添加档案失败：' + error.message)
  }
}

const selectProfile = (profile: any) => {
  currentProfileId.value = profile.id
  currentProfile.value = profile
  chatHistory.value = []

  let welcomeMsg = `你好，我是你的AI健康顾问。`
  if (profile.aiSummary) {
    welcomeMsg += `根据过往记录：${profile.aiSummary}。请问今天哪里不舒服？`
  } else {
    welcomeMsg += `请问今天哪里不舒服？`
  }
  chatHistory.value.push({ role: 'ai', content: welcomeMsg })
}

// --- 核心流式对话 (AI Chat Stream) ---
const sendMessage = async () => {
  if (!inputText.value.trim() || isGenerating.value) return

  const userMsg = inputText.value
  chatHistory.value.push({ role: 'user', content: userMsg })
  inputText.value = ''
  scrollToBottom()

  isGenerating.value = true

  // 先在聊天记录里占个位置给AI
  chatHistory.value.push({ role: 'ai', content: '' })
  const aiMessageIndex = chatHistory.value.length - 1

  try {
    // 接口文档 4.1 流式健康咨询 (基于 Fetch API 的 SSE 接收)
    const response = await fetch(`${API_BASE_URL}/api/ai/chat/stream`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'token': localStorage.getItem('token') || ''
      },
      body: JSON.stringify({
        profileId: currentProfileId.value,
        messages: chatHistory.value.slice(0, -1) // 发送历史记录(不包含当前空回复)
      })
    });

    if (!response.body) throw new Error('流式响应失败')

    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8')

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      const chunk = decoder.decode(value, { stream: true })

      // 处理后端返回 NOT_LOGIN 的边界情况
      if (chunk.includes('NOT_LOGIN')) {
        ElMessage.error('登录失效')
        handleLogout()
        break
      }

      // 拼接到最后一条AI消息的末尾，实现打字机效果
      chatHistory.value[aiMessageIndex].content += chunk
      scrollToBottom()
    }
  } catch (error: any) {
    chatHistory.value[aiMessageIndex].content = '网络异常，连接大模型失败。'
    ElMessage.error('消息发送失败: ' + error.message)
  } finally {
    isGenerating.value = false
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

// --- 保存并生成病情总结 ---
const saveCondition = async () => {
  ElMessage.info('正在向AI发送历史对话，生成病情总结...')
  try {
    // 接口文档 4.2 保存并生成病情总结
    const fullLogText = chatHistory.value.map(msg => `${msg.role === 'user' ? '患者' : '医生'}: ${msg.content}`).join('\n')

    const res = await axios.post(`${API_BASE_URL}/api/ai/save-summary`, {
      profileId: currentProfileId.value,
      fullChatLog: fullLogText
    }, {
      headers: getAuthHeaders()
    })

    if (res.data.code === 1) {
      ElMessage.success('病情总结生成完毕并归档！')
      fetchProfiles() // 刷新档案，获取最新AI总结
    } else {
      ElMessage.error(res.data.msg || '保存失败')
    }
  } catch (error: any) {
    ElMessage.error('保存请求异常：' + error.message)
  }
}

</script>

<style scoped>
.app-container { height: 100vh; width: 100vw; background-color: #f0f2f5; display: flex; justify-content: center; align-items: center; }
.login-overlay { width: 100%; height: 100%; display: flex; justify-content: center; align-items: center; background-image: linear-gradient(135deg, #e0c3fc 0%, #8ec5fc 100%); }
.login-card { width: 400px; border-radius: 12px; box-shadow: 0 8px 24px rgba(0,0,0,0.1); }
.card-header h2 { margin: 0; text-align: center; color: #333; font-size: 20px;}
.toggle-mode { text-align: center; margin-top: 15px; }

.main-layout { height: 100vh; width: 100vw; }
.header { background-color: #fff; border-bottom: 1px solid #e6e6e6; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 4px rgba(0,0,0,0.05); z-index: 10; }
.logo { font-size: 20px; font-weight: bold; color: #409EFF; }
.logo span { font-size: 14px; color: #909399; font-weight: normal; margin-left: 10px; }
.welcome-text { margin-right: 20px; color: #606266; font-size: 14px; }

.content-container { height: calc(100vh - 60px); }
.sidebar { background-color: #fff; border-right: 1px solid #e6e6e6; display: flex; flex-direction: column; }
.sidebar-header { padding: 15px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #ebeef5; }
.sidebar-header h3 { margin: 0; color: #303133; font-size: 16px; }
.patient-menu { flex: 1; border-right: none; overflow-y: auto; }
.sidebar-footer { padding: 20px 15px; border-top: 1px solid #ebeef5; }
.save-btn { width: 100%; }

.chat-main { padding: 0; display: flex; flex-direction: column; background-color: #f5f7fa; }
.empty-state { height: 100%; display: flex; justify-content: center; align-items: center; }
.chat-container { height: 100%; display: flex; flex-direction: column; }
.chat-header { padding: 10px 20px; background-color: #fff; border-bottom: 1px solid #e6e6e6; font-size: 13px; color: #666; }
.history-tag { margin-left: 15px; color: #E6A23C; background: #fdf6ec; padding: 2px 6px; border-radius: 4px;}
.message-list { flex: 1; padding: 20px; overflow-y: auto; }
.message-item { display: flex; margin-bottom: 20px; }
.message-user { flex-direction: row-reverse; }
.avatar { width: 40px; height: 40px; border-radius: 50%; background-color: #409EFF; color: white; display: flex; justify-content: center; align-items: center; font-size: 14px; font-weight: bold; flex-shrink: 0; }
.message-ai .avatar { background-color: #67C23A; margin-right: 15px; }
.message-user .avatar { margin-left: 15px; }
.bubble { max-width: 60%; padding: 12px 16px; border-radius: 8px; font-size: 14px; line-height: 1.5; white-space: pre-wrap; word-break: break-all; }
.message-ai .bubble { background-color: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.message-user .bubble { background-color: #95ec69; color: #000; }

.input-area { background-color: #fff; border-top: 1px solid #e6e6e6; padding: 15px 20px; }
.input-box { display: flex; gap: 15px; align-items: flex-end; }
.send-btn { height: 40px; width: 100px; }
</style>