import axiosInstance from '../api/axiosInstance'

const getAll = async (params = {}) => {
  const response = await axiosInstance.get('/api/admin/users', { params })
  return response.data
}

const getSystemUsers = async (params = {}) => {
  const response = await axiosInstance.get('/api/admin/usersSystem/getusers', { params })
  return response.data
}

const getById = async (id) => {
  const response = await axiosInstance.get(`/api/admin/users/${id}`)
  return response.data
}

const create = async (data) => {
  const response = await axiosInstance.post('/api/admin/users', data)
  return response.data
}

const update = async (id, data) => {
  const response = await axiosInstance.put(`/api/admin/users/${id}`, data)
  return response.data
}

const deleteUser = async (id) => {
  await axiosInstance.delete(`/api/admin/users/${id}`)
}

const getRevendeurs = async (params = {}) => {
  const response = await axiosInstance.get('/api/admin/revendeurs', { params })
  return response.data
}

const getRevendeurById = async (id) => {
  const response = await axiosInstance.get(`/api/admin/revendeurs/${id}`)
  return response.data
}

const getHistoriqueUser = async (id) => {
  const response = await axiosInstance.get(`/api/admin/users/${id}/historique`)
  return response.data
}

const getRoles = async () => {
  const response = await axiosInstance.get('/api/role/allroles')
  return response.data
}

const getPrivileges = async () => {
  const response = await axiosInstance.get('/api/role/allprivileges')
  return response.data
}

const usersService = {
  getAll,
  getSystemUsers,
  getById,
  create,
  update,
  delete: deleteUser,
  getRevendeurs,
  getRevendeurById,
  getHistoriqueUser,
  getRoles,
  getPrivileges,
}
export default usersService
