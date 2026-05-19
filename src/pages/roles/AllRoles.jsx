import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'

const AllRoles = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })

  const load = (page = 1) => {
    setLoading(true)
    axiosInstance.get('/api/role/allroles', { params: { page: page - 1, size: 20 } })
      .then(res => {
        const d = res.data
        setData(d?.content || d || [])
        setPagination(prev => ({ ...prev, page, total: d?.totalElements || 0, totalPages: d?.totalPages || 1 }))
      })
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const columns = [
    { header: '#', render: (_, i) => ((pagination.page - 1) * 20) + i + 1 },
    { header: 'Nom du rôle', field: 'roleName' },
    { header: 'Description', field: 'description' },
    { header: 'Nombre de privilèges', render: (r) => r.privileges?.length || 0 },
    {
      header: 'Actions',
      render: (r) => (
        <Link to={`/role/editrole/${r.roleId || r.id}`} className="btn btn-warning btn-sm">
          <i className="fas fa-edit"></i>
        </Link>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Gestion des rôles" breadcrumb={[{ label: 'Rôles' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Rôles</h6>
            <Link to="/role/addrole" className="btn btn-primary btn-sm">
              <i className="fas fa-plus mr-1"></i> Ajouter
            </Link>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucun rôle trouvé" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllRoles
