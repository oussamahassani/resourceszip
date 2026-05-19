import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import axiosInstance from '../../api/axiosInstance'
import { formatCurrency, MONTHS_FR, getYearRange, getCurrentYear } from '../../utils/helpers'

const ViewListeRecapRevendeur = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [year, setYear] = useState(getCurrentYear())
  const [month, setMonth] = useState(new Date().getMonth() + 1)

  const load = () => {
    setLoading(true)
    axiosInstance.get('/api/payement/viewlisterecaperevendeur', { params: { year, month } })
      .then(res => setData(res.data?.content || res.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const columns = [
    { header: '#', render: (_, i) => i + 1 },
    { header: 'Code', render: (r) => r.revendeur?.codeUser || '-' },
    { header: 'Revendeur', render: (r) => r.revendeur ? `${r.revendeur.firstName} ${r.revendeur.lastName}` : '-' },
    { header: 'Zone', render: (r) => r.revendeur?.zone?.zoneName || '-' },
    { header: 'Total versé', render: (r) => formatCurrency(r.montantVerse) },
    { header: 'Commission', render: (r) => formatCurrency(r.montantCommission) },
    { header: 'Solde', render: (r) => (
      <span className={`font-weight-bold ${(r.solde || 0) < 0 ? 'text-danger' : 'text-success'}`}>
        {formatCurrency(r.solde)}
      </span>
    )},
    {
      header: 'Actions',
      render: (r) => (
        <Link to="/payement/recaprevendeur" className="btn btn-info btn-sm">
          <i className="fas fa-eye"></i>
        </Link>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Récapitulatif des revendeurs" breadcrumb={[{ label: 'Paiements' }, { label: 'Récap revendeurs' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex flex-wrap align-items-center gap-2">
            <select className="form-control form-control-sm mr-2" style={{ width: 120 }} value={year} onChange={e => setYear(parseInt(e.target.value))}>
              {getYearRange().map(y => <option key={y} value={y}>{y}</option>)}
            </select>
            <select className="form-control form-control-sm mr-2" style={{ width: 140 }} value={month} onChange={e => setMonth(parseInt(e.target.value))}>
              {MONTHS_FR.map(m => <option key={m.value} value={m.value}>{m.label}</option>)}
            </select>
            <button className="btn btn-primary btn-sm" onClick={load} disabled={loading}>
              <i className="fas fa-search mr-1"></i> Afficher
            </button>
          </div>
          <div className="card-body">
            <DataTable columns={columns} data={data} loading={loading} emptyMessage="Aucune donnée" />
          </div>
        </div>
      </div>
    </section>
  )
}

export default ViewListeRecapRevendeur
