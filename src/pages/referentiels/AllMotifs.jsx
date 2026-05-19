import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import Alert from '../../components/common/Alert'
import referentielsService from '../../services/referentielsService'
import axiosInstance from '../../api/axiosInstance'

const AllMotifs = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [showAdd, setShowAdd] = useState(false)
  const [newName, setNewName] = useState('')
  const [success, setSuccess] = useState(null)

  useEffect(() => { load() }, [])

  const load = () => {
    setLoading(true)
    referentielsService.getMotifs()
      .then(setData)
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  const handleAdd = async (e) => {
    e.preventDefault()
    try {
      await axiosInstance.post('/api/motif/addmotif', { motifName: newName })
      setSuccess('Motif ajouté')
      setNewName('')
      setShowAdd(false)
      load()
    } catch {}
  }

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Motif', render: (r) => r.motifName || r.name || '-' },
    { header: 'Description', field: 'description' },
  ]

  return (
    <section className="content">
      <PageHeader title="Motifs" breadcrumb={[{ label: 'Référentiels' }, { label: 'Motifs' }]} />
      <div className="container-fluid">
        {success && <Alert type="success" message={success} onClose={() => setSuccess(null)} />}
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Motifs</h6>
            <button className="btn btn-primary btn-sm" onClick={() => setShowAdd(s => !s)}>
              <i className="fas fa-plus mr-1"></i> Ajouter
            </button>
          </div>
          {showAdd && (
            <div className="card-body border-bottom">
              <form onSubmit={handleAdd} className="d-flex gap-2">
                <input className="form-control form-control-sm mr-2" style={{ maxWidth: 300 }} placeholder="Nom du motif"
                  value={newName} onChange={e => setNewName(e.target.value)} required />
                <button type="submit" className="btn btn-success btn-sm mr-2">Enregistrer</button>
                <button type="button" className="btn btn-secondary btn-sm" onClick={() => setShowAdd(false)}>Annuler</button>
              </form>
            </div>
          )}
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucun motif trouvé" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllMotifs
