# AI健康顾问平台接口文档

**全局说明**：
除登录接口外，所有接口的 HTTP 请求头（Header）中必须携带 `token` 字段用于鉴权。后端通过解析 Token 获取当前登录用户的 `id`，进行后续的业务操作与越权校验。

## 1. 认证接口

### 1.1 登录接口

#### 1.1.1 基本信息

> **请求路径**：`/api/auth/login`
> 
> **请求方式**：`POST`
> 
> **接口描述**：用于用户登录，成功后返回JWT令牌。

#### 1.1.2 请求参数

**格式**：`application/json`

| 参数名      | 类型     | 是否必填 | 备注  |
| -------- | ------ | ---- | --- |
| username | string | 是    | 用户名 |
| password | string | 是    | 密码  |

**请求示例**：

```json
{
  "username": "johndoe",
  "password": "password123"
}
```

#### 1.1.3 响应数据

**格式**：`application/json`

| 参数名  | 类型     | 是否必填 | 备注             |
| ---- | ------ | ---- | -------------- |
| code | number | 是    | 响应码（1为成功，0为失败） |
| msg  | string | 否    | 提示信息           |
| data | object | 是    | 用户信息及JWT令牌     |

**响应示例**：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "username": "johndoe",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MX0.6O4gVwbb...OlvbhgD7z"
  }
}
```

---

## 2. 患者档案管理

### 2.1 获取当前用户的所有就诊人（档案）

#### 2.1.1 基本信息

> **请求路径**：`/api/profiles`
> 
> **请求方式**：`GET`
> 
> **接口描述**：查询当前登录账号下的所有患者/亲属档案。用户ID从Header中的Token解析，前端无需显式传递。

#### 2.1.2 请求参数

无请求体参数。

#### 2.1.3 响应数据

**格式**：`application/json`

| 参数名  | 类型       | 是否必填 | 备注     |
| ---- | -------- | ---- | ------ |
| code | number   | 是    | 响应码    |
| msg  | string   | 否    | 提示信息   |
| data | object[] | 是    | 患者档案列表 |

**响应示例**：

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "relation": "本人",
      "realName": "张三",
      "gender": "男",
      "age": 22,
      "height": 175.5,
      "weight": 65.0,
      "medicalHistory": "无重大病史",
      "aiSummary": "身体健康"
    },
    {
      "id": 2,
      "relation": "父亲",
      "realName": "张建国",
      "gender": "男",
      "age": 55,
      "height": 170.0,
      "weight": 70.0,
      "medicalHistory": "高血压",
      "aiSummary": "需定期服用降压药"
    }
  ]
}
```

### 2.2 新增患者档案

#### 2.2.1 基本信息

> **请求路径**：`/api/profiles`
> 
> **请求方式**：`POST`
> 
> **接口描述**：为当前账号添加新的就诊人档案。

#### 2.2.2 请求参数

**格式**：`application/json`

| 参数名            | 类型     | 是否必填 | 备注                 |
| -------------- | ------ | ---- | ------------------ |
| relation       | string | 是    | 与主账号关系（如：本人、父亲、母亲） |
| realName       | string | 是    | 患者姓名               |
| gender         | string | 是    | 性别（男/女）            |
| age            | number | 是    | 患者年龄               |
| height         | number | 否    | 身高(cm)             |
| weight         | number | 否    | 体重(kg)             |
| medicalHistory | string | 否    | 既往病史               |

**请求示例**：

```json
{
  "relation": "父亲",
  "realName": "张建国",
  "age": 55,
  "gender": "男",
  "height": 170.0,
  "weight": 70.0,
  "medicalHistory": "高血压"
}
```

#### 2.2.3 响应数据

**格式**：`application/json`

| 参数名  | 类型     | 是否必填 | 备注   |
| ---- | ------ | ---- | ---- |
| code | number | 是    | 响应码  |
| msg  | string | 否    | 提示信息 |

**响应示例**：

```json
{
  "code": 1,
  "msg": "success"
}
```

### 2.3 更新患者档案

#### 2.3.1 基本信息

> **请求路径**：`/api/profiles/{id}`
> 
> **请求方式**：`PUT`
> 
> **接口描述**：更新指定的患者档案信息（后端需校验该 id 是否属于当前 Token 对应的账号）。

#### 2.3.2 请求参数

**格式**：`application/json` (参数同新增接口)

#### 2.3.3 响应数据

**格式**：`application/json`

**响应示例**：

```json
{
  "code": 1,
  "msg": "success"
}
```

---

## 3. AI 聊天交互

### 3.1 健康问题咨询

#### 3.1.1 基本信息

> **请求路径**：`/api/ai/chat`
> 
> **请求方式**：`POST`
> 
> **接口描述**：向AI咨询健康相关问题。**后端逻辑**：通过前端传来的 `profileId` 结合 Token 解析出的 `userId` 去数据库中安全查询档案，将其转化为 Prompt 的一部分发给大模型。

#### 3.1.2 请求参数

**格式**：`application/json`

| 参数名       | 类型     | 是否必填 | 备注              |
| --------- | ------ | ---- | --------------- |
| profileId | number | 是    | 当前对话选中的患者档案ID主键 |
| question  | string | 是    | 用户提出的最新问题       |

**请求示例**：

```json
{
  "profileId": 1,
  "question": "我最近总是觉得头晕，跟我的高血压有关系吗？"
}
```

#### 3.1.3 响应数据

**格式**：`application/json`

| 参数名  | 类型     | 是否必填 | 备注    |
| ---- | ------ | ---- | ----- |
| code | number | 是    | 响应码   |
| msg  | string | 否    | 提示信息  |
| data | object | 是    | AI的回答 |

**响应示例**：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "answer": "您好，头晕确实可能是高血压的常见症状之一。考虑到您的既往病史，建议您..."
  }
}
```

---

## 4. AI总结生成

### 4.1 生成并保存病历总结

#### 4.1.1 基本信息

> **请求路径**：`/api/ai/save-summary`
> 
> **请求方式**：`POST`
> 
> **接口描述**：在用户退出或主动保存时调用，基于完整对话记录让大模型生成总结，并覆盖保存到对应患者的 `ai_summary` 字段中。**后端需校验该 `profileId` 是否属于当前登录用户**。

#### 4.1.2 请求参数

**格式**：`application/json`

| 参数名         | 类型     | 是否必填 | 备注       |
| ----------- | ------ | ---- | -------- |
| profileId   | number | 是    | 患者档案ID主键 |
| fullChatLog | string | 是    | 完整的对话记录  |

**请求示例**：

```json
{
  "profileId": 1,
  "fullChatLog": "患者：我最近总是觉得头晕... AI：您好，头晕确实可能是..."
}
```

#### 4.1.3 响应数据

**格式**：`application/json`

| 参数名  | 类型     | 是否必填 | 备注   |
| ---- | ------ | ---- | ---- |
| code | number | 是    | 响应码  |
| msg  | string | 否    | 提示信息 |

**响应示例**：

```json
{
  "code": 1,
  "msg": "success"
}
```

---

## 5. 错误处理

### 5.1 全局错误处理

系统中出现任何异常或 Token 失效时，都会返回一个统一的错误格式。

**响应示例**：

```json
{
  "code": 0,
  "msg": "未登录或Token已失效"
}
```
