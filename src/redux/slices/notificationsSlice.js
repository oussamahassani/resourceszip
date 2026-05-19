import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import notificationsService from '../../services/notificationsService'

export const fetchNotifications = createAsyncThunk('notifications/fetchAll', async (_, thunkAPI) => {
  try {
    return await notificationsService.getAll()
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

const notificationsSlice = createSlice({
  name: 'notifications',
  initialState: {
    list: [],
    unreadCount: 0,
    isLoading: false,
    isError: false,
  },
  reducers: {
    markAsRead: (state, action) => {
      const notif = state.list.find(n => n.id === action.payload)
      if (notif) {
        notif.read = true
        state.unreadCount = Math.max(0, state.unreadCount - 1)
      }
    },
    markAllAsRead: (state) => {
      state.list.forEach(n => { n.read = true })
      state.unreadCount = 0
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchNotifications.pending, (state) => { state.isLoading = true })
      .addCase(fetchNotifications.fulfilled, (state, action) => {
        state.isLoading = false
        state.list = action.payload
        state.unreadCount = action.payload.filter(n => !n.read).length
      })
      .addCase(fetchNotifications.rejected, (state) => { state.isLoading = false })
  },
})

export const { markAsRead, markAllAsRead } = notificationsSlice.actions
export default notificationsSlice.reducer
