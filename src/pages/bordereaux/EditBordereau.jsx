import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import FormField from '../../components/common/FormField'
import Alert from '../../components/common/Alert'
import Loader from '../../components/common/Loader'
import axiosInstance from '../../api/axiosInstance'

const EditBordereau = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [bordereau, setBordereau] = useState(null)
  const [error, setError] = useState(null)
  const [formData, setFormData] = useState({ statut: '', commentaire: '' })

  useEffect(() => {
    setLoading(true)
    axiosInstance.get(`/api/bordereau/detailsbordereau/${id}`)
      .then(res => {
        setBordereau(res.data)
        setFormData({ statut: res.data.statut || '', commentaire: res.data.commentaire || '' })
      })
      .catch(() => setError('Erreur de chargement'))
      .finally(() => setLoading(false))
  }, [id])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      await axiosInstance.put(`/api/bordereau/editbordereaux/${id}`, formData)
      navigate(`/bordereaux/detailsbordereau/${id}`)
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la mise à jour')
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <Loader />

  return (
    <section className="content">
      <PageHeader title="Modifier bordereau"
        breadcrumb={[{ label: 'Bordereaux', path: '/bordereaux/Listebordereaux' }, { label: 'Modifier' }]} />
      <div className="container-fluid">
        <div className="card shadow">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>
              Bordereau #{bordereau?.numeroBordereau || id}
            </h6>
          </div>
          <div className="card-body">
            {error && <Alert type="danger" message={error} />}
            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6">
                  <FormField label="Statut" name="statut" type="select" value={formData.statut}
                    onChange={e => setFormData(f => ({ ...f, statut: e.target.value }))}>
                    <option value="">Choisir un statut</option>
                    <option value="En attente">En attente</option>
                    <option value="Validé">Validé</option>
                    <option value="Refusé">Refusé</option>
                  </FormField>
                </div>
                <div className="col-12">
                  <FormField label="Commentaire" name="commentaire" type="textarea"
                    value={formData.commentaire} onChange={e => setFormData(f => ({ ...f, commentaire: e.target.value }))} rows={3} />
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

export default EditBordereau
