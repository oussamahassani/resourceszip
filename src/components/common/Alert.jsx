import React, { useState } from 'react'

const Alert = ({ type = 'info', message, dismissible = true, onClose }) => {
  const [visible, setVisible] = useState(true)

  if (!visible || !message) return null

  const handleClose = () => {
    setVisible(false)
    if (onClose) onClose()
  }

  return (
    <div className={`alert alert-${type}${dismissible ? ' alert-dismissible' : ''} fade show`} role="alert">
      {message}
      {dismissible && (
        <button type="button" className="close" onClick={handleClose}>
          <span>&times;</span>
        </button>
      )}
    </div>
  )
}

export default Alert
