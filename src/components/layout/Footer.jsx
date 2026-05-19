import React from 'react'

const Footer = () => {
  return (
    <footer className="main-footer">
      <strong>Copyright &copy; {new Date().getFullYear()} <a href="/">Nety CRM</a>.</strong>
      {' '}Tous droits réservés.
      <div className="float-right d-none d-sm-inline-block">
        <b>Version</b> 2.0.0
      </div>
    </footer>
  )
}

export default Footer
