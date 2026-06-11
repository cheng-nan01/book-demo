import request from './index'

export function getSaleList(params) {
  return request.get('/sales', { params })
}

export function getSale(id) {
  return request.get(`/sales/${id}`)
}

export function addSale(data) {
  return request.post('/sales', data)
}

export function getSalesByBookId(bookId) {
  return request.get(`/sales/book/${bookId}`)
}

export function updateSaleItem(saleId, bookId, data) {
  return request.put(`/sales/${saleId}/items/${bookId}`, data)
}

export function deleteSaleItem(saleId, bookId) {
  return request.delete(`/sales/${saleId}/items/${bookId}`)
}

export function deleteSale(id) {
  return request.delete(`/sales/${id}`)
}
