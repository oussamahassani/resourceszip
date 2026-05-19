import React, { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import { createAbonnement, reset } from '../../redux/slices/abonnementsSlice'
import PageHeader from '../../components/common/PageHeader'
import FormField from '../../components/common/FormField'
import Alert from '../../components/common/Alert'
import referentielsService from '../../services/referentielsService'

const AddAbonnement = () => {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { isLoading, isSuccess, isError, message } = useSelector((state) => state.abonnements)

  const [step, setStep] = useState(1)
  const [offres, setOffres] = useState([])
  const [packs, setPacks] = useState([])
  const [gouvernorats, setGouvernorats] = useState([])
  const [villes, setVilles] = useState([])
  const [professions, setProfessions] = useState([])
  const [typesPaiement, setTypesPaiement] = useState([])
  const [zones, setZones] = useState([])

  const [formData, setFormData] = useState({
    clientFirstName: '', clientLastName: '', clientCin: '', clientTel: '',
    clientEmail: '', clientAdresse: '', clientGouvernoratId: '', clientVilleId: '',
    clientProfessionId: '', clientDateNaissance: '', clientSexe: '',
    offreId: '', packId: '', zoneId: '', typePaiementId: '',
    loginModem: '', serialModem: '', dateInstallation: '',
    commentaire: '',
  })
  const [cinVerified, setCinVerified] = useState(false)
  const [cinChecking, setCinChecking] = useState(false)
  const [existingClient, setExistingClient] = useState(null)

  useEffect(() => {
    referentielsService.getOffres().then(setOffres).catch(() => {})
    referentielsService.getPacks().then(setPacks).catch(() => {})
    referentielsService.getGouvernorats().then(setGouvernorats).catch(() => {})
    referentielsService.getProfessions().then(setProfessions).catch(() => {})
    referentielsService.getTypesPaiement().then(setTypesPaiement).catch(() => {})
    referentielsService.getZones().then(setZones).catch(() => {})
    return () => dispatch(reset())
  }, [dispatch])

  useEffect(() => {
    if (isSuccess) navigate('/demandeabonnement/alldemandesabonnement/1/20')
  }, [isSuccess, navigate])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
    if (name === 'clientGouvernoratId' && value) {
      referentielsService.getVilles(value).then(setVilles).catch(() => {})
    }
  }

  const handleVerifyCIN = async () => {
    if (!formData.clientCin) return
    setCinChecking(true)
    try {
      const result = await import('../../services/abonnementsService').then(m => m.default.verifierCIN(formData.clientCin))
      if (result?.exists && result?.client) {
        setExistingClient(result.client)
        setFormData(prev => ({
          ...prev,
          clientFirstName: result.client.firstName || '',
          clientLastName: result.client.lastName || '',
          clientTel: result.client.telephone || '',
          clientEmail: result.client.email || '',
          clientAdresse: result.client.adresse || '',
        }))
      }
      setCinVerified(true)
    } catch {
      setCinVerified(true)
    } finally {
      setCinChecking(false)
    }
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    dispatch(createAbonnement(formData))
  }

  return (
    <section className="content">
      <PageHeader title="Nouvel abonnement" breadcrumb={[{ label: 'Abonnements', path: '/demandeabonnement/alldemandesabonnement/1/20' }, { label: 'Nouveau' }]} />
      <div className="container-fluid">
        <div className="card shadow">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Demande d&apos;abonnement</h6>
          </div>
          <div className="card-body">
            {isError && <Alert type="danger" message={message} />}

            <div className="steps-indicator mb-4">
              {[1, 2, 3].map(s => (
                <button
                  key={s}
                  type="button"
                  className={`btn btn-sm mr-2 ${step === s ? 'btn-primary' : step > s ? 'btn-success' : 'btn-outline-secondary'}`}
                  onClick={() => s <= step && setStep(s)}
                >
                  Étape {s}
                </button>
              ))}
            </div>

            <form onSubmit={handleSubmit}>
              {step === 1 && (
                <div>
                  <h5 className="mb-3">Vérification CIN</h5>
                  <div className="row">
                    <div className="col-md-6">
                      <div className="input-group mb-3">
                        <input
                          type="text"
                          className="form-control"
                          placeholder="Numéro CIN"
                          name="clientCin"
                          value={formData.clientCin}
                          onChange={handleChange}
                          required
                        />
                        <div className="input-group-append">
                          <button type="button" className="btn btn-primary" onClick={handleVerifyCIN} disabled={cinChecking}>
                            {cinChecking ? 'Vérification...' : 'Vérifier'}
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>

                  {existingClient && (
                    <div className="alert alert-info">
                      Client existant trouvé: <strong>{existingClient.firstName} {existingClient.lastName}</strong>
                    </div>
                  )}

                  {(cinVerified || existingClient) && (
                    <div className="row">
                      <div className="col-md-6">
                        <FormField label="Prénom" name="clientFirstName" value={formData.clientFirstName} onChange={handleChange} required />
                      </div>
                      <div className="col-md-6">
                        <FormField label="Nom" name="clientLastName" value={formData.clientLastName} onChange={handleChange} required />
                      </div>
                      <div className="col-md-6">
                        <FormField label="Téléphone" name="clientTel" value={formData.clientTel} onChange={handleChange} required />
                      </div>
                      <div className="col-md-6">
                        <FormField label="Email" name="clientEmail" type="email" value={formData.clientEmail} onChange={handleChange} />
                      </div>
                      <div className="col-md-6">
                        <FormField label="Date de naissance" name="clientDateNaissance" type="date" value={formData.clientDateNaissance} onChange={handleChange} />
                      </div>
                      <div className="col-md-6">
                        <FormField label="Sexe" name="clientSexe" type="select" value={formData.clientSexe} onChange={handleChange}>
                          <option value="">Choisir</option>
                          <option value="M">Masculin</option>
                          <option value="F">Féminin</option>
                        </FormField>
                      </div>
                      <div className="col-md-6">
                        <FormField label="Gouvernorat" name="clientGouvernoratId" type="select" value={formData.clientGouvernoratId} onChange={handleChange}>
                          <option value="">Choisir un gouvernorat</option>
                          {gouvernorats.map(g => <option key={g.gouvernoratId || g.id} value={g.gouvernoratId || g.id}>{g.gouvernoratName || g.name}</option>)}
                        </FormField>
                      </div>
                      <div className="col-md-6">
                        <FormField label="Ville" name="clientVilleId" type="select" value={formData.clientVilleId} onChange={handleChange}>
                          <option value="">Choisir une ville</option>
                          {villes.map(v => <option key={v.villeId || v.id} value={v.villeId || v.id}>{v.villeName || v.name}</option>)}
                        </FormField>
                      </div>
                      <div className="col-12">
                        <FormField label="Adresse" name="clientAdresse" type="textarea" value={formData.clientAdresse} onChange={handleChange} rows={2} />
                      </div>
                    </div>
                  )}

                  {cinVerified && (
                    <button type="button" className="btn btn-primary" onClick={() => setStep(2)}>
                      Suivant <i className="fas fa-arrow-right ml-1"></i>
                    </button>
                  )}
                </div>
              )}

              {step === 2 && (
                <div>
                  <h5 className="mb-3">Choix de l&apos;offre</h5>
                  <div className="row">
                    <div className="col-md-6">
                      <FormField label="Offre" name="offreId" type="select" value={formData.offreId} onChange={handleChange}>
                        <option value="">Choisir une offre</option>
                        {offres.map(o => <option key={o.offreId || o.id} value={o.offreId || o.id}>{o.offreName || o.name}</option>)}
                      </FormField>
                    </div>
                    <div className="col-md-6">
                      <FormField label="Pack" name="packId" type="select" value={formData.packId} onChange={handleChange}>
                        <option value="">Choisir un pack</option>
                        {packs.map(p => <option key={p.packId || p.id} value={p.packId || p.id}>{p.packName || p.name}</option>)}
                      </FormField>
                    </div>
                    <div className="col-md-6">
                      <FormField label="Zone" name="zoneId" type="select" value={formData.zoneId} onChange={handleChange}>
                        <option value="">Choisir une zone</option>
                        {zones.map(z => <option key={z.zoneId || z.id} value={z.zoneId || z.id}>{z.zoneName || z.name}</option>)}
                      </FormField>
                    </div>
                    <div className="col-md-6">
                      <FormField label="Type de paiement" name="typePaiementId" type="select" value={formData.typePaiementId} onChange={handleChange}>
                        <option value="">Choisir un type</option>
                        {typesPaiement.map(t => <option key={t.id} value={t.id}>{t.typePaiementName || t.name}</option>)}
                      </FormField>
                    </div>
                  </div>
                  <div className="mt-3">
                    <button type="button" className="btn btn-secondary mr-2" onClick={() => setStep(1)}>
                      <i className="fas fa-arrow-left mr-1"></i> Précédent
                    </button>
                    <button type="button" className="btn btn-primary" onClick={() => setStep(3)}>
                      Suivant <i className="fas fa-arrow-right ml-1"></i>
                    </button>
                  </div>
                </div>
              )}

              {step === 3 && (
                <div>
                  <h5 className="mb-3">Détails techniques</h5>
                  <div className="row">
                    <div className="col-md-6">
                      <FormField label="Login Modem" name="loginModem" value={formData.loginModem} onChange={handleChange} />
                    </div>
                    <div className="col-md-6">
                      <FormField label="N° Série Modem" name="serialModem" value={formData.serialModem} onChange={handleChange} />
                    </div>
                    <div className="col-md-6">
                      <FormField label="Date d'installation" name="dateInstallation" type="date" value={formData.dateInstallation} onChange={handleChange} />
                    </div>
                    <div className="col-12">
                      <FormField label="Commentaire" name="commentaire" type="textarea" value={formData.commentaire} onChange={handleChange} rows={3} />
                    </div>
                  </div>
                  <div className="mt-3">
                    <button type="button" className="btn btn-secondary mr-2" onClick={() => setStep(2)}>
                      <i className="fas fa-arrow-left mr-1"></i> Précédent
                    </button>
                    <button type="submit" className="btn btn-success" disabled={isLoading}>
                      {isLoading ? 'Enregistrement...' : <><i className="fas fa-check mr-1"></i> Soumettre</>}
                    </button>
                  </div>
                </div>
              )}
            </form>
          </div>
        </div>
      </div>
    </section>
  )
}

export default AddAbonnement
