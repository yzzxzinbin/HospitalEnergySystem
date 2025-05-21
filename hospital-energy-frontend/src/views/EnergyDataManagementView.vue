<template>
  <div class="energy-data-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>能源数据查询</span>
      </div>

      <!-- 搜索和筛选区域 -->
      <el-form :inline="true" :model="searchParams" class="demo-form-inline mb-20">
        <el-form-item label="选择设备">
          <el-select v-model="searchParams.deviceId" placeholder="请选择设备" clearable filterable style="width: 200px;">
            <el-option v-for="device in deviceOptions" :key="device.id" :label="device.name + ' (' + device.roomName + ')'" :value="device.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数据类型">
          <el-select v-model="searchParams.dataType" placeholder="请选择数据类型" clearable style="width: 150px;">
            <el-option label="电能" value="electricity"></el-option>
            <el-option label="水能" value="water"></el-option>
            <el-option label="热能" value="heat"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchParams.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期时间"
            end-placeholder="结束日期时间"
            value-format="yyyy-MM-dd HH:mm:ss">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-download" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>

      <!-- 能源数据图表 -->
      <el-card shadow="never" class="mb-20" v-if="chartVisible">
          <div ref="energyDataChart" style="height: 400px;"></div>
      </el-card>

      <!-- 能源数据列表表格 -->
      <el-table :data="energyDataList" v-loading="loading" style="width: 100%" border>
        <el-table-column prop="id" label="记录ID" width="80" align="center"></el-table-column>
        <el-table-column prop="deviceName" label="设备名称" align="center"></el-table-column>
        <el-table-column prop="dataType" label="数据类型" align="center">
            <template slot-scope="scope">{{ formatDataType(scope.row.dataType) }}</template>
        </el-table-column>
        <el-table-column prop="value" label="读数" align="center">
            <template slot-scope="scope">{{ scope.row.value }} {{ getUnit(scope.row.dataType) }}</template>
        </el-table-column>
        <el-table-column prop="timestamp" label="记录时间" align="center" width="180"></el-table-column>
        <!-- <el-table-column label="操作" width="100" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="info" @click="handleViewDetails(scope.row)">详情</el-button>
          </template>
        </el-table-column> -->
      </el-table>

      <!-- 分页组件 -->
      <el-pagination
        style="margin-top: 20px; text-align: right;"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
// 实际项目中会从 src/api/energyData.js, src/api/device.js 导入 API
// import { getEnergyData, exportEnergyData } from '@/api/energyData';
// import { getDevices } from '@/api/device';

export default {
  name: "EnergyDataManagementView",
  data() {
    return {
      loading: false,
      chartVisible: false,
      energyDataList: [],
      deviceOptions: [], // 设备选项
      searchParams: {
        deviceId: null,
        dataType: "electricity", // 默认查询电能
        dateRange: [], // [startTime, endTime]
      },
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
      energyDataChartInstance: null,
      // 模拟数据
      mockEnergyData: [
        { id: 1, deviceId: 1, deviceName: '空调A (101 门诊部)', dataType: 'electricity', value: 10.5, timestamp: '2025-05-21 10:00:00' },
        { id: 2, deviceId: 1, deviceName: '空调A (101 门诊部)', dataType: 'electricity', value: 11.2, timestamp: '2025-05-21 11:00:00' },
        { id: 3, deviceId: 2, deviceName: '照明灯B (102 门诊部)', dataType: 'electricity', value: 0.8, timestamp: '2025-05-21 10:00:00' },
        { id: 4, deviceId: 1, deviceName: '空调A (101 门诊部)', dataType: 'water', value: 0.5, timestamp: '2025-05-21 10:00:00' },
      ],
      mockDeviceData: [
        { id: 1, name: '空调A', roomName: '101 门诊部' },
        { id: 2, name: '照明灯B', roomName: '102 门诊部' },
        { id: 3, name: '电梯C', roomName: '201 住院部' },
      ]
    };
  },
  created() {
    this.fetchDeviceOptions();
    // 默认加载一次数据，或者用户点击查询后加载
    // this.fetchEnergyData(); 
  },
  mounted() {
    // 初始化图表，但可能需要等待数据加载后
  },
  beforeDestroy() {
    if (this.energyDataChartInstance) {
      this.energyDataChartInstance.dispose();
    }
  },
  methods: {
    fetchDeviceOptions() {
      // 模拟API调用
      this.deviceOptions = this.mockDeviceData;
      // 实际API: getDevices({ pageSize: -1 }) // 获取所有设备用于下拉
    },
    fetchEnergyData() {
      if (!this.searchParams.deviceId && !this.searchParams.dateRange.length) {
          this.$message.warning("请至少选择一个设备或时间范围进行查询");
          // return;
      }
      this.loading = true;
      this.chartVisible = false; // 查询时先隐藏图表，数据显示后再显示
      // 模拟API调用
      setTimeout(() => {
        let filteredData = this.mockEnergyData.filter(item => {
          const deviceMatch = this.searchParams.deviceId ? item.deviceId === this.searchParams.deviceId : true;
          const typeMatch = this.searchParams.dataType ? item.dataType === this.searchParams.dataType : true;
          let dateMatch = true;
          if (this.searchParams.dateRange && this.searchParams.dateRange.length === 2) {
            const startTime = new Date(this.searchParams.dateRange[0]).getTime();
            const endTime = new Date(this.searchParams.dateRange[1]).getTime();
            const itemTime = new Date(item.timestamp).getTime();
            dateMatch = itemTime >= startTime && itemTime <= endTime;
          }
          return deviceMatch && typeMatch && dateMatch;
        });

        this.pagination.total = filteredData.length;
        const start = (this.pagination.currentPage - 1) * this.pagination.pageSize;
        const end = start + this.pagination.pageSize;
        this.energyDataList = filteredData.slice(start, end);
        this.loading = false;

        if (this.energyDataList.length > 0 && this.searchParams.deviceId) {
            this.chartVisible = true;
            this.$nextTick(() => {
                this.initEnergyDataChart(this.energyDataList);
            });
        } else {
            if (this.energyDataChartInstance) {
                this.energyDataChartInstance.dispose();
                this.energyDataChartInstance = null;
            }
        }
      }, 500);
      // 实际API: getEnergyData({ ...this.searchParams, page: this.pagination.currentPage, size: this.pagination.pageSize })
    },
    handleSearch() {
      this.pagination.currentPage = 1;
      this.fetchEnergyData();
    },
    handleExport() {
      if (this.energyDataList.length === 0) {
        this.$message.warning("当前没有数据可以导出");
        return;
      }
      this.$confirm("确定导出当前查询结果吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "info",
      }).then(() => {
        this.$message.success("导出请求已发送，请稍后查看下载（模拟）");
        // 实际API: exportEnergyData(this.searchParams).then(response => { ...处理文件下载... })
      }).catch(() => {});
    },
    initEnergyDataChart(data) {
      if (this.energyDataChartInstance) {
        this.energyDataChartInstance.dispose();
      }
      this.energyDataChartInstance = this.$echarts.init(this.$refs.energyDataChart);
      const chartData = data.map(item => [item.timestamp, item.value]);
      const device = this.deviceOptions.find(d => d.id === this.searchParams.deviceId);
      const seriesName = device ? device.name : '能耗数据';

      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: params => {
            const param = params[0];
            return `${param.seriesName}<br/>${param.marker}${param.name}: ${param.value[1]} ${this.getUnit(this.searchParams.dataType)}`;
          }
        },
        legend: {
          data: [seriesName]
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'time',
          boundaryGap: false,
        },
        yAxis: {
          type: 'value',
          name: this.getUnit(this.searchParams.dataType)
        },
        series: [
          {
            name: seriesName,
            type: 'line',
            smooth: true,
            data: chartData
          }
        ]
      };
      this.energyDataChartInstance.setOption(option);
    },
    formatDataType(type) {
      if (type === 'electricity') return '电能';
      if (type === 'water') return '水能';
      if (type === 'heat') return '热能';
      return '未知';
    },
    getUnit(type) {
      if (type === 'electricity') return 'kWh';
      if (type === 'water') return 'm³';
      if (type === 'heat') return 'GJ';
      return '';
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.fetchEnergyData();
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.fetchEnergyData();
    },
  },
};
</script>

<style scoped>
.energy-data-container {
  padding: 20px;
}
.box-card {
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
