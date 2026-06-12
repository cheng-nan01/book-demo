import request from './index'

export function getSalespeople(params) {
  return request.get('/salespeople', { params })
}

export function addSalesperson(data) {
  return request.post('/salespeople', data)
}

export function updateSalesperson(id, data) {
  return request.put(`/salespeople/${id}`, data)
}

export function deleteSalesperson(id) {
  return request.delete(`/salespeople/${id}`)
}
