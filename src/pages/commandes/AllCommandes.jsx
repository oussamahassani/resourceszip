import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDate } from '../../utils/helpers'

const AllCommandes = () => {
  const { page = 1, size = 20 } = useParams()
  const [commandes, setCommandes] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })

  const load = (p = parseInt(page)) => {
    setLoading(true)
    axiosInstance.get('/api/commande/allcommandes', { params: { page: p - 1, size: parseInt(size) } })
      .then(res => {
        const data = res.data
        setCommandes(data?.content || data || [])
        setPagination(prev => ({ ...prev, page: p, total: data?.totalElements || 0, totalPages: data?.totalPages || 1 }))
      })
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const columns = [
    { header: '#', render: (_, i) => ((pagination.page - 1) * 20) + i + 1 },
    { header: 'N° Commande', field: 'numeroCommande' },
    { header: 'Client', render: (r) => `${r.client?.firstName || ''} ${r.client?.lastName || ''}` },
    { header: 'Produit', render: (r) => r.produit?.produitName || '-' },
    { header: 'Quantité', field: 'quantite' },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Livré' ? 'success' : r.statut === 'Annulé' ? 'danger' : 'warning'}`}>
        {r.statut || '-'}
      </span>
    )},
    { header: 'Revendeur', render: (r) => r.revendeur ? `${r.revendeur.firstName} ${r.revendeur.lastName}` : '-' },
    { header: 'Date', render: (r) => formatDate(r.dateCommande || r.dateCreation) },
    {
      header: 'Actions',
      render: (r) => (
        <div className="d-flex justify-content-center">
          <button className="btn btn-info btn-sm mr-1" onClick={() => {}}>
            <i className="fas fa-eye"></i>
          </button>
        </div>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Liste des commandes" breadcrumb={[{ label: 'Commandes' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Commandes</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={commandes} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucune commande trouvée" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllCommandes
