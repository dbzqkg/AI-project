import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'

const app = createApp(App)

// 关键点 1：必须对应你的 SpringBoot 端口
axios.defaults.baseURL = 'http://localhost:8080'

// 关键点 2：必须对应 AuthAspect.java 中的 request.getHeader("token")
axios.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers['token'] = token
    }
    return config
})

app.use(ElementPlus)
app.mount('#app')