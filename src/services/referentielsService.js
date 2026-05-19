import axiosInstance from '../api/axiosInstance'

const getGouvernorats = async () => {
  const response = await axiosInstance.get('/api/ville/gouvernorats')
  return response.data
}

const getVilles = async (gouvernoratId) => {
  const params = gouvernoratId ? { gouvernoratId } : {}
  const response = await axiosInstance.get('/api/ville/villes', { params })
  return response.data
}

const getZones = async () => {
  const response = await axiosInstance.get('/api/zone/allzones')
  return response.data
}

const getProfessions = async () => {
  const response = await axiosInstance.get('/api/profession/allprofessions')
  return response.data
}

const getOffres = async () => {
  const response = await axiosInstance.get('/api/offre/allOffres')
  return response.data
}

const getPacks = async () => {
  const response = await axiosInstance.get('/api/pack/allPack')
  return response.data
}

const getProduits = async () => {
  const response = await axiosInstance.get('/api/pi/allproduits')
  return response.data
}

const getCategories = async () => {
  const response = await axiosInstance.get('/api/pi/allcategories')
  return response.data
}

const getStatuts = async () => {
  const response = await axiosInstance.get('/api/statut/allstatus')
  return response.data
}

const getMotifs = async () => {
  const response = await axiosInstance.get('/api/motif/allmotifs')
  return response.data
}

const getTypesPaiement = async () => {
  const response = await axiosInstance.get('/api/typepaiement/alltypepaiements')
  return response.data
}

const getTypesVisite = async () => {
  const response = await axiosInstance.get('/api/typevisite/alltypevisites')
  return response.data
}

const getServiceTypes = async () => {
  const response = await axiosInstance.get('/api/servicetype/allservicetypes')
  return response.data
}

const referentielsService = {
  getGouvernorats,
  getVilles,
  getZones,
  getProfessions,
  getOffres,
  getPacks,
  getProduits,
  getCategories,
  getStatuts,
  getMotifs,
  getTypesPaiement,
  getTypesVisite,
  getServiceTypes,
}
export default referentielsService
