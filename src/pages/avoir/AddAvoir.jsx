import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import FormField from '../../components/common/FormField'
import Alert from '../../components/common/Alert'
import axiosInstance from '../../api/axiosInstance'

const AddAvoir = () => {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [formData, setFormData] = useState({
    clientId: '', factureid: '', montant: '', motif: '', commentaire: '',
  })

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await axiosInstance.post('/api/avoir/addNewAvoir', formData)
      navigate('/AvoirClient/AllAvoirClientNotPublic')
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la création')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="content">
      <PageHeader title="Créer un avoir" breadcrumb={[{ label: 'Avoirs', path: '/AvoirClient/AllAvoirClient' }, { label: 'Créer' }]} />
      <div className="container-fluid">
        <div className="card shadow">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Nouvelle facture d&apos;avoir</h6>
          </div>
          <div className="card-body">
            {error && <Alert type="danger" message={error} />}
            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6">
                  <FormField label="N° Facture / ID" name="factureid" value={formData.factureid} onChange={handleChange} required
                    placeholder="Entrez l'identifiant de la facture" />
                </div>
                <div className="col-md-6">
                  <FormField label="Montant (TND)" name="montant" type="number" value={formData.montant} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <FormField label="Motif" name="motif" value={formData.motif} onChange={handleChange} required />
                </div>
                <div className="col-12">
                  <FormField label="Commentaire" name="commentaire" type="textarea" value={formData.commentaire} onChange={handleChange} rows={3} />
                </div>
              </div>
              <div className="mt-3">
                <button type="submit" className="btn btn-primary mr-2" disabled={loading}>
                  {loading ? 'Création...' : 'Créer'}
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

export default AddAvoir
