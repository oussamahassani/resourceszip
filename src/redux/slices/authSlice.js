import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import authService from '../../services/authService'

const user = JSON.parse(localStorage.getItem('user'))
const token = localStorage.getItem('token')

export const login = createAsyncThunk('auth/login', async (credentials, thunkAPI) => {
  try {
    return await authService.login(credentials)
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Erreur de connexion'
    return thunkAPI.rejectWithValue(message)
  }
})

export const logout = createAsyncThunk('auth/logout', async () => {
  await authService.logout()
})

export const refreshToken = createAsyncThunk('auth/refreshToken', async (_, thunkAPI) => {
  try {
    return await authService.refreshToken()
  } catch (error) {
    return thunkAPI.rejectWithValue('Session expirée')
  }
})

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    user: user || null,
    token: token || null,
    isLoading: false,
    isSuccess: false,
    isError: false,
    message: '',
  },
  reducers: {
    reset: (state) => {
      state.isLoading = false
      state.isSuccess = false
      state.isError = false
      state.message = ''
    },
    setUser: (state, action) => {
      state.user = action.payload
    },
    setToken: (state, action) => {
      state.token = action.payload
      localStorage.setItem('token', action.payload)
    },
    clearAuth: (state) => {
      state.user = null
      state.token = null
      localStorage.removeItem('user')
      localStorage.removeItem('token')
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.isLoading = true
        state.isError = false
        state.message = ''
      })
      .addCase(login.fulfilled, (state, action) => {
        state.isLoading = false
        state.isSuccess = true
        state.user = action.payload.user
        state.token = action.payload.token
        localStorage.setItem('user', JSON.stringify(action.payload.user))
        localStorage.setItem('token', action.payload.token)
      })
      .addCase(login.rejected, (state, action) => {
        state.isLoading = false
        state.isError = true
        state.message = action.payload
        state.user = null
        state.token = null
      })
      .addCase(logout.fulfilled, (state) => {
        state.user = null
        state.token = null
        localStorage.removeItem('user')
        localStorage.removeItem('token')
      })
      .addCase(refreshToken.fulfilled, (state, action) => {
        state.token = action.payload.token
        localStorage.setItem('token', action.payload.token)
      })
      .addCase(refreshToken.rejected, (state) => {
        state.user = null
        state.token = null
        localStorage.removeItem('user')
        localStorage.removeItem('token')
      })
  },
})

export const { reset, setUser, setToken, clearAuth } = authSlice.actions
export default authSlice.reducer
