import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import axiosInstance from '../../api/axiosInstance'
import { formatCurrency, MONTHS_FR, getYearRange, getCurrentYear } from '../../utils/helpers'

const RecapRevendeur = () => {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(false)
  const [year, setYear] = useState(getCurrentYear())
  const [month, setMonth] = useState(new Date().getMonth() + 1)

  const load = () => {
    setLoading(true)
    axiosInstance.get('/api/payement/recaprevendeur', { params: { year, month } })
      .then(res => setData(res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const d = data || {}

  return (
    <section className="content">
      <PageHeader title="Récapitulatif revendeur" breadcrumb={[{ label: 'Paiements' }, { label: 'Récap' }]} />
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
            {loading ? (
              <div className="text-center"><div className="spinner-border text-primary"></div></div>
            ) : data ? (
              <div className="row">
                <div className="col-md-6">
                  <table className="table table-bordered">
                    <tbody>
                      <tr><th>Revendeur</th><td>{d.revendeur ? `${d.revendeur.firstName} ${d.revendeur.lastName}` : '-'}</td></tr>
                      <tr><th>Mois/Année</th><td>{MONTHS_FR.find(m => m.value === month)?.label}/{year}</td></tr>
                      <tr><th>Montant versé</th><td className="font-weight-bold text-success">{formatCurrency(d.montantVerse)}</td></tr>
                      <tr><th>Montant commission</th><td>{formatCurrency(d.montantCommission)}</td></tr>
                      <tr><th>Solde</th><td className={`font-weight-bold ${(d.solde || 0) < 0 ? 'text-danger' : 'text-success'}`}>{formatCurrency(d.solde)}</td></tr>
                    </tbody>
                  </table>
                </div>
              </div>
            ) : (
              <p className="text-muted text-center">Sélectionnez une période pour afficher le récapitulatif</p>
            )}
          </div>
        </div>
      </div>
    </section>
  )
}

export default RecapRevendeur
