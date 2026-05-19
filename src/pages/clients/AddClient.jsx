import React, { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import { createClient, reset } from '../../redux/slices/clientsSlice'
import PageHeader from '../../components/common/PageHeader'
import FormField from '../../components/common/FormField'
import Alert from '../../components/common/Alert'
import referentielsService from '../../services/referentielsService'

const AddClient = () => {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { isLoading, isSuccess, isError, message } = useSelector((state) => state.clients)

  const [gouvernorats, setGouvernorats] = useState([])
  const [villes, setVilles] = useState([])
  const [professions, setProfessions] = useState([])

  const [formData, setFormData] = useState({
    firstName: '', lastName: '', cin: '', telephone: '', email: '',
    adresse: '', gouvernoratId: '', villeId: '', professionId: '',
    dateNaissance: '', sexe: '',
  })
  const [errors, setErrors] = useState({})

  useEffect(() => {
    referentielsService.getGouvernorats().then(setGouvernorats).catch(() => {})
    referentielsService.getProfessions().then(setProfessions).catch(() => {})
    return () => dispatch(reset())
  }, [dispatch])

  useEffect(() => {
    if (isSuccess) navigate('/client/allclients')
  }, [isSuccess, navigate])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
    if (name === 'gouvernoratId' && value) {
      referentielsService.getVilles(value).then(setVilles).catch(() => {})
    }
  }

  const validate = () => {
    const errs = {}
    if (!formData.firstName) errs.firstName = 'Prénom requis'
    if (!formData.lastName) errs.lastName = 'Nom requis'
    if (!formData.cin) errs.cin = 'CIN requis'
    if (!formData.telephone) errs.telephone = 'Téléphone requis'
    setErrors(errs)
    return Object.keys(errs).length === 0
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    if (validate()) dispatch(createClient(formData))
  }

  return (
    <section className="content">
      <PageHeader title="Ajouter un client" breadcrumb={[{ label: 'Clients', path: '/client/allclients' }, { label: 'Ajouter' }]} />
      <div className="container-fluid">
        <div className="card shadow">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Nouveau client</h6>
          </div>
          <div className="card-body">
            {isError && <Alert type="danger" message={message} />}
            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6">
                  <FormField label="Prénom" name="firstName" value={formData.firstName} onChange={handleChange} error={errors.firstName} required />
                </div>
                <div className="col-md-6">
                  <FormField label="Nom" name="lastName" value={formData.lastName} onChange={handleChange} error={errors.lastName} required />
                </div>
                <div className="col-md-6">
                  <FormField label="CIN" name="cin" value={formData.cin} onChange={handleChange} error={errors.cin} required />
                </div>
                <div className="col-md-6">
                  <FormField label="Téléphone" name="telephone" value={formData.telephone} onChange={handleChange} error={errors.telephone} required />
                </div>
                <div className="col-md-6">
                  <FormField label="Email" name="email" type="email" value={formData.email} onChange={handleChange} />
                </div>
                <div className="col-md-6">
                  <FormField label="Date de naissance" name="dateNaissance" type="date" value={formData.dateNaissance} onChange={handleChange} />
                </div>
                <div className="col-md-6">
                  <FormField label="Sexe" name="sexe" type="select" value={formData.sexe} onChange={handleChange}>
                    <option value="">Choisir</option>
                    <option value="M">Masculin</option>
                    <option value="F">Féminin</option>
                  </FormField>
                </div>
                <div className="col-md-6">
                  <FormField label="Profession" name="professionId" type="select" value={formData.professionId} onChange={handleChange}>
                    <option value="">Choisir une profession</option>
                    {professions.map(p => <option key={p.id} value={p.id}>{p.professionName || p.name}</option>)}
                  </FormField>
                </div>
                <div className="col-md-12">
                  <FormField label="Adresse" name="adresse" type="textarea" value={formData.adresse} onChange={handleChange} rows={2} />
                </div>
                <div className="col-md-6">
                  <FormField label="Gouvernorat" name="gouvernoratId" type="select" value={formData.gouvernoratId} onChange={handleChange}>
                    <option value="">Choisir un gouvernorat</option>
                    {gouvernorats.map(g => <option key={g.gouvernoratId || g.id} value={g.gouvernoratId || g.id}>{g.gouvernoratName || g.name}</option>)}
                  </FormField>
                </div>
                <div className="col-md-6">
                  <FormField label="Ville" name="villeId" type="select" value={formData.villeId} onChange={handleChange}>
                    <option value="">Choisir une ville</option>
                    {villes.map(v => <option key={v.villeId || v.id} value={v.villeId || v.id}>{v.villeName || v.name}</option>)}
                  </FormField>
                </div>
              </div>
              <div className="mt-3">
                <button type="submit" className="btn btn-primary mr-2" disabled={isLoading}>
                  {isLoading ? 'Enregistrement...' : 'Enregistrer'}
                </button>
                <button type="button" className="btn btn-secondary" onClick={() => navigate('/client/allclients')}>Annuler</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </section>
  )
}

export default AddClient
