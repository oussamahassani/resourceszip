import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDate } from '../../utils/helpers'

const DemandModem = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    axiosInstance.get('/api/modem/demandemodem')
      .then(res => setData(res.data?.content || res.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const handleAction = async (id, action) => {
    try {
      await axiosInstance.put(`/api/modem/demandemodem/${id}/${action}`)
      setData(prev => prev.map(d => d.id === id ? { ...d, statut: action === 'approve' ? 'Approuvé' : 'Refusé' } : d))
    } catch {}
  }

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Revendeur', render: (r) => r.revendeur ? `${r.revendeur.firstName} ${r.revendeur.lastName}` : '-' },
    { header: 'Quantité demandée', field: 'quantite' },
    { header: 'Quantité accordée', field: 'quantiteAccordee' },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Approuvé' ? 'success' : r.statut === 'Refusé' ? 'danger' : 'warning'}`}>
        {r.statut || 'En attente'}
      </span>
    )},
    { header: 'Date', render: (r) => formatDate(r.dateDemande || r.dateCreation) },
    {
      header: 'Actions',
      render: (r) => r.statut === 'En attente' ? (
        <div className="d-flex justify-content-center">
          <button className="btn btn-success btn-sm mr-1" onClick={() => handleAction(r.id, 'approve')}>
            <i className="fas fa-check"></i>
          </button>
          <button className="btn btn-danger btn-sm" onClick={() => handleAction(r.id, 'reject')}>
            <i className="fas fa-times"></i>
          </button>
        </div>
      ) : null
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Demandes de modem" breadcrumb={[{ label: 'Modems' }, { label: 'Demandes' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Demandes de modem</h6>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucune demande de modem" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default DemandModem
