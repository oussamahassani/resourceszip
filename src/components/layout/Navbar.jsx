import React, { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link } from 'react-router-dom'
import { toggleSidebar } from '../../redux/slices/uiSlice'
import { logout } from '../../redux/slices/authSlice'
import { useNavigate } from 'react-router-dom'
import { usePermissions } from '../../hooks/usePermissions'
import dashboardService from '../../services/dashboardService'

const Navbar = () => {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { user } = useSelector((state) => state.auth)
  const { hasAnyAuthority } = usePermissions()
  const [encaissement, setEncaissement] = useState(null)
  const [serverName, setServerName] = useState('')

  useEffect(() => {
    if (hasAnyAuthority('VIEW_MONTANT_ENCAISSE')) {
      dashboardService.getEncaissementPlafon()
        .then(data => {
          setEncaissement(data)
          setServerName(data?.serverNameCRM || '')
        })
        .catch(() => {})
    }
  }, [])

  const handleLogout = async () => {
    await dispatch(logout())
    navigate('/login')
  }

  const userPhoto = user?.photo
  const userEmail = user?.email

  return (
    <nav className="main-header navbar navbar-expand navbar-white navbar-light">
      <ul className="navbar-nav">
        <li className="nav-item">
          <a className="nav-link" href="#" role="button" onClick={(e) => { e.preventDefault(); dispatch(toggleSidebar()) }}>
            <i className="fas fa-bars"></i>{' '}
            <span>{serverName}</span>
          </a>
        </li>
      </ul>

      {hasAnyAuthority('VIEW_MONTANT_ENCAISSE') && (
        <li className="navbar-nav ml-4" style={{ listStyle: 'none' }}>
          <Link to="/demandeabonnement/adddemandeabonnementwithsteps" className="text-center">
            La précommande de l&apos;offre 05G est maintenant disponible chez Nety.
          </Link>
        </li>
      )}

      <ul className="navbar-nav ml-auto">
        {hasAnyAuthority('VIEW_MONTANT_ENCAISSE') && encaissement && (
          <li className="mr-3 mt-3">
            <h6 className="animate-charcter">
              Montant non versé : <span>{encaissement.total ? parseFloat(encaissement.total).toFixed(3) : '0.000'} DT</span>
              &emsp;/&emsp;
              Plafond autorisé : <span>{encaissement.plafon ? parseFloat(encaissement.plafon) : '0'} DT</span>
            </h6>
          </li>
        )}

        <li className="nav-item">
          <a className="nav-link" href="#" role="button" onClick={(e) => {
            e.preventDefault()
            if (document.fullscreenElement) {
              document.exitFullscreen()
            } else {
              document.documentElement.requestFullscreen()
            }
          }}>
            <i className="fas fa-expand-arrows-alt"></i>
          </a>
        </li>

        <li>
          {hasAnyAuthority('UPDATE_PROFILE_REVENDEUR') && (
            <div className="nav-item dropdown">
              <a href="#" role="button" data-toggle="dropdown" className="d-block">
                <div className="user-panel p-1 d-flex">
                  <div className="image">
                    {userPhoto ? (
                      <img
                        className="img-circle elevation-2"
                        src={`/photos/${userEmail}/${userPhoto}`}
                        alt="Profile"
                        style={{ width: 35, height: 35, objectFit: 'cover' }}
                      />
                    ) : (
                      <img
                        className="img-circle elevation-2"
                        src="/img/user2-160x160.jpg"
                        alt="Profile"
                        style={{ width: 35, height: 35, objectFit: 'cover' }}
                      />
                    )}
                  </div>
                </div>
              </a>
              <div className="dropdown-menu dropdown-menu-right">
                <Link className="dropdown-item" to="/RevendeurUser/users/mon-profile">
                  <i className="fas fa-user mr-2"></i> Modifier le profil
                </Link>
              </div>
            </div>
          )}
          <a className="nav-link" role="button" onClick={handleLogout} style={{ cursor: 'pointer' }}>
            <i className="fas fa-sign-out-alt"></i> Déconnexion
          </a>
        </li>
      </ul>
    </nav>
  )
}

export default Navbar
