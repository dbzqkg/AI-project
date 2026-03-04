<template>
  <el-container class="layout-container">
    <el-aside width="280px" class="layout-aside">
      <div class="aside-header">
        <el-icon class="logo-icon"><FirstAidKit /></el-icon>
        <span class="app-name">AI Health Doctor</span>
      </div>

      <div class="profile-section">
        <div class="section-title">
          <span>我的就诊人</span>
          <el-button type="primary" link :icon="Plus" @click="openAddProfile" size="small">新建</el-button>
        </div>

        <el-scrollbar class="profile-list-scroll">
          <template v-if="profiles.length > 0">
            <div
              v-for="item in profiles"
              :key="item.id"
              class="profile-item"
              :class="{ active: currentProfileId === item.id }"
              @click="selectProfile(item)"
            >
              <el-avatar :size="36" class="profile-avatar">{{ item.realName ? item.realName.charAt(0) : 'U' }}</el-avatar>
              <div class="profile-info">
                <div class="name-row">
                  <span class="name">{{ item.realName }}</span>
                  <el-tag size="small" type="info" effect="plain" class="relation-tag">{{ item.relation }}</el-tag>
                </div>
                <div class="meta-row">{{ item.gender }} | {{ item.age }}岁</div>
              </div>

              <div class="action-buttons">
                <el-button link type="primary" :icon="Edit" @click.stop="openEditProfile(item)" alt="编辑"></el-button>
                <el-button link type="danger" :icon="Delete" @click.stop="deleteProfile(item)" alt="删除"></el-button>
              </div>
            </div>
          </template>
          <el-empty v-else description="暂无档案，请创建" :image-size="60"></el-empty>
        </el-scrollbar>
      </div>

      <div class="user-footer">
        <div class="user-info">
          <el-avatar :size="32" icon="UserFilled" />
          <span class="username">{{ userStore.username }}</span>
        </div>
        <el-button type="danger" circle plain :icon="SwitchButton" @click="handleLogout" title="退出登录"></el-button>
      </div>
    </el-aside>

    <el-main class="layout-main">
      <div v-if="currentProfileId" class="chat-container">
        <div class="chat-header">
          <div class="chat-header-left">
            <h3>{{ currentProfile?.realName }} 的健康咨询</h3>
            <span class="header-meta">
              {{ currentProfile?.gender }} · {{ currentProfile?.age }}岁
              <span v-if="currentProfile?.medicalHistory" class="history-tag">既往史: {{ currentProfile.medicalHistory }}</span>
            </span>
          </div>
          <el-button type="success" plain :icon="DocumentChecked" @click="saveSummary" :loading="savingSummary">保存病情总结</el-button>
        </div>

        <div class="chat-messages" ref="msgListRef">
          <div v-for="(msg, index) in messages" :key="index" class="message-wrapper" :class="msg.role">
            <div class="message-bubble">
              <div class="message-content" v-html="formatMessage(msg.content)"></div>
            </div>
          </div>
          <div v-if="isStreaming" class="message-wrapper ai">
            <div class="message-bubble streaming">
              <span class="dot">.</span><span class="dot">.</span><span class="dot">.</span>
            </div>
          </div>
        </div>

        <div class="chat-input-area">
          <div class="upload-preview" v-if="uploadedImageUrl">
             <el-image style="width: 100px; height: 100px" :src="uploadedImageUrl" fit="cover" />
             <el-button type="danger" circle size="small" icon="Close" @click="clearImage" class="clear-img-btn"></el-button>
          </div>

          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="3"
            placeholder="请详细描述症状..."
            resize="none"
            class="custom-textarea"
            @keydown.enter.prevent="sendMessage"
          />
          <div class="input-actions" style="justify-content: space-between; display: flex;">
            <div>
               <el-upload
                 class="upload-demo"
                 :show-file-list="false"
                 :http-request="handleUpload"
                 accept="image/*">
                 <el-button type="primary" link :icon="Picture" :loading="isUploading">上传图片</el-button>
               </el-upload>
            </div>

            <div style="display: flex; align-items: center; gap: 10px;">
                <span class="disclaimer">AI 建议仅供参考，不作为最终医疗诊断</span>
                <el-button type="primary" class="send-btn" @click="sendMessage" :disabled="(!inputMessage.trim() && !uploadedImageUrl) || isStreaming">
                  发送 <el-icon class="el-icon--right"><Position /></el-icon>
                </el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <img src="@/assets/logo.svg" alt="Logo" class="empty-logo" style="width: 120px; opacity: 0.1; filter: grayscale(100%); margin-bottom: 20px;">
        <h2>欢迎使用 AI 智能医生</h2>
        <p>请在左侧选择一位就诊人开始咨询</p>
      </div>
    </el-main>

    <!-- Profile Dialog -->
    <el-dialog v-model="profileDialogVisible" :title="isEdit ? '编辑档案' : '新建档案'" width="500px">
      <el-form :model="profileForm" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="profileForm.realName" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item label="关系">
          <el-select v-model="profileForm.relation" placeholder="与本人关系" style="width: 100%">
            <el-option label="本人" value="本人" />
            <el-option label="父母" value="父母" />
            <el-option label="子女" value="子女" />
            <el-option label="配偶" value="配偶" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="profileForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="profileForm.age" :min="0" :max="120" />
        </el-form-item>
        <el-form-item label="既往病史">
          <el-input type="textarea" v-model="profileForm.medicalHistory" placeholder="无则不填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="profileDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitProfile">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  FirstAidKit, Plus, SwitchButton, UserFilled, ArrowRight,
  DocumentChecked, Position, Edit, Delete, Picture, Close
} from '@element-plus/icons-vue'
import type { PatientProfile } from '@/types'

const userStore = useUserStore()
const router = useRouter()

// Data
const profiles = ref<PatientProfile[]>([])
const currentProfileId = ref<number | null>(null)
const currentProfile = ref<PatientProfile | null>(null)
const messages = ref<{role: string, content: string, imageUrl?: string}[]>([])
const inputMessage = ref('')
const isStreaming = ref(false)
const msgListRef = ref<HTMLElement | null>(null)
const savingSummary = ref(false)
const uploadedImageUrl = ref('')
const isUploading = ref(false)

// Profile Dialog
const profileDialogVisible = ref(false)
const isEdit = ref(false)
const profileForm = reactive<PatientProfile>({
  realName: '',
  relation: '本人',
  gender: '男',
  age: 30,
  medicalHistory: ''
})

// === Methods ===

const handleUpload = async (options: any) => {
  const file = options.file
  const formData = new FormData()
  formData.append('file', file)
  formData.append('name', 'UserUpload')
  formData.append('age', '0')

  isUploading.value = true
  try {
    const res: any = await request.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    // UploadController returns Result.success(url)
    if (res.code === 1) {
       uploadedImageUrl.value = res.data
       ElMessage.success('图片上传成功')
    }
  } catch (e) {
    ElMessage.error('上传失败')
  } finally {
    isUploading.value = false
  }
}

const clearImage = () => {
    uploadedImageUrl.value = ''
}
  const formData = new FormData()
  // Backend UploadController expects: name, age, file
  formData.append('file', file)
  formData.append('name', 'UserUpload')
  formData.append('age', '0')

  isUploading.value = true
  try {
    const res: any = await request.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    // Check if result is wrapped in Result or direct
    // UploadController returns Result.success(url)
    // request.ts interceptor returns res.data
    // If successful, res.code is 1. res.data is url.
    // Wait, request.ts: return res (the whole response body).
    // So if code===1, uploaded url is res.data
    if (res.code === 1) {
       uploadedImageUrl.value = res.data
       ElMessage.success('图片上传成功')
    }
  } catch (e) {
    ElMessage.error('上传失败')
  } finally {
    isUploading.value = false
  }
}

const clearImage = () => {
    uploadedImageUrl.value = ''
}

const fetchProfiles = async () => {
  try {
    const res: any = await request.get('/api/profiles')
    if (res.code === 1) {
      profiles.value = res.data || []
      // 只有在没有选中且列表不为空时才自动选中第一个
      if (profiles.value.length > 0 && !currentProfileId.value) {
        selectProfile(profiles.value[0])
      } else if (profiles.value.length === 0) {
        currentProfileId.value = null
        currentProfile.value = null
      }
    }
  } catch (e) {
    // handled by interceptor
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

const openAddProfile = () => {
  isEdit.value = false
  Object.assign(profileForm, {
    realName: '',
    relation: '本人',
    gender: '男',
    age: 30,
    medicalHistory: ''
  })
  delete profileForm.id
  profileDialogVisible.value = true
}

const openEditProfile = (item: PatientProfile) => {
  isEdit.value = true
  Object.assign(profileForm, item)
  profileDialogVisible.value = true
}

const deleteProfile = (item: PatientProfile) => {
    ElMessageBox.confirm(
    `确定要删除就诊人 "${item.realName}" 的档案吗？`,
    '警告',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
     try {
         const res: any = await request.delete(`/api/profiles/${item.id}`)
         if (res.code === 1) {
             ElMessage.success('删���成功')
             if (currentProfileId.value === item.id) {
                 currentProfileId.value = null
                 currentProfile.value = null
             }
             fetchProfiles()
         }
     } catch(e) {}
  }).catch(() => {})
}

const submitProfile = async () => {
  if(!profileForm.realName) return ElMessage.warning('请输入姓名')

  try {
    let res: any;
    if (isEdit.value && profileForm.id) {
       res = await request.put(`/api/profiles/${profileForm.id}`, profileForm)
    } else {
       res = await request.post('/api/profiles', profileForm)
    }

    if(res.code === 1) {
      ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
      profileDialogVisible.value = false
      fetchProfiles()
    }
  } catch(e) {}
}

const selectProfile = (item: PatientProfile) => {
  if (currentProfileId.value === item.id) return; // Prevent reload if same

  currentProfileId.value = item.id!
  currentProfile.value = item
  messages.value = [] // Clear history on switch

  // Add initial AI greeting
  let greeting = `您好，我是您的AI医疗助手。`
  if(item.aiSummary) greeting += `\n根据上次记录：${item.aiSummary}`
  greeting += `\n请问${item.realName}今天有什么不适？`

  messages.value.push({ role: 'ai', content: greeting })
}

const scrollToBottom = () => {
  nextTick(() => {
    if(msgListRef.value) {
      msgListRef.value.scrollTop = msgListRef.value.scrollHeight
    }
  })
}

const formatMessage = (content: string) => {
  return content.replace(/\n/g, '<br/>')
}

// === Chat Logic ===

const sendMessage = async () => {
  if((!inputMessage.value.trim() && !uploadedImageUrl.value) || isStreaming.value || !currentProfileId.value) return

  const userMsg = inputMessage.value
  const imgUrl = uploadedImageUrl.value

  messages.value.push({ role: 'user', content: userMsg, imageUrl: imgUrl })

  inputMessage.value = ''
  uploadedImageUrl.value = ''
  scrollToBottom()

  isStreaming.value = true

  // 准备AI回复占位
  const aiMsgIndex = messages.value.length
  messages.value.push({ role: 'ai', content: '' })

  try {
    const token = localStorage.getItem('token') || ''

    // Construct message history for context
    const contextMessages = messages.value.slice(0, aiMsgIndex).map(m => ({
      role: m.role === 'ai' ? 'assistant' : m.role,
      content: m.content,
      imageUrl: m.imageUrl
    }))

    const response = await fetch('http://localhost:8080/api/ai/chat/stream', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'token': token
        },
        body: JSON.stringify({
            profileId: currentProfileId.value,
            messages: contextMessages
        })
    })

    if (!response.ok) {
         throw new Error(`Chat request failed: ${response.status} ${response.statusText}`)
    }
    if (!response.body) {
         throw new Error('No response body')
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    while (true) {
        const { done, value } = await reader.read()

        if (done) {
            // Flush decoder
            // buffer += decoder.decode() // optional if stream:true was consistent but safe to do
            // Process any remaining buffer
            if (buffer.trim()) {
                const lines = buffer.split('\n')
                for (const line of lines) {
                    if (line.trim().startsWith('data:')) {
                        messages.value[aiMsgIndex].content += line.replace(/^data:/, '')
                    }
                }
            }
            break
        }

        const chunk = decoder.decode(value, { stream: true })

        if (chunk.includes('NOT_LOGIN')) {
             ElMessage.error('登录过期')
             handleLogout()
             break
        }

        buffer += chunk

        const lines = buffer.split('\n')
        // Keep the last segment in buffer
        buffer = lines.pop() || ''

        for (const line of lines) {
            if (line.trim() === '') continue
            if (line.startsWith('data:')) {
                const content = line.replace(/^data:/, '')
                messages.value[aiMsgIndex].content += content
            }
        }

        scrollToBottom()
    }

  } catch (error) {
    console.error('AI Stream Error:', error)
    ElMessage.error('AI服务暂时不可用')
    messages.value[aiMsgIndex].content += '\n[连接中断]'
  } finally {
    isStreaming.value = false
  }
}

const saveSummary = async () => {
  if(!currentProfileId.value) return
  savingSummary.value = true
  try {
    // 拼接全文
    const fullLog = messages.value.map(m => `${m.role}: ${m.content}`).join('\n')

    const res: any = await request.post('/api/ai/save-summary', {
      profileId: currentProfileId.value,
      fullChatLog: fullLog
    })

    if(res.code === 1) {
      ElMessage.success('病情总结已更新')
      // Update local profile summary
      if(currentProfile.value) {
          currentProfile.value.aiSummary = res.data
      }
    }
  } catch(e) {
  } finally {
    savingSummary.value = false
  }
}

onMounted(() => {
  fetchProfiles()
})

</script>

<style scoped>
.layout-container {
  height: 100vh;
  background-color: #f0f2f5;
}

.layout-aside {
  background: #fff;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
}

.aside-header {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid #f0f2f5;
  background-color: #fff;
}

.logo-icon {
  font-size: 24px;
  color: #1890ff;
  margin-right: 10px;
}

.app-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.profile-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 10px 0;
}

.section-title {
  padding: 10px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #666;
  font-size: 14px;
}

.profile-list-scroll {
  flex: 1;
}

.profile-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: background 0.3s;
  border-left: 3px solid transparent;
}

.profile-item:hover {
  background-color: #f6ffed;
}

.profile-item.active {
  background-color: #e6f7ff;
  border-left-color: #1890ff;
}

.profile-avatar {
  background: #40a9ff;
  margin-right: 12px;
  flex-shrink: 0;
}

.profile-info {
  flex: 1;
  overflow: hidden;
}

.name-row {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.name {
  font-weight: 500;
  color: #333;
  margin-right: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.meta-row {
  font-size: 12px;
  color: #999;
}

.arrow-icon {
  font-size: 14px;
  color: #ccc;
}

.action-buttons {
  display: flex;
  visibility: hidden;
  gap: 4px;
}

.profile-item:hover .action-buttons {
  visibility: visible;
}

.user-footer {
  height: 64px;
  border-top: 1px solid #f0f2f5;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fafafa;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.username {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.layout-main {
  padding: 0;
  display: flex;
  flex-direction: column;
  height: 100vh;
}

/* Chat Styles */
.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-header {
  height: 60px;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}

.chat-header h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #333;
}

.header-meta {
  font-size: 12px;
  color: #666;
}

.history-tag {
  background: #fffbe6;
  color: #faad14;
  padding: 2px 6px;
  border-radius: 4px;
  margin-left: 8px;
  border: 1px solid #ffe58f;
}

.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background-color: #f5f7fa;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-wrapper {
  display: flex;
  width: 100%;
}

.message-wrapper.user {
  justify-content: flex-end;
}

.message-wrapper.ai {
  justify-content: flex-start;
}

.message-bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-wrap: break-word;
}

.message-wrapper.user .message-bubble {
  background-color: #1890ff;
  color: #fff;
  border-bottom-right-radius: 4px;
  box-shadow: 0 4px 10px rgba(24, 144, 255, 0.2);
}

.message-wrapper.ai .message-bubble {
  background-color: #fff;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.05);
}

.chat-input-area {
  background: #fff;
  padding: 20px;
  border-top: 1px solid #e8e8e8;
}

.custom-textarea :deep(.el-textarea__inner) {
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  transition: all 0.3s;
}

.custom-textarea :deep(.el-textarea__inner:focus) {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.disclaimer {
  font-size: 12px;
  color: #999;
}

.send-btn {
  border-radius: 18px;
  padding: 8px 24px;
}

.empty-state {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
}

.dot {
  animation: dot-blink 1.4s infinite ease-in-out both;
}

.dot:nth-child(1) { animation-delay: -0.32s; }
.dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes dot-blink {
  0%, 80%, 100% { opacity: 0; }
  40% { opacity: 1; }
}

/* Scrollbar beautify */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.15);
  border-radius: 3px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
</style>

