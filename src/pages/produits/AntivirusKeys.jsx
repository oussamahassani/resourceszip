import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import Alert from '../../components/common/Alert'
import axiosInstance from '../../api/axiosInstance'
import { formatDate } from '../../utils/helpers'

const AntivirusKeys = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState(null)
  const [showAdd, setShowAdd] = useState(false)
  const [newKey, setNewKey] = useState({ licenseKey: '', expiryDate: '' })

  useEffect(() => { load() }, [])

  const load = () => {
    setLoading(true)
    axiosInstance.get('/api/antiVirus/antivirus_keys')
      .then(res => setData(res.data?.content || res.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  const handleAdd = async (e) => {
    e.preventDefault()
    try {
      await axiosInstance.post('/api/antiVirus/add_key', newKey)
      setSuccess('Clé ajoutée avec succès')
      setShowAdd(false)
      setNewKey({ licenseKey: '', expiryDate: '' })
      load()
    } catch {}
  }

  const handleAssign = async (id, clientId) => {
    try {
      await axiosInstance.put(`/api/antiVirus/assign/${id}`, { clientId })
      load()
    } catch {}
  }

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Clé de licence', field: 'licenseKey' },
    { header: 'Client assigné', render: (r) => r.client ? `${r.client.firstName} ${r.client.lastName}` : '-' },
    { header: 'Date expiration', render: (r) => formatDate(r.expiryDate) },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Disponible' ? 'success' : 'warning'}`}>
        {r.statut || (r.client ? 'Assignée' : 'Disponible')}
      </span>
    )},
    { header: 'Date attribution', render: (r) => formatDate(r.dateAttribution) },
  ]

  return (
    <section className="content">
      <PageHeader title="Clés antivirus" breadcrumb={[{ label: 'Services' }, { label: 'Clés antivirus' }]} />
      <div className="container-fluid">
        {success && <Alert type="success" message={success} onClose={() => setSuccess(null)} />}
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Clés antivirus</h6>
            <button className="btn btn-primary btn-sm" onClick={() => setShowAdd(s => !s)}>
              <i className="fas fa-plus mr-1"></i> Ajouter une clé
            </button>
          </div>
          {showAdd && (
            <div className="card-body border-bottom">
              <form onSubmit={handleAdd}>
                <div className="row">
                  <div className="col-md-5">
                    <input className="form-control form-control-sm" placeholder="Clé de licence"
                      value={newKey.licenseKey} onChange={e => setNewKey(k => ({ ...k, licenseKey: e.target.value }))} required />
                  </div>
                  <div className="col-md-4">
                    <input type="date" className="form-control form-control-sm" placeholder="Date d'expiration"
                      value={newKey.expiryDate} onChange={e => setNewKey(k => ({ ...k, expiryDate: e.target.value }))} />
                  </div>
                  <div className="col-md-3">
                    <button type="submit" className="btn btn-success btn-sm mr-2">Enregistrer</button>
                    <button type="button" className="btn btn-secondary btn-sm" onClick={() => setShowAdd(false)}>Annuler</button>
                  </div>
                </div>
              </form>
            </div>
          )}
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucune clé antivirus" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AntivirusKeys
