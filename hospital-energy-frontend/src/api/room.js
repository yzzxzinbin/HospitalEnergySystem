import request from '@/utils/request';

// 获取房间列表 (通常用于下拉选择，可不分页或指定较大size)
export function getRooms(params) {
  return request({
    url: '/api/rooms',
    method: 'get',
    params // 例如 { pageSize: -1 } 来获取所有数据
  });
}

// 根据ID获取房间详情
export function getRoomById(id) {
  return request({
    url: `/api/rooms/${id}`,
    method: 'get'
  });
}

// 新增房间
export function createRoom(data) {
  return request({
    url: '/api/rooms',
    method: 'post',
    data
  });
}

// 更新房间信息
export function updateRoom(id, data) {
  return request({
    url: `/api/rooms/${id}`,
    method: 'put',
    data
  });
}

// 删除房间
export function deleteRoom(id) {
  return request({
    url: `/api/rooms/${id}`,
    method: 'delete'
  });
}

/**
 * 根据房间号获取房间
 * @param {string} roomNumber 房间号
 * @returns Promise
 */
export function getRoomByRoomNumber(roomNumber) {
  return request({
    url: `/api/rooms/by-room-number/${roomNumber}`,
    method: 'get'
  });
}

/**
 * 根据楼层获取房间列表
 * @param {string|number} floor 楼层号
 * @returns Promise
 */
export function getRoomsByFloor(floor) {
  return request({
    url: `/api/rooms/by-floor/${floor}`,
    method: 'get'
  });
}

/**
 * 根据部门获取房间列表
 * @param {string} department 部门名称
 * @returns Promise
 */
export function getRoomsByDepartment(department) {
  return request({
    url: `/api/rooms/by-department/${department}`,
    method: 'get'
  });
}

