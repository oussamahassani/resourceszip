import axios from 'axios'
import { store } from '../redux/store'
import { clearAuth } from '../redux/slices/authSlice'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

const axiosInstance = axios.create({
  baseURL: BASE_URL,
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
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content')
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content')
    if (csrfToken && csrfHeader) {
      config.headers[csrfHeader] = csrfToken
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
        const response = await axios.post(`${BASE_URL}/api/auth/refresh`, {}, { withCredentials: true })
        const newToken = response.data.token
        localStorage.setItem('token', newToken)
        originalRequest.headers['Authorization'] = `Bearer ${newToken}`
        return axiosInstance(originalRequest)
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
