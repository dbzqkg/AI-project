# AI健康顾问平台接口文档

**全局说明**：
除登录接口外，所有接口的 HTTP 请求头（Header）中必须携带 `token` 字段用于鉴权。

## 1. 认证接口

### 1.1 登录接口

> **请求路径**：`/api/auth/login`
> **请求方式**：`POST`

**请求参数**：

```json
{
  "username": "johndoe",
  "password": "password123"
}
```

**响应数据**：

```json
{
  "code": 1,
  "msg": "success",
  "data": { "id": 1, "username": "johndoe", "token": "eyJhb..." }
}
```

---

## 2. 患者档案管理

*(此部分保持原有逻辑，前端传参获取/新增/修改档案)*

---

## 3. AI 聊天交互 (核心流式接口)

### 3.1 健康问题咨询（流式打字机效果）

> **请求路径**：`/api/ai/chat/stream`
> **请求方式**：`POST`
> **接口描述**：支持上下文多轮对话，返回 Server-Sent Events (SSE) 数据流。

**请求参数**：


| 参数名 | 类型 | 是否必填 | 备注 |
| --- | --- | --- | --- |
| profileId | number | 是 | 当前选中的患者档案ID |
| messages | array | 是 | 当前看诊的完整对话记录数组 |

**请求示例**：

```json
{
  "profileId": 1,
  "messages": [
    {"role": "user", "content": "我最近觉得头晕"},
    {"role": "assistant", "content": "您有量过血压吗？"},
    {"role": "user", "content": "高压150"}
  ]
}
```

**响应数据**：
此接口不返回标准的 JSON，而是返回 `text/event-stream` 流式字符串。
前端每次读取到一段字符串，直接追加到页面的聊天气泡中即可。

---

## 4. AI 总结生成与保存

### 4.1 结束问诊生成总结

> **请求路径**：`/api/ai/save-summary`
> **请求方式**：`POST`
> **接口描述**：结束看病时调用。后端要求大模型生成病情总结，并覆盖保存到数据库。

**请求参数**：

```json
{
  "profileId": 1,
  "fullChatLog": "患者：我头晕... AI：建议休息..."
}
```

**响应数据**：

```json
{
  "code": 1,
  "msg": "success",
  "data": "生成的新总结内容"
}
```
