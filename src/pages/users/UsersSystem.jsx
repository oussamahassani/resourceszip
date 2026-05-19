import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import { Link } from 'react-router-dom'
import usersService from '../../services/usersService'

const UsersSystem = () => {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    usersService.getSystemUsers()
      .then(data => setUsers(data?.content || data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Code', field: 'codeUser' },
    { header: 'Nom', render: (r) => `${r.firstName || ''} ${r.lastName || ''}` },
    { header: 'Email', field: 'email' },
    { header: 'Rôle', render: (r) => r.roles?.map(role => role.roleName || role).join(', ') || '-' },
    {
      header: 'Actions',
      render: (r) => (
        <div className="d-flex justify-content-center">
          <Link to={`/admin/users/edit-user/${r.userid}`} className="btn btn-warning btn-sm mr-1">
            <i className="fas fa-edit"></i>
          </Link>
        </div>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Liste des utilisateurs système" breadcrumb={[{ label: 'Utilisateurs système' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Utilisateurs Système</h6>
          </div>
          <div className="card-body">
            <DataTable
              columns={columns}
              data={users}
              loading={loading}
              emptyMessage="Aucun utilisateur système trouvé"
            />
          </div>
        </div>
      </div>
    </section>
  )
}

export default UsersSystem
