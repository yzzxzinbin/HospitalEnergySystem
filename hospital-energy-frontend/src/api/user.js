import request from '@/utils/request'

// 获取用户列表（可带分页和查询参数）
export function getUsers(params) {
  return request({
    url: '/api/users',
    method: 'get',
    params
  })
}

// 获取单个用户详情
export function getUserById(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'get'
  })
}

// 创建新用户
export function createUser(data) {
  return request({
    url: '/api/users',
    method: 'post',
    data
  })
}

// 更新用户信息
export function updateUser(id, data) {
  return request({
    url: `/api/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'delete'
  })
}

// 获取用户角色（如果API支持）
export function getUserRoles(userId) {
  return request({
    url: `/api/users/${userId}/roles`, // 假设的API端点
    method: 'get'
  })
}

// 分配用户角色（如果API支持）
export function assignUserRoles(userId, roleIds) {
  return request({
    url: `/api/users/${userId}/roles`, // 假设的API端点
    method: 'put',
    data: { roleIds }
  })
}

// 修改用户状态（启用/禁用）
export function updateUserStatus(id, status) {
    return request({
        url: `/api/users/${id}/status`, // 假设的API端点
        method: 'put',
        data: { status }
    })
}
