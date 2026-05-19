import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import FormField from '../../components/common/FormField'
import Alert from '../../components/common/Alert'
import axiosInstance from '../../api/axiosInstance'
import usersService from '../../services/usersService'

const AddRole = () => {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [privileges, setPrivileges] = useState([])
  const [selectedPrivileges, setSelectedPrivileges] = useState([])
  const [formData, setFormData] = useState({ roleName: '', description: '' })

  useEffect(() => {
    usersService.getPrivileges().then(data => setPrivileges(data || [])).catch(() => {})
  }, [])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  const togglePrivilege = (id) => {
    setSelectedPrivileges(prev =>
      prev.includes(id) ? prev.filter(p => p !== id) : [...prev, id]
    )
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await axiosInstance.post('/api/role/addrole', { ...formData, privilegeIds: selectedPrivileges })
      navigate('/role/allroles/1')
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la création')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="content">
      <PageHeader title="Ajouter un rôle" breadcrumb={[{ label: 'Rôles', path: '/role/allroles/1' }, { label: 'Ajouter' }]} />
      <div className="container-fluid">
        <div className="card shadow">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Nouveau rôle</h6>
          </div>
          <div className="card-body">
            {error && <Alert type="danger" message={error} />}
            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6">
                  <FormField label="Nom du rôle" name="roleName" value={formData.roleName} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <FormField label="Description" name="description" value={formData.description} onChange={handleChange} />
                </div>
              </div>
              <div className="form-group">
                <label>Privilèges</label>
                <div className="row" style={{ maxHeight: 300, overflowY: 'auto' }}>
                  {privileges.map(p => (
                    <div key={p.id || p.privilegeId} className="col-md-4">
                      <div className="form-check">
                        <input
                          type="checkbox"
                          className="form-check-input"
                          id={`priv-${p.id || p.privilegeId}`}
                          checked={selectedPrivileges.includes(p.id || p.privilegeId)}
                          onChange={() => togglePrivilege(p.id || p.privilegeId)}
                        />
                        <label className="form-check-label" htmlFor={`priv-${p.id || p.privilegeId}`}>
                          {p.privilegeName}
                        </label>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
              <div className="mt-3">
                <button type="submit" className="btn btn-primary mr-2" disabled={loading}>
                  {loading ? 'Enregistrement...' : 'Enregistrer'}
                </button>
                <button type="button" className="btn btn-secondary" onClick={() => navigate(-1)}>Annuler</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </section>
  )
}

export default AddRole
