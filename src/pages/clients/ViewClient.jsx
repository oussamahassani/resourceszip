import React, { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import Loader from '../../components/common/Loader'
import clientsService from '../../services/clientsService'
import { formatDate } from '../../utils/helpers'

const ViewClient = () => {
  const { id } = useParams()
  const [client, setClient] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    clientsService.getById(id)
      .then(setClient)
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <Loader />

  const c = client || {}

  return (
    <section className="content">
      <PageHeader title="Détails client" breadcrumb={[{ label: 'Clients', path: '/client/allclients' }, { label: 'Détails' }]} />
      <div className="container-fluid">
        <div className="row">
          <div className="col-md-4">
            <div className="card card-primary card-outline">
              <div className="card-body box-profile">
                <div className="text-center">
                  <img className="profile-user-img img-fluid img-circle"
                    src="/img/default-150x150.png" alt="Profile" />
                </div>
                <h3 className="profile-username text-center">{c.firstName} {c.lastName}</h3>
                <p className="text-muted text-center">{c.codeClient}</p>
                <ul className="list-group list-group-unbordered mb-3">
                  <li className="list-group-item">
                    <b>Statut</b>
                    <span className={`float-right badge badge-${c.statut === 'Actif' ? 'success' : c.statut === 'Résilié' ? 'danger' : 'secondary'}`}>
                      {c.statut || '-'}
                    </span>
                  </li>
                  <li className="list-group-item"><b>CIN</b><span className="float-right">{c.cin || '-'}</span></li>
                  <li className="list-group-item"><b>Téléphone</b><span className="float-right">{c.telephone || '-'}</span></li>
                  <li className="list-group-item"><b>Email</b><span className="float-right">{c.email || '-'}</span></li>
                </ul>
                <Link to={`/client/editclient/${id}`} className="btn btn-warning btn-block">
                  <i className="fas fa-edit mr-1"></i> Modifier
                </Link>
              </div>
            </div>
          </div>

          <div className="col-md-8">
            <div className="card">
              <div className="card-header">
                <h3 className="card-title">Informations personnelles</h3>
              </div>
              <div className="card-body">
                <div className="row">
                  <div className="col-sm-6">
                    <div className="form-group">
                      <label>Prénom</label>
                      <p className="form-control-plaintext">{c.firstName || '-'}</p>
                    </div>
                  </div>
                  <div className="col-sm-6">
                    <div className="form-group">
                      <label>Nom</label>
                      <p className="form-control-plaintext">{c.lastName || '-'}</p>
                    </div>
                  </div>
                  <div className="col-sm-6">
                    <div className="form-group">
                      <label>Date de naissance</label>
                      <p className="form-control-plaintext">{formatDate(c.dateNaissance)}</p>
                    </div>
                  </div>
                  <div className="col-sm-6">
                    <div className="form-group">
                      <label>Sexe</label>
                      <p className="form-control-plaintext">{c.sexe === 'M' ? 'Masculin' : c.sexe === 'F' ? 'Féminin' : '-'}</p>
                    </div>
                  </div>
                  <div className="col-sm-6">
                    <div className="form-group">
                      <label>Gouvernorat</label>
                      <p className="form-control-plaintext">{c.gouvernorat?.gouvernoratName || '-'}</p>
                    </div>
                  </div>
                  <div className="col-sm-6">
                    <div className="form-group">
                      <label>Ville</label>
                      <p className="form-control-plaintext">{c.ville?.villeName || '-'}</p>
                    </div>
                  </div>
                  <div className="col-sm-12">
                    <div className="form-group">
                      <label>Adresse</label>
                      <p className="form-control-plaintext">{c.adresse || '-'}</p>
                    </div>
                  </div>
                  <div className="col-sm-6">
                    <div className="form-group">
                      <label>Date création</label>
                      <p className="form-control-plaintext">{formatDate(c.dateCreation)}</p>
                    </div>
                  </div>
                  <div className="col-sm-6">
                    <div className="form-group">
                      <label>Créé par</label>
                      <p className="form-control-plaintext">{c.createdBy || '-'}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

export default ViewClient
