import axiosInstance from '../api/axiosInstance'

const getAll = async (params = {}) => {
  const response = await axiosInstance.get('/api/client/allclients', { params })
  return response.data
}

const getById = async (id) => {
  const response = await axiosInstance.get(`/api/client/${id}`)
  return response.data
}

const create = async (data) => {
  const response = await axiosInstance.post('/api/client', data)
  return response.data
}

const update = async (id, data) => {
  const response = await axiosInstance.put(`/api/client/${id}`, data)
  return response.data
}

const getActive = async (params = {}) => {
  const response = await axiosInstance.get('/api/client/active', { params })
  return response.data
}

const getResilie = async (params = {}) => {
  const response = await axiosInstance.get('/api/client/resilie', { params })
  return response.data
}

const getNonConnecter = async (params = {}) => {
  const response = await axiosInstance.get('/api/client/non-connecte', { params })
  return response.data
}

const getEnRecouvrement = async (params = {}) => {
  const response = await axiosInstance.get('/api/client/recouvrement', { params })
  return response.data
}

const exportList = async (params = {}) => {
  const response = await axiosInstance.get('/api/client/export', {
    params,
    responseType: 'blob',
  })
  return response.data
}

const clientsService = {
  getAll,
  getById,
  create,
  update,
  getActive,
  getResilie,
  getNonConnecter,
  getEnRecouvrement,
  exportList,
}
export default clientsService
