import React, { useState } from 'react'

const DataTable = ({
  columns = [],
  data = [],
  loading = false,
  pagination = null,
  onPageChange = null,
  onSort = null,
  emptyMessage = 'Aucune donnée trouvée',
  tableId,
  className = '',
}) => {
  const [sortField, setSortField] = useState(null)
  const [sortDir, setSortDir] = useState('asc')

  const handleSort = (field) => {
    const newDir = sortField === field && sortDir === 'asc' ? 'desc' : 'asc'
    setSortField(field)
    setSortDir(newDir)
    if (onSort) onSort(field, newDir)
  }

  return (
    <div className={`table-responsive ${className}`}>
      <table id={tableId} className="table table-bordered table-striped dataTable">
        <thead>
          <tr>
            {columns.map((col, i) => (
              <th
                key={i}
                className={`text-center${col.sortable ? ' sorting' : ''}`}
                style={col.width ? { width: col.width } : {}}
                onClick={col.sortable ? () => handleSort(col.field) : undefined}
              >
                {col.header}
                {col.sortable && sortField === col.field && (
                  <i className={`ml-1 fas fa-sort-${sortDir === 'asc' ? 'up' : 'down'}`}></i>
                )}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {loading ? (
            <tr>
              <td colSpan={columns.length} className="text-center">
                <div className="spinner-border spinner-border-sm text-primary" role="status"></div>
                {' '}Chargement...
              </td>
            </tr>
          ) : data.length === 0 ? (
            <tr>
              <td colSpan={columns.length} className="text-center text-muted">{emptyMessage}</td>
            </tr>
          ) : (
            data.map((row, ri) => (
              <tr key={ri}>
                {columns.map((col, ci) => (
                  <td key={ci} className="text-center">
                    {col.render ? col.render(row) : (col.field ? row[col.field] : null)}
                  </td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>

      {pagination && (
        <div className="d-flex justify-content-between align-items-center mt-2">
          <span className="text-muted">
            Affichage de {((pagination.page - 1) * pagination.size) + 1} à {Math.min(pagination.page * pagination.size, pagination.total)} sur {pagination.total} entrées
          </span>
          <nav>
            <ul className="pagination mb-0">
              <li className={`page-item${pagination.page <= 1 ? ' disabled' : ''}`}>
                <button className="page-link" onClick={() => onPageChange && onPageChange(pagination.page - 1)}>
                  Précédent
                </button>
              </li>
              {Array.from({ length: Math.min(5, pagination.totalPages || 1) }, (_, i) => {
                const pageNum = Math.max(1, pagination.page - 2) + i
                if (pageNum > (pagination.totalPages || 1)) return null
                return (
                  <li key={pageNum} className={`page-item${pagination.page === pageNum ? ' active' : ''}`}>
                    <button className="page-link" onClick={() => onPageChange && onPageChange(pageNum)}>
                      {pageNum}
                    </button>
                  </li>
                )
              })}
              <li className={`page-item${pagination.page >= (pagination.totalPages || 1) ? ' disabled' : ''}`}>
                <button className="page-link" onClick={() => onPageChange && onPageChange(pagination.page + 1)}>
                  Suivant
                </button>
              </li>
            </ul>
          </nav>
        </div>
      )}
    </div>
  )
}

export default DataTable
