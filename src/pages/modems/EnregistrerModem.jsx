import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import FormField from '../../components/common/FormField'
import Alert from '../../components/common/Alert'
import axiosInstance from '../../api/axiosInstance'

const EnregistrerModem = () => {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [formData, setFormData] = useState({
    serialNumber: '', modele: '', marque: '', imei: '', macAddress: '',
    dateReception: '', fournisseur: '', commentaire: '',
  })

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await axiosInstance.post('/api/modem/enregistrermodem', formData)
      navigate('/modem/modems')
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de l\'enregistrement')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="content">
      <PageHeader title="Enregistrer un modem" breadcrumb={[{ label: 'Modems', path: '/modem/modems' }, { label: 'Enregistrer' }]} />
      <div className="container-fluid">
        <div className="card shadow">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Nouveau modem</h6>
          </div>
          <div className="card-body">
            {error && <Alert type="danger" message={error} />}
            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6">
                  <FormField label="N° Série" name="serialNumber" value={formData.serialNumber} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <FormField label="Modèle" name="modele" value={formData.modele} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <FormField label="Marque" name="marque" value={formData.marque} onChange={handleChange} />
                </div>
                <div className="col-md-6">
                  <FormField label="IMEI" name="imei" value={formData.imei} onChange={handleChange} />
                </div>
                <div className="col-md-6">
                  <FormField label="Adresse MAC" name="macAddress" value={formData.macAddress} onChange={handleChange} />
                </div>
                <div className="col-md-6">
                  <FormField label="Date de réception" name="dateReception" type="date" value={formData.dateReception} onChange={handleChange} />
                </div>
                <div className="col-md-6">
                  <FormField label="Fournisseur" name="fournisseur" value={formData.fournisseur} onChange={handleChange} />
                </div>
                <div className="col-12">
                  <FormField label="Commentaire" name="commentaire" type="textarea" value={formData.commentaire} onChange={handleChange} rows={3} />
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

export default EnregistrerModem
