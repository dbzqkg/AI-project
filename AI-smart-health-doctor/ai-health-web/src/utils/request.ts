import axios from 'axios'
import { ElMessage } from 'element-plus'

const instance = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 60000
})

instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['token'] = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  (response) => {
    const res = response.data
    // 如果是流式响应或其他非标准JSON结构(虽然流式一般走fetch)，这里主要处理Result结构
    // 后端Result结构: { code: 1, msg: "success", data: ... }

    // 特定情况：后端有些接口可能直接返回数据而非Result封装（如流式），但axios主要用于普通接口
    // 这里假设普通接口都遵循 Result 结构

    if (res.code === 0 && res.msg === 'NOT_LOGIN') {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      window.location.href = '/login'
      return Promise.reject(new Error('NOT_LOGIN'))
    }

    // 后端 error 方法 code 为 0
    if (res.code === 0) {
      ElMessage.error(res.msg || 'Error')
      return Promise.reject(new Error(res.msg || 'Error'))
    }

    // 成功
    return res
  },
  (error) => {
    console.error('Request Error:', error)
    if (error.response?.status === 401) {
       ElMessage.error('Unauthorized')
       localStorage.removeItem('token')
       window.location.href = '/login'
    } else {
        ElMessage.error(error.message || 'Network Error')
    }
    return Promise.reject(error)
  }
)

export default instance

