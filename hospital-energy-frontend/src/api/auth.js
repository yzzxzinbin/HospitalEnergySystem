import request from '@/utils/request';

/**
 * 用户登录
 * @param {object} data包含 username 和 password
 * @returns Promise
 */
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  });
}

/**
 * 用户注册
 * @param {object} data包含 username, password, email
 * @returns Promise
 */
export function register(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  });
}

// 如果有获取当前登录用户信息、登出等API，也可以在这里添加
// export function getInfo() { ... }
// export function logout() { ... }
