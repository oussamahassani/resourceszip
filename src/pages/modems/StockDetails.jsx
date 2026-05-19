import React, { useEffect, useState } from 'react'
import PageHeader from '../../components/common/PageHeader'
import axiosInstance from '../../api/axiosInstance'

const StockDetails = () => {
  const [stock, setStock] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    axiosInstance.get('/api/modem/stockDetails')
      .then(res => setStock(res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const s = stock || {}

  return (
    <section className="content">
      <PageHeader title="Détails du stock modems" breadcrumb={[{ label: 'Modems' }, { label: 'Stock' }]} />
      <div className="container-fluid">
        <div className="row">
          {[
            { label: 'Total modems', value: s.total, color: 'primary' },
            { label: 'Disponibles', value: s.disponibles, color: 'success' },
            { label: 'Affectés', value: s.affectes, color: 'info' },
            { label: 'Défectueux', value: s.defectueux, color: 'danger' },
          ].map((item, i) => (
            <div key={i} className="col-md-3 col-sm-6 mb-4">
              <div className={`card border-left-${item.color} shadow py-2`}>
                <div className="card-body">
                  <div className="row no-gutters align-items-center">
                    <div className="col">
                      <div className={`text-xs font-weight-bold text-${item.color} text-uppercase mb-1`}>{item.label}</div>
                      <div className="h5 mb-0 font-weight-bold text-gray-800">
                        {loading ? <span className="spinner-border spinner-border-sm"></span> : (item.value ?? '0')}
                      </div>
                    </div>
                    <div className="col-auto">
                      <i className="fas fa-wifi fa-2x text-gray-300"></i>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>

        {s.byZone && s.byZone.length > 0 && (
          <div className="card shadow mb-4">
            <div className="card-header">
              <h6 className="m-0 font-weight-bold">Répartition par zone</h6>
            </div>
            <div className="card-body">
              <div className="table-responsive">
                <table className="table table-bordered">
                  <thead>
                    <tr>
                      <th>Zone</th>
                      <th className="text-center">Disponibles</th>
                      <th className="text-center">Affectés</th>
                      <th className="text-center">Total</th>
                    </tr>
                  </thead>
                  <tbody>
                    {s.byZone.map((z, i) => (
                      <tr key={i}>
                        <td>{z.zoneName}</td>
                        <td className="text-center">{z.disponibles}</td>
                        <td className="text-center">{z.affectes}</td>
                        <td className="text-center font-weight-bold">{z.total}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}
      </div>
    </section>
  )
}

export default StockDetails
