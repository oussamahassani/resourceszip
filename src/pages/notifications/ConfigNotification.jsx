import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import Alert from '../../components/common/Alert'
import Loader from '../../components/common/Loader'
import axiosInstance from '../../api/axiosInstance'

const ConfigNotification = () => {
  const [configs, setConfigs] = useState([])
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(null)
  const [success, setSuccess] = useState(null)

  useEffect(() => {
    setLoading(true)
    axiosInstance.get('/api/NotificationConfig/getListConfigNotif')
      .then(res => setConfigs(res.data || []))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const handleToggle = async (config) => {
    setSaving(config.id)
    try {
      await axiosInstance.put(`/api/NotificationConfig/update/${config.id}`, {
        ...config, enabled: !config.enabled
      })
      setConfigs(prev => prev.map(c => c.id === config.id ? { ...c, enabled: !c.enabled } : c))
      setSuccess('Configuration mise à jour')
    } catch {}
    setSaving(null)
  }

  const handleChange = (id, field, value) => {
    setConfigs(prev => prev.map(c => c.id === id ? { ...c, [field]: value } : c))
  }

  const handleSave = async (config) => {
    setSaving(config.id)
    try {
      await axiosInstance.put(`/api/NotificationConfig/update/${config.id}`, config)
      setSuccess('Configuration sauvegardée')
    } catch {}
    setSaving(null)
  }

  if (loading) return <Loader />

  return (
    <section className="content">
      <PageHeader title="Configuration des notifications" breadcrumb={[{ label: 'Configuration' }, { label: 'Notifications' }]} />
      <div className="container-fluid">
        {success && <Alert type="success" message={success} onClose={() => setSuccess(null)} />}
        <div className="card shadow mb-4">
          <div className="card-header">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Configuration des notifications</h6>
          </div>
          <div className="card-body">
            {configs.length === 0 ? (
              <p className="text-muted text-center">Aucune configuration trouvée</p>
            ) : (
              <div className="table-responsive">
                <table className="table table-bordered">
                  <thead>
                    <tr>
                      <th>Type de notification</th>
                      <th className="text-center">Activé</th>
                      <th>Email destinataires</th>
                      <th>Fréquence</th>
                      <th className="text-center">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {configs.map(config => (
                      <tr key={config.id}>
                        <td className="font-weight-bold">{config.typeNotification || config.name || '-'}</td>
                        <td className="text-center">
                          <div className="custom-control custom-switch">
                            <input
                              type="checkbox"
                              className="custom-control-input"
                              id={`switch-${config.id}`}
                              checked={config.enabled || false}
                              onChange={() => handleToggle(config)}
                              disabled={saving === config.id}
                            />
                            <label className="custom-control-label" htmlFor={`switch-${config.id}`}></label>
                          </div>
                        </td>
                        <td>
                          <input
                            type="text"
                            className="form-control form-control-sm"
                            value={config.emailDestinataires || ''}
                            onChange={e => handleChange(config.id, 'emailDestinataires', e.target.value)}
                            placeholder="email@example.com"
                          />
                        </td>
                        <td>
                          <select
                            className="form-control form-control-sm"
                            value={config.frequence || 'DAILY'}
                            onChange={e => handleChange(config.id, 'frequence', e.target.value)}
                          >
                            <option value="DAILY">Quotidien</option>
                            <option value="WEEKLY">Hebdomadaire</option>
                            <option value="MONTHLY">Mensuel</option>
                            <option value="IMMEDIATE">Immédiat</option>
                          </select>
                        </td>
                        <td className="text-center">
                          <button
                            className="btn btn-primary btn-sm"
                            onClick={() => handleSave(config)}
                            disabled={saving === config.id}
                          >
                            {saving === config.id
                              ? <span className="spinner-border spinner-border-sm"></span>
                              : <><i className="fas fa-save mr-1"></i> Sauvegarder</>
                            }
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      </div>
    </section>
  )
}

export default ConfigNotification
