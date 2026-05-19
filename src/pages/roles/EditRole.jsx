import React, { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import FormField from '../../components/common/FormField'
import Alert from '../../components/common/Alert'
import Loader from '../../components/common/Loader'
import axiosInstance from '../../api/axiosInstance'
import usersService from '../../services/usersService'

const EditRole = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)
  const [role, setRole] = useState(null)
  const [privileges, setPrivileges] = useState([])
  const [selectedPrivileges, setSelectedPrivileges] = useState([])
  const [formData, setFormData] = useState({ roleName: '', description: '' })

  useEffect(() => {
    setLoading(true)
    Promise.all([
      axiosInstance.get(`/api/role/getrole/${id}`),
      usersService.getPrivileges(),
    ])
      .then(([roleRes, privData]) => {
        const r = roleRes.data
        setRole(r)
        setFormData({ roleName: r.roleName || '', description: r.description || '' })
        setSelectedPrivileges(r.privileges?.map(p => p.id || p.privilegeId) || [])
        setPrivileges(privData || [])
      })
      .catch(() => setError('Erreur de chargement'))
      .finally(() => setLoading(false))
  }, [id])

  const togglePrivilege = (pid) => {
    setSelectedPrivileges(prev =>
      prev.includes(pid) ? prev.filter(p => p !== pid) : [...prev, pid]
    )
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      await axiosInstance.put(`/api/role/editrole/${id}`, { ...formData, privilegeIds: selectedPrivileges })
      navigate('/role/allroles/1')
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la mise à jour')
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <Loader />

  return (
    <section className="content">
      <PageHeader title="Modifier le rôle" breadcrumb={[{ label: 'Rôles', path: '/role/allroles/1' }, { label: 'Modifier' }]} />
      <div className="container-fluid">
        <div className="card shadow">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>{role?.roleName || 'Modifier'}</h6>
          </div>
          <div className="card-body">
            {error && <Alert type="danger" message={error} />}
            <form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6">
                  <FormField label="Nom du rôle" name="roleName" value={formData.roleName}
                    onChange={e => setFormData(f => ({ ...f, roleName: e.target.value }))} required />
                </div>
                <div className="col-md-6">
                  <FormField label="Description" name="description" value={formData.description}
                    onChange={e => setFormData(f => ({ ...f, description: e.target.value }))} />
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
                <button type="submit" className="btn btn-primary mr-2" disabled={saving}>
                  {saving ? 'Mise à jour...' : 'Mettre à jour'}
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

export default EditRole
