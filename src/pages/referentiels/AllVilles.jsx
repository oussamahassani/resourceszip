import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import Alert from '../../components/common/Alert'
import referentielsService from '../../services/referentielsService'
import axiosInstance from '../../api/axiosInstance'

const AllVilles = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [gouvernorats, setGouvernorats] = useState([])
  const [selectedGov, setSelectedGov] = useState('')
  const [showAdd, setShowAdd] = useState(false)
  const [newForm, setNewForm] = useState({ villeName: '', gouvernoratId: '' })
  const [success, setSuccess] = useState(null)

  useEffect(() => {
    referentielsService.getGouvernorats().then(setGouvernorats).catch(() => {})
    load()
  }, [])

  const load = (govId = '') => {
    setLoading(true)
    referentielsService.getVilles(govId || undefined)
      .then(setData)
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  const handleFilter = (e) => {
    const val = e.target.value
    setSelectedGov(val)
    load(val)
  }

  const handleAdd = async (e) => {
    e.preventDefault()
    try {
      await axiosInstance.post('/api/ville/addville', newForm)
      setSuccess('Ville ajoutée')
      setNewForm({ villeName: '', gouvernoratId: '' })
      setShowAdd(false)
      load()
    } catch {}
  }

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Ville', render: (r) => r.villeName || r.name || '-' },
    { header: 'Gouvernorat', render: (r) => r.gouvernorat?.gouvernoratName || '-' },
    { header: 'Code postal', render: (r) => r.codePostal || '-' },
  ]

  return (
    <section className="content">
      <PageHeader title="Villes" breadcrumb={[{ label: 'Référentiels' }, { label: 'Villes' }]} />
      <div className="container-fluid">
        {success && <Alert type="success" message={success} onClose={() => setSuccess(null)} />}
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center flex-wrap gap-2">
            <div className="d-flex align-items-center">
              <h6 className="m-0 font-weight-bold mr-3" style={{ color: '#590E9C' }}>Villes</h6>
              <select className="form-control form-control-sm" style={{ width: 200 }} value={selectedGov} onChange={handleFilter}>
                <option value="">Tous les gouvernorats</option>
                {gouvernorats.map(g => <option key={g.gouvernoratId || g.id} value={g.gouvernoratId || g.id}>{g.gouvernoratName || g.name}</option>)}
              </select>
            </div>
            <button className="btn btn-primary btn-sm" onClick={() => setShowAdd(s => !s)}>
              <i className="fas fa-plus mr-1"></i> Ajouter
            </button>
          </div>
          {showAdd && (
            <div className="card-body border-bottom">
              <form onSubmit={handleAdd}>
                <div className="row">
                  <div className="col-md-4">
                    <input className="form-control form-control-sm" placeholder="Nom de la ville"
                      value={newForm.villeName} onChange={e => setNewForm(f => ({ ...f, villeName: e.target.value }))} required />
                  </div>
                  <div className="col-md-4">
                    <select className="form-control form-control-sm" value={newForm.gouvernoratId}
                      onChange={e => setNewForm(f => ({ ...f, gouvernoratId: e.target.value }))} required>
                      <option value="">Choisir un gouvernorat</option>
                      {gouvernorats.map(g => <option key={g.gouvernoratId || g.id} value={g.gouvernoratId || g.id}>{g.gouvernoratName || g.name}</option>)}
                    </select>
                  </div>
                  <div className="col-md-4">
                    <button type="submit" className="btn btn-success btn-sm mr-2">Enregistrer</button>
                    <button type="button" className="btn btn-secondary btn-sm" onClick={() => setShowAdd(false)}>Annuler</button>
                  </div>
                </div>
              </form>
            </div>
          )}
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucune ville trouvée" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllVilles
