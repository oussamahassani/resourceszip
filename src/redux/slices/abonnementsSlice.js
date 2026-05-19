import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import abonnementsService from '../../services/abonnementsService'

export const fetchAbonnements = createAsyncThunk('abonnements/fetchAll', async (params, thunkAPI) => {
  try {
    return await abonnementsService.getAll(params)
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

export const fetchAbonnementById = createAsyncThunk('abonnements/fetchById', async (id, thunkAPI) => {
  try {
    return await abonnementsService.getById(id)
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

export const createAbonnement = createAsyncThunk('abonnements/create', async (data, thunkAPI) => {
  try {
    return await abonnementsService.create(data)
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

export const updateAbonnement = createAsyncThunk('abonnements/update', async ({ id, data }, thunkAPI) => {
  try {
    return await abonnementsService.update(id, data)
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

const abonnementsSlice = createSlice({
  name: 'abonnements',
  initialState: {
    list: [],
    current: null,
    pagination: { page: 0, size: 20, total: 0 },
    filters: {},
    isLoading: false,
    isError: false,
    isSuccess: false,
    message: '',
  },
  reducers: {
    reset: (state) => {
      state.isLoading = false
      state.isError = false
      state.isSuccess = false
      state.message = ''
    },
    setFilters: (state, action) => {
      state.filters = action.payload
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchAbonnements.pending, (state) => { state.isLoading = true })
      .addCase(fetchAbonnements.fulfilled, (state, action) => {
        state.isLoading = false
        state.list = action.payload.content || action.payload
        state.pagination = {
          page: action.payload.number || 0,
          size: action.payload.size || 20,
          total: action.payload.totalElements || 0,
        }
      })
      .addCase(fetchAbonnements.rejected, (state, action) => {
        state.isLoading = false
        state.isError = true
        state.message = action.payload
      })
      .addCase(fetchAbonnementById.fulfilled, (state, action) => {
        state.current = action.payload
      })
      .addCase(createAbonnement.fulfilled, (state) => { state.isSuccess = true })
      .addCase(updateAbonnement.fulfilled, (state) => { state.isSuccess = true })
  },
})

export const { reset, setFilters } = abonnementsSlice.actions
export default abonnementsSlice.reducer
