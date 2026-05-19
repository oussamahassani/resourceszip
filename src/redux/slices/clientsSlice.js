import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import clientsService from '../../services/clientsService'

export const fetchClients = createAsyncThunk('clients/fetchAll', async (params, thunkAPI) => {
  try {
    return await clientsService.getAll(params)
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

export const fetchClientById = createAsyncThunk('clients/fetchById', async (id, thunkAPI) => {
  try {
    return await clientsService.getById(id)
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

export const createClient = createAsyncThunk('clients/create', async (data, thunkAPI) => {
  try {
    return await clientsService.create(data)
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

export const updateClient = createAsyncThunk('clients/update', async ({ id, data }, thunkAPI) => {
  try {
    return await clientsService.update(id, data)
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

const clientsSlice = createSlice({
  name: 'clients',
  initialState: {
    list: [],
    current: null,
    pagination: { page: 1, size: 20, total: 0 },
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
      .addCase(fetchClients.pending, (state) => { state.isLoading = true })
      .addCase(fetchClients.fulfilled, (state, action) => {
        state.isLoading = false
        state.list = action.payload.content || action.payload
        state.pagination = {
          page: action.payload.number || 0,
          size: action.payload.size || 20,
          total: action.payload.totalElements || 0,
        }
      })
      .addCase(fetchClients.rejected, (state, action) => {
        state.isLoading = false
        state.isError = true
        state.message = action.payload
      })
      .addCase(fetchClientById.fulfilled, (state, action) => {
        state.current = action.payload
      })
      .addCase(createClient.fulfilled, (state) => {
        state.isSuccess = true
      })
      .addCase(updateClient.fulfilled, (state) => {
        state.isSuccess = true
      })
  },
})

export const { reset, setFilters } = clientsSlice.actions
export default clientsSlice.reducer
