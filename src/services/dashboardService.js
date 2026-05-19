import axiosInstance from '../api/axiosInstance'

const getAdminStats = async () => {
  const response = await axiosInstance.get('/api/dashboard/admin/stats')
  return response.data
}

const getOtherStats = async () => {
  const response = await axiosInstance.get('/api/dashboard/other/stats')
  return response.data
}

const getChiffreAffaire = async ({ year, month } = {}) => {
  const params = {}
  if (year) params.year = year
  if (month) params.month = month
  const response = await axiosInstance.get('/api/dashboard/chiffre-affaire', { params })
  return response.data
}

const getEncaissement = async ({ year, month } = {}) => {
  const params = {}
  if (year) params.year = year
  if (month) params.month = month
  const response = await axiosInstance.get('/api/dashboard/encaissement', { params })
  return response.data
}

const getEncaissementPlafon = async () => {
  const response = await axiosInstance.get('/payement/calculeencaissement')
  return response.data
}

const getTechnoRepartition = async () => {
  const response = await axiosInstance.get('/api/dashboard/techno-repartition')
  return response.data
}

const getBordereauStatus = async () => {
  const response = await axiosInstance.get('/api/dashboard/bordereau-status')
  return response.data
}

const getAbonnementsByGouvernorat = async (params = {}) => {
  const response = await axiosInstance.get('/api/dashboard/abonnements-by-gouvernorat', { params })
  return response.data
}

const getTopRevendeurs = async (filter = 'all') => {
  const response = await axiosInstance.get('/api/dashboard/top-revendeurs', { params: { filter } })
  return response.data
}

const dashboardService = {
  getAdminStats,
  getOtherStats,
  getChiffreAffaire,
  getEncaissement,
  getEncaissementPlafon,
  getTechnoRepartition,
  getBordereauStatus,
  getAbonnementsByGouvernorat,
  getTopRevendeurs,
}
export default dashboardService
