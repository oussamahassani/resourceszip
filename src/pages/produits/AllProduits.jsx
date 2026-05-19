import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import Alert from '../../components/common/Alert'
import axiosInstance from '../../api/axiosInstance'

const AllProduits = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })
  const [showAdd, setShowAdd] = useState(false)
  const [formData, setFormData] = useState({ produitName: '', description: '', categoryId: '' })
  const [categories, setCategories] = useState([])
  const [success, setSuccess] = useState(null)
  const [error, setError] = useState(null)

  const load = (page = 1) => {
    setLoading(true)
    axiosInstance.get('/api/pi/allproduits', { params: { page: page - 1, size: 20 } })
      .then(res => {
        const d = res.data
        setData(d?.content || d || [])
        setPagination(prev => ({ ...prev, page, total: d?.totalElements || 0, totalPages: d?.totalPages || 1 }))
      })
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
    axiosInstance.get('/api/pi/allcategories').then(res => setCategories(res.data || [])).catch(() => {})
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      await axiosInstance.post('/api/pi/addproduit', formData)
      setSuccess('Produit ajouté avec succès')
      setShowAdd(false)
      setFormData({ produitName: '', description: '', categoryId: '' })
      load()
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de l\'ajout')
    }
  }

  const columns = [
    { header: '#', render: (_, i) => ((pagination.page - 1) * 20) + i + 1 },
    { header: 'Nom du produit', field: 'produitName' },
    { header: 'Catégorie', render: (r) => r.category?.categoryName || '-' },
    { header: 'Description', field: 'description' },
    { header: 'Actif', render: (r) => (
      <span className={`badge badge-${r.actif ? 'success' : 'danger'}`}>{r.actif ? 'Oui' : 'Non'}</span>
    )},
  ]

  return (
    <section className="content">
      <PageHeader title="Produits internet" breadcrumb={[{ label: 'Services' }, { label: 'Produits' }]} />
      <div className="container-fluid">
        {success && <Alert type="success" message={success} onClose={() => setSuccess(null)} />}
        {error && <Alert type="danger" message={error} onClose={() => setError(null)} />}
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Produits internet</h6>
            <button className="btn btn-primary btn-sm" onClick={() => setShowAdd(s => !s)}>
              <i className="fas fa-plus mr-1"></i> Ajouter
            </button>
          </div>
          {showAdd && (
            <div className="card-body border-bottom">
              <form onSubmit={handleSubmit}>
                <div className="row">
                  <div className="col-md-4">
                    <input className="form-control form-control-sm" placeholder="Nom produit" value={formData.produitName}
                      onChange={e => setFormData(f => ({ ...f, produitName: e.target.value }))} required />
                  </div>
                  <div className="col-md-4">
                    <select className="form-control form-control-sm" value={formData.categoryId}
                      onChange={e => setFormData(f => ({ ...f, categoryId: e.target.value }))}>
                      <option value="">Choisir une catégorie</option>
                      {categories.map(c => <option key={c.id} value={c.id}>{c.categoryName}</option>)}
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
            <DataTable columns={columns} data={data} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucun produit trouvé" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllProduits
