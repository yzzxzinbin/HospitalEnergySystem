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
// Note: The endpoint /api/users/{userId}/roles is not in the provided API说明.md.
// This function might not work as expected if the backend doesn't support it.
export function getUserRoles(userId) {
  return request({
    url: `/api/users/${userId}/roles`, // 假设的API端点
    method: 'get'
  })
}

// 分配用户角色（如果API支持）
// Note: The endpoint /api/users/{userId}/roles is not in the provided API说明.md.
// This function might not work as expected if the backend doesn't support it.
export function assignUserRoles(userId, roleIds) {
  return request({
    url: `/api/users/${userId}/roles`, // 假设的API端点
    method: 'put',
    data: { roleIds }
  })
}

// 修改用户状态（启用/禁用）
// Note: The endpoint /api/users/{id}/status is not in the provided API说明.md.
// This function might not work as expected if the backend doesn't support it.
export function updateUserStatus(id, status) {
    return request({
        url: `/api/users/${id}/status`, // 假设的API端点
        method: 'put',
        data: { status }
    })
}

// Removed changePassword function as /api/users/change-password is not a documented endpoint.
// User profile password changes should use updateUser with the user's ID.
// Removed getCurrentUserProfile function as /api/users/me is not a documented endpoint.
// User profile information should be loaded via existing auth mechanisms or getUserById if ID is known.
