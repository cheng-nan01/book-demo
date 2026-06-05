import request from './index'

export function login(data) {
  return request.post('/auth/login', data)
}

export function register(data) {
  return request.post('/auth/register', data)
}

export function me() {
  return request.get('/auth/me')
}

export function logout() {
  return request.post('/auth/logout')
}
