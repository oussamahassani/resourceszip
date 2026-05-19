import axiosInstance from '../api/axiosInstance'

const login = async (credentials) => {
  const params = new URLSearchParams()
  params.append('username', credentials.username)
  params.append('password', credentials.password)
  if (credentials.recaptchaToken) {
    params.append('recaptchaToken', credentials.recaptchaToken)
  }

  const response = await axiosInstance.post('/login', params, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  })

  if (response.data) {
    return response.data
  }
  return response
}

const logout = async () => {
  try {
    await axiosInstance.post('/logout')
  } finally {
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }
}

const refreshToken = async () => {
  const response = await axiosInstance.post('/api/auth/refresh')
  return response.data
}

const getCurrentUser = async () => {
  const response = await axiosInstance.get('/api/auth/me')
  return response.data
}

const authService = { login, logout, refreshToken, getCurrentUser }
export default authService
