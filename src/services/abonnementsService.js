import axiosInstance from '../api/axiosInstance'

const getAll = async (params = {}) => {
  const response = await axiosInstance.get('/api/demandeabonnement', { params })
  return response.data
}

const getById = async (id) => {
  const response = await axiosInstance.get(`/api/demandeabonnement/${id}`)
  return response.data
}

const create = async (data) => {
  const response = await axiosInstance.post('/api/demandeabonnement', data)
  return response.data
}

const update = async (id, data) => {
  const response = await axiosInstance.put(`/api/demandeabonnement/${id}`, data)
  return response.data
}

const getNonSignees = async (params = {}) => {
  const response = await axiosInstance.get('/api/demandeabonnement/non-signees', { params })
  return response.data
}

const getEnCours = async (params = {}) => {
  const response = await axiosInstance.get('/api/demandeabonnement/en-cours', { params })
  return response.data
}

const getTraites = async (params = {}) => {
  const response = await axiosInstance.get('/api/demandeabonnement/traites', { params })
  return response.data
}

const getValides = async (params = {}) => {
  const response = await axiosInstance.get('/api/demandeabonnement/valides', { params })
  return response.data
}

const recherche = async (params = {}) => {
  const response = await axiosInstance.get('/api/demandeabonnement/recherche', { params })
  return response.data
}

const verifierCIN = async (cin) => {
  const response = await axiosInstance.get(`/api/demandeabonnement/verify-cin/${cin}`)
  return response.data
}

const abonnementsService = {
  getAll,
  getById,
  create,
  update,
  getNonSignees,
  getEnCours,
  getTraites,
  getValides,
  recherche,
  verifierCIN,
}
export default abonnementsService
