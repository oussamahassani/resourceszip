import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import dashboardService from '../../services/dashboardService'

export const fetchAdminStats = createAsyncThunk('dashboard/fetchAdminStats', async (_, thunkAPI) => {
  try {
    return await dashboardService.getAdminStats()
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

export const fetchOtherStats = createAsyncThunk('dashboard/fetchOtherStats', async (_, thunkAPI) => {
  try {
    return await dashboardService.getOtherStats()
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

export const fetchChiffreAffaire = createAsyncThunk('dashboard/fetchChiffreAffaire', async (params, thunkAPI) => {
  try {
    return await dashboardService.getChiffreAffaire(params)
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

export const fetchEncaissement = createAsyncThunk('dashboard/fetchEncaissement', async (params, thunkAPI) => {
  try {
    return await dashboardService.getEncaissement(params)
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

const dashboardSlice = createSlice({
  name: 'dashboard',
  initialState: {
    adminStats: null,
    otherStats: null,
    chiffreAffaire: null,
    encaissement: null,
    isLoading: false,
    isError: false,
    message: '',
  },
  reducers: {
    reset: (state) => {
      state.isLoading = false
      state.isError = false
      state.message = ''
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchAdminStats.pending, (state) => { state.isLoading = true })
      .addCase(fetchAdminStats.fulfilled, (state, action) => {
        state.isLoading = false
        state.adminStats = action.payload
      })
      .addCase(fetchAdminStats.rejected, (state, action) => {
        state.isLoading = false
        state.isError = true
        state.message = action.payload
      })
      .addCase(fetchOtherStats.pending, (state) => { state.isLoading = true })
      .addCase(fetchOtherStats.fulfilled, (state, action) => {
        state.isLoading = false
        state.otherStats = action.payload
      })
      .addCase(fetchOtherStats.rejected, (state, action) => {
        state.isLoading = false
        state.isError = true
        state.message = action.payload
      })
      .addCase(fetchChiffreAffaire.fulfilled, (state, action) => {
        state.chiffreAffaire = action.payload
      })
      .addCase(fetchEncaissement.fulfilled, (state, action) => {
        state.encaissement = action.payload
      })
  },
})

export const { reset } = dashboardSlice.actions
export default dashboardSlice.reducer
