import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import Alert from '../../components/common/Alert'
import axiosInstance from '../../api/axiosInstance'

const AllCategories = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })
  const [showAdd, setShowAdd] = useState(false)
  const [newName, setNewName] = useState('')
  const [success, setSuccess] = useState(null)

  const load = (page = 1) => {
    setLoading(true)
    axiosInstance.get('/api/pi/allcategories', { params: { page: page - 1, size: 20 } })
      .then(res => {
        const d = res.data
        setData(d?.content || d || [])
        setPagination(prev => ({ ...prev, page, total: d?.totalElements || 0, totalPages: d?.totalPages || 1 }))
      })
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const handleAdd = async (e) => {
    e.preventDefault()
    try {
      await axiosInstance.post('/api/pi/addcategory', { categoryName: newName })
      setSuccess('Catégorie ajoutée')
      setNewName('')
      setShowAdd(false)
      load()
    } catch {}
  }

  const columns = [
    { header: '#', render: (_, i) => ((pagination.page - 1) * 20) + i + 1 },
    { header: 'Nom de la catégorie', field: 'categoryName' },
    { header: 'Nombre de produits', render: (r) => r.products?.length || 0 },
  ]

  return (
    <section className="content">
      <PageHeader title="Catégories produits internet" breadcrumb={[{ label: 'Services' }, { label: 'Catégories' }]} />
      <div className="container-fluid">
        {success && <Alert type="success" message={success} onClose={() => setSuccess(null)} />}
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Catégories</h6>
            <button className="btn btn-primary btn-sm" onClick={() => setShowAdd(s => !s)}>
              <i className="fas fa-plus mr-1"></i> Ajouter
            </button>
          </div>
          {showAdd && (
            <div className="card-body border-bottom">
              <form onSubmit={handleAdd} className="d-flex gap-2">
                <input className="form-control form-control-sm mr-2" style={{ maxWidth: 300 }} placeholder="Nom de la catégorie"
                  value={newName} onChange={e => setNewName(e.target.value)} required />
                <button type="submit" className="btn btn-success btn-sm mr-2">Enregistrer</button>
                <button type="button" className="btn btn-secondary btn-sm" onClick={() => setShowAdd(false)}>Annuler</button>
              </form>
            </div>
          )}
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucune catégorie trouvée" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllCategories
