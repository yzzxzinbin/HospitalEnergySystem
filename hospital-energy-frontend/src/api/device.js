import request from '@/utils/request';

// 获取设备列表 (分页)
export function getDevices(params) {
  return request({
    url: '/api/devices',
    method: 'get',
    params
  });
}

// 根据ID获取设备详情
export function getDeviceById(id) {
  return request({
    url: `/api/devices/${id}`,
    method: 'get'
  });
}

// 新增设备
export function createDevice(data) {
  return request({
    url: '/api/devices',
    method: 'post',
    data
  });
}

// 更新设备信息
export function updateDevice(id, data) {
  return request({
    url: `/api/devices/${id}`,
    method: 'put',
    data
  });
}

// 删除设备
export function deleteDevice(id) {
  return request({
    url: `/api/devices/${id}`,
    method: 'delete'
  });
}

/**
 * 根据房间ID获取设备列表
 * @param {string} roomId 房间ID
 * @returns Promise
 */
export function getDevicesByRoom(roomId) {
  return request({
    url: `/api/devices/by-room/${roomId}`,
    method: 'get'
  });
}

/**
 * 根据设备模板ID获取设备列表
 * @param {string} templateId 设备模板ID
 * @returns Promise
 */
export function getDevicesByTemplate(templateId) {
  return request({
    url: `/api/devices/by-template/${templateId}`,
    method: 'get'
  });
}

/**
 * 根据状态获取设备列表
 * @param {string} status 设备状态
 * @returns Promise
 */
export function getDevicesByStatus(status) {
  return request({
    url: `/api/devices/by-status/${status}`,
    method: 'get'
  });
}
