import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatDate, formatCurrency } from '../../utils/helpers'

const ListAvoirNotPublish = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)

  const load = () => {
    setLoading(true)
    axiosInstance.get('/api/avoir/AllAvoirClientNotPublic')
      .then(res => setData(res.data?.content || res.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const handlePublish = async (id) => {
    try {
      await axiosInstance.put(`/api/avoir/publish/${id}`)
      load()
    } catch {}
  }

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'N° Avoir', field: 'numeroAvoir' },
    { header: 'Client', render: (r) => `${r.client?.firstName || ''} ${r.client?.lastName || ''}` },
    { header: 'Montant', render: (r) => formatCurrency(r.montant) },
    { header: 'Motif', field: 'motif' },
    { header: 'Date', render: (r) => formatDate(r.dateAvoir || r.dateCreation) },
    {
      header: 'Actions',
      render: (r) => (
        <div className="d-flex justify-content-center">
          <button className="btn btn-success btn-sm mr-1" onClick={() => handlePublish(r.avoirId || r.id)} title="Publier">
            <i className="fas fa-check mr-1"></i> Publier
          </button>
          <button className="btn btn-info btn-sm" onClick={() => window.open(`/api/avoir/open/${r.avoirId || r.id}`, '_blank')}>
            <i className="fas fa-file-pdf"></i>
          </button>
        </div>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Avoirs en cours (non publiés)"
        breadcrumb={[{ label: 'Avoirs', path: '/AvoirClient/AllAvoirClient' }, { label: 'En cours' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Avoirs non publiés</h6>
            <Link to="/AvoirClient/addNewAvoir" className="btn btn-primary btn-sm">
              <i className="fas fa-plus mr-1"></i> Créer
            </Link>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucun avoir non publié" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default ListAvoirNotPublish
