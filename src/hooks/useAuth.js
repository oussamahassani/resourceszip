import { useSelector, useDispatch } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import { login, logout, reset } from '../redux/slices/authSlice'

export const useAuth = () => {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { user, token, isLoading, isError, isSuccess, message } = useSelector((state) => state.auth)

  const handleLogin = async (credentials) => {
    const result = await dispatch(login(credentials))
    if (login.fulfilled.match(result)) {
      navigate('/')
    }
    return result
  }

  const handleLogout = async () => {
    await dispatch(logout())
    navigate('/login')
  }

  const hasAuthority = (authority) => {
    if (!user?.authorities) return false
    return user.authorities.some(a =>
      (typeof a === 'string' ? a : a.authority) === authority
    )
  }

  const hasAnyAuthority = (...authorities) => {
    return authorities.some(auth => hasAuthority(auth))
  }

  const isAuthenticated = () => !!token && !!user

  const resetState = () => dispatch(reset())

  return {
    user,
    token,
    isLoading,
    isError,
    isSuccess,
    message,
    handleLogin,
    handleLogout,
    hasAuthority,
    hasAnyAuthority,
    isAuthenticated,
    resetState,
  }
}

export default useAuth
