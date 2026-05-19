import { useState, useCallback } from 'react'

export const usePagination = (initialPage = 1, initialSize = 20) => {
  const [page, setPage] = useState(initialPage)
  const [size, setSize] = useState(initialSize)
  const [total, setTotal] = useState(0)

  const totalPages = Math.ceil(total / size)

  const goToPage = useCallback((newPage) => {
    if (newPage >= 1 && newPage <= totalPages) {
      setPage(newPage)
    }
  }, [totalPages])

  const nextPage = useCallback(() => goToPage(page + 1), [page, goToPage])
  const prevPage = useCallback(() => goToPage(page - 1), [page, goToPage])

  const updateTotal = useCallback((newTotal) => setTotal(newTotal), [])
  const updateSize = useCallback((newSize) => {
    setSize(newSize)
    setPage(1)
  }, [])

  return { page, size, total, totalPages, goToPage, nextPage, prevPage, updateTotal, updateSize }
}

export default usePagination
