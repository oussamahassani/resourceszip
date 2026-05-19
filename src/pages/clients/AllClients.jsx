import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link } from 'react-router-dom'
import { fetchClients } from '../../redux/slices/clientsSlice'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import { formatDate } from '../../utils/helpers'
import { usePermissions } from '../../hooks/usePermissions'
import clientsService from '../../services/clientsService'

const AllClients = () => {
  const dispatch = useDispatch()
  const { list, pagination, isLoading } = useSelector((state) => state.clients)
  const { hasAnyAuthority } = usePermissions()
  const [currentPage, setCurrentPage] = useState(1)
  const [filters, setFilters] = useState({
    nom: '', prenom: '', cin: '', codeClient: '', tel: '', gouvernorat: '', statut: '',
  })
  const [showFilters, setShowFilters] = useState(false)

  useEffect(() => {
    dispatch(fetchClients({ page: currentPage - 1, size: 20, ...filters }))
  }, [dispatch, currentPage])

  const handleSearch = (e) => {
    e.preventDefault()
    setCurrentPage(1)
    dispatch(fetchClients({ page: 0, size: 20, ...filters }))
  }

  const handleExport = async () => {
    try {
      const blob = await clientsService.exportList(filters)
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'clients.xlsx'
      a.click()
      window.URL.revokeObjectURL(url)
    } catch {}
  }

  const columns = [
    { header: '#', render: (_, i) => ((currentPage - 1) * 20) + i + 1 },
    { header: 'Code', field: 'codeClient' },
    { header: 'Nom', render: (r) => `${r.firstName || ''} ${r.lastName || ''}` },
    { header: 'CIN', field: 'cin' },
    { header: 'Téléphone', field: 'telephone' },
    { header: 'Gouvernorat', render: (r) => r.gouvernorat?.gouvernoratName || '-' },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Actif' ? 'success' : r.statut === 'Résilié' ? 'danger' : 'secondary'}`}>
        {r.statut || '-'}
      </span>
    )},
    { header: 'Date création', render: (r) => formatDate(r.dateCreation) },
    {
      header: 'Actions',
      render: (r) => (
        <div className="d-flex justify-content-center">
          <Link to={`/client/viewclient/${r.clientId || r.id}`} className="btn btn-info btn-sm mr-1" title="Voir">
            <i className="fas fa-eye"></i>
          </Link>
          <Link to={`/client/editclient/${r.clientId || r.id}`} className="btn btn-warning btn-sm" title="Modifier">
            <i className="fas fa-edit"></i>
          </Link>
        </div>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Liste des clients" breadcrumb={[{ label: 'Clients' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex flex-wrap align-items-center gap-2">
            <button className="btn btn-sm btn-warning mr-2" onClick={() => setShowFilters(f => !f)}>
              <i className="fas fa-filter mr-1"></i> Filtre
            </button>
            {hasAnyAuthority('UPDATE_SUBSCRIPTION_REQUEST_STATUS', 'CLIENT_EXTRAIR_LIST') && (
              <button className="btn btn-sm btn-info mr-2" onClick={handleExport}>
                <i className="fas fa-file-excel mr-1"></i> Extraire la liste des clients
              </button>
            )}
          </div>

          {showFilters && (
            <div className="card-body border-bottom">
              <form onSubmit={handleSearch}>
                <div className="row">
                  <div className="col-md-2">
                    <input className="form-control form-control-sm" placeholder="Nom" value={filters.nom}
                      onChange={e => setFilters(f => ({ ...f, nom: e.target.value }))} />
                  </div>
                  <div className="col-md-2">
                    <input className="form-control form-control-sm" placeholder="Prénom" value={filters.prenom}
                      onChange={e => setFilters(f => ({ ...f, prenom: e.target.value }))} />
                  </div>
                  <div className="col-md-2">
                    <input className="form-control form-control-sm" placeholder="CIN" value={filters.cin}
                      onChange={e => setFilters(f => ({ ...f, cin: e.target.value }))} />
                  </div>
                  <div className="col-md-2">
                    <input className="form-control form-control-sm" placeholder="Code client" value={filters.codeClient}
                      onChange={e => setFilters(f => ({ ...f, codeClient: e.target.value }))} />
                  </div>
                  <div className="col-md-2">
                    <input className="form-control form-control-sm" placeholder="Téléphone" value={filters.tel}
                      onChange={e => setFilters(f => ({ ...f, tel: e.target.value }))} />
                  </div>
                  <div className="col-md-2">
                    <button type="submit" className="btn btn-success btn-sm btn-block">
                      <i className="fas fa-search mr-1"></i> Rechercher
                    </button>
                  </div>
                </div>
              </form>
            </div>
          )}

          <div className="card-body">
            <DataTable
              columns={columns}
              data={list}
              loading={isLoading}
              pagination={{ ...pagination, page: currentPage, totalPages: Math.ceil((pagination.total || 0) / 20) }}
              onPageChange={setCurrentPage}
              emptyMessage="Aucun client trouvé"
            />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllClients
