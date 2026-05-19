export const formatDate = (dateString) => {
  if (!dateString) return '-'
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' })
  } catch {
    return dateString
  }
}

export const formatDateTime = (dateString) => {
  if (!dateString) return '-'
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('fr-FR', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    })
  } catch {
    return dateString
  }
}

export const formatCurrency = (amount, currency = 'TND') => {
  if (amount === null || amount === undefined) return '-'
  return `${parseFloat(amount).toFixed(3)} ${currency}`
}

export const formatNumber = (num) => {
  if (num === null || num === undefined) return '0'
  return new Intl.NumberFormat('fr-FR').format(num)
}

export const downloadFile = (blob, filename) => {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.setAttribute('download', filename)
  document.body.appendChild(link)
  link.click()
  link.remove()
  window.URL.revokeObjectURL(url)
}

export const getStatusBadgeClass = (status) => {
  const statusMap = {
    'Actif': 'success',
    'Résilié': 'danger',
    'En cours': 'warning',
    'Traité': 'info',
    'Validé': 'success',
    'Refusé': 'danger',
    'En attente': 'secondary',
    'Nouveau': 'primary',
  }
  return statusMap[status] || 'secondary'
}

export const buildQueryParams = (params) => {
  const filtered = Object.fromEntries(
    Object.entries(params).filter(([, v]) => v !== null && v !== undefined && v !== '')
  )
  return new URLSearchParams(filtered).toString()
}

export const getCurrentYear = () => new Date().getFullYear()

export const getYearRange = (start = 2019, end = 2035) => {
  const years = []
  for (let y = start; y <= end; y++) years.push(y)
  return years
}

export const MONTHS_FR = [
  { value: 1, label: 'Janvier' },
  { value: 2, label: 'Février' },
  { value: 3, label: 'Mars' },
  { value: 4, label: 'Avril' },
  { value: 5, label: 'Mai' },
  { value: 6, label: 'Juin' },
  { value: 7, label: 'Juillet' },
  { value: 8, label: 'Août' },
  { value: 9, label: 'Septembre' },
  { value: 10, label: 'Octobre' },
  { value: 11, label: 'Novembre' },
  { value: 12, label: 'Décembre' },
]
