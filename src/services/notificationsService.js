import axiosInstance from '../api/axiosInstance'

const getAll = async () => {
  const response = await axiosInstance.get('/api/notifications')
  return response.data
}

const getConfig = async () => {
  const response = await axiosInstance.get('/api/notifications/config')
  return response.data
}

const create = async (data) => {
  const response = await axiosInstance.post('/api/notifications', data)
  return response.data
}

const update = async (id, data) => {
  const response = await axiosInstance.put(`/api/notifications/${id}`, data)
  return response.data
}

const notificationsService = { getAll, getConfig, create, update }
export default notificationsService
