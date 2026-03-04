import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'
import type { LoginInfo, User } from '@/types'

export const useUserStore = defineStore('user', () => {
    const token = ref<string>(localStorage.getItem('token') || '')
    const username = ref<string>(localStorage.getItem('username') || '')

    function setLoginInfo(info: LoginInfo) {
        token.value = info.token
        username.value = info.username
        localStorage.setItem('token', info.token)
        localStorage.setItem('username', info.username)
    }

    function logout() {
        token.value = ''
        username.value = ''
        localStorage.removeItem('token')
        localStorage.removeItem('username')
    }

    async function login(loginForm: User) {
        const res: any = await request.post('/api/auth/login', loginForm)
        // LoginController returns success(null) on failure, so check data
        if (res.code === 1 && res.data) {
            setLoginInfo(res.data)
            return true
        }
        return false
    }

    async function register(registerForm: User) {
        const res: any = await request.post('/api/auth/register', registerForm)
        return res.code === 1
    }

    return { token, username, login, logout, register }
})