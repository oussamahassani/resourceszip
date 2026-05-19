import React, { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import FormField from '../../components/common/FormField'
import Alert from '../../components/common/Alert'
import Loader from '../../components/common/Loader'
import usersService from '../../services/usersService'
import axiosInstance from '../../api/axiosInstance'

const EditRevendeur = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)
  const [revendeur, setRevendeur] = useState(null)
  const [formData, setFormData] = useState({
    firstName: '', lastName: '', email: '', phone: '', plafond: '', enabled: true,
  })

  useEffect(() => {
    setLoading(true)
    usersService.getRevendeurById(id)
      .then(data => {
        setRevendeur(data)
        setFormData({
          firstName: data.firstName || '',
          lastName: data.lastName || '',
          email: data.email || '',
          phone: data.phone || '',
          plafond: data.plafond || '',
          enabled: data.enabled !== false,
        })
      })
      .catch(() => setError('Erreur lors du chargement'))
      .finally(() => setLoading(false))
  }, [id])

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target
    setFormData(prev => ({ ...prev, [name]: type === 'checkbox' ? checked : value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      await axiosInstance.put(`/api/admin/revendeurs/${id}`, formData)
      navigate(`/RevendeurUser/revendeurdetail/${id}`)
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la mise à jour')
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <Loader />

  return (
    <section className="content">
      <PageHeader
        title="Modifier le revendeur"
        breadcrumb={[{ label: 'Revendeurs', path: '/RevendeurUser/revendeurliste/1' }, { label: 'Modifier' }]}
      />
      <div className="container-fluid">
        <div className="card shadow">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>
              {revendeur?.firstName} {revendeur?.lastName}
            </h6>
          </div>
          <div className="card-body">
            {error && <Alert type="danger" message={error} />}
            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6">
                  <FormField label="Prénom" name="firstName" value={formData.firstName} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <FormField label="Nom" name="lastName" value={formData.lastName} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <FormField label="Email" name="email" type="email" value={formData.email} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <FormField label="Téléphone" name="phone" value={formData.phone} onChange={handleChange} />
                </div>
                <div className="col-md-6">
                  <FormField label="Plafond (DT)" name="plafond" type="number" value={formData.plafond} onChange={handleChange} />
                </div>
                <div className="col-md-6">
                  <div className="form-group mt-4">
                    <div className="form-check">
                      <input type="checkbox" className="form-check-input" name="enabled" checked={formData.enabled} onChange={handleChange} id="enabled" />
                      <label className="form-check-label" htmlFor="enabled">Compte actif</label>
                    </div>
                  </div>
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

export default EditRevendeur
