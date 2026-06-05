import request from './index'

export function getCustomerList(params) {
  return request.get('/customers', { params })
}

export function addCustomer(data) {
  return request.post('/customers', data)
}

export function updateCustomer(id, data) {
  return request.put(`/customers/${id}`, data)
}

export function deleteCustomer(id) {
  return request.delete(`/customers/${id}`)
}
