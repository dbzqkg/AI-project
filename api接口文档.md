好的，我明白了！以下是基于您的需求，专门为企业级标准打造的中文接口文档。文档涵盖了完整的功能、请求和响应数据等内容。



---



\# AI健康顾问平台接口文档



\## 1. 认证接口



\### 1.1 登录接口



\#### 1.1.1 基本信息



> \*\*请求路径\*\*：`/api/auth/login`

>

> \*\*请求方式\*\*：`POST`

>

> \*\*接口描述\*\*：用于用户登录，成功后返回JWT令牌。



\#### 1.1.2 请求参数



\*\*格式\*\*：`application/json`



| 参数名      | 类型     | 是否必填 | 备注  |

| -------- | ------ | ---- | --- |

| username | string | 是    | 用户名 |

| password | string | 是    | 密码  |



\*\*请求示例\*\*：



```json

{

&nbsp; "username": "johndoe",

&nbsp; "password": "password123"

}

```



\#### 1.1.3 响应数据



\*\*格式\*\*：`application/json`



| 参数名  | 类型     | 是否必填 | 备注             |

| ---- | ------ | ---- | -------------- |

| code | number | 是    | 响应码（1为成功，0为失败） |

| msg  | string | 否    | 提示信息           |

| data | object | 是    | 用户信息及JWT令牌     |



\*\*响应示例\*\*：



```json

{

&nbsp; "code": 1,

&nbsp; "msg": "success",

&nbsp; "data": {

&nbsp;   "id": 1,

&nbsp;   "username": "johndoe",

&nbsp;   "name": "John Doe",

&nbsp;   "token": "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MX0.6O4gVwbb...OlvbhgD7z"

&nbsp; }

}

```



\### 1.2 获取用户的所有患者档案



\#### 1.2.1 基本信息



> \*\*请求路径\*\*：`/api/auth/profiles/{userId}`

>

> \*\*请求方式\*\*：`GET`

>

> \*\*接口描述\*\*：查询用户的所有患者档案。



\#### 1.2.2 请求参数



\*\*路径参数\*\*：



| 参数名    | 类型     | 是否必填 | 备注   |

| ------ | ------ | ---- | ---- |

| userId | number | 是    | 用户ID |



\#### 1.2.3 响应数据



\*\*格式\*\*：`application/json`



| 参数名  | 类型       | 是否必填 | 备注     |

| ---- | -------- | ---- | ------ |

| code | number   | 是    | 响应码    |

| msg  | string   | 否    | 提示信息   |

| data | object\[] | 是    | 患者档案列表 |



\*\*响应示例\*\*：



```json

{

&nbsp; "code": 1,

&nbsp; "msg": "success",

&nbsp; "data": \[

&nbsp;   {

&nbsp;     "id": 1,

&nbsp;     "realName": "Jane Doe",

&nbsp;     "gender": "Female",

&nbsp;     "age": 30,

&nbsp;     "medicalHistory": "No significant history",

&nbsp;     "aiSummary": "Healthy individual"

&nbsp;   }

&nbsp; ]

}

```



---



\## 2. 患者档案管理



\### 2.1 新增患者档案



\#### 2.1.1 基本信息



> \*\*请求路径\*\*：`/api/profiles`

>

> \*\*请求方式\*\*：`POST`

>

> \*\*接口描述\*\*：添加新的患者档案。



\#### 2.1.2 请求参数



\*\*格式\*\*：`application/json`



| 参数名            | 类型     | 是否必填 | 备注        |

| -------------- | ------ | ---- | --------- |

| realName       | string | 是    | 患者姓名      |

| age            | number | 是    | 患者年龄      |

| gender         | string | 是    | 性别（男/女）   |

| medicalHistory | string | 否    | 既往病史      |

| aiSummary      | string | 否    | AI生成的病历总结 |



\*\*请求示例\*\*：



```json

{

&nbsp; "realName": "John Smith",

&nbsp; "age": 45,

&nbsp; "gender": "Male",

&nbsp; "medicalHistory": "Diabetes",

&nbsp; "aiSummary": "Patient needs regular insulin intake"

}

```



\#### 2.1.3 响应数据



\*\*格式\*\*：`application/json`



| 参数名  | 类型     | 是否必填 | 备注     |

| ---- | ------ | ---- | ------ |

| code | number | 是    | 响应码    |

| msg  | string | 否    | 提示信息   |

| data | object | 是    | 患者档案信息 |



\*\*响应示例\*\*：



```json

{

&nbsp; "code": 1,

&nbsp; "msg": "success",

&nbsp; "data": {

&nbsp;   "id": 2,

&nbsp;   "realName": "John Smith",

&nbsp;   "age": 45,

&nbsp;   "gender": "Male",

&nbsp;   "medicalHistory": "Diabetes",

&nbsp;   "aiSummary": "Patient needs regular insulin intake"

&nbsp; }

}

```



\### 2.2 更新患者档案



\#### 2.2.1 基本信息



> \*\*请求路径\*\*：`/api/profiles/{id}`

>

> \*\*请求方式\*\*：`PUT`

>

> \*\*接口描述\*\*：更新已有患者档案信息。



\#### 2.2.2 请求参数



\*\*格式\*\*：`application/json`



| 参数名            | 类型     | 是否必填 | 备注        |

| -------------- | ------ | ---- | --------- |

| realName       | string | 是    | 患者姓名      |

| age            | number | 是    | 患者年龄      |

| gender         | string | 是    | 性别（男/女）   |

| medicalHistory | string | 否    | 既往病史      |

| aiSummary      | string | 否    | AI生成的病历总结 |



\#### 2.2.3 响应数据



\*\*格式\*\*：`application/json`



| 参数名  | 类型     | 是否必填 | 备注       |

| ---- | ------ | ---- | -------- |

| code | number | 是    | 响应码      |

| msg  | string | 否    | 提示信息     |

| data | object | 是    | 更新后的患者信息 |



---



\## 3. AI 聊天交互



\### 3.1 健康问题咨询



\#### 3.1.1 基本信息



> \*\*请求路径\*\*：`/api/ai/chat`

>

> \*\*请求方式\*\*：`POST`

>

> \*\*接口描述\*\*：向AI咨询健康相关问题，传入患者的背景信息。



\#### 3.1.2 请求参数



\*\*格式\*\*：`application/json`



| 参数名       | 类型     | 是否必填 | 备注      |

| --------- | ------ | ---- | ------- |

| profileId | number | 是    | 患者档案ID  |

| question  | string | 是    | 用户提出的问题 |



\*\*请求示例\*\*：



```json

{

&nbsp; "profileId": 1,

&nbsp; "question": "What should I do for diabetes?"

}

```



\#### 3.1.3 响应数据



\*\*格式\*\*：`application/json`



| 参数名  | 类型     | 是否必填 | 备注    |

| ---- | ------ | ---- | ----- |

| code | number | 是    | 响应码   |

| msg  | string | 否    | 提示信息  |

| data | object | 是    | AI的回答 |



\*\*响应示例\*\*：



```json

{

&nbsp; "code": 1,

&nbsp; "msg": "success",

&nbsp; "data": {

&nbsp;   "answer": "For diabetes, it's essential to monitor blood sugar levels regularly, and you may need insulin or oral medication as prescribed by your healthcare provider."

&nbsp; }

}

```



---



\## 4. AI总结生成



\### 4.1 生成病历总结



\#### 4.1.1 基本信息



> \*\*请求路径\*\*：`/api/ai/save-summary`

>

> \*\*请求方式\*\*：`POST`

>

> \*\*接口描述\*\*：基于与AI的对话记录生成病历总结并更新患者档案。



\#### 4.1.2 请求参数



\*\*格式\*\*：`application/json`



| 参数名         | 类型     | 是否必填 | 备注      |

| ----------- | ------ | ---- | ------- |

| profileId   | number | 是    | 患者档案ID  |

| fullChatLog | string | 是    | 完整的对话记录 |



\*\*请求示例\*\*：



```json

{

&nbsp; "profileId": 1,

&nbsp; "fullChatLog": "Patient has diabetes and requires regular insulin intake."

}

```



\#### 4.1.3 响应数据



\*\*格式\*\*：`application/json`



| 参数名  | 类型     | 是否必填 | 备注     |

| ---- | ------ | ---- | ------ |

| code | number | 是    | 响应码    |

| msg  | string | 否    | 提示信息   |

| data | object | 是    | AI总结结果 |



\*\*响应示例\*\*：



```json

{

&nbsp; "code": 1,

&nbsp; "msg": "success",

&nbsp; "data": {

&nbsp;   "summary": "Patient has diabetes and needs regular insulin intake."

&nbsp; }

}

```



---



\## 5. 错误处理



\### 5.1 全局错误处理



系统中出现任何异常时，都会返回一个统一的错误格式。



\*\*响应示例\*\*：



```json

{

&nbsp; "code": 0,

&nbsp; "msg": "系统发生异常，请联系管理员"

}

```



---



这份文档已根据您的需求进行详细规划，并遵循了企业级接口文档标准。每个API接口都提供了详细的请求参数和响应格式，适用于前后端开发的合作。



