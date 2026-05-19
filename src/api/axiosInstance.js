import axios from 'axios'
import { store } from '../redux/store'
import { clearAuth } from '../redux/slices/authSlice'

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8282',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
})

axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      try {
        const refreshToken = localStorage.getItem('refreshToken')
        if (!refreshToken) {
          store.dispatch(clearAuth())
          window.location.href = '/login'
          return Promise.reject(error)
        }
        const response = await axios.post('/api/auth/refresh', {}, {
          headers: { Authorization: `Bearer ${refreshToken}` },
          withCredentials: true,
        })
        const newToken = response.data?.data?.token || response.data?.token
        if (newToken) {
          localStorage.setItem('token', newToken)
          originalRequest.headers['Authorization'] = `Bearer ${newToken}`
          return axiosInstance(originalRequest)
        }
        throw new Error('No token in refresh response')
      } catch {
        store.dispatch(clearAuth())
        window.location.href = '/login'
      }
    }

    if (error.response?.status === 403) {
      window.location.href = '/error/403'
    }

    return Promise.reject(error)
  }
)

export default axiosInstance
