import request from './index'

export function getMonthlyStatistics(params) {
  return request.get('/statistics/monthly', { params })
}

export function getDashboardData() {
  return request.get('/statistics/dashboard')
}

export function getRankingList(params) {
  return request.get('/statistics/ranking', { params })
}
