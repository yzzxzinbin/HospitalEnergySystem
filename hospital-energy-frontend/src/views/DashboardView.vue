<template>
  <div class="dashboard-container">
    <div v-if="isLoading" class="loading-spinner">加载中...</div>
    <div v-else>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>能源消耗概览</span>
            </div>
            <div v-if="dashboardSummary">
              <p>今日电耗: {{ dashboardSummary.realtimeElectricityConsumption.toFixed(2) }} kWh</p>
              <p>今日水耗: {{ dashboardSummary.realtimeWaterConsumption.toFixed(2) }} m³</p>
              <p>今日气耗: {{ dashboardSummary.realtimeGasConsumption.toFixed(2) }} m³</p>
              <p v-if="dashboardSummary.lastUpdatedAt">数据更新于: {{ formatLastUpdate(dashboardSummary.lastUpdatedAt) }}</p>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>设备状态</span>
            </div>
            <div ref="deviceStatusChart" style="height: 160px;"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>实时功率</span>
            </div>
            <div v-if="dashboardSummary">
              <p>电力: {{ dashboardSummary.realtimeElectricityPower.toFixed(2) }} kW</p>
              <p>水: {{ dashboardSummary.realtimeWaterPower.toFixed(2) }} m³/h</p>
              <p>燃气: {{ dashboardSummary.realtimeGasPower.toFixed(2) }} m³/h</p>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="16">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>近7日能耗趋势</span>
            </div>
            <div ref="energyChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>常用功能</span>
            </div>
            <el-row :gutter="10" class="quick-access">
              <el-col :span="12" v-for="(item, index) in quickAccess" :key="index" class="quick-access-item">
                  <el-button type="primary" plain @click="navigateTo(item.path)" :icon="item.icon">{{ item.name }}</el-button>
              </el-col>
            </el-row>
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
        { name: '房间管理', path: '/rooms', icon: 'el-icon-office-building' },
        { name: '设备控制', path: '/devices', icon: 'el-icon-cpu' },
        { name: '能耗报表', path: '/energy-data', icon: 'el-icon-s-data' },
        { name: '用户设置', path: '/profile', icon: 'el-icon-setting' },
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
      
      console.log("Energy Chart Data for ECharts:", { dates, electricityData, waterData, gasData });

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
          name: '消耗量'
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

      console.log("Device Status Chart Data for ECharts:", { online, offline, faulty, maintenance });

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 10,
          data: ['在线', '离线', '故障', '维护中']
        },
        series: [
          {
            name: '设备状态',
            type: 'pie',
            radius: ['50%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '16',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: online, name: '在线' },
              { value: offline, name: '离线' },
              { value: faulty, name: '故障' },
              { value: maintenance, name: '维护中' }
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
}

.loading-spinner {
  text-align: center;
  padding: 50px;
  font-size: 1.5em;
}

.box-card {
  margin-bottom: 20px;
  min-height: 200px; /* Ensure cards have a minimum height */
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}

.quick-access-item {
    margin-bottom: 10px;
    display: flex;
}

.quick-access-item .el-button {
    width: 100%;
}
</style>
