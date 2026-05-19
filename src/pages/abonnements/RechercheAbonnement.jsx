import React, { useState } from 'react'
import { Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import DataTable from '../../components/common/DataTable'
import abonnementsService from '../../services/abonnementsService'
import { formatDate } from '../../utils/helpers'

const RechercheAbonnement = () => {
  const [filters, setFilters] = useState({ cin: '', nom: '', prenom: '', reference: '', tel: '', loginModem: '' })
  const [results, setResults] = useState([])
  const [loading, setLoading] = useState(false)
  const [searched, setSearched] = useState(false)

  const handleSearch = async (e) => {
    e.preventDefault()
    setLoading(true)
    setSearched(true)
    try {
      const data = await abonnementsService.recherche(filters)
      setResults(data?.content || data || [])
    } catch {
      setResults([])
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (e) => {
    const { name, value } = e.target
    setFilters(prev => ({ ...prev, [name]: value }))
  }

  const columns = [
    { header: 'Référence', field: 'reference' },
    { header: 'Client', render: (r) => `${r.client?.firstName || ''} ${r.client?.lastName || ''}` },
    { header: 'CIN', render: (r) => r.client?.cin || '-' },
    { header: 'Téléphone', render: (r) => r.client?.telephone || '-' },
    { header: 'Offre', render: (r) => r.offre?.offreName || '-' },
    { header: 'Statut', render: (r) => (
      <span className={`badge badge-${r.statut === 'Validé' ? 'success' : r.statut === 'Refusé' ? 'danger' : 'secondary'}`}>
        {r.statut || '-'}
      </span>
    )},
    { header: 'Date', render: (r) => formatDate(r.dateCreation) },
    {
      header: 'Actions',
      render: (r) => (
        <Link to={`/demandeabonnement/getdemandeabonnement/${r.demandeId || r.id}`} className="btn btn-info btn-sm">
          <i className="fas fa-eye"></i>
        </Link>
      )
    },
  ]

  return (
    <section className="content">
      <PageHeader title="Recherche d'abonnement" breadcrumb={[{ label: 'Abonnements' }, { label: 'Recherche' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Recherche d&apos;abonnement</h6>
          </div>
          <div className="card-body">
            <form onSubmit={handleSearch} className="mb-4">
              <div className="row">
                <div className="col-md-2">
                  <div className="form-group">
                    <label>CIN</label>
                    <input className="form-control" name="cin" value={filters.cin} onChange={handleChange} placeholder="CIN" />
                  </div>
                </div>
                <div className="col-md-2">
                  <div className="form-group">
                    <label>Nom</label>
                    <input className="form-control" name="nom" value={filters.nom} onChange={handleChange} placeholder="Nom" />
                  </div>
                </div>
                <div className="col-md-2">
                  <div className="form-group">
                    <label>Prénom</label>
                    <input className="form-control" name="prenom" value={filters.prenom} onChange={handleChange} placeholder="Prénom" />
                  </div>
                </div>
                <div className="col-md-2">
                  <div className="form-group">
                    <label>Référence</label>
                    <input className="form-control" name="reference" value={filters.reference} onChange={handleChange} placeholder="Référence" />
                  </div>
                </div>
                <div className="col-md-2">
                  <div className="form-group">
                    <label>Téléphone</label>
                    <input className="form-control" name="tel" value={filters.tel} onChange={handleChange} placeholder="Téléphone" />
                  </div>
                </div>
                <div className="col-md-2">
                  <div className="form-group">
                    <label>Login Modem</label>
                    <input className="form-control" name="loginModem" value={filters.loginModem} onChange={handleChange} placeholder="Login Modem" />
                  </div>
                </div>
              </div>
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? <><span className="spinner-border spinner-border-sm mr-1"></span>Recherche...</> : <><i className="fas fa-search mr-1"></i> Rechercher</>}
              </button>
            </form>

            {searched && (
              <DataTable
                columns={columns}
                data={results}
                loading={loading}
                emptyMessage="Aucun résultat trouvé"
              />
            )}
          </div>
        </div>
      </div>
    </section>
  )
}

export default RechercheAbonnement
