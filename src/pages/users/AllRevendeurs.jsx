import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import usersService from '../../services/usersService'
import { formatDate } from '../../utils/helpers'

const AllRevendeurs = () => {
  const { page = 1 } = useParams()
  const [revendeurs, setRevendeurs] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: parseInt(page), size: 20, total: 0, totalPages: 0 })
  const [search, setSearch] = useState({ nom: '', prenom: '', codeUser: '' })

  const loadData = (p = 1, s = search) => {
    setLoading(true)
    usersService.getRevendeurs({ page: p - 1, size: 20, ...s })
      .then(data => {
        setRevendeurs(data?.content || data || [])
        setPagination(prev => ({ ...prev, total: data?.totalElements || 0, totalPages: data?.totalPages || 1 }))
      })
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { loadData(parseInt(page)) }, [page])

  const handleSearch = (e) => {
    e.preventDefault()
    loadData(1, search)
    setPagination(prev => ({ ...prev, page: 1 }))
  }

  const columns = [
    { header: '#', render: (_, i) => ((pagination.page - 1) * 20) + i + 1 },
    { header: 'Code', field: 'codeUser' },
    { header: 'Nom', render: (r) => `${r.firstName || ''} ${r.lastName || ''}` },
    { header: 'Email', field: 'email' },
    { header: 'Téléphone', field: 'phone' },
    { header: 'Zone', render: (r) => r.zone?.zoneName || '-' },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.enabled ? 'success' : 'danger'}`}>
        {r.enabled ? 'Actif' : 'Inactif'}
      </span>
    )},
    {
      header: 'Actions',
      render: (r) => (
        <div className="d-flex justify-content-center">
          <Link to={`/RevendeurUser/revendeurdetail/${r.userid}`} className="btn btn-info btn-sm mr-1" title="Détails">
            <i className="fas fa-eye"></i>
          </Link>
          <Link to={`/RevendeurUser/editrevendeur/${r.userid}`} className="btn btn-warning btn-sm" title="Modifier">
            <i className="fas fa-edit"></i>
          </Link>
        </div>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Liste des revendeurs" breadcrumb={[{ label: 'Revendeurs' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Revendeurs</h6>
          </div>
          <div className="card-body">
            <form onSubmit={handleSearch} className="mb-3">
              <div className="row">
                <div className="col-md-3">
                  <input className="form-control form-control-sm" placeholder="Nom" value={search.nom}
                    onChange={e => setSearch(s => ({ ...s, nom: e.target.value }))} />
                </div>
                <div className="col-md-3">
                  <input className="form-control form-control-sm" placeholder="Code" value={search.codeUser}
                    onChange={e => setSearch(s => ({ ...s, codeUser: e.target.value }))} />
                </div>
                <div className="col-md-2">
                  <button type="submit" className="btn btn-primary btn-sm btn-block">
                    <i className="fas fa-search mr-1"></i> Rechercher
                  </button>
                </div>
              </div>
            </form>
            <DataTable
              columns={columns}
              data={revendeurs}
              loading={loading}
              pagination={{ ...pagination }}
              onPageChange={(p) => { setPagination(prev => ({ ...prev, page: p })); loadData(p) }}
              emptyMessage="Aucun revendeur trouvé"
            />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllRevendeurs
