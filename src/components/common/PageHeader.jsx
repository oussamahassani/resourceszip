import React from 'react'
import { Link } from 'react-router-dom'

const PageHeader = ({ title, breadcrumb = [] }) => {
  return (
    <div className="content-header">
      <div className="container-fluid">
        <div className="row mb-2">
          <div className="col-sm-6">
            <h1 className="m-0">{title}</h1>
          </div>
          {breadcrumb.length > 0 && (
            <div className="col-sm-6">
              <ol className="breadcrumb float-sm-right">
                <li className="breadcrumb-item"><Link to="/">Accueil</Link></li>
                {breadcrumb.map((item, i) => (
                  <li key={i} className={`breadcrumb-item${i === breadcrumb.length - 1 ? ' active' : ''}`}>
                    {i < breadcrumb.length - 1 ? <Link to={item.path}>{item.label}</Link> : item.label}
                  </li>
                ))}
              </ol>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default PageHeader
