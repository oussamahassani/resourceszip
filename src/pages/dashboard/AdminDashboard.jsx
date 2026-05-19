import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { fetchAdminStats } from '../../redux/slices/dashboardSlice'
import { usePermissions } from '../../hooks/usePermissions'
import PageHeader from '../../components/common/PageHeader'
import StatCard from '../../components/common/StatCard'
import dashboardService from '../../services/dashboardService'
import { getCurrentYear, MONTHS_FR, getYearRange } from '../../utils/helpers'

const AdminDashboard = () => {
  const dispatch = useDispatch()
  const { adminStats, isLoading } = useSelector((state) => state.dashboard)
  const { hasAnyAuthority } = usePermissions()

  const [chiffreData, setChiffreData] = useState(null)
  const [selectedYear, setSelectedYear] = useState(getCurrentYear())
  const [selectedMonth, setSelectedMonth] = useState('')

  useEffect(() => {
    dispatch(fetchAdminStats())
  }, [dispatch])

  useEffect(() => {
    if (hasAnyAuthority('READ_ADMINISTRATEUR')) {
      dashboardService.getChiffreAffaire({ year: selectedYear, month: selectedMonth || undefined })
        .then(setChiffreData)
        .catch(() => {})
    }
  }, [selectedYear, selectedMonth])

  const s = adminStats || {}

  return (
    <section className="content">
      <PageHeader title="Tableau de bord" />
      <div className="container-fluid">

        <div className="row justify-content-center">
          {hasAnyAuthority('READ_ADMINISTRATEUR', 'VIEW_CARDS_ADMIN') && (
            <>
              <StatCard
                title="Nouveaux client(s)"
                value={s.nouvelleAbonnementsThisMonth}
                subtitle={s.percentageChange}
                iconSrc="/img/Groupe 14559.png"
                accentColor="#F80457"
                isLoading={isLoading}
              />
              <StatCard
                title="Demande(s) abonnements"
                value={s.todayCountDemande}
                subtitle={s.yesterdayCountDemande ? `Hier: ${s.yesterdayCountDemande}` : undefined}
                iconSrc="/img/Groupe 14558.png"
                accentColor="#E5B000"
                isLoading={isLoading}
              />
              <StatCard
                title="Total revendeurs actifs"
                value={s.TotalRevendeurActifs}
                subtitle={
                  <span>
                    Accès limité: <span style={{ color: 'red' }}>{s.NbreRevendeur}</span> |
                    Complet: <span style={{ color: 'green' }}>{s.NbreRevendeurActive}</span>
                  </span>
                }
                iconSrc="/img/Groupe 14561.png"
                accentColor="#1FBE9D"
                isLoading={isLoading}
              />
              <StatCard
                title="Abonnements résiliés"
                value={s.resilierAbonnement}
                subtitle={s.resilierAbonnementLastMonth}
                iconSrc="/img/Groupe 14560.png"
                accentColor="#590E9D"
                isLoading={isLoading}
              />
            </>
          )}
        </div>

        {hasAnyAuthority('READ_ADMINISTRATEUR') && (
          <div className="row mb-0">
            <div className="col-lg-8 col-md-12 col-sm-12 mb-2 mt-2">
              <div className="card" style={{ borderRadius: 11, boxShadow: '0px 3px 6px #00000029' }}>
                <div className="card-header border-0">
                  <h3 className="card-title" style={{ fontWeight: 700 }}>Chiffre d&apos;affaire (HT)</h3>
                </div>
                <div className="card-body">
                  <div className="form-inline mb-3">
                    <label className="mr-2">Année:</label>
                    <select
                      className="form-control mr-2"
                      value={selectedYear}
                      onChange={e => setSelectedYear(parseInt(e.target.value))}
                    >
                      {getYearRange().map(y => <option key={y} value={y}>{y}</option>)}
                    </select>
                    <label className="mr-2">Mois:</label>
                    <select
                      className="form-control"
                      value={selectedMonth}
                      onChange={e => setSelectedMonth(e.target.value)}
                    >
                      <option value="">Tous</option>
                      {MONTHS_FR.map(m => <option key={m.value} value={m.value}>{m.label}</option>)}
                    </select>
                  </div>
                  {chiffreData ? (
                    <ChiffreAffaireChart data={chiffreData} />
                  ) : (
                    <div className="text-center text-muted py-4">
                      <i className="fas fa-chart-bar fa-3x mb-2"></i>
                      <p>Chargement des données...</p>
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div className="col-lg-4 col-md-6 col-sm-6 mt-2">
              {hasAnyAuthority('READ_ADMINISTRATEUR', 'VIEW_FINANCE_CHART') && (
                <TechnoRepartitionCard />
              )}
            </div>
          </div>
        )}

        {hasAnyAuthority('READ_ADMINISTRATEUR', 'VIEW_FINANCE_CHART') && (
          <div className="row mb-0">
            <div className="col-lg-6 col-md-12 mb-2 mt-2">
              <TopRevendeursCard />
            </div>
            <div className="col-lg-6 col-md-12 mb-2 mt-2">
              <AbonnementsByGouvernoratCard />
            </div>
          </div>
        )}
      </div>
    </section>
  )
}

const ChiffreAffaireChart = ({ data }) => {
  if (!data || !data.labels) return null
  return (
    <div className="table-responsive">
      <table className="table table-sm table-bordered">
        <thead>
          <tr>
            {data.labels?.map((l, i) => <th key={i} className="text-center">{l}</th>)}
          </tr>
        </thead>
        <tbody>
          <tr>
            {data.values?.map((v, i) => (
              <td key={i} className="text-center font-weight-bold" style={{ color: '#590E9C' }}>
                {parseFloat(v).toFixed(3)} DT
              </td>
            ))}
          </tr>
        </tbody>
      </table>
    </div>
  )
}

const TechnoRepartitionCard = () => {
  const [data, setData] = useState(null)
  useEffect(() => {
    dashboardService.getTechnoRepartition().then(setData).catch(() => {})
  }, [])
  return (
    <div className="card mb-1" style={{ borderRadius: 11, boxShadow: '0px 3px 6px #00000029' }}>
      <div className="card-header border-0">
        <h3 className="card-title" style={{ fontWeight: 700 }}>Répartition des technologies d&apos;abonnement</h3>
      </div>
      <div className="card-body">
        {data ? (
          <ul className="list-group list-group-flush">
            {Object.entries(data).map(([key, val]) => (
              <li key={key} className="list-group-item d-flex justify-content-between align-items-center px-0">
                {key}
                <span className="badge badge-primary badge-pill">{val}</span>
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-muted text-center">Chargement...</p>
        )}
      </div>
    </div>
  )
}

const TopRevendeursCard = () => {
  const [data, setData] = useState([])
  const [filter, setFilter] = useState('all')
  const [loading, setLoading] = useState(false)

  const load = (f) => {
    setLoading(true)
    setFilter(f)
    dashboardService.getTopRevendeurs(f)
      .then(setData)
      .catch(() => {})
      .finally(() => setLoading(false))
  }

  useEffect(() => { load('all') }, [])

  return (
    <div className="card">
      <div className="card-header border-0 d-flex align-items-center justify-content-between">
        <h3 className="card-title mb-0">10 Derniers commissions payées</h3>
        <div>
          {[['all','Tous'],['lastMonth','Mois précédent'],['currentYear','Année courante']].map(([f, l]) => (
            <button
              key={f}
              className={`btn btn-sm btn-primary mr-1${filter === f ? ' active' : ''}`}
              onClick={() => load(f)}
            >{l}</button>
          ))}
        </div>
      </div>
      <div className="card-body p-0">
        <div className="table-responsive">
          <table className="table table-bordered mb-0">
            <thead>
              <tr>
                <th className="text-center">Revendeur</th>
                <th className="text-center">Commission (TND)</th>
                <th className="text-center">Mois/Année</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td colSpan="3" className="text-center"><div className="spinner-border spinner-border-sm text-primary"></div></td></tr>
              ) : data.length === 0 ? (
                <tr><td colSpan="3" className="text-center text-muted">Rien trouvé - désolé</td></tr>
              ) : data.map((r, i) => (
                <tr key={i}>
                  <td className="text-center">{r.revendeur?.firstName} {r.revendeur?.lastName} ({r.revendeur?.codeUser})</td>
                  <td className="text-center">{r.formattedTotalTtc}</td>
                  <td className="text-center">{r.monthName}/{r.annee}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

const AbonnementsByGouvernoratCard = () => {
  const [data, setData] = useState([])
  useEffect(() => {
    dashboardService.getAbonnementsByGouvernorat().then(d => setData(d || [])).catch(() => {})
  }, [])
  return (
    <div className="card">
      <div className="card-header border-0">
        <h3 className="card-title">Répartition des abonnements actifs par gouvernorat</h3>
      </div>
      <div className="card-body p-0">
        <div className="table-responsive">
          <table className="table table-bordered mb-0">
            <thead>
              <tr>
                <th className="text-center">Gouvernorat</th>
                <th className="text-center">Abonnements</th>
              </tr>
            </thead>
            <tbody>
              {data.length === 0 ? (
                <tr><td colSpan="2" className="text-center text-muted">Aucune donnée</td></tr>
              ) : data.map((r, i) => (
                <tr key={i}>
                  <td className="text-center">{r.gouvernorat || r.label}</td>
                  <td className="text-center">{r.count || r.value}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

export default AdminDashboard
