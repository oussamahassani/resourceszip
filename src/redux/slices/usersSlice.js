import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import usersService from '../../services/usersService'

export const fetchUsers = createAsyncThunk('users/fetchAll', async (params, thunkAPI) => {
  try {
    return await usersService.getAll(params)
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

export const fetchUserById = createAsyncThunk('users/fetchById', async (id, thunkAPI) => {
  try {
    return await usersService.getById(id)
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

export const createUser = createAsyncThunk('users/create', async (data, thunkAPI) => {
  try {
    return await usersService.create(data)
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

export const updateUser = createAsyncThunk('users/update', async ({ id, data }, thunkAPI) => {
  try {
    return await usersService.update(id, data)
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

export const deleteUser = createAsyncThunk('users/delete', async (id, thunkAPI) => {
  try {
    await usersService.delete(id)
    return id
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data?.message || error.message)
  }
})

const usersSlice = createSlice({
  name: 'users',
  initialState: {
    list: [],
    current: null,
    pagination: { page: 1, size: 20, total: 0 },
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
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchUsers.pending, (state) => { state.isLoading = true })
      .addCase(fetchUsers.fulfilled, (state, action) => {
        state.isLoading = false
        state.list = action.payload.content || action.payload
        state.pagination = {
          page: action.payload.number || 0,
          size: action.payload.size || 20,
          total: action.payload.totalElements || 0,
        }
      })
      .addCase(fetchUsers.rejected, (state, action) => {
        state.isLoading = false
        state.isError = true
        state.message = action.payload
      })
      .addCase(fetchUserById.fulfilled, (state, action) => {
        state.current = action.payload
      })
      .addCase(createUser.fulfilled, (state, action) => {
        state.isSuccess = true
        state.list.push(action.payload)
      })
      .addCase(updateUser.fulfilled, (state, action) => {
        state.isSuccess = true
        const idx = state.list.findIndex(u => u.userid === action.payload.userid)
        if (idx !== -1) state.list[idx] = action.payload
      })
      .addCase(deleteUser.fulfilled, (state, action) => {
        state.isSuccess = true
        state.list = state.list.filter(u => u.userid !== action.payload)
      })
  },
})

export const { reset } = usersSlice.actions
export default usersSlice.reducer
