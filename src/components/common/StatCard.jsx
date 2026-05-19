import React from 'react'

const StatCard = ({
  title,
  value,
  subtitle,
  iconSrc,
  accentColor = '#1FBE9D',
  isLoading = false,
}) => {
  return (
    <div className="col-sm-12 col-md-6 col-lg-3 mb-4 stretch-card">
      <div className="left-side-shape" style={{ background: accentColor }}></div>
      <div className="card card-statistics">
        <div className="card-body">
          <div className="clearfix">
            {iconSrc && (
              <div className="taille-19">
                <img src={iconSrc} className="responsive-img" alt="" style={{ height: 40, width: 'auto', marginLeft: 120 }} />
              </div>
            )}
            <div className="float-left ml-4">
              {isLoading ? (
                <div className="spinner-border spinner-border-sm text-primary" role="status"></div>
              ) : (
                <h1 className="custom-heading ml-3">{value ?? '-'}</h1>
              )}
              <h5 className="custom-paragraph ml-3"><strong>{title}</strong></h5>
              {subtitle && (
                <p className="custom-paragraph2 ml-3 mb-2">
                  <strong>{subtitle}</strong>
                </p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default StatCard
