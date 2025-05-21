<template>
  <div class="device-management-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>设备列表</span>
        <el-button style="float: right;" type="primary" icon="el-icon-plus" @click="handleAddDevice">新增设备</el-button>
      </div>

      <!-- 搜索和筛选区域 -->
      <el-form :inline="true" :model="searchParams" class="demo-form-inline mb-20">
        <el-form-item label="设备名称">
          <el-input v-model="searchParams.name" placeholder="请输入设备名称"></el-input>
        </el-form-item>
        <el-form-item label="所属房间">
          <el-select v-model="searchParams.roomId" placeholder="请选择房间" clearable filterable>
            <el-option v-for="room in roomOptions" :key="room.id" :label="room.roomNumber + ' (' + room.department + ')'" :value="room.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备状态">
          <el-select v-model="searchParams.status" placeholder="请选择状态" clearable>
            <el-option label="在线" value="online"></el-option>
            <el-option label="离线" value="offline"></el-option>
            <el-option label="故障" value="faulty"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 设备列表表格 -->
      <el-table :data="deviceList" v-loading="loading" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="name" label="设备名称" align="center"></el-table-column>
        <el-table-column prop="roomName" label="所属房间" align="center"></el-table-column> <!-- 假设后端会返回房间名 -->
        <el-table-column prop="templateName" label="设备型号" align="center"></el-table-column> <!-- 假设后端会返回模板名 -->
        <el-table-column prop="status" label="状态" align="center">
          <template slot-scope="scope">
            <el-tag :type="statusTagType(scope.row.status)">{{ formatStatus(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="installationDate" label="安装日期" align="center"></el-table-column>
        <el-table-column label="操作" width="280" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="info" icon="el-icon-view" @click="handleViewDevice(scope.row)">详情</el-button>
            <el-button size="mini" type="warning" icon="el-icon-edit" @click="handleEditDevice(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" icon="el-icon-delete" @click="handleDeleteDevice(scope.row)">删除</el-button>
          </template>
        </el-table-column>
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

    <!-- 新增/编辑设备对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="40%" @close="resetForm('deviceForm')">
      <el-form :model="deviceForm" :rules="deviceRules" ref="deviceForm" label-width="100px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="deviceForm.name" placeholder="请输入设备名称"></el-input>
        </el-form-item>
        <el-form-item label="所属房间" prop="roomId">
          <el-select v-model="deviceForm.roomId" placeholder="请选择房间" filterable style="width:100%;">
            <el-option v-for="room in roomOptions" :key="room.id" :label="room.roomNumber + ' (' + room.department + ')'" :value="room.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备模板" prop="templateId">
           <el-select v-model="deviceForm.templateId" placeholder="请选择设备模板" filterable style="width:100%;">
            <el-option v-for="template in templateOptions" :key="template.id" :label="template.manufacturer + ' - ' + template.model" :value="template.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="安装日期" prop="installationDate">
          <el-date-picker v-model="deviceForm.installationDate" type="date" placeholder="选择日期" style="width:100%;" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="设备状态" prop="status">
          <el-select v-model="deviceForm.status" placeholder="请选择状态" style="width:100%;">
            <el-option label="在线" value="online"></el-option>
            <el-option label="离线" value="offline"></el-option>
            <el-option label="故障" value="faulty"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input type="textarea" v-model="deviceForm.remarks" placeholder="请输入备注信息"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitDeviceForm">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 设备详情抽屉 -->
    <el-drawer
      title="设备详情"
      :visible.sync="drawerVisible"
      direction="rtl"
      size="40%">
      <div style="padding: 20px;">
        <el-descriptions :column="1" border>
            <el-descriptions-item label="设备ID">{{ currentDevice.id }}</el-descriptions-item>
            <el-descriptions-item label="设备名称">{{ currentDevice.name }}</el-descriptions-item>
            <el-descriptions-item label="所属房间">{{ currentDevice.roomName }}</el-descriptions-item>
            <el-descriptions-item label="设备型号">{{ currentDevice.templateName }}</el-descriptions-item>
            <el-descriptions-item label="状态">
                <el-tag :type="statusTagType(currentDevice.status)">{{ formatStatus(currentDevice.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="安装日期">{{ currentDevice.installationDate }}</el-descriptions-item>
            <el-descriptions-item label="最后上线时间">{{ currentDevice.lastOnlineTime || 'N/A' }}</el-descriptions-item>
            <el-descriptions-item label="备注">{{ currentDevice.remarks || '无' }}</el-descriptions-item>
        </el-descriptions>
        <!-- 可以在这里添加更多设备相关信息，如实时数据、控制按钮等 -->
      </div>
    </el-drawer>

  </div>
</template>

<script>
// 实际项目中会从 src/api/device.js, src/api/room.js, src/api/deviceTemplate.js 导入 API
// import { getDevices, createDevice, updateDevice, deleteDevice, getDeviceById } from '@/api/device';
// import { getRooms } from '@/api/room';
// import { getDeviceTemplates } from '@/api/deviceTemplate';

export default {
  name: "DeviceManagementView",
  data() {
    return {
      loading: false,
      deviceList: [],
      roomOptions: [], // 房间选项，用于下拉选择
      templateOptions: [], // 设备模板选项
      searchParams: {
        name: "",
        roomId: null,
        status: "",
      },
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
      dialogVisible: false,
      dialogTitle: "",
      deviceForm: {
        id: null,
        name: "",
        roomId: null,
        templateId: null,
        installationDate: "",
        status: "online",
        remarks: "",
      },
      deviceRules: {
        name: [{ required: true, message: "请输入设备名称", trigger: "blur" }],
        roomId: [{ required: true, message: "请选择所属房间", trigger: "change" }],
        templateId: [{ required: true, message: "请选择设备模板", trigger: "change" }],
        installationDate: [{ required: true, message: "请选择安装日期", trigger: "change" }],
        status: [{ required: true, message: "请选择设备状态", trigger: "change" }],
      },
      drawerVisible: false,
      currentDevice: {},
      // 模拟数据
      mockDeviceData: [
        { id: 1, name: '空调A', roomId: 1, roomName: '101 (门诊部)', templateId: 1, templateName: '格力 KFR-35GW', status: 'online', installationDate: '2023-01-15', lastOnlineTime: '2025-05-21 10:00', remarks: '主楼空调' },
        { id: 2, name: '照明灯B', roomId: 2, roomName: '102 (门诊部)', templateId: 2, templateName: '飞利浦 LED筒灯', status: 'offline', installationDate: '2023-02-20', lastOnlineTime: '2025-05-20 18:00', remarks: '' },
        { id: 3, name: '电梯C', roomId: 3, roomName: '201 (住院部)', templateId: 3, templateName: '三菱 Maxiez', status: 'faulty', installationDate: '2022-12-10', lastOnlineTime: '2025-05-21 08:30', remarks: '传感器故障' },
      ],
      mockRoomData: [
        { id: 1, roomNumber: '101', department: '门诊部' },
        { id: 2, roomNumber: '102', department: '门诊部' },
        { id: 3, roomNumber: '201', department: '住院部' },
      ],
      mockTemplateData: [
        { id: 1, manufacturer: '格力', model: 'KFR-35GW', category: '空调' },
        { id: 2, manufacturer: '飞利浦', model: 'LED筒灯', category: '照明' },
        { id: 3, manufacturer: '三菱', model: 'Maxiez', category: '电梯' },
      ]
    };
  },
  created() {
    this.fetchInitialData();
  },
  methods: {
    fetchInitialData() {
      this.fetchDeviceList();
      this.fetchRoomOptions();
      this.fetchTemplateOptions();
    },
    fetchDeviceList() {
      this.loading = true;
      // 模拟API调用
      setTimeout(() => {
        let filteredData = this.mockDeviceData.filter(item => {
          return (
            (this.searchParams.name ? item.name.includes(this.searchParams.name) : true) &&
            (this.searchParams.roomId ? item.roomId === this.searchParams.roomId : true) &&
            (this.searchParams.status ? item.status === this.searchParams.status : true)
          );
        });
        this.pagination.total = filteredData.length;
        const start = (this.pagination.currentPage - 1) * this.pagination.pageSize;
        const end = start + this.pagination.pageSize;
        this.deviceList = filteredData.slice(start, end);
        this.loading = false;
      }, 500);
      // 实际API: getDevices({ ...this.searchParams, page: this.pagination.currentPage, size: this.pagination.pageSize })
    },
    fetchRoomOptions() {
      // 模拟API调用
      this.roomOptions = this.mockRoomData;
      // 实际API: getRooms({ pageSize: -1 }) // 获取所有房间用于下拉
    },
    fetchTemplateOptions() {
      // 模拟API调用
      this.templateOptions = this.mockTemplateData;
      // 实际API: getDeviceTemplates({ pageSize: -1 })
    },
    handleSearch() {
      this.pagination.currentPage = 1;
      this.fetchDeviceList();
    },
    resetSearch() {
      this.searchParams = { name: "", roomId: null, status: "" };
      this.handleSearch();
    },
    handleAddDevice() {
      this.dialogTitle = "新增设备";
      this.deviceForm = { id: null, name: "", roomId: null, templateId: null, installationDate: "", status: "online", remarks: "" };
      this.dialogVisible = true;
    },
    handleEditDevice(row) {
      this.dialogTitle = "编辑设备";
      this.deviceForm = { ...row }; // 确保所有字段都被正确填充
      this.dialogVisible = true;
    },
    handleViewDevice(row) {
        // 模拟获取设备详细信息
        // 实际项目中，如果列表信息不全，可能需要根据 row.id 再次调用API获取完整详情
        // getDeviceById(row.id).then(res => { this.currentDevice = res.data; this.drawerVisible = true; });
        this.currentDevice = { ...row }; // 使用列表数据，如果包含足够信息
        this.drawerVisible = true;
    },
    handleDeleteDevice(row) {
      this.$confirm(`确定删除设备 ${row.name} 吗?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        // 模拟API
        this.mockDeviceData = this.mockDeviceData.filter(item => item.id !== row.id);
        this.fetchDeviceList();
        this.$message({ type: "success", message: "删除成功!" });
        // 实际API: deleteDevice(row.id).then(...)
      }).catch(() => {
        this.$message({ type: "info", message: "已取消删除" });
      });
    },
    submitDeviceForm() {
      this.$refs.deviceForm.validate((valid) => {
        if (valid) {
          // 模拟API
          const room = this.roomOptions.find(r => r.id === this.deviceForm.roomId);
          const template = this.templateOptions.find(t => t.id === this.deviceForm.templateId);
          const deviceData = {
            ...this.deviceForm,
            roomName: room ? `${room.roomNumber} (${room.department})` : 'N/A',
            templateName: template ? `${template.manufacturer} - ${template.model}` : 'N/A',
          };

          if (this.deviceForm.id) { // 编辑
            const index = this.mockDeviceData.findIndex(item => item.id === this.deviceForm.id);
            if (index !== -1) {
              this.mockDeviceData.splice(index, 1, deviceData);
            }
            this.$message({ type: "success", message: "更新成功!" });
          } else { // 新增
            deviceData.id = Date.now(); // 简单ID
            this.mockDeviceData.unshift(deviceData);
            this.$message({ type: "success", message: "新增成功!" });
          }
          this.dialogVisible = false;
          this.fetchDeviceList();
          // 实际API: this.deviceForm.id ? updateDevice(...) : createDevice(...)
        } else {
          return false;
        }
      });
    },
    resetForm(formName) {
      if (this.$refs[formName]) {
        this.$refs[formName].resetFields();
      }
      this.deviceForm = { id: null, name: "", roomId: null, templateId: null, installationDate: "", status: "online", remarks: "" };
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.fetchDeviceList();
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.fetchDeviceList();
    },
    statusTagType(status) {
      if (status === 'online') return 'success';
      if (status === 'offline') return 'info';
      if (status === 'faulty') return 'danger';
      return '';
    },
    formatStatus(status) {
      if (status === 'online') return '在线';
      if (status === 'offline') return '离线';
      if (status === 'faulty') return '故障';
      return '未知';
    }
  },
};
</script>

<style scoped>
.device-management-container {
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
