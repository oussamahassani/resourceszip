import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'

const AllPrivileges = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 20, total: 0, totalPages: 0 })

  const load = (page = 1) => {
    setLoading(true)
    axiosInstance.get('/api/role/allprivileges', { params: { page: page - 1, size: 20 } })
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
    { header: 'Nom du privilège', field: 'privilegeName' },
    { header: 'Description', field: 'description' },
    { header: 'Catégorie', field: 'category' },
  ]

  return (
    <section className="content">
      <PageHeader title="Gestion des privilèges" breadcrumb={[{ label: 'Rôles', path: '/role/allroles/1' }, { label: 'Privilèges' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Privilèges</h6>
            <Link to="/role/addprivilege" className="btn btn-primary btn-sm">
              <i className="fas fa-plus mr-1"></i> Ajouter
            </Link>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading}
              pagination={pagination} onPageChange={load} emptyMessage="Aucun privilège trouvé" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default AllPrivileges
