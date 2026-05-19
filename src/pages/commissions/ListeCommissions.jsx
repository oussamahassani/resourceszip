import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatCurrency } from '../../utils/helpers'
import { MONTHS_FR, getYearRange, getCurrentYear } from '../../utils/helpers'

const ListeCommissions = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [year, setYear] = useState(getCurrentYear())
  const [month, setMonth] = useState('')

  const load = () => {
    setLoading(true)
    axiosInstance.get('/api/commission/listeCommision', { params: { year, month: month || undefined } })
      .then(res => setData(res.data?.content || res.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Revendeur', render: (r) => r.revendeur ? `${r.revendeur.firstName} ${r.revendeur.lastName} (${r.revendeur.codeUser})` : '-' },
    { header: 'Montant HT', render: (r) => formatCurrency(r.montantHt) },
    { header: 'Montant TTC', render: (r) => formatCurrency(r.montantTtc) },
    { header: 'Mois', render: (r) => r.monthName || '-' },
    { header: 'Année', field: 'annee' },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Payé' ? 'success' : 'warning'}`}>{r.statut || '-'}</span>
    )},
    {
      header: 'Actions',
      render: (r) => (
        <Link to={`/commission/detailsCommission/${r.id}`} className="btn btn-info btn-sm">
          <i className="fas fa-eye"></i>
        </Link>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Liste des commissions" breadcrumb={[{ label: 'Commissions' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex flex-wrap align-items-center gap-2">
            <select className="form-control form-control-sm mr-2" style={{ width: 120 }} value={year} onChange={e => setYear(parseInt(e.target.value))}>
              {getYearRange().map(y => <option key={y} value={y}>{y}</option>)}
            </select>
            <select className="form-control form-control-sm mr-2" style={{ width: 140 }} value={month} onChange={e => setMonth(e.target.value)}>
              <option value="">Tous les mois</option>
              {MONTHS_FR.map(m => <option key={m.value} value={m.value}>{m.label}</option>)}
            </select>
            <button className="btn btn-primary btn-sm" onClick={load}>
              <i className="fas fa-search mr-1"></i> Filtrer
            </button>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucune commission trouvée" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default ListeCommissions
