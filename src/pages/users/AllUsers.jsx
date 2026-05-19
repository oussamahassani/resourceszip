import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link, useParams } from 'react-router-dom'
import { fetchUsers } from '../../redux/slices/usersSlice'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import { formatDate } from '../../utils/helpers'

const AllUsers = () => {
  const dispatch = useDispatch()
  const { list, pagination, isLoading } = useSelector((state) => state.users)
  const { page = 1 } = useParams()
  const [currentPage, setCurrentPage] = useState(parseInt(page))
  const [search, setSearch] = useState({ nom: '', prenom: '', email: '', codeUser: '' })

  useEffect(() => {
    dispatch(fetchUsers({ page: currentPage - 1, size: 20, ...search }))
  }, [dispatch, currentPage])

  const handleSearch = (e) => {
    e.preventDefault()
    setCurrentPage(1)
    dispatch(fetchUsers({ page: 0, size: 20, ...search }))
  }

  const columns = [
    { header: '#', render: (_, i) => ((currentPage - 1) * 20) + i + 1 },
    { header: 'Code', field: 'codeUser' },
    { header: 'Nom', render: (r) => `${r.firstName || ''} ${r.lastName || ''}` },
    { header: 'Email', field: 'email' },
    { header: 'Rôle', render: (r) => r.roles?.map(role => role.roleName || role).join(', ') || '-' },
    { header: 'Zone', render: (r) => r.zone?.zoneName || '-' },
    { header: 'Date création', render: (r) => formatDate(r.dateCreation) },
    {
      header: 'Actions',
      render: (r) => (
        <div className="d-flex justify-content-center gap-1">
          <Link to={`/admin/users/edit-user/${r.userid}`} className="btn btn-warning btn-sm mr-1" title="Modifier">
            <i className="fas fa-edit"></i>
          </Link>
          <Link to={`/admin/users/historique/${r.userid}`} className="btn btn-info btn-sm" title="Historique">
            <i className="fas fa-history"></i>
          </Link>
        </div>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Liste des utilisateurs" breadcrumb={[{ label: 'Utilisateurs' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex align-items-center justify-content-between">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Utilisateurs</h6>
            <Link to="/admin/users/add-user" className="btn btn-sm btn-primary">
              <i className="fas fa-plus mr-1"></i> Ajouter
            </Link>
          </div>
          <div className="card-body">
            <form onSubmit={handleSearch} className="mb-3">
              <div className="row">
                <div className="col-md-3">
                  <input className="form-control form-control-sm" placeholder="Nom" value={search.nom}
                    onChange={e => setSearch(s => ({ ...s, nom: e.target.value }))} />
                </div>
                <div className="col-md-3">
                  <input className="form-control form-control-sm" placeholder="Prénom" value={search.prenom}
                    onChange={e => setSearch(s => ({ ...s, prenom: e.target.value }))} />
                </div>
                <div className="col-md-3">
                  <input className="form-control form-control-sm" placeholder="Email" value={search.email}
                    onChange={e => setSearch(s => ({ ...s, email: e.target.value }))} />
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
              data={list}
              loading={isLoading}
              pagination={{ ...pagination, page: currentPage, totalPages: Math.ceil(pagination.total / 20) }}
              onPageChange={setCurrentPage}
              emptyMessage="Aucun utilisateur trouvé"
            />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllUsers
