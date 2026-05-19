import React, { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import usersService from '../../services/usersService'
import { formatDateTime } from '../../utils/helpers'

const HistoriqueUser = () => {
  const { id } = useParams()
  const [historique, setHistorique] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    usersService.getHistoriqueUser(id)
      .then(data => setHistorique(data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [id])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Action', field: 'action' },
    { header: 'Description', field: 'description' },
    { header: 'Date', render: (r) => formatDateTime(r.dateAction || r.date) },
    { header: 'Effectué par', render: (r) => r.performedBy || r.user || '-' },
  ]

  return (
    <section className="content">
      <PageHeader
        title="Historique utilisateur"
        breadcrumb={[{ label: 'Utilisateurs', path: '/admin/users/allusers/1' }, { label: 'Historique' }]}
      />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Historique</h6>
            <Link to={`/admin/users/edit-user/${id}`} className="btn btn-warning btn-sm">
              <i className="fas fa-edit mr-1"></i> Modifier
            </Link>
          </div>
          <div className="card-body">
            <DataTable
              columns={columns}
              data={historique}
              loading={loading}
              emptyMessage="Aucun historique trouvé"
            />
          </div>
        </div>
      </div>
    </section>
  )
}

export default HistoriqueUser
