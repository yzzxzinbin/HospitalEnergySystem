// 统一导出所有 API 模块

import * as device from './device';
import * as room from './room';
import * as deviceTemplate from './deviceTemplate';
import * as user from './user'; // 引入用户API模块
import * as energyData from './energyData'; // 示例：如果需要能耗数据相关API
import * as dashboard from './dashboard'; // 引入仪表盘API模块

export default {
  device,
  room,
  deviceTemplate,
  user, // 导出用户API模块
  energyData,
  dashboard // 导出仪表盘API模块
};

