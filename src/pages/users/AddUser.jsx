import React, { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import { createUser, reset } from '../../redux/slices/usersSlice'
import PageHeader from '../../components/common/PageHeader'
import FormField from '../../components/common/FormField'
import Alert from '../../components/common/Alert'
import usersService from '../../services/usersService'

const AddUser = () => {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { isLoading, isSuccess, isError, message } = useSelector((state) => state.users)

  const [roles, setRoles] = useState([])
  const [formData, setFormData] = useState({
    firstName: '', lastName: '', email: '', username: '', password: '',
    phone: '', codeUser: '', roleId: '', zoneId: '',
  })
  const [errors, setErrors] = useState({})

  useEffect(() => {
    usersService.getRoles().then(data => setRoles(data || [])).catch(() => {})
    return () => dispatch(reset())
  }, [dispatch])

  useEffect(() => {
    if (isSuccess) {
      navigate('/admin/users/allusers/1')
    }
  }, [isSuccess, navigate])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }))
  }

  const validate = () => {
    const errs = {}
    if (!formData.firstName) errs.firstName = 'Prénom requis'
    if (!formData.lastName) errs.lastName = 'Nom requis'
    if (!formData.email) errs.email = 'Email requis'
    if (!formData.username) errs.username = 'Login requis'
    if (!formData.password) errs.password = 'Mot de passe requis'
    if (!formData.roleId) errs.roleId = 'Rôle requis'
    setErrors(errs)
    return Object.keys(errs).length === 0
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    if (validate()) {
      dispatch(createUser(formData))
    }
  }

  return (
    <section className="content">
      <PageHeader title="Ajouter un utilisateur" breadcrumb={[{ label: 'Utilisateurs', path: '/admin/users/allusers/1' }, { label: 'Ajouter' }]} />
      <div className="container-fluid">
        <div className="card shadow">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Nouvel utilisateur</h6>
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
                  <FormField label="Email" name="email" type="email" value={formData.email} onChange={handleChange} error={errors.email} required />
                </div>
                <div className="col-md-6">
                  <FormField label="Login" name="username" value={formData.username} onChange={handleChange} error={errors.username} required />
                </div>
                <div className="col-md-6">
                  <FormField label="Mot de passe" name="password" type="password" value={formData.password} onChange={handleChange} error={errors.password} required />
                </div>
                <div className="col-md-6">
                  <FormField label="Téléphone" name="phone" value={formData.phone} onChange={handleChange} />
                </div>
                <div className="col-md-6">
                  <FormField label="Code utilisateur" name="codeUser" value={formData.codeUser} onChange={handleChange} />
                </div>
                <div className="col-md-6">
                  <FormField label="Rôle" name="roleId" type="select" value={formData.roleId} onChange={handleChange} error={errors.roleId} required>
                    <option value="">Choisir un rôle</option>
                    {roles.map(r => <option key={r.id || r.roleId} value={r.id || r.roleId}>{r.roleName}</option>)}
                  </FormField>
                </div>
              </div>
              <div className="mt-3">
                <button type="submit" className="btn btn-primary mr-2" disabled={isLoading}>
                  {isLoading ? <><span className="spinner-border spinner-border-sm mr-1"></span>Enregistrement...</> : 'Enregistrer'}
                </button>
                <button type="button" className="btn btn-secondary" onClick={() => navigate('/admin/users/allusers/1')}>
                  Annuler
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </section>
  )
}

export default AddUser
