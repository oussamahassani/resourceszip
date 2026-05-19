import React from 'react'

const Loader = ({ text = 'Chargement...' }) => {
  return (
    <div className="page-loader">
      <div className="text-center">
        <div className="spinner-border text-primary" role="status" style={{ width: '3rem', height: '3rem' }}>
          <span className="sr-only">{text}</span>
        </div>
        <p className="mt-2 text-muted">{text}</p>
      </div>
    </div>
  )
}

export default Loader
