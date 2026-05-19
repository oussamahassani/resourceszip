import React from 'react'

const FormField = ({
  label,
  name,
  type = 'text',
  value,
  onChange,
  error,
  required = false,
  placeholder,
  disabled = false,
  className = '',
  children,
  options = [],
  rows = 3,
}) => {
  const id = `field-${name}`

  const renderInput = () => {
    if (type === 'select') {
      return (
        <select
          id={id}
          name={name}
          className={`form-control${error ? ' is-invalid' : ''} ${className}`}
          value={value}
          onChange={onChange}
          disabled={disabled}
          required={required}
        >
          {children || options.map((opt, i) => (
            <option key={i} value={typeof opt === 'object' ? opt.value : opt}>
              {typeof opt === 'object' ? opt.label : opt}
            </option>
          ))}
        </select>
      )
    }
    if (type === 'textarea') {
      return (
        <textarea
          id={id}
          name={name}
          className={`form-control${error ? ' is-invalid' : ''} ${className}`}
          value={value}
          onChange={onChange}
          disabled={disabled}
          required={required}
          placeholder={placeholder}
          rows={rows}
        />
      )
    }
    if (type === 'checkbox') {
      return (
        <div className="form-check">
          <input
            id={id}
            type="checkbox"
            name={name}
            className={`form-check-input${error ? ' is-invalid' : ''}`}
            checked={value}
            onChange={onChange}
            disabled={disabled}
          />
          <label className="form-check-label" htmlFor={id}>{label}</label>
        </div>
      )
    }
    return (
      <input
        id={id}
        type={type}
        name={name}
        className={`form-control${error ? ' is-invalid' : ''} ${className}`}
        value={value}
        onChange={onChange}
        disabled={disabled}
        required={required}
        placeholder={placeholder}
      />
    )
  }

  if (type === 'checkbox') {
    return (
      <div className="form-group">
        {renderInput()}
        {error && <div className="invalid-feedback">{error}</div>}
      </div>
    )
  }

  return (
    <div className="form-group">
      {label && (
        <label htmlFor={id}>
          {label}
          {required && <span className="text-danger ml-1">*</span>}
        </label>
      )}
      {renderInput()}
      {error && <div className="invalid-feedback d-block">{error}</div>}
    </div>
  )
}

export default FormField
