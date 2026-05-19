import React, { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import PageHeader from '../../components/common/PageHeader'
import Loader from '../../components/common/Loader'
import axiosInstance from '../../api/axiosInstance'
import { formatDate, formatCurrency } from '../../utils/helpers'

const ViewFacture = () => {
  const { id } = useParams()
  const [facture, setFacture] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    axiosInstance.get(`/api/facture/view/${id}`)
      .then(res => setFacture(res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <Loader />

  const f = facture || {}

  return (
    <section className="content">
      <PageHeader title="Détails facture" breadcrumb={[{ label: 'Factures', path: '/facture' }, { label: 'Détails' }]} />
      <div className="container-fluid">
        <div className="card shadow mb-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h6 className="m-0 font-weight-bold" style={{ color: '#590E9C' }}>Facture #{f.numeroFacture || id}</h6>
            <div>
              <span className={`badge badge-${f.statut === 'Payé' ? 'success' : f.statut === 'En retard' ? 'danger' : 'warning'} mr-2`}>
                {f.statut || '-'}
              </span>
              <a href={`/facture/open/${id}`} target="_blank" rel="noopener noreferrer" className="btn btn-primary btn-sm">
                <i className="fas fa-file-pdf mr-1"></i> PDF
              </a>
            </div>
          </div>
          <div className="card-body">
            <div className="row">
              <div className="col-md-6">
                <h6 className="font-weight-bold">Client</h6>
                <table className="table table-bordered table-sm">
                  <tbody>
                    <tr><th>Nom</th><td>{f.client?.firstName} {f.client?.lastName}</td></tr>
                    <tr><th>Code</th><td>{f.client?.codeClient || '-'}</td></tr>
                    <tr><th>Adresse</th><td>{f.client?.adresse || '-'}</td></tr>
                  </tbody>
                </table>
              </div>
              <div className="col-md-6">
                <h6 className="font-weight-bold">Facture</h6>
                <table className="table table-bordered table-sm">
                  <tbody>
                    <tr><th>N° Facture</th><td>{f.numeroFacture || '-'}</td></tr>
                    <tr><th>Date</th><td>{formatDate(f.dateFacture)}</td></tr>
                    <tr><th>Montant HT</th><td>{formatCurrency(f.montantHt)}</td></tr>
                    <tr><th>TVA</th><td>{formatCurrency(f.tva)}</td></tr>
                    <tr><th>Montant TTC</th><td className="font-weight-bold">{formatCurrency(f.montantTtc)}</td></tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

export default ViewFacture
