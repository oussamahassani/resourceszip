import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import FormField from '../../components/common/FormField'
import Alert from '../../components/common/Alert'
import Loader from '../../components/common/Loader'
import axiosInstance from '../../api/axiosInstance'
import referentielsService from '../../services/referentielsService'

const EditReclamation = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)
  const [reclamation, setReclamation] = useState(null)
  const [motifs, setMotifs] = useState([])
  const [formData, setFormData] = useState({
    statut: '', motifId: '', description: '', resolution: '', dateIntervention: '',
  })

  useEffect(() => {
    setLoading(true)
    axiosInstance.get(`/api/reclamation/view/${id}`)
      .then(res => {
        const r = res.data
        setReclamation(r)
        setFormData({
          statut: r.statut || '',
          motifId: r.motif?.id || r.motifId || '',
          description: r.description || '',
          resolution: r.resolution || '',
          dateIntervention: r.dateIntervention ? r.dateIntervention.split('T')[0] : '',
        })
      })
      .catch(() => setError('Erreur de chargement'))
      .finally(() => setLoading(false))

    referentielsService.getMotifs().then(setMotifs).catch(() => {})
  }, [id])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      await axiosInstance.put(`/api/reclamation/edit/${id}`, formData)
      navigate(`/reclamation/view/${id}`)
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la mise à jour')
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <Loader />

  return (
    <section className="content">
      <PageHeader title="Modifier réclamation"
        breadcrumb={[{ label: 'Réclamations', path: '/reclamation/allreclamations' }, { label: 'Modifier' }]} />
      <div className="container-fluid">
        <div className="card shadow">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Réclamation #{reclamation?.numeroReclamation || id}</h6>
          </div>
          <div className="card-body">
            {error && <Alert type="danger" message={error} />}
            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6">
                  <FormField label="Statut" name="statut" type="select" value={formData.statut} onChange={handleChange}>
                    <option value="">Choisir un statut</option>
                    <option value="Nouveau">Nouveau</option>
                    <option value="En cours">En cours</option>
                    <option value="Résolu">Résolu</option>
                    <option value="Fermé">Fermé</option>
                  </FormField>
                </div>
                <div className="col-md-6">
                  <FormField label="Motif" name="motifId" type="select" value={formData.motifId} onChange={handleChange}>
                    <option value="">Choisir un motif</option>
                    {motifs.map(m => <option key={m.id || m.motifId} value={m.id || m.motifId}>{m.motifName || m.name}</option>)}
                  </FormField>
                </div>
                <div className="col-md-6">
                  <FormField label="Date d'intervention" name="dateIntervention" type="date" value={formData.dateIntervention} onChange={handleChange} />
                </div>
                <div className="col-12">
                  <FormField label="Description" name="description" type="textarea" value={formData.description} onChange={handleChange} rows={3} />
                </div>
                <div className="col-12">
                  <FormField label="Résolution" name="resolution" type="textarea" value={formData.resolution} onChange={handleChange} rows={3} />
                </div>
              </div>
              <div className="mt-3">
                <button type="submit" className="btn btn-primary mr-2" disabled={saving}>
                  {saving ? 'Mise à jour...' : 'Mettre à jour'}
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

export default EditReclamation
