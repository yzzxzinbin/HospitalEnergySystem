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
    }
    if (this.deviceStatusChartInstance) {
      this.deviceStatusChartInstance.dispose();
    }
  },
  methods: {
    async fetchDashboardData() {
      this.isLoading = true;
      try {
        const response = await api.dashboard.getDashboardSummary(); // 使用API模块
        this.dashboardSummary = response.data; // 假设API响应直接是数据对象
        
        this.$nextTick(() => {
          this.initEnergyChart();
          this.initDeviceStatusChart();
        });

      } catch (error) {
        console.error("获取仪表盘数据失败:", error);
        this.$message.error('获取仪表盘数据失败，请稍后重试。');
        // 可以设置一些默认值或错误状态下的dashboardSummary
        this.dashboardSummary = { // 重置或设为错误状态的默认值
            onlineDeviceCount: 0, offlineDeviceCount: 0, maintenanceDeviceCount: 0, faultyDeviceCount: 0,
            realtimeElectricityPower: 0, realtimeElectricityConsumption: 0,
            realtimeWaterPower: 0, realtimeWaterConsumption: 0,
            realtimeGasPower: 0, realtimeGasConsumption: 0,
            last7DaysElectricityConsumption: Array(7).fill({date: 'N/A', consumption: 0}),
            last7DaysWaterConsumption: Array(7).fill({date: 'N/A', consumption: 0}),
            last7DaysGasConsumption: Array(7).fill({date: 'N/A', consumption: 0}),
            lastUpdatedAt: new Date().toISOString(),
        };
        // 即使出错，也尝试初始化图表为空状态或提示
        this.$nextTick(() => {
          this.initEnergyChart();
          this.initDeviceStatusChart();
        });
      } finally {
        this.isLoading = false;
      }
    },
    initEnergyChart() {
      if (!this.$refs.energyChart) return;
      if (this.energyChartInstance) {
        this.energyChartInstance.dispose();
      }
      this.energyChartInstance = this.$echarts.init(this.$refs.energyChart);

      const dates = (this.dashboardSummary.last7DaysElectricityConsumption || []).map(item => {
        const parts = item.date.split('-');
        return `${parts[1]}-${parts[2]}`; // MM-DD format
      });
      const electricityData = (this.dashboardSummary.last7DaysElectricityConsumption || []).map(item => item.consumption.toFixed(2));
      const waterData = (this.dashboardSummary.last7DaysWaterConsumption || []).map(item => item.consumption.toFixed(2));
      const gasData = (this.dashboardSummary.last7DaysGasConsumption || []).map(item => item.consumption.toFixed(2));

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
      this.energyChartInstance.setOption(option);
    },
    initDeviceStatusChart() {
      if (!this.$refs.deviceStatusChart) return;
      if (this.deviceStatusChartInstance) {
        this.deviceStatusChartInstance.dispose();
      }
      this.deviceStatusChartInstance = this.$echarts.init(this.$refs.deviceStatusChart);
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
              { value: this.dashboardSummary.onlineDeviceCount, name: '在线' },
              { value: this.dashboardSummary.offlineDeviceCount, name: '离线' },
              { value: this.dashboardSummary.faultyDeviceCount, name: '故障' },
              { value: this.dashboardSummary.maintenanceDeviceCount, name: '维护中' }
            ]
          }
        ]
      };
      this.deviceStatusChartInstance.setOption(option);
    },
    navigateTo(path) {
      this.$router.push(path);
    },
    formatLastUpdate(isoDateTime) {
      if (!isoDateTime) return 'N/A';
      const date = new Date(isoDateTime);
      return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false });
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
