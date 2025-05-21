import request from '@/utils/request';

// 获取设备模板列表 (通常用于下拉选择，可不分页或指定较大size)
export function getDeviceTemplates(params) {
  return request({
    url: '/api/device-templates',
    method: 'get',
    params // 例如 { pageSize: -1 } 来获取所有数据
  });
}

// 根据ID获取设备模板详情
export function getDeviceTemplateById(id) {
  return request({
    url: `/api/device-templates/${id}`,
    method: 'get'
  });
}

// 新增设备模板
export function createDeviceTemplate(data) {
  return request({
    url: '/api/device-templates',
    method: 'post',
    data
  });
}

// 更新设备模板信息
export function updateDeviceTemplate(id, data) {
  return request({
    url: `/api/device-templates/${id}`,
    method: 'put',
    data
  });
}

// 删除设备模板
export function deleteDeviceTemplate(id) {
  return request({
    url: `/api/device-templates/${id}`,
    method: 'delete'
  });
}

/**
 * 根据制造商获取设备模板列表
 * @param {string} manufacturer 制造商名称
 * @returns Promise
 */
export function getDeviceTemplatesByManufacturer(manufacturer) {
  return request({
    url: `/api/device-templates/by-manufacturer/${manufacturer}`,
    method: 'get'
  });
}

/**
 * 根据类别获取设备模板列表
 * @param {string} category 设备类别
 * @returns Promise
 */
export function getDeviceTemplatesByCategory(category) {
  return request({
    url: `/api/device-templates/by-category/${category}`,
    method: 'get'
  });
}

/**
 * 根据制造商和型号获取设备模板
 * @param {object} params 查询参数 { manufacturer, modelIdentifier }
 * @returns Promise
 */
export function getDeviceTemplateByManufacturerAndModel(params) {
  return request({
    url: '/api/device-templates/by-manufacturer-model',
    method: 'get',
    params
  });
}
