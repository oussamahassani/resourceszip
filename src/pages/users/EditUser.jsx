import React, { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate, useParams } from 'react-router-dom'
import { fetchUserById, updateUser, reset } from '../../redux/slices/usersSlice'
import PageHeader from '../../components/common/PageHeader'
import FormField from '../../components/common/FormField'
import Alert from '../../components/common/Alert'
import usersService from '../../services/usersService'
import Loader from '../../components/common/Loader'

const EditUser = () => {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { id } = useParams()
  const { current, isLoading, isSuccess, isError, message } = useSelector((state) => state.users)

  const [roles, setRoles] = useState([])
  const [formData, setFormData] = useState({
    firstName: '', lastName: '', email: '', phone: '', codeUser: '', roleId: '', enabled: true,
  })
  const [errors, setErrors] = useState({})

  useEffect(() => {
    dispatch(fetchUserById(id))
    usersService.getRoles().then(data => setRoles(data || [])).catch(() => {})
    return () => dispatch(reset())
  }, [dispatch, id])

  useEffect(() => {
    if (current) {
      setFormData({
        firstName: current.firstName || '',
        lastName: current.lastName || '',
        email: current.email || '',
        phone: current.phone || '',
        codeUser: current.codeUser || '',
        roleId: current.roles?.[0]?.id || current.roles?.[0]?.roleId || '',
        enabled: current.enabled !== false,
      })
    }
  }, [current])

  useEffect(() => {
    if (isSuccess) navigate('/admin/users/allusers/1')
  }, [isSuccess, navigate])

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target
    setFormData(prev => ({ ...prev, [name]: type === 'checkbox' ? checked : value }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    dispatch(updateUser({ id, data: formData }))
  }

  if (!current && isLoading) return <Loader />

  return (
    <section className="content">
      <PageHeader title="Modifier l'utilisateur" breadcrumb={[{ label: 'Utilisateurs', path: '/admin/users/allusers/1' }, { label: 'Modifier' }]} />
      <div className="container-fluid">
        <div className="card shadow">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>
              {current ? `${current.firstName} ${current.lastName}` : 'Modifier'}
            </h6>
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
                  <FormField label="Email" name="email" type="email" value={formData.email} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <FormField label="Téléphone" name="phone" value={formData.phone} onChange={handleChange} />
                </div>
                <div className="col-md-6">
                  <FormField label="Code utilisateur" name="codeUser" value={formData.codeUser} onChange={handleChange} />
                </div>
                <div className="col-md-6">
                  <FormField label="Rôle" name="roleId" type="select" value={formData.roleId} onChange={handleChange} required>
                    <option value="">Choisir un rôle</option>
                    {roles.map(r => <option key={r.id || r.roleId} value={r.id || r.roleId}>{r.roleName}</option>)}
                  </FormField>
                </div>
                <div className="col-md-6">
                  <div className="form-group">
                    <div className="form-check">
                      <input type="checkbox" className="form-check-input" name="enabled" checked={formData.enabled} onChange={handleChange} id="enabled" />
                      <label className="form-check-label" htmlFor="enabled">Compte actif</label>
                    </div>
                  </div>
                </div>
              </div>
              <div className="mt-3">
                <button type="submit" className="btn btn-primary mr-2" disabled={isLoading}>
                  {isLoading ? <><span className="spinner-border spinner-border-sm mr-1"></span>Mise à jour...</> : 'Mettre à jour'}
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

export default EditUser
