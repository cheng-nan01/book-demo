import request from './index'

export function addPurchase(data) {
  return request.post('/purchases', data)
}
