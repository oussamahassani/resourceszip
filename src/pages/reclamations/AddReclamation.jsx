import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import FormField from '../../components/common/FormField'
import Alert from '../../components/common/Alert'
import axiosInstance from '../../api/axiosInstance'
import referentielsService from '../../services/referentielsService'

const AddReclamation = () => {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [motifs, setMotifs] = useState([])
  const [typesVisite, setTypesVisite] = useState([])
  const [formData, setFormData] = useState({
    clientCin: '', clientId: '', reference: '',
    motifId: '', typeVisiteId: '', description: '',
    dateIntervention: '',
  })

  useEffect(() => {
    referentielsService.getMotifs().then(setMotifs).catch(() => {})
    referentielsService.getTypesVisite().then(setTypesVisite).catch(() => {})
  }, [])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await axiosInstance.post('/api/reclamation/addreclamation', formData)
      navigate('/reclamation/allreclamations')
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la création')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="content">
      <PageHeader title="Nouvelle réclamation" breadcrumb={[{ label: 'Réclamations', path: '/reclamation/allreclamations' }, { label: 'Nouveau' }]} />
      <div className="container-fluid">
        <div className="card shadow">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Nouvelle réclamation</h6>
          </div>
          <div className="card-body">
            {error && <Alert type="danger" message={error} />}
            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6">
                  <FormField label="CIN Client" name="clientCin" value={formData.clientCin} onChange={handleChange} required
                    placeholder="Saisir le CIN du client" />
                </div>
                <div className="col-md-6">
                  <FormField label="Référence abonnement" name="reference" value={formData.reference} onChange={handleChange}
                    placeholder="Référence de l'abonnement" />
                </div>
                <div className="col-md-6">
                  <FormField label="Motif" name="motifId" type="select" value={formData.motifId} onChange={handleChange} required>
                    <option value="">Choisir un motif</option>
                    {motifs.map(m => <option key={m.id || m.motifId} value={m.id || m.motifId}>{m.motifName || m.name}</option>)}
                  </FormField>
                </div>
                <div className="col-md-6">
                  <FormField label="Type de visite" name="typeVisiteId" type="select" value={formData.typeVisiteId} onChange={handleChange}>
                    <option value="">Choisir un type</option>
                    {typesVisite.map(t => <option key={t.id} value={t.id}>{t.typeVisiteName || t.name}</option>)}
                  </FormField>
                </div>
                <div className="col-md-6">
                  <FormField label="Date d'intervention" name="dateIntervention" type="date" value={formData.dateIntervention} onChange={handleChange} />
                </div>
                <div className="col-12">
                  <FormField label="Description" name="description" type="textarea" value={formData.description} onChange={handleChange} rows={4} required />
                </div>
              </div>
              <div className="mt-3">
                <button type="submit" className="btn btn-primary mr-2" disabled={loading}>
                  {loading ? 'Enregistrement...' : 'Enregistrer'}
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

export default AddReclamation
