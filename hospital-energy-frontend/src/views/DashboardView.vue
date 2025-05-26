<template>
  <div class="dashboard-container">
    <div v-if="isLoading" class="loading-spinner">加载中...</div>
    <div v-else class="dashboard-content">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="box-card modern-card">
            <div slot="header" class="clearfix card-header">
              <span><i class="el-icon-s-data header-icon"></i>能源消耗概览</span>
            </div>
            <div class="card-content">
              <p v-if="dashboardSummary">实时电耗: <strong>{{ dashboardSummary.realtimeElectricityConsumption.toFixed(2) }} kWh</strong></p>
              <p v-if="dashboardSummary">实时水耗: <strong>{{ dashboardSummary.realtimeWaterConsumption.toFixed(2) }} m³</strong></p>
              <p v-if="dashboardSummary">实时气耗: <strong>{{ dashboardSummary.realtimeGasConsumption.toFixed(2) }} m³</strong></p>
              <p v-if="dashboardSummary && dashboardSummary.lastUpdatedAt" class="last-update-text">
                数据更新于: {{ formatLastUpdate(dashboardSummary.lastUpdatedAt) }}
              </p>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="box-card modern-card">
            <div slot="header" class="clearfix card-header">
              <span><i class="el-icon-pie-chart header-icon"></i>设备状态</span>
            </div>
            <div class="card-content">
              <div ref="deviceStatusChart" style="height: 180px;" class="chart-container"></div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="box-card modern-card">
            <div slot="header" class="clearfix card-header">
              <span><i class="el-icon-odometer header-icon"></i>实时功率</span>
            </div>
            <div class="card-content">
              <p v-if="dashboardSummary">电力: <strong>{{ dashboardSummary.realtimeElectricityPower.toFixed(2) }} kW</strong></p>
              <p v-if="dashboardSummary">水: <strong>{{ dashboardSummary.realtimeWaterPower.toFixed(2) }} m³/h</strong></p>
              <p v-if="dashboardSummary">燃气: <strong>{{ dashboardSummary.realtimeGasPower.toFixed(2) }} m³/h</strong></p>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :xs="24" :lg="16">
          <el-card class="box-card modern-card">
            <div slot="header" class="clearfix card-header">
              <span><i class="el-icon-date header-icon"></i>近7日能耗趋势</span>
            </div>
            <div class="card-content">
              <div ref="energyChart" style="height: 320px;" class="chart-container"></div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="8">
          <el-card class="box-card modern-card">
            <div slot="header" class="clearfix card-header">
              <span><i class="el-icon-menu header-icon"></i>常用功能</span>
            </div>
            <div class="quick-access card-content">
              <el-row :gutter="10">
                <el-col :xs="12" :sm="12" v-for="(item, index) in quickAccess" :key="index" class="quick-access-item">
                    <el-button 
                      type="primary" 
                      plain 
                      @click="navigateTo(item.path)" 
                      :icon="item.icon"
                      class="quick-access-button">
                      {{ item.name }}
                    </el-button>
                </el-col>
              </el-row>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
// 假设你有一个API客户端，例如axios，已经配置在 this.$api 或 this.$axios
import api from '@/api'; // 导入统一的API实例

export default {
  name: "DashboardView",
  data() {
    return {
      isLoading: true,
      dashboardSummary: {
        onlineDeviceCount: 0,
        offlineDeviceCount: 0,
        maintenanceDeviceCount: 0,
        faultyDeviceCount: 0,
        realtimeElectricityPower: 0.0,
        realtimeElectricityConsumption: 0.0,
        realtimeWaterPower: 0.0,
        realtimeWaterConsumption: 0.0,
        realtimeGasPower: 0.0,
        realtimeGasConsumption: 0.0,
        last7DaysElectricityConsumption: [],
        last7DaysWaterConsumption: [],
        last7DaysGasConsumption: [],
        lastUpdatedAt: null,
      },
      quickAccess: [
        { name: '房间管理', path: '/management/rooms', icon: 'el-icon-office-building' },
        { name: '设备管理', path: '/management/devices', icon: 'el-icon-cpu' },
        { name: '能耗数据', path: '/data/energy-data', icon: 'el-icon-s-data' },
        { name: '能耗分析', path: '/analysis/energy-analysis', icon: 'el-icon-s-marketing' },
      ],
      energyChartInstance: null,
      deviceStatusChartInstance: null,
    };
  },
  mounted() {
    this.fetchDashboardData();
  },
  beforeDestroy() {
    if (this.energyChartInstance) {
      this.energyChartInstance.dispose();
      this.energyChartInstance = null;
    }
    if (this.deviceStatusChartInstance) {
      this.deviceStatusChartInstance.dispose();
      this.deviceStatusChartInstance = null;
    }
  },
  methods: {
    async fetchDashboardData() {
      this.isLoading = true;
      // 使用默认值初始化 dashboardSummary，以防API调用失败时模板访问undefined属性
      this.dashboardSummary = {
            onlineDeviceCount: 0, offlineDeviceCount: 0, maintenanceDeviceCount: 0, faultyDeviceCount: 0,
            realtimeElectricityPower: 0, realtimeElectricityConsumption: 0,
            realtimeWaterPower: 0, realtimeWaterConsumption: 0,
            realtimeGasPower: 0, realtimeGasConsumption: 0,
            last7DaysElectricityConsumption: Array(7).fill({date: 'N/A', consumption: 0}),
            last7DaysWaterConsumption: Array(7).fill({date: 'N/A', consumption: 0}),
            last7DaysGasConsumption: Array(7).fill({date: 'N/A', consumption: 0}),
            lastUpdatedAt: new Date().toISOString(),
      };

      try {
        const response = await api.dashboard.getDashboardSummary();
        console.log("Full API Response (which should be data object directly):", response); 

        // 检查 response 是否有效 (直接是数据对象)
        if (response && typeof response === 'object' && !Array.isArray(response)) { // Ensure it's an object, not an array or null
          this.dashboardSummary = response; // response is already the data object
          console.log("Assigned dashboardSummary from API:", JSON.stringify(this.dashboardSummary, null, 2));
        } else {
          console.error("API response is missing, invalid, or not an object. Response:", response);
          this.$message.error('未能获取有效的仪表盘数据格式。');
          // dashboardSummary 已经有默认值，图表会用默认值初始化
        }

      } catch (error) {
        console.error("获取仪表盘数据失败 (catch block):", error);
        if (error.response) {
          // Axios错误对象包含response属性
          console.error("Error response data:", error.response.data);
          console.error("Error response status:", error.response.status);
        } else if (error.request) {
          // 请求已发出但没有收到响应
          console.error("Error request:", error.request);
        } else {
          // 其他错误
          console.error('Error message:', error.message);
        }
        this.$message.error('获取仪表盘数据失败，请检查网络或联系管理员。');
        // dashboardSummary 已经有默认值，图表会用默认值初始化
      } finally {
        this.isLoading = false;
        // 确保在isLoading为false且DOM更新后初始化图表
        this.$nextTick(() => {
          this.initEnergyChart();
          this.initDeviceStatusChart();
        });
      }
    },
    initEnergyChart() {
      if (!this.$refs.energyChart) {
        console.warn("Energy chart ref ($refs.energyChart) not found in initEnergyChart. DOM might not be ready or ref name is incorrect.");
        return;
      }
      if (this.energyChartInstance) {
        this.energyChartInstance.dispose();
      }
      this.energyChartInstance = this.$echarts.init(this.$refs.energyChart);

      let dates = [];
      let electricityData = [];
      let waterData = [];
      let gasData = [];

      // 使用安全的方式访问可能不存在的属性，并提供默认值
      const elecCons = (this.dashboardSummary && this.dashboardSummary.last7DaysElectricityConsumption) || [];
      dates = elecCons.map(item => {
        if (item && typeof item.date === 'string') {
          const parts = item.date.split('-');
          return parts.length >= 3 ? `${parts[1]}-${parts[2]}` : 'N/A';
        }
        return 'N/A';
      });
      electricityData = elecCons.map(item => 
        (item && typeof item.consumption === 'number' ? item.consumption.toFixed(2) : '0.00')
      );
      if (elecCons.length === 0 && dates.length === 0) { // 如果源数据为空，填充默认
          dates = Array(7).fill('N/A');
          electricityData = Array(7).fill('0.00');
      }


      const waterCons = (this.dashboardSummary && this.dashboardSummary.last7DaysWaterConsumption) || [];
      waterData = waterCons.map(item => 
        (item && typeof item.consumption === 'number' ? item.consumption.toFixed(2) : '0.00')
      );
       if (waterCons.length === 0) {
          waterData = Array(7).fill('0.00');
      }
      
      const gasCons = (this.dashboardSummary && this.dashboardSummary.last7DaysGasConsumption) || [];
      gasData = gasCons.map(item => 
        (item && typeof item.consumption === 'number' ? item.consumption.toFixed(2) : '0.00')
      );
      if (gasCons.length === 0) {
          gasData = Array(7).fill('0.00');
      }
      
      // console.log("Energy Chart Data for ECharts:", { dates, electricityData, waterData, gasData });

      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['电耗 (kWh)', '水耗 (m³)', '气耗 (m³)']
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
          data: dates
        },
        yAxis: {
          type: 'value',
          name: '消耗量',
          nameTextStyle: {
            padding: [0, 0, 0, 30] // Adjust padding if name is cut off
          }
        },
        series: [
          {
            name: '电耗 (kWh)',
            type: 'line',
            smooth: true,
            data: electricityData
          },
          {
            name: '水耗 (m³)',
            type: 'line',
            smooth: true,
            data: waterData
          },
          {
            name: '气耗 (m³)',
            type: 'line',
            smooth: true,
            data: gasData
          }
        ]
      };
      this.energyChartInstance.setOption(option, true); // true for notMerge
    },
    initDeviceStatusChart() {
      if (!this.$refs.deviceStatusChart) {
        console.warn("Device status chart ref ($refs.deviceStatusChart) not found in initDeviceStatusChart. DOM might not be ready or ref name is incorrect.");
        return;
      }
      if (this.deviceStatusChartInstance) {
        this.deviceStatusChartInstance.dispose();
      }
      this.deviceStatusChartInstance = this.$echarts.init(this.$refs.deviceStatusChart);

      const online = (this.dashboardSummary && typeof this.dashboardSummary.onlineDeviceCount === 'number') ? this.dashboardSummary.onlineDeviceCount : 0;
      const offline = (this.dashboardSummary && typeof this.dashboardSummary.offlineDeviceCount === 'number') ? this.dashboardSummary.offlineDeviceCount : 0;
      const faulty = (this.dashboardSummary && typeof this.dashboardSummary.faultyDeviceCount === 'number') ? this.dashboardSummary.faultyDeviceCount : 0;
      const maintenance = (this.dashboardSummary && typeof this.dashboardSummary.maintenanceDeviceCount === 'number') ? this.dashboardSummary.maintenanceDeviceCount : 0;

      // console.log("Device Status Chart Data for ECharts:", { online, offline, faulty, maintenance });

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'right', // Move legend to the right
          top: 'center', // Center vertically
          itemGap: 10,
          data: ['在线', '离线', '故障', '维护中']
        },
        series: [
          {
            name: '设备状态',
            type: 'pie',
            radius: ['50%', '75%'], // Make the ring slightly thicker
            center: ['40%', '50%'], // Adjust center to make space for legend
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '14', // Slightly smaller font for emphasis
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: online, name: '在线', itemStyle: { color: '#67C23A' } },
              { value: offline, name: '离线', itemStyle: { color: '#909399' } },
              { value: faulty, name: '故障', itemStyle: { color: '#F56C6C' } },
              { value: maintenance, name: '维护中', itemStyle: { color: '#E6A23C' } }
            ]
          }
        ]
      };
      if (online === 0 && offline === 0 && faulty === 0 && maintenance === 0) {
        console.warn("All device status counts are zero. Pie chart might appear empty or show 'No Data'.");
      }
      this.deviceStatusChartInstance.setOption(option, true); // true for notMerge
    },
    navigateTo(path) {
      this.$router.push(path);
    },
    formatLastUpdate(isoDateTime) {
      if (!isoDateTime) return 'N/A';
      try {
        const date = new Date(isoDateTime);
        if (isNaN(date.getTime())) {
            console.warn("Invalid date for formatLastUpdate:", isoDateTime);
            return 'N/A';
        }
        return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false });
      } catch (e) {
        console.error("Error formatting date:", isoDateTime, e);
        return 'N/A';
      }
    }
  },
};
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f4f6f8; /* Light background for the whole page */
}

.loading-spinner {
  display: flex;
  justify-content: center;
  align-items: center;
  height: calc(100vh - 100px); /* Adjust based on header/footer */
  font-size: 1.5em;
  color: #555;
}

.dashboard-content {
  /* Styles for the main content area after loading */
}

.box-card.modern-card {
  margin-bottom: 20px;
  border-radius: 8px; /* Rounded corners */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); /* Softer shadow */
  border: none; /* Remove default border if any */
  display: flex; /* Added for flex layout */
  flex-direction: column; /* Stack header and content vertically */
  height: 100%; /* Make card take full height of its col */
}

.card-header {
  font-weight: bold;
  font-size: 16px;
  color: #303133;
  border-bottom: 1px solid #ebeef5; /* Subtle separator */
  padding: 15px 20px; /* Adjust padding */
}

.header-icon {
  margin-right: 8px;
  color: #409EFF;
}

.card-content {
  padding: 15px 20px; /* Reduced vertical padding, kept horizontal */
  font-size: 14px;
  color: #606266;
  flex-grow: 1; /* Allow content to take available space */
  display: flex;
  flex-direction: column;
  justify-content: flex-start; /* Align content to the top */
}

.card-content p {
  margin: 6px 0; /* Slightly reduced margin for p tags */
  line-height: 1.6;
}
.card-content p strong {
  color: #303133;
  font-weight: 600;
}

.last-update-text {
  font-size: 12px;
  color: #909399;
  margin-top: 10px; /* Reduced margin */
}

.chart-container {
  width: 100%;
  flex-grow: 1; /* Allow chart container to grow if it's the main content */
  display: flex; /* To center chart if needed, or just to contain it */
  align-items: center; /* Vertically center chart if its height is less than container */
  justify-content: center; /* Horizontally center chart */
  /* height is set inline on the chart's direct div */
}
.chart-container > div { /* Target the ECharts initialized div */
    width: 100%; /* Ensure ECharts div takes full width of chart-container */
    height: 100%; /* Ensure ECharts div takes full height of chart-container (e.g. 180px) */
}


.quick-access {
  /* padding is inherited from card-content */
}

.quick-access-item {
    margin-bottom: 12px; /* Increased margin */
    display: flex;
}

.quick-access-button {
    width: 100%;
    padding-top: 12px; /* Taller buttons */
    padding-bottom: 12px;
    font-size: 14px;
    transition: all 0.3s ease;
}
.quick-access-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}


.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}

/* Responsive adjustments for column span if needed */
@media (max-width: 768px) {
  .el-col-sm-12 { /* Ensure cards stack on small screens */
    margin-bottom: 20px;
  }
  .el-col-sm-12:last-child {
     margin-bottom: 0; /* Remove bottom margin for the last card in a stacked row */
  }

  .el-col-lg-16, .el-col-lg-8 {
    margin-bottom: 20px;
  }
  .el-col-lg-8:last-child {
    margin-bottom: 0;
  }
   .card-content {
    padding: 15px; /* Consistent padding on smaller screens */
  }
  .quick-access-button {
    font-size: 13px;
  }
}
</style>
