import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import abonnementsService from '../../services/abonnementsService'

const VerificationCin = () => {
  const navigate = useNavigate()
  const [cin, setCin] = useState('')
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)

  const handleVerify = async (e) => {
    e.preventDefault()
    if (!cin) return
    setLoading(true)
    setError(null)
    try {
      const data = await abonnementsService.verifierCIN(cin)
      setResult(data)
    } catch {
      setError('CIN introuvable ou erreur de vérification')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="content">
      <PageHeader title="Vérification CIN" breadcrumb={[{ label: 'Abonnements' }, { label: 'Vérification CIN' }]} />
      <div className="container-fluid">
        <div className="card shadow" style={{ maxWidth: 600 }}>
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Vérification du CIN</h6>
          </div>
          <div className="card-body">
            <form onSubmit={handleVerify}>
              <div className="input-group mb-3">
                <input
                  type="text"
                  className="form-control"
                  placeholder="Entrez le numéro CIN"
                  value={cin}
                  onChange={e => setCin(e.target.value)}
                  required
                />
                <div className="input-group-append">
                  <button type="submit" className="btn btn-primary" disabled={loading}>
                    {loading ? 'Vérification...' : 'Vérifier'}
                  </button>
                </div>
              </div>
            </form>

            {error && <div className="alert alert-danger">{error}</div>}

            {result && (
              <div>
                {result.exists ? (
                  <div className="alert alert-warning">
                    <strong>Client existant trouvé!</strong>
                    <p className="mb-1">Nom: {result.client?.firstName} {result.client?.lastName}</p>
                    <p className="mb-1">Téléphone: {result.client?.telephone}</p>
                    <p className="mb-0">Code: {result.client?.codeClient}</p>
                    <div className="mt-3">
                      <button
                        className="btn btn-primary mr-2"
                        onClick={() => navigate('/demandeabonnement/adddemandeabonnementwithsteps', { state: { cin, client: result.client } })}
                      >
                        Créer abonnement pour ce client
                      </button>
                      <button className="btn btn-info" onClick={() => navigate(`/client/viewclient/${result.client?.clientId}`)}>
                        Voir le client
                      </button>
                    </div>
                  </div>
                ) : (
                  <div className="alert alert-success">
                    <strong>Nouveau client</strong>
                    <p className="mb-2">Ce CIN n&apos;existe pas dans le système. Vous pouvez créer un nouvel abonnement.</p>
                    <button
                      className="btn btn-primary"
                      onClick={() => navigate('/demandeabonnement/adddemandeabonnementwithsteps', { state: { cin } })}
                    >
                      Créer un abonnement
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </section>
  )
}

export default VerificationCin
