import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { fetchOtherStats } from '../../redux/slices/dashboardSlice'
import { usePermissions } from '../../hooks/usePermissions'
import PageHeader from '../../components/common/PageHeader'
import dashboardService from '../../services/dashboardService'

const OtherDashboard = () => {
  const dispatch = useDispatch()
  const { otherStats, isLoading } = useSelector((state) => state.dashboard)
  const { hasAnyAuthority } = usePermissions()
  const s = otherStats || {}

  useEffect(() => {
    dispatch(fetchOtherStats())
  }, [dispatch])

  const [encaissement, setEncaissement] = useState(null)
  useEffect(() => {
    dashboardService.getEncaissementPlafon().then(setEncaissement).catch(() => {})
  }, [])

  return (
    <section className="content">
      <PageHeader title="Tableau de bord" />

      <div className="text-center mb-3">
        <img src="/img/Banner5GCRM.jpg" alt="Nety" className="rounded" style={{ width: '70%' }} />
      </div>

      {(s.ContHorsEcheance || s.ContLimit) && (
        <div className="row justify-content-center ml-3">
          <div className="col-12 col-md-8">
            <div className="alert alert-danger alert-dismissible text-white" role="alert"
              style={{ backgroundColor: '#eb4844', borderRadius: 20 }}>
              <h3 className="mb-2 ml-4 text-white"><strong>Attention !!</strong></h3>
              {s.ContHorsEcheance > 0 && (
                <h5 className="text-white mt-2 ml-4 mb-2">
                  - Vous serez bloqué en raison du nombre de factures non versées qui ont dépassé les 25 jours
                  (nombre de factures non versées: {s.ContHorsEcheance})
                </h5>
              )}
              {s.ContLimit > 0 && (
                <h5 className="text-white mt-0 ml-4 mb-0">
                  - Veuillez vous dépêcher pour verser ces factures avant de dépasser les 25 jours
                  (nombre de factures non versées: {s.ContLimit})
                </h5>
              )}
            </div>
          </div>
        </div>
      )}

      <div className="container-fluid">
        <div className="row justify-content-center mt-2">
          {hasAnyAuthority('READ_CARD_NEW_CLIENTS_OTHERS') && (
            <MiniStatCard
              title="Nouveaux clients"
              value={s.nouvelleAbonnementsThisMonth}
              sub={s.percentageChange}
              subColor={s.percentageChange?.startsWith('-') ? 'red' : 'green'}
              imgSrc="/img/newClient.png"
              isLoading={isLoading}
            />
          )}
          {hasAnyAuthority('READ_CARD_REV_OF_DIST') && (
            <MiniStatCard
              title="Total Revendeurs Désactivés"
              value={s.NbreRevendeur}
              sub={s.NbreRevendeurActive}
              imgSrc="/img/RevDesac.png"
              isLoading={isLoading}
            />
          )}
          {hasAnyAuthority('READ_CARD_AFFECT_MODEM_OTHERS') && (
            <MiniStatCard
              title="Modems afféctés"
              value={s.nbrModemAffectedToday}
              sub={s.nbrModemAffectedCurrentMonth}
              imgSrc="/img/affectedModem.png"
              isLoading={isLoading}
            />
          )}
          {hasAnyAuthority('READ_CARD_RECIEVED_MODEMS') && (
            <MiniStatCard
              title="Modems Reçus"
              value={s.nbrModemRecievedToday}
              sub={s.nbrModemRecievedCurrentMonth}
              imgSrc="/img/affectedModem.png"
              isLoading={isLoading}
            />
          )}
          {hasAnyAuthority('READ_CARD_DEM_MODEM_OTHERS') && (
            <MiniStatCard
              title="Modems Demandés"
              value={s.nbrDemandModemToday}
              sub={s.nbrDemandModemCurrentMonth}
              imgSrc="/img/demandModem.png"
              isLoading={isLoading}
            />
          )}
          {hasAnyAuthority('READ_CARD_DABON_RESIL_OTHERS') && (
            <MiniStatCard
              title="Abonnements Résiliés"
              value={s.resilierAbonnementThisMonth}
              sub={s.resilierAbonnementLastMonth}
              imgSrc="/img/AbonResil.png"
              isLoading={isLoading}
            />
          )}
        </div>
      </div>
    </section>
  )
}

const MiniStatCard = ({ title, value, sub, subColor, imgSrc, isLoading }) => (
  <div className="col-xl-2 col-lg-2 col-md-3 col-sm-6 grid-margin stretch-card ml-3">
    <div className="card card-statistics" style={{ background: '#fff', borderRadius: 5, boxShadow: '0 4px 8px rgba(0,0,0,0.1)' }}>
      <div className="card-body">
        <div className="clearfix">
          <div className="float-right">
            <img src={imgSrc} width="50px" style={{ marginLeft: 10 }} alt="" />
          </div>
          <div className="float-left ml-4">
            <p className="mb-0 text-left text-muted"><strong>{title}</strong></p>
            <div>
              {isLoading
                ? <div className="spinner-border spinner-border-sm text-primary"></div>
                : <h4 className="font-weight-medium text-left mb-0">{value ?? '-'}</h4>}
            </div>
          </div>
        </div>
        {sub !== undefined && (
          <p className="text-muted mt-0 mb-3 ml-4" style={subColor ? { color: subColor } : {}}>
            {sub}
          </p>
        )}
      </div>
    </div>
  </div>
)

export default OtherDashboard
