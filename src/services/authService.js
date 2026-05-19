import axiosInstance from '../api/axiosInstance'

const login = async (credentials) => {
  const response = await axiosInstance.post('/api/auth/login', {
    username: credentials.username,
    password: credentials.password,
  })

  const data = response.data
  if (!data.success) {
    throw new Error(data.message || 'Erreur de connexion')
  }

  return {
    token: data.data.token,
    refreshToken: data.data.refreshToken,
    user: data.data.user,
  }
}

const logout = async () => {
  try {
    await axiosInstance.post('/api/auth/logout')
  } finally {
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
  }
}

const refreshToken = async () => {
  const token = localStorage.getItem('refreshToken')
  const response = await axiosInstance.post('/api/auth/refresh', {}, {
    headers: { Authorization: `Bearer ${token}` },
  })
  const data = response.data
  return {
    token: data.data?.token || data.token,
    refreshToken: data.data?.refreshToken || data.refreshToken,
  }
}

const getCurrentUser = async () => {
  const response = await axiosInstance.get('/api/auth/me')
  const data = response.data
  if (data.success) {
    return data.data
  }
  return data.user || data
}

const authService = { login, logout, refreshToken, getCurrentUser }
export default authService
