<template>
  <div class="energy-analysis-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>能耗分析</span>
      </div>

      <!-- Filter Section -->
      <el-form :inline="true" :model="filters" class="filter-form" @submit.native.prevent>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="filters.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 240px;"
            clearable
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="能源类型">
          <el-select v-model="filters.energyType" placeholder="选择能源类型" style="width: 150px;" clearable>
            <el-option label="电能" value="ELECTRICITY"></el-option>
            <el-option label="水能" value="WATER"></el-option>
            <el-option label="气能" value="GAS"></el-option>
            <!-- <el-option label="热能" value="HEAT"></el-option> -->
          </el-select>
        </el-form-item>
        <el-form-item label="分析类型">
          <el-select v-model="filters.analysisType" placeholder="选择分析类型" style="width: 180px;">
            <el-option label="能耗趋势" value="trend"></el-option>
            <el-option label="按部门对比" value="department"></el-option>
            <el-option label="按设备类型对比" value="deviceType"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="时间聚合" v-if="filters.analysisType === 'trend'">
          <el-select v-model="filters.timeAggregation" placeholder="选择聚合方式" style="width: 150px;">
            <el-option label="按小时" value="hourly"></el-option>
            <el-option label="按日" value="daily"></el-option>
            <el-option label="按月" value="monthly"></el-option>
            <el-option label="按年" value="yearly"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleAnalysis" :loading="loading">分析</el-button>
        </el-form-item>
      </el-form>

      <!-- Chart Section -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="24">
          <el-card shadow="never">
            <div slot="header">
              <span>{{ chartTitle }}</span>
            </div>
            <div v-if="loading" class="chart-loading">图表加载中...</div>
            <div v-show="!loading && chartDataProcessed" ref="analysisChart" style="height: 400px; width: 100%;"></div>
            <el-empty v-if="!loading && !chartDataProcessed && analysisAttempted" description="暂无数据或未能生成图表"></el-empty>
          </el-card>
        </el-col>
      </el-row>

       <!-- Statistics Section (Placeholder) -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="24">
          <el-card shadow="never">
            <div slot="header">
              <span>能耗统计摘要 (基于当前筛选)</span>
            </div>
            <div v-if="statistics.totalConsumption !== null">
              <p>总 {{ selectedEnergyTypeText }} 消耗: {{ statistics.totalConsumption.toFixed(2) }} {{ statistics.unit }}</p>
              <!-- Add more stats as needed -->
            </div>
            <el-empty v-else description="请先执行分析以查看统计数据"></el-empty>
          </el-card>
        </el-col>
      </el-row>

    </el-card>
  </div>
</template>

<script>
import { getAllEnergyData } from '@/api/energyData';
import { getDevices } from '@/api/device';
import { getRooms } from '@/api/room';
import { getDeviceTemplates } from '@/api/deviceTemplate';
import { parseTime } from '@/utils'; // Assuming parseTime is in utils

// import * as echarts from 'echarts'; // Already globally registered in main.js

export default {
  name: "EnergyAnalysisView",
  data() {
    return {
      loading: false,
      analysisAttempted: false,
      chartDataProcessed: false,
      filters: {
        dateRange: [], // [startDate, endDate]
        energyType: 'ELECTRICITY', // Default
        analysisType: 'trend', // Default
        timeAggregation: 'daily', // Default for trend analysis
      },
      chartTitle: '能耗分析图表',
      statistics: {
        totalConsumption: null,
        unit: '',
      },
      // Raw data stores
      allEnergyData: [],
      allDevices: [],
      allRooms: [],
      allDeviceTemplates: [],
      // Mappings
      deviceToRoomMap: {},
      deviceToTemplateMap: {},
      roomToDepartmentMap: {},
      templateToCategoryMap: {},

      analysisChartInstance: null,
      energyTypeUnits: {
        ELECTRICITY: 'kWh', // Assuming consumption is in kWh
        WATER: 'm³',      // Assuming consumption is in m³
        GAS: 'm³',        // Assuming consumption is in m³
        // HEAT: 'GJ' // Example for heat
      },
      energyTypeNames: {
        ELECTRICITY: '电能',
        WATER: '水能',
        GAS: '气能',
        // HEAT: '热能'
      }
    };
  },
  computed: {
    selectedEnergyTypeText() {
      return this.energyTypeNames[this.filters.energyType] || '能源';
    }
  },
  async created() {
    // Set default date range (e.g., last 7 days)
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(endDate.getDate() - 6); // Default to daily, so 7 days is fine
    this.filters.dateRange = [
      parseTime(startDate, '{y}-{m}-{d}'),
      parseTime(endDate, '{y}-{m}-{d}')
    ];
    
    await this.fetchAllMasterData();
    // Optionally, run initial analysis
    // this.handleAnalysis();
  },
  mounted() {
    // Chart initialization will happen in handleAnalysis after data is fetched and processed
    // Add resize listener here if you anticipate the chart instance might be created later
    // window.addEventListener('resize', this.resizeAnalysisChart); // Moved to init
  },
  beforeDestroy() {
    if (this.analysisChartInstance) {
      this.analysisChartInstance.dispose();
      this.analysisChartInstance = null;
    }
    window.removeEventListener('resize', this.resizeAnalysisChart); // Ensure listener is removed
  },
  methods: {
    async fetchAllMasterData() {
        this.loading = true;
        try {
            const [devicesRes, roomsRes, templatesRes] = await Promise.all([
                getDevices({ page: 0, size: 10000 }), // Fetch all devices
                getRooms({ page: 0, size: 10000 }),   // Fetch all rooms
                getDeviceTemplates({ page: 0, size: 10000 }) // Fetch all templates
            ]);

            this.allDevices = (Array.isArray(devicesRes) ? devicesRes : devicesRes?.content || devicesRes?.records || []);
            this.allRooms = roomsRes?.records || [];
            this.allDeviceTemplates = Array.isArray(templatesRes) ? templatesRes : (templatesRes?.records || []);


            // Create mappings
            this.allDevices.forEach(d => {
                this.deviceToRoomMap[d.id] = d.roomId;
                this.deviceToTemplateMap[d.id] = d.deviceTemplateId;
            });
            this.allRooms.forEach(r => {
                this.roomToDepartmentMap[r.id] = r.department;
            });
            this.allDeviceTemplates.forEach(t => {
                this.templateToCategoryMap[t.id] = t.deviceCategory;
            });

        } catch (error) {
            this.$message.error('获取基础数据失败: ' + (error.message || '请稍后再试'));
            console.error("Error fetching master data:", error);
        } finally {
            this.loading = false;
        }
    },

    async handleAnalysis() {
      if (!this.filters.dateRange || this.filters.dateRange.length !== 2) {
        this.$message.warning("请选择时间范围");
        return;
      }
      if (!this.filters.energyType) {
        this.$message.warning("请选择能源类型");
        return;
      }
      this.loading = true;
      this.analysisAttempted = true;
      this.chartDataProcessed = false;
      this.statistics.totalConsumption = null;
      this.statistics.unit = '';

      try {
        const params = {
          type: this.filters.energyType,
          startTime: `${this.filters.dateRange[0]} 00:00:00`,
          endTime: `${this.filters.dateRange[1]} 23:59:59`,
          page: 0,
          size: 100000, // Attempt to fetch all relevant data for the period
          sort: 'timestamp,asc'
        };
        const energyRes = await getAllEnergyData(params);
        this.allEnergyData = energyRes.records || [];

        if (this.allEnergyData.length === 0) {
          this.$message.info("所选条件下未查询到能耗数据。");
          if(this.analysisChartInstance) this.analysisChartInstance.clear();
          this.loading = false;
          return;
        }
        
        this.processDataForChart();
        this.calculateStatistics();

      } catch (error) {
        this.$message.error("分析失败: " + (error.message || '请稍后再试'));
        console.error("Error during analysis:", error);
        if(this.analysisChartInstance) this.analysisChartInstance.clear();
      } finally {
        this.loading = false;
      }
    },

    processDataForChart() {
      let option = {};
      const energyData = this.allEnergyData;

      if (this.filters.analysisType === 'trend') {
        const aggregation = this.filters.timeAggregation;
        this.chartTitle = `${this.selectedEnergyTypeText}消耗趋势 (${this.filters.dateRange[0]} 至 ${this.filters.dateRange[1]} - ${this.getAggregationText(aggregation)})`;
        
        const aggregatedConsumption = {};
        let timeFormat = '{y}-{m}-{d}'; // Default for daily
        if (aggregation === 'hourly') timeFormat = '{y}-{m}-{d} {h}:00';
        else if (aggregation === 'monthly') timeFormat = '{y}-{m}';
        else if (aggregation === 'yearly') timeFormat = '{y}';

        energyData.forEach(item => {
          const timeKey = parseTime(item.timestamp, timeFormat);
          if (!aggregatedConsumption[timeKey]) {
            aggregatedConsumption[timeKey] = 0;
          }
          aggregatedConsumption[timeKey] += (item.consumption || 0);
        });

        const sortedTimeKeys = Object.keys(aggregatedConsumption).sort((a, b) => {
          // For robust sorting, convert keys to comparable values (e.g., Date objects or padded strings)
          if (aggregation === 'hourly') return new Date(a).getTime() - new Date(b).getTime();
          if (aggregation === 'monthly') return new Date(a + '-01').getTime() - new Date(b + '-01').getTime();
          if (aggregation === 'yearly') return parseInt(a) - parseInt(b);
          return new Date(a).getTime() - new Date(b).getTime(); // Daily
        });

        const chartXAxis = sortedTimeKeys;
        const chartSeriesData = sortedTimeKeys.map(key => parseFloat(aggregatedConsumption[key].toFixed(2)));
        
        option = {
          tooltip: { trigger: 'axis' },
          xAxis: { 
            type: 'category', 
            data: chartXAxis,
            axisLabel: { 
              rotate: (aggregation === 'hourly' || aggregation === 'daily') && chartXAxis.length > 10 ? 30 : 0,
              interval: 'auto' 
            }
          },
          yAxis: { type: 'value', name: `消耗 (${this.energyTypeUnits[this.filters.energyType] || '单位'})` },
          series: [{ data: chartSeriesData, type: 'line', smooth: true, name: this.selectedEnergyTypeText }],
          grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
          dataZoom: [{ type: 'slider', show: true }, { type: 'inside' }],
        };
      } else if (this.filters.analysisType === 'department') {
        this.chartTitle = `${this.selectedEnergyTypeText}按部门对比 (${this.filters.dateRange[0]} 至 ${this.filters.dateRange[1]})`;
        const departmentConsumption = {};
        energyData.forEach(item => {
          const roomId = this.deviceToRoomMap[item.deviceId];
          const department = roomId ? (this.roomToDepartmentMap[roomId] || '未知部门') : '无房间设备';
          if (!departmentConsumption[department]) {
            departmentConsumption[department] = 0;
          }
          departmentConsumption[department] += (item.consumption || 0);
        });
        
        const chartXAxis = Object.keys(departmentConsumption);
        const chartSeriesData = chartXAxis.map(dept => parseFloat(departmentConsumption[dept].toFixed(2)));
        option = {
          tooltip: { trigger: 'axis', axisPointer: { type: 'shadow'} },
          xAxis: { 
            type: 'category', 
            data: chartXAxis, 
            axisLabel: { interval: 0, rotate: chartXAxis.length > 5 ? 30 : 0 } 
          },
          yAxis: { type: 'value', name: `总消耗 (${this.energyTypeUnits[this.filters.energyType] || '单位'})` },
          series: [{ data: chartSeriesData, type: 'bar', barMaxWidth: '60px', name: this.selectedEnergyTypeText }],
          grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true }, // Ensure containLabel is true
        };
      } else if (this.filters.analysisType === 'deviceType') {
        this.chartTitle = `${this.selectedEnergyTypeText}按设备类型对比 (${this.filters.dateRange[0]} 至 ${this.filters.dateRange[1]})`;
        const deviceTypeConsumption = {};
        energyData.forEach(item => {
          const templateId = this.deviceToTemplateMap[item.deviceId];
          const category = templateId ? (this.templateToCategoryMap[templateId] || '未知类型') : '无模板设备';
           if (!deviceTypeConsumption[category]) {
            deviceTypeConsumption[category] = 0;
          }
          deviceTypeConsumption[category] += (item.consumption || 0);
        });

        const chartXAxis = Object.keys(deviceTypeConsumption);
        const chartSeriesData = chartXAxis.map(type => parseFloat(deviceTypeConsumption[type].toFixed(2)));
        option = {
          tooltip: { trigger: 'axis', axisPointer: { type: 'shadow'} },
          xAxis: { 
            type: 'category', 
            data: chartXAxis, 
            axisLabel: { interval: 0, rotate: chartXAxis.length > 5 ? 30 : 0 } 
          },
          yAxis: { type: 'value', name: `总消耗 (${this.energyTypeUnits[this.filters.energyType] || '单位'})` },
          series: [{ data: chartSeriesData, type: 'bar', barMaxWidth: '60px', name: this.selectedEnergyTypeText }],
          grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true }, // Ensure containLabel is true
        };
      }

      this.chartDataProcessed = true; // Set this flag to make the div visible via v-show

      this.$nextTick(() => {
        if (this.$refs.analysisChart) {
          if (!this.analysisChartInstance) {
            this.analysisChartInstance = this.$echarts.init(this.$refs.analysisChart);
            window.addEventListener('resize', this.resizeAnalysisChart); // Add listener on first init
          }
          this.analysisChartInstance.setOption(option, true); // true to clear previous options
        } else {
            console.warn("Chart DOM element not ready for " + this.filters.analysisType);
            // this.chartDataProcessed = false; // Potentially reset if DOM element is truly gone
        }
      });
    },
    calculateStatistics() {
        if (this.allEnergyData.length > 0) {
            this.statistics.totalConsumption = this.allEnergyData.reduce((acc, item) => acc + (item.consumption || 0), 0);
            this.statistics.unit = this.energyTypeUnits[this.filters.energyType] || '单位';
        } else {
            this.statistics.totalConsumption = null;
            this.statistics.unit = '';
        }
    },
    getAggregationText(aggregation) {
      switch (aggregation) {
        case 'hourly': return '按小时';
        case 'daily': return '按日';
        case 'monthly': return '按月';
        case 'yearly': return '按年';
        default: return '';
      }
    },
    resizeAnalysisChart() {
      if (this.analysisChartInstance) {
        this.analysisChartInstance.resize();
      }
    }
  }
};
</script>

<style scoped>
.energy-analysis-container {
  padding: 20px;
}
.box-card {
  min-height: calc(100vh - 90px); /* Adjust to fit your layout */
}
.filter-form .el-form-item {
  margin-bottom: 10px;
}
.chart-loading {
  text-align: center;
  padding: 50px;
  color: #909399;
}
</style>
