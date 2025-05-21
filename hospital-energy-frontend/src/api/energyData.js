import request from '@/utils/request';

/**
 * 创建能源数据记录
 * @param {object} data EnergyDataDto
 * @returns Promise
 */
export function createEnergyData(data) {
  return request({
    url: '/api/energy-data',
    method: 'post',
    data
  });
}

/**
 * 根据ID获取能源数据记录
 * @param {string} id 能源数据ID
 * @returns Promise
 */
export function getEnergyDataById(id) {
  return request({
    url: `/api/energy-data/${id}`,
    method: 'get'
  });
}

/**
 * 获取所有能源数据记录 (分页和排序)
 * @param {object} params 查询参数 (page, size, sort)
 * @returns Promise
 */
export function getAllEnergyData(params) {
  return request({
    url: '/api/energy-data',
    method: 'get',
    params
  });
}

/**
 * 更新能源数据记录
 * @param {string} id 能源数据ID
 * @param {object} data EnergyDataDto
 * @returns Promise
 */
export function updateEnergyData(id, data) {
  return request({
    url: `/api/energy-data/${id}`,
    method: 'put',
    data
  });
}

/**
 * 删除能源数据记录
 * @param {string} id 能源数据ID
 * @returns Promise
 */
export function deleteEnergyData(id) {
  return request({
    url: `/api/energy-data/${id}`,
    method: 'delete'
  });
}

/**
 * 根据设备ID获取能源数据列表
 * @param {string} deviceId 设备ID
 * @param {object} params (可选) 查询参数，例如时间范围
 * @returns Promise
 */
export function getEnergyDataByDevice(deviceId, params) {
  return request({
    url: `/api/energy-data/by-device/${deviceId}`,
    method: 'get',
    params
  });
}

/**
 * 根据能源类型获取能源数据列表
 * @param {string} type 能源类型
 * @param {object} params (可选) 查询参数
 * @returns Promise
 */
export function getEnergyDataByType(type, params) {
  return request({
    url: `/api/energy-data/by-type/${type}`,
    method: 'get',
    params
  });
}

/**
 * 根据时间范围获取能源数据列表
 * @param {object} params 查询参数 (start, end)
 * @returns Promise
 */
export function getEnergyDataByTimestamp(params) {
  return request({
    url: '/api/energy-data/by-timestamp',
    method: 'get',
    params
  });
}

/**
 * 根据设备ID和时间范围获取能源数据列表
 * @param {string} deviceId 设备ID
 * @param {object} params 查询参数 (start, end)
 * @returns Promise
 */
export function getEnergyDataByDeviceAndTimestamp(deviceId, params) {
  return request({
    url: `/api/energy-data/by-device-timestamp/${deviceId}`,
    method: 'get',
    params
  });
}
