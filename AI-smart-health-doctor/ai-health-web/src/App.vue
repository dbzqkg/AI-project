<template>
  <el-config-provider :locale="zhCn">
    <router-view />
  </el-config-provider>
</template>

<script setup lang="ts">
import { ElConfigProvider } from 'element-plus'
// @ts-ignore
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
</script>

<style>
/* Global Reset */
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: #f5f7fa;
  -webkit-font-smoothing: antialiased;
}

/* ================= 登录页 (精美医疗感) ================= */
.login-wrapper {
  display: flex;
  height: 100%;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
}
.login-left-brand {
  margin-right: 100px;
  text-align: center;
  color: #0050b3;
}
.brand-icon {
  font-size: 80px;
  margin-bottom: 20px;
}
.login-left-brand h1 {
  font-size: 36px;
  margin: 0 0 10px 0;
  font-weight: 600;
}
.login-left-brand p {
  font-size: 16px;
  opacity: 0.8;
}
.login-card {
  width: 420px;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  border: none;
}
.login-header {
  text-align: center;
  margin-bottom: 30px;
}
.login-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}
.login-header .subtitle {
  font-size: 13px;
  color: #999;
}
.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
}
.toggle-mode {
  text-align: center;
  margin-top: 10px;
}

/* ================= 主布局 ================= */
.main-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}
.nav-header {
  height: 60px;
  background-color: #ffffff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  z-index: 10;
}
.nav-brand {
  font-size: 18px;
  font-weight: bold;
  color: #1890ff;
  display: flex;
  align-items: center;
  gap: 10px;
}
.version-tag {
  font-size: 12px;
  background: #e6f7ff;
  padding: 2px 6px;
  border-radius: 4px;
  color: #1890ff;
  font-weight: normal;
}
.nav-user {
  display: flex;
  align-items: center;
  gap: 12px;
}
.welcome-text {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
.user-avatar {
  background-color: #1890ff;
}

.content-wrapper {
  flex: 1;
  overflow: hidden;
  display: flex;
}

/* ================= 左侧档案栏 ================= */
.sidebar {
  background-color: #ffffff;
  border-right: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
}
.sidebar-header {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
}
.sidebar-header .title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}
.profile-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}
.empty-profile {
  margin-top: 50px;
}
.profile-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
  margin-bottom: 10px;
}
.profile-item:hover {
  background-color: #fafafa;
}
.profile-item.active {
  background-color: #e6f7ff;
  border-color: #91d5ff;
}
.profile-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 20px;
  margin-right: 12px;
}
.profile-item.active .profile-avatar {
  background: #1890ff;
  color: #fff;
}
.profile-info .name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}
.profile-info .detail {
  font-size: 12px;
  color: #999;
}
.sidebar-footer {
  padding: 20px;
  border-top: 1px solid #f0f0f0;
}
.save-btn {
  width: 100%;
  height: 40px;
  border-radius: 8px;
  font-weight: bold;
}

/* ================= 右侧聊天区 ================= */
.chat-main {
  padding: 0;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}
.chat-empty-state {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #999;
}
.huge-icon {
  font-size: 80px;
  color: #d9d9d9;
  margin-bottom: 20px;
}
.chat-box {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.chat-header-info {
  background: #fff;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.02);
  z-index: 5;
}
.patient-badge {
  font-size: 14px;
  color: #333;
}
.history-badge {
  font-size: 13px;
  color: #fa8c16;
  background: #fff7e6;
  padding: 4px 10px;
  border-radius: 12px;
  border: 1px solid #ffd591;
  display: flex;
  align-items: center;
  gap: 4px;
}

.message-list-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 20px 30px;
}
.message-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.message-item {
  display: flex;
  gap: 16px;
  max-width: 85%;
}
.message-item.is-user {
  align-self: flex-end;
  flex-direction: row-reverse;
}
.message-item.is-ai {
  align-self: flex-start;
}
.msg-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}
.is-ai .msg-avatar {
  background-color: #1890ff;
  color: #fff;
}
.is-user .msg-avatar {
  background-color: #52c41a;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
}
.msg-bubble {
  display: flex;
  flex-direction: column;
}
.msg-name {
  font-size: 12px;
  color: #999;
  margin-bottom: 6px;
  margin-left: 4px;
}
.msg-content {
  padding: 14px 18px;
  border-radius: 12px;
  font-size: 15px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
  box-shadow: 0 2px 6px rgba(0,0,0,0.04);
}
.is-ai .msg-content {
  background-color: #fff;
  border: 1px solid #ebebeb;
  border-top-left-radius: 4px;
}
.is-user .msg-content {
  background-color: #d9f7be;
  border: 1px solid #b7eb8f;
  border-top-right-radius: 4px;
  color: #135200;
}

/* 输入区 */
.input-area {
  background: #fff;
  padding: 20px 30px;
  border-top: 1px solid #e8e8e8;
}
.medical-input :deep(.el-textarea__inner) {
  border-radius: 8px;
  background-color: #fafafa;
  font-size: 15px;
}
.medical-input :deep(.el-textarea__inner:focus) {
  background-color: #fff;
}
.input-toolbar {
  margin-top: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.tip-text {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}
.send-button {
  border-radius: 8px;
  padding: 0 24px;
  height: 40px;
}
</style>