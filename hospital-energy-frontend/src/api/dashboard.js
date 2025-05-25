import request from '@/utils/request';

/**
 * 获取仪表盘摘要数据
 * @returns Promise
 */
export function getDashboardSummary() {
  return request({
    url: '/api/dashboard/summary',
    method: 'get'
  });
}

/**
 * 手动刷新仪表盘摘要数据
 * @returns Promise
 */
export function refreshDashboardSummary() {
  return request({
    url: '/api/dashboard/refresh',
    method: 'post'
  });
}
