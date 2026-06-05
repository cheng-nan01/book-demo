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

export function updateSaleItem(itemId, data) {
  return request.put(`/sales/items/${itemId}`, data)
}

export function deleteSaleItem(itemId) {
  return request.delete(`/sales/items/${itemId}`)
}

export function deleteSale(id) {
  return request.delete(`/sales/${id}`)
}
