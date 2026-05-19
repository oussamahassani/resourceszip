import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate, useParams } from 'react-router-dom'
import { fetchAbonnementById, updateAbonnement, reset } from '../../redux/slices/abonnementsSlice'
import PageHeader from '../../components/common/PageHeader'
import FormField from '../../components/common/FormField'
import Alert from '../../components/common/Alert'
import Loader from '../../components/common/Loader'
import referentielsService from '../../services/referentielsService'

const EditAbonnement = () => {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { id } = useParams()
  const { current, isLoading, isSuccess, isError, message } = useSelector((state) => state.abonnements)

  const [offres, setOffres] = useState([])
  const [packs, setPacks] = useState([])
  const [zones, setZones] = useState([])
  const [typesPaiement, setTypesPaiement] = useState([])
  const [statuts, setStatuts] = useState([])

  const [formData, setFormData] = useState({
    offreId: '', packId: '', zoneId: '', typePaiementId: '',
    loginModem: '', serialModem: '', dateInstallation: '',
    statut: '', commentaire: '',
  })

  useEffect(() => {
    dispatch(fetchAbonnementById(id))
    referentielsService.getOffres().then(setOffres).catch(() => {})
    referentielsService.getPacks().then(setPacks).catch(() => {})
    referentielsService.getZones().then(setZones).catch(() => {})
    referentielsService.getTypesPaiement().then(setTypesPaiement).catch(() => {})
    referentielsService.getStatuts().then(setStatuts).catch(() => {})
    return () => dispatch(reset())
  }, [dispatch, id])

  useEffect(() => {
    if (current) {
      setFormData({
        offreId: current.offre?.offreId || current.offreId || '',
        packId: current.pack?.packId || current.packId || '',
        zoneId: current.zone?.zoneId || current.zoneId || '',
        typePaiementId: current.typePaiement?.id || current.typePaiementId || '',
        loginModem: current.loginModem || '',
        serialModem: current.serialModem || '',
        dateInstallation: current.dateInstallation ? current.dateInstallation.split('T')[0] : '',
        statut: current.statut || '',
        commentaire: current.commentaire || '',
      })
    }
  }, [current])

  useEffect(() => {
    if (isSuccess) navigate(`/demandeabonnement/getdemandeabonnement/${id}`)
  }, [isSuccess, navigate, id])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    dispatch(updateAbonnement({ id, data: formData }))
  }

  if (!current && isLoading) return <Loader />

  return (
    <section className="content">
      <PageHeader
        title="Modifier abonnement"
        breadcrumb={[{ label: 'Abonnements', path: '/demandeabonnement/alldemandesabonnement/1/20' }, { label: 'Modifier' }]}
      />
      <div className="container-fluid">
        <div className="card shadow">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>
              Demande #{current?.reference || id}
            </h6>
          </div>
          <div className="card-body">
            {isError && <Alert type="danger" message={message} />}
            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6">
                  <FormField label="Statut" name="statut" type="select" value={formData.statut} onChange={handleChange}>
                    <option value="">Choisir un statut</option>
                    {statuts.map(s => <option key={s.id} value={s.statutName || s.name}>{s.statutName || s.name}</option>)}
                  </FormField>
                </div>
                <div className="col-md-6">
                  <FormField label="Offre" name="offreId" type="select" value={formData.offreId} onChange={handleChange}>
                    <option value="">Choisir une offre</option>
                    {offres.map(o => <option key={o.offreId || o.id} value={o.offreId || o.id}>{o.offreName || o.name}</option>)}
                  </FormField>
                </div>
                <div className="col-md-6">
                  <FormField label="Pack" name="packId" type="select" value={formData.packId} onChange={handleChange}>
                    <option value="">Choisir un pack</option>
                    {packs.map(p => <option key={p.packId || p.id} value={p.packId || p.id}>{p.packName || p.name}</option>)}
                  </FormField>
                </div>
                <div className="col-md-6">
                  <FormField label="Zone" name="zoneId" type="select" value={formData.zoneId} onChange={handleChange}>
                    <option value="">Choisir une zone</option>
                    {zones.map(z => <option key={z.zoneId || z.id} value={z.zoneId || z.id}>{z.zoneName || z.name}</option>)}
                  </FormField>
                </div>
                <div className="col-md-6">
                  <FormField label="Login Modem" name="loginModem" value={formData.loginModem} onChange={handleChange} />
                </div>
                <div className="col-md-6">
                  <FormField label="N° Série Modem" name="serialModem" value={formData.serialModem} onChange={handleChange} />
                </div>
                <div className="col-md-6">
                  <FormField label="Date d'installation" name="dateInstallation" type="date" value={formData.dateInstallation} onChange={handleChange} />
                </div>
                <div className="col-12">
                  <FormField label="Commentaire" name="commentaire" type="textarea" value={formData.commentaire} onChange={handleChange} rows={3} />
                </div>
              </div>
              <div className="mt-3">
                <button type="submit" className="btn btn-primary mr-2" disabled={isLoading}>
                  {isLoading ? 'Mise à jour...' : 'Mettre à jour'}
                </button>
                <button type="button" className="btn btn-secondary" onClick={() => navigate(-1)}>Annuler</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </section>
  )
}

export default EditAbonnement
