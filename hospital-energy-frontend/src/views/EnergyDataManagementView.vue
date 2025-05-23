<template>
  <div class="energy-data-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>能源数据查询</span>
      </div>

      <!-- 搜索和筛选区域 -->
      <el-form :inline="true" :model="searchParams" class="demo-form-inline mb-20" @submit.native.prevent>
        <el-form-item label="选择设备">
          <el-select v-model="searchParams.deviceId" placeholder="请选择设备" clearable filterable style="width: 280px;">
            <el-option 
              v-for="device in deviceOptions" 
              :key="device.id" 
              :label="device.name + (device.roomNumber ? ` (房间: ${device.roomNumber})` : '')" 
              :value="device.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数据类型">
          <el-select v-model="searchParams.dataType" placeholder="请选择数据类型" clearable style="width: 150px;">
            <el-option label="电能" value="ELECTRICITY"></el-option>
            <el-option label="水能" value="WATER"></el-option>
            <el-option label="热能" value="HEAT"></el-option>
            <!-- Add other types if available, e.g., GAS -->
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchParams.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期时间"
            end-placeholder="结束日期时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            :default-time="['00:00:00', '23:59:59']"
            style="width: 380px;">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch" icon="el-icon-refresh">重置</el-button>
          <el-button icon="el-icon-download" @click="handleExport" :disabled="energyDataList.length === 0">导出</el-button>
        </el-form-item>
      </el-form>

      <!-- 能源数据图表 -->
      <el-card shadow="never" class="mb-20" v-if="chartVisible && energyDataList.length > 0">
          <div ref="energyDataChart" style="height: 400px; width: 100%;"></div>
      </el-card>
      <el-empty description="暂无数据可供图表显示" v-if="chartVisible && energyDataList.length === 0 && searchAttempted"></el-empty>


      <!-- 能源数据列表表格 -->
      <el-table :data="energyDataList" v-loading="loading" style="width: 100%" border stripe>
        <el-table-column prop="id" label="记录ID" width="80" align="center"></el-table-column>
        <el-table-column label="设备名称" width="250" show-overflow-tooltip align="left">
            <template slot-scope="scope">
                <!-- EnergyDataDto directly contains deviceName -->
                {{ scope.row.deviceName || 'N/A' }}
            </template>
        </el-table-column>
        <!-- Removed "所属房间" column as EnergyDataDto does not directly provide full room details -->
        <el-table-column prop="type" label="数据类型" width="100" align="center"> <!-- Changed prop to "type" -->
            <template slot-scope="scope">{{ formatDataType(scope.row.type) }}</template> <!-- Changed scope.row.dataType to scope.row.type -->
        </el-table-column>
        <el-table-column prop="value" label="瞬时值" width="150" align="center">
            <template slot-scope="scope">{{ scope.row.value }} {{ getValueUnit(scope.row.type) }}</template> <!-- Changed scope.row.dataType to scope.row.type -->
        </el-table-column>
        <el-table-column prop="consumption" label="分钟消耗量" width="150" align="center">
            <template slot-scope="scope">{{ scope.row.consumption }} {{ scope.row.unit }}</template>
        </el-table-column>
        <el-table-column prop="timestamp" label="记录时间" width="180" align="center">
            <template slot-scope="scope">{{ scope.row.timestamp | formatDateTime }}</template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" align="center">
            <template slot-scope="scope">{{ scope.row.createdAt | formatDateTime }}</template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <el-pagination
        style="margin-top: 20px; text-align: right;"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page.sync="searchParams.page"
        :page-sizes="[10, 20, 50, 100, 200]"
        :page-size.sync="searchParams.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
import { getAllEnergyData } from '@/api/energyData';
import { getDevices } from '@/api/device';
import { parseTime } from '@/utils'; // Assuming parseTime is in utils

// Import ECharts if not globally registered
// import * as echarts from 'echarts'; 

export default {
  name: "EnergyDataManagementView",
  filters: {
    formatDateTime(time) {
      console.log('[formatDateTime] Input time:', time); 
      if (!time) return '';

      // 1. 直接尝试用 new Date() 解析时间字符串
      const dateObj = new Date(time);
      console.log('[formatDateTime] Created Date object from input:', dateObj);

      // 2. 检查 Date 对象是否有效
      if (isNaN(dateObj.getTime())) {
        console.error('[formatDateTime] Failed to parse time string into a valid Date object. Input was:', time, '. Resulting Date object is Invalid.');
        return '时间解析失败'; // 返回一个明确的错误提示
      }
      
      // 3. 如果 Date 对象有效，再尝试用 parseTime 工具函数格式化
      // 注意：parseTime 函数本身可能也有自己的 new Date(time) 逻辑
      const formattedTimeFromParseTime = parseTime(time, '{y}-{m}-{d} {h}:{i}:{s}');
      console.log('[formatDateTime] Output from parseTime utility:', formattedTimeFromParseTime); 
      
      // 4. 检查 parseTime 的输出是否是 "0-0-0..." 这种异常格式
      //    如果 parseTime 返回了异常值，但我们知道 dateObj 是有效的，则尝试手动格式化
      if (typeof formattedTimeFromParseTime === 'string' && 
          (formattedTimeFromParseTime.startsWith('0-0-0') || formattedTimeFromParseTime.toLowerCase().includes('invalid'))) {
          console.warn('[formatDateTime] parseTime utility returned an unexpected/error value. Attempting manual formatting using the valid Date object.');
          try {
            const year = dateObj.getFullYear();
            const month = (dateObj.getMonth() + 1).toString().padStart(2, '0');
            const day = dateObj.getDate().toString().padStart(2, '0');
            const hours = dateObj.getHours().toString().padStart(2, '0');
            const minutes = dateObj.getMinutes().toString().padStart(2, '0');
            const seconds = dateObj.getSeconds().toString().padStart(2, '0');
            
            // 确保年份不是0或NaN等异常情况
            if (isNaN(year) || year === 0 || year < 1000) {
                 console.error('[formatDateTime] Manual formatting: Year is invalid from Date object.', dateObj);
                 return '手动格式化年份错误';
            }

            const manualFormattedTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
            console.log('[formatDateTime] Manually formatted time:', manualFormattedTime);
            return manualFormattedTime;
          } catch (e) {
            console.error('[formatDateTime] Error during manual formatting:', e);
            return '手动格式化异常';
          }
      }
      
      // 5. 如果 parseTime 的输出看起来正常，则返回它
      return formattedTimeFromParseTime;
    }
  },
  data() {
    return {
      loading: false,
      chartVisible: false, // Controls visibility of the chart card
      searchAttempted: false, // To show empty state for chart only after a search
      energyDataList: [],
      deviceOptions: [], 
      searchParams: {
        deviceId: null,
        dataType: '', // Example: ELECTRICITY, WATER, HEAT
        dateRange: [], // [startTime, endTime]
        page: 1,
        size: 10,
      },
      pagination: {
        total: 0,
      },
      energyDataChartInstance: null,
      dataTypeMap: {
        'ELECTRICITY': { text: '电能', valueUnit: 'W' }, // Wh for consumption will come from data.unit
        'WATER': { text: '水能', valueUnit: 'L/h' },   // m³ for consumption will come from data.unit (assuming value is flow rate)
        'HEAT': { text: '热能', valueUnit: 'kW' },    // GJ for consumption will come from data.unit (assuming value is thermal power)
        // Add other types if available
      }
    };
  },
  created() {
    this.fetchDeviceOptions();
    // Optionally, load initial data or wait for user search
    // this.fetchEnergyData(); 
  },
  mounted() {
    // ECharts instance should be created after the DOM is ready and data is available
  },
  beforeDestroy() {
    if (this.energyDataChartInstance) {
      this.energyDataChartInstance.dispose();
      this.energyDataChartInstance = null;
    }
  },
  methods: {
    async fetchDeviceOptions() {
      try {
        const response = await getDevices({ page: 0, size: 10000, sort: 'name,asc' }); 
        // console.log('API Response from getDevices (for options):', JSON.parse(JSON.stringify(response)));
        if (Array.isArray(response)) {
          this.deviceOptions = response;
        } else if (response && response.content) { // Assuming getDevices might return Page<DeviceDto>
          this.deviceOptions = response.content || [];
        } else if (response && response.records) { // Or if it returns a custom PageResponseDto like other APIs
            this.deviceOptions = response.records || [];
        }
         else {
          this.deviceOptions = [];
          console.warn('Unexpected response structure from getDevices for options:', response);
        }
        // console.log('Processed this.deviceOptions:', JSON.parse(JSON.stringify(this.deviceOptions)));
      } catch (error) {
        this.$message.error('获取设备列表失败: ' + (error.message || '请稍后再试'));
        this.deviceOptions = [];
      }
    },
    async fetchEnergyData() {
      this.loading = true;
      this.searchAttempted = true;
      try {
        const queryParams = {
          page: this.searchParams.page - 1, // Spring Pageable is 0-indexed
          size: this.searchParams.size,
          deviceId: this.searchParams.deviceId,
          dataType: this.searchParams.dataType,
        };
        if (this.searchParams.dateRange && this.searchParams.dateRange.length === 2) {
          queryParams.startTime = this.searchParams.dateRange[0];
          queryParams.endTime = this.searchParams.dateRange[1];
        }
        
        // Remove null or empty string parameters before sending
        for (const key in queryParams) {
          if (queryParams[key] === null || queryParams[key] === '') {
            delete queryParams[key];
          }
        }

        const response = await getAllEnergyData(queryParams);
        console.log('API Response from getAllEnergyData:', JSON.parse(JSON.stringify(response))); // Log raw response

        // Correctly access data from 'records' and total from 'total'
        this.energyDataList = response.records || [];
        this.pagination.total = response.total || 0;
        console.log('Processed this.energyDataList:', JSON.parse(JSON.stringify(this.energyDataList)));
        
        this.chartVisible = true; // Show chart area after fetching data
        if (this.energyDataList.length > 0) {
          this.$nextTick(() => { // Ensure DOM is updated for chart rendering
            this.initEnergyDataChart(this.energyDataList);
          });
        } else {
           if (this.energyDataChartInstance) {
            this.energyDataChartInstance.clear(); // Clear previous chart if no data
          }
        }

      } catch (error) {
        this.$message.error('获取能源数据失败: ' + (error.message || '请稍后再试'));
        this.energyDataList = [];
        this.pagination.total = 0;
        if (this.energyDataChartInstance) {
          this.energyDataChartInstance.clear();
        }
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
      this.searchParams.page = 1; // Reset to first page for new search
      this.fetchEnergyData();
    },
    resetSearch() {
      this.searchParams = {
        deviceId: null,
        dataType: '',
        dateRange: [],
        page: 1,
        size: this.searchParams.size, // Keep current page size
      };
      this.chartVisible = false; // Hide chart on reset
      this.searchAttempted = false;
      if (this.energyDataChartInstance) {
        this.energyDataChartInstance.dispose(); // Dispose chart instance
        this.energyDataChartInstance = null;
      }
      this.energyDataList = [];
      this.pagination.total = 0;
      // Optionally, clear the chart or fetch default data
      // this.fetchEnergyData(); // or clear list and chart
    },
    handleExport() {
      if (this.energyDataList.length === 0) {
        this.$message.warning("没有数据可导出");
        return;
      }
      this.$confirm("确定导出当前查询结果的能源数据吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "info",
      }).then(() => {
        this.$message.info('导出功能后端API暂未实现。');
        // TODO: Implement export when backend API is available
        // Example: exportEnergyData(this.searchParams).then(response => { ...handle file download... });
      }).catch(() => {
        this.$message.info("已取消导出");
      });
    },
    initEnergyDataChart(data) {
      if (!this.$refs.energyDataChart) {
        // console.warn("Chart DOM element not found.");
        return;
      }
      // Ensure ECharts is available (globally or imported)
      const echarts = this.$echarts || window.echarts; // Adjust if ECharts is imported differently
      if (!echarts) {
        this.$message.error("ECharts库未加载，无法渲染图表。");
        return;
      }

      if (this.energyDataChartInstance) {
        this.energyDataChartInstance.dispose();
      }
      this.energyDataChartInstance = echarts.init(this.$refs.energyDataChart);
      
      // Sort data by timestamp for time series chart
      const sortedData = [...data].sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));
      
      const chartDataValues = sortedData.map(item => item.value);
      const chartDataTimestamps = sortedData.map(item => parseTime(item.timestamp, '{y}-{m}-{d} {h}:{i}'));

      const selectedDevice = this.deviceOptions.find(d => d.id === this.searchParams.deviceId);
      const deviceName = selectedDevice ? selectedDevice.name : '所选设备';
      // Use dataType from searchParams to determine the valueUnit for the chart
      const dataTypeForChart = this.searchParams.dataType || (sortedData.length > 0 ? sortedData[0].dataType : '');
      const valueUnitForChart = this.getValueUnit(dataTypeForChart);
      const dataTypeText = this.formatDataType(dataTypeForChart);
      
      const seriesName = `${deviceName} - ${dataTypeText}`;

      const option = {
        title: {
          text: seriesName + '趋势图',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          formatter: params => {
            const param = params[0];
            return `${param.axisValueLabel}<br/>${param.marker}${param.seriesName}: ${param.value} ${valueUnitForChart}`;
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: chartDataTimestamps,
          axisLabel: {
            rotate: 30,
          }
        },
        yAxis: {
          type: 'value',
          name: `瞬时值 (${valueUnitForChart})`,
          axisLabel: {
            formatter: '{value}'
          }
        },
        series: [
          {
            name: seriesName,
            type: 'line',
            smooth: true,
            data: chartDataValues,
            itemStyle: {
                color: '#409EFF' 
            },
            areaStyle: { // Optional: add area fill
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: 'rgba(64, 158, 255, 0.3)'
                }, {
                    offset: 1,
                    color: 'rgba(64, 158, 255, 0)'
                }])
            }
          }
        ],
        dataZoom: [ // Optional: add data zoom functionality
            {
                type: 'slider', // or 'inside'
                start: 0,
                end: 100 
            },
            {
                start: 0,
                end: 100
            }
        ],
      };
      this.energyDataChartInstance.setOption(option);
      
      // Handle window resize for chart responsiveness
      window.addEventListener('resize', this.resizeChart);
    },
    resizeChart() {
        if (this.energyDataChartInstance) {
            this.energyDataChartInstance.resize();
        }
    },
    formatDataType(type) {
      // console.log('formatDataType method input:', type, 'Current dataTypeMap keys:', Object.keys(this.dataTypeMap));
      if (typeof type !== 'string') {
        // console.warn('formatDataType: received non-string type:', type);
        return type; // Or some default error string
      }
      const key = type.trim().toUpperCase(); // Normalize key, e.g., "electricity" -> "ELECTRICITY"
      const entry = this.dataTypeMap[key];
      if (entry) {
        // console.log(`formatDataType: Matched key "${key}", returning text "${entry.text}"`);
        return entry.text;
      }
      // console.warn(`formatDataType: No mapping found for type "${type}" (normalized to "${key}")`);
      return type; // Fallback to raw type if no match
    },
    getValueUnit(type) { 
      if (typeof type !== 'string') {
        return '';
      }
      const key = type.trim().toUpperCase(); // Normalize key
      // console.log('getValueUnit method input:', type, 'Normalized key:', key);
      const entry = this.dataTypeMap[key];
      if (entry) {
        // console.log(`getValueUnit: Matched key "${key}", returning unit "${entry.valueUnit}"`);
        return entry.valueUnit;
      }
      // console.warn(`getValueUnit: No mapping found for type "${type}" (normalized to "${key}")`);
      return ''; // Fallback to empty string if no match
    },
    handleSizeChange(newSize) {
      this.searchParams.size = newSize;
      this.searchParams.page = 1; // Reset to first page
      this.fetchEnergyData();
    },
    handleCurrentChange(newPage) {
      this.searchParams.page = newPage;
      this.fetchEnergyData();
    },
  },
  beforeUnmount() { // Vue 3, for Vue 2 use beforeDestroy
    window.removeEventListener('resize', this.resizeChart);
    if (this.energyDataChartInstance) {
      this.energyDataChartInstance.dispose();
      this.energyDataChartInstance = null;
    }
  }
};
</script>

<style scoped>
.energy-data-container {
  padding: 20px;
}
.box-card {
  min-height: calc(100vh - 90px); /* Adjust to fit your layout */
  margin-bottom: 20px;
}
.mb-20 {
  margin-bottom: 20px;
}
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}
</style>
