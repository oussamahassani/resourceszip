import { useSelector } from 'react-redux'

export const usePermissions = () => {
  const { user } = useSelector((state) => state.auth)

  const authorities = user?.authorities?.map(a =>
    typeof a === 'string' ? a : a.authority
  ) || []

  const hasAuthority = (authority) => authorities.includes(authority)

  const hasAnyAuthority = (...auths) => auths.some(a => hasAuthority(a))

  const hasAllAuthorities = (...auths) => auths.every(a => hasAuthority(a))

  return { authorities, hasAuthority, hasAnyAuthority, hasAllAuthorities }
}

export default usePermissions
