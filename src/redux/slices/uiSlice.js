import { createSlice } from '@reduxjs/toolkit'

const uiSlice = createSlice({
  name: 'ui',
  initialState: {
    sidebarCollapsed: false,
    loading: false,
    globalError: null,
  },
  reducers: {
    toggleSidebar: (state) => {
      state.sidebarCollapsed = !state.sidebarCollapsed
    },
    setSidebarCollapsed: (state, action) => {
      state.sidebarCollapsed = action.payload
    },
    setLoading: (state, action) => {
      state.loading = action.payload
    },
    setGlobalError: (state, action) => {
      state.globalError = action.payload
    },
    clearGlobalError: (state) => {
      state.globalError = null
    },
  },
})

export const { toggleSidebar, setSidebarCollapsed, setLoading, setGlobalError, clearGlobalError } = uiSlice.actions
export default uiSlice.reducer
