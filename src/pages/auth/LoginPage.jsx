import React, { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import { login, reset } from '../../redux/slices/authSlice'

const LoginPage = () => {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { isLoading, isError, isSuccess, message, token } = useSelector((state) => state.auth)

  const [formData, setFormData] = useState({ username: '', password: '' })
  const [showPassword, setShowPassword] = useState(false)

  useEffect(() => {
    if (token) {
      navigate('/', { replace: true })
    }
  }, [token, navigate])

  useEffect(() => {
    return () => { dispatch(reset()) }
  }, [dispatch])

  const handleChange = (e) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    dispatch(login(formData))
  }

  return (
    <div className="login-page" style={{
      alignItems: 'center',
      background: 'url(/img/Bg.jpg) no-repeat center center fixed',
      backgroundSize: 'cover',
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      justifyContent: 'center',
      textAlign: 'center',
    }}>
      <div className="login-box" style={{ width: 360 }}>
        <img src="/img/logo chifco.png" alt="Nety" className="brand-image" style={{ marginBottom: 40, maxWidth: '100%' }} />

        <div className="card">
          <div className="card-body login-card-body" style={{
            backgroundColor: '#590E9C',
            padding: 0,
          }}>
            <div style={{ backgroundColor: '#fff', padding: 20 }}>
              <br /><br />

              {isError && (
                <div className="alert alert-danger py-2">
                  {message || 'Le nom d\'utilisateur ou le mot de passe est invalide'}
                </div>
              )}

              <form onSubmit={handleSubmit} className="form-signin">
                <h3 className="form-signin-heading" style={{ color: '#590E9C' }}>LOGIN PAGE</h3>
                <br />

                <div className="form-group">
                  <input
                    type="text"
                    id="username"
                    name="username"
                    placeholder="Login"
                    className="form-control"
                    required
                    value={formData.username}
                    onChange={handleChange}
                    style={{ height: 50, padding: '10px 12px' }}
                  />
                </div>

                <div className="input-group mb-3">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    name="password"
                    placeholder="Mot de passe"
                    className="form-control"
                    required
                    value={formData.password}
                    onChange={handleChange}
                    style={{ height: 50, padding: '10px 12px' }}
                  />
                  <div className="input-group-append">
                    <span
                      className="input-group-text"
                      style={{ cursor: 'pointer', display: 'flex', alignItems: 'center' }}
                      onClick={() => setShowPassword(s => !s)}
                    >
                      <i className={`fa ${showPassword ? 'fa-eye-slash' : 'fa-eye'}`}></i>
                    </span>
                  </div>
                </div>

                <button
                  className="btn btn-lg btn-block"
                  type="submit"
                  disabled={isLoading}
                  style={{
                    backgroundColor: '#590E9C',
                    borderColor: '#590E9C',
                    color: '#fff',
                  }}
                >
                  {isLoading ? (
                    <><span className="spinner-border spinner-border-sm mr-2"></span>Connexion...</>
                  ) : 'Login'}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default LoginPage
