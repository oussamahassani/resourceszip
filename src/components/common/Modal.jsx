import React, { useEffect } from 'react'

const Modal = ({ show, onClose, title, children, size = '', footer = null }) => {
  useEffect(() => {
    if (show) {
      document.body.classList.add('modal-open')
    } else {
      document.body.classList.remove('modal-open')
    }
    return () => document.body.classList.remove('modal-open')
  }, [show])

  if (!show) return null

  return (
    <>
      <div className={`modal fade show`} style={{ display: 'block' }} tabIndex="-1" role="dialog">
        <div className={`modal-dialog${size ? ` modal-${size}` : ''}`} role="document">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">{title}</h5>
              <button type="button" className="close" onClick={onClose}>
                <span>&times;</span>
              </button>
            </div>
            <div className="modal-body">{children}</div>
            {footer && <div className="modal-footer">{footer}</div>}
          </div>
        </div>
      </div>
      <div className="modal-backdrop fade show" onClick={onClose}></div>
    </>
  )
}

export default Modal
