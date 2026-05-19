import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import Alert from '../../components/common/Alert'
import referentielsService from '../../services/referentielsService'
import axiosInstance from '../../api/axiosInstance'

const AllGouvernorats = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [showAdd, setShowAdd] = useState(false)
  const [newName, setNewName] = useState('')
  const [success, setSuccess] = useState(null)

  useEffect(() => { load() }, [])

  const load = () => {
    setLoading(true)
    referentielsService.getGouvernorats()
      .then(setData)
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  const handleAdd = async (e) => {
    e.preventDefault()
    try {
      await axiosInstance.post('/api/ville/addgouvernorat', { gouvernoratName: newName })
      setSuccess('Gouvernorat ajouté')
      setNewName('')
      setShowAdd(false)
      load()
    } catch {}
  }

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Gouvernorat', render: (r) => r.gouvernoratName || r.name || '-' },
    { header: 'Code', render: (r) => r.gouvernoratCode || r.code || '-' },
    { header: 'Nombre de villes', render: (r) => r.villes?.length || 0 },
  ]

  return (
    <section className="content">
      <PageHeader title="Gouvernorats" breadcrumb={[{ label: 'Référentiels' }, { label: 'Gouvernorats' }]} />
      <div className="container-fluid">
        {success && <Alert type="success" message={success} onClose={() => setSuccess(null)} />}
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Gouvernorats</h6>
            <button className="btn btn-primary btn-sm" onClick={() => setShowAdd(s => !s)}>
              <i className="fas fa-plus mr-1"></i> Ajouter
            </button>
          </div>
          {showAdd && (
            <div className="card-body border-bottom">
              <form onSubmit={handleAdd} className="d-flex gap-2">
                <input className="form-control form-control-sm mr-2" style={{ maxWidth: 300 }} placeholder="Nom du gouvernorat"
                  value={newName} onChange={e => setNewName(e.target.value)} required />
                <button type="submit" className="btn btn-success btn-sm mr-2">Enregistrer</button>
                <button type="button" className="btn btn-secondary btn-sm" onClick={() => setShowAdd(false)}>Annuler</button>
              </form>
            </div>
          )}
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucun gouvernorat trouvé" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllGouvernorats
