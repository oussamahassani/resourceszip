import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link, useParams } from 'react-router-dom'
import { fetchAbonnements } from '../../redux/slices/abonnementsSlice'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import { formatDate } from '../../utils/helpers'

const AllAbonnements = () => {
  const dispatch = useDispatch()
  const { list, pagination, isLoading } = useSelector((state) => state.abonnements)
  const { page = 1, size = 20 } = useParams()
  const [currentPage, setCurrentPage] = useState(parseInt(page))
  const [showFilters, setShowFilters] = useState(false)
  const [filters, setFilters] = useState({ nom: '', prenom: '', cin: '', reference: '', statut: '' })

  useEffect(() => {
    dispatch(fetchAbonnements({ page: currentPage - 1, size: parseInt(size), ...filters }))
  }, [dispatch, currentPage])

  const handleSearch = (e) => {
    e.preventDefault()
    setCurrentPage(1)
    dispatch(fetchAbonnements({ page: 0, size: parseInt(size), ...filters }))
  }

  const columns = [
    { header: '#', render: (_, i) => ((currentPage - 1) * parseInt(size)) + i + 1 },
    { header: 'Référence', field: 'reference' },
    { header: 'Client', render: (r) => `${r.client?.firstName || ''} ${r.client?.lastName || ''}` },
    { header: 'CIN', render: (r) => r.client?.cin || '-' },
    { header: 'Offre/Pack', render: (r) => r.offre?.offreName || r.pack?.packName || '-' },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Validé' ? 'success' : r.statut === 'Refusé' ? 'danger' : r.statut === 'En cours' ? 'warning' : 'secondary'}`}>
        {r.statut || '-'}
      </span>
    )},
    { header: 'Revendeur', render: (r) => r.revendeur ? `${r.revendeur.firstName} ${r.revendeur.lastName}` : '-' },
    { header: 'Date', render: (r) => formatDate(r.dateCreation) },
    {
      header: 'Actions',
      render: (r) => (
        <div className="d-flex justify-content-center">
          <Link to={`/demandeabonnement/getdemandeabonnement/${r.demandeId || r.id}`} className="btn btn-info btn-sm mr-1" title="Voir">
            <i className="fas fa-eye"></i>
          </Link>
          <Link to={`/demandeabonnement/editdemandeabonnement/${r.demandeId || r.id}`} className="btn btn-warning btn-sm" title="Modifier">
            <i className="fas fa-edit"></i>
          </Link>
        </div>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Liste des abonnements" breadcrumb={[{ label: 'Abonnements' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex align-items-center">
            <button className="btn btn-sm btn-warning mr-2" onClick={() => setShowFilters(f => !f)}>
              <i className="fas fa-filter mr-1"></i> Filtre
            </button>
            <Link to="/demandeabonnement/adddemandeabonnementwithsteps" className="btn btn-sm btn-primary">
              <i className="fas fa-plus mr-1"></i> Nouvel abonnement
            </Link>
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
                    <input className="form-control form-control-sm" placeholder="Référence" value={filters.reference}
                      onChange={e => setFilters(f => ({ ...f, reference: e.target.value }))} />
                  </div>
                  <div className="col-md-2">
                    <select className="form-control form-control-sm" value={filters.statut}
                      onChange={e => setFilters(f => ({ ...f, statut: e.target.value }))}>
                      <option value="">Tous statuts</option>
                      <option value="En cours">En cours</option>
                      <option value="Validé">Validé</option>
                      <option value="Refusé">Refusé</option>
                      <option value="Traité">Traité</option>
                    </select>
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
              pagination={{ ...pagination, page: currentPage, totalPages: Math.ceil((pagination.total || 0) / parseInt(size)) }}
              onPageChange={setCurrentPage}
              emptyMessage="Aucun abonnement trouvé"
            />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllAbonnements
