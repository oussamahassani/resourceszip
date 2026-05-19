import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import Loader from '../../components/common/Loader'

const OpenFacture = () => {
  const { id } = useParams()
  const [loading, setLoading] = useState(true)

  const pdfUrl = `/api/facture/open/${id}`

  return (
    <section className="content" style={{ padding: 0 }}>
      {loading && <Loader />}
      <iframe
        src={pdfUrl}
        title="Facture"
        style={{ width: '100%', height: '100vh', border: 'none' }}
        onLoad={() => setLoading(false)}
      />
    </section>
  )
}

export default OpenFacture
