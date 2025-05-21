<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>能源总览</span>
          </div>
          <div>
            <p>今日总耗能: {{ energyOverview.todayTotalConsumption }} kWh</p>
            <p>昨日总耗能: {{ energyOverview.yesterdayTotalConsumption }} kWh</p>
            <p>本月总耗能: {{ energyOverview.monthTotalConsumption }} kWh</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>设备状态</span>
          </div>
          <div>
            <p>在线设备: {{ deviceStatus.online }} 台</p>
            <p>离线设备: {{ deviceStatus.offline }} 台</p>
            <p>故障设备: {{ deviceStatus.faulty }} 台</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>告警信息</span>
            <el-button style="float: right; padding: 3px 0" type="text">查看更多</el-button>
          </div>
          <div v-if="alarms.length === 0" class="no-alarms">暂无告警</div>
          <el-timeline v-else :reverse="false">
            <el-timeline-item
              v-for="(alarm, index) in alarms.slice(0, 3)" 
              :key="index"
              :timestamp="alarm.time">
              {{alarm.message}}
            </el-timeline-item>
          </el-timeline>
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
                <el-button type="primary" plain @click="navigateTo(item.path)" icon-class="item.icon">{{ item.name }}</el-button>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "DashboardView",
  data() {
    return {
      energyOverview: {
        todayTotalConsumption: 1250.5,
        yesterdayTotalConsumption: 1180.2,
        monthTotalConsumption: 35600.0,
      },
      deviceStatus: {
        online: 185,
        offline: 10,
        faulty: 2,
      },
      alarms: [
        { time: '2025-05-21 10:30', message: '3号楼空调系统能耗异常' },
        { time: '2025-05-21 09:15', message: '手术室照明设备离线' },
        { time: '2025-05-20 17:45', message: '1号楼电梯能耗超标' },
        { time: '2025-05-20 17:00', message: '2号楼服务器机房温度过高' },
      ],
      quickAccess: [
        { name: '房间管理', path: '/rooms', icon: 'el-icon-office-building' },
        { name: '设备控制', path: '/devices', icon: 'el-icon-cpu' },
        { name: '能耗报表', path: '/energy-data', icon: 'el-icon-s-data' },
        { name: '用户设置', path: '/profile', icon: 'el-icon-setting' },
      ],
      energyChartInstance: null,
    };
  },
  mounted() {
    this.initEnergyChart();
  },
  beforeDestroy() {
    if (this.energyChartInstance) {
      this.energyChartInstance.dispose();
    }
  },
  methods: {
    initEnergyChart() {
      this.energyChartInstance = this.$echarts.init(this.$refs.energyChart);
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['总耗能']
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
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] // 替换为实际日期
        },
        yAxis: {
          type: 'value',
          name: 'kWh'
        },
        series: [
          {
            name: '总耗能',
            type: 'line',
            stack: '总量',
            data: [120, 132, 101, 134, 90, 230, 210] // 替换为实际数据
          }
        ]
      };
      this.energyChartInstance.setOption(option);
    },
    navigateTo(path) {
      this.$router.push(path);
    }
  },
};
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.box-card {
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

.no-alarms {
  text-align: center;
  color: #909399;
  padding: 20px 0;
}

.quick-access-item {
    margin-bottom: 10px;
    display: flex;
}

.quick-access-item .el-button {
    width: 100%;
}
</style>
